package at.ameise.moodtracker.fragment;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

/**
 * The default value formatter for Y values in {@link at.ameise.moodtracker.fragment.MoodHistoryFragment}
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 06.04.15.
 */
public class DefaultValueFormatter implements ValueFormatter {

    private DecimalFormat mFormat;

    public DefaultValueFormatter() {

        mFormat = new DecimalFormat("0.0");
    }

    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value);
    }
}