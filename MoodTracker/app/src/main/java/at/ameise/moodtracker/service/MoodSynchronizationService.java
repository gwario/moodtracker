package at.ameise.moodtracker.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import at.ameise.moodtracker.ITag;
import at.ameise.moodtracker.domain.AverageCalculatorHelper;
import at.ameise.moodtracker.domain.MoodTableHelper;
import at.ameise.moodtracker.util.IntentUtil;
import at.ameise.moodtracker.util.Logger;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * Calculates the mood values for 4-value-per-day, daily, weekly and monthly averages.
 * <p/>
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 04.04.15.
 */
public class MoodSynchronizationService extends IntentService {

    private static final String ACTION_SYNCHRONIZE_MOODS = "at.ameise.moodtracker.service.action.SYNCHRONIZE_MOODS";


    /**
     * Starts the service to synchronize moods with the backend.
     * @param context
     */
    public static void startActionSynchronizeMoods(Context context) {

        context.startService(new Intent(context, MoodSynchronizationService.class).setAction(ACTION_SYNCHRONIZE_MOODS));
    }


    public MoodSynchronizationService() {
        super("MoodSynchronizationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (IntentUtil.hasAction(intent, ACTION_SYNCHRONIZE_MOODS)) {

            handleActionSynchronizeMoods(intent);
        }
    }

    /**
     * Handles the {@link MoodSynchronizationService#ACTION_SYNCHRONIZE_MOODS}.
     *
     * @param intent
     */
    private void handleActionSynchronizeMoods(Intent intent) {

        //TODO upload all non synchronized moods
        //TODO get last mood from server
        //TODO compare it to last mood from local
        //TODO get
    }

}
