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

import org.bson.Document;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Offering {
    Date lastCrawl;
    String offeringName;
    public String toscaString = null;

    private String type;
    private HashMap<String, String> properties = new HashMap<>();


    public Offering(String offeringName) {
        this.offeringName = offeringName;
        this.lastCrawl = Calendar.getInstance().getTime();
    }

    private Offering (String offeringName, Date lastCrawl) {
        this(offeringName);
        this.lastCrawl = lastCrawl;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Used to add a new property to the offering
     *
     * @param propertyName the name of the property
     * @param property the value of the property
     * @return true if the property was already present (and it has been replaced), false otherwise
     */
    public boolean addProperty(String propertyName, String property) {
        boolean ret = this.properties.containsKey(propertyName);
        this.properties.put(propertyName, property);
        return ret;
    }

    public String getProperty(String propertyName) {
        return this.properties.get(propertyName);
    }


    /**
     * It performs the validation of any offering ID passed as input.
     * @param cloudOfferingId The offering ID to validate.
     * @return <code>true</code> if <code>cloudOfferingId</code> is valid,
     * <code>false</code> otherwise.
     */
    public static boolean validateOfferingId(String cloudOfferingId) {
        /* input checks */
        if(cloudOfferingId == null || cloudOfferingId.length() == 0)
            return false;

        return true;
    }

    /**
     * Retrieves the name of the offering.
     * @return The name of the offering.
     */
    public String getName() {
        return this.offeringName;
    }

    public String toTosca() {
        if (this.toscaString == null) {
            this.toscaString = getPreamble() + this.getNodeTemplate();
        }

        return this.toscaString;
    }

    public String getNodeTemplate() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("    %s:\n", this.offeringName));
        sb.append(String.format("      type: %s\n", this.type));
        sb.append("      properties:\n");

        this.sanitizeLocation();

        for (String key: this.properties.keySet()) {
            sb.append(String.format("        %s: %s\n", key, this.properties.get(key)));
        }

        return sb.toString();
    }

    private void sanitizeLocation() {
        String sanitizedLocation = LocationMapping.getLocation(this.getName());

        if (sanitizedLocation != null) {
            this.addProperty("location", sanitizedLocation);
        }
    }

    public static String getPreamble() {
        StringBuilder sb = new StringBuilder();

        sb.append("tosca_definitions_version: tosca_simple_yaml_1_0_0_wd03\n");
        sb.append("description:\n");
        sb.append("template_name:\n");
        sb.append("template_version: 1.0.0-SNAPSHOT\n");
        sb.append("imports:\n");
        sb.append("  - tosca-normative-types:1.0.0.wd03-SNAPSHOT\n");
        sb.append("\n");
        sb.append("topology_template:\n");
        sb.append("  node_templates:\n");

        return sb.toString();
    }

    /**
     * Sanitizes a name by replacing every not alphanumeric character with '_'
     *
     * @param name the name of the offerings
     * @return the sanitized name
     */
    public static String sanitizeName(String name) {

        if (name  == null)
            throw new NullPointerException("Parameter name cannot be null");

        name = name.trim();

        if (name.length() == 0)
            throw new IllegalArgumentException("Parameter name cannot be empty");

        StringBuilder ret = new StringBuilder("");

        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (Character.isLetter(ch) || Character.isDigit(ch)) {
                ret.append(ch);
            } else {
                ret.append("_");
            }

        }

        return ret.toString();
    }

    public static Offering fromDB(Document dbOjb) {
        if (dbOjb == null)
            return null;

        String offeringName = (String) dbOjb.get("offering_name");
        String type = (String) dbOjb.get("type");

        String lastCrawlMilliseconds = (String) dbOjb.get("last_crawl");
        Date lastCrawl = new Date(Long.parseLong(lastCrawlMilliseconds));
        Offering offering = new Offering(offeringName, lastCrawl);
        offering.setType(type);

        offering.toscaString = (String) dbOjb.get("tosca_string");
        return offering;
    }

    public Document toDBObject() {
        Document document = new Document();

        document.put("offering_name", this.offeringName);
        document.put("type", this.type);
        document.put("last_crawl", Long.toString(this.lastCrawl.getTime()));
        document.put("tosca_string", this.toTosca());

        return document;
    }
}
