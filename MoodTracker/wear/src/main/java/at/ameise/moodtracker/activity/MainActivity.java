package at.ameise.moodtracker.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import at.ameise.moodtracker.R;
import at.ameise.moodtracker.util.Logger;


public class MainActivity extends Activity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        moodButtons = new ArrayList<>();

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

}
