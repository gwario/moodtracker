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
     * @return true if the intent got the extra. Does check for null values.
     */
    public static boolean hasExtra(Intent intent, String extraName) {

        return intent != null && intent.hasExtra(extraName);
    }

    /**
     * @param intent
     * @param actionName
     * @return true if the intent got the action. Does check for null values.
     */
    public static boolean hasAction(Intent intent, String actionName) {

        return intent != null && intent.getAction() != null && intent.getAction().equals(actionName);
    }

    /**
     * @param intent
     * @param extraName
     * @return true returns the boolean extra of the intent or false if it does not exist.
     * Does check for null values.
     */
    public static boolean getBooleanExtra(Intent intent, String extraName) {

        return intent != null && intent.getBooleanExtra(extraName, false);
    }
}
