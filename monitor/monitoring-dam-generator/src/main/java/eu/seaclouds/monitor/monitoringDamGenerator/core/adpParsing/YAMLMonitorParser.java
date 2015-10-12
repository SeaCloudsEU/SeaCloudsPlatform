package eu.seaclouds.monitor.monitoringDamGenerator.core.adpParsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

@SuppressWarnings("unchecked")
public class YAMLMonitorParser {

    private static Logger logger = LoggerFactory
            .getLogger(YAMLMonitorParser.class);

    public List<Module> getModuleRelevantInfoFromAdp(String adp)
            throws ParsingException {

        logger.info("Parsing the Abstract Deployment Model.");
        Yaml yamlApp = new Yaml();
        Map<String, Object> appMap = (Map<String, Object>) yamlApp.load(adp);
        return getModuleRelevantInfoFromAdp(appMap);
    }

    private List<Module> getModuleRelevantInfoFromAdp(Map<String, Object> appMap)
            throws ParsingException {

        List<Module> toReturn = new ArrayList<Module>();

        Map<String, Object> groups = getGroupsFromAdp(appMap);

        Map<String, Object> nodeTypes = getNodeTypeMapFromAppMap(appMap);

        for (String group : groups.keySet()) {
            List<String> groupMembers = getGroupMembers((Map<String, Object>) groups
                    .get(group));
            Map<String, Map<String, Object>> groupRelatedNodesType = getNodesTypeFromGroupMembers(
                    groupMembers, nodeTypes);

            if (groupRelatedNodesType != null & groupMembers != null) {
                toReturn.addAll(buildModulesInfo(
                        (Map<String, Object>) groups.get(group),
                        groupRelatedNodesType));
            } else {
                throw new ParsingException(
                        "There was an error parsing the ADP!");
            }
        }

        return toReturn;

    }

    private Map<String, Object> getGroupsFromAdp(Map<String, Object> appMap) {
        Map<String, Object> groupsMap;
        try {
            groupsMap = (Map<String, Object>) appMap.get("groups");

        } catch (NullPointerException E) {
            logger.error("It was not found '" + "groups");
            return null;
        }
        return groupsMap;
    }

    private Map<String, Object> getNodeTypeMapFromAppMap(
            Map<String, Object> appMap) {
        Map<String, Object> nodesTypeMap;
        try {
            nodesTypeMap = (Map<String, Object>) appMap.get("node_types");

        } catch (NullPointerException E) {
            logger.error("It was not found '" + "node_types");
            return null;
        }
        return nodesTypeMap;
    }

    private Map<String, Map<String, Object>> getNodesTypeFromGroupMembers(
            List<String> groupMembers, Map<String, Object> nodeTypes) {

        Map<String, Map<String, Object>> toReturn = new HashMap<String, Map<String, Object>>();

        for (String member : groupMembers) {
            for (String nodeType : nodeTypes.keySet()) {
                if (nodeType.split("\\.")[1].equals(member)) {
                    toReturn.put(member,
                            (Map<String, Object>) nodeTypes.get(nodeType));
                }
            }
        }

        return toReturn;
    }

    private List<String> getGroupMembers(Map<String, Object> group) {

        List<String> toReturn = null;

        for (String key : group.keySet()) {
            if (key.equals("members")) {
                toReturn = (List<String>) group.get(key);
            }
        }

        return toReturn;
    }

    private List<Module> buildModulesInfo(Map<String, Object> moduleGroup,
            Map<String, Map<String, Object>> modulesNodeType)
            throws ParsingException {
        List<Module> toReturn = new ArrayList<Module>();

        List<String> members = getGroupMembers(moduleGroup);
        List<Map<String, Object>> policies = (List<Map<String, Object>>) moduleGroup
                .get("policies");

        Map<String, Object> qosRequirements = null;

        for (Map<String, Object> policy : policies) {
            for (String key : policy.keySet()) {
                if (key.equals("QoSRequirements")) {
                    qosRequirements = (Map<String, Object>) policy
                            .get("QoSRequirements");
                }
            }
        }

        for (String member : members) {
            Module toAdd = new Module();
            toAdd.setModuleName(member);

            if (qosRequirements != null) {
                for (String requirement : qosRequirements.keySet()) {
                    if (requirement.equals("response_time")) {
                        Map<String, Object> condition = (Map<String, Object>) qosRequirements
                                .get(requirement);
                        if (condition.keySet().size() > 1) {
                            throw new ParsingException(
                                    "Error parsing the ADP: found more than 1 condition for a qos requirment of module: "
                                            + member);
                        } else {
                            for (String key : condition.keySet()) {
                                toAdd.setResponseTimeMillis(Double
                                        .parseDouble(condition.get(key)
                                                .toString().split(" ")[0]));
                            }
                        }
                    } else if (requirement.equals("availability")) {
                        Map<String, Object> condition = (Map<String, Object>) qosRequirements
                                .get(requirement);
                        if (condition.keySet().size() > 1) {
                            throw new ParsingException(
                                    "Error parsing the ADP: found more than 1 condition for a qos requirment of module: "
                                            + member);
                        } else {
                            for (String key : condition.keySet()) {
                                toAdd.setAvailability(Double
                                        .parseDouble(condition.get(key)
                                                .toString().split(" ")[0]));
                            }
                        }
                    } else if (requirement.equals("cost")) {
                        Map<String, Object> condition = (Map<String, Object>) qosRequirements
                                .get(requirement);
                        if (condition.keySet().size() > 1) {
                            throw new ParsingException(
                                    "Error parsing the ADP: found more than 1 condition for a qos requirment of module: "
                                            + member);
                        } else {
                            for (String key : condition.keySet()) {
                                toAdd.setCostMonth(Double.parseDouble(condition
                                        .get(key).toString().split(" ")[0]));
                            }
                        }
                    } else if (requirement.equals("workload")) {
                        Map<String, Object> condition = (Map<String, Object>) qosRequirements
                                .get(requirement);
                        if (condition.keySet().size() > 1) {
                            throw new ParsingException(
                                    "Error parsing the ADP: found more than 1 condition for a qos requirment of module: "
                                            + member);
                        } else {
                            for (String key : condition.keySet()) {
                                toAdd.setWorkloadMinute(Double
                                        .parseDouble(condition.get(key)
                                                .toString().split(" ")[0]));
                            }
                        }
                    }
                }
            }

            for (String moduleName : modulesNodeType.keySet()) {
                if (moduleName.equals(member)) {
                    Map<String, Object> properties = (Map<String, Object>) modulesNodeType
                            .get(moduleName).get("properties");

                    for (String property : properties.keySet()) {
                        if (property.equals("java_support")) {
                            toAdd.setJavaApp(getJavaSupportValue((Map<String, Object>) properties
                                    .get(property)));
                        } else if (property.equals("resource_type")) {
                            toAdd.setDeploymentType(getResourceTypeValue((Map<String, Object>) properties
                                    .get(property)));
                        }
                    }
                }
            }

            toReturn.add(toAdd);
        }

        return toReturn;
    }

    private boolean getJavaSupportValue(Map<String, Object> javaSupportProperty) {

        boolean toReturn = false;

        for (String key : javaSupportProperty.keySet()) {
            if (key.equals("constraints")) {
                List<Map<String, Object>> value = (List<Map<String, Object>>) javaSupportProperty
                        .get(key);

                for (Map<String, Object> condition : value) {
                    for (String key2 : condition.keySet()) {
                        if (key2.equals("equal")) {
                            toReturn = (boolean) condition.get(key2);
                        }
                    }
                }

            }
        }

        return toReturn;
    }

    private String getResourceTypeValue(Map<String, Object> resourceTypeProperty) {

        String toReturn = null;
        for (String key : resourceTypeProperty.keySet()) {
            if (key.equals("constraints")) {
                List<Map<String, Object>> value = (List<Map<String, Object>>) resourceTypeProperty
                        .get(key);

                for (Map<String, Object> condition : value) {
                    for (String key2 : condition.keySet()) {
                        if (key2.equals("equal")) {
                            toReturn = (String) condition.get(key2);

                        }
                    }
                }
            }
        }

        return toReturn;
    }
}
