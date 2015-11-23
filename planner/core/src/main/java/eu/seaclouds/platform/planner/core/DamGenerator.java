package eu.seaclouds.platform.planner.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import static com.google.common.base.Preconditions.*;
import java.io.File;
import java.util.*;
import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringDamGenerator;

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

    static Map<String, List<Module>> monitoringInfoByApplication=new HashMap<String,List<Module>>();

    static Logger log = LoggerFactory.getLogger(DamGenerator.class);

    public static String generateDam(String adp, String monitorGenURL, String monitorGenPort, String slaGenURL){
        Yaml yml = new Yaml();
        Map<String, Object> adpYaml = (Map<String, Object>) yml.load(adp);
        adpYaml = DamGenerator.translateAPD(adpYaml);
        adpYaml = DamGenerator.addMonitorInfo(adp, monitorGenURL, monitorGenPort);

        String slaInfoResponse = new HttpHelper(slaGenURL).postInBody(SLA_GEN_OP, adp);
        checkNotNull(slaInfoResponse, "Error getting SLA info");
        adpYaml = DamGenerator.addApplicationInfo(adpYaml, slaGenURL, SLA_INFO_GROUPNAME);

        String adpStr = yml.dump(adpYaml);
        return adpStr;
    }


    public static Map<String, Object> addMonitorInfo(String adp, String monitorUrl, String monitorPort){
        MonitoringDamGenerator monDamGen = new MonitoringDamGenerator(monitorUrl, monitorPort);
        String generatedApplicationId = UUID.randomUUID().toString();

        List<Module> generated = monDamGen.generateMonitoringInfo(adp);
        monitoringInfoByApplication.put(generatedApplicationId, generated);

        HashMap<String, Object> appGroup = new HashMap<>();
        appGroup.put("members", new String[]{"application"});

        ArrayList<HashMap<String, String>> l = new ArrayList<>();
        HashMap<String, String> m = new HashMap<>();
        m.put("id", generatedApplicationId);
        l.add(m);
        appGroup.put("policies", l);

        Yaml yml = new Yaml();
        Map<String, Object> adpYaml = (Map<String, Object>) yml.load(adp);
        Map<String, Object> groups = (Map<String, Object>) adpYaml.get("groups");
        groups.put(MONITOR_INFO_GROUPNAME, appGroup);

        return adpYaml;
    }

    public static Map<String, Object> translateAPD(Map<String, Object> adpYaml){
        Yaml yml = new Yaml();
        List<Object> groupsToAdd = new ArrayList<>();
        Map<String, ArrayList<String>> groups = new HashMap<>();

        Map<String, Object> ADPgroups = (Map<String, Object>) adpYaml.get("groups");

        Map<String, Object> topologyTemplate = (Map<String, Object>) adpYaml.get("topology_template");
        Map<String, Object> nodeTemplates = (Map<String, Object>) topologyTemplate.get("node_templates");
        Map<String, Object> nodeTypes = (Map<String, Object>) adpYaml.get("node_types");

        for(String moduleName:nodeTemplates.keySet()){
            Map<String, Object> module = (Map<String, Object>) nodeTemplates.get(moduleName);

            //type replacement
            String moduleType = (String) module.get("type");
            if(nodeTypes.containsKey(moduleType)){
                Map<String, Object> type = (HashMap<String, Object>) nodeTypes.get(moduleType);
                String oldType = (String) type.get("derived_from");
                if(oldType.startsWith("seaclouds.nodes.")){
                    String newType = oldType.replaceAll("seaclouds.nodes.", "org.apache.brooklyn.entity.");
                    module.put("type", newType);
                }
            }

            if(module.keySet().contains("requirements")){
                List<Map<String, Object> > requirements = (ArrayList<Map<String, Object> >) module.get("requirements");
                for(Map<String, Object> req : requirements){
                    if(req.keySet().contains("host")){
                        String host = (String) req.get("host");
                        if(!groups.keySet().contains(host)){
                            groups.put(host, new ArrayList<String>());
                        }
                        groups.get(host).add(moduleName);
                    }
                }
            }
        }

        //get brookly location from host
        int blidx = 1;
        for(String group: groups.keySet()){
            HashMap<String, Object> policyGroup = new HashMap<>();
            policyGroup.put("members", groups.get(group));

            HashMap<String, Object> cloudOffering = (HashMap<String, Object>) nodeTemplates.get(group);
            HashMap<String, Object> properties = (HashMap<String, Object>) cloudOffering.get("properties");
            String location = (String) properties.get("location");
            String region = (String) properties.get("region");
            String hardwareId = (String) properties.get("hardwareId");

            ArrayList<HashMap<String, Object>> policy = new ArrayList<>();
            HashMap<String, Object> p = new HashMap<>();
            p.put("brooklyn.location", location + (location.equals("CloudFoundry") ? "" : ":" + region));
            policy.add(p);

            policyGroup.put("policies", policy);

            HashMap<String, Object> finalGroup = new HashMap<>();

            ADPgroups.put("add_brooklyn_location" + blidx++ ,policyGroup);
        }

        String finalDam = yml.dump(adpYaml);
        return adpYaml;
    }

    public static Map<String, Object> addApplicationInfo(Map<String, Object> damYml, String serviceResponse, String groupName){
        Map<String, Object> groups = (Map<String, Object>) damYml.get("groups");

        try {
            ObjectMapper mapper = new ObjectMapper();
            ApplicationMonitorId i = mapper.readValue(serviceResponse, ApplicationMonitorId.class);

            HashMap<String, Object> appGroup = new HashMap<>();
            appGroup.put("members", new String[]{"application"});

            ArrayList<HashMap<String, String>> l = new ArrayList<>();
            HashMap<String, String> m = new HashMap<>();
            m.put("id", i.id);
            l.add(m);
            appGroup.put("policies", l);

            groups.put(groupName, appGroup);

        }catch(Exception e){
            log.error("Error adding " + groupName + " info", e);
        }
        return damYml;
    }

}
