/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.ameise.moodtracker.apis;

import java.io.IOException;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.ameise.moodtracker.models.Mood;
import at.ameise.moodtracker.utils.MoodUtil;

import static at.ameise.moodtracker.OfyService.ofy;

public class MoodServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(MoodServlet.class.getName());

    @Override
    public final void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        String userEmail = req.getParameter("userEmail");
        Float moodValue = Float.valueOf(req.getParameter("mood"));
        Long timestamp = Long.valueOf(req.getParameter("timestamp"));
        String scope = req.getParameter("scope");

        Mood mood = new Mood();

        mood.setScope(scope);
        mood.setMood(moodValue);
        mood.setTimestamp(timestamp);


        // Let Task Queue handle any exceptions through normal retry logic and error logging,
        // so the code only catches InvalidFormatException and allows the other exception pass
        // through.

        try {

            ofy().save().entity(mood).now();

        } catch (IllegalFormatException e) {

            LOG.warning("IllegalFormatException caught. This indicates that "
                    + "the format of the recommendation template is invalid. "
                    + "Skipping generating personalized recommendations");
            return;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);

        String userEmail = req.getParameter("userEmail");
        //DateTime afterDateTime = DateTime.parse(req.getParameter("afterDateTime"));
        LOG.finest("Request: " + req.toString());

        List<Mood> moods = MoodUtil.getMoodsOfUser(userEmail);

        resp.getWriter().println(Arrays.toString(moods.toArray()));
        resp.getWriter().flush();
        resp.getWriter().close();
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
