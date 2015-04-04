package at.ameise.moodtracker.activity;

import android.app.Activity;
import android.os.Bundle;

import at.ameise.moodtracker.R;
import at.ameise.moodtracker.domain.Mood;
import at.ameise.moodtracker.fragment.EnterMoodFragment;
import at.ameise.moodtracker.fragment.MoodHistoryFragment;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends Activity implements EnterMoodFragment.OnFragmentInteractionListener {

    private MoodHistoryFragment moodHistoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        moodHistoryFragment = (MoodHistoryFragment) getFragmentManager().findFragmentById(R.id.get_mood);
    }

    @Override
    public void onMoodUpdate(Mood currentMood) {

    }
}
