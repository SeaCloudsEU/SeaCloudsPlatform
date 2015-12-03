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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Offering {
    Date lastCrawl;
    String offeringName;
    String offeringId = null;

    private String type;
    private HashMap<String, String> properties = new HashMap<>();

    private String toscaString = null;
    private String offeringPath = null;

    private static JSONParser parser = new JSONParser();


    public Offering(String offeringName) {
        this.offeringName = offeringName;
        this.lastCrawl = Calendar.getInstance().getTime();
    }

    private Offering (String offeringName, String offeringId, Date lastCrawl) {
        this(offeringName);
        this.offeringId = offeringId;
        this.lastCrawl = lastCrawl;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Used to set the path of the offering file (containing TOSCA)
     *
     * @param offeringPath the path of the offering file
     */
    public void setOfferingPath(String offeringPath) {
        this.offeringPath = offeringPath;
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
        if(cloudOfferingId == null)
            return false;
        if( cloudOfferingId.trim().length() != cloudOfferingId.length() )
            return false;
        if( cloudOfferingId.length() == 0 )
            return false;

        /* input check: we do NOT allow dots, slashes and backslashes */
        int n = cloudOfferingId.length();
        for(int i=0; i<n; i++) {
            char ch = cloudOfferingId.charAt(i);
            if( ch == '.' || ch == '/' || ch == '\\' )
                return false;
        }

        /* all good */
        return true;
    }

    /**
     * Gets the ID of the Offering.
     * @return The ID of the Offering.
     */
    public String getId() {
        if(this.offeringId == null)
            throw new NullPointerException("The offering has not been assigned any ID. "
                    + "See Offering.setId(String).");
        return this.offeringId;
    }

    /**
     * Assigns a unique ID to the Offering.
     * @param uniqueId The ID to assign.
     * @return <code>true</code> if the assignment is successful,
     * <code>false</code> otherwise.
     */
    public boolean setId(String uniqueId) {
        /* input check */
        boolean validId = validateOfferingId(uniqueId);
        if( !validId )
            return false;

        /* assignment */
        this.offeringId = uniqueId;
        return true;
    }

    /**
     * Retrieves the name of the offering.
     * @return The name of the offering.
     */
    public String getName() {
        return this.offeringName;
    }

    public boolean moreRecent(Offering offering) {
        Date oldDate = offering.lastCrawl;
        Date myDate = this.lastCrawl;

        return myDate.compareTo(oldDate) > 0;
    }

    public String toJSON() {
        JSONObject obj = new JSONObject();

        obj.put("offering_id", this.offeringId);
        obj.put("offering_name", this.offeringName);
        obj.put("type", this.type);
        obj.put("last_crawl", this.lastCrawl.getTime());
        obj.put("offering_path", this.offeringPath);

        return obj.toJSONString();
    }

    public String toTosca() {
        /* if the TOSCA string is already present it is returned */
        if (this.toscaString != null)
            return this.toscaString;

        /* otherwise, if the path of the file is valid, then it is possible to read the offering from file */
        if (this.offeringPath != null) {
            try {
                String offeringDirectory = Discoverer.instance().offeringManager.getOfferingDirectory().getAbsolutePath();
                String offeringFileName = "offer_" + this.offeringId + ".yaml";
                this.toscaString = new String(Files.readAllBytes(Paths.get(offeringDirectory + File.separatorChar + offeringFileName)));
                return this.toscaString;
            } catch (IOException e) { }
        }
        /* otherwise this is a new offering, crawled, but neved stored in the local repository */
        this.toscaString = Offering.getPreamble() + this.getNodeTemplate();
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

    public static Offering fromJSON(String json) {
        JSONObject obj;

        try {
            obj = (JSONObject) Offering.parser.parse(json);
        } catch (ParseException e) {
            return null;
        }

        String offeringId = (String) obj.get("offering_id");
        String offeringName = (String) obj.get("offering_name");
        String type = (String) obj.get("type");

        Long lastCrawlMilliseconds = (Long) obj.get("last_crawl");

        Date lastCrawl = new Date(lastCrawlMilliseconds);

        String offeringPath = (String) obj.get("offering_path");

        Offering offering = new Offering(offeringName, offeringId, lastCrawl);
        offering.setType(type);
        offering.setOfferingPath(offeringPath);

        return offering;
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
}
