package at.ameise.moodtracker.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

import at.ameise.moodtracker.R;
import at.ameise.moodtracker.util.Logger;


public class MainActivity extends Activity implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageView ibCurrentMood0;
    private ImageView ibCurrentMood1;
    private ImageView ibCurrentMood2;
    private ImageView ibCurrentMood3;
    private ImageView ibCurrentMood4;
    private ImageView ibCurrentMood5;
    private ImageView ibCurrentMood6;

    private ImageView ibSet;
    //private ImageView ibShare;

    private static int currentMoodInt = -1;

    private List<ImageView> moodButtons;

    private static final String MOOD_KEY = "com.example.key.mood";
    private static final String TIMESTAMP_KEY = "com.example.key.ts";

    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        moodButtons = new ArrayList<>();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                ibCurrentMood0 = (ImageView) stub.findViewById(R.id.ibCurrentMoodWear0);
                ibCurrentMood1 = (ImageView) stub.findViewById(R.id.ibCurrentMoodWear1);
                ibCurrentMood2 = (ImageView) stub.findViewById(R.id.ibCurrentMoodWear2);
                ibCurrentMood3 = (ImageView) stub.findViewById(R.id.ibCurrentMoodWear3);
                ibCurrentMood4 = (ImageView) stub.findViewById(R.id.ibCurrentMoodWear4);
                ibCurrentMood5 = (ImageView) stub.findViewById(R.id.ibCurrentMoodWear5);
                ibCurrentMood6 = (ImageView) stub.findViewById(R.id.ibCurrentMoodWear6);

                ibSet = (ImageView) stub.findViewById(R.id.ibSetCurrentMood);
                //ibShare = (ImageView) stub.findViewById(R.id.ibShareCurrentMood);

                moodButtons.add(ibCurrentMood0);
                moodButtons.add(ibCurrentMood1);
                moodButtons.add(ibCurrentMood2);
                moodButtons.add(ibCurrentMood3);
                moodButtons.add(ibCurrentMood4);
                moodButtons.add(ibCurrentMood5);
                moodButtons.add(ibCurrentMood6);

                ibCurrentMood0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentMoodInt = 1;
                        setMoodOnButtons(currentMoodInt);
                        Logger.verbose(TAG, "Mood: " + currentMoodInt);
                    }
                });

                ibCurrentMood1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentMoodInt = 2;
                        setMoodOnButtons(currentMoodInt);
                        Logger.verbose(TAG, "Mood: " + currentMoodInt);
                    }
                });

                ibCurrentMood2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentMoodInt = 3;
                        setMoodOnButtons(currentMoodInt);
                        Logger.verbose(TAG, "Mood: " + currentMoodInt);
                    }
                });

                ibCurrentMood3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentMoodInt = 4;
                        setMoodOnButtons(currentMoodInt);
                        Logger.verbose(TAG, "Mood: " + currentMoodInt);
                    }
                });

                ibCurrentMood4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentMoodInt = 5;
                        setMoodOnButtons(currentMoodInt);
                        Logger.verbose(TAG, "Mood: " + currentMoodInt);
                    }
                });

                ibCurrentMood5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentMoodInt = 6;
                        setMoodOnButtons(currentMoodInt);
                        Logger.verbose(TAG, "Mood: " + currentMoodInt);
                    }
                });

                ibCurrentMood6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentMoodInt = 7;
                        setMoodOnButtons(currentMoodInt);
                        Logger.verbose(TAG, "Mood: " + currentMoodInt);
                    }
                });

                ibSet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        long timestamp = System.currentTimeMillis();
                        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/mood/" + timestamp);
                        putDataMapReq.getDataMap().putInt(MOOD_KEY, currentMoodInt);
                        putDataMapReq.getDataMap().putLong(TIMESTAMP_KEY, timestamp);
                        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
                        PendingResult<DataApi.DataItemResult> pendingResult =  Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);

                        Logger.debug(TAG, "Mood updated");
                        Toast.makeText(MainActivity.this, "Updated mood", Toast.LENGTH_SHORT).show();
                        setDefaultMoodOnButtons();
                    }
                });

                /*ibShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Logger.debug(TAG, "Mood shared");
                    }
                });
                */
                setDefaultMoodOnButtons();
            }
        });
    }

    /**
     * Updates the state of the buttons to the default mood.
     */
    private void setDefaultMoodOnButtons() {

        setMoodOnButtons(4);
    }

    /**
     * Updates the state of the buttons according to the mood.
     * @param mood the mood to be set
     */
    private void setMoodOnButtons(int mood) {

        for(int i = 0, moodButtonIdx = mood - 1; i < moodButtons.size(); i++) {

            if(i <= moodButtonIdx)
                moodButtons.get(i).setActivated(true);
            else
                moodButtons.get(i).setActivated(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
