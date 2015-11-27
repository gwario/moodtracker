package at.ameise.moodtracker.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import at.ameise.moodtracker.IApiConstants;
import at.ameise.moodtracker.R;
import at.ameise.moodtracker.domain.Mood;
import at.ameise.moodtracker.fragment.EnterMoodFragment;
import at.ameise.moodtracker.fragment.MoodHistoryFragment;
import at.ameise.moodtracker.moodTrackerBackend.MoodTrackerBackend;
import at.ameise.moodtracker.util.CloudEndpointBuilderHelper;
import at.ameise.moodtracker.util.Logger;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends Activity implements EnterMoodFragment.OnFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Time limit for the application to wait on a response from Play Services.
     */
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Name of the key for the shared preferences to access the current device
     * registration id for GCM.
     */
    private static final String PROPERTY_REG_ID = "registrationId";

    /**
     * Name of the key for the shared preferences to access the current
     * application version, to see if GCM registration id needs to be updated.
     */
    private static final String PROPERTY_APP_VERSION = "appVersion";

    private MoodHistoryFragment moodHistoryFragment;

    private MoodTrackerBackend moodTrackerAPI;

    private GoogleCloudMessaging gcm;

    private Context context;

    private String regId;

    /**
     * Returns the application version.
     * @param context the Application context.
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(final Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handle to the GAE endpoints in the backend
        moodTrackerAPI = CloudEndpointBuilderHelper.getEndpoints();

        context = getApplicationContext();

        //new CheckInTask().execute();

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_main);

        // Check device for Play Services APK. If check succeeds, proceed
        // with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regId = getRegistrationId(context);

            if (regId.isEmpty()) {
                Logger.info(TAG, "Not registered with GCM.");

                // Register GCM id in the background
                new GcmAsyncRegister().execute();

            }
        } else {
            Logger.info(TAG, "No valid Google Play Services APK found.");
        }

        moodHistoryFragment = (MoodHistoryFragment) getFragmentManager().findFragmentById(R.id.get_mood);
    }

    /**
     * Checks if Google Play Services are installed and if not it initializes
     * opening the dialog to allow user to install Google Play Services.
     * @return a boolean indicating if the Google Play Services are available.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Logger.info(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     * @param applicationContext the Application context.
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(final Context applicationContext) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Logger.info(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs
                .getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Logger.info(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     * @param applicationContext application's context.
     * @param registrationId     registration ID
     */
    private void storeRegistrationId(final Context applicationContext,
                                     final String registrationId) {
        final SharedPreferences prefs = getGCMPreferences(applicationContext);
        int appVersion = getAppVersion(applicationContext);
        Logger.info(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, registrationId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    /**
     * @param applicationContext the Application context.
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(final Context
                                                        applicationContext) {
        // This sample app persists the registration ID in shared preferences,
        // but how you store the registration ID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use
     * GCM/HTTP or CCS to send messages to your app. Not needed for this
     * demo since the device sends upstream messages to a server that echoes
     * back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {

        try {

            moodTrackerAPI.registrations().registerDevice(regId).execute();
            // Persist the registration ID - no need to register again.
            storeRegistrationId(context, regId);

        } catch (IOException e) {

            Logger.warn(TAG, "Exception when sending registration ID to the " + "backend = " + e.getMessage());
            // If there is an error, we will try again to register the
            // device with GCM the next time the MainActivity starts.
        }
    }

    @Override
    public void onMoodUpdate(Mood currentMood) {

    }

    /**
     * AsyncTask for retrieving the list of nearby places (e.g., stores) and
     * updating the corresponding ListView and label.
     */
    /*private class ListOfPlacesAsyncRetriever
            extends AsyncTask<Location, Void, PlaceInfoCollection> {

        /**
         * Updates UI to indicate that the list of nearby places is being
         * retrieved.
         *
        @Override
        protected void onPreExecute() {
            placesListLabel.setText(string.retrievingPlaces);
            MainActivity.this.setProgressBarIndeterminateVisibility(true);
        }

        /**
         * Updates UI to indicate that retrieval of the list of nearby places
         * completed successfully or failed.
         *
        @Override
        protected void onPostExecute(final PlaceInfoCollection result) {
            MainActivity.this.setProgressBarIndeterminateVisibility(false);

            if (result == null || result.getItems() == null
                    || result.getItems().size() < 1) {
                if (result == null) {
                    placesListLabel.setText(string.failedToRetrievePlaces);
                } else {
                    placesListLabel.setText(string.noPlacesNearby);
                }

                placesList.setAdapter(null);
                return;
            }

            placesListLabel.setText(string.nearbyPlaces);

            ListAdapter placesListAdapter = createPlaceListAdapter(
                    result.getItems());
            placesList.setAdapter(placesListAdapter);

            places = result.getItems();
        }

        /**
         * Creates ListAdapter populated with the list of nearby places.
         * @param placesRetrieved the list of places to put in the adapter.
         * @return an adapter populated with the list of nearby places.
         *
        private ListAdapter createPlaceListAdapter(final List<PlaceInfo>
                                                           placesRetrieved) {
            final double kilometersInAMile = 1.60934;
            List<Map<String, Object>> data = new ArrayList<>();
            for (PlaceInfo place : placesRetrieved) {
                Map<String, Object> map = new HashMap<>();
                map.put("placeIcon", R.drawable.ic_shopping_cart_black_48dp);
                map.put("placeName", place.getName());
                map.put("placeAddress", place.getAddress());
                String distance = String.format(
                        getString(string.distance),
                        place.getDistanceInKilometers(),
                        place.getDistanceInKilometers() / kilometersInAMile);
                map.put("placeDistance", distance);
                data.add(map);
            }

            return new SimpleAdapter(MainActivity.this, data,
                    R.layout.place_item,
                    new String[]{"placeIcon", "placeName", "placeAddress",
                            "placeDistance"},
                    new int[]{R.id.place_Icon, R.id.place_name,
                            R.id.place_address,
                            R.id.place_distance});
        }


        /**
         * Retrieves the list of nearby places through appropriate
         * CloudEndpoint.
         * @param params the current geolocation for which to retrieve the list
         *      of nearby places.
         * @return the collection of retrieved nearby places.
         *
        @Override
        protected PlaceInfoCollection doInBackground(final Location... params) {
            Location checkInLocation = params[0];

            float longitude;
            float latitude;

            if (checkInLocation == null) {
                // This is used to easily simulate a location in the emulator
                // when developing. For real devices deployment,
                // this temporary code should be removed and the function
                // should just return null.
                longitude = BuildConfig.DUMMY_LONGITUDE;
                latitude = BuildConfig.DUMMY_LATITUDE;
                // return null;
            } else {
                latitude = (float) checkInLocation.getLatitude();
                longitude = (float) checkInLocation.getLongitude();
            }

            PlaceInfoCollection result;

            // Retrieve the list of up to 10 places within 50 kms
            try {

                final long distanceInKm = 50;
                final int count = 10;

                result = shoppingAssistantAPI.places().getPlaces(
                        Float.toString(longitude), Float.toString(latitude),
                        distanceInKm, count)
                        .execute();
            } catch (IOException e) {
                String message = e.getMessage();
                if (message == null) {
                    message = e.toString();
                }
                LOG.severe("Exception=" + message);
                result = null;
            }
            return result;
        }
    }*/

    /**
     * AsyncTask for calling Mobile Assistant API for checking into a place
     * (e.g., a store).
     */
    /*private class CheckInTask extends AsyncTask<PlaceInfo, Void, Void> {

        /**
         * Calls appropriate CloudEndpoint to indicate that user checked into a
         * place.
         *
         * @param params the place where the user is checking in.
         *
        @Override
        protected Void doInBackground(final PlaceInfo... params) {

            CheckIn checkin = new CheckIn();
            checkin.setPlaceId("StoreOfy");

            try {
                moodTrackerAPI.checkins().insertCheckIn(checkin)
                        .execute();
            } catch (IOException e) {
                String message = e.getMessage();
                if (message == null) {
                    message = e.toString();
                }
                Logger.warn(TAG, "Exception when checking in =" + message);
            }
            return null;
        }
    }*/

    /**
     * AsyncTask for registering the device with GCM in the background.
     */
    private class GcmAsyncRegister extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(final Void... params) {

            String msg;

            try {

                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }

                regId = gcm.register(IApiConstants.SENDER_ID);

                msg = String.format(getString(R.string.gcmRegistrationSuccess), regId);

                sendRegistrationIdToBackend();

            } catch (IOException ex) {

                Logger.warn(TAG, "Exception when registering device with GCM =" + ex.getMessage());
                // If there is an error, we will try again to register the
                // device with GCM the next time the MainActivity starts.
                msg = getString(R.string.gcmRegistrationError);
            }
            return msg;
        }

        @Override
        protected void onPostExecute(final String msg) {

            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            Logger.info(TAG, msg);
        }
    }
}
