package at.ameise.moodtracker.utils;

import com.google.appengine.api.users.User;

import java.util.List;
import java.util.logging.Logger;

import at.ameise.moodtracker.models.Mood;
import at.ameise.moodtracker.models.UserAccount;

import static at.ameise.moodtracker.OfyService.ofy;

public final class UserAccountUtil {

    private static final Logger LOG = Logger.getLogger(UserAccountUtil.class.getName());

    private UserAccountUtil() {}

    /**
     * Retrieves the list of moods of a given user.
     * @param userEmail The email address of the user.
     * @return The user account.
     */
    public static UserAccount getUser(final String userEmail) {

        List<UserAccount> userAccounts = ofy().load().type(UserAccount.class).filter("email =", userEmail).limit(1).list();

        if(userAccounts.size() < 1) {

            return null;

        } else {

            return userAccounts.get(0);
        }
    }

    /**
     * Creates the user account for a given user.
     * @param user The user.
     * @return The UserAccount of the user.
     */
    public static UserAccount createUser(final User user) {

        UserAccount userAccount = new UserAccount();

        userAccount.setEmail(user.getEmail());

        ofy().save().entity(userAccount).now();

        return userAccount;
    }
}
