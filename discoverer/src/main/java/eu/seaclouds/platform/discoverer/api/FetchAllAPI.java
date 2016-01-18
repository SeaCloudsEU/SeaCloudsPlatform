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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


@Path("/fetch_all")
@Produces(MediaType.APPLICATION_JSON)
public class FetchAllAPI {
    /* vars */
    private Discoverer discoverer;

    public FetchAllAPI(Discoverer discoverer) {
        this.discoverer = discoverer;
    }

    @GET
    public FetchAllRepresentation getAllOfferings() throws IOException {

        /* collecting all the ids within the repository */
        ArrayList<String> ids = new ArrayList<String>();
        Collection<String> offeringIds = discoverer.getAllOfferingIds();
        for (String offeringId : offeringIds) {
            ids.add(offeringId);
        }

        Offering offering = this.discoverer.fetchOffer("all");
        String offeringTOSCA = "";

        if(offering != null) {
            offeringTOSCA = offering.toscaString;
        }

        if (offeringTOSCA.isEmpty()) {
            ids.clear();
        }

        return new FetchAllRepresentation(ids, offeringTOSCA);
    }

    private class FetchAllRepresentation {

        private ArrayList<String> offeringIds;
        private String offering;

        public FetchAllRepresentation(ArrayList<String> offeringIds, String offering) {
            this.offeringIds = offeringIds;
            this.offering = offering;
        }

        @JsonProperty("offering_ids")
        public ArrayList<String> getOfferingIds() {
            return this.offeringIds;
        }

        @JsonProperty("offering")
        public String getOffering() {
            return this.offering;
        }
    }
}


