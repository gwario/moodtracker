package at.ameise.moodtracker.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;

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

    private ImageView ibIncrement;
    private ImageView ibDecrement;
    private ImageView ibSet;
    private ImageView ibShare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

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


                ibIncrement = (ImageView) stub.findViewById(R.id.ibIncrementMood);
                ibDecrement = (ImageView) stub.findViewById(R.id.ibDecrementMood);
                ibSet = (ImageView) stub.findViewById(R.id.ibSetCurrentMood);
                ibShare = (ImageView) stub.findViewById(R.id.ibShareCurrentMood);

                ibCurrentMood0.setSelected(true);
                ibCurrentMood0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ibCurrentMood1.setSelected(true);
                        ibCurrentMood2.setSelected(false);
                        ibCurrentMood3.setSelected(false);
                        ibCurrentMood4.setSelected(false);
                        ibCurrentMood5.setSelected(false);
                        ibCurrentMood6.setSelected(false);
                        Logger.debug(TAG, "Mood: 1");
                    }
                });

                ibCurrentMood1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ibCurrentMood1.setSelected(true);
                        ibCurrentMood2.setSelected(false);
                        ibCurrentMood3.setSelected(false);
                        ibCurrentMood4.setSelected(false);
                        ibCurrentMood5.setSelected(false);
                        ibCurrentMood6.setSelected(false);
                        Logger.debug(TAG, "Mood: 2");
                    }
                });

                ibCurrentMood2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ibCurrentMood1.setSelected(true);
                        ibCurrentMood2.setSelected(true);
                        ibCurrentMood3.setSelected(false);
                        ibCurrentMood4.setSelected(false);
                        ibCurrentMood5.setSelected(false);
                        ibCurrentMood6.setSelected(false);
                        Logger.debug(TAG, "Mood: 3");
                    }
                });

                ibCurrentMood3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ibCurrentMood1.setSelected(true);
                        ibCurrentMood2.setSelected(true);
                        ibCurrentMood3.setSelected(true);
                        ibCurrentMood4.setSelected(false);
                        ibCurrentMood5.setSelected(false);
                        ibCurrentMood6.setSelected(false);
                        Logger.debug(TAG, "Mood: 4");
                    }
                });

                ibCurrentMood4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ibCurrentMood1.setSelected(true);
                        ibCurrentMood2.setSelected(true);
                        ibCurrentMood3.setSelected(true);
                        ibCurrentMood4.setSelected(true);
                        ibCurrentMood5.setSelected(false);
                        ibCurrentMood6.setSelected(false);
                        Logger.debug(TAG, "Mood: 5");
                    }
                });

                ibCurrentMood5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ibCurrentMood1.setSelected(true);
                        ibCurrentMood2.setSelected(true);
                        ibCurrentMood3.setSelected(true);
                        ibCurrentMood4.setSelected(true);
                        ibCurrentMood5.setSelected(true);
                        ibCurrentMood6.setSelected(false);
                        Logger.debug(TAG, "Mood: 6");
                    }
                });

                ibCurrentMood6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ibCurrentMood1.setSelected(true);
                        ibCurrentMood2.setSelected(true);
                        ibCurrentMood3.setSelected(true);
                        ibCurrentMood4.setSelected(true);
                        ibCurrentMood5.setSelected(true);
                        ibCurrentMood6.setSelected(true);
                        Logger.debug(TAG, "Mood: 7");
                    }
                });
            }
        });
    }
}
