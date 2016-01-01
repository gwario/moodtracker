package at.ameise.moodtracker.app.util;

import android.content.Context;
import android.widget.Toast;

import at.ameise.moodtracker.R;

/**
 * Util class to display toast messages.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 01.01.16.
 */
public class ToastUtil {

    private ToastUtil() {}

    /**
     * Shows a {@link Toast} message, informing the user, that his mood has been tracked.
     * See R.string.message_enterMood_text
     * @param context the context
     */
    public static void showMoodUpdatedText(Context context) {

        Toast.makeText(context.getApplicationContext(), R.string.message_enterMood_text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a {@link Toast} message, informing the user, that the google play services are mandatory.
     * @param context the context
     */
    public static void showMustInstallGooglePlayServicesText(Context context) {

        Toast.makeText(context.getApplicationContext(), "Google Play Services must be installed.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a {@link Toast} message, informing the user, that the mood data export failed.
     * @param context the context
     */
    public static void showExportFailedText(Context context) {

        Toast.makeText(context.getApplicationContext(), "Export failed!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a {@link Toast} message, informing the user, that the mood data import failed.
     * @param context the context
     */
    public static void showImportFailedText(Context context) {

        Toast.makeText(context.getApplicationContext(), "Import failed!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a {@link Toast} message, informing the user, that there are no moods yet.
     * See R.string.message_no_moods_yet
     * @param context the context
     */
    public static void showNoMoodsYetText(Context context) {

        Toast.makeText(context.getApplicationContext(), R.string.message_no_moods_yet, Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a {@link Toast} message, informing the user, that the moods have not yet been loaded.
     * See R.string.message_still_loading
     * @param context the context
     */
    public static void showMoodsNotYetLoadedText(Context context) {

        Toast.makeText(context.getApplicationContext(), R.string.message_still_loading, Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a {@link Toast} message, informing the user, that the exported data has been coppied to the clip board.
     * @param context the context
     */
    public static void showExportCopiedToClipboardText(Context context) {

        Toast.makeText(context.getApplicationContext(), "Data copied to clipboard.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a {@link Toast} message, informing the user, about the result of a mood import.
     * @param context the context
     * @param successCnt the number of successfully imported moods.
     * @param failedCnt the number of moods which could not be imported.
     */
    public static void showImportResultText(Context context, int successCnt, int failedCnt) {

        Toast.makeText(context.getApplicationContext(), "Imported " + successCnt + " mood records"+(failedCnt > 0?" (" + failedCnt + " failed).":"."), Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a {@link Toast} message, informing the user, about the result of a mood export.
     * @param context the context
     * @param successCnt the number of successfully exported moods.
     * @param failedCnt the number of moods which could not be exported.
     */
    public static void showExportResultText(Context context, int successCnt, int failedCnt) {

        Toast.makeText(context.getApplicationContext(), "Exported " + successCnt + " mood records" + (failedCnt > 0 ? " (" + failedCnt + " failed)." : "."), Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a {@link Toast} message, informing the user, that the synchronization with the backend failed.
     * @param context the context
     */
    public static void showSynchronizationFailedText(Context context) {

        Toast.makeText(context.getApplicationContext(), "Failed to synchronize moods!", Toast.LENGTH_LONG).show();
    }
}
