package at.ameise.moodtracker.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import at.ameise.moodtracker.ILoader;
import at.ameise.moodtracker.ITag;
import at.ameise.moodtracker.R;
import at.ameise.moodtracker.domain.Mood;
import at.ameise.moodtracker.domain.MoodCursorHelper;
import at.ameise.moodtracker.domain.MoodTableHelper;
import at.ameise.moodtracker.util.Logger;
import at.ameise.moodtracker.util.ShareUtil;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoodHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoodHistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Parameter to specify the loader to be used initially.
     */
    public static final String ARG_INITIAL_LOADER = "initialLoader";

    private int mDisplayingLoader;

    private LineChart lineChart;

    private Cursor allValuesCursor;
    private Cursor perDayCursor;
    private Cursor perWeekCursor;
    private Cursor perMonthCursor;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param defaultLoader The loader to be used initially.
     * @return A new instance of fragment MoodHistoryFragment.
     */
    public static MoodHistoryFragment newInstance(int  defaultLoader) {

        final MoodHistoryFragment fragment = new MoodHistoryFragment();
        final Bundle args = new Bundle();

        args.putInt(ARG_INITIAL_LOADER, defaultLoader);

        fragment.setArguments(args);

        return fragment;
    }

    public MoodHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            mDisplayingLoader = getArguments().getInt(ARG_INITIAL_LOADER, ILoader.MOOD_HISTORY_ALL_VALUES_LOADER);

        } else {

            mDisplayingLoader = ILoader.MOOD_HISTORY_ALL_VALUES_LOADER;
        }
        //TODO either create all loaders at first and only populate graphic with the current or only keep one loader at a time and destroy the old if a new one is required
        //TODO since we want to have all values loader too, we should consider it a performance issue to keep this loader....
        //TODO so it maybe better to keep only on loader! But we need the all values loader for the mostRecentMood... so we need it anyway...
        //TODO so we keep all loaders.... :-(

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //init loaders
        getLoaderManager().initLoader(ILoader.MOOD_HISTORY_ALL_VALUES_LOADER, null, this);
        getLoaderManager().initLoader(ILoader.MOOD_HISTORY_PER_DAY_LOADER, null, this);
        getLoaderManager().initLoader(ILoader.MOOD_HISTORY_PER_WEEK_LOADER, null, this);
        getLoaderManager().initLoader(ILoader.MOOD_HISTORY_PER_MONTH_LOADER, null, this);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mood_history, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.mood_history, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem selectedItem) {

        switch (selectedItem.getItemId()) {
            case R.id.menu_item_history_day_values:
                if(mDisplayingLoader != ILoader.MOOD_HISTORY_ALL_VALUES_LOADER) {
                    mDisplayingLoader = ILoader.MOOD_HISTORY_ALL_VALUES_LOADER;
                    repopulateChart(allValuesCursor);
                }
                return true;

            case R.id.menu_item_history_daily_avg:
                if(mDisplayingLoader != ILoader.MOOD_HISTORY_PER_DAY_LOADER) {
                    mDisplayingLoader = ILoader.MOOD_HISTORY_PER_DAY_LOADER;
                    repopulateChart(perDayCursor);

                }
                return true;

            case R.id.menu_item_history_weekly_avg:
                if(mDisplayingLoader != ILoader.MOOD_HISTORY_PER_WEEK_LOADER) {
                    mDisplayingLoader = ILoader.MOOD_HISTORY_PER_WEEK_LOADER;
                    repopulateChart(perWeekCursor);
                }
                return true;

            case R.id.menu_item_history_month_avg:
                if(mDisplayingLoader != ILoader.MOOD_HISTORY_PER_MONTH_LOADER) {
                    mDisplayingLoader = ILoader.MOOD_HISTORY_PER_MONTH_LOADER;
                    repopulateChart(perMonthCursor);
                }
                return true;

            case R.id.menu_item_share:
                if(allValuesCursor != null) {
                    if (allValuesCursor.moveToLast())
                        ShareUtil.shareMood(getActivity(), MoodTableHelper.fromCursor(allValuesCursor));
                    else
                        Toast.makeText(getActivity(), R.string.message_no_moods_yet, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.message_still_loading, Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(selectedItem);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineChart = (LineChart) view.findViewById(R.id.lcMoodHistory);

        lineChart.setNoDataText("No data!");
        lineChart.setNoDataTextDescription("No data why: ...");
        lineChart.setDescription("");
    }

    /**
     * Repopulates the chart with the values of the specified {@link android.database.Cursor}.
     * This method should be called after new data was loaded by the cursor providing loader.
     * @param data the cursor containing moods.
     */
    private void repopulateChart(Cursor data) {

        final DecimalFormat twoDForm = new DecimalFormat("#,##");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale);
        final ArrayList<String> xVals = new ArrayList<>();
        final ArrayList<LineDataSet> yVals = new ArrayList<>();

        if (data.moveToFirst()) {

            ArrayList<Entry> entries = new ArrayList<>();
            int index = 0;

            Mood mood;

            do {

                mood = MoodTableHelper.fromCursor(data);
                entries.add(index, new Entry(Float.parseFloat(twoDForm.format(mood.getMood())), index));
                xVals.add(dateFormat.format(mood.getDate().getTime()));
                index++;

            } while (data.moveToNext());

            LineDataSet lineData = new LineDataSet(entries, "Mood history");
            lineData.setDrawCircles(true);
            lineData.setDrawCubic(false);
            lineData.setDrawFilled(true);
            yVals.add(lineData);
        }
        Logger.info(ITag.MOOD_HISTORY, "Found " + data.getCount() + " moods");
        LineData lineData = new LineData(xVals, yVals);
        lineData.setDrawValues(true);

        lineChart.setData(lineData);

        if(mDisplayingLoader == ILoader.MOOD_HISTORY_ALL_VALUES_LOADER) {

            lineChart.setVisibleXRange(16);
            lineChart.moveViewToX(lineData.getXValCount()-17);
//                lineChart.setMaxVisibleValueCount(16); after 16 values, the values wont be drawn anymore...

        } else if(mDisplayingLoader == ILoader.MOOD_HISTORY_PER_DAY_LOADER) {

            lineChart.setVisibleXRange(7);
            lineChart.moveViewToX(lineData.getXValCount()-8);
//                lineChart.setMaxVisibleValueCount(16);

        } else if(mDisplayingLoader == ILoader.MOOD_HISTORY_PER_WEEK_LOADER) {

            lineChart.setVisibleXRange(4);
            lineChart.moveViewToX(lineData.getXValCount()-5);
//                lineChart.setMaxVisibleValueCount(16);

        } else if(mDisplayingLoader == ILoader.MOOD_HISTORY_PER_MONTH_LOADER) {

            lineChart.setVisibleXRange(4);
            lineChart.moveViewToX(lineData.getXValCount()-5);
//                lineChart.setMaxVisibleValueCount(16);
        }

        lineChart.invalidate();
        lineChart.getLegend().setEnabled(false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        final Loader<Cursor> loader;

        switch(id) {
            case ILoader.MOOD_HISTORY_ALL_VALUES_LOADER:
                loader = MoodCursorHelper.getAllMoodsCursorLoader(getActivity());
                break;

            case ILoader.MOOD_HISTORY_PER_DAY_LOADER:
                loader = MoodCursorHelper.getAllMoodsAvgDayCursorLoader(getActivity());
                break;

            case ILoader.MOOD_HISTORY_PER_WEEK_LOADER:
                loader = MoodCursorHelper.getAllMoodsAvgWeekCursorLoader(getActivity());
                break;

            case ILoader.MOOD_HISTORY_PER_MONTH_LOADER:
                loader = MoodCursorHelper.getAllMoodsAvgMonthCursorLoader(getActivity());
                break;

            default:
                throw new AssertionError("Not yet implemented! id="+id);
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(loader.getId() == ILoader.MOOD_HISTORY_ALL_VALUES_LOADER) {

            allValuesCursor = data;

        } else if(loader.getId() == ILoader.MOOD_HISTORY_PER_DAY_LOADER) {

            perDayCursor = data;

        } else if(loader.getId() == ILoader.MOOD_HISTORY_PER_WEEK_LOADER) {

            perWeekCursor = data;

        } else if(loader.getId() == ILoader.MOOD_HISTORY_PER_MONTH_LOADER) {

            perMonthCursor = data;
        }


        if(loader.getId() == mDisplayingLoader) {

            repopulateChart(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        //remove ref to cursor since it was closed with the loader...
        if(loader.getId() == ILoader.MOOD_HISTORY_ALL_VALUES_LOADER)
            allValuesCursor = null;
        if(loader.getId() == ILoader.MOOD_HISTORY_PER_DAY_LOADER)
            perDayCursor = null;
        if(loader.getId() == ILoader.MOOD_HISTORY_PER_WEEK_LOADER)
            perWeekCursor = null;
        if(loader.getId() == ILoader.MOOD_HISTORY_PER_MONTH_LOADER)
            perMonthCursor = null;
    }

}
