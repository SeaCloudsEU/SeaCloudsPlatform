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
package eu.seaclouds.platform.planner.core.application.topology.modifier;


import com.google.common.collect.Iterators;
import eu.seaclouds.platform.planner.core.DamGenerator;
import eu.seaclouds.platform.planner.core.application.topology.TopologyTemplateFacade;
import eu.seaclouds.platform.planner.core.application.topology.nodetemplate.NodeTemplate;
import org.apache.brooklyn.util.collections.MutableList;
import org.apache.brooklyn.util.collections.MutableMap;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

public abstract class AbstractRelationModifier implements TopologyTemplateModifier {

    public static final String NODE = "node";
    public static final String RELATIONSHIP = "relationship";
    public static final String PROP_COLLECTION = "prop.collection";
    public static final String PROP_NAME = "prop.name";
    public static final String PROP_VALUE = "prop.value";
    public static final String RELATION_CONFIGURE = "brooklyn.relationships.Configure";

    protected TopologyTemplateFacade topologyTemplate;
    protected NodeTemplate nodeTemplate;

    @Override
    public void apply(NodeTemplate nodeTemplate, TopologyTemplateFacade topologyTemplateFacade) {
        this.topologyTemplate = topologyTemplateFacade;
        this.nodeTemplate = nodeTemplate;
        if (nodeTemplateCanBeModified(nodeTemplate)) {
            applyTransformation(nodeTemplate, topologyTemplateFacade);
        }
    }

    protected void applyTransformation(NodeTemplate nodeTemplate, TopologyTemplateFacade topologyTemplateFacade) {
        List<Map<String, Object>> requirements = findTargetRelations(nodeTemplate);
        List<Map<String, Object>> transformedRequirements = transformRequirements(requirements);
        deleteRequirements(nodeTemplate, requirements);
        nodeTemplate.getRequirements().addAll(transformedRequirements);
    }

    protected List<Map<String, Object>> transformRequirements(List<Map<String, Object>> requirements) {
        List<Map<String, Object>> transformedRequirements = MutableList.of();
        for (Map<String, Object> requirement : requirements) {
            transformedRequirements.add(transformRequirement(requirement));
        }
        return transformedRequirements;
    }

    protected Map<String, Object> transformRequirement(Map<String, Object> requirement) {
        Map<String, Object> transformedRequirement = MutableMap.of();
        transformedRequirement.put(
                getRelationName(),
                getTransformRequirementValues(requirement));

        return transformedRequirement;
    }

    protected Map<String, Object> getTransformRequirementValues(Map<String, Object> requirement) {
        Map<String, Object> transformedRequirementValues = MutableMap.of();
        transformedRequirementValues.put(NODE, getTargetNodeId(requirement));
        transformedRequirementValues.put(RELATIONSHIP, RELATION_CONFIGURE);
        transformedRequirementValues.put(DamGenerator.PROPERTIES,
                getRelationPropertiesValues(requirement));
        return transformedRequirementValues;
    }

    protected Map<String, Object> getRelationPropertiesValues(Map<String, Object> requirement) {
        Map<String, Object> properties = MutableMap.of();
        properties.put(PROP_COLLECTION, getPropCollection(requirement));
        properties.put(PROP_NAME, getPropName(requirement));
        properties.put(PROP_VALUE, getPropValue(requirement));
        return properties;
    }

    protected final String getNodeTargetType(Map<String, Object> requirement) {
        return topologyTemplate.getNodeTypeOf(getTargetNodeId(requirement));
    }

    protected List<Map<String, Object>> findTargetRelations(NodeTemplate nodeTemplate) {
        List<Map<String, Object>> requirements = nodeTemplate.getRequirements();
        List<Map<String, Object>> foundRelations = MutableList.of();

        for (Map<String, Object> requirement : requirements) {
            if (canBeTransformed(requirement)) {
                foundRelations.add(requirement);
            }
        }
        return foundRelations;
    }

    private boolean canBeTransformed(Map<String, Object> requirement) {
        return !isHostRequirement(requirement)
                && isSupportedRelation(requirement)
                && isValidTargetNode(requirement);
    }

    private boolean isSupportedRelation(Map<String, Object> requirement) {
        return getSupportedRelationTypes().contains(getRequirmentType(requirement));
    }

    private void deleteRequirements(NodeTemplate nodeTemplate, List<Map<String, Object>> requirements) {
        for (Map<String, Object> requirement : requirements) {
            nodeTemplate.getRequirements().remove(requirement);
        }
    }

    private boolean isHostRequirement(Map<String, Object> requirement) {
        return requirement.containsKey(DamGenerator.HOST);
    }

    protected Map<String, Object> getRequirementValues(Map<String, Object> requirement) {
        if (isHostRequirement(requirement)) {
            return requirement;
        } else {
            return (Map<String, Object>) requirement.get(getRequirementId(requirement));
        }
    }

    private String getRequirementId(Map<String, Object> requirement) {
        if (requirement.containsKey(DamGenerator.HOST)) {
            return (String) requirement.get(DamGenerator.HOST);
        } else {
            return getNotHostRequirement(requirement);
        }
    }

    private String getNotHostRequirement(Map<String, Object> requirement) {
        checkState(requirement.size() == 1, "Not valid Requirement, too properties " +
                "for non-Host requirement. Expected a map with just an entry");
        return Iterators.getOnlyElement(requirement.keySet().iterator());
    }

    protected String getTargetNodeId(Map<String, Object> requirement) {
        return (String) getRequirementValues(requirement).get(NODE);
    }

    private String getRequirmentType(Map<String, Object> requirement) {
        return (String) getRequirementValues(requirement).get(DamGenerator.TYPE);
    }

    protected abstract boolean nodeTemplateCanBeModified(NodeTemplate nodeTemplate);

    protected abstract List<String> getSupportedRelationTypes();

    protected abstract boolean isValidTargetNode(Map<String, Object> requirement);

    protected abstract String getPropValue(Map<String, Object> requirementValues);

    protected abstract String getPropName(Map<String, Object> requirementValues);

    protected abstract String getPropCollection(Map<String, Object> requirementValues);

    protected abstract String getRelationName();


}
