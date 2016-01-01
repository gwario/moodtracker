package at.ameise.moodtracker.app.util;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import at.ameise.moodtracker.app.Setting;

/**
 * AsyncTask which retries {@link AsyncTask#doInBackground(Object[])}
 * {@link Setting#BACKEND_CALL_TRIES} times.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 05.12.15.
 */
abstract class RetryingAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    public static final String TAG = "RetryingAsyncTask";

    /**
     * Contains the throwables which occurred during the attempts.
     */
    private List<Throwable> errors = new ArrayList<Throwable>();

    /**
     * True if one attempt succeeded.
     */
    private boolean succeeded;

    /**
     * This method is being retried {@link Setting#BACKEND_CALL_TRIES} times in case an exception was thrown.
     * @param params the parameters.
     * @return the result or null if {@link RetryingAsyncTask#doRetryInBackground(Object[])} failed.
     * @see AsyncTask#doInBackground(Object[])
     */
    protected abstract Result doRetryInBackground(Params... params) throws Throwable;

    /**
     * Returns an array of the messages returned by the error's {@link Throwable#getMessage()} method.
     * @param errors The errors returned by {@link RetryingAsyncTask#onPostExecuteSuccess(Object, List)} or {@link RetryingAsyncTask#onPostExecuteFailure(List)}.
     * @return an array of the {@link Throwable#getMessage()}.
     */
    public static String[] getErrorsMessages(List<Throwable> errors) {

        String[] errorMessages = new String[errors.size()];

        for(int i = 0; i < errors.size(); i++) {
            errorMessages[i] = errors.get(i).getMessage();
        }

        return errorMessages;
    }

    @Override
    protected Result doInBackground(Params... params) {

        Result result = null;

        int tryCnt = 1;
        int maxTries = Setting.BACKEND_CALL_TRIES;

        while(tryCnt <= maxTries) {

            try {

                result = doRetryInBackground(params);

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

        return result;
    }

    @Override
    protected void onPostExecute(Result result) {

        if(succeeded) {

            onPostExecuteSuccess(result, errors);

        } else {

            onPostExecuteFailure(errors);
        }

        super.onPostExecute(result);
    }

    /**
     * Called if the task failed.
     *
     * @param errors The list of the errors of each attempt.
     */
    protected abstract void onPostExecuteFailure(List<Throwable> errors);

    /**
     * Called if the task succeeded.
     *
     * @param result The result of the task.
     * @param errors The list of errors. One for each failed attempt(which preceded the successful attempt).
     */
    protected abstract void onPostExecuteSuccess(Result result, List<Throwable> errors);
}
