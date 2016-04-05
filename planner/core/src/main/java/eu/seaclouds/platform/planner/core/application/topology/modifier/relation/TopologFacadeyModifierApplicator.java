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
import eu.seaclouds.platform.planner.core.application.topology.TopologyTemplateFacade;
import eu.seaclouds.platform.planner.core.application.topology.nodetemplate.NodeTemplate;

import java.util.List;

public class TopologFacadeyModifierApplicator implements TopologyModifierApplication{

    @Override
    public void applyModifiers(NodeTemplate nodeTemplate, TopologyTemplateFacade topologyTemplate) {
        for (TopologyTemplateModifier modifier : getTopologyModifiers()) {
            modifier.apply(nodeTemplate, topologyTemplate);
        }
    }

    private List<TopologyTemplateModifier> getTopologyModifiers() {
        return ImmutableList.of(
                (TopologyTemplateModifier) new JdbcIaasRelationModifier(),
                (TopologyTemplateModifier) new JdbcPaasRelationModifier(),
                (TopologyTemplateModifier) new EndpointIaasRelationModifier(),
                (TopologyTemplateModifier) new EndpointPaasRelationModifier(),
                (TopologyTemplateModifier) new PhpDatabaseIaaSRelationModifier()
        );
    }

}
