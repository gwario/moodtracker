package at.ameise.moodtracker.util;

import android.content.Intent;

/**
 * Utility class which provides convenience methods to deal with intents.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 04.12.15.
 */
public class IntentUtil {

    private IntentUtil() {}

    /**
     * @param intent
     * @param extraName
     * @return true if the intent got the extra.
     */
    public static boolean hasExtra(Intent intent, String extraName) {

        return intent != null && intent.hasExtra(extraName);
    }

    /**
     * @param intent
     * @param extraName
     * @return true returns the boolean extra of the intent or false if it does not exist.
     */
    public static boolean getBooleanExtra(Intent intent, String extraName) {

        return intent != null && intent.getBooleanExtra(extraName, false);
    }
}
