package at.ameise.moodtracker.backend.utils;

import com.googlecode.objectify.Ref;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import at.ameise.moodtracker.backend.models.Mood;
import at.ameise.moodtracker.backend.models.UserAccount;

import static at.ameise.moodtracker.backend.OfyService.ofy;

/**
 * Utility class to handle {@link Mood}s.
 */
public final class MoodUtil {

    private static final Logger LOG = Logger.getLogger(MoodUtil.class.getName());

    private MoodUtil() {}

    /**
     * Retrieves all moods of a given user.
     *
     * @param userAccount The account of the user.
     * @return List of all matching {@link Mood} entities.
     */
    //@SuppressWarnings({"cast", "unchecked"}) TODO why is this necessary
    public static List<Mood> getMoodsOfUser(final UserAccount userAccount) {

        LOG.info("list moods for user = " + userAccount.getEmail());

        return ofy().load()
            .type(Mood.class)
            .ancestor(userAccount)
            .list();
    }

    /**
     * Retrieves all moods of a given user after the clients last synchronization.
     *
     * @param userAccount The account of the user.
     * @param syncTimestampNs the timestamp of the most recent mood on the requesting device.
     * @return List of all {@link Mood} entities which have {@link Mood#getSyncTimestampNs()} after syncTimestampNs.
     */
    public static List<Mood> getMoodsOfUserAfterSync(final UserAccount userAccount, final long syncTimestampNs) {

        LOG.info("list moods for user = " + userAccount.getEmail() + " after " + syncTimestampNs);

        return ofy().load()
            .type(Mood.class)
            .ancestor(userAccount)
            .filter("syncTimestampNs >", syncTimestampNs)
            //.order("syncTimestampNs")
            .list();
    }

    /**
     * Saves the given mood. This method sets the {@link Mood#getSyncTimestampNs()} property for
     * synchronization purposes.
     *
     * @param mood the mood to be saved.
     * @param userAccount the user to which the mood is connected to.
     */
    public static void saveMoodOfUser(Mood mood, UserAccount userAccount) {

        mood.setUserAccount(Ref.create(userAccount));
        mood.setSyncTimestampNs(System.nanoTime());

        ofy().save()
            .entity(mood)
            .now();

        LOG.info("saved mood for user = " + userAccount.getEmail() + ": " + mood);
    }

    /**
     * Saves the given moods. This method sets the {@link Mood#getSyncTimestampNs()} property for
     * synchronization purposes.
     *
     * @param moods the moods to be saved.
     * @param userAccount the user to which the mood is connected to.
     */
    public static void saveMoodsOfUser(Iterable<Mood> moods, UserAccount userAccount) {

        int count = 0;
        for(Iterator<Mood> moodIterator = moods.iterator(); moodIterator.hasNext();) {

            count++;
            Mood mood = moodIterator.next();
            mood.setUserAccount(Ref.create(userAccount));
            mood.setSyncTimestampNs(System.nanoTime());
        }

        ofy().save()
            .entities(moods)
            .now();

        LOG.info("Saved "+count+" mood for user = " + userAccount.getEmail() + ".");
    }

    /**
     * Updates the given mood. This method sets the {@link Mood#getSyncTimestampNs()} property for
     * synchronization purposes.
     *
     * @param mood the mood to be updated.
     * @param userAccount the user to which the mood is connected to.
     */
    public static void updateMoodOfUser(Mood mood, UserAccount userAccount) {

        mood.setUserAccount(Ref.create(userAccount));
        mood.setSyncTimestampNs(System.nanoTime());

        ofy().save()
            .entity(mood)
            .now();

        LOG.info("saved mood for user = " + userAccount.getEmail() + ": " + mood);
    }
}
