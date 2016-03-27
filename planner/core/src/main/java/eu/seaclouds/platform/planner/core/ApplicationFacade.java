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


import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringDamGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringInfo;
import eu.seaclouds.platform.planner.core.template.ApplicationMetadata;
import eu.seaclouds.platform.planner.core.template.NodeTemplate;
import eu.seaclouds.platform.planner.core.template.TopologyTemplateFacade;
import org.apache.brooklyn.util.collections.MutableMap;
import org.apache.brooklyn.util.exceptions.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ApplicationFacade {

    static Logger log = LoggerFactory.getLogger(ApplicationFacade.class);

    private final Map<String, Object> adp;
    private final String influxdbPort;
    private final String monitorUrl;
    private final String monitorPort;
    private final String influxdbUrl;
    private Map<String, Object> template;
    private TopologyTemplateFacade topologyTemplate;
    private Map<String, Object> nodeTypes;

    public ApplicationFacade(Map<String, Object> adp, String monitorUrl, String monitorPort, String influxdbUrl, String influxdbPort) {
        this.adp = adp;
        this.template = MutableMap.copyOf(adp);
        this.topologyTemplate = new TopologyTemplateFacade(adp);
        this.monitorUrl = monitorUrl;
        this.monitorPort = monitorPort;
        this.influxdbUrl = influxdbUrl;
        this.influxdbPort = influxdbPort;
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        nodeTypes = (template.get(DamGenerator.NODE_TYPES) != null)
                ? (Map<String, Object>) template.get(DamGenerator.NODE_TYPES)
                : MutableMap.<String, Object>of();
    }

    public void generateDam() {
        ApplicationMetadata applicationMetadata = new ApplicationMetadata(template);
        applicationMetadata.normalizeMetadata();

        this.topologyTemplate = new TopologyTemplateFacade(adp);
        updateCurrentNodeTypes(topologyTemplate.getRequiredNodeTypes());
        updateNodeTemplates();
        addMonitoringInfo();
        //add sla
        //join Platform Elements
        //add SeaCloudsPolicies
        //add location policies
    }

    private void addMonitoringInfo() {
        MonitoringInfo monitoringInfo = generateMonitoringInfo();
        addMonitorInfoToTemplate(monitoringInfo);
        topologyTemplate.updateNoExistNodeTemplate(template);
        updateNodeTemplates();
    }

    public MonitoringInfo generateMonitoringInfo() {
        MonitoringDamGenerator monDamGen;
        monDamGen = new MonitoringDamGenerator(getMonitoringEndpoint(), getInfluxDbEndpoint());
        return monDamGen.generateMonitoringInfo(templateToString());
    }

    public void addMonitorInfoToTemplate(MonitoringInfo monitoringInfo) {
        String generatedApplicationId = UUID.randomUUID().toString();

        //TODO: DELETE this map??
        Map<String, MonitoringInfo> monitoringInfoByApplication = MutableMap.of();
        monitoringInfoByApplication.put(generatedApplicationId, monitoringInfo);

        //TODO: SPLIT in a new method policyGeneration
        HashMap<String, Object> appGroup = new HashMap<>();
        appGroup.put(DamGenerator.MEMBERS, Arrays.asList(DamGenerator.APPLICATION));
        Map<String, Object> policy = new HashMap<>();

        HashMap<String, String> policyProperties = new HashMap<>();
        policyProperties.put(DamGenerator.ID, generatedApplicationId);
        policyProperties.put(DamGenerator.TYPE, DamGenerator.SEACLOUDS_MONITORING_RULES_ID_POLICY);
        policy.put(DamGenerator.MONITORING_RULES_POLICY_NAME, policyProperties);

        ArrayList<Map<String, Object>> policiesList = new ArrayList<>();
        policiesList.add(policy);

        appGroup.put(DamGenerator.POLICIES, policiesList);

        template = (Map<String, Object>) getYamlParser().load(monitoringInfo.getReturnedAdp());
        Map<String, Object> groups = (Map<String, Object>) template.get(DamGenerator.GROUPS);
        groups.put(DamGenerator.MONITOR_INFO_GROUPNAME, appGroup);
    }

    private void updateNodeTemplates() {
        Map<String, Object> transformedNodeTemplates =
                topologyTemplate.getNodeTransformedNodeTemplates();
        setNodeTemplates(transformedNodeTemplates);
    }

    private void updateCurrentNodeTypes(Map<String, Object> newNodeTypes) {
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

    public URL getMonitoringEndpoint() {
        try {
            return new URL("http://" + monitorUrl + ":" + monitorPort + "");
        } catch (MalformedURLException e) {
            Exceptions.propagateIfFatal(e);
            log.warn("Error creating MonitoringEndpoint: http://" + monitorUrl + ":" + monitorPort);
        }
        return null;
    }

    public URL getInfluxDbEndpoint() {
        try {
            return new URL("http://" + influxdbUrl + ":" + influxdbPort + "");

        } catch (MalformedURLException e) {
            Exceptions.propagateIfFatal(e);
            log.warn("Error creating InfluxDbEndpoint: http://" + influxdbUrl + ":" + influxdbPort);
        }
        return null;
    }

    private Yaml getYamlParser() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(options);
    }

    private String getSlaEndpoint() {
        return "";
    }

    public String templateToString() {
        return getYamlParser().dump(template);
    }
}
