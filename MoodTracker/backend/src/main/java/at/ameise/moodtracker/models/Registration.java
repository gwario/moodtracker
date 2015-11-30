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

package at.ameise.moodtracker.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;


/**
 * The Objectify object model for device registrations we are persisting.
 */
@Entity
public class Registration {

    /**
     * Unique identifier of this Entity in the database.
     */
    @Id
    private Long id;

    /**
     * The device registration ID.
     */
    @Index
    private String regId;

    /**
     * Returns the registration ID.
     * @return the device registration ID.
     */
    public final String getRegId() {
        return regId;
    }

    /**
     * Sets the registration ID.
     * @param pRegId the device registration ID to set.
     */
    public final void setRegId(final String pRegId) {
        this.regId = pRegId;
    }
}
