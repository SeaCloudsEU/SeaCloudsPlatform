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

package eu.seaclouds.platform.discoverer.ws;

import alien4cloud.tosca.parser.ParsingException;
import eu.seaclouds.platform.discoverer.core.Offering;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class PaasifySpider extends SCSpider {
    /* consts */
    private final String paasifyRepositoryDirecory = System.getProperty("user.home") + "paas-profiles";
    private final String paasifyRepositoryURL = "https://github.com/stefan-kolb/paas-profiles/";

    private JSONParser jsonParser = new JSONParser();
    private static HashMap<String, String> paasifyToSeaclouds;

    static {
        HashMap<String, String> initializedMap = new HashMap<>();

        /* Initialized map from paasify to Seaclouds keywords */
        initializedMap.put("java", "java_support");
        initializedMap.put("go", "go_support");
        initializedMap.put("node", "node_support");
        initializedMap.put("php", "php_support");
        initializedMap.put("python", "python_support");
        initializedMap.put("ruby", "ruby_support");

        paasifyToSeaclouds = initializedMap;
    }

    private static HashMap<String, String> continentFullName;

    static {
        HashMap<String, String> initializedMap = new HashMap<>();

        initializedMap.put("AF", "Africa");
        initializedMap.put("AS", "Asia");
        initializedMap.put("EU", "Europe");
        initializedMap.put("NA", "America");
        initializedMap.put("SA", "America");
        initializedMap.put("OC", "Oceania");

        continentFullName = initializedMap;
    }



    /**
     * Initializes a temporary directory and clones paasify repository in it
     */
    public PaasifySpider() {

        File tempDirectory = new File(paasifyRepositoryDirecory);

        /** Create paasify-offerings directory into os temp directory if it has not been created yet */
        if (!tempDirectory.exists()) {
            tempDirectory.mkdir();

            try {
                Git.cloneRepository()
                        .setURI(paasifyRepositoryURL)
                        .setDirectory(tempDirectory)
                        .call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * Synchronizes with paasifiy repository and get updated offerings
     *
     * @return the array of offerings that have been successfully converted into SeaClouds offerings
     */
    public CrawlingResult[] crawl() {

        CrawlingResult[] offerings = null;

        try {
            updateLocalRepository();
            offerings = getOfferings();
        } catch (Exception e){
            e.printStackTrace();
        }

        return offerings;
    }



    /**
     * Updates local Paasify repository
     *
     * @throws GitAPIException
     */
    private void updateLocalRepository() throws GitAPIException {

        try {
            Git.open(new File(paasifyRepositoryDirecory)).pull().call();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     *
     * @return an array of offerings successfully translated into SeaClouds offering format
     */
    private CrawlingResult[] getOfferings() {

        File offeringsDirectory = new File(paasifyRepositoryDirecory + "/profiles");
        ArrayList<CrawlingResult> offers = new ArrayList<>();

        for (File offerFile : offeringsDirectory.listFiles()) {
            try {
                if (offerFile.isFile() && isJSON(offerFile)) {
                    JSONObject obj =(JSONObject) jsonParser.parse(new FileReader(offerFile));
                    convertToTOSCA(obj, offers);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return offers.toArray(new CrawlingResult[offers.size()]);
    }



    /**
     * Conversion from Paasify JSON model into TOSCA PaaS model
     *
     * @param obj the decoded JSON offering
     * @param offers the array containing already decoded offerings
     *
     * @throws ParsingException
     * @throws IOException
     */
    private void convertToTOSCA(JSONObject obj, ArrayList<CrawlingResult> offers) throws ParsingException, IOException {

        JSONArray infrastructures = (JSONArray) obj.get("infrastructures");
        String name = (String) obj.get("name");

        if (infrastructures == null || infrastructures.size() == 0) {
            String offeringName = Offering.sanitizeName(name);
            ArrayList<String> generatedTOSCA = parseOffering(offeringName, obj);
            CrawlingResult crawlingResult = getCrawlingResult(super.join("\n", generatedTOSCA), obj);

            offers.add(crawlingResult);
        } else {
            for (Object element: infrastructures) {
                JSONObject infrastructure = (JSONObject) element;
                String continent = "";
                String country = "";
                String fullName = name;

                if (infrastructure.containsKey("continent")) {
                    continent = infrastructure.get("continent").toString();
                    if (!continent.isEmpty()) {
                        continent = continentFullName.get(continent);
                        fullName = fullName + "." + continent;
                    }
                }

                if (infrastructure.containsKey("country")) {
                    country = infrastructure.get("country").toString();
                    if (!country.isEmpty())
                        fullName = fullName + "." + country;
                }

                String offeringName = Offering.sanitizeName(fullName);
                ArrayList<String> generatedTOSCA = parseOffering(offeringName, obj);

                if (!continent.isEmpty())
                    generatedTOSCA.add("      continent: " + continent);

                if (!country.isEmpty())
                    generatedTOSCA.add("      country: " + country);

                CrawlingResult crawlingResult = getCrawlingResult(super.join("\n", generatedTOSCA), obj);
                offers.add(crawlingResult);
            }
        }
    }



    private CrawlingResult getCrawlingResult(String offering, JSONObject obj) throws ParsingException, IOException {
        Offering off = Offering.fromTosca(offering);
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        Date lastRevision;

        try {
            lastRevision = df.parse((String) obj.get("revision"));
        } catch (Exception e) {
            e.printStackTrace();
            lastRevision = Calendar.getInstance().getTime();
        }

        return new CrawlingResult(lastRevision, off);
    }



    private ArrayList<String> parseOffering(String name, JSONObject obj) {
        ArrayList<String> generatedTOSCA = new ArrayList<>();

        generatedTOSCA.add("tosca_definitions_version: tosca_simple_yaml_1_0_0_wd03");
        generatedTOSCA.add("imports:");
        generatedTOSCA.add("  - tosca-normative-types:1.0.0.wd03-SNAPSHOT");
        generatedTOSCA.add("topology_template:");
        generatedTOSCA.add(" node_templates:");
        generatedTOSCA.add(String.format("  %s:", name));
        generatedTOSCA.add(String.format("    type: seaclouds.Nodes.Platform.%s", name));
        generatedTOSCA.add("    properties:");

        parseRuntimes(obj, generatedTOSCA);
        parseScaling(obj, generatedTOSCA);
        parseHosting(obj, generatedTOSCA);
        parseQOS(obj, generatedTOSCA);

        return generatedTOSCA;
    }



    private void parseRuntimes(JSONObject obj, ArrayList<String> generatedTOSCA) {
        JSONArray runtimes = (JSONArray) obj.get("runtimes");

        if(runtimes == null)
            return;

        for (int i = 0; i < runtimes.size(); i++) {
            JSONObject runtime = (JSONObject) runtimes.get(i);
            String currentLanguage = (String) runtime.get("language");

            String runtimeSupportTag = paasifyToSeaclouds.get(currentLanguage);

            if (runtimeSupportTag != null)
                generatedTOSCA.add(String.format("      %s: true", runtimeSupportTag));
        }
    }



    private void parseHosting(JSONObject obj, ArrayList<String> generatedTOSCA) {
        JSONObject hosting = (JSONObject) obj.get("hosting");

        if (hosting == null)
            return;

        Boolean publicHosting = (Boolean) hosting.get("public");
        Boolean privateHosting = (Boolean) hosting.get("private");

        generatedTOSCA.add(String.format("      public_hosting: %b", publicHosting));
        generatedTOSCA.add(String.format("      private_hosting: %b", privateHosting));
    }



    private void parseScaling(JSONObject obj, ArrayList<String> generatedTOSCA) {
        JSONObject scaling = (JSONObject) obj.get("scaling");

        if (scaling == null)
            return;

        Boolean verticalScaling = (Boolean) scaling.get("vertical");
        Boolean horizontalScaling = (Boolean) scaling.get("horizontal");
        Boolean autoScaling = (Boolean) scaling.get("auto");

        generatedTOSCA.add(String.format("      vertical_scaling: %b", verticalScaling));
        generatedTOSCA.add(String.format("      horizontal_scaling: %b", horizontalScaling));
        generatedTOSCA.add(String.format("      auto_scaling: %b", autoScaling));
    }



    private void parseQOS(JSONObject obj, ArrayList<String> generatedTOSCA) {
        JSONObject qos = (JSONObject) obj.get("qos");

        if (qos == null || !qos.containsKey("uptime"))
            return;

        Double value = (Double) qos.get("uptime");

        if (value != null)
            generatedTOSCA.add(String.format("      uptime: %.2f", value.doubleValue()));
    }



    private boolean isJSON(File file) {
        String fileName = file.getName();
        String fileExtension = "";

        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            fileExtension = fileName.substring(fileName.lastIndexOf(".")+1);

        return fileExtension.equals("json");
    }
}