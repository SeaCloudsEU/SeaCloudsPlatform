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

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import eu.seaclouds.platform.discoverer.crawler.CloudHarmonySPECint;
import eu.seaclouds.platform.discoverer.crawler.CrawlerManager;
import org.bson.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;


public class Discoverer {
    /* singleton */
    private ArrayList<String> offeringNodeTemplates = new ArrayList<>();
    private boolean refreshing = false;
    private ArrayList<String> activeCrawlers;

    private String databaseName = "DrACO_DB";
    private String collectionName = "offerings";

    public int totalCrawledOfferings;
    public int crawledTimes;
    public Date lastCrawl;

    public Discoverer(MongoClient mongoClient, ArrayList<String> activeCrawlers) {
        this.initializeResources();

        MongoDatabase db = mongoClient.getDatabase(this.databaseName);
        MongoCollection<Document> coll = db.getCollection(this.collectionName);

        this.offeringManager = new OfferingManager(coll);
        this.offeringManager.initializeOfferings();

        this.activeCrawlers = activeCrawlers;
    }

    public Discoverer(MongoClient mongoClient) {
        this(mongoClient, new ArrayList<String>());
    }

    /* vars */
    public OfferingManager offeringManager;

    public synchronized void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
    }

    public synchronized boolean getRefreshing() {
        return refreshing;
    }

    /**
     * Reads an offering from the local repository.
     * @param cloudOfferingId The ID of the offering to read.
     * @return The <code>Offering</code> object instance for the fetchOffer'ed ID.
     */
    public Offering fetchOffer(String cloudOfferingId) {
        if (cloudOfferingId == null)
            return null;

        return offeringManager.getOffering(cloudOfferingId);
    }

    /**
     * Inserts or eventually updates an offering in the local repository
     *
     * @param newOffering The <code>Offering</code> object instance representing the new offering to insert.
     * @return the ID assigned to the newly-inserted offering.
     */
    public String addOffering(Offering newOffering) {
        if (newOffering == null)
            return null;

        String offeringName = newOffering.getName();

        /* if the offering was already present in the repository it is removed */
        if (offeringManager.getOffering(offeringName) != null) {
            offeringManager.removeOffering(offeringName);
        }

        offeringManager.addOffering(newOffering);

        /* updates the list of all node templates */
        this.offeringNodeTemplates.add(newOffering.getNodeTemplate());
        totalCrawledOfferings++;

        return offeringName;
    }

    /**
     * Removes an offering from the repository.
     * @param cloudOfferingId The ID of the offering to remove.
     * @return <code>true</code> in case of successful removal;
     * <code>false</code> otherwise.
     */
    public boolean removeOffering(String cloudOfferingId) {
        return Offering.validateOfferingId(cloudOfferingId) && offeringManager.removeOffering(cloudOfferingId);
    }

    /**
     * Gets an iterator for the content of the offering repository.
     * @return Iterator of cloud offering IDs that can be used as argument of
     * <code>fetchOffer</code> method.
     */
    public Collection<String> getAllOfferingIds() {
        return offeringManager.getAllOfferingIds();
    }

    public void generateSingleOffering() {
        String nodeTemplates = Offering.getPreamble() + "\n";

        for (String nodeTemplate : offeringNodeTemplates) {
            nodeTemplates += nodeTemplate + "\n";
        }

        this.offeringManager.generateSingleOffering(nodeTemplates);
        this.offeringNodeTemplates.clear();
    }

    public void refreshRepository() {
        if (this.getRefreshing() == false) {
            this.setRefreshing(true);
            CrawlerManager cm = new CrawlerManager(this, this.activeCrawlers);
            new Thread(cm).start();
        }
    }

    private void initializeResources() {
        /* Location map */
        InputStream locationMap = this.getClass().getClassLoader().getResourceAsStream("location_mapping");
        if (locationMap != null)
            LocationMapping.initializeMap(locationMap);

        /* SPECint map */
        InputStream SPECintMap = this.getClass().getClassLoader().getResourceAsStream("SPECint_mapping");
        if (SPECintMap != null)
            CloudHarmonySPECint.initializeMap(SPECintMap);
    }
}

