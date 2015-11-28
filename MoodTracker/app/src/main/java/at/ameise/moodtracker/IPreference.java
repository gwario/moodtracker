package at.ameise.moodtracker;

/**
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 04.04.15.
 */
public interface IPreference {

    String APPLICATION_PREFERENCES = "applicationPreferences";

    String KEY_APPLICATION_INITIALIZED = "extraInitialized";

    /**
     * Name of the key for the shared preferences to access the current signed in account.
     */
    String KEY_ACCOUNT_NAME = "extraAccountName";
}
