package at.ameise.moodtracker.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import at.ameise.moodtracker.ITag;
import at.ameise.moodtracker.R;
import at.ameise.moodtracker.domain.Mood;
import at.ameise.moodtracker.domain.MoodCursorHelper;
import at.ameise.moodtracker.util.Logger;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnterMoodFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com>.
 */
public class EnterMoodFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, View.OnTouchListener {

    public static final String TAG = "EnterMoodFrag";

    private SeekBar sbCurrentMood;
    private TextView tvCurrentMood;
    private Button bUpdateCurrentMood;
    private GestureDetector mCurrentMoodDoubleTapGestureDetector;

    /**
     * Listener to propagate events to the fragment holder.
     */
    private OnFragmentInteractionListener mFragmentInteractionListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EnterMoodFragment.
     */
    public static EnterMoodFragment newInstance() {

        final EnterMoodFragment fragment = new EnterMoodFragment();
        final Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    public EnterMoodFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        Logger.verbose(ITag.ENTER_MOOD, "Set current mood to " + currentMood);
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

            final Mood currentMood = new Mood(getMoodFromSeekbar());

            MoodCursorHelper.createMood(getActivity(), currentMood);

            Logger.verbose(TAG, "Created " + currentMood.toString());
            Toast.makeText(this.getActivity(), "Updated mood", Toast.LENGTH_LONG).show();

            mFragmentInteractionListener.onMoodUpdate(currentMood);
        }
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
    private int getMoodFromSeekbar() {

        return sbCurrentMood.getProgress() + 1;
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

        /**
         * Called when the mood has been updated.
         * @param currentMood the mood which was just set.
         */
        void onMoodUpdate(Mood currentMood);
    }
}
