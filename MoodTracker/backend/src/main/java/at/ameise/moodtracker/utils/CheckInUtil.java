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

import at.ameise.moodtracker.models.CheckIn;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static at.ameise.moodtracker.OfyService.ofy;

/**
 * CheckIn Utility class.
 */
public final class CheckInUtil {

    /**
     * Log output.
     */
    private static final Logger LOG = Logger
            .getLogger(CheckInUtil.class.getName());

    /**
     * Default constructor, never called.
     */
    private CheckInUtil() {
    }


    /**
     * Retrieves the list of check in done by a given user at a given place
     * since a specific date.
     * @param userEmail The email address of the user.
     * @param placeId   The id of the place.
     * @param dateFrom  The start date for matching check in.
     * @return List of all matching CheckIn entities.
     */
    @SuppressWarnings({"cast", "unchecked"})
    public static List<CheckIn> getCheckInsForUser(final String userEmail,
            final String placeId, final Date dateFrom) {

        LOG.info("list checkins for user = " + userEmail
                + " checked into place = " + placeId
                + "after " + dateFrom);

        return ofy().load().type(CheckIn.class)
                .filter("userEmail", userEmail)
                .filter("placeId", placeId)
                .filter("checkinDate >", dateFrom)
                .list();
    }
}
