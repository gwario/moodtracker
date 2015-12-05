package at.ameise.moodtracker.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import at.ameise.moodtracker.ILoader;
import at.ameise.moodtracker.IPreference;
import at.ameise.moodtracker.ISetting;
import at.ameise.moodtracker.ITag;
import at.ameise.moodtracker.R;
import at.ameise.moodtracker.activity.SimpleImportExportActivity;
import at.ameise.moodtracker.activity.SignInActivity;
import at.ameise.moodtracker.domain.Mood;
import at.ameise.moodtracker.domain.MoodCursorHelper;
import at.ameise.moodtracker.domain.MoodTableHelper;
import at.ameise.moodtracker.service.AverageCalculatorService;
import at.ameise.moodtracker.util.Logger;
import at.ameise.moodtracker.util.ShareUtil;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoodHistoryFragment#newInstance} factory method to create an instance of this
 * fragment.
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com>
 */
public class MoodHistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleDateFormat quarterDailyDateFormat;
    private SimpleDateFormat dailyDateFormat;
    private SimpleDateFormat weeklyDateFormat;
    private SimpleDateFormat monthlyDateFormat;

    /**
     * Parameter to specify the loader to be used initially.
     */
    public static final String ARG_INITIAL_LOADER = "initialLoader";

    private int mDisplayingLoader;
    private LineChart lineChart;
    private Cursor perQuarterDayCursor;
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

        quarterDailyDateFormat = new SimpleDateFormat(getString(R.string.history_date_format_quarterDaily), getResources().getConfiguration().locale);
        dailyDateFormat = new SimpleDateFormat(getString(R.string.history_date_format_daily), getResources().getConfiguration().locale);
        weeklyDateFormat = new SimpleDateFormat(getString(R.string.history_date_format_weekly), getResources().getConfiguration().locale);
        monthlyDateFormat = new SimpleDateFormat(getString(R.string.history_date_format_monthly), getResources().getConfiguration().locale);

        if (getArguments() != null) {

            mDisplayingLoader = getArguments().getInt(ARG_INITIAL_LOADER, ILoader.MOOD_HISTORY_PER_QUARTER_DAY_LOADER);

        } else {

            mDisplayingLoader = ILoader.MOOD_HISTORY_PER_QUARTER_DAY_LOADER;
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
        getLoaderManager().initLoader(ILoader.MOOD_HISTORY_PER_QUARTER_DAY_LOADER, null, this);
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

        if(ISetting.APP_MODE_DEBUG) {

            MenuItem item = menu.add("Run avg Calc");
            item.setOnMenuItemClickListener (new MenuItem.OnMenuItemClickListener(){
                @Override
                public boolean onMenuItemClick (MenuItem item){

                    AverageCalculatorService.startActionCalculateAverage(getActivity(), new MoodTableHelper.EMoodScope[]{ MoodTableHelper.EMoodScope.QUARTER_DAY, MoodTableHelper.EMoodScope.DAY, MoodTableHelper.EMoodScope.WEEK, MoodTableHelper.EMoodScope.MONTH });
                    return true;
                }
            });
        }

        if(SignInActivity.isSignedIn(getActivity())) {

            MenuItem item = menu.add(R.string.options_menu_signOut);
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    SignInActivity.onSignOut(getActivity());
                    getActivity().finish();
                    return true;
                }
            });
        } else {

            MenuItem item = menu.add(R.string.options_menu_signIn);
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    startActivity(new Intent(getActivity(), SignInActivity.class).putExtra(SignInActivity.EXTRA_SIGN_IN, true));
                    getActivity().finish();
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem selectedItem) {

        int itemId = selectedItem.getItemId();

        switch (itemId) {
            case R.id.menu_item_history_day_values:
                if(mDisplayingLoader != ILoader.MOOD_HISTORY_PER_QUARTER_DAY_LOADER) {
                    mDisplayingLoader = ILoader.MOOD_HISTORY_PER_QUARTER_DAY_LOADER;
                    repopulateChart(perQuarterDayCursor, quarterDailyDateFormat);
                }
                return true;

            case R.id.menu_item_history_daily_avg:
                if(mDisplayingLoader != ILoader.MOOD_HISTORY_PER_DAY_LOADER) {
                    mDisplayingLoader = ILoader.MOOD_HISTORY_PER_DAY_LOADER;
                    repopulateChart(perDayCursor, dailyDateFormat);
                }
                return true;

            case R.id.menu_item_history_weekly_avg:
                if(mDisplayingLoader != ILoader.MOOD_HISTORY_PER_WEEK_LOADER) {
                    mDisplayingLoader = ILoader.MOOD_HISTORY_PER_WEEK_LOADER;
                    repopulateChart(perWeekCursor, weeklyDateFormat);
                }
                return true;

            case R.id.menu_item_history_month_avg:
                if(mDisplayingLoader != ILoader.MOOD_HISTORY_PER_MONTH_LOADER) {
                    mDisplayingLoader = ILoader.MOOD_HISTORY_PER_MONTH_LOADER;
                    repopulateChart(perMonthCursor, monthlyDateFormat);
                }
                return true;

            case R.id.menu_item_share:
                if(perQuarterDayCursor != null) {
                    if (perQuarterDayCursor.moveToLast())
                        ShareUtil.shareMood(getActivity(), MoodTableHelper.fromCursor(perQuarterDayCursor));
                    else
                        Toast.makeText(getActivity(), R.string.message_no_moods_yet, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.message_still_loading, Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.menu_item_export:
                startActivity(new Intent().putExtra(SimpleImportExportActivity.EXTRA_IMPORT, false).setClass(getActivity(), SimpleImportExportActivity.class));
                return true;

            case R.id.menu_item_import:
                startActivity(new Intent().putExtra(SimpleImportExportActivity.EXTRA_IMPORT, true).setClass(getActivity(), SimpleImportExportActivity.class));
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
    private void repopulateChart(Cursor data, SimpleDateFormat dateFormat) {

        final ArrayList<String> xValues = new ArrayList<>();
        final ArrayList<LineDataSet> yValues = new ArrayList<>();

        if (data.moveToFirst()) {

            ArrayList<Entry> entries = new ArrayList<>();
            int index = 0;

            Mood mood;

            do {

                mood = MoodTableHelper.fromCursor(data);
                Logger.verbose(ITag.MOOD_HISTORY, mood.toString());

                entries.add(index, new Entry(mood.getMood(), index));
                xValues.add(dateFormat.format(new Date(mood.getTimestamp().getMillis())));

                index++;

            } while (data.moveToNext());

            LineDataSet lineData = new LineDataSet(entries, "Mood history");
            lineData.setDrawCircles(true);
            lineData.setDrawCubic(false);
            lineData.setDrawFilled(true);
            yValues.add(lineData);
        }
        Logger.info(ITag.MOOD_HISTORY, "Found " + data.getCount() + " moods");
        LineData lineData = new LineData(xValues, yValues);
        lineData.setDrawValues(true);

        lineChart.setData(lineData);

//        switch(cursorId) {
//
//            case ILoader.MOOD_HISTORY_PER_DAY_LOADER:
//                break;
//
//            case ILoader.MOOD_HISTORY_PER_WEEK_LOADER:
//                break;
//
//            case ILoader.MOOD_HISTORY_PER_MONTH_LOADER:
//                break;
//
//            case ILoader.MOOD_HISTORY_PER_QUARTER_DAY_LOADER:
//            default:
//
//                break;
//        }

        lineChart.invalidate();

        lineChart.getAxisLeft().setAxisMinValue(0.5f);
        lineChart.getAxisLeft().setAxisMaxValue(7.5f);
        lineChart.getAxisLeft().setValueFormatter(new DefaultValueFormatter());
        lineChart.getAxisRight().setAxisMinValue(0.5f);
        lineChart.getAxisRight().setAxisMaxValue(7.5f);
        lineChart.getAxisRight().setValueFormatter(new DefaultValueFormatter());
        lineChart.getLineData().setValueFormatter(new DefaultValueFormatter());
//        lineChart.setMaxVisibleValueCount(18);//hide value labels after 18 values in viewport
        lineChart.getLegend().setEnabled(false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        final Loader<Cursor> loader;

        switch(id) {
            case ILoader.MOOD_HISTORY_PER_QUARTER_DAY_LOADER:
                loader = MoodCursorHelper.getAllMoodsAvgQuarterDayCursorLoader(getActivity());
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

        if(loader.getId() == ILoader.MOOD_HISTORY_PER_QUARTER_DAY_LOADER) {

            perQuarterDayCursor = data;
            if(loader.getId() == mDisplayingLoader)
                repopulateChart(data, quarterDailyDateFormat);

        } else if(loader.getId() == ILoader.MOOD_HISTORY_PER_DAY_LOADER) {

            perDayCursor = data;
            if(loader.getId() == mDisplayingLoader)
                repopulateChart(data, dailyDateFormat);

        } else if(loader.getId() == ILoader.MOOD_HISTORY_PER_WEEK_LOADER) {

            perWeekCursor = data;
            if(loader.getId() == mDisplayingLoader)
                repopulateChart(data, weeklyDateFormat);

        } else if(loader.getId() == ILoader.MOOD_HISTORY_PER_MONTH_LOADER) {

            perMonthCursor = data;
            if(loader.getId() == mDisplayingLoader)
                repopulateChart(data, monthlyDateFormat);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        //remove ref to cursor since it was closed with the loader...
        if(loader.getId() == ILoader.MOOD_HISTORY_PER_QUARTER_DAY_LOADER)
            perQuarterDayCursor = null;
        if(loader.getId() == ILoader.MOOD_HISTORY_PER_DAY_LOADER)
            perDayCursor = null;
        if(loader.getId() == ILoader.MOOD_HISTORY_PER_WEEK_LOADER)
            perWeekCursor = null;
        if(loader.getId() == ILoader.MOOD_HISTORY_PER_MONTH_LOADER)
            perMonthCursor = null;
    }

}
