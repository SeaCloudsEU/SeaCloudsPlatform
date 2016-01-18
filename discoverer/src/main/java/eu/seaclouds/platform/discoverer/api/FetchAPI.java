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


@Path("/fetch")
@Produces(MediaType.APPLICATION_JSON)
public class FetchAPI {
    /* vars */
    private Discoverer discoverer;

    public FetchAPI(Discoverer discoverer) {
        this.discoverer = discoverer;
    }

    @GET
    public ArrayList<String> getOfferingIds()
            throws IOException {
        /* collecting all the ids within the repository */

        ArrayList<String> ids = new ArrayList<String>();
        Collection<String> offeringIds = discoverer.getAllOfferingIds();
        for (String offeringId : offeringIds) {
            ids.add(offeringId);
        }

        return ids;
    }

    @POST
    public FetchRepresentation getOfferingById(@QueryParam("oid") String offerId)
            throws IOException {

        /* input check */
        if(!Offering.validateOfferingId(offerId)) {
            return new FetchRepresentation("", "");
        }

        /* fetching the offering */
        Offering offering = this.discoverer.fetchOffer(offerId);

        String toscaString = null;

        if (offering != null) {
            toscaString = offering.toscaString;
        }

        return new FetchRepresentation(offerId, toscaString);
    }

    private class FetchRepresentation {

        private String offeringId;
        private String offering;

        public FetchRepresentation(String offeringId, String offering) {
            this.offeringId = offeringId;
            this.offering = offering;
        }

        @JsonProperty("offering_id")
        public String getOfferingId() {
            return offeringId;
        }

        @JsonProperty("offering")
        public String getOffering() {
            return offering;
        }
    }
}


