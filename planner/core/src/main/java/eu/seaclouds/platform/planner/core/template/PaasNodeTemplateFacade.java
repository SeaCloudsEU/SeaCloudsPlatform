package eu.seaclouds.platform.planner.core.template;

import eu.seaclouds.platform.planner.core.template.host.HostNodeTemplate;
import eu.seaclouds.platform.planner.core.template.host.PlatformNodeTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PaasNodeTemplateFacade implements HostNodeTemplate {

    private final AbstractNodeTemplate nodeTemplate;
    private final PlatformNodeTemplate platformNodeTemplate;

    public PaasNodeTemplateFacade(AbstractNodeTemplate hostedNodeTemplate, PlatformNodeTemplate platform) {
        this.nodeTemplate = hostedNodeTemplate;
        this.platformNodeTemplate = platform;
        init();
    }

    private void init() {
        nodeTemplate.deleteHostRequirement();
    }

    @Override
    public String getNodeTemplateId() {
        return nodeTemplate.getNodeTemplateId();
    }

    @Override
    public Map<String, Object> getLocationPolicyGroupValues() {
        Map<String, Object> locationGroupValues =
                platformNodeTemplate.getLocationPolicyGroupValues();
        locationGroupValues.put(MEMBERS, Arrays.asList(getNodeTemplateId()));
        return locationGroupValues;
    }

    @Override
    public String getLocationPolicyGroupName() {
        return ADD_BROOKLYN_LOCATION_PEFIX + getNodeTemplateId();
    }

    @Override
    public Map<String, Object> transform() {
        return nodeTemplate.transform();
    }

    @Override
    public boolean isDeployedOnIaaS() {
        return false;
    }

    @Override
    public Map<String, Object> getNodeTypeDefinition() {
        return nodeTemplate.getNodeTypeDefinition();
    }

    @Override
    public String getModuleType() {
        return nodeTemplate.getModuleType();
    }

    @Override
    public String getType() {
        return nodeTemplate.getType();
    }

    @Override
    public String getHostNodeName() {
        return null;
    }

    @Override
    public List<Map<String, Object>> getArtifacts() {
        return nodeTemplate.getArtifacts();
    }


}
