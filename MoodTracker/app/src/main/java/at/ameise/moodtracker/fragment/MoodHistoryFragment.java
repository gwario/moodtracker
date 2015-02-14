package at.ameise.moodtracker.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;

import at.ameise.moodtracker.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoodHistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MoodHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoodHistoryFragment extends Fragment {
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mood_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineChart = (LineChart) view.findViewById(R.id.lcMoodHistory);
        Calendar now = Calendar.getInstance();
        ArrayList<String> xVals = new ArrayList<>();
        xVals.add(now.toString());
        now.add(Calendar.HOUR, 24);
        xVals.add(now.toString());
        now.add(Calendar.HOUR, 24);
        xVals.add(now.toString());
        now.add(Calendar.HOUR, 24);
        xVals.add(now.toString());

        ArrayList<LineDataSet> yVals = new ArrayList<>();
        yVals.add(new LineDataSet(new ArrayList<Entry>() {{ this.add(new Entry(2, 0));this.add(new Entry(2, 1));this.add(new Entry(3, 2));}}, "Me"));

        lineChart.setData(new LineData(xVals, yVals));
    }

}
