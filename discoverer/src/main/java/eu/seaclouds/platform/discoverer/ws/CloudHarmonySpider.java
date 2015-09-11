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

/* JSON parser */
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/* seaclouds */
import alien4cloud.tosca.parser.ParsingException;
import eu.seaclouds.platform.discoverer.core.Offering;

/* rest client */
import java.util.*;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


/* cloud type enumeration */
enum CloudTypes {
    IAAS,
    PAAS
}


public class CloudHarmonySpider extends SCSpider {
    /* consts */
    private static final String API_KEY = "3y5A9MYhtEPFaq16CLdUvfH7TgK0zjc2";

    /* vars */
    private JSONParser jsonParser;
    private Hashtable<String, String> booleans;
    private Hashtable<String, String> numbers;
    private Hashtable<String, String> strings;
    CloseableHttpClient httpclient;


    /* c.tor */
    public CloudHarmonySpider() {
        /* json parser */
        this.jsonParser = new JSONParser();

        /* client init */
        this.httpclient = HttpClients.createDefault();

        /* mapping booleans */
        this.booleans = new Hashtable<String, String>();
        this.booleans.put("autoScaling", "auto_scaling");
        this.booleans.put("selfHostable", "self_hostable");
        this.booleans.put("apiRestricted", "api_restricted");
        this.booleans.put("autoFailover", "auto_failover");
        this.booleans.put("processPricing", "process_pricing");
        this.booleans.put("vmBased", "vm_based");

        /* mapping numbers */
        this.numbers = new Hashtable<String, String>();
        this.numbers.put("cpuCores", "num_cpus");
        this.numbers.put("localDisks", "num_disks");
        this.numbers.put("memory", "ram");
        this.numbers.put("localStorage", "local_storage");

        /* mapping strings */
        this.strings = new Hashtable<String, String>();
        this.strings.put("localDiskType", "disk_type");
    }


    // Method not active:
    // Determine proper tosca metrics for cloudharmony bandwidth pricing
    //
    // private JSONArray getBandwidthPricing(String serviceId) {
    //     /* computing the query string */
    //     String getBandwidthPricingQuery = "https://cloudharmony.com/api/pricing/bandwidth/"
    //             + serviceId + "?"
    //             + "api-key=" + API_KEY + "&";
    //     return (JSONArray) query(getBandwidthPricingQuery);
    // }



    private JSONArray iaas_getServiceFeatures(String serviceId) {
        /* computing the query string */
        String getServiceFeaturesQuery = "https://cloudharmony.com/api/properties/compute/"
                + serviceId + "?"
                + "api-key=" + API_KEY + "&";
        return (JSONArray) query(getServiceFeaturesQuery);
    }



    private JSONObject paas_getServiceFeatures(String serviceId) {
        /* computing the query string */
        String getServiceFeaturesQuery = "https://cloudharmony.com/api/properties/paas/"
                + serviceId + "?"
                + "api-key=" + API_KEY + "&";
        return (JSONObject) query(getServiceFeaturesQuery);
    }



    private Object query(String query) {
        HttpGet httpGet = new HttpGet(query);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = new Scanner(entity.getContent()).useDelimiter("\\Z").next();
            return jsonParser.parse(content);
        } catch (ParseException | NullPointerException | IOException | NoSuchElementException e) {
            // TODO: investigate why it raises NoSuchElementException, sometimes.
            //       It looks like a CloudHarmony bug.
            return null;
        }
    }



    private JSONObject getComputeInstanceType(String serviceId, String instanceType) {
        /* query string */
        String queryStr = "https://cloudharmony.com/api/compute/"
                + serviceId + "/"
                + instanceType + "?"
                + "api-key=" + API_KEY + "&";
        return (JSONObject) query(queryStr);
    }



    private CloudHarmonyService getService(String serviceId, CloudTypes t) {
        /* computing the query string */
        String getServiceQuery = "https://cloudharmony.com/api/service/"
                + serviceId + "?"
                + "api-key=" + API_KEY + "&";
        JSONObject service = null;
        try { service = (JSONObject) query(getServiceQuery); }
        catch(Exception ex) {
            // 'no content' possible. dunno why
        }

        if(service == null)
            return null;

        /* name */
        String name = "unknown_name";
        if(service.containsKey("name"))
            name = Offering.sanitizeName((String) service.get("name"));

        /* sla */
        Object sla = "-1";
        if (service.containsKey("sla"))
            sla = (service.get("sla"));

        /* locations */
        JSONArray locations = null;
        if (service.containsKey("regions"))
            locations = (JSONArray) service.get("regions");

        // Method not active:
        // Determine proper tosca metrics for cloudharmony bandwidth pricing
        //
        // /* bandwidth pricing */
        // JSONArray bandwidthPricing = null;
        // boolean hasBandwidthPricing = false;
        // if( service.containsKey("hasBandwidthPricing") ) {
        //     Boolean bb = (Boolean) service.get("hasBandwidthPricing");
        //     hasBandwidthPricing = bb.booleanValue();
        // }
        //
        // if (hasBandwidthPricing)
        //     bandwidthPricing = getBandwidthPricing(serviceId);

        /* service features */
        Object serviceFeatures;
        ArrayList<JSONObject> computeInstanceTypes = null;
        if (t == CloudTypes.IAAS) {
            serviceFeatures = iaas_getServiceFeatures(serviceId); /* in the service features we can find
                                                                   * all the instance types to use to retrieve
                                                                   * the compute instance types.
                                                                   */
            if( serviceFeatures != null && ((JSONArray)(serviceFeatures)).size() != 0 ) {
                /* retrieving instance types */
                JSONObject oneFeature = (JSONObject) ((JSONArray) (serviceFeatures)).get(0);
                JSONArray instanceTypes = (JSONArray) oneFeature.get("instanceTypes");
                Iterator<String> it = instanceTypes.iterator();

                /* querying each instance type */
                computeInstanceTypes = new ArrayList<JSONObject>();
                while (it.hasNext()) {
                    String instanceType = it.next();
                    JSONObject cit = getComputeInstanceType(serviceId, instanceType);
                    computeInstanceTypes.add(cit);
                }
            }
        } else { // t == CloudTypes.PAAS
            serviceFeatures = paas_getServiceFeatures(serviceId);
        }

        /* returning everything */
        return new CloudHarmonyService(t, // cloud type
                name,                     // cloud name
                sla,                      // availability
                locations,                // geographics
                null,                     // bandwidth pricing (unused)
                serviceFeatures,          // cloudharmony service features
                computeInstanceTypes);    // compute instance types
    }



    private void generateOfferings(CloudHarmonyService chs, ArrayList<CrawlingResult> collected) {
        if(chs == null || chs.computeInstanceTypes == null || chs.locations == null)
            return;

        /* for each location... */
        Iterator<JSONObject> locations = chs.locations.iterator();
        while (locations.hasNext()) {
            JSONObject location_i = locations.next();
            if(chs.cloudType == CloudTypes.IAAS) {
                /* ...and for each compute instance type */
                for (JSONObject cit_i : chs.computeInstanceTypes) {
                    /* generate the offering */
                    String generatedTosca = iaas_generateTosca(chs, location_i, cit_i);
                    Offering newOffer = null;
                    try { newOffer = Offering.fromTosca(generatedTosca); }
                    catch (IOException | ParsingException pex) { pex.printStackTrace(); }

                    /* collect */
                    if (newOffer != null)
                        collected.add(new CrawlingResult(newOffer));
                }
            } else { // chs.cloudType == CloudTypes.PAAS
                /* generate the offering */
                String generatedTosca = paas_generateTosca(chs, location_i);
                Offering newOffer = null;
                try { newOffer = Offering.fromTosca(generatedTosca); }
                catch(IOException | ParsingException ex) { ex.printStackTrace(); }

                /* collect */
                if(newOffer != null)
                    collected.add(new CrawlingResult(newOffer));
            }
        }
    }



    private String expandCountryCode(String cc) {
        Locale l = new Locale("", cc);
        return l.getDisplayCountry();
    }



    private String iaas_generateTosca(CloudHarmonyService chs, JSONObject location,
            JSONObject computeInstanceType) {
        /* tosca lines into ArrayList */
        ArrayList<String> gt = new ArrayList<>();

        /* name */
        String name = Offering.sanitizeName(chs.name + "_" + location.get("city"));

        /* header */
        gt.add("tosca_definitions_version: tosca_simple_yaml_1_0_0_wd03");
        gt.add("imports:");
        gt.add("  - tosca-normative-types:1.0.0.wd03-SNAPSHOT");
        gt.add("topology_template:");
        gt.add(" node_templates:");
        gt.add(String.format("  %s:", name));
        gt.add(String.format("    type: seaclouds.Nodes.Compute.%s", name));
        gt.add("    properties:");

        /* sla */
        if( chs.sla.equals("-1") == false )
            gt.add("        availability: " + chs.sla.toString());

        /* location */
        gt.add("        country: " + expandCountryCode((String) (location.get("country"))));
        gt.add("        city: " + expandCountryCode((String) (location.get("city"))));

        /* compute instance type - numbers */
        for(String k : this.numbers.keySet()) {
            if( computeInstanceType.containsKey(k) ) {
                Object vv = computeInstanceType.get(k);
                gt.add("        " + (String)(this.numbers.get(k)) + ": " + vv.toString());
            }
        }

        /* compute instance type - strings */
        if( computeInstanceType != null ) {
            for (String k : this.strings.keySet()) {
                if (computeInstanceType.containsKey(k)) {
                    String v = (String) (computeInstanceType.get(k));
                    gt.add("        " + (String) (this.strings.get(k)) + ": " + v);
                }
            }
        }

        /* building the tosca */
        return super.join("\n", gt);
    }



    private String paas_generateTosca(CloudHarmonyService chs, JSONObject location) {
        /* tosca lines into ArrayList */
        ArrayList<String> generatedTOSCA = new ArrayList<>();

        /* name */
        String name = Offering.sanitizeName(chs.name + "_" + location.get("city"));

        /* header */
        generatedTOSCA.add("tosca_definitions_version: tosca_simple_yaml_1_0_0_wd03");
        generatedTOSCA.add("imports:");
        generatedTOSCA.add("  - tosca-normative-types:1.0.0.wd03-SNAPSHOT");
        generatedTOSCA.add("topology_template:");
        generatedTOSCA.add(" node_templates:");
        generatedTOSCA.add(String.format("  %s:", name));
        generatedTOSCA.add(String.format("    type: seaclouds.Nodes.Platform.%s", name));
        generatedTOSCA.add("    properties:");

        /* sla */
        if( chs.sla.equals("-1") == false )
            generatedTOSCA.add("        availability: " + chs.sla.toString());

        /* location */
        generatedTOSCA.add("        country: " + expandCountryCode((String) (location.get("country"))));
        generatedTOSCA.add("        city: " + expandCountryCode((String)(location.get("city"))));

        /* features */
        JSONObject feat = (JSONObject) chs.serviceFeatures;

        /* booleans */
        for(String k : this.booleans.keySet()) {
            if( feat.containsKey(k) ) {
                boolean v = Boolean.parseBoolean((String)(feat.get(k)));
                generatedTOSCA.add("        " + (String)(this.booleans.get(k)) + ": " + (v ? "true" : "false"));
            }
        }

        /* supported databases and languages */
        supports("supportedDatabases", feat, generatedTOSCA);
        supports("supportedLanguages", feat, generatedTOSCA);

        /* building the tosca */
        return super.join("\n", generatedTOSCA);
    }



    private void supports(String keyName, JSONObject feat, ArrayList<String> generatedTOSCA) {
        if( feat.containsKey(keyName) == false )
            return;

        JSONArray dbs = (JSONArray) feat.get(keyName);
        for(int i=0; i<dbs.size(); i++) {
            String dbname = (String) dbs.get(i);
            generatedTOSCA.add("        " + dbname + "_support: true");
        }
    }



    @Override
    public CrawlingResult[] crawl() {
        /* result set */
        ArrayList<CrawlingResult> resultSet = new ArrayList<CrawlingResult>();

        /* iaas section */
        String computeQuery = "https://cloudharmony.com/api/services?"
                + "api-key=" + API_KEY + "&"
                + "serviceTypes=compute";
        JSONObject resp = (JSONObject) query(computeQuery);
        JSONArray computes = (JSONArray) resp.get("ids");

        Iterator<String> it = computes.iterator();
        while (it.hasNext()) {
            try {
                String serviceId = it.next();
                CloudHarmonyService chService = getService(serviceId, CloudTypes.IAAS);
                if (chService != null)
                    generateOfferings(chService, resultSet);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }

        /* paas section */
        String paasQuery = "https://cloudharmony.com/api/services?"
                + "api-key=" + API_KEY + "&"
                + "serviceTypes=paas";
        resp = (JSONObject) query(paasQuery);
        JSONArray paases = (JSONArray) resp.get("ids");
        it = paases.iterator();
        while (it.hasNext()) {
            try {
                String serviceId = it.next();
                CloudHarmonyService chService = getService(serviceId, CloudTypes.PAAS);
                generateOfferings(chService, resultSet);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        /* finalization */
        CrawlingResult[] ret = new CrawlingResult[resultSet.size()];
        for(int i=0; i<resultSet.size(); i++)
            ret[i] = resultSet.get(i);

        return ret;
    }
}



class CloudHarmonyService {
    /* members */
    public CloudTypes cloudType;
    public String name;
    public Object sla;
    public JSONArray locations;
    public JSONArray bandwidthPricing;

    /**
     * In case of <code>cloudType</code> is equal to <code>CloudTypes.IAAS</code>,
     * this field must be cast as <code>JSONArray</code>, while in case of
     * <code>cloudType</code> is equal to <code>CloudTypes.PAAS</code>, this
     * field must be cast as <code>JSONObject</code>.
     */
    public Object serviceFeatures;
    public ArrayList<JSONObject> computeInstanceTypes;


    /* c.tor */
    public CloudHarmonyService(CloudTypes cloudType, String name, Object sla, JSONArray locations,
            JSONArray bandwidthPricing, Object serviceFeatures,
            ArrayList<JSONObject> computeInstanceTypes) {
        this.cloudType = cloudType;
        this.name = name;
        this.sla = sla;
        this.locations = locations;
        this.bandwidthPricing = bandwidthPricing;
        this.serviceFeatures = serviceFeatures;
        this.computeInstanceTypes = computeInstanceTypes;
    }
}
