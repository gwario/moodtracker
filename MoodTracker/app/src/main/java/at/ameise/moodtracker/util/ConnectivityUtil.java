package at.ameise.moodtracker.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Contains utility methods to determine network connectivity.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 03.12.15.
 */
public class ConnectivityUtil {

    private ConnectivityUtil() {}

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
