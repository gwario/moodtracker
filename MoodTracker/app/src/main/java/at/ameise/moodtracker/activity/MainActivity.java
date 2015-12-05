package at.ameise.moodtracker.activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;

import at.ameise.moodtracker.IPreference;
import at.ameise.moodtracker.ISetting;
import at.ameise.moodtracker.R;
import at.ameise.moodtracker.domain.Mood;
import at.ameise.moodtracker.fragment.EnterMoodFragment;
import at.ameise.moodtracker.moodTrackerBackend.MoodTrackerBackend;
import at.ameise.moodtracker.moodTrackerBackend.model.MoodCollection;
import at.ameise.moodtracker.util.BackendEndpointUtil;
import at.ameise.moodtracker.util.Logger;
import at.ameise.moodtracker.util.RetryingAsyncTask;

/**
 * The main activity of mood tracker.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com>
 */
public class MainActivity extends Activity implements EnterMoodFragment.OnFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_PROGRESS);
        setProgress(20);

        setContentView(R.layout.activity_main);
    }

    @Override
    public void onMoodUpdate(final Mood currentMood) {

        if(getSharedPreferences(IPreference.APPLICATION_PREFERENCES, Context.MODE_PRIVATE).getBoolean(IPreference.KEY_DO_SIGN_IN, false)) {
            new RetryingAsyncTask<Void, Void, Boolean>() {

                @Override
                public Boolean doRetryInBackground(Void... params) throws IOException {

                    MoodTrackerBackend backendEndpointApi = BackendEndpointUtil.buildBackendEndpointsApi();
                    at.ameise.moodtracker.moodTrackerBackend.model.Mood createdMood = backendEndpointApi.mood().insertMood(currentMood.getMoodModel()).execute();
                    Logger.info(TAG, "Mood sent to server.");
                    Logger.info(TAG, "Create mood: " + createdMood);

                    MoodCollection moodList = backendEndpointApi.mood().listMoods().execute();
                    if (moodList.getItems() != null) {
                        for (at.ameise.moodtracker.moodTrackerBackend.model.Mood mood : moodList.getItems()) {

                            Logger.verbose(TAG, mood.toString());
                        }
                    }

                    return true;
                }

                @Override
                protected void onPostExecute(Boolean success) {

                    if (success != null && !success)
                        Toast.makeText(MainActivity.this, "Server communication failed!", Toast.LENGTH_LONG).show();

                    super.onPostExecute(success);
                }
            }.execute();
        }
    }
}
