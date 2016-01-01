package at.ameise.moodtracker.app.util;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import at.ameise.moodtracker.app.Setting;

/**
 * IntentService which retries {@link IntentService#onHandleIntent(Intent)}
 * {@link Setting#BACKEND_CALL_TRIES} times.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 04.04.15.
 */
public abstract class RetryingIntentService extends IntentService {

    private static final String TAG = "RetryingIntentService";

    /**
     * Contains the throwables which occurred during the attempts.
     */
    private List<Throwable> errors = new ArrayList<>();

    /**
     * True if one attempt succeeded.
     */
    private boolean succeeded;

    /**
     * Returns an array of the messages returned by the error's {@link Throwable#getMessage()}
     * method.
     *
     * @param errors The errors returned by
     * {@link RetryingIntentService#onRetryHandleIntentSuccess(List)} or
     * {@link RetryingIntentService#onRetryHandleIntentFailure(List)}.
     * @return an array of the {@link Throwable#getMessage()}.
     */
    public static String[] getErrorsMessages(List<Throwable> errors) {

        String[] errorMessages = new String[errors.size()];

        for(int i = 0; i < errors.size(); i++) {
            errorMessages[i] = errors.get(i).getMessage();
        }

        return errorMessages;
    }

    protected RetryingIntentService(String name) {
        super(name);
    }

    /**
     * Called if the task failed.
     *
     * @param errors The list of the errors of each attempt.
     */
    protected abstract void onRetryHandleIntentFailure(List<Throwable> errors);

    /**
     * Called if the task succeeded.
     *
     * @param errors The list of errors. One for each failed attempt(which preceded the successful
     *               attempt).
     */
    protected abstract void onRetryHandleIntentSuccess(List<Throwable> errors);

    /**
     * This method is being retried {@link Setting#BACKEND_CALL_TRIES} times in case an exception
     * was thrown.
     * @param intent the parameters.
     * @see IntentService#onHandleIntent(Intent)
     */
    protected abstract void onRetryHandleIntent(Intent intent) throws Throwable;

    @Override
    protected void onHandleIntent(Intent intent) {

        int tryCnt = 1;
        int maxTries = Setting.BACKEND_CALL_TRIES;

        while(tryCnt <= maxTries) {

            try {

                onRetryHandleIntent(intent);
                Logger.debug(TAG, "Try " + tryCnt + " succeeded!");
                succeeded = true;
                break;

            } catch (Throwable e) {

                succeeded = false;
                errors.add(e);
                Logger.warn(TAG, "Try " + tryCnt + " failed! Cause:" + e.getMessage(), e);
                Logger.debug(TAG, "Try " + tryCnt + " of " + maxTries + " failed!");
            }

            tryCnt++;
        }

        if(succeeded) {

            onRetryHandleIntentSuccess(errors);

        } else {

            onRetryHandleIntentFailure(errors);
        }
    }

}
