package at.ameise.moodtracker.app.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import at.ameise.moodtracker.app.LoaderId;
import at.ameise.moodtracker.R;
import at.ameise.moodtracker.app.domain.Mood;
import at.ameise.moodtracker.app.domain.MoodCursorHelper;
import at.ameise.moodtracker.app.domain.MoodTableHelper;
import at.ameise.moodtracker.app.util.Logger;
import at.ameise.moodtracker.app.util.ToastUtil;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SimpleImportExportFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SimpleImportExportFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com>
 */
public class SimpleImportExportFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "SimpleIOFragment";

    private static final String ARG_IMPORT = "at.ameise.moodtracker.app.fragmentSimpleImportExportFragment.extraImport";

    private boolean mImport;

    private EditText etData;
    private Button bImport;
    private Button bCopy;
    private Button bPaste;
    private ProgressDialog progressDialog;

    private static Handler handler;

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

            getLoaderManager().initLoader(LoaderId.MOOD_HISTORY_ALL_VALUES_LOADER, null, this);
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

                ToastUtil.showExportCopiedToClipboardText(getActivity());

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

                    new ImportCsvAsyncTask(getActivity()) {
                        @Override
                        public void onImported(int importCnt, int recordCnt) {

                            int failedCnt = recordCnt - importedCnt;

                            Logger.info(TAG, "Imported " + importedCnt + " mood records" + (failedCnt > 0 ? " (" + failedCnt + " failed)." : "."));

                            ToastUtil.showImportResultText(SimpleImportExportFragment.this.getActivity(), importedCnt, failedCnt);

                            getActivity().finish();

                            if(mListener != null)
                                mListener.onImportSuccess();
                        }
                    }.execute(etData.getText().toString());

                } catch (Exception e) {

                    if(mListener != null)
                        mListener.onImportFailure(e);
                }
                break;

            default:
                throw new IllegalArgumentException("Button action not implemented!");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        final Loader<Cursor> loader;

        switch(id) {
            case LoaderId.MOOD_HISTORY_ALL_VALUES_LOADER:
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

        if(loader.getId() == LoaderId.MOOD_HISTORY_ALL_VALUES_LOADER) {

            try {

                new ExportCsvAsyncTask(getActivity()) {
                    @Override
                    public void onExported(String cvsData, int exportCnt, int recordCnt) {

                        etData.setText(cvsData);

                        int failedCnt = recordCnt - exportCnt;

                        Logger.info(TAG, "Exported " + exportCnt + " mood records" + (failedCnt > 0 ? " (" + failedCnt + " failed).":"."));

                        ToastUtil.showExportResultText(SimpleImportExportFragment.this.getActivity(), exportCnt, failedCnt);

                        if(mListener != null)
                            mListener.onExportSuccess();
                    }
                }.execute(cursor);

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
     * Imports the csv data.
     */
    private abstract class ImportCsvAsyncTask extends AsyncTask<String, Integer, Void> {

        private ProgressDialog progressDialog;

        int importedCnt = -1;
        int recordCnt = -1;

        public ImportCsvAsyncTask(Activity activity) {
            progressDialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... databaseCsv) {

            if (databaseCsv.length != 1) throw new AssertionError();

            String[] rows = databaseCsv[0].split("\n");

            recordCnt = rows.length;
            importedCnt = 0;

            int onePercentCnt = recordCnt / 100;

            for(int iRecord = 0; iRecord < recordCnt; iRecord++) {

                try {

                    publishProgress(iRecord / onePercentCnt);

                    String row = rows[iRecord];

                    Mood mood = MoodTableHelper.fromCsv(row);
                    Logger.verbose(TAG, mood.toString());

                    MoodCursorHelper.createMood(getActivity(), mood);

                    importedCnt++;

                } catch (Exception e) {
                    Logger.verbose(TAG, "Failed to import record!");
                }
            }

            publishProgress(100);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            progressDialog.setMessage("Importing... " + importedCnt + " of " +recordCnt);
            progressDialog.setProgress(values[0]);
        }

        protected void onPostExecute(Void result) {

            progressDialog.dismiss();

            onImported(importedCnt, recordCnt);

            super.onPostExecute(result);
        }

        /**
         * Called when all records are imported.
         * @param importCnt the number of actually imported records.
         * @param recordCnt
         */
        public abstract void onImported(int importCnt, int recordCnt);
    }

    /**
     * Exports the data as csv.
     */
    private abstract class ExportCsvAsyncTask extends AsyncTask<Cursor, Integer, String> {

        private ProgressDialog progressDialog;

        int exportedCnt = -1;
        int recordCnt = -1;

        public ExportCsvAsyncTask(Activity activity) {
            progressDialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Cursor... databaseCursor) {

            if (databaseCursor.length != 1) throw new AssertionError();

            StringBuilder csvData = new StringBuilder();
            Cursor cursor = databaseCursor[0];

            recordCnt = cursor.getCount();
            exportedCnt = 0;

            int onePercentCnt = recordCnt / 100;

            if (cursor.moveToFirst()) {
                do {

                    try {

                        publishProgress(exportedCnt / onePercentCnt);

                        exportedCnt++;

                        csvData.append(MoodTableHelper.csvFromCursor(cursor));

                        if(!cursor.isLast())
                            csvData.append("\n");

                    } catch (Exception e) {
                        Logger.verbose(TAG, "Failed to export record!");
                    }

                } while (cursor.moveToNext());
            }

            publishProgress(100);

            return csvData.toString();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            progressDialog.setMessage("Exporting... " + exportedCnt + " of " +recordCnt);
            progressDialog.setProgress(values[0]);
        }

        protected void onPostExecute(String cvsData) {

            progressDialog.dismiss();

            onExported(cvsData, exportedCnt, recordCnt);

            super.onPostExecute(cvsData);
        }

        /**
         * Called when all records are exported.
         * @param cvsData the exported csv.
         * @param exportCnt the number of actually imported records.
         * @param recordCnt the number of records.
         */
        public abstract void onExported(String cvsData, int exportCnt, int recordCnt);
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

        void onImportSuccess();
        void onImportFailure(Exception exception);

        void onExportSuccess();
        void onExportFailure(Exception exception);
    }
}
