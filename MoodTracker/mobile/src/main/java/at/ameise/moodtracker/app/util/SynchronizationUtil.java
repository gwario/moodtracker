package at.ameise.moodtracker.app.util;

import android.content.Context;

import at.ameise.moodtracker.app.PreferenceConstant;

/**
 * Utility class which contains methods to support synchronization.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 06.12.15.
 */
public class SynchronizationUtil {

    private SynchronizationUtil() {}

    /**
     * Returns wheter or not the synchronization is enabled.
     *
     * @param context
     * @return true if the synchronization is activated.
     */
    public static boolean isSynchronizationActivated(Context context) {

        return context.getSharedPreferences(PreferenceConstant.APPLICATION_PREFERENCES, Context.MODE_PRIVATE).getString(PreferenceConstant.KEY_ACCOUNT_NAME, null) != null;
    }
}
