package at.ameise.moodtracker;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.Calendar;

import at.ameise.moodtracker.receiver.QuarterDailyReceiver;
import at.ameise.moodtracker.util.CalendarUtil;
import at.ameise.moodtracker.util.Logger;

/**
 * Application state; initializes the app.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 04.04.15.
 */
public class MoodTrackerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.info(ITag.INITIALIZATION, "MoodTracker created.");

        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        Logger.info(ITag.INITIALIZATION, "Initializing MoodTracker...");
        JodaTimeAndroid.init(this);
        scheduleAlarms(this, alarmManager);
    }

    /**
     * Schedules alarms.
     */
    private static void scheduleAlarms(Context ctx, AlarmManager alarmManager) {

        Logger.debug(ITag.INITIALIZATION, "Scheduling quarter daily alarm...");

        final PendingIntent intent = PendingIntent.getBroadcast(ctx, 0, new Intent(ctx, QuarterDailyReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        final Calendar startTimeUTC = Calendar.getInstance();
        CalendarUtil.setStartOfNextQuarterOfDay(startTimeUTC);

        //TODO schedule sync task

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTimeUTC.getTimeInMillis(), ITimeConstant.QUARTER_DAY_MILLIS, intent);
    }
}
