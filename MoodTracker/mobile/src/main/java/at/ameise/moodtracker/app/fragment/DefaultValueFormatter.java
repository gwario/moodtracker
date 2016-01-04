package at.ameise.moodtracker.app.fragment;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * The default value formatter for Y values in {@link at.ameise.moodtracker.app.fragment.MoodHistoryFragment}
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 06.04.15.
 */
public class DefaultValueFormatter implements ValueFormatter {

    private DecimalFormat mFormat;

    public DefaultValueFormatter() {

        mFormat = new DecimalFormat("0.0");
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return mFormat.format(value);
    }
}