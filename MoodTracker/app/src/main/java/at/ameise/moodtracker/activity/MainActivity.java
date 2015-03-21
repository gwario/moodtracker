package at.ameise.moodtracker.activity;

import at.ameise.moodtracker.R;
import at.ameise.moodtracker.fragment.MoodHistoryFragment;

import android.app.Activity;
import android.os.Bundle;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends Activity implements MoodHistoryFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    public void onLoaderSet(int newLoaderId) {

    }
}
