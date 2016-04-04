package eu.seaclouds.monitor.monitoringdamgenerator.adpparsing;

import eu.seaclouds.monitor.monitoringdamgenerator.DeploymentType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class YAMLMonitorParser {

    private static Logger logger = LoggerFactory
            .getLogger(YAMLMonitorParser.class);

    public static final String COMPUTE_NODE_PREFIX = "seaclouds.nodes.Compute";
    public static final String PLATFORM_NODE_PREFIX = "seaclouds.nodes.Platform";
    public static final String QOS_REQUIREMENT_POLICY = "AppQoSRequirements";
    public static final String TOSCA_COMPUTE_NODE_PREFIX = "tosca.nodes.Compute";
    public static final String RESPONSE_TIME_REQUIREMENT = "response_time";
    public static final String AVAILABILITY_REQUIREMENT = "availability";
    public static final String COST_REQUIREMENT = "cost";
    public static final String WORKLOAD_REQUIREMENT = "workload";
    public static final String TOPOLOGY_KEY = "topology_template";
    public static final String NODE_TEMPLATES_KEY = "node_templates";
    public static final String NODE_TYPES_KEY = "node_types";
    public static final String GROUPS_KEY = "groups";
    public static final String MEMBERS_KEY = "members";
    public static final String POLICIES_KEY = "policies";
    public static final String LANGUAGE_KEY = "language";
    public static final String PORT_KEY = "port";
    public static final String PROPERTIES_KEY = "properties";
    public static final String HOST_KEY = "host";
    public static final String REQUIREMENTS_KEY = "requirements";
    public static final String TYPE_KEY = "type";
    public static final String RAW_TYPE_KEY = "derived_from";


    public List<Module> getModuleRelevantInfoFromAdp(String adp)
            throws AdpParsingException {

        logger.info("Parsing the Abstract Deployment Model.");
        Yaml yamlApp = new Yaml();
        Map<String, Object> appMap = (Map<String, Object>) yamlApp.load(adp);
        return getModuleRelevantInfoFromAdp(appMap);
    }

    private List<Module> getModuleRelevantInfoFromAdp(Map<String, Object> adp)
            throws AdpParsingException {

        List<Module> toReturn = new ArrayList<Module>();
        Map<String, Host> hosts = new HashMap<String, Host>();
        Module tempModule;
        Host tempHost;

        Map<String, Object> groups = getGroupsFromAdp(adp);

        Map<String, Object> nodeTemplates = getNodeTemplatesFromAdp(adp);
        Map<String, Object> nodeTypes = getNodeTypesFromAdp(adp);

        for (String nodeTemplate : nodeTemplates.keySet()) {
            String type = getNodeTemplateTypeName((Map<String, Object>) nodeTemplates
                    .get(nodeTemplate));
            if (type.startsWith(COMPUTE_NODE_PREFIX)
                    || type.startsWith(TOSCA_COMPUTE_NODE_PREFIX)) {
                tempHost = new Host();
                tempHost.setHostName(nodeTemplate);
                tempHost.setDeploymentType(DeploymentType.IaaS);
                hosts.put(tempHost.getHostName(), tempHost);
            } else if (type.startsWith(PLATFORM_NODE_PREFIX)) {
                tempHost = new Host();
                tempHost.setHostName(nodeTemplate);
                tempHost.setDeploymentType(DeploymentType.PaaS);
                hosts.put(tempHost.getHostName(), tempHost);
            }
        }

        for (String nodeTemplate : nodeTemplates.keySet()) {
            String type = getNodeTemplateTypeName((Map<String, Object>) nodeTemplates
                    .get(nodeTemplate));

            if (!(type.startsWith(COMPUTE_NODE_PREFIX)
                    || type.startsWith(TOSCA_COMPUTE_NODE_PREFIX)
                    || type.startsWith(PLATFORM_NODE_PREFIX))) {
                tempModule = new Module();

                tempModule.setModuleName(nodeTemplate);

                Map<String, Object> nodeType = (Map<String, Object>) nodeTypes.get(type);
                String rawType = (String) nodeType.get(RAW_TYPE_KEY);
                tempModule.setType(rawType);

                if (getNodeTemplateLanguage((Map<String, Object>) nodeTemplates
                        .get(nodeTemplate)) != null) {
                    tempModule.setLanguage(getNodeTemplateLanguage((Map<String, Object>) nodeTemplates
                            .get(nodeTemplate)));
                }

                if (getNodeTemplatePort((Map<String, Object>) nodeTemplates
                        .get(nodeTemplate)) != null) {
                    tempModule.setPort(getNodeTemplatePort((Map<String, Object>) nodeTemplates
                            .get(nodeTemplate)));
                }

                if (getNodeTemplateHost((Map<String, Object>) nodeTemplates
                        .get(nodeTemplate)) != null) {
                    tempModule
                            .setHost(hosts
                                    .get(getNodeTemplateHost((Map<String, Object>) nodeTemplates
                                            .get(nodeTemplate))));
                }

                for (String key : groups.keySet()) {
                    for (String member : getGroupMembers((Map<String, Object>) groups
                            .get(key))) {
                        if (member.equals(tempModule.getModuleName())) {
                            setQosRequirements(
                                    (Map<String, Object>) getQosRequirementsFromGroup((Map<String, Object>) groups
                                            .get(key)), tempModule);
                        }
                    }
                }

                toReturn.add(tempModule);
            }
        }
        return toReturn;
    }

    private Map<String, Object> getQosRequirementsFromGroup(
            Map<String, Object> group) throws AdpParsingException {

        try {
            List<Map<String, Object>> policies = (List<Map<String, Object>>) group
                    .get(POLICIES_KEY);

            for (Map<String, Object> policy : policies) {
                for (String key : policy.keySet()) {
                    if (key.equals(QOS_REQUIREMENT_POLICY)) {
                        return (Map<String, Object>) policy
                                .get(QOS_REQUIREMENT_POLICY);
                    }
                }
            }

            return null;

        } catch (NullPointerException e) {
            logger.error("The parser was not able to retrieve the 'QoSRequirements' of one of the group in the current ADP.");
            throw new AdpParsingException(
                    "The parser was not able to retrieve the 'QoSRequirements' of one of the group in the current ADP.");
        }

    }

    private void setQosRequirements(Map<String, Object> qosRequirements,
                                    Module module) throws AdpParsingException {

        if (qosRequirements != null) {
            for (String requirement : qosRequirements.keySet()) {
                if (requirement.equals(RESPONSE_TIME_REQUIREMENT)) {
                    Map<String, Object> condition = (Map<String, Object>) qosRequirements
                            .get(requirement);
                    if (condition.keySet().size() > 1) {
                        throw new AdpParsingException(
                                "Error parsing the ADP: found more than 1 condition for a qos requirment.");
                    } else {
                        for (String key : condition.keySet()) {
                            module.setResponseTimeMillis(Double
                                    .parseDouble(condition.get(key).toString()
                                            .split(" ")[0]));
                        }
                    }
                } else if (requirement.equals(AVAILABILITY_REQUIREMENT)) {
                    Map<String, Object> condition = (Map<String, Object>) qosRequirements
                            .get(requirement);
                    if (condition.keySet().size() > 1) {
                        throw new AdpParsingException(
                                "Error parsing the ADP: found more than 1 condition for a qos requirment of module.");
                    } else {
                        for (String key : condition.keySet()) {
                            module.setAvailability(Double.parseDouble(condition
                                    .get(key).toString().split(" ")[0]));
                        }
                    }
                }
            }
        }

    }

    private String getNodeTemplateTypeName(Map<String, Object> nodeTemplate)
            throws AdpParsingException {

        try {
            return (String) nodeTemplate.get(TYPE_KEY);
        } catch (NullPointerException e) {
            logger.error("The parser was not able to retrieve the 'type' of one of the node templates in the current ADP.");
            throw new AdpParsingException(
                    "The parser was not able to retrieve the 'type' of one of the node templates in the current ADP.");
        }
    }

    private String getNodeTemplateLanguage(Map<String, Object> nodeTemplate)
            throws AdpParsingException {

        try {
            Map<String, Object> properties = (Map<String, Object>) nodeTemplate
                    .get(PROPERTIES_KEY);
            return (String) properties.get(LANGUAGE_KEY);
        } catch (NullPointerException e) {
            logger.error("The parser was not able to retrieve the 'language' property from one of the node templates in the current ADP.");
            throw new AdpParsingException(
                    "The parser was not able to retrieve the 'language' property from one of the node templates in the current ADP.");
        }

    }

    private String getNodeTemplatePort(Map<String, Object> nodeTemplate) throws AdpParsingException {

        try {
            Map<String, Object> properties = (Map<String, Object>) nodeTemplate
                    .get(PROPERTIES_KEY);
            return (String) properties.get(PORT_KEY);
        } catch (NullPointerException e) {
            logger.error("The parser was not able to retrieve the 'port' property from one of the node templates in the current ADP.");
            throw new AdpParsingException(
                    "The parser was not able to retrieve the 'port' property from one of the node templates in the current ADP.");
        }

    }

    private String getNodeTemplateHost(Map<String, Object> nodeTemplate)
            throws AdpParsingException {
        try {
            List<Map<String, Object>> requirements = (List<Map<String, Object>>) nodeTemplate
                    .get(REQUIREMENTS_KEY);

            for (Map<String, Object> requirement : requirements) {
                for (String key : requirement.keySet()) {
                    if (key.equals(HOST_KEY)) {
                        return (String) requirement.get(key);
                    }
                }
            }

            throw new NullPointerException();

        } catch (NullPointerException e) {
            logger.error("The parser was not able to retrieve the 'host' requirement from one of the node templates in the current ADP.");
            throw new AdpParsingException(
                    "The parser was not able to retrieve the 'host' requirement from one of the node templates in the current ADP.");
        }

    }

    private Map<String, Object> getGroupsFromAdp(Map<String, Object> appMap)
            throws AdpParsingException {
        try {
            return (Map<String, Object>) appMap.get(GROUPS_KEY);

        } catch (NullPointerException E) {
            logger.error("The parser was not able to retrieve the 'groups' from the current ADP.");
            throw new AdpParsingException(
                    "The parser was not able to retrieve the 'groups' from the current ADP.");
        }
    }

    private Map<String, Object> getNodeTemplatesFromAdp(Map<String, Object> adp)
            throws AdpParsingException {

        try {
            Map<String, Object> topology = (Map<String, Object>) adp
                    .get(TOPOLOGY_KEY);
            return (Map<String, Object>) topology.get(NODE_TEMPLATES_KEY);

        } catch (NullPointerException E) {
            logger.error("The parser was not able to retrieve the 'node_templates'  from the current ADP.");
            throw new AdpParsingException(
                    "The parser was not able to retrieve the 'node_templates' from the current ADP.");
        }
    }

    private Map<String, Object> getNodeTypesFromAdp(Map<String, Object> adp)
            throws AdpParsingException {

        try {
            return (Map<String, Object>) adp.get(NODE_TYPES_KEY);

        } catch (NullPointerException E) {
            logger.error("The parser was not able to retrieve the 'node_types'  from the current ADP.");
            throw new AdpParsingException(
                    "The parser was not able to retrieve the 'node_types' from the current ADP.");
        }
    }

    private List<String> getGroupMembers(Map<String, Object> group)
            throws AdpParsingException {

        try {
            for (String key : group.keySet()) {
                if (key.equals(MEMBERS_KEY)) {
                    return coerceStringList((ArrayList) group.get(key));
                }
            }

            throw new NullPointerException();

        } catch (NullPointerException e) {
            logger.error("The parser was not able to retrieve the 'members' field from one of the 'group' in the current ADP.");
            throw new AdpParsingException(
                    "The parser was not able to retrieve the 'members' field from one of the 'group' in the current ADP.");
        }

    }

    private List<String> coerceStringList(List list) {
        List<String> result = new ArrayList<>();
        if (list != null) {
            for (Object item : list) {
                result.add((String) item);
            }
        }
        return result;
    }
}
