package eu.seaclouds.platform.planner.core.application.topology.nodetemplate.modifiers;


import eu.seaclouds.platform.planner.core.application.topology.nodetemplate.NodeTemplate;
import org.apache.brooklyn.util.collections.MutableMap;

import java.util.Map;

public class IaasJavaEnvVariablesModifier implements NodeTemplateFacadeModifier {

    private static final String LANGUAGE = "language";
    private static final String ENV_PROPERTY = "env";
    private static final String JAVA_SYS_PROP = "java.sysprops";
    private static final String JAVA = "JAVA";

    private boolean isApplicable(NodeTemplate nodeTemplate) {
        String languajeProperty = (String)nodeTemplate.getPropertyValue(LANGUAGE);
        return ((languajeProperty != null)
                && (nodeTemplate.isDeployedOnIaaS())
                && (languajeProperty .equalsIgnoreCase(JAVA)));
    }

    @Override
    public void apply(NodeTemplate nodeTemplate) {
        if (isApplicable(nodeTemplate)) {
            modifyEnvProperty(nodeTemplate);
        }
    }

    private void modifyEnvProperty(NodeTemplate nodeTemplate) {
        Map<String, Object> envProperties = getEnvProperties(nodeTemplate);
        if(!envProperties.isEmpty()){
            nodeTemplate.removeProperty(ENV_PROPERTY);
            nodeTemplate.addProperty(JAVA_SYS_PROP, envProperties);
        }
    }

    private Map<String, Object> getEnvProperties(NodeTemplate nodeTemplate){
        Map<String, Object> envProperties =
                (Map<String, Object>) nodeTemplate.getPropertyValue(ENV_PROPERTY);
        return (envProperties == null)
                ? MutableMap.<String, Object>of()
                : envProperties;

    }




}
