package at.ameise.moodtracker.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import at.ameise.moodtracker.ITag;
import at.ameise.moodtracker.domain.MoodTableHelper;
import at.ameise.moodtracker.util.Logger;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * Calculates the mood values for 4-value-per-day, daily, weekly and monthly averages.
 * <p/>
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 04.04.15.
 */
public class AverageCalculatorService extends IntentService {

    private static final String ACTION_CALCULATE_AVERAGE = "at.ameise.moodtracker.service.action.CALCULATE_AVERAGE";

    /**
     * Contains an array of {@link at.ameise.moodtracker.domain.MoodTableHelper.EMoodScope#name()}s.
     */
    private static final String EXTRA_SCOPES = "at.ameise.moodtracker.service.extra.SCOPES";


    /**
     * Starts the service to calculate the average in the specified scopes
     * @param context
     * @param scopes the scopes in which the average is to be calculated
     */
    public static void startActionCalculateAverage(Context context, MoodTableHelper.EMoodScope[] scopes) {

        final Intent intent = new Intent(context, AverageCalculatorService.class);
        final String[] scopesStrings = new String[scopes.length];

        for(int i = 0; i < scopes.length; i++) {
            scopesStrings[i] = scopes[i].name();
        }

        intent.setAction(ACTION_CALCULATE_AVERAGE);

        intent.putExtra(EXTRA_SCOPES, scopesStrings);

        context.startService(intent);
    }


    public AverageCalculatorService() {
        super("MoodAggregatorService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {

            final String action = intent.getAction();

            if (ACTION_CALCULATE_AVERAGE.equals(action)) {

                handleActionCalculateAverage(intent);
            }
        }
    }

    /**
     * Handles the {@link at.ameise.moodtracker.service.AverageCalculatorService#ACTION_CALCULATE_AVERAGE}.
     *
     * @param intent
     */
    private void handleActionCalculateAverage(Intent intent) {

        final String[] scopes = intent.getStringArrayExtra(EXTRA_SCOPES);

        /*
         * Calculate all requested averages in the given order.
         */
        for (String scope : scopes) {

            if(MoodTableHelper.EMoodScope.QUARTER_DAY.name().equals(scope)) {

                calculateQuarterDailyAverage();

            } else if(MoodTableHelper.EMoodScope.DAY.name().equals(scope)) {

                calculateDailyAverage();

            } else if(MoodTableHelper.EMoodScope.WEEK.name().equals(scope)) {

                calculateWeeklyAverage();

            } else if(MoodTableHelper.EMoodScope.MONTH.name().equals(scope)) {

                calculateMonthlyAverage();
            }
        }
    }

    private void calculateMonthlyAverage() {

        Logger.debug(ITag.DATABASE, "Calculating monthly average value.");

        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void calculateWeeklyAverage() {

        Logger.debug(ITag.DATABASE, "Calculating weekly average value.");

        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void calculateDailyAverage() {

        Logger.debug(ITag.DATABASE, "Calculating daily average value.");

        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void calculateQuarterDailyAverage() {

        Logger.debug(ITag.DATABASE, "Calculating quarter daily average value.");

        //TODO Find the last day for which no quarterly data exists.

        //TODO select quarterly averages until now and insert them









        throw new UnsupportedOperationException("Not yet implemented");
    }
}
