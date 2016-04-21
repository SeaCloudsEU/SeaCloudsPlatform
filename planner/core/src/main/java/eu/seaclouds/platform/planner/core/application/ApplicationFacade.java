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
package eu.seaclouds.platform.planner.core.application;


import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringInfo;
import eu.seaclouds.platform.planner.core.DamGenerator;
import eu.seaclouds.platform.planner.core.DamGeneratorConfigBag;
import eu.seaclouds.platform.planner.core.application.agreements.AgreementGenerator;
import eu.seaclouds.platform.planner.core.application.topology.TopologyTemplateFacade;
import eu.seaclouds.platform.planner.core.application.topology.modifier.relation.TopologFacadeyModifierApplicator;
import eu.seaclouds.platform.planner.core.application.topology.nodetemplate.NodeTemplate;
import eu.seaclouds.platform.planner.core.utils.YamlParser;
import org.apache.brooklyn.util.collections.MutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ApplicationFacade {

    static Logger log = LoggerFactory.getLogger(ApplicationFacade.class);

    private final Map<String, Object> adp;
    private final DamGeneratorConfigBag configBag;
    private Map<String, Object> template;
    private TopologyTemplateFacade topologyTemplate;
    private Map<String, Object> nodeTypes;
    private AgreementGenerator agreementGenerator;
    private String applicationSlaId;
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


    public void addGroup(String groupName, Map<String, Object> groupValue) {
        getGroups().put(groupName, groupValue);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getGroups() {
        return (Map<String, Object>) template.get(DamGenerator.GROUPS);
    }

    public void addSlaInformation(String agreementId) {
        this.applicationSlaId = agreementId;
    }

    public DamGeneratorConfigBag getConfigBag() {
        return configBag;
    }

    public AgreementGenerator getAgreementGenerator() {
        return agreementGenerator;
    }

    public void setAgreementGenerator(AgreementGenerator agreementGenerator) {
        this.agreementGenerator = agreementGenerator;
    }

    public MonitoringInfo getMonitoringInfo() {
        return monitoringInfo;
    }

    public void addMonitoringInfo(MonitoringInfo monitoringInfo) {
        this.monitoringInfo = monitoringInfo;
        template = YamlParser.load(monitoringInfo.getReturnedAdp());
        topologyTemplate.updateNodeTemplates(template);
        updateNodeTemplates();
    }

    public String getApplicationSlaId() {
        return applicationSlaId;
    }

    public String templateToString() {
        return YamlParser.dump(template);
    }

    public void generateDam() {
        normalizeMetadata();
        createTopologyTemplate();
        applyDecorators();
        applyTopologyModifiers();
        joinPlatformsAndHostedNodeTemplates();
        addPoliciesLocations();
    }

    private void normalizeMetadata() {
        ApplicationMetadataGenerator applicationMetadata = new ApplicationMetadataGenerator(template);
        applicationMetadata.addMetadataMetadataToTemplate();
    }

    private void createTopologyTemplate() {
        this.topologyTemplate = new TopologyTemplateFacade(adp);
        updateTypesAndTemplates();
    }

    private void updateTypesAndTemplates() {
        updateNodeTypes(topologyTemplate.getRequiredNodeTypes());
        updateNodeTemplates();
    }

    private void joinPlatformsAndHostedNodeTemplates() {
        topologyTemplate.joinPlatformNodeTemplates();
        updateNodeTemplates();
    }

    private void applyDecorators() {
        ApplicationFacadeDecoratorApplicator applicator =
                new ApplicationFacadeDecoratorApplicator();
        applicator.applyDecorators(this);
    }

    private void applyTopologyModifiers() {
        topologyTemplate.applyModifierApplicator(new TopologFacadeyModifierApplicator());
    }

    private void addPoliciesLocations() {
        Map<String, Object> locationGroups = topologyTemplate.getLocationPoliciesGroups();
        addGroups(locationGroups);
    }

    private void updateNodeTemplates() {
        Map<String, Object> transformedNodeTemplates =
                topologyTemplate.getNodeTransformedNodeTemplates();
        setNodeTemplates(transformedNodeTemplates);
    }

    private Map<String, Object> getNodeTypes() {
        return nodeTypes;
    }

    @SuppressWarnings("unchecked")
    private void setNodeTemplates(Map<String, Object> nodeTemplates) {
        Map<String, Object> topologyTem =
                (Map<String, Object>) template.get(DamGenerator.TOPOLOGY_TEMPLATE);
        topologyTem.put(DamGenerator.NODE_TEMPLATES, nodeTemplates);
    }

    private void addGroups(Map<String, Object> groups) {
        for (Map.Entry<String, Object> groupEntry : groups.entrySet()) {
            String groupName = groupEntry.getKey();
            Map<String, Object> groupValue = (Map<String, Object>) groupEntry.getValue();
            addGroup(groupName, groupValue);
        }
    }

    private void setNodeTypes(Map<String, Object> nodeTypes) {
        this.nodeTypes = nodeTypes;
        template.put(DamGenerator.NODE_TYPES, nodeTypes);
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

}
