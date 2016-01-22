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

package eu.seaclouds.platform.discoverer.core;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collection;

public class OfferingManager {

    private final MongoCollection<Document> offeringsCollection;

    public ArrayList<String> offeringNames =  new ArrayList<>();

    public OfferingManager(MongoCollection<Document> collection) {
        this.offeringsCollection = collection;
    }

    /**
     * Get the list of all offering name
     *
     * @return the list of all offering names
     */
    public Collection<String> getAllOfferingIds() {
        return offeringNames;
    }

    /**
     * Get an offering
     *
     * @param offeringName the name of the offering
     * @return the offering identified by offeringId
     */
    public Offering getOffering(String offeringName) {
        BasicDBObject query = new BasicDBObject("offering_name", offeringName);
        FindIterable<Document> cursor = this.offeringsCollection.find(query);

        return Offering.fromDB(cursor.first());
    }

    /**
     * Add a new offering in the repository
     *
     * @param offering the Offering to add
     * @return the id of the added Offering
     */
    public String addOffering(Offering offering) {
        this.offeringsCollection.insertOne(offering.toDBObject());
        this.offeringNames.add(offering.getName());
        return offering.getName();
    }

    /**
     * Remove an offering
     *
     * @param offeringName the name of the offering to remove
     * @return
     */
    public boolean removeOffering(String offeringName) {
        if(offeringName == null)
            throw new NullPointerException("The parameter \"cloudOfferingId\" cannot be null.");

        BasicDBObject query = new BasicDBObject("offering_name", offeringName);
        Document removedOffering = this.offeringsCollection.findOneAndDelete(query);

        return removedOffering != null;
    }

    /**
     * Initialize the list of offerings known by the discoverer
     *
     */
    public void initializeOfferings() {
        FindIterable<Document> offerings = this.offeringsCollection.find();

        for (Document d : offerings) {
            offeringNames.add((String) d.get("offering_name"));
        }
    }

    /**
     * Generates a single offering file containing all node templates fetched
     *
     * @param offeringNodeTemplates node templates to write on file
     */
    public void generateSingleOffering(String offeringNodeTemplates) {
        this.removeOffering("0");
        Offering singleOffering = new Offering("all");
        singleOffering.toscaString = offeringNodeTemplates;
        this.addOffering(singleOffering);
    }
}


