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
import alien4cloud.tosca.parser.ParsingException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import eu.seaclouds.common.tosca.ToscaSerializer;
import eu.seaclouds.planner.matchmaker.Matchmaker;
import eu.seaclouds.planner.matchmaker.Pair;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.*;

public class Planner {

    private static final String DAM_GEN_OP = "/daminfo";

    private final String discovererURL;
    private final String optimizerURL;
    private final String slaGenURL;
    private final String monitorGenURL;
    private final HttpHelper discovererClient;
    private final HttpHelper optimizerClient;
    private final String aam;
    private final String dam;
    private final String adp;

    public Planner() {
        this(null, null, null);
    }

    //Replan ctor
    public Planner(String discovererURL, String optimizerURL, String aam, String dam){
        this(discovererURL, optimizerURL, null, null, aam, dam, null);
    }

    //Plan ctor
    public Planner(String discovererURL, String optimizerURL, String aam){
        this(discovererURL, optimizerURL, aam, null);
    }

    //DamGen ctor
    public Planner(String[] damGenURLs, String adp){
        this(null, null, damGenURLs[0], damGenURLs[1], null, null, adp);
    }

    private Planner(String discovererURL, String optimizerURL, String monitorGenURL, String slaGenURL, String aam, String dam, String adp){
        this.discovererURL = discovererURL;
        this.optimizerURL = optimizerURL;
        this.monitorGenURL = monitorGenURL;
        this.slaGenURL = slaGenURL;
        this.discovererClient = new HttpHelper(discovererURL);
        this.optimizerClient = new HttpHelper(optimizerURL);
        this.aam = aam;
        this.dam = dam;
        this.adp = adp;
    }

    private String generatMMOutput(Map<String, HashSet<String>> mmResult, Map<String, Pair<NodeTemplate, String>> offerings) throws Exception{
        ObjectMapper mapper = new ObjectMapper();

        ArrayList<Map<String, ArrayList<Map<String, Object>>>> singleRes = new ArrayList<>();

        for(String moduleName : mmResult.keySet()) {

            ArrayList<Map<String, Object>> suitableList = new ArrayList<>();

            //create suitable lists
            for (String id : mmResult.get(moduleName)) {
                Object off = offerings.get(id);
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

    public String plan() throws ParsingException, IOException {

        //Get offerings
        Map<String, Pair<NodeTemplate, String>> offerings = getOfferingsFromDiscoverer();

        //Matchmake
        Matchmaker mm = new Matchmaker();
        Map<String, HashSet<String>> matchingOfferings = mm.match(ToscaSerializer.fromTOSCA(aam), offerings);

        //Optimize
        String mmOutput = "";
        try {
            mmOutput = generatMMOutput(matchingOfferings, offerings);
        } catch (Exception e) {
            //What should we return in case of failure?
            e.printStackTrace();
        }

        //CHECKME: I am assuming it is possible to call the optimizer using a POST to /optimize?aam=xxx&dam=xxx&mmResults=xxx
        // https://docs.google.com/document/d/1GQrH3kYbIZN34OkHPmMlg8VrYlPZmC2jsbDOc-dkVcc/edit#

        List<NameValuePair> optParams = getReqParams(
                new BasicNameValuePair("aam", aam),
                new BasicNameValuePair("mmResults", mmOutput)
        );

        String optResult = optimizerClient.postRequest("/optimize", optParams);
        //return result
        return optResult; //TODO: check the output for the optimizer
    }

    private Map<String, HashSet<String>> filterOffering(Map<String, HashSet<String>> matchingResult){
        //TODO: need clear parameter requirements description
        throw new UnsupportedOperationException();
    }

    public String rePlan() throws ParsingException, IOException {
        //Get offerings
        Map<String, Pair<NodeTemplate, String>> offerings = getOfferingsFromDiscoverer();

        //Matchmake
        Matchmaker mm = new Matchmaker();
        Map<String, HashSet<String>> matchingOfferings = mm.match(ToscaSerializer.fromTOSCA(aam), offerings);

        matchingOfferings = filterOffering(matchingOfferings);

        //Optimize
        String mmOutput = "";
        try {
            mmOutput = generatMMOutput(matchingOfferings, offerings);
        } catch (Exception e) {
            //What should we return in case of failure?
            e.printStackTrace();
            return null;
        }

        //CHECKME: I am assuming it is possible to call the optimizer using a POST to /optimize?aam=xxx&dam=xxx&mmResults=xxx
        // https://docs.google.com/document/d/1GQrH3kYbIZN34OkHPmMlg8VrYlPZmC2jsbDOc-dkVcc/edit#

        List<NameValuePair> optParams = getReqParams(
                new BasicNameValuePair("aam", aam),
                new BasicNameValuePair("dam", dam),
                new BasicNameValuePair("mmResults", mmOutput)
        );
        String optResult = optimizerClient.postRequest("/optimize", optParams);
        return optResult; //TODO: check the output for the optimizer
    }

    public String generateDam() {

        List<NameValuePair> damInfoParams = getReqParams(new BasicNameValuePair("adp", adp));

        String monitorInfo = new HttpHelper(monitorGenURL).postRequest(DAM_GEN_OP, damInfoParams);

        if(monitorInfo == null)
            return "error"; //TODO: check it is a good response, need error handling policy

        String partialDam = generateMonitoringInfo(adp, monitorInfo);

        if(partialDam == null)
            return "error"; //TODO: need real error handling policy

        List<NameValuePair> slaGenInfoParams = getReqParams(new BasicNameValuePair("dam", partialDam));
        String slaInfo = new HttpHelper(slaGenURL).postRequest(DAM_GEN_OP, slaGenInfoParams);

        if(slaInfo == null)
            return "error"; //TODO: check it is a good response, need proper error handling

        String finalDam = generateSlaInfo(partialDam, slaInfo);
        return finalDam;
    }

    public String generateMonitoringInfo(String currentAdp, String monitoringServiceResponse){
        Yaml parser = new Yaml();
        Map<String, Map<String, Object>> adpYml = (Map<String, Map<String, Object>>) parser.load(currentAdp);
        Map<String, Object> groups = adpYml.get("groups");

        try{
            ObjectMapper mapper = new ObjectMapper();
            String[] mrs = mapper.readValue(monitoringServiceResponse, new String[0].getClass());
            for(String s: mrs){
                Map<String, Object> rule = (HashMap<String, Object>) parser.load(s);
                for(String k:rule.keySet()){
                    groups.put(k, rule.get(k));
                }
            }
            return parser.dump(adpYml);
        }catch(Exception e){
            e.printStackTrace();
            return "error"; //FIXME: need error handling policy
        }
    }

    public String generateSlaInfo(String partialDam, String slaServiceResponse){
        Yaml parser = new Yaml();
        Map<String, Map<String, Object>> damYml = (Map<String, Map<String, Object>>) parser.load(partialDam);
        Map<String, Object> groups = damYml.get("groups");


        try {
            ObjectMapper mapper = new ObjectMapper();
            SLAInfo i = mapper.readValue(slaServiceResponse, SLAInfo.class);

            String slaPolicy = new StringBuilder().append("sla_gen_info:\n")
                                                  .append("\tmember: [ application ]\n")
                                                  .append("\tpolicies:\n").append("\t\t- id: ")
                                                  .append("123").append("\n").toString();

            HashMap<String, Object> slaGroup = new HashMap<>();
            slaGroup.put("members", new String[]{"application"});

            ArrayList<HashMap<String, String>> l = new ArrayList<>();
            HashMap<String, String> m = new HashMap<>();
            m.put("id", i.id);
            l.add(m);
            slaGroup.put("policies", l);

            groups.put("sla_gen_info", slaGroup);

            return parser.dump(damYml);

        }catch(Exception e){
            e.printStackTrace();
            return "error"; //FIXME: need error handling policy
        }
    }

    private List<NameValuePair> getReqParams(NameValuePair... params){
        ArrayList<NameValuePair> reqParams = new ArrayList<>();
        for(NameValuePair nvp : params)
            reqParams.add(nvp);
        return reqParams;
    }

    private Map<String, Pair<NodeTemplate, String>> getOfferingsFromDiscoverer() throws ParsingException, IOException {
        //Get discoverer ids
        // GET discovererurl/fetch -> ids [ "id1", ..., "idn" ]
        // POST discovererurl/fetch?oid=idx -> { code: "...", errormessage: "...", idx: "yaml"  }
        String idsString = discovererClient.getRequest("/fetch", Collections.EMPTY_LIST);
        String[] discoveredIds = discovererClient.getObjectFromJson(idsString, String[].class);

        Map<String, Pair<NodeTemplate, String>> offerings = new HashMap<>();

        //Get Offerings
        for(String offerId:discoveredIds) {
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("oid", offerId));
            String offerStr = discovererClient.postRequest("/fetch", params);
            Map<String, String> offer = discovererClient.getObjectFromJson(offerStr, new HashMap<String, String>().getClass());

            NodeTemplate nt = ToscaSerializer.fromTOSCA(offer.get(offerId)).getResult().getTopology().getNodeTemplates().values().iterator().next();
            offerings.put(offerId, new Pair<NodeTemplate, String>(nt, offer.get(offerId)));
        }

        return offerings;
    }


    public static class SLAInfo {
        public String id;

        public SLAInfo() {}

        @JsonProperty
        public void setId(String id) { this.id = id; }

        @JsonProperty
        public String getId() { return this.id; }

    }

    class OfferJson {

        public String code;
        public String idx;

        public OfferJson() {
        }

        @JsonProperty
        public void setCode(String code) {
            this.code = code;
        }

        @JsonProperty
        public String getCode() {
            return code;
        }

        @JsonProperty
        public void setIdx(String idx) {
            this.idx = idx;
        }

        @JsonProperty
        public String getIdx() {
            return idx;
        }

    }

}
