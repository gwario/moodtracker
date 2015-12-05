package at.ameise.moodtracker.util;

import android.os.AsyncTask;

import java.io.IOException;

import at.ameise.moodtracker.ISetting;

/**
 * AsyncTask which retries {@link AsyncTask#doInBackground(Object[])}
 * {@link at.ameise.moodtracker.ISetting#BACKEND_CALL_TRIES} times.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 05.12.15.
 */
public abstract class RetryingAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    public static final String TAG = "RetryingAsyncTask";

    /**
     * Retries {@link AsyncTask#doInBackground(Object[])} {@link at.ameise.moodtracker.ISetting#BACKEND_CALL_TRIES}.
     * @param params the parameters.
     * @return the result.
     * @see AsyncTask#doInBackground(Object[])
     */
    public abstract Result doRetryInBackground(Params... params) throws Throwable;

    @Override
    protected Result doInBackground(Params... params) {

        Result result = null;

        int tryCnt = 1;
        int maxTries = ISetting.BACKEND_CALL_TRIES;

        while(tryCnt <= maxTries) {

            try {

                result = doRetryInBackground(params);
                break;

            } catch (Throwable e) {

                Logger.warn(TAG, "Try "+tryCnt+" failed! Cause:"+e.getMessage(), e);
                Logger.debug(TAG, "Try " + tryCnt + " of " + maxTries+ " failed!");
            }

            tryCnt++;
        }

        return result;
    }
}
