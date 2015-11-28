package at.ameise.moodtracker.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import at.ameise.moodtracker.ILoader;
import at.ameise.moodtracker.R;
import at.ameise.moodtracker.domain.Mood;
import at.ameise.moodtracker.domain.MoodCursorHelper;
import at.ameise.moodtracker.domain.MoodTableHelper;
import at.ameise.moodtracker.util.Logger;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SimpleImportExportFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SimpleImportExportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SimpleImportExportFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "SimpleIOFragment";

    private static final String ARG_IMPORT = "argImport";

    private boolean mImport;

    private EditText etData;
    private Button bImport;
    private Button bCopy;
    private Button bPaste;

    private OnFragmentInteractionListener mListener;

    public SimpleImportExportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param doImport whether to use it for import or export.
     * @return A new instance of fragment SimpleImportExportFragment.
     */
    public static SimpleImportExportFragment newInstance(boolean doImport) {

        SimpleImportExportFragment fragment = new SimpleImportExportFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARG_IMPORT, doImport);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            mImport = getArguments().getBoolean(ARG_IMPORT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(mImport) {

            return inflater.inflate(R.layout.fragment_simple_import, container, false);

        } else {

            return inflater.inflate(R.layout.fragment_simple_export, container, false);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(mImport) {

            etData = (EditText) view.findViewById(R.id.fragment_simple_import_etData);
            bImport = (Button) view.findViewById(R.id.fragment_simple_import_bImport);
            bPaste = (Button) view.findViewById(R.id.fragment_simple_import_bPaste);
            bImport.setOnClickListener(this);
            bPaste.setOnClickListener(this);

        } else {

            etData = (EditText) view.findViewById(R.id.fragment_simple_export_etData);
            bCopy = (Button) view.findViewById(R.id.fragment_simple_export_bCopy);
            bCopy.setOnClickListener(this);

            getLoaderManager().initLoader(ILoader.MOOD_HISTORY_ALL_VALUES_LOADER, null, this);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnFragmentInteractionListener) {

            mListener = (OnFragmentInteractionListener) activity;

        } else {

            throw new RuntimeException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {

        int buttonId = view.getId();

        switch (buttonId) {

            case R.id.fragment_simple_export_bCopy:

                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("simple text", etData.getText());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getActivity(), "Data copied to clipboard.", Toast.LENGTH_SHORT).show();

                getActivity().finish();
                break;

            case R.id.fragment_simple_import_bPaste:

                clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                clip = clipboard.getPrimaryClip();
                ClipData.Item item = clip.getItemAt(0);
                etData.setText(item.getText());
                break;

            case R.id.fragment_simple_import_bImport:

                try {

                    String databaseCsv = etData.getText().toString();

                    importDatabaseCsv(databaseCsv);

                    Toast.makeText(getActivity(), "Import successful.", Toast.LENGTH_SHORT).show();

                    getActivity().finish();

                    if(mListener != null)
                        mListener.onImportSuccess();

                } catch (Exception e) {

                    if(mListener != null)
                        mListener.onImportFailure(e);
                }
                break;

            default:
                throw new IllegalArgumentException("Button action not implemented!");
        }

    }

    /**
     * Imports the csv data.
     * @param databaseCsv the data
     */
    private void importDatabaseCsv(String databaseCsv) {

        long imported = 0;
        long failed = 0;

        String[] rows = databaseCsv.split("\n");

        for(int i = 0; i < rows.length; i++) {

            try {

                String row = rows[i];

                Mood mood = MoodTableHelper.fromCsv(row);
                Logger.verbose(TAG, mood.toString());

                MoodCursorHelper.createMood(getActivity(), mood);

                imported++;

            } catch (Exception e) {
                failed++;
                Logger.verbose(TAG, "Failed to import mood!");
            }
        }

        Logger.info(TAG, "Imported "+imported+" moods ("+failed+" failed).");
    }

    /**
     * Returns the whole mood database as csv.
     * @return the whole mood database as csv
     * @param cursor
     */
    private CharSequence exportDatabaseCsv(Cursor cursor) {

        StringBuilder csvData = new StringBuilder();

        if (cursor.moveToFirst()) {
            do {

                csvData.append(MoodTableHelper.csvFromCursor(cursor));

                if(!cursor.isLast())
                    csvData.append("\n");

            } while (cursor.moveToNext());
        }

        return csvData.toString();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        final Loader<Cursor> loader;

        switch(id) {
            case ILoader.MOOD_HISTORY_ALL_VALUES_LOADER:
                loader = MoodCursorHelper.getAllMoodsCursorLoader(getActivity());
                break;

            default:
                throw new AssertionError("Not yet implemented! id="+id);
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        Logger.debug(TAG, "onLoadFinished");

        if(loader.getId() == ILoader.MOOD_HISTORY_ALL_VALUES_LOADER) {

            try {

                etData.setText(exportDatabaseCsv(cursor));

                if(mListener != null) {
                    mListener.onExportSuccess();
                }

            } catch (Exception e) {

                if(mListener != null) {
                    mListener.onExportFailure(e);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

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

        void onImportSuccess();
        void onImportFailure(Exception exception);

        void onExportSuccess();
        void onExportFailure(Exception exception);
    }
}
