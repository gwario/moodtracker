package at.ameise.moodtracker.app;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Locale;
import java.util.TimeZone;

import at.ameise.moodtracker.BuildConfig;

/**
 * Contains app-global settings.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 05.03.15.
 */
public final class Setting {

    private Setting() {}

    public static final boolean APP_MODE_DEBUG = BuildConfig.DEBUG;

    public static final boolean LOG_MODE_DEBUG = BuildConfig.DEBUG;

    public static final int BACKEND_CALL_TRIES = BuildConfig.BACKEND_CALL_TRIES;

    public static final int SYNC_SERVICE_INTERVAL_H = BuildConfig.SYNC_SERVICE_INTERVAL_H;

    public static final int TRACK_MOOD_REMINDER_INTERVAL_H = 24;

    public static final int MANUAL_SYNC_DOWNTIME_M = 20;

    //"yyyy-MM-dd -- HH:mm:ss.SSSZ"
    public static final DateTimeFormatter DEBUG_DATE_FORMATTER = ISODateTimeFormat.dateTime();
}
