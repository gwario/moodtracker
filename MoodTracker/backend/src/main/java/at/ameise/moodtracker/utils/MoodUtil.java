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

package at.ameise.moodtracker.utils;

import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.googlecode.objectify.Ref;

import java.util.List;
import java.util.logging.Logger;

import at.ameise.moodtracker.models.Mood;
import at.ameise.moodtracker.models.UserAccount;

import static at.ameise.moodtracker.OfyService.ofy;

/**
 * Mood Utility class.
 */
public final class MoodUtil {

    private static final Logger LOG = Logger.getLogger(MoodUtil.class.getName());

    private MoodUtil() {}

    /**
     * Retrieves the list of moods of a given user.
     * @param userAccount The account of the user.
     * @return List of all matching {@link Mood} entities.
     */
    @SuppressWarnings({"cast", "unchecked"})
    public static List<Mood> getMoodsOfUser(final UserAccount userAccount) {

        LOG.info("list moods for user = " + userAccount.getEmail());

        return ofy().load().type(Mood.class)
                .ancestor(userAccount)
                .list();
    }

    /**
     * Retrieves the list of moods of a given user.
     * @param userAccount The account of the user.
     * @return List of all {@link Mood} entities which have {@link Mood#getTimestamp()} after afterDateTime.
     */
    @SuppressWarnings({"cast", "unchecked"})
    public static List<Mood> getMoodsOfUserAfter(final UserAccount userAccount, final DateTime afterDateTime) {

        LOG.info("list moods for user = " + userAccount.getEmail() + " after " + afterDateTime);

        return ofy().load().type(Mood.class)
                .ancestor(userAccount)
                .filter("timestampMs >", afterDateTime.getMillis())
                .list();
    }
}
