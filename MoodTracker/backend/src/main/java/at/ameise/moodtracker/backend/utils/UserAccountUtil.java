package at.ameise.moodtracker.backend.utils;

import com.google.appengine.api.users.User;

import java.util.List;
import java.util.logging.Logger;

import at.ameise.moodtracker.backend.models.UserAccount;

import static at.ameise.moodtracker.backend.OfyService.ofy;

public final class UserAccountUtil {

    private static final Logger LOG = Logger.getLogger(UserAccountUtil.class.getName());

    private UserAccountUtil() {}

    /**
     * Loads the user's account. This method does not check whether the user is authenticated or
     * not! If the user does not yet exist, a new account is created.
     *
     * @param user The {@link User}.
     * @return The user's account.
     */
    public static UserAccount getUserAccount(final User user) {


        List<UserAccount> userAccounts = ofy().load().type(UserAccount.class)
                .filter("email =", user.getEmail())
                .limit(1)
                .list();

        if(userAccounts.size() < 1) {

            LOG.info("User does not yet exist. Creating it...");
            createUser(user.getEmail());
            return null;

        } else {

            return userAccounts.get(0);
        }
    }

    /**
     * Creates the user account for a given email address.
     *
     * @param email The email address of the user.
     * @return The UserAccount of the user.
     */
    private static UserAccount createUser(final String email) {


        UserAccount userAccount = new UserAccount();

        userAccount.setEmail(email);

        ofy().save().entity(userAccount).now();

        return userAccount;
    }
}
