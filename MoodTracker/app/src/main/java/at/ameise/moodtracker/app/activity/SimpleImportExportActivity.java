package at.ameise.moodtracker.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import at.ameise.moodtracker.R;
import at.ameise.moodtracker.app.fragment.SimpleImportExportFragment;
import at.ameise.moodtracker.app.service.MoodSynchronizationService;
import at.ameise.moodtracker.app.util.Logger;
import at.ameise.moodtracker.app.util.ToastUtil;

/**
 * Activity which allows import and export of mood data.
 *
 * Mario Gastegger <mario DOT gastegger AT gmail DOT com>
 */
public class SimpleImportExportActivity extends Activity implements SimpleImportExportFragment.OnFragmentInteractionListener {

    public static final String TAG = "SimpleIOActivity";

    public static final String EXTRA_IMPORT = "at.ameise.moodtracker.app.activitySimpleImportExportActivity.extraImport";

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
        MoodSynchronizationService.startActionSynchronizeMoods(this);
    }

    @Override
    public void onImportFailure(Exception exception) {
        Logger.error(TAG, exception.getMessage());
        ToastUtil.showImportFailedText(this);
    }

    @Override
    public void onExportSuccess() {
        Logger.info(TAG, "Database export successful!");
    }

    @Override
    public void onExportFailure(Exception exception) {
        Logger.error(TAG, exception.getMessage());
        ToastUtil.showExportFailedText(this);
    }
}
