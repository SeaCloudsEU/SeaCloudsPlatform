package eu.seaclouds.platform.planner.core;

import com.google.common.io.Resources;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;


/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@Test
public class DamGeneratorTest {

    private static final String BROOKLYN_TYPES_MAPPING = "mapping/brooklyn-types-mapping.yaml";

    @Test
    public void  damBrooklynTest() throws Exception {
        String adp = new Scanner(new File(Resources.getResource("generated_adp.yml").toURI())).useDelimiter("\\Z").next();

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yml = new Yaml(options);

        Map<String, Object> adpYaml = (HashMap<String, Object>) yml.load(adp);

        adpYaml = DamGenerator.manageTemplateMetada(adpYaml);
        adpYaml = DamGenerator.translateAPD(adpYaml);

        Map groups = (Map) adpYaml.remove(DamGenerator.GROUPS);

        ((Map) adpYaml.get(DamGenerator.TOPOLOGY_TEMPLATE))
                .put(DamGenerator.GROUPS, groups);

        String dam = yml.dump(adpYaml);
        assertNotNull(adpYaml);
    }

    @Test
    public void monitorRuleTest() throws Exception{
        String adp = new Scanner(new File(Resources.getResource("generated_adp.yml").toURI())).useDelimiter("\\Z").next();
        Yaml yml = new Yaml();
        Map<String, Object> adpYaml = (HashMap<String, Object>) yml.load(adp);

        adpYaml = DamGenerator.translateAPD(adpYaml);
        adpYaml = DamGenerator.addMonitorInfo(adp, "127.0.0.1", "8080", "127.0.0.1", "8083");

        String dam = yml.dump(adpYaml);
        Assert.assertNotNull(adpYaml);
    }

    @Test
    public void damTranslation() throws Exception{

        DeployerTypesResolver deployerTypesResolver = null;
        try{
            deployerTypesResolver = new DeployerTypesResolver(Resources
                    .getResource(BROOKLYN_TYPES_MAPPING).toURI().toString());}
        catch(Exception e){
            throw new RuntimeException(e);
        }

        //String adp = new Scanner(new File(Resources.getResource("example_adp.yml").toURI())).useDelimiter("\\Z").next();
        String adp = new Scanner(new File(Resources.getResource("generated_adp.yml").toURI())).useDelimiter("\\Z").next();
        Yaml yml =new Yaml();

        Map<String, Object> damUsedNodeTypes = new HashMap<>();
        ArrayList<Object> groupsToAdd = new ArrayList<>();
        HashMap<String, ArrayList<String>> groups = new HashMap<>();

        Map<String, Map<String, Object>> adpYaml = (Map<String, Map<String, Object>>) yml.load(adp);
        assertNotNull(adpYaml);
        Map<String, Object> ADPgroups = adpYaml.get("groups");

        Map<String, Object> nodeTemplates = (Map<String, Object>) adpYaml.get("topology_template").get("node_templates");
        Map<String, Object> nodeTypes = (Map<String, Object>) adpYaml.get("node_types");
        assertNotNull(nodeTemplates);

        for(String moduleName:nodeTemplates.keySet()){
            Map<String, Object> module = (Map<String, Object>) nodeTemplates.get(moduleName);

            //type replacement
            String moduleType = (String) module.get("type");
            if(nodeTypes.containsKey(moduleType)){
                Map<String, Object> type = (HashMap<String, Object>) nodeTypes.get(moduleType);
                String sourceType = (String) type.get("derived_from");
                String targetType = deployerTypesResolver.resolveNodeType(sourceType);

                if (targetType != null) {
                    module.put("type", targetType);
                    if(deployerTypesResolver.getNodeTypeDefinition(targetType)!=null){
                        damUsedNodeTypes.put(targetType,
                                deployerTypesResolver.getNodeTypeDefinition(targetType));
                    } else {
                        //error
                    }
                } else {
                    damUsedNodeTypes.put(moduleType, nodeTypes.get(moduleType));
                }

                assertNotNull(type);
            }

            if(module.keySet().contains("requirements")){
                ArrayList<Map<String, Object> > requirements = (ArrayList<Map<String, Object> >) module.get("requirements");
                assertNotNull(requirements);
                for(Map<String, Object> req : requirements){
                    if(req.keySet().contains("host")){
                        String host = (String) req.get("host");
                        if(!groups.keySet().contains(host)){
                            groups.put(host, new ArrayList<String>());
                        }
                        groups.get(host).add(moduleName);
                    }
                    req.remove(DamGenerator.INSTANCES_POC);

                }
            }
        }
        adpYaml.put(DamGenerator.NODE_TYPES, damUsedNodeTypes);
        assertNotNull(groups);

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
            p.put("brooklyn.location", location + ":" + region);
            policy.add(p);

            policyGroup.put("policies", policy);

            HashMap<String, Object> finalGroup = new HashMap<>();

            ADPgroups.put("add_brooklyn_location" + blidx++ ,policyGroup);
        }

        String finalDam = yml.dump(adpYaml);
        assertNotNull(finalDam);
    }

    @Test
    public void testManagingTemplateMetadata() throws Exception{
        String adp = new Scanner(new File(Resources.getResource("generated_adp.yml").toURI())).useDelimiter("\\Z").next();

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yml = new Yaml(options);
        Map<String, Object> adpYaml = (HashMap<String, Object>) yml.load(adp);

        adpYaml = DamGenerator.manageTemplateMetada(adpYaml);
        adpYaml = DamGenerator.translateAPD(adpYaml);


        assertNotNull(adpYaml);
        assertNotNull(adpYaml.get(DamGenerator.TEMPLATE_NAME));
        assertTrue(((String) adpYaml.get(DamGenerator.TEMPLATE_NAME)).contains(DamGenerator.TEMPLATE_NAME_PREFIX));

        assertNotNull(adpYaml.get(DamGenerator.TEMPLATE_VERSION));
        assertTrue(((String) adpYaml.get(DamGenerator.TEMPLATE_VERSION)).contains(DamGenerator.DEFAULT_TEMPLATE_VERSION));

        assertNotNull(adpYaml.get(DamGenerator.IMPORTS));
        assertTrue(adpYaml.get(DamGenerator.IMPORTS) instanceof List);
        List imports = (List) adpYaml.get(DamGenerator.IMPORTS);
        assertEquals(imports.size(), 2);
        assertEquals(imports.get(0), DamGenerator.TOSCA_NORMATIVE_TYPES+":"+DamGenerator.TOSCA_NORMATIVE_TYPES_VERSION);
    }

    @Test
    public void testGroupsAsTopologyChild() throws Exception{

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        String adp = new Scanner(new File(Resources.getResource("generated_adp.yml").toURI())).useDelimiter("\\Z").next();
        Yaml yml = new Yaml(options);
        Map<String, Object> adpYaml = (HashMap<String, Object>) yml.load(adp);

        adpYaml = DamGenerator.manageTemplateMetada(adpYaml);
        adpYaml = DamGenerator.translateAPD(adpYaml);
        adpYaml = DamGenerator.addMonitorInfo(yml.dump(adpYaml), "127.0.0.1", "8080", "127.0.0.1", "8083");

        Map groups = (Map) adpYaml.remove(DamGenerator.GROUPS);

        DamGenerator.addPoliciesTypeIfNotPresent(groups);
        ((Map) adpYaml.get(DamGenerator.TOPOLOGY_TEMPLATE)).put(DamGenerator.GROUPS, groups);

        Map<String, Object> topologyGroups =
                (Map<String, Object>) ((Map<String, Object>)adpYaml.get(DamGenerator.TOPOLOGY_TEMPLATE)).get(DamGenerator.GROUPS);

        assertNotNull(topologyGroups);
        assertEquals(topologyGroups.size(), 7);
        assertTrue(topologyGroups.containsKey("operation_www"));
        assertTrue(topologyGroups.containsKey("operation_webservices"));
        assertTrue(topologyGroups.containsKey("operation_db1"));
        assertTrue(topologyGroups.containsKey("add_brooklyn_location_Vultr_64gb_mc_atlanta"));
        assertTrue(topologyGroups.containsKey("add_brooklyn_location_Rapidcloud_io_Asia_HK"));
        assertTrue(topologyGroups.containsKey("add_brooklyn_location_App42_PaaS_America_US"));
        assertTrue(topologyGroups.containsKey("monitoringInformation"));


        String dam = yml.dump(adpYaml);
        Assert.assertNotNull(adpYaml);

    }


}