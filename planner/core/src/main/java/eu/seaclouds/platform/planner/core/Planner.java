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
import eu.seaclouds.common.tosca.ToscaSerializer;
import eu.seaclouds.planner.matchmaker.Matchmaker;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.*;

public class Planner {

    private static final String DAM_GEN_OP = "/daminfo";

    private final String discovererURL;
    private final String optimizerURL;
    private final String slaGenURL;
    private final String monitorGenURL;
    private final String credentialGenURL;
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
        this(discovererURL, optimizerURL, null, null, null, aam, dam, null);
    }

    //Plan ctor
    public Planner(String discovererURL, String optimizerURL, String aam){
        this(discovererURL, optimizerURL, aam, null);
    }

    //DamGen ctor
    public Planner(String[] damGenURLs, String adp){
        this(null, null, damGenURLs[0], damGenURLs[1], damGenURLs[2], null, null, adp);
    }

    private Planner(String discovererURL, String optimizerURL, String credentialGenURL, String monitorGenURL, String slaGenURL, String aam, String dam, String adp){
        this.discovererURL = discovererURL;
        this.optimizerURL = optimizerURL;
        this.credentialGenURL = credentialGenURL;
        this.monitorGenURL = monitorGenURL;
        this.slaGenURL = slaGenURL;
        this.discovererClient = new HttpHelper(discovererURL);
        this.optimizerClient = new HttpHelper(optimizerURL);
        this.aam = aam;
        this.dam = dam;
        this.adp = adp;
    }

    public String plan() throws ParsingException, IOException {

        //Get offerings
        Map<String, NodeTemplate> offerings = getOfferingsFromDiscoverer();

        //Matchmake
        Matchmaker mm = new Matchmaker();
        Map<String, HashSet<String>> matchingOfferings = mm.match(ToscaSerializer.fromTOSCA(aam), offerings);

        //Optimize
        ObjectMapper mapper = new ObjectMapper();

            //FIXME: I am assuming it is possible to call the optimizer using a POST to /optimize?aam=xxx&dam=xxx&mmResults=xxx
            // https://docs.google.com/document/d/1GQrH3kYbIZN34OkHPmMlg8VrYlPZmC2jsbDOc-dkVcc/edit#

        List<NameValuePair> optParams = getReqParams(
                new BasicNameValuePair("aam", aam),
                new BasicNameValuePair("mmResults", mapper.writeValueAsString(matchingOfferings))
        );

        String optResult = optimizerClient.postRequest("/optimize", optParams);
        //return result
        return optResult; //TODO: check the output for the optimizer
    }

    public String rePlan() throws ParsingException, IOException {
        //Get offerings
        Map<String, NodeTemplate> offerings = getOfferingsFromDiscoverer();

        //Matchmake
        Matchmaker mm = new Matchmaker();
        Map<String, HashSet<String>> matchingOfferings = mm.match(ToscaSerializer.fromTOSCA(aam), offerings);

        //Optimize
        ObjectMapper mapper = new ObjectMapper();
            //FIXME: I am assuming it is possible to call the optimizer using a POST to /optimize?aam=xxx&dam=xxx&mmResults=xxx
            // https://docs.google.com/document/d/1GQrH3kYbIZN34OkHPmMlg8VrYlPZmC2jsbDOc-dkVcc/edit#

        List<NameValuePair> optParams = getReqParams(
                new BasicNameValuePair("aam", aam),
                new BasicNameValuePair("dam", dam),
                new BasicNameValuePair("mmResults", mapper.writeValueAsString(matchingOfferings))
        );
        String optResult = optimizerClient.postRequest("/optimize", optParams);
        //return result
        return optResult; //TODO: check the output for the optimizer
    }

    public String generateDam() {

        List<NameValuePair> damInfoParams = getReqParams(new BasicNameValuePair("adp", adp));

        String slaInfo = new HttpHelper(slaGenURL).postRequest(DAM_GEN_OP, damInfoParams);
        String monitorInfo = new HttpHelper(monitorGenURL).postRequest(DAM_GEN_OP, damInfoParams);
        String credentialInfo = new HttpHelper(credentialGenURL).postRequest(DAM_GEN_OP, damInfoParams);

        //TODO: combine output

        throw new UnsupportedOperationException();
    }

    private List<NameValuePair> getReqParams(NameValuePair... params){
        ArrayList<NameValuePair> reqParams = new ArrayList<>();
        for(NameValuePair nvp : params)
            reqParams.add(nvp);
        return reqParams;
    }

    private Map<String, NodeTemplate> getOfferingsFromDiscoverer() throws ParsingException, IOException {
        //Get discoverer ids
        // GET discovererurl/fetch -> ids [ "id1", ..., "idn" ]
        // POST discovererurl/fetch?oid=idx -> { code: "...", errormessage: "...", idx: "yaml"  }
        String idsString = discovererClient.getRequest("/fetch", Collections.EMPTY_LIST);
        String[] discoveredIds = discovererClient.getObjectFromJson(idsString, String[].class);

        Map<String, NodeTemplate> offerings = new HashMap<>();

        //Get Offerings
        for(String offerId:discoveredIds) {
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("oid", offerId));
            String offerStr = discovererClient.postRequest("/fetch", params);
            OfferJson offer = discovererClient.getObjectFromJson(offerStr, OfferJson.class);

            NodeTemplate nt = ToscaSerializer.fromTOSCA(offer.code).getResult().getTopology().getNodeTemplates().values().iterator().next();
            offerings.put(offer.idx, nt);
        }

        return offerings;
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
