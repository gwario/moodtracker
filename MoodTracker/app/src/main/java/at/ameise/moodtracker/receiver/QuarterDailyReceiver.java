package at.ameise.moodtracker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import at.ameise.moodtracker.domain.MoodTableHelper;
import at.ameise.moodtracker.service.AverageCalculatorService;
import at.ameise.moodtracker.util.CalendarUtil;

/**
 * Will be scheduled to run at the end of every quarter of every day.
 *
 * This receiver decides which service is to be started.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 04.04.15.
 */
public class QuarterDailyReceiver extends BroadcastReceiver {

    public QuarterDailyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        final List<MoodTableHelper.EMoodScope> scopeList = new ArrayList<>();
        final MoodTableHelper.EMoodScope[] scopes;
        final Calendar now = Calendar.getInstance();

        if(CalendarUtil.isFirstQuarterOfDay(now)) {
            //after the last quarter of the previous day

            //calc the previous day's last quarter average
            scopeList.add(MoodTableHelper.EMoodScope.QUARTER_DAY);
            //and the previous day's average
            scopeList.add(MoodTableHelper.EMoodScope.DAY);

            if(CalendarUtil.isFirstDayOfWeek(now)) {

                //after the last quarter of the previous week...calc the previous week's average
                scopeList.add(MoodTableHelper.EMoodScope.WEEK);
            }

            if(CalendarUtil.isFirstDayOfMonth(now)) {

                //after the last quarter of the previous week...calc the previous week's average
                scopeList.add(MoodTableHelper.EMoodScope.MONTH);
            }

        } else {

            //after the first, second or the third quarter of the current day...calc the previous quarter's average
            scopeList.add(MoodTableHelper.EMoodScope.QUARTER_DAY);
        }

        scopes = scopeList.toArray(new MoodTableHelper.EMoodScope[scopeList.size()]);

        AverageCalculatorService.startActionCalculateAverage(context, scopes);
    }
}
