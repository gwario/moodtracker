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

package at.ameise.moodtracker;

/**
 * API Keys, Client Ids and Audience Ids for accessing APIs and configuring
 * Cloud Endpoints.
 * When you deploy your solution, you need to use your own API Keys and IDs.
 * Please refer to the documentation for this sample for more details.
 */
public interface IApiConstants {
    
    /** Android client ID from Google Cloud console. */
    String ANDROID_CLIENT_ID = "1008624441817-pfdk2u7462bgnckai1p7u395tsnegl7n.apps.googleusercontent.com";

    /** Web client ID from Google Cloud console. */
    String WEB_CLIENT_ID = "1008624441817-c2fcrutro43qsqcrkn7j91p744iaoofu.apps.googleusercontent.com";

    /** Audience ID used to limit access to some client to the API. */
    String AUDIENCE_ID = WEB_CLIENT_ID;

    /** API package name. */
    String API_OWNER = "moodtracker.ameise.at";

    /** API package path. */
    String API_PACKAGE_PATH = "";
}
