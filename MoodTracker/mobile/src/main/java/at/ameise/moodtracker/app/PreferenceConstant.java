package at.ameise.moodtracker.app;

/**
 * Contains constants to handle {@link android.content.SharedPreferences}s.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 04.04.15.
 */
public final class PreferenceConstant {

    private PreferenceConstant() {}

    /**
     * Name of the application wide preferences.
     */
    public static final String APPLICATION_PREFERENCES = "at.ameise.moodtracker.app.applicationPreferences";

    /**
     * To determine if the application has been started before.
     */
    public static final String KEY_FIRST_APPLICATION_START = "at.ameise.moodtracker.app.applicationPreferences.extraFirstStart";

    /**
     * Contains the account with which the user wants to sign in. If this extra is null, the uses decided not to sign in.
     */
    public static final String KEY_ACCOUNT_NAME = "at.ameise.moodtracker.app.applicationPreferences.extraAccountName";

    /**
     * The time of the last manual synchronization. This preference is to prevent dos attacks on the backend.
     */
    public static final String KEY_LAST_MANUAL_SYNC_TIMESTAMP = "at.ameise.moodtracker.app.applicationPreferences.extraLastManualSyncTimestamp";
}
