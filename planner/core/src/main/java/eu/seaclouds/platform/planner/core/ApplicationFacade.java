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


import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringInfo;
import eu.seaclouds.platform.planner.core.agreements.AgreementGenerator;
import eu.seaclouds.platform.planner.core.decorators.MonitoringInformationDecorator;
import eu.seaclouds.platform.planner.core.template.ApplicationMetadata;
import eu.seaclouds.platform.planner.core.template.NodeTemplate;
import eu.seaclouds.platform.planner.core.template.TopologyTemplateFacade;
import eu.seaclouds.platform.planner.core.utils.YamlParser;
import org.apache.brooklyn.util.collections.MutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ApplicationFacade {

    static Logger log = LoggerFactory.getLogger(ApplicationFacade.class);

    private final Map<String, Object> adp;
    private final DamGeneratorConfigBag configBag;
    private Map<String, Object> template;
    private TopologyTemplateFacade topologyTemplate;
    private Map<String, Object> nodeTypes;
    private AgreementGenerator agreementGenerator;
    private String applicationInfoId;
    private MonitoringInfo monitoringInfo;

    public ApplicationFacade(Map<String, Object> adp, DamGeneratorConfigBag configBag) {
        this.adp = adp;
        this.template = MutableMap.copyOf(adp);
        this.topologyTemplate = new TopologyTemplateFacade(adp);
        this.configBag = configBag;
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        nodeTypes = (template.get(DamGenerator.NODE_TYPES) != null)
                ? (Map<String, Object>) template.get(DamGenerator.NODE_TYPES)
                : MutableMap.<String, Object>of();
        agreementGenerator = new AgreementGenerator(configBag.getSlaEndpoint());
    }

    public void generateDam() {
        ApplicationMetadata applicationMetadata = new ApplicationMetadata(template);
        applicationMetadata.normalizeMetadata();

        this.topologyTemplate = new TopologyTemplateFacade(adp);
        updateNodeTypes(topologyTemplate.getRequiredNodeTypes());
        updateNodeTemplates();

        applyDecorators();
        //addMonitoringInfo();
        addSlaInformation();

        //TODO:join Platform Elements

        addPoliciesLocations();

        //TODO:add SeaCloudsPolicies
    }

    private void applyDecorators() {
        MonitoringInformationDecorator monitoringInformationDecorator =
                new MonitoringInformationDecorator();
        monitoringInformationDecorator.apply(this);
    }

    private void addPoliciesLocations() {
        Map<String, Object> locationGroups = topologyTemplate.getLocationPoliciesGroups();
        addGroupds(locationGroups);
    }

    private void addGroupds(Map<String, Object> groups) {
        for (Map.Entry<String, Object> groupEntry : groups.entrySet()) {
            String groupName = groupEntry.getKey();
            Map<String, Object> groupValue = (Map<String, Object>) groupEntry.getValue();
            addGroup(groupName, groupValue);
        }
    }

    public void addMonitoringInfo(MonitoringInfo monitoringInfo) {
        this.monitoringInfo = monitoringInfo;
        template = (Map<String, Object>) YamlParser.getYamlParser().load(monitoringInfo.getReturnedAdp());
        //addMonitorInfoToTemplate(monitoringInfo);
        topologyTemplate.updateNoExistNodeTemplate(template);
        updateNodeTemplates();
    }

    private void addSlaInformation() {
        applicationInfoId = getAgreementGenerator().generateAgreeemntId(template);
        addApplicationInfo(applicationInfoId);
    }


    public void addApplicationInfo(String applicationInfoId) {
        Map<String, Object> groups = (Map<String, Object>) template.get(DamGenerator.GROUPS);
        HashMap<String, Object> appGroup = new HashMap<>();
        appGroup.put(DamGenerator.MEMBERS, Arrays.asList(DamGenerator.APPLICATION));

        //TODO: split in some methods or objects
        Map<String, Object> policy = new HashMap<>();
        HashMap<String, String> policyProperties = new HashMap<>();
        policyProperties.put(DamGenerator.ID, applicationInfoId);
        policyProperties.put(DamGenerator.TYPE, DamGenerator.SEACLOUDS_APPLICATION_INFORMATION_POLICY_TYPE);
        policy.put(DamGenerator.SEACLOUDS_APPLICATION_POLICY_NAME, policyProperties);

        ArrayList<Map<String, Object>> policiesList = new ArrayList<>();
        policiesList.add(policy);

        appGroup.put(DamGenerator.POLICIES, policiesList);
        groups.put(DamGenerator.SLA_INFO_GROUPNAME, appGroup);
    }


    private void updateNodeTemplates() {
        Map<String, Object> transformedNodeTemplates =
                topologyTemplate.getNodeTransformedNodeTemplates();
        setNodeTemplates(transformedNodeTemplates);
    }

    private void updateNodeTypes(Map<String, Object> newNodeTypes) {
        Map<String, Object> currentNodeTypes = getNodeTypes();
        Map<String, NodeTemplate> nodeTemplates = topologyTemplate.getNodeTemplates();
        Map<String, Object> usedNodeTemplates = MutableMap.of();

        for (Map.Entry<String, NodeTemplate> nodeTemplateEntry : nodeTemplates.entrySet()) {

            NodeTemplate nodeTemplate = nodeTemplateEntry.getValue();
            String moduleType = nodeTemplate.getModuleType();

            //TODO: Split
            if (currentNodeTypes.containsKey(moduleType)) {
                String targetType = nodeTemplate.getType();
                if (targetType != null) {
                    if (nodeTemplate.getNodeTypeDefinition() != null) {
                        //TODO probably it could be added directly
                        usedNodeTemplates.put(targetType,
                                nodeTemplate.getNodeTypeDefinition());
                    } else {
                        log.error("TargetType definition " + targetType + "was not found" +
                                "so it will not added to DAM");
                    }
                } else {
                    usedNodeTemplates.put(moduleType, newNodeTypes.get(moduleType));
                }
            }
        }
        setNodeTypes(usedNodeTemplates);
    }

    private void setNodeTypes(Map<String, Object> nodeTypes) {
        this.nodeTypes = nodeTypes;
        template.put(DamGenerator.NODE_TYPES, nodeTypes);
    }

    private Map<String, Object> getNodeTypes() {
        return nodeTypes;
    }

    private void setNodeTemplates(Map<String, Object> nodeTemplates) {
        Map<String, Object> topologyTem =
                (Map<String, Object>) template.get(DamGenerator.TOPOLOGY_TEMPLATE);
        topologyTem.put(DamGenerator.NODE_TEMPLATES, nodeTemplates);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getNodeTemplates() {
        Map<String, Object> topologyTemplate = (Map<String, Object>) template.get(DamGenerator.TOPOLOGY_TEMPLATE);
        return (Map<String, Object>) topologyTemplate.get(DamGenerator.NODE_TEMPLATES);
    }

    public String templateToString() {
        return YamlParser.getYamlParser().dump(template);
    }

    private AgreementGenerator getAgreementGenerator() {
        return agreementGenerator;
    }

    public void setAgreementGenerator(AgreementGenerator agreementGenerator) {
        this.agreementGenerator = agreementGenerator;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getGroups() {
        return (Map<String, Object>) template.get(DamGenerator.GROUPS);
    }

    public void addGroup(String groupName, Map<String, Object> groupValue) {
        getGroups().put(groupName, groupValue);
    }

    public DamGeneratorConfigBag getConfigBag() {
        return configBag;
    }


}
