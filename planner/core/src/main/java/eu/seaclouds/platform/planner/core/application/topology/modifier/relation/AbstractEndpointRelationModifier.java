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
package eu.seaclouds.platform.planner.core.application.topology.modifier.relation;

import com.google.common.collect.ImmutableList;
import eu.seaclouds.platform.planner.core.DamGenerator;

import java.util.List;
import java.util.Map;

public abstract class AbstractEndpointRelationModifier extends AbstractRelationModifier {

    public static final String SUPPORTED_RELATIONS = "seaclouds.relation.connection.endpoint.host";
    private static final String RELATION_NAME = "endpoint_configuration";
    private static final String POINTED_ATTRIBUTE = "webapp.url";

    @Override
    protected List<String> getSupportedRelationTypes() {
        return ImmutableList.of(SUPPORTED_RELATIONS);
    }

    @Override
    protected boolean isValidTargetNode(Map<String, Object> requirement) {
        return true;
    }

    @Override
    protected String getPropValue(Map<String, Object> requirement) {
        return createPropertyValue(requirement);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected String getPropName(Map<String, Object> requirement) {
        Map<String, Object> properties =
                (Map<String, Object>) getRequirementValues(requirement)
                        .get(DamGenerator.PROPERTIES);
        return (String) properties.get(PROP_NAME);
    }

    @Override
    protected String getRelationName() {
        return RELATION_NAME;
    }

    @SuppressWarnings("unchecked")
    private String createPropertyValue(Map<String, Object> requirement) {
        String targetId = getTargetNodeId(requirement);
        return "$brooklyn:component(\"" + targetId + "\")" +
                ".attributeWhenReady(\"" + POINTED_ATTRIBUTE + "\")";
    }

}
