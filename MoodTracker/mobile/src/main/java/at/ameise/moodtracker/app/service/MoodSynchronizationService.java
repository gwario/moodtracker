package at.ameise.moodtracker.app.service;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.ameise.moodtracker.app.TagConstant;
import at.ameise.moodtracker.app.activity.SignInActivity;
import at.ameise.moodtracker.app.domain.MoodCursorHelper;
import at.ameise.moodtracker.app.domain.MoodTableHelper;
import at.ameise.moodtracker.app.util.BackendEndpointUtil;
import at.ameise.moodtracker.app.util.ConnectivityUtil;
import at.ameise.moodtracker.app.util.IntentUtil;
import at.ameise.moodtracker.app.util.Logger;
import at.ameise.moodtracker.app.util.RetryingIntentService;
import at.ameise.moodtracker.app.util.SynchronizationUtil;
import at.ameise.moodtracker.app.util.ToastUtil;
import at.ameise.moodtracker.moodTrackerBackend.MoodTrackerBackend;
import at.ameise.moodtracker.moodTrackerBackend.model.Mood;
import at.ameise.moodtracker.moodTrackerBackend.model.MoodCollection;
import at.ameise.moodtracker.moodTrackerBackend.model.MoodList;

/**
 * IntentService to synchronize the local moods with the backend.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 04.04.15.
 */
public class MoodSynchronizationService extends RetryingIntentService {

    public static final String ACTION_SYNCHRONIZE_MOODS = "at.ameise.moodtracker.app.service.action.SYNCHRONIZE_MOODS";

    /**
     * Starts the service to synchronize moods with the backend.
     * @param context the context.
     */
    public static void startActionSynchronizeMoods(Context context) {

        context.startService(new Intent(context, MoodSynchronizationService.class).setAction(ACTION_SYNCHRONIZE_MOODS));
    }

    public MoodSynchronizationService() {
        super("MoodSynchronizationService");
    }

    @Override
    protected void onRetryHandleIntentFailure(List<Throwable> errors) {

        Logger.error(TagConstant.SYNCHRONIZATION, "Failed to synchronize moods! Cause: " + Arrays.toString(RetryingIntentService.getErrorsMessages(errors)));
        ToastUtil.showSynchronizationFailedText(getApplicationContext());
    }

    @Override
    protected void onRetryHandleIntentSuccess(List<Throwable> errors) {

        if(errors.isEmpty()) {

            Logger.debug(TagConstant.SYNCHRONIZATION, "Synchronization successful.");

        } else {

            Logger.error(TagConstant.SYNCHRONIZATION, "Failed to synchronize moods! Cause: " + Arrays.toString(RetryingIntentService.getErrorsMessages(errors)));
        }
    }

    @Override
    protected void onRetryHandleIntent(Intent intent) throws Throwable {

        if (IntentUtil.hasAction(intent, ACTION_SYNCHRONIZE_MOODS)) {

            handleActionSynchronizeMoods(intent);

        } else {

            throw new AssertionError("Unhandled action: "+intent.getAction());
        }
    }

    /**
     * Handles the {@link MoodSynchronizationService#ACTION_SYNCHRONIZE_MOODS}.
     *
     * @param intent the intent
     */
    private void handleActionSynchronizeMoods(Intent intent) throws IOException {

        if(SynchronizationUtil.isSynchronizationActivated(getApplicationContext())
            && SignInActivity.isSignedIn(getApplicationContext())
            && ConnectivityUtil.isNetworkAvailable(getApplicationContext())) {

            MoodTrackerBackend backend = BackendEndpointUtil.buildBackendEndpointsApi();

            //get the timestamp of the most recently synchronized mood
            Long mostRecentSyncTimestampNs = MoodCursorHelper.getMostRecentSyncTimestampNs(getApplicationContext());

            //get all data since last sync (give me data since my last sync ts(which is obtained with the sync from the backend))
            MoodCollection moodCollection;
            if(mostRecentSyncTimestampNs != null) {

                Logger.debug(TagConstant.SYNCHRONIZATION, "Retrieving Moods newer than "+mostRecentSyncTimestampNs);
                moodCollection = backend.mood().listMoodsSince(mostRecentSyncTimestampNs).execute();

            } else {

                Logger.debug(TagConstant.SYNCHRONIZATION, "No synchronized local moods yet. Retrieving all moods from server.");
                moodCollection = backend.mood().listMoods().execute();
            }

            if(moodCollection.getItems() != null) {

                Logger.debug(TagConstant.SYNCHRONIZATION, "Got "+moodCollection.getItems().size()+" moods from the backend.");

                for (Mood remoteMoodModel : moodCollection.getItems()) {

                    at.ameise.moodtracker.app.domain.Mood remoteMood = at.ameise.moodtracker.app.domain.Mood.getMoodFromModel(remoteMoodModel);
                    MoodCursorHelper.createMood(getApplicationContext(), remoteMood);
                    Logger.verbose(TagConstant.SYNCHRONIZATION, "Synchronized mood from server: " + remoteMood);
                }

            } else {

                Logger.error(TagConstant.SYNCHRONIZATION,"Failed to get moods from the server!");
            }

            //get all non synchronized moods
            MoodList moodList = new MoodList();
            moodList.setMoods(new ArrayList<Mood>());
            Cursor c = MoodCursorHelper.getAllNonSynchronizedMoodsCursor(getApplicationContext());
            Logger.debug(TagConstant.SYNCHRONIZATION, "Got "+c.getCount()+" not yet synchronized moods.");

             //upload them and get a new sync ts with them and store it in the local db
            if(c.moveToFirst()) {
                do {
                    at.ameise.moodtracker.app.domain.Mood localMood = MoodTableHelper.fromCursor(c);
                    moodList.getMoods().add(localMood.getMoodModel());

                } while (c.moveToNext());
            }
            c.close();

            try {
                Logger.debug(TagConstant.SYNCHRONIZATION, "Synchronizing " + moodList.getMoods().size() + " moods to the backend...");
                MoodList synchronizedMoods = backend.mood().insertMoods(moodList).execute();
                Logger.debug(TagConstant.SYNCHRONIZATION, "Successfully synchronized moods to the backend.");

                Logger.debug(TagConstant.SYNCHRONIZATION, "Updating local synchronization timestamps...");
                for(Mood remoteMood : synchronizedMoods.getMoods()) {
                    try {

                        at.ameise.moodtracker.app.domain.Mood moodWithSyncTs = at.ameise.moodtracker.app.domain.Mood.getMoodFromModel(remoteMood);
                        Logger.verbose(TagConstant.SYNCHRONIZATION, "Updating local synchronization timestamp...");
                        MoodCursorHelper.saveSyncTimestampNs(getApplicationContext(), moodWithSyncTs);
                        Logger.verbose(TagConstant.SYNCHRONIZATION, "Local mood updated.");

                    } catch (Exception e) {

                        Logger.error(TagConstant.SYNCHRONIZATION, "Synchronization timestamp could not be set on local mood!", e);

                        if(remoteMood != null) {
                            Logger.verbose(TagConstant.SYNCHRONIZATION, "Deleting entity on backend...");
                            BackendEndpointUtil.buildBackendEndpointsApi().mood().removeMood(remoteMood).execute();
                            Logger.error(TagConstant.SYNCHRONIZATION, "Successfully deleted entity on the backend!");
                        }
                    }
                }
                Logger.debug(TagConstant.SYNCHRONIZATION, "Successfully updated local synchronization timestamps.");

            } catch (Exception e) {

                Logger.error(TagConstant.SYNCHRONIZATION, "Failed to synchronize moods to the backend!", e);
            }

            Logger.debug(TagConstant.SYNCHRONIZATION, "Synchronized "+moodList.getMoods().size()+" moods to the backend.");

        } else {

            Logger.warn(TagConstant.SYNCHRONIZATION, "Synchronization not activated or no network connectivity!");
        }
    }

}
