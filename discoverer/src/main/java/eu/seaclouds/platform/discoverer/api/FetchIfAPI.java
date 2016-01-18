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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;


@Path("/fetchif")
@Produces(MediaType.APPLICATION_JSON)
public class FetchIfAPI {
    /* vars */
    private Discoverer discoverer;
    private JSONParser JSONParser;
    static Logger log = LoggerFactory.getLogger(FetchIfAPI.class);

    public FetchIfAPI(Discoverer discoverer) {
        this.discoverer = discoverer;
        this.JSONParser = new JSONParser();
    }

    @POST
    public FetchIfRepresentation getOfferingsIf(@QueryParam("constraints") String constraints)
            throws IOException {

        ArrayList<String> validOfferingIds = new ArrayList<>();
        ArrayList<String> validOfferings = new ArrayList<>();
        JSONObject constraintsObject;

        try {
            constraintsObject = (JSONObject) JSONParser.parse(constraints);
            Collection<String> offeringIds = discoverer.getAllOfferingIds();

            for (String offeringId : offeringIds) {
                Offering offering = this.discoverer.fetchOffer(offeringId);
                if (this.satisfyAllConstraints(constraintsObject, offering)) {
                    validOfferingIds.add(offeringId);
                    validOfferings.add(offering.toTosca());
                }
            }
        } catch (ParseException e) {
            log.error("Cannot parse constraints");
            log.error(e.getMessage());
        }

        return new FetchIfRepresentation(validOfferingIds, validOfferings);
    }

    private boolean satisfyAllConstraints(JSONObject constraintsObject, Offering offering) {
        String toscaString = offering.toTosca();
        Set keys = constraintsObject.keySet();

        for (Object key : keys) {
            String constraintName = (String) key;
            String constraintValue = (String) constraintsObject.get(key);

            if (!satisfyConstraint(constraintName, constraintValue, toscaString))
                return false;
        }

        return true;
    }

    private boolean satisfyConstraint(String constraintName, String constraintValue, String toscaString) {
        int i = toscaString.indexOf(constraintName);

        if (i == -1) {
            return false;
        }

        i += constraintName.length();
        while (toscaString.charAt(i++) != ':');
        if (toscaString.charAt(i) == ' ') i++;

        return toscaString.substring(i).startsWith(constraintValue);
    }

    public class FetchIfRepresentation {

        private ArrayList<String> offeringIds;
        private ArrayList<String> offerings;

        public FetchIfRepresentation(ArrayList<String> offeringIds, ArrayList<String> offerings) {
            this.offeringIds = offeringIds;
            this.offerings = offerings;
        }

        @JsonProperty("offering_ids")
        public ArrayList<String> getOfferingIds() {
            return offeringIds;
        }

        @JsonProperty("offerings")
        public ArrayList<String> getOfferings() {
            return offerings;
        }
    }
}


