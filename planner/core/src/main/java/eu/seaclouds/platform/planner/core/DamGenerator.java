package eu.seaclouds.platform.planner.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringDamGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class DamGenerator {

    private static final String SLA_GEN_OP = "/seaclouds/templates";
    private static final String SLA_INFO_GROUPNAME = "sla_gen_info";
    private static final String MONITOR_INFO_GROUPNAME = "monitoringInformation";

    private static final String ADD_BROOKLYN_LOCATION = "add_brooklyn_location_";
    private static final String BROOKLYN_LOCATION = "brooklyn.location";
    private static final String LOCATION = "location";
    public static final String REGION = "region";
    public static final String HARDWARE_ID = "hardwareId";
    public static final String CLOUD_FOUNDRY = "CloudFoundry";
    public static final String POLICIES = "policies";
    public static final String GROUPS = "groups";
    public static final String HOST = "host";
    public static final String REQUIREMENTS = "requirements";
    public static final String MEMBERS = "members";
    public static final String ID = "id";
    public static final String APPLICATION = "application";
    public static final String TYPE = "type";
    public static final String TOPOLOGY_TEMPLATE = "topology_template";
    public static final String NODE_TEMPLATES = "node_templates";
    public static final String NODE_TYPES = "node_types";
    public static final String PROPERTIES = "properties";
    private static final String BROOKLYN_TYPES_MAPPING = "mapping/brooklyn-types-mapping.yaml";


    static Map<String, List<Module>> monitoringInfoByApplication=new HashMap<String,List<Module>>();

    static Logger log = LoggerFactory.getLogger(DamGenerator.class);

    public static String generateDam(String adp, String monitorGenURL, String monitorGenPort, String slaGenURL){
        Yaml yml = new Yaml();
        Map<String, Object> adpYaml = (Map<String, Object>) yml.load(adp);
        adpYaml = DamGenerator.translateAPD(adpYaml);
        adpYaml = DamGenerator.addMonitorInfo(yml.dump(adpYaml), monitorGenURL, monitorGenPort);

        String slaInfoResponse = new HttpHelper(slaGenURL).postInBody(SLA_GEN_OP, yml.dump(adpYaml));
        checkNotNull(slaInfoResponse, "Error getting SLA info");
        adpYaml = DamGenerator.addApplicationInfo(adpYaml, slaGenURL, SLA_INFO_GROUPNAME);

        String adpStr = yml.dump(adpYaml);
        return adpStr;
    }


    public static Map<String, Object> addMonitorInfo(String adp, String monitorUrl, String monitorPort){
        MonitoringDamGenerator monDamGen = new MonitoringDamGenerator(monitorUrl, monitorPort);
        String generatedApplicationId = UUID.randomUUID().toString();


        List<Module> generated = new ArrayList<>();
        try {
            generated = monDamGen.generateMonitoringInfo(adp);
        }catch (Exception e){
            log.error("MonitorGeneration failed", e);
        }
        monitoringInfoByApplication.put(generatedApplicationId, generated);

        HashMap<String, Object> appGroup = new HashMap<>();
        appGroup.put(MEMBERS, new String[]{APPLICATION});

        ArrayList<HashMap<String, String>> l = new ArrayList<>();
        HashMap<String, String> m = new HashMap<>();
        m.put(ID, generatedApplicationId);
        l.add(m);
        appGroup.put(POLICIES, l);

        Yaml yml = new Yaml();
        Map<String, Object> adpYaml = (Map<String, Object>) yml.load(adp);
        Map<String, Object> groups = (Map<String, Object>) adpYaml.get(GROUPS);
        groups.put(MONITOR_INFO_GROUPNAME, appGroup);

        return adpYaml;
    }

    public static Map<String, Object> translateAPD(Map<String, Object> adpYaml){

        DeployerTypesResolver deployerTypesResolver = null;
        try{
            deployerTypesResolver = new DeployerTypesResolver(Resources
                    .getResource(BROOKLYN_TYPES_MAPPING).toURI().toString());}
        catch(Exception e){
            throw new RuntimeException(e);
        }

        Yaml yml = new Yaml();
        List<Object> groupsToAdd = new ArrayList<>();
        Map<String, ArrayList<String>> groups = new HashMap<>();

        Map<String, Object> ADPgroups = (Map<String, Object>) adpYaml.get(GROUPS);

        Map<String, Object> topologyTemplate = (Map<String, Object>) adpYaml.get(TOPOLOGY_TEMPLATE);
        Map<String, Object> nodeTemplates = (Map<String, Object>) topologyTemplate.get(NODE_TEMPLATES);
        Map<String, Object> nodeTypes = (Map<String, Object>) adpYaml.get(NODE_TYPES);

        for(String moduleName:nodeTemplates.keySet()){
            Map<String, Object> module = (Map<String, Object>) nodeTemplates.get(moduleName);

            //type replacement
            String moduleType = (String) module.get(TYPE);
            if(nodeTypes.containsKey(moduleType)) {
                Map<String, Object> type = (HashMap<String, Object>) nodeTypes.get(moduleType);
                String sourceType = (String) type.get("derived_from");
                String targetType = deployerTypesResolver.resolveNodeType(sourceType);
                if (targetType != null) {
                    module.put("type", targetType);
                } else {
                    module.put("type", sourceType);
                }
            }

            if(module.keySet().contains(REQUIREMENTS)){
                List<Map<String, Object> > requirements = (ArrayList<Map<String, Object> >) module.get(REQUIREMENTS);
                for(Map<String, Object> req : requirements){
                    if(req.keySet().contains(HOST)){
                        String host = (String) req.get(HOST);
                        if(!groups.keySet().contains(host)){
                            groups.put(host, new ArrayList<String>());
                        }
                        groups.get(host).add(moduleName);
                    }
                }
            }
        }

        //get brookly location from host
        for(String group: groups.keySet()){
            HashMap<String, Object> policyGroup = new HashMap<>();
            policyGroup.put(MEMBERS, groups.get(group));

            HashMap<String, Object> cloudOffering = (HashMap<String, Object>) nodeTemplates.get(group);
            HashMap<String, Object> properties = (HashMap<String, Object>) cloudOffering.get(PROPERTIES);

            String location = (String) properties.get(LOCATION);
            String region = (String) properties.get(REGION);
            String hardwareId = (String) properties.get(HARDWARE_ID);

            ArrayList<HashMap<String, Object>> policy = new ArrayList<>();
            HashMap<String, Object> p = new HashMap<>();
            if(location != null) {
                p.put(BROOKLYN_LOCATION, location + (location.equals(CLOUD_FOUNDRY) ? "" : ":" + region));
            }else{
                p.put(BROOKLYN_LOCATION, group);
            }
            policy.add(p);

            policyGroup.put(POLICIES, policy);

            HashMap<String, Object> finalGroup = new HashMap<>();

            ADPgroups.put(ADD_BROOKLYN_LOCATION + group ,policyGroup);
        }

        String finalDam = yml.dump(adpYaml);
        return adpYaml;
    }

    public static Map<String, Object> addApplicationInfo(Map<String, Object> damYml, String serviceResponse, String groupName){
        Map<String, Object> groups = (Map<String, Object>) damYml.get(GROUPS);

        try {
            ObjectMapper mapper = new ObjectMapper();
            ApplicationMonitorId i = mapper.readValue(serviceResponse, ApplicationMonitorId.class);

            HashMap<String, Object> appGroup = new HashMap<>();
            appGroup.put(MEMBERS, new String[]{APPLICATION});

            ArrayList<HashMap<String, String>> l = new ArrayList<>();
            HashMap<String, String> m = new HashMap<>();
            m.put(ID, i.id);
            l.add(m);
            appGroup.put(POLICIES, l);

            groups.put(groupName, appGroup);

        }catch(Exception e){
            log.error("Error adding " + groupName + " info", e);
        }
        return damYml;
    }

}
