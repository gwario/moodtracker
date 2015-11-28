package at.ameise.moodtracker.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import at.ameise.moodtracker.R;
import at.ameise.moodtracker.fragment.SimpleImportExportFragment;
import at.ameise.moodtracker.util.Logger;

public class SimpleImportExportActivity extends Activity implements SimpleImportExportFragment.OnFragmentInteractionListener {

    public static final String TAG = "SimpleIOActivity";

    public static final String EXTRA_IMPORT = "extraImport";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_import_export);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(getIntent().getBooleanExtra(EXTRA_IMPORT, false))
            getFragmentManager().beginTransaction().replace(R.id.simpleImportExportActivity_flFragmentContainer, SimpleImportExportFragment.newInstance(true)).commit();
        else
            getFragmentManager().beginTransaction().replace(R.id.simpleImportExportActivity_flFragmentContainer, SimpleImportExportFragment.newInstance(false)).commit();
    }

    @Override
    public void onImportSuccess() {
        Logger.info(TAG, "Database import successful!");
    }

    @Override
    public void onImportFailure(Exception exception) {
        Logger.error(TAG, exception.getMessage());
        Toast.makeText(SimpleImportExportActivity.this, "Import failed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onExportSuccess() {
        Logger.info(TAG, "Database export successful!");
    }

    @Override
    public void onExportFailure(Exception exception) {
        Logger.error(TAG, exception.getMessage());
        Toast.makeText(SimpleImportExportActivity.this, "Exoport failed!", Toast.LENGTH_SHORT).show();
    }
}
