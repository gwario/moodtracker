package at.ameise.moodtracker;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Contains app-global settings.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 05.03.15.
 */
public class Setting {

    private Setting() {}

    public static final boolean APP_MODE_DEBUG = BuildConfig.DEBUG;

    public static final boolean LOG_MODE_DEBUG = BuildConfig.DEBUG;

    //int BACKEND_CALL_TRIES = BuildConfig.BACKEND_CALL_TRIES;

    //SimpleDateFormat DEBUG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd -- HH:mm:ss.SSSZ", Locale.GERMAN);
}
