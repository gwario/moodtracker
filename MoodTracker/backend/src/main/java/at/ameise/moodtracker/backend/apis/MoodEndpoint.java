package at.ameise.moodtracker.backend.apis;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.users.User;

import java.util.List;
import java.util.logging.Logger;

import at.ameise.moodtracker.backend.ApiConstants;
import at.ameise.moodtracker.backend.apis.dto.MoodList;
import at.ameise.moodtracker.backend.models.Mood;
import at.ameise.moodtracker.backend.utils.EndpointUtil;
import at.ameise.moodtracker.backend.utils.MoodUtil;
import at.ameise.moodtracker.backend.utils.UserAccountUtil;

import static at.ameise.moodtracker.backend.OfyService.ofy;

/**
 * Exposes REST API over Mood resources.
 */
@Api(name = "moodTrackerBackend", version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = ApiConstants.API_OWNER,
                ownerName = ApiConstants.API_OWNER,
                packagePath = ApiConstants.API_PACKAGE_PATH
        )
)
@ApiClass(resource = "mood",
        clientIds = {
                ApiConstants.ANDROID_CLIENT_ID,
                ApiConstants.WEB_CLIENT_ID},
        audiences = {ApiConstants.AUDIENCE_ID}
)
public class MoodEndpoint {

    private static final Logger LOG = Logger.getLogger(MoodEndpoint.class.getName());

    /**
     * Lists all the moods inserted in datastore by the given user.
     *
     * @param user the user of which the moods are to be retrieved.
     * @return List of moods.
     * @throws com.google.api.server.spi.ServiceException if user is not authorized
     */
    @ApiMethod(httpMethod = "GET")
    public final List<Mood> listMoods(final User user) throws ServiceException {
        EndpointUtil.throwIfNotAuthenticated(user);

        List<Mood> moodList = MoodUtil.getMoodsOfUser(UserAccountUtil.getUserAccount(user));

        LOG.info("Returning all moods of "+user.getEmail());
        LOG.fine("Returning all " + moodList.size() + " moods of " + user.getEmail());
        return moodList;
    }

    /**
     * Lists all the moods of the given user, which have been inserted since a certain point in
     * time(exclusive).<br/>
     * <p>To synchronize moods from the server, an app has to determine its most recent local mood
     * and then fetch all moods from the server which have been inserted after that time using this
     * method.</p>
     *
     * @param user the user of which the moods are to be retrieved.
     * @param syncTimestampNs the timestamp of the most recently inserted mood.
     * @return List of moods in ascending order by syncTimestampNs. (THIS IS ESSENTIAL: If the sync fails on the client for some reason, it will continue as expected!)
     * @throws com.google.api.server.spi.ServiceException if user is not authorized
     */
    @ApiMethod(httpMethod = "GET", path = "sync/since")
    public final List<Mood> listMoodsSince(final User user, @Named("syncTimestampNs") final long syncTimestampNs) throws ServiceException {
        EndpointUtil.throwIfNotAuthenticated(user);

        List<Mood> moodList = MoodUtil.getMoodsOfUserAfterSync(UserAccountUtil.getUserAccount(user), syncTimestampNs);

        LOG.info("Returning all moods of "+user.getEmail()+" since "+syncTimestampNs);
        LOG.fine("Returning all " + moodList.size() + " moods of " + user.getEmail()+" since "+syncTimestampNs);
        return moodList;
    }

    /**
     * Inserts a mood entity. This method sets the {@link Mood#getSyncTimestampNs()} to support the
     * synchronization mechanism.<br/>
     * <p>The synchronization works using the {@link Mood#getSyncTimestampNs()} field to determine
     * all moods inserted after a certain point in time.</p>
     *
     * @param mood the entity to be inserted.
     * @param user the user inserting the entity.
     * @return The inserted entity.
     * @throws com.google.api.server.spi.ServiceException if user is not authorized
     */
    @ApiMethod(httpMethod = "POST")
    public final Mood insertMood(final Mood mood, final User user) throws ServiceException {
        EndpointUtil.throwIfNotAuthenticated(user);

        LOG.info("Got new mood by " + user.getEmail());
        LOG.fine("Got new mood by " + user.getEmail() + ": " + mood.toString());

        MoodUtil.saveMoodOfUser(mood, UserAccountUtil.getUserAccount(user));

        return mood;
    }

    /**
     * Inserts a mood entities. This method sets the {@link Mood#getSyncTimestampNs()} to support the
     * synchronization mechanism.<br/>
     * <p>The synchronization works using the {@link Mood#getSyncTimestampNs()} field to determine
     * all moods inserted after a certain point in time.</p>
     *
     * @param moods the entities to be inserted.
     * @param user the user inserting the entity.
     * @return The inserted entity.
     * @throws com.google.api.server.spi.ServiceException if user is not authorized
     */
    @ApiMethod(httpMethod = "POST", path = "sync/")
    public final MoodList insertMoods(final MoodList moods, final User user) throws ServiceException {
        EndpointUtil.throwIfNotAuthenticated(user);

        LOG.info("Got " + moods.size() + " new mood by " + user.getEmail());
        LOG.fine("Got " + moods.size() + " new mood by " + user.getEmail() + ": " + moods.toString());

        MoodUtil.saveMoodsOfUser(moods, UserAccountUtil.getUserAccount(user));

        return moods;
    }

    /**
     * Updates a mood entity. This method updates the {@link Mood#getSyncTimestampNs()} to ensure
     * clients download it with their next synchronization call.
     *
     * @param mood the entity to be updated.
     * @param user the user updating the entity.
     * @return The updated entity.
     * @throws com.google.api.server.spi.ServiceException if user is not authorized
     */
    @ApiMethod(httpMethod = "PUT")
    public final Mood updateMood(final Mood mood, final User user) throws ServiceException {
        EndpointUtil.throwIfNotAuthenticated(user);

        LOG.info("Updating mood by " + user.getEmail());
        LOG.fine("Updating mood by " + user.getEmail() + ": " + mood.toString());

        MoodUtil.updateMoodOfUser(mood, UserAccountUtil.getUserAccount(user));

        return mood;
    }

    /**
     * Removes the mood entity.
     *
     * @param user the user deleting the entity.
     * @throws com.google.api.server.spi.ServiceException if user is not authorized
     */
    @ApiMethod(httpMethod = "DELETE")
    public final void removeMood(final Mood mood, final User user) throws ServiceException {
        EndpointUtil.throwIfNotAuthenticated(user);

        LOG.info("Deleting mood by " + user.getEmail());
        LOG.fine("Deleting mood by " + user.getEmail() + ": " + mood.getKey());

        ofy().delete()
                .entity(mood)
                .now();
    }
}
