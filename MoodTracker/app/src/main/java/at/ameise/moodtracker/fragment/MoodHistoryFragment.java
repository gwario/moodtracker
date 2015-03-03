package at.ameise.moodtracker.fragment;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import at.ameise.moodtracker.ILoader;
import at.ameise.moodtracker.ITag;
import at.ameise.moodtracker.R;
import at.ameise.moodtracker.domain.Mood;
import at.ameise.moodtracker.domain.MoodCursorHelper;


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getLoaderManager().initLoader(ILoader.MOOD_HISTORY_LOADER, null, this);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mood_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineChart = (LineChart) view.findViewById(R.id.lcMoodHistory);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return MoodCursorHelper.getAllMoodsCursorLoader(getActivity());
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

                mood = MoodCursorHelper.fromCursor(data);
                entries.add(index++, new Entry(mood.getMood(), index));
                xVals.add(dateFormat.format(mood.getDate().getTime()));

            } while(data.moveToNext());

            yVals.add(new LineDataSet(entries, "Me"));
        }
        Log.i(ITag.TAG_MOOD_HISTORY, "Found "+data.getCount()+" moods");
        lineChart.setData(new LineData(xVals, yVals));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
