package at.ameise.moodtracker.app.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import at.ameise.moodtracker.R;
import at.ameise.moodtracker.app.Setting;
import at.ameise.moodtracker.app.TagConstant;
import at.ameise.moodtracker.app.TimeConstant;
import at.ameise.moodtracker.app.domain.Mood;
import at.ameise.moodtracker.app.domain.MoodCursorHelper;
import at.ameise.moodtracker.app.fragment.EnterMoodFragment;
import at.ameise.moodtracker.app.service.MoodSynchronizationService;
import at.ameise.moodtracker.app.util.Logger;
import at.ameise.moodtracker.app.util.NotificationUtil;
import at.ameise.moodtracker.app.util.ToastUtil;

/**
 * The main activity of mood tracker.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com>
 */
public class MainActivity extends Activity implements EnterMoodFragment.OnFragmentInteractionListener,  DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    //TODO make this unique in a java lib module!!!
    private static final String MOOD_KEY = "com.example.key.mood";
    private static final String TIMESTAMP_KEY = "com.example.key.ts";

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_PROGRESS);
        setProgress(20);

        setContentView(R.layout.activity_main);

        NotificationUtil.consumeReminderNotification(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();
    }

    @Override
    public void onMoodUpdate(final Mood currentMood) {

        MoodSynchronizationService.startActionSynchronizeMoods(this);
        ToastUtil.showMoodUpdatedText(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().startsWith("/mood/")) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

                    Mood newMood = new Mood(dataMap);
                    Logger.info(TAG, "Got mood from wear device: " + newMood);

                    Logger.verbose(TagConstant.ENTER_MOOD, "Creating mood in the local database...");
                    MoodCursorHelper.createMood(this, newMood);
                    Logger.verbose(TagConstant.ENTER_MOOD, "Mood has been created in the local database.");

                    PendingResult<DataApi.DeleteDataItemsResult> pendingResult =  Wearable.DataApi.deleteDataItems(mGoogleApiClient, Uri.parse("/mood/" + newMood.getTimestamp().getMillis()));
                }
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
