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

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.seaclouds.platform.discoverer.core.Discoverer;
import eu.seaclouds.platform.discoverer.core.Offering;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;


@Path("/delete")
@Produces(MediaType.APPLICATION_JSON)
public class DeleteAPI {
    /* vars */
    private Discoverer discoverer;

    public DeleteAPI(Discoverer discoverer) {
        this.discoverer = discoverer;
    }

    @POST
    public DeleteRepresentation deleteOfferingById(@QueryParam("oid") String offerId)
            throws IOException {

        ArrayList<String> removedOfferingIds = new ArrayList<>();

        /* if the offering id is valid and the discoverer is able to remove the offering associated
         * then the offering id is added to the list of removed offering  */
        if(this.discoverer.removeOffering(offerId)) {
            removedOfferingIds.add(offerId);
        }

        return new DeleteRepresentation(removedOfferingIds);
    }

    private class DeleteRepresentation {

        private ArrayList<String> removedOfferingIds;

        public DeleteRepresentation(ArrayList<String> removedOfferingIds) {
            this.removedOfferingIds = removedOfferingIds;
        }

        @JsonProperty("deleted_offerings")
        public ArrayList<String> getDeletedOfferings() {
            return removedOfferingIds;
        }
    }
}
