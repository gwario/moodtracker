package at.ameise.moodtracker;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Contains some settings.
 *
 * Created by Mario Gastegger <mgastegger AT buzzmark DOT com> on 05.03.15.
 */
public interface ISetting {

    final boolean APP_MODE_DEBUG = true;

    final boolean LOG_MODE_DEBUG = true;

    final SimpleDateFormat DEBUG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd -- HH:mm:ss.SSSZ", Locale.GERMAN);
}
