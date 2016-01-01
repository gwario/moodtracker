package at.ameise.moodtracker.backend.apis;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.ameise.moodtracker.backend.models.Mood;
import at.ameise.moodtracker.backend.models.UserAccount;
import at.ameise.moodtracker.backend.utils.MoodUtil;
import at.ameise.moodtracker.backend.utils.UserAccountUtil;

public class MoodServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(MoodServlet.class.getName());

    @Override
    public final void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException, ServletException {
        super.doPost(req, resp);
        LOG.finest("Request: " + req.toString());

        UserService userService = UserServiceFactory.getUserService();

        User user = userService.getCurrentUser();
        if (user == null) {

            LOG.warning("User not authenticated. Sending redirect...");
            resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));

        } else {

            LOG.info("User authenticated. Creating mood...");

            Mood mood = Mood.getFrom(req);

            UserAccount userAccount = UserAccountUtil.getUserAccount(user);

            MoodUtil.saveMoodOfUser(mood, userAccount);

            LOG.fine("Mood created: "+mood);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
        LOG.finest("Request: " + req.toString());

        UserService userService = UserServiceFactory.getUserService();

        User user = userService.getCurrentUser();
        if (user == null) {

            LOG.warning("User not authenticated. Sending redirect...");
            resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));

        } else {

            LOG.info("User authenticated. Returning his mood...");

            UserAccount userAccount = UserAccountUtil.getUserAccount(user);

            List<Mood> moods = MoodUtil.getMoodsOfUser(userAccount);

            resp.setStatus(HttpServletResponse.SC_OK);

            resp.getWriter().println(Arrays.toString(moods.toArray()));
            resp.getWriter().flush();
            resp.getWriter().close();
        }
    }
}
