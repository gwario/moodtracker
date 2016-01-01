package at.ameise.moodtracker.app.fragment;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;

/**
 * The default value formatter for Y values in {@link MoodHistoryFragment}
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 06.04.15.
 */
public class DefaultYAxisValueFormatter implements YAxisValueFormatter {

    private DecimalFormat mFormat;

    public DefaultYAxisValueFormatter() {

        mFormat = new DecimalFormat("0.0");
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return mFormat.format(value);
    }
}