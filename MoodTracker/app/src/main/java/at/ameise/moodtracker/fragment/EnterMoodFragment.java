package at.ameise.moodtracker.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import at.ameise.moodtracker.ISetting;
import at.ameise.moodtracker.ITag;
import at.ameise.moodtracker.R;
import at.ameise.moodtracker.domain.Mood;
import at.ameise.moodtracker.domain.MoodCursorHelper;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnterMoodFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnterMoodFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, View.OnTouchListener {

    public static final String TAG = "EnterMoodFrag";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SeekBar sbCurrentMood;
    private TextView tvCurrentMood;
    private Button bUpdateCurrentMood;
    private GestureDetector mCurrentMoodDoubleTapGestureDetector;

    private int maxMood;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnterMoodFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EnterMoodFragment newInstance(String param1, String param2) {
        EnterMoodFragment fragment = new EnterMoodFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public EnterMoodFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mCurrentMoodDoubleTapGestureDetector = new GestureDetector(this.getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {

                //pass to the click handler of the update button
                EnterMoodFragment.this.onClick(EnterMoodFragment.this.bUpdateCurrentMood);
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enter_mood, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sbCurrentMood = (SeekBar) view.findViewById(R.id.sbCurrentMood);
        tvCurrentMood = (TextView) view.findViewById(R.id.tvCurrentMood);
        bUpdateCurrentMood = (Button) view.findViewById(R.id.bUpdateCurrentMood);

        sbCurrentMood.setOnSeekBarChangeListener(this);
        sbCurrentMood.setOnTouchListener(this);
        bUpdateCurrentMood.setOnClickListener(this);

        sbCurrentMood.setMax(getResources().getInteger(R.integer.max_mood) - 1);// this is a necessary scale correction for the seek bar
        sbCurrentMood.setProgress(getResources().getInteger(R.integer.default_mood) - 1);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        final int currentMood = getMoodFromSeekbar();

        tvCurrentMood.setText(currentMood+"/"+getResources().getInteger(R.integer.max_mood));
        Log.v(ITag.TAG_ENTER_MOOD, "Set current mood to " + currentMood);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.bUpdateCurrentMood) {

            final Mood currentMood = createMoodFromInput();

            MoodCursorHelper.createMood(getActivity(), currentMood);

            Log.v(TAG, "Created " + currentMood.toString());
            Toast.makeText(this.getActivity(), "Updated mood", Toast.LENGTH_LONG).show();
        }
    }

    private Mood createMoodFromInput() {

        final Mood mood = new Mood();

        mood.setDate(Calendar.getInstance());
        mood.setMood(getMoodFromSeekbar());

        return mood;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(v.getId() == R.id.sbCurrentMood)
            mCurrentMoodDoubleTapGestureDetector.onTouchEvent(event);

        return false;
    }

    /**
     * @return The actual mood. This is the scale corrected value of the progress of the seek bar.
     */
    private final int getMoodFromSeekbar() {

        return sbCurrentMood.getProgress() + 1;
    }

}
