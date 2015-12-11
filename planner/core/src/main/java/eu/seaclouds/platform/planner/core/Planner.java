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
package eu.seaclouds.platform.planner.core;

import alien4cloud.model.topology.NodeTemplate;
import alien4cloud.tosca.model.ArchiveRoot;
import alien4cloud.tosca.parser.ParsingException;
import alien4cloud.tosca.parser.ParsingResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.seaclouds.common.tosca.ToscaSerializer;
import eu.seaclouds.planner.matchmaker.Matchmaker;
import eu.seaclouds.planner.matchmaker.Pair;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import eu.seaclouds.platform.planner.optimizer.Optimizer;

import java.io.IOException;
import java.util.*;

public class Planner {
    public static final String BENCHMARK_PLATFORM = "benchmark_platform";
    public static final String QOS_INFO = "QoSInfo";
    public static final String OPERATION = "operation";
    public static final String POLICIES = "policies";
    public static final String GROUPS = "groups";
    static Logger log = LoggerFactory.getLogger(ToscaSerializer.class);
    private static final String DISCOVERER_PATH = "discoverer/";

    private final String discovererURL;
    private HttpHelper discovererClient;
    private final String aam;
    private final String dam;
    private final String adp;

    public Planner() {
        this(null, null, null, null);
    }

    //Replan ctor
    public Planner(String discovererURL, String aam, String adp){
        this(discovererURL, aam, adp, null);
    }

    //Plan ctor
    public Planner(String discovererURL, String aam){
        this(discovererURL, aam, null, null);
    }

    private Planner(String discovererURL, String aam, String adp, String dam) {
        this.discovererURL = discovererURL + DISCOVERER_PATH;
        this.discovererClient = new HttpHelper(this.discovererURL);
        this.aam = aam;
        this.dam = dam;
        this.adp = adp;
    }

    private String generateMMOutput2(Map<String, HashSet<String>> mmResult, Map<String, Pair<NodeTemplate, String>> offerings) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        ArrayList<Map<String, ArrayList<Map<String, Object>>>> singleRes = new ArrayList<>();

        Yaml yml = new Yaml();

        for(String moduleName : mmResult.keySet()) {

            ArrayList<Map<String, Object>> suitableList = new ArrayList<>();

            //create suitable lists
            for (String id : mmResult.get(moduleName)) {
                String off = offerings.get(id).second;
                Map<String, Object> innerMap = new HashMap<>();
                innerMap.put(id, off);
                suitableList.add(innerMap);
            }

            HashMap<String, ArrayList<Map<String, Object>>> finalRes = new HashMap<>();
            finalRes.put(moduleName, suitableList);
            singleRes.add(finalRes);
        }


            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(singleRes);
    }

    protected String addBenchmarkInfo(String aam, Map<String, Object> nodeTemplates){
        Yaml yml = new Yaml();
        Map<String, Object> aamYaml = (Map<String, Object>) yml.load(aam);
        Map<String, Map<String, Object>> aamGroups = (Map<String, Map<String, Object>>) aamYaml.get(GROUPS);

         for(String m:aamGroups.keySet()){
            if(m.startsWith(OPERATION)){
                List<Map<String, Object>> policies = (List<Map<String, Object>>) aamGroups.get(m).get(POLICIES);
                for(Map<String, Object> o : policies){
                    if(o.keySet().contains(QOS_INFO)){
                        Map<String, Object> QoSInfo = (Map<String, Object>) o.get(QOS_INFO);
                        String benchMarkPlatform = (String) QoSInfo.get(BENCHMARK_PLATFORM);
                        if(!nodeTemplates.keySet().contains(benchMarkPlatform)){
                            log.error("Impossible to add benchmark node. The node "+benchMarkPlatform+" is not among the offerings");
                        }else {
                            Map<String, Object> bpNode = new HashMap<>();
                            bpNode.put(benchMarkPlatform, nodeTemplates.get(benchMarkPlatform));
                            QoSInfo.put(BENCHMARK_PLATFORM, bpNode);
                        }
                    }
                }
            }
        }

        String newAAM = yml.dump(aamYaml);
        return newAAM;
    }

    private String generateMMOutput(Map<String, HashSet<String>> mmResult, Map<String, Pair<NodeTemplate, String>> offerings) throws Exception{
        ObjectMapper mapper = new ObjectMapper();

        ArrayList<Map<String, ArrayList<Map<String, Object>>>> singleRes = new ArrayList<>();

        Yaml yml = new Yaml();

        for(String moduleName : mmResult.keySet()) {

            ArrayList<Map<String, Object>> suitableList = new ArrayList<>();

            //create suitable lists
            for (String id : mmResult.get(moduleName)) {
                String off = offerings.get(id).second;
                Map<String, Object> innerMap = new HashMap<>();
                Map<String, Map<String, Object>> tosca = (Map<String, Map<String, Object>>) yml.load(off);

                Object o = tosca.get("topology_template").get("node_templates");
                String od = yml.dump(o);
                innerMap.put(id, od);
                suitableList.add(innerMap);
            }

            HashMap<String, ArrayList<Map<String, Object>>> finalRes = new HashMap<>();
            finalRes.put(moduleName, suitableList);
            singleRes.add(finalRes);
        }


        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(singleRes);
    }

    public String[] plan() throws ParsingException, IOException {
        log.info("Planning for aam: \n" + aam);

        //Get offerings
        log.info("Getting Offeing Step: Start");
        Map<String, Pair<NodeTemplate, String>> offerings = getOfferings(); // getOfferingsFromDiscoverer();
        log.info("Getting Offeing Step: Complete");

        log.info("Got " + offerings.size() + " offerings from discoverer:");
        for(String s: offerings.keySet()){
            log.info("\n" + s + "\n\t" +offerings.get(s).second);
        }

        //Matchmake
        log.info("Matchmaking Step: Start");
        Matchmaker mm = new Matchmaker();
        Map<String, HashSet<String>> matchingOfferings = mm.match(ToscaSerializer.fromTOSCA(aam), offerings);
        log.info("Matchmaking Step: Complete");
        //Optimize
        String mmOutput = "";
        try {
            mmOutput = generateMMOutput2(matchingOfferings, offerings);
        }catch(JsonProcessingException e){
            log.error("Error preparing matchmaker output for optimization", e);
        }

        for(String s:matchingOfferings.keySet()){
            log.info("Module " + s + "has matching offerings: " + matchingOfferings.get(s));
        }

        log.info("Adding benchmark information to AAM");
        String modifiedAAM = addBenchmarkInfo(aam, transformDiscovererOutput(offerings));
        log.info("The modified AAM is:\n " + modifiedAAM );
        log.info("benchmark information added");

        log.info("Optimization Step: Start");
        log.info("Calling optimizer with suitable offerings: \n" + mmOutput);
        Optimizer optimizer = new Optimizer();
        String[] outputPlans = optimizer.optimize(modifiedAAM, mmOutput);
        log.info("Optimzer result: " + Arrays.asList(outputPlans));
        log.info("Optimization Step: Complete");

        return outputPlans;
    }

    public String[] plan(List<String> deployableOfferings) throws ParsingException, IOException {

        log.info("Planning for aam: \n" + aam);

        //Get offerings
        log.info("Getting Offeing Step: Start");
        Map<String, Pair<NodeTemplate, String>> offerings = getOfferings(deployableOfferings); // getOfferingsFromDiscoverer();
        log.info("Getting Offeing Step: Complete");

        log.info("\nNot deployable offering have been filtered!");
        log.info("\nDeployable offerings have location: " + deployableOfferings);

        log.info("Got " + offerings.size() + " offerings from discoverer:");
        for(String s: offerings.keySet()){
            log.info("\n" + s + "\n\t" +offerings.get(s).second);
        }

        //Matchmake
        log.info("Matchmaking Step: Start");
        Matchmaker mm = new Matchmaker();
        Map<String, HashSet<String>> matchingOfferings = mm.match(ToscaSerializer.fromTOSCA(aam), offerings);
        log.info("Matchmaking Step: Complete");
        //Optimize
        String mmOutput = "";
        try {
            mmOutput = generateMMOutput2(matchingOfferings, offerings);
        }catch(JsonProcessingException e){
            log.error("Error preparing matchmaker output for optimization", e);
        }

        for(String s:matchingOfferings.keySet()){
            log.info("Module " + s + "has matching offerings: " + matchingOfferings.get(s));
        }

        log.info("Adding benchmark information to AAM");
        String modifiedAAM = addBenchmarkInfo(aam, transformDiscovererOutput(offerings));
        log.info("The modified AAM is:\n " + modifiedAAM );
        log.info("benchmark information added");

        log.info("Optimization Step: Start");
        log.info("Calling optimizer with suitable offerings: \n" + mmOutput);
        Optimizer optimizer = new Optimizer();
        String[] outputPlans = optimizer.optimize(aam, mmOutput);
        log.info("Optimzer result: " + Arrays.asList(outputPlans));
        log.info("Optimization Step: Complete");

        return outputPlans;
    }

    private Map<String, HashSet<String>> filterOffering(Map<String, HashSet<String>> matchingResult, List<String> modulesToFilter){
        //TODO: need clear parameter requirements description
        throw new UnsupportedOperationException();
    }

    public String[] rePlan(List<String> deployableOfferings, List<String> modulesToFilter) throws ParsingException, IOException {
        //Get offerings
        log.info("Getting Offeing Step: Start");
        Map<String, Pair<NodeTemplate, String>> offerings = getOfferings(deployableOfferings); // getOfferingsFromDiscoverer();
        log.info("Getting Offeing Step: Complete");

        //Matchmake
        log.info("Matchmaking Step: Start");
        Matchmaker mm = new Matchmaker();
        Map<String, HashSet<String>> matchingOfferings = mm.match(ToscaSerializer.fromTOSCA(aam), offerings);
        matchingOfferings = filterOffering(matchingOfferings, modulesToFilter);
        log.info("Matchmaking Step: Complete");
        //Optimize
        String mmOutput = "";
        try {
            mmOutput = generateMMOutput2(matchingOfferings, offerings);
        }catch(JsonProcessingException e){
            log.error("Error preparing matchmaker output for optimization", e);
        }


        log.info("Optimization Step: Start");
        Optimizer optimizer = new Optimizer();
        String[] outputPlans = optimizer.optimize(aam, mmOutput);

        log.info("Optimization Step: Complete");

        return  outputPlans;
    }


    private List<NameValuePair> getReqParams(NameValuePair... params){
        ArrayList<NameValuePair> reqParams = new ArrayList<>();
        for(NameValuePair nvp : params)
            reqParams.add(nvp);
        return reqParams;
    }


    public Map<String, Pair<NodeTemplate, String>> getOfferings(){
        Map<String, Pair<NodeTemplate, String>> map = new HashMap<>();
        try {
            String discovererOutput = discovererClient.getRequest("fetch_all", Collections.EMPTY_LIST);

            ObjectMapper mapper = new ObjectMapper();
            DiscovererFetchallResult allOfferings = mapper.readValue(discovererOutput, DiscovererFetchallResult.class);
            String offerings = allOfferings.offering;

            ParsingResult<ArchiveRoot> offeringRes = ToscaSerializer.fromTOSCA(offerings);
            Map<String, NodeTemplate> offering = offeringRes.getResult().getTopology().getNodeTemplates();
            Yaml yml = new Yaml();
            Map<String, Map<String, Object>> adpYaml = (Map<String, Map<String, Object>>) yml.load(offerings);
            Map<String, Object> nodeTemplates = (Map<String, Object>) adpYaml.get("topology_template").get("node_templates");

            for (String node : offering.keySet()) {

                NodeTemplate nt = offering.get(node);
                HashMap<String, Object> offerMap = new HashMap<>();
                offerMap.put(node, nodeTemplates.get(node));
                String offerDump = yml.dump(offerMap);

                map.put(node, new Pair<NodeTemplate, String>(nt, offerDump));
            }
        }catch (Exception e){
            log.error(e.getCause().getMessage());
        }


        return map;
    }

    private Map<String, Object> transformDiscovererOutput(Map<String, Pair<NodeTemplate, String>> offerings){
        Map<String, Object> ofs = new HashMap<>();
        for(String k: offerings.keySet()){
            Pair<NodeTemplate, String> of = offerings.get(k);
            ofs.put(k, of.second);
        }
        return ofs;
    }

    public Map<String, Pair<NodeTemplate, String>> getOfferings(List<String> deployableOfferings){
        Map<String, Pair<NodeTemplate, String>> map = new HashMap<>();
        try {
            String discovererOutput = discovererClient.getRequest("fetch_all", Collections.EMPTY_LIST);

            ObjectMapper mapper = new ObjectMapper();
            DiscovererFetchallResult allOfferings = mapper.readValue(discovererOutput, DiscovererFetchallResult.class);
            String offerings = allOfferings.offering;

            ParsingResult<ArchiveRoot> offeringRes = ToscaSerializer.fromTOSCA(offerings);
            Map<String, NodeTemplate> offering = offeringRes.getResult().getTopology().getNodeTemplates();
            Yaml yml = new Yaml();
            Map<String, Map<String, Object>> adpYaml = (Map<String, Map<String, Object>>) yml.load(offerings);
            Map<String, Object> nodeTemplates = (Map<String, Object>) adpYaml.get("topology_template").get("node_templates");

            for (String node : offering.keySet()) {

                Map<String, Object> properties = ((Map<String, Map<String, Object>>) nodeTemplates.get(node)).get("properties");
                String location = (String) properties.get("location");

                if(deployableOfferings.contains(location)){

                    NodeTemplate nt = offering.get(node);
                    String s= nt.getProperties().get("location").toString();
                    HashMap<String, Object> offerMap = new HashMap<>();
                    offerMap.put(node, nodeTemplates.get(node));

                    String offerDump = yml.dump(offerMap);

                    map.put(node, new Pair<NodeTemplate, String>(nt, offerDump));
                }
            }
        }catch (Exception e){ }


        return map;
    }

    private Map<String, Pair<NodeTemplate, String>> getOfferingsFromDiscoverer() throws  IOException {
        //Get discoverer ids
        // GET discovererurl/fetch -> ids [ "id1", ..., "idn" ]
        // POST discovererurl/fetch?oid=idx -> { code: "...", errormessage: "...", idx: "yaml"  }
        ObjectMapper mapper = new ObjectMapper();
        String idsString = discovererClient.getRequest("fetch", Collections.EMPTY_LIST);
        String[] discoveredIds = mapper.readValue(idsString, String[].class);

        Map<String, Pair<NodeTemplate, String>> offerings = new HashMap<>();

        //Get Offerings
        for(String offerId:discoveredIds) {
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("oid", offerId));
            Pair<String, String> discovererResponse = discovererClient.postRequestWithParams("fetch", params);

            if(discovererResponse.first.equals("200")){
                String offerStr = discovererResponse.second;
                Offering offer = mapper.readValue(offerStr, Offering.class);

                try {
                    NodeTemplate nt = ToscaSerializer.fromTOSCA(offer.offering).getResult().getTopology().getNodeTemplates().values().iterator().next();
                    offerings.put(offerId, new Pair<NodeTemplate, String>(nt, offer.offering));
                }catch (ParsingException e){

                }
            }
        }

        return offerings;
    }

    public static class DiscovererFetchallResult{
        public String offering;
        public String[] offering_ids;
    }

    public static class Offering {

        public String offering;
        public String offering_id;

        public Offering() {}

        @JsonProperty
        public void setOffering(String offering) {
            this.offering = offering;
        }

        @JsonProperty
        public String getOffering(){
            return offering;
        }

        @JsonProperty
        public void setOffering_id(String offering_id) {
            this.offering_id = offering_id;
        }

        @JsonProperty
        public String getOffering_id() {
            return offering_id;
        }

    }

}
