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

package eu.seaclouds.platform.discoverer.crawler;

import eu.seaclouds.platform.discoverer.core.Offering;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

public class PaasifyCrawler extends SCCrawler {
    /* consts */
    private final String paasifyRepositoryDirecory = System.getProperty("user.home") + "/paas-profiles";
    private final String paasifyRepositoryURL = "https://github.com/stefan-kolb/paas-profiles/";

    private JSONParser jsonParser = new JSONParser();

    private DecimalFormat slaFormat = new DecimalFormat("0.00000");

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

    public static String Name = "PaasifyCrawler";

    /**
     * Initializes a temporary directory and clones paasify repository in it
     */
    public PaasifyCrawler() {

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
    public void crawl() {

        try {
            updateLocalRepository();
            getOfferings();
        } catch (Exception e){
            e.printStackTrace();
        }
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
    private void getOfferings() {

        File offeringsDirectory = new File(paasifyRepositoryDirecory + "/profiles");

        for (File offerFile : offeringsDirectory.listFiles()) {
            try {
                if (offerFile.isFile() && isJSON(offerFile)) {
                    FileReader fr = new FileReader(offerFile);
                    JSONObject obj =(JSONObject) jsonParser.parse(fr);
                    Offering offering = getOfferingFromJSON(obj);
                    fr.close();

                    if (offering != null)
                        addOffering(offering);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * Conversion from Paasify JSON model into TOSCA PaaS model
     *
     * @param obj the decoded JSON offering
     *
     * @throws IOException
     */
    private Offering getOfferingFromJSON(JSONObject obj) throws IOException {

        JSONArray infrastructures = (JSONArray) obj.get("infrastructures");
        String name = (String) obj.get("name");
        Offering offering = null;

        if (infrastructures == null || infrastructures.size() == 0) {
            String providerName = Offering.sanitizeName(name);
            offering = this.parseOffering(providerName, providerName, obj);

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

                String providerName = Offering.sanitizeName(name);
                String offeringName = Offering.sanitizeName(fullName);

                offering = this.parseOffering(providerName, offeringName, obj);

                if (!continent.isEmpty())
                    offering.addProperty("continent", continent);

                if (!country.isEmpty())
                    offering.addProperty("country", country);
            }
        }

        return offering;
    }


    private Offering parseOffering(String providerName, String name, JSONObject obj) {
        Offering offering = new Offering(name);

        offering.setType("seaclouds.nodes.Platform." + providerName);
        offering.addProperty("resource_type", "platform");

        parseRuntimesAndMiddlewares(obj, offering);
        parseScaling(obj, offering);
        parseHosting(obj, offering);
        parseQOS(obj, offering);
        parseNativeAndAddonServices(obj, offering);

        return offering;
    }



    private void parseRuntimesAndMiddlewares(JSONObject obj, Offering offering) {
        JSONArray runtimes = (JSONArray) obj.get("runtimes");

        if(runtimes != null) {

            for (int i = 0; i < runtimes.size(); i++) {
                JSONObject runtime = (JSONObject) runtimes.get(i);
                String currentLanguage = (String) runtime.get("language");

                String runtimeSupportTag = externaltoSCTag.get(currentLanguage);

                if (runtimeSupportTag != null) {
                    offering.addProperty(runtimeSupportTag + "_support", "true");
                    parseVersion(runtimeSupportTag, runtime, offering);
                }

            }

        }
        JSONArray middlewares = (JSONArray) obj.get("middleware");

        if(middlewares != null) {

            for (int i = 0; i < middlewares.size(); i++) {
                JSONObject middleware = (JSONObject) middlewares.get(i);
                String middlewareName = (String) middleware.get("name");

                String middlewareSupportTag = externaltoSCTag.get(middlewareName);

                if (middlewareSupportTag != null) {
                    offering.addProperty(middlewareSupportTag + "_support", "true");
                    parseVersion(middlewareSupportTag, middleware, offering);
                }

            }
        }
    }


    private void parseHosting(JSONObject obj, Offering offering) {
        JSONObject hosting = (JSONObject) obj.get("hosting");

        if (hosting == null)
            return;

        Boolean publicHosting = (Boolean) hosting.get("public");
        Boolean privateHosting = (Boolean) hosting.get("private");

        offering.addProperty("public_hosting", publicHosting.toString());
        offering.addProperty("private_hosting", privateHosting.toString());
    }



    private void parseScaling(JSONObject obj, Offering offering) {
        JSONObject scaling = (JSONObject) obj.get("scaling");

        if (scaling == null)
            return;

        Boolean verticalScaling = (Boolean) scaling.get("vertical");
        Boolean horizontalScaling = (Boolean) scaling.get("horizontal");
        Boolean autoScaling = (Boolean) scaling.get("auto");

        offering.addProperty("vertical_scaling", verticalScaling.toString());
        offering.addProperty("horizontal_scaling", horizontalScaling.toString());
        offering.addProperty("auto_scaling", autoScaling.toString());
    }



    private void parseQOS(JSONObject obj, Offering offering) {
        JSONObject qos = (JSONObject) obj.get("qos");

        if (qos == null || !qos.containsKey("uptime"))
            return;

        Double value = (Double) qos.get("uptime");

        if (value != null)
            offering.addProperty("availability", this.slaFormat.format(value/100.0));
    }

    private void parseNativeAndAddonServices(JSONObject obj, Offering offering) {
        JSONObject services = (JSONObject) obj.get("services");

        if(services == null)
            return;

        JSONArray nativeServices = (JSONArray) services.get("native");
        JSONArray addonServices = (JSONArray) services.get("addon");

        if (nativeServices != null)
            parseServices(nativeServices, offering);

        if (addonServices != null)
            parseServices(addonServices, offering);
    }

    private void parseServices(JSONArray services, Offering offering) {
        for (int i = 0; i < services.size(); i++) {
            JSONObject service = (JSONObject) services.get(i);
            String serviceName = (String) service.get("name");

            String serviceSupportTag = externaltoSCTag.get(serviceName);

            if (serviceSupportTag != null && offering.getProperty(serviceSupportTag + "_support") == null) {

                offering.addProperty(serviceSupportTag + "_support", "true");
                parseVersion(serviceSupportTag, service, offering);
            }
        }
    }

    private void parseVersion(String name, JSONObject service, Offering offering) {
        JSONArray versions = (JSONArray) service.get("versions");

        if (versions != null && versions.size() > 0) {
            String version = (String) versions.get(versions.size() - 1);

            if (version.indexOf('*') == -1) {
                if (name.equals("java")) { // per java la versione da prendere non è 1.x, ma 6,7,8..
                    version = version.substring(2);
                }

                offering.addProperty(name + "_version", version);
            }
        }
    }

    private boolean isJSON(File file) {
        String fileName = file.getName();
        String fileExtension = "";

        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            fileExtension = fileName.substring(fileName.lastIndexOf(".")+1);

        return fileExtension.equals("json");
    }
}