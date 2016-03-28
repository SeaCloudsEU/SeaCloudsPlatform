package eu.seaclouds.platform.planner.core.application.topology.modifier;


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
        return ImmutableList.of((TopologyTemplateModifier) new JdbcIaasRelationModifier());
    }

}
