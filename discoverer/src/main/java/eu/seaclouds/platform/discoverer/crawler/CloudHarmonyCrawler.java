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
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;


/* cloud type enumeration */
enum CloudTypes {
    IAAS,
    PAAS
}


public class CloudHarmonyCrawler extends SCCrawler {
    /* consts */
    private static final String API_KEY = "3y5A9MYhtEPFaq16CLdUvfH7TgK0zjc2";

    /* vars */
    private JSONParser jsonParser;
    private Hashtable<String, String> booleans;
    private Hashtable<String, String> numbers;
    private Hashtable<String, String> strings;
    private DecimalFormat slaFormat = new DecimalFormat("0.00000");
    CloseableHttpClient httpclient;
    static Logger log = LoggerFactory.getLogger(CloudHarmonyCrawler.class);

    public static String Name = "CloudHarmonyCrawler";

    /* c.tor */
    public CloudHarmonyCrawler() {
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
        this.numbers.put("localStorage", "disk_size");

        /* mapping strings */
        this.strings = new Hashtable<String, String>();
        this.strings.put("localDiskType", "disk_type");
    }

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


    private JSONObject getComputeInstanceType(String serviceId, String instanceTypeId) {
        /* query string */
        String queryStr = "https://cloudharmony.com/api/compute/"
                + serviceId + "/"
                + instanceTypeId + "?"
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
            name = (String) service.get("name");

        /* sla */
        Double sla = null;
        if (service.containsKey("sla")) {
            Object slaValue = service.get("sla");
            if (slaValue.getClass().equals(Long.class)) {
                sla = ((Long) slaValue).doubleValue();
            } else { // Double
                sla = (Double) slaValue;
            }
        }

        /* locations */
        JSONArray locations = null;
        if (service.containsKey("regions"))
            locations = (JSONArray) service.get("regions");


        /* service features */
        Object serviceFeatures;
        ArrayList<JSONObject> computeInstanceTypes = null;
        if (t == CloudTypes.IAAS) {
            serviceFeatures = iaas_getServiceFeatures(serviceId); /* in the service features we can find
                                                                   * all the instance types to use to retrieve
                                                                   * the compute instance types.
                                                                   */
            if( serviceFeatures != null && ((JSONArray)(serviceFeatures)).size() != 0 ) {
                /* retrieving instance types
                 * up to now only Amazon AWS:EC2 and Aruba ARUBA:COMPUTE
                 * return more than one properties object */
                JSONObject oneFeature = (JSONObject) ((JSONArray) (serviceFeatures)).get(0);

                /* It's actually returned a list of service features apparently equal
                 * (except for id which is -> aws:ec2, aws:ec2-..-..) */
                JSONArray instanceTypes = (JSONArray) oneFeature.get("instanceTypes");
                Iterator<String> instanceTypesIDs = instanceTypes.iterator();

                /* querying each instance type */
                computeInstanceTypes = new ArrayList<JSONObject>();
                while (instanceTypesIDs.hasNext()) {
                    /* current instance id */
                    String instanceTypeID = instanceTypesIDs.next();

                    JSONObject cit = getComputeInstanceType(serviceId, instanceTypeID);
                    computeInstanceTypes.add(cit);
                }
            }
        } else { // t == CloudTypes.PAAS
            serviceFeatures = paas_getServiceFeatures(serviceId);
        }

        /* returning everything */
        return new CloudHarmonyService(t, // cloud type
                serviceId,                // service id
                name,                     // cloud name
                sla,                      // availability
                locations,                // geographics
                null,                     // bandwidth pricing (unused)
                serviceFeatures,          // cloudharmony service features
                computeInstanceTypes);    // compute instance types
    }



    private void generateOfferings(CloudHarmonyService chs) {
        if(chs == null || chs.computeInstanceTypes == null || chs.locations == null)
            return;

        /* for each location... */
        Iterator<JSONObject> locations = chs.locations.iterator();
        while (locations.hasNext()) {
            JSONObject location_i = locations.next();

            Offering newOffer;

            if(chs.cloudType == CloudTypes.IAAS) {
                /* ...and for each compute instance type */
                for (JSONObject cit_i : chs.computeInstanceTypes) {
                    /* generate the offering */
                    newOffer = generateIAASOffering(chs, location_i, cit_i);

                    /* collect */
                    if (newOffer != null)
                        addOffering(newOffer);
                }
            } else { // chs.cloudType == CloudTypes.PAAS
                /* generate the offering */
                newOffer = generatePAASOffering(chs, location_i);

                /* collect */
                if(newOffer != null)
                    addOffering(newOffer);
            }
        }
    }



    private String expandCountryCode(String cc) {
        Locale l = new Locale("", cc);
        return l.getDisplayCountry();
    }



    private Offering generateIAASOffering(CloudHarmonyService chs, JSONObject locationInformation,
                                      JSONObject computeInstanceType) {
        /* tosca lines into ArrayList */
        ArrayList<String> gt = new ArrayList<>();
        String providerName = chs.name;
        /* name not sanitized (used to access SPECint table) */
        String providerOriginalName = chs.originalName;
        String instanceId = (String) computeInstanceType.get("instanceId");
        String locationCode = (String) locationInformation.get("providerCode");

        /* name, taken as 'cloud service name'_'instance id'_'city where is located'  */
        String name = Offering.sanitizeName(providerName + "_" + instanceId + "_" + locationCode);

        Offering offering = new Offering(name);
        offering.setType("seaclouds.nodes.Compute." + Offering.sanitizeName(providerName));

        offering.addProperty("resource_type", "compute");
        offering.addProperty("hardwareId", instanceId);
        offering.addProperty("location", chs.serviceId);
        offering.addProperty("region", locationCode);

        /* Performance */
        Integer performance = CloudHarmonySPECint.getSPECint(providerOriginalName, instanceId);
        if (performance != null) {
            offering.addProperty("performance", performance.toString());
        }
        /* sla */
        if(chs.sla != null) {
            offering.addProperty("availability", this.slaFormat.format(chs.sla/100.0));
        }

        /* location */
        offering.addProperty("country", expandCountryCode((String) (locationInformation.get("country"))));
        offering.addProperty("city", expandCountryCode((String) (locationInformation.get("city"))));

        JSONArray pricings = (JSONArray) computeInstanceType.get("pricing");

        if (pricings.size() > 0) {
            JSONObject pricing = ((JSONObject) ((JSONArray) computeInstanceType.get("pricing")).get(0));

            Object price = pricing.get("price");
            Object currency = pricing.get("currency");
            Object priceInterval = pricing.get("priceInterval");

            offering.addProperty("cost", price.toString() + " " + currency.toString() + "/" + priceInterval);
        }

        /* compute instance type - numbers */
        for(String k : this.numbers.keySet()) {
            if( computeInstanceType.containsKey(k) ) {
                Object vv = computeInstanceType.get(k);
                offering.addProperty(this.numbers.get(k), vv.toString());
            }
        }

        /* compute instance type - strings */
        if( computeInstanceType != null ) {
            for (String k : this.strings.keySet()) {
                if (computeInstanceType.containsKey(k)) {
                    String v = (String) (computeInstanceType.get(k));
                    offering.addProperty(this.strings.get(k), v);
                }
            }
        }

        return offering;
    }



    private Offering generatePAASOffering(CloudHarmonyService chs, JSONObject location) {
        /* tosca lines into ArrayList */
        /* name */
        String name = Offering.sanitizeName(chs.name + "_" + location.get("city"));

        Offering offering = new Offering(name);

        offering.setType("seaclouds.nodes.Platform." + Offering.sanitizeName(chs.name));

        /* resource type  */
        offering.addProperty("resource_type", "platform");

        /* sla */
        if(chs.sla != null) {
            offering.addProperty("availability", this.slaFormat.format(chs.sla / 100.0));
        }

        /* location */
        offering.addProperty("country", expandCountryCode((String) (location.get("country"))));
        offering.addProperty("city", expandCountryCode((String)(location.get("city"))));

        /* features */
        JSONObject feat = (JSONObject) chs.serviceFeatures;

        /* booleans */
        for(String k : this.booleans.keySet()) {
            if( feat.containsKey(k) ) {
                boolean v = Boolean.parseBoolean((String)(feat.get(k)));
                offering.addProperty(this.booleans.get(k), (v ? "true" : "false"));
            }
        }

        /* supported databases and languages */
        supports("supportedDatabases", feat, offering);
        supports("supportedLanguages", feat, offering);

        /* building the tosca */
        return offering;
    }



    private void supports(String keyName, JSONObject feat, Offering offering) {
        if( feat.containsKey(keyName) == false )
            return;

        JSONArray dbs = (JSONArray) feat.get(keyName);
        for(int i = 0; i < dbs.size(); i++) {
            String cloudHarmonySupportName = (String) dbs.get(i);
            String seaCloudsSupportName = externaltoSCTag.get(cloudHarmonySupportName);

            if (seaCloudsSupportName != null)
                offering.addProperty(seaCloudsSupportName + "_support", "true");
        }
    }

    private void crawlComputeOfferings() {
        /* iaas section */
        String computeQuery = "https://cloudharmony.com/api/services?"
                + "api-key=" + API_KEY + "&"
                + "serviceTypes=compute";

        JSONObject resp = (JSONObject) query(computeQuery);

        if(resp == null) {
            return;
        }

        JSONArray computes = (JSONArray) resp.get("ids");

        Iterator<String> it = computes.iterator();
        while (it.hasNext()) {
            try {
                String serviceId = it.next();
                CloudHarmonyService chService = getService(serviceId, CloudTypes.IAAS);
                if (chService != null)
                    generateOfferings(chService);
            } catch(Exception ex) {
                log.warn(ex.getMessage());
            }
        }
    }

    private void crawlPaasOfferings() {
        JSONObject resp;
        Iterator<String> it;

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
                generateOfferings(chService);
            } catch(Exception e) {
                log.warn(e.getMessage());
            }
        }
    }

    @Override
    public void crawl() {
        /* crawl Compute and PaaS offerings */
        this.crawlComputeOfferings();
        this.crawlPaasOfferings();
    }
}



class CloudHarmonyService {
    /* members */
    public CloudTypes cloudType;
    public String name;
    public String originalName;
    public String serviceId;
    public Double sla;
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
    public CloudHarmonyService(CloudTypes cloudType, String serviceId, String name, Double sla, JSONArray locations,
                               JSONArray bandwidthPricing, Object serviceFeatures,
                               ArrayList<JSONObject> computeInstanceTypes) {
        this.serviceId = serviceId;
        this.cloudType = cloudType;
        this.name = Offering.sanitizeName(name);
        this.originalName = name;
        this.sla = sla;
        this.locations = locations;
        this.bandwidthPricing = bandwidthPricing;
        this.serviceFeatures = serviceFeatures;
        this.computeInstanceTypes = computeInstanceTypes;
    }
}
