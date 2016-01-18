/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.seaclouds.platform.discoverer.api;

import eu.seaclouds.platform.discoverer.core.Discoverer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/refresh")
@Produces(MediaType.APPLICATION_JSON)
public class RefreshAPI {
    /* vars */
    private Discoverer discoverer;

    public RefreshAPI(Discoverer discoverer) {
        this.discoverer = discoverer;
    }

    @GET
    public Boolean refreshRepository() {
        this.discoverer.refreshRepository();
        return true;
    }
}
