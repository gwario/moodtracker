package at.ameise.moodtracker;

/**
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 04.04.15.
 */
public interface IPreference {

    /**
     * Name of the application wide preferences.
     */
    String APPLICATION_PREFERENCES = "at.ameise.moodtracker.applicationPreferences";

    /**
     * To determine if the application has been started before.
     */
    String KEY_FIRST_APPLICATION_START = "at.ameise.moodtracker.applicationPreferences.extraFirstStart";

    /**
     * Contains the users decision on whether or not to sign in.
     */
    String KEY_DO_SIGN_IN = "at.ameise.moodtracker.applicationPreferences.extraDoSignIn";

    /**
     * Contains the account with which the user wants to sign in.
     */
    String KEY_ACCOUNT_NAME = "at.ameise.moodtracker.applicationPreferences.extraAccountName";


}
