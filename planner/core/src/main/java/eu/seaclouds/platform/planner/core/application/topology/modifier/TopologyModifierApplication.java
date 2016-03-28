package eu.seaclouds.platform.planner.core.application.topology.modifier;


import eu.seaclouds.platform.planner.core.application.topology.TopologyTemplateFacade;
import eu.seaclouds.platform.planner.core.application.topology.nodetemplate.NodeTemplate;

public interface TopologyModifierApplication {

    void applyModifiers(NodeTemplate nodeTemplate, TopologyTemplateFacade topologyTemplate);
}
