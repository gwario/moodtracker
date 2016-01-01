package at.ameise.moodtracker.app;

import at.ameise.moodtracker.BuildConfig;

/**
 * API Keys, Client Ids and Audience Ids for accessing APIs and configuring
 * Cloud Endpoints. When you deploy your solution, you need to use your own API
 * Keys and IDs. Please update config.gradle to define them.
 */
public final class ApiConstant {

    private ApiConstant() {}

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    public static final String SENDER_ID = BuildConfig.SENDER_ID;

    /**
     * Web client ID from Google Cloud console.
     */
    public static final String WEB_CLIENT_ID = BuildConfig.WEB_CLIENT_ID;

    /**
     * The web client ID from Google Cloud Console.
     */
    public static final String AUDIENCE_ANDROID_CLIENT_ID = "server:client_id:" + WEB_CLIENT_ID;

    public static final String ROOT_URL = BuildConfig.ROOT_URL;
}
