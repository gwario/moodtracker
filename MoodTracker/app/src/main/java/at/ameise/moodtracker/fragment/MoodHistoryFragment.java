package at.ameise.moodtracker.fragment;

import android.app.Activity;
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
     * Prefix indicating the active menu item.
     */
    final String SELECTED_PREFIX = "> ";

    /**
     * Parameter to specify the loader to be used initially.
     */
    public static final String ARG_INITIAL_LOADER = "initialLoader";

    private int mDisplayingLoader;

    private Mood mMostRecentMood;

    private LineChart lineChart;
    /**
     * Listener to propagate events to the fragment holder.
     */
    private OnFragmentInteractionListener mFragmentInteractionListener;

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

        MenuItem item = menu.findItem(R.id.menu_item_share);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem selectedItem) {

        //TODO find a better solution to get the cursor of this loader...
        //TODO maybe we could keep the cursors as members and only repopulate(loadercallbacks or ) the graphics on option selected

        switch (selectedItem.getItemId()) {
            case R.id.menu_item_history_day_values:
                mDisplayingLoader = ILoader.MOOD_HISTORY_ALL_VALUES_LOADER;
                getLoaderManager().restartLoader(ILoader.MOOD_HISTORY_ALL_VALUES_LOADER, null, this);
                return true;

            case R.id.menu_item_history_daily_avg:
                mDisplayingLoader = ILoader.MOOD_HISTORY_PER_DAY_LOADER;
                getLoaderManager().restartLoader(ILoader.MOOD_HISTORY_PER_DAY_LOADER, null, this);
                return true;

            case R.id.menu_item_history_weekly_avg:
                mDisplayingLoader = ILoader.MOOD_HISTORY_PER_WEEK_LOADER;
                getLoaderManager().restartLoader(ILoader.MOOD_HISTORY_PER_WEEK_LOADER, null, this);
                return true;

            case R.id.menu_item_history_month_avg:
                mDisplayingLoader = ILoader.MOOD_HISTORY_PER_MONTH_LOADER;
                getLoaderManager().restartLoader(ILoader.MOOD_HISTORY_PER_MONTH_LOADER, null, this);
                return true;

            case R.id.menu_item_share:
                if(mMostRecentMood != null)
                    ShareUtil.shareMood(getActivity(), mMostRecentMood);
                else
                    Toast.makeText(getActivity(), R.string.message_no_moods_yet, Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(selectedItem);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineChart = (LineChart) view.findViewById(R.id.lcMoodHistory);
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

            if(data.moveToLast())
                mMostRecentMood = MoodTableHelper.fromCursor(data);
        }

        //TODO keep cursor instance as member
        //TODO if it is the displaying cursor, repopulate the graphics

        if(loader.getId() == mDisplayingLoader) {

            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            final ArrayList<String> xVals = new ArrayList<>();
            final ArrayList<LineDataSet> yVals = new ArrayList<>();

            if (data.moveToFirst()) {

                ArrayList<Entry> entries = new ArrayList<>();
                int index = 0;

                Mood mood = null;

                do {

                    mood = MoodTableHelper.fromCursor(data);
                    entries.add(index, new Entry(mood.getMood(), index));
                    xVals.add(dateFormat.format(mood.getDate().getTime()));
                    index++;

                } while (data.moveToNext());

                LineDataSet lineData = new LineDataSet(entries, "Mood history");
                lineData.setDrawCircles(false);
                lineData.setDrawCubic(false);
                lineData.setDrawFilled(true);
                yVals.add(lineData);
            }
            Logger.info(ITag.MOOD_HISTORY, "Found " + data.getCount() + " moods");
            LineData lineData = new LineData(xVals, yVals);
            lineData.setDrawValues(true);
            lineChart.setData(lineData);
            lineChart.invalidate();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //TODO remove the reference to the cursor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {

            mFragmentInteractionListener = (OnFragmentInteractionListener) activity;

        } catch (ClassCastException e) {

            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mFragmentInteractionListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

    }
}
