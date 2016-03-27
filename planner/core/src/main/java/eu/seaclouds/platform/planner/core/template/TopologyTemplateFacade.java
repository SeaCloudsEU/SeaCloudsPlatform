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
package eu.seaclouds.platform.planner.core.template;


import eu.seaclouds.platform.planner.core.DamGenerator;
import eu.seaclouds.platform.planner.core.template.host.HostNodeTemplate;
import org.apache.brooklyn.util.collections.MutableList;
import org.apache.brooklyn.util.collections.MutableMap;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class TopologyTemplateFacade {

    private final Map<String, Object> originalAdp;
    private Map<String, Object> topologyTemplate;
    private Map<String, NodeTemplate> nodeTemplates;
    private Map<String, HostNodeTemplate> hostNodeTemplates;
    private Map<String, List<NodeTemplate>> topologyTree;
    private Map<String, Object> originalNodeTemplates;

    public TopologyTemplateFacade(Map<String, Object> adp) {
        this.originalAdp = MutableMap.copyOf(adp);
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        initOriginalTopologyTemplate();
        initOriginalNodesTemplates();

        nodeTemplates = MutableMap.of();
        hostNodeTemplates = MutableMap.of();
        topologyTree = MutableMap.of();
        initNodeTemplates();

    }

    private void initOriginalTopologyTemplate() {
        checkNotNull(originalAdp.get(DamGenerator.TOPOLOGY_TEMPLATE),
                "TopologyTemplate has to a topology_template element");
        topologyTemplate = (Map<String, Object>) originalAdp.get(DamGenerator.TOPOLOGY_TEMPLATE);
    }

    private void initOriginalNodesTemplates() {
        checkNotNull(topologyTemplate.get(DamGenerator.NODE_TEMPLATES),
                "TopologyTemplate has to contain NodeTemplates");
        originalNodeTemplates = (Map<String, Object>) topologyTemplate.get(DamGenerator.NODE_TEMPLATES);
    }

    private void initNodeTemplates() {
        for (String nodeTemplateId : originalNodeTemplates.keySet()) {
            NodeTemplate nodeTemplate =
                    NodeTemplateFactory.createNodeTemplate(originalAdp, nodeTemplateId);
            addNodeTemplate(nodeTemplateId, nodeTemplate);
        }
    }

    private void addNodeTemplate(String nodeTemplateId, NodeTemplate nodeTemplate) {
        nodeTemplates.put(nodeTemplateId, nodeTemplate);

        if (nodeTemplate instanceof HostNodeTemplate) {
            hostNodeTemplates
                    .put(nodeTemplateId, (HostNodeTemplate) nodeTemplate);
        } else {
            String hostNodeTemplateName = nodeTemplate.getHostNodeName();
            if (!topologyTree.containsKey(hostNodeTemplateName)) {
                topologyTree.put(hostNodeTemplateName, MutableList.<NodeTemplate>of());
            }
            topologyTree.get(hostNodeTemplateName).add(nodeTemplate);
        }
    }

    public Map<String, Object> getRequiredNodeTypes() {
        Map<String, Object> usedNodeTypes = MutableMap.of();
        for (Map.Entry<String, NodeTemplate> nodeTemplateEntry : nodeTemplates.entrySet()) {
            NodeTemplate nodeTemplate = nodeTemplateEntry.getValue();
            Map<String, Object> nodeType = nodeTemplate.getNodeTypeDefinition();
            if (nodeType != null) {
                usedNodeTypes.put(nodeTemplate.getType(), nodeTemplate.getNodeTypeDefinition());
            }
        }
        return usedNodeTypes;
    }

    public Map<String, Object> getNodeTransformedNodeTemplates() {
        Map<String, Object> transformedNodeTemplates = MutableMap.of();
        for (Map.Entry<String, NodeTemplate> nodeTemplateEntry : nodeTemplates.entrySet()) {
            transformedNodeTemplates
                    .put(nodeTemplateEntry.getKey(), nodeTemplateEntry.getValue().transform());
        }
        return transformedNodeTemplates;
    }

    public Map<String, NodeTemplate> getNodeTemplates() {
        return nodeTemplates;
    }

    private boolean contained(String nodeTemplateId) {
        return nodeTemplates.containsKey(nodeTemplateId);
    }

    public void updateNoExistNodeTemplate(Map<String, Object> adp) {
        //TODO: it could be better create a new Template
        topologyTemplate = (Map<String, Object>) adp.get(DamGenerator.TOPOLOGY_TEMPLATE);
        originalNodeTemplates = (Map<String, Object>) topologyTemplate.get(DamGenerator.NODE_TEMPLATES);

        for (Map.Entry<String, Object> newNodeTemplate : originalNodeTemplates.entrySet()) {
            String nodeTemplateId = newNodeTemplate.getKey();
            if (!contained(newNodeTemplate.getKey())) {
                NodeTemplate nodeTemplate =
                        NodeTemplateFactory.createNodeTemplate(adp, nodeTemplateId);
                addNodeTemplate(nodeTemplateId, nodeTemplate);
            }
        }

    }
}
