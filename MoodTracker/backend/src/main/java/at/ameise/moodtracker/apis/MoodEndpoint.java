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

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.googlecode.objectify.Ref;

import at.ameise.moodtracker.IApiConstants;
import at.ameise.moodtracker.models.Mood;
import at.ameise.moodtracker.models.UserAccount;
import at.ameise.moodtracker.utils.EndpointUtil;
import at.ameise.moodtracker.utils.MoodUtil;
import at.ameise.moodtracker.utils.UserAccountUtil;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static at.ameise.moodtracker.OfyService.ofy;

/**
 * Exposes REST API over Mood resources.
 */
@Api(name = "moodTrackerBackend", version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = IApiConstants.API_OWNER,
                ownerName = IApiConstants.API_OWNER,
                packagePath = IApiConstants.API_PACKAGE_PATH
        )
)
@ApiClass(resource = "mood",
        clientIds = {
                IApiConstants.ANDROID_CLIENT_ID,
                IApiConstants.WEB_CLIENT_ID},
        audiences = {IApiConstants.AUDIENCE_ID}
)
public class MoodEndpoint {

    private static final Logger LOG = Logger.getLogger(MoodEndpoint.class.getName());

    /**
     * Lists all the entities inserted in datastore.
     * @param user the user requesting the entities.
     * @return List of all entities persisted.
     * @throws com.google.api.server.spi.ServiceException if user is not authorized
     */
    @ApiMethod(httpMethod = "GET")
    public final List<Mood> listMoods(final User user) throws ServiceException {
        EndpointUtil.throwIfNotAuthenticated(user);

        UserAccount userAccount = UserAccountUtil.getUser(user.getEmail());

        List<Mood> moodList = ofy().load().type(Mood.class).ancestor(userAccount).list();

        LOG.info("Returning all moods of "+user.getEmail());
        LOG.fine("Returning all " + moodList.size() + " moods of " + user.getEmail());
        return moodList;
    }

    /**
     * Lists all the entities inserted in datastore since a certain point in time(exclusive).
     * @param user the user requesting the entities.
     * @return List of all entities persisted.
     * @throws com.google.api.server.spi.ServiceException if user is not authorized
     */
    @ApiMethod(httpMethod = "GET", path = "since")
    public final List<Mood> listMoodsSince(final User user, DateTime since) throws ServiceException {
        EndpointUtil.throwIfNotAuthenticated(user);

        UserAccount userAccount = UserAccountUtil.getUser(user.getEmail());

        List<Mood> moodList = MoodUtil.getMoodsOfUserAfter(userAccount, since);

        LOG.info("Returning all moods of "+user.getEmail()+" since "+since.toString());
        LOG.fine("Returning all " + moodList.size() + " moods of " + user.getEmail()+" since "+since.toString());
        return moodList;
    }

    /**
     * Returns the most recent entity.
     * @param user the user requesting the entities.
     * @return The most recent entity or null if there aren't any moods yet.
     * @throws com.google.api.server.spi.ServiceException if user is not authorized
     */
    @ApiMethod(httpMethod = "GET", path = "mostRecent")
    public final Mood getMostRecentMood(final User user) throws ServiceException {
        EndpointUtil.throwIfNotAuthenticated(user);

        UserAccount userAccount = UserAccountUtil.getUser(user.getEmail());

        List<Mood> moodList = ofy().load().type(Mood.class).ancestor(userAccount).order("-timestampMs").limit(1).list();

        Mood mostRecentMood = null;
        if(moodList.size() == 1) {

            mostRecentMood = moodList.get(0);
        }

        LOG.info("Returning the most recent mood of "+user.getEmail());
        LOG.fine("Returning the most recent mood of " + user.getEmail()+"(from "+(mostRecentMood != null?mostRecentMood.getDateTime(): null)+")");
        return mostRecentMood;
    }

    /**
     * Inserts the entity into App Engine datastore. It uses HTTP POST method.
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

        UserAccount userAccount = UserAccountUtil.getUser(user.getEmail());
        mood.setUserAccount(Ref.create(userAccount));

        ofy().save().entity(mood).now();

        return mood;
    }

    /**
     * Updates an entity. It uses HTTP PUT method.
     * @param mood the entity to be updated.
     * @param user the user updating the entity.
     * @return The updated entity.
     * @throws com.google.api.server.spi.ServiceException if user is not authorized
     */
    @ApiMethod(httpMethod = "PUT")
    public final Mood updateMood(final Mood mood, final User user) throws ServiceException {
        EndpointUtil.throwIfNotAuthenticated(user);

        ofy().save().entity(mood).now();

        return mood;
    }

    /**
     * Removes the entity with primary key id. It uses HTTP DELETE method.
     * @param id the primary key of the entity to be deleted.
     * @param user the user deleting the entity.
     * @throws com.google.api.server.spi.ServiceException if user is not authorized
     */
    @ApiMethod(httpMethod = "DELETE")
    public final void removeMood(@Named("id") final String id, final User user) throws ServiceException {
        EndpointUtil.throwIfNotAuthenticated(user);

        Mood mood = ofy().load().type(Mood.class).id(id).now();

        if (mood == null) {
            LOG.info( "Mood " + id + " not found, skipping deletion.");
            return;
        }
        ofy().delete().entity(mood).now();
    }
}
