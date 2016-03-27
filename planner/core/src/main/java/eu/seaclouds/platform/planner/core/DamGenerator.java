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
package eu.seaclouds.platform.planner.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import eu.seaclouds.platform.planner.core.agreements.AgreementGenerator;
import eu.seaclouds.platform.planner.core.resolver.DeployerTypesResolver;
import eu.seaclouds.platform.planner.core.utils.YamlParser;
import org.apache.brooklyn.util.collections.MutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;


public class DamGenerator {

    static Logger log = LoggerFactory.getLogger(DamGenerator.class);

    public static final String SLA_INFO_GROUPNAME = "sla_gen_info";
    public static final String MONITOR_INFO_GROUPNAME = "monitoringInformation";

    public static final String TYPE = "type";
    public static final String POLICIES = "policies";
    public static final String GROUPS = "groups";
    public static final String MEMBERS = "members";
    public static final String ID = "id";
    public static final String APPLICATION = "application";
    public static final String TOPOLOGY_TEMPLATE = "topology_template";
    public static final String NODE_TEMPLATES = "node_templates";
    public static final String NODE_TYPES = "node_types";
    public static final String PROPERTIES = "properties";
    public static final String BROOKLYN_IAAS_TYPES_MAPPING = "mapping/brooklyn-iaas-types-mapping.yaml";
    public static final String BROOKLYN_PAAS_TYPES_MAPPING = "mapping/brooklyn-paas-types-mapping.yaml";
    public static final String BROOKLYN_POLICY_TYPE = "brooklyn.location";


    public static final String SEACLOUDS_MONITORING_RULES_ID_POLICY = "seaclouds.policies.monitoringrules";
    public static final String MONITORING_RULES_POLICY_NAME = "monitoringrules.information.policy";
    public static final String SEACLOUDS_APPLICATION_INFORMATION_POLICY_TYPE = "seaclouds.policies.app.information";
    public static final String SEACLOUDS_APPLICATION_POLICY_NAME = "seaclouds.app.information";
    public static final String SEACLOUDS_NODE_PREFIX = "seaclouds.nodes";

    public static final String SEACLOUDS_APPLICATION_CONFIGURATION =
            "seaclouds_configuration_policy";

    public static final String REQUIREMENTS = "requirements";
    public static final String HOST = "host";
    private final DamGeneratorConfigBag configBag;

    private Map<String, Object> template;
    private Map<String, Object> originalAdp;
    private AgreementGenerator agreementGenerator;

    public DamGenerator(DamGeneratorConfigBag configBag) {
        this.configBag = configBag;
        init();
    }

    private void init() {
        agreementGenerator = new AgreementGenerator(configBag.getSlaEndpoint());
    }

    public String generateDam(String adp) {
        originalAdp =
                normalizeComputeTypes((Map<String, Object>) YamlParser.getYamlParser().load(adp));

        ApplicationFacade applicationFacade = new ApplicationFacade(originalAdp, configBag);
        applicationFacade.setAgreementGenerator(agreementGenerator);
        applicationFacade.generateDam();
        String generatedDam = applicationFacade.templateToString();
        template = (Map<String, Object>) YamlParser.getYamlParser().load(generatedDam);

        customize();

        return YamlParser.getYamlParser().dump(template);
    }

    private void customize() {
        normalizePlatformNodeTemplates();
        relationManagement();
        manageNodeTypes();
        manageGroups();
    }

    private void manageNodeTypes() {
        if (template.containsKey(NODE_TYPES)) {
            template.remove(NODE_TYPES);
        }
    }

    private Map<String, Object> normalizeComputeTypes(Map<String, Object> adpTemplate) {
        Map<String, Object> topologyTemplate = (Map<String, Object>) adpTemplate.get(TOPOLOGY_TEMPLATE);
        Map<String, Object> nodeTemplates = (Map<String, Object>) topologyTemplate.get(NODE_TEMPLATES);
        //Solve offerings Types issue
        for (Map.Entry<String, Object> nodeTemplateEntry : nodeTemplates.entrySet()) {
            Map<String, Object> nodeTemplate = (Map<String, Object>) nodeTemplateEntry.getValue();
            String nodeTemplateType = (String) nodeTemplate.get(TYPE);
            if (nodeTemplateType.contains("seaclouds.nodes.Compute")) {
                nodeTemplate.put(TYPE, "seaclouds.nodes.Compute");
            }
        }
        return adpTemplate;
    }

    @SuppressWarnings("unchecked")
    private void manageGroups() {
        Map groups = (Map) template.remove(GROUPS);
        ((Map) template.get(TOPOLOGY_TEMPLATE)).put(GROUPS, groups);
    }

    private void normalizePlatformNodeTemplates() {
        //TODO: refactoring
//        Map<String, Object> topologyTemplate = (Map<String, Object>) template.get(TOPOLOGY_TEMPLATE);
//        Map<String, Object> nodeTemplates = (Map<String, Object>) topologyTemplate.get(NODE_TEMPLATES);
//
//        Map<String, Object> groups = (Map<String, Object>) template.get(GROUPS);
//
//        for (Map.Entry<String, HostNodeTemplate> entry : hostNodeTemplateFacades.entrySet()) {
//            HostNodeTemplate hostNodeTemplate = entry.getValue();
//            if (hostNodeTemplate instanceof PlatformNodeTemplate) {
//                nodeTemplates.remove(entry.getKey());
//                String platformPolicyGroupName =
//                        hostNodeTemplate.getLocationPolicyGroupName();
//
//                Map<String, Object> platformPolicyGroupValues =
//                        (Map<String, Object>) groups.get(platformPolicyGroupName);
//
//                ArrayList<String> nodeTemplatesDeployedOnPlatform = topologyTree.get(entry.getKey());
//                if (nodeTemplatesDeployedOnPlatform.size() != 1) {
//                    throw new IllegalStateException("just one NodeTemplate can be deployed " +
//                            "on a PlatformNodeTemplate and " + entry.getKey() + " contains " +
//                            nodeTemplatesDeployedOnPlatform.size() + ": " +
//                            nodeTemplatesDeployedOnPlatform.toString());
//                }
//                String nodeTemplateDeployedPlatform = nodeTemplatesDeployedOnPlatform.get(0);
//
//                platformPolicyGroupValues.put(MEMBERS, Arrays.asList(nodeTemplateDeployedPlatform));
//                groups.remove(platformPolicyGroupName);
//                groups.put(HostNodeTemplate.ADD_BROOKLYN_LOCATION_PEFIX + nodeTemplateDeployedPlatform, platformPolicyGroupValues);
//
//                nodeTemplates.get(nodeTemplateDeployedPlatform);
//                AbstractNodeTemplate nodeTemplate = (AbstractNodeTemplate) nodeTemplateFacades.get(nodeTemplateDeployedPlatform);
//                nodeTemplate.deleteHostRequirement();
//                nodeTemplates.put(nodeTemplateDeployedPlatform, nodeTemplate.transform());
//            }
//        }
    }

    /*This method filters current requirements and avoid non-host requirements. It is only a temporal
    * fix. Relations have to be managed ASAP*/
    public void relationManagement() {
        Map<String, Object> topologyTemplate = (Map<String, Object>) template.get(TOPOLOGY_TEMPLATE);
        Map<String, Object> nodeTemplates = (Map<String, Object>) topologyTemplate.get(NODE_TEMPLATES);

        for (Map.Entry<String, Object> nodeTemplateEntry : nodeTemplates.entrySet()) {

            Map<String, Object> nodeTemplate = (Map<String, Object>) nodeTemplateEntry.getValue();

            List<Map<String, Object>> requirements = (List<Map<String, Object>>) nodeTemplate.get(REQUIREMENTS);
            List<Map<String, Object>> fixedRequirements = MutableList.of();
            if (requirements != null) {
                for (Map<String, Object> requirement : requirements) {
                    if (requirement.containsKey(HOST)) {
                        fixedRequirements.add(requirement);
                    }
                }
                if (fixedRequirements.isEmpty()) {
                    nodeTemplate.remove(REQUIREMENTS);
                } else {
                    nodeTemplate.put(REQUIREMENTS, fixedRequirements);
                }
            }
        }
    }


    public void setAgreementGenerator(AgreementGenerator agreementGenerator) {
        this.agreementGenerator = agreementGenerator;
    }

}
