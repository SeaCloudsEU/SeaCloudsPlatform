package eu.seaclouds.platform.planner.core.template.datacollector;

import eu.seaclouds.platform.planner.core.template.AbstractNodeTemplate;
import org.apache.brooklyn.util.collections.MutableMap;

import java.util.Map;

public class Datacollector extends AbstractNodeTemplate {

    private static final String SUPPORTED_TYPES = "seaclouds.nodes.Datacollector";
    private static final String INTERFACES = "interfaces";

    private Map<String, Object> interfaces;

    public Datacollector(Map<String, Object> applicationTemplate, String nodeTemplateId) {
        super(applicationTemplate, nodeTemplateId);
    }

    @Override
    protected void init() {
        super.init();
        if (module.get(INTERFACES) != null) {
            interfaces = ((Map<String, Object>) module.get(INTERFACES));
        } else {
            interfaces = MutableMap.of();
        }
    }

    @Override
    public Map<String, Object> transform() {
        Map<String, Object> transformedNodeTemplate = super.transform();
        if (!interfaces.isEmpty()) {
            transformedNodeTemplate.put(INTERFACES, interfaces);
        }
        return transformedNodeTemplate;
    }

    public static boolean isSupported(String type) {
        return SUPPORTED_TYPES.equals(type);
    }

    @Override
    public String getType() {
        return getModuleType();
    }


}
