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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoodHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoodHistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LineChart lineChart;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoodHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoodHistoryFragment newInstance(String param1, String param2) {
        MoodHistoryFragment fragment = new MoodHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //default loader
        getLoaderManager().initLoader(ILoader.MOOD_HISTORY_ALL_VALUES_LOADER, null, this);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mood_history, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mood_history, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.optAllValues:
                getLoaderManager().restartLoader(ILoader.MOOD_HISTORY_ALL_VALUES_LOADER, null, this);
                return true;

            case R.id.optGbDay:
                getLoaderManager().restartLoader(ILoader.MOOD_HISTORY_PER_DAY_LOADER, null, this);
                return true;

            case R.id.optGbWeek:
                getLoaderManager().restartLoader(ILoader.MOOD_HISTORY_PER_WEEK_LOADER, null, this);
                return true;

            case R.id.optGbMonth:
                getLoaderManager().restartLoader(ILoader.MOOD_HISTORY_PER_MONTH_LOADER, null, this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
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

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final ArrayList<String> xVals = new ArrayList<>();
        final ArrayList<LineDataSet> yVals = new ArrayList<>();

        if(data.moveToFirst()) {

            ArrayList<Entry> entries = new ArrayList<>();
            int index = 0;

            Mood mood = null;

            do {

                mood = MoodTableHelper.fromCursor(data);
                entries.add(index, new Entry(mood.getMood(), index));
                xVals.add(dateFormat.format(mood.getDate().getTime()));
                index++;

            } while(data.moveToNext());

            yVals.add(new LineDataSet(entries, "Mood history"));
        }
        Logger.info(ITag.MOOD_HISTORY, "Found " + data.getCount() + " moods");
        lineChart.setData(new LineData(xVals, yVals));
        lineChart.invalidate();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
