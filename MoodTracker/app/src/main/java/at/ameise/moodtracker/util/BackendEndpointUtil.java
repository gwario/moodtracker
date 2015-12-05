package at.ameise.moodtracker.util;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import at.ameise.moodtracker.IApiConstants;
import at.ameise.moodtracker.MoodTrackerApplication;
import at.ameise.moodtracker.activity.SignInActivity;
import at.ameise.moodtracker.moodTrackerBackend.MoodTrackerBackend;

/**
 * Utility class for the backend endpoints.
 *
 * Created by Mario Gastegger <mario DOT gastegger AT gmail DOT com> on 03.12.15.
 */
public class BackendEndpointUtil {

    private BackendEndpointUtil() {}

    /**
     * Builds the endpoints api for authenticated communication with the backend.
     * @return the backend endpoints.
     */
    public static MoodTrackerBackend buildBackendEndpointsApi() {

        MoodTrackerBackend.Builder builder = new MoodTrackerBackend.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), SignInActivity.getCredential());

        builder.setRootUrl(IApiConstants.ROOT_URL);

        builder.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
            @Override
            public void initialize(final AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                abstractGoogleClientRequest.setDisableGZipContent(true);
            }
        });

        builder.setApplicationName(MoodTrackerApplication.class.getSimpleName());

        return builder.build();
    }
}
