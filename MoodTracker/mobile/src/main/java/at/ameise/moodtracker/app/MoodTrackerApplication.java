package at.ameise.moodtracker.app;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;

import java.util.TimeZone;

import at.ameise.moodtracker.app.receiver.DailyReceiver;
import at.ameise.moodtracker.app.receiver.QuarterDailyReceiver;
import at.ameise.moodtracker.app.service.MoodSynchronizationService;
import at.ameise.moodtracker.app.util.DateTimeUtil;
import at.ameise.moodtracker.app.util.Logger;

/**
 * Application state; initializes the app.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 04.04.15.
 */
public class MoodTrackerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.info(TagConstant.INITIALIZATION, "MoodTracker created.");

        if (Setting.APP_MODE_DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
        }

        Logger.info(TagConstant.INITIALIZATION, "Initializing MoodTracker...");
        JodaTimeAndroid.init(this);
        DateTimeZone.setDefault(DateTimeZone.forOffsetMillis(TimeZone.getDefault().getRawOffset()));
        Logger.info(TagConstant.INITIALIZATION, "Your time zone is UTC" + DateTimeZone.getDefault());
        scheduleAlarms(this);
    }

    /**
     * Schedules alarms.
     */
    private static void scheduleAlarms(Context ctx) {

        final AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);
        DateTime startTime;
        Period period;

        Logger.debug(TagConstant.INITIALIZATION, "Scheduling quarter daily alarm...");
        startTime = DateTimeUtil.getStartOfNextQuarterOfDay();
        period = Period.hours(TimeConstant.QUARTER_DAY_H);
        Logger.debug(TagConstant.INITIALIZATION, "Starting at: " + Setting.DEBUG_DATE_FORMATTER.print(startTime) + ", Repeating every: " + period);
        PendingIntent intent = PendingIntent.getBroadcast(ctx, 0, new Intent(ctx, QuarterDailyReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime.getMillis(), period.toStandardDuration().getMillis(), intent);

        Logger.debug(TagConstant.INITIALIZATION, "Scheduling daily reminder alarm...");
        startTime = DateTimeUtil.getNextNoon();
        period = Period.hours(Setting.TRACK_MOOD_REMINDER_INTERVAL_H);
        Logger.debug(TagConstant.INITIALIZATION, "Starting at: " + Setting.DEBUG_DATE_FORMATTER.print(startTime) + ", Repeating every: " + period);
        intent = PendingIntent.getBroadcast(ctx, 0, new Intent(ctx, DailyReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime.getMillis(), period.toStandardDuration().getMillis(), intent);

        Logger.debug(TagConstant.INITIALIZATION, "Scheduling synchronization alarm...");
        startTime = DateTime.now().plusDays(1).withHourOfDay(4).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        period = Period.hours(Setting.SYNC_SERVICE_INTERVAL_H);
        Logger.debug(TagConstant.INITIALIZATION, "Starting at: " + Setting.DEBUG_DATE_FORMATTER.print(startTime)+", Repeating every: "+period);
        intent = PendingIntent.getService(ctx, 0, new Intent(ctx, MoodSynchronizationService.class).setAction(MoodSynchronizationService.ACTION_SYNCHRONIZE_MOODS), PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime.getMillis(), period.toStandardDuration().getMillis(), intent);
    }
}
