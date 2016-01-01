package at.ameise.moodtracker.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import at.ameise.moodtracker.R;
import at.ameise.moodtracker.app.domain.Mood;
import at.ameise.moodtracker.app.fragment.EnterMoodFragment;
import at.ameise.moodtracker.app.service.MoodSynchronizationService;
import at.ameise.moodtracker.app.util.NotificationUtil;
import at.ameise.moodtracker.app.util.ToastUtil;

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

        NotificationUtil.consumeReminderNotification(this);
    }

    @Override
    public void onMoodUpdate(final Mood currentMood) {

        MoodSynchronizationService.startActionSynchronizeMoods(this);
        ToastUtil.showMoodUpdatedText(this);
    }
}
