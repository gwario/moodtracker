package at.ameise.moodtracker;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Contains some settings.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 05.03.15.
 */
public interface ISetting {

    boolean APP_MODE_DEBUG = BuildConfig.DEBUG;

    boolean LOG_MODE_DEBUG = BuildConfig.DEBUG;

    int BACKEND_CALL_TRIES = BuildConfig.BACKEND_CALL_TRIES;

    SimpleDateFormat DEBUG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd -- HH:mm:ss.SSSZ", Locale.GERMAN);
}
