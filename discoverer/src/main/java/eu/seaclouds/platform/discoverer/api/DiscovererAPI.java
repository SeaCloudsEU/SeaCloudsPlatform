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
import java.util.Date;
import java.util.Set;

@Path("/")
public class DiscovererAPI {

    private final Discoverer discoverer;
    private org.json.simple.parser.JSONParser JSONParser;
    static Logger log = LoggerFactory.getLogger(DiscovererAPI.class);

    public DiscovererAPI(Discoverer discoverer) {
        this.discoverer = discoverer;
        this.JSONParser = new JSONParser();
    }

    @POST
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
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

    @GET
    @Path("/fetch_all")
    @Produces(MediaType.APPLICATION_JSON)
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

    @GET
    @Path("/fetch")
    @Produces(MediaType.APPLICATION_JSON)
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
    @Path("/fetch")
    @Produces(MediaType.APPLICATION_JSON)
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

    @GET
    @Path("/fetchif")
    @Produces(MediaType.APPLICATION_JSON)
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

    @GET
    @Path("/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean refreshRepository() {
        this.discoverer.refreshRepository();
        return true;
    }

    @GET
    @Path("/statistics")
    @Produces(MediaType.APPLICATION_JSON)
    public StatisticsRepresentation getStatistics() {
        int crawledTimes = discoverer.crawledTimes;
        int totalCrawledOfferings = discoverer.totalCrawledOfferings;
        Date lastCrawl = discoverer.lastCrawl;

        return new StatisticsRepresentation(crawledTimes, totalCrawledOfferings, lastCrawl);
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

    private class StatisticsRepresentation {

        private int crawledTimes;
        private int totalCrawledOfferings;
        private Date lastCrawl;

        public StatisticsRepresentation(int crawledTimes, int totalCrawledOfferings, Date lastCrawl) {
            this.crawledTimes = crawledTimes;
            this.totalCrawledOfferings = totalCrawledOfferings;
            this.lastCrawl = lastCrawl;
        }

        @JsonProperty("crawled_times")
        public int getCrawledTimes() {
            return crawledTimes;
        }

        @JsonProperty("total_crawled_offerings")
        public int getTotalCrawledOfferings() {
            return totalCrawledOfferings;
        }

        @JsonProperty("last_crawl")
        public Date getLastCrawl() {
            return lastCrawl;
        }
    }
}