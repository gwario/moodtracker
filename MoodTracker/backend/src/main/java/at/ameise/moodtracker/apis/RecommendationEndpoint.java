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
import com.google.appengine.api.users.User;
import at.ameise.moodtracker.IApiConstants;
import at.ameise.moodtracker.utils.EndpointUtil;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static at.ameise.moodtracker.OfyService.ofy;

/**
 * Exposes REST API over Recommendation resources.
 */
@Api(name = "moodTrackerBackend", version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = IApiConstants.API_OWNER,
                ownerName = IApiConstants.API_OWNER,
                packagePath = IApiConstants.API_PACKAGE_PATH
        )
)
@ApiClass(resource = "recommendations",
        clientIds = {
                IApiConstants.ANDROID_CLIENT_ID,
                IApiConstants.WEB_CLIENT_ID},
        audiences = {IApiConstants.AUDIENCE_ID}
)
public class RecommendationEndpoint {

    /**
     * Log output.
     */
    private static final Logger LOG = Logger
            .getLogger(RecommendationEndpoint.class.getName());

    /**
     * Lists all the entities inserted in datastore.
     * @param placeId the identifier of the recommendation to retrieve.
     * @param user the user requesting the entities.
     * @return List of all entities persisted.
     */
    @SuppressWarnings({"cast", "unchecked"})
    @ApiMethod(httpMethod = "GET")
    public final List<Recommendation> listRecommendations(
            @Named("placeId") final Long placeId, final User user) {
        // Optional: Retrieve only recommendations applicable to a given place

        return ofy().load().type(Recommendation.class).filter("expiration >",
                new Date()).list();
    }

    /**
     * Inserts the entity into App Engine datastore. It uses HTTP POST method.
     * @param recommendation the entity to be inserted.
     * @param user the user inserting the entity.
     * @return The inserted entity.
     * @throws com.google.api.server.spi.ServiceException if user is not
     * authorized
     */
    @ApiMethod(httpMethod = "POST")
    public final Recommendation insertRecommendation(final Recommendation
            recommendation, final User user)
            throws ServiceException {
        EndpointUtil.throwIfNotAdmin(user);

        ofy().save().entity(recommendation).now();

        return recommendation;
    }

    /**
     * Updates an entity. It uses HTTP PUT method.
     * @param recommendation the entity to be updated.
     * @param user the user updating the entity.
     * @return The updated entity.
     * @throws com.google.api.server.spi.ServiceException if user is not
     * authorized
     */
    @ApiMethod(httpMethod = "PUT")
    public final Recommendation updateRecommendation(final Recommendation
            recommendation, final User user)
            throws ServiceException {
        EndpointUtil.throwIfNotAdmin(user);

        ofy().save().entity(recommendation).now();

        return recommendation;
    }

    /**
     * Removes the entity with primary key id. It uses HTTP DELETE method.
     * @param id the primary key of the entity to be deleted.
     * @param user the user deleting the entity.
     * @throws com.google.api.server.spi.ServiceException if user is not
     * authorized
     */
    @ApiMethod(httpMethod = "DELETE")
    public final void removeRecommendation(@Named("id") final String id,
            final User user)
            throws ServiceException {
        EndpointUtil.throwIfNotAdmin(user);

        Recommendation recommendation = findRecommendation(id);
        if (recommendation == null) {
            LOG.info(
                    "Recommendation " + id + " not found, skipping deletion.");
            return;
        }
        ofy().delete().entity(recommendation).now();
    }

    /**
     * Searches an entity by ID.
     * @param id the offer ID to search
     * @return the Offer associated to id
     */
    private Recommendation findRecommendation(final String id) {
        return ofy().load().type(Recommendation.class).id(id).now();
    }
}
