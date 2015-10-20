package eu.seaclouds.monitor.monitoringDamGenerator.adpParsing;

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
    
    public static final String COMPUTE_NODE_PREFIX = "seaclouds.Nodes.Compute";
    public static final String PLATFORM_NODE_PREFIX = "seaclouds.Nodes.Platform";
    public static final String QOS_REQUIREMENT_POLICY = "QoSRequirements";
    public static final String RESPONSE_TIME_REQUIREMENT = "response_time";
    public static final String AVAILABILITY_REQUIREMENT = "availability";
    public static final String COST_REQUIREMENT = "cost";
    public static final String WORKLOAD_REQUIREMENT = "workload";
    public static final String TOPOLOGY_KET = "topology_template";
    public static final String NODE_TEMPLATES_KEY = "node_templates";
    public static final String GROUPS_KEY = "groups";
    public static final String MEMBERS_KEY = "members";
    public static final String POLICIES_KEY = "policies";
    public static final String LANGUAGE_KEY = "language";
    public static final String PROPERTIES_KEY = "properties";
    public static final String HOST_KEY = "host";
    public static final String REQUIREMENTS_KEY = "requirements";
    public static final String TYPE_KEY = "type";


    public List<Module> getModuleRelevantInfoFromAdp(String adp)
            throws ParsingException {

        logger.info("Parsing the Abstract Deployment Model.");
        Yaml yamlApp = new Yaml();
        Map<String, Object> appMap = (Map<String, Object>) yamlApp.load(adp);
        return getModuleRelevantInfoFromAdp(appMap);
    }

    private List<Module> getModuleRelevantInfoFromAdp(Map<String, Object> adp)
            throws ParsingException {

        List<Module> toReturn = new ArrayList<Module>();
        Map<String,Host> hosts = new HashMap<String,Host>();
        Module tempModule;
        Host tempHost;

        Map<String, Object> groups = getGroupsFromAdp(adp);
        
        Map<String, Object> nodeTemplates = getNodeTemplatesFromAdp(adp);
        
        for(String nodeTemplate : nodeTemplates.keySet()){  
            String type=getNodeTemplateTypeName((Map<String,Object>) nodeTemplates.get(nodeTemplate));   
            if(type.startsWith(COMPUTE_NODE_PREFIX)){
                tempHost = new Host();
                tempHost.setHostName(nodeTemplate);
                tempHost.setDeploymentType("IaaS");
                hosts.put(tempHost.getHostName(), tempHost);
            }else if (type.startsWith(PLATFORM_NODE_PREFIX)){
                tempHost = new Host();
                tempHost.setHostName(nodeTemplate);
                tempHost.setDeploymentType("PaaS");
                hosts.put(tempHost.getHostName(), tempHost);
            }
        }
        
        for(String nodeTemplate : nodeTemplates.keySet()){            
            String type=getNodeTemplateTypeName((Map<String,Object>) nodeTemplates.get(nodeTemplate));

            if(!(type.startsWith(COMPUTE_NODE_PREFIX) || type.startsWith(PLATFORM_NODE_PREFIX))){
                tempModule = new Module();

                tempModule.setModuleName(nodeTemplate);
                
                if(getNodeTemplateLanguage((Map<String,Object>) nodeTemplates.get(nodeTemplate)) != null
                        && getNodeTemplateLanguage((Map<String,Object>) nodeTemplates.get(nodeTemplate)).equals("JAVA")){
                    tempModule.setJavaApp(true);
                }
                
                if(getNodeTemplateHost((Map<String,Object>) nodeTemplates.get(nodeTemplate))!=null){
                    tempModule.setHost(hosts.get(getNodeTemplateHost((Map<String,Object>) nodeTemplates.get(nodeTemplate))));
                }  
                
                
                for(String key : groups.keySet()){
                    for(String member: getGroupMembers((Map<String,Object>)groups.get(key))){
                        if(member.equals(tempModule.getModuleName())){
                            setQosRequirements((Map<String,Object>) groups.get(key),tempModule);
                        }
                    }
                }
                
                toReturn.add(tempModule);
            }else {
                
            }
        }
        return toReturn;
    }
    
    private void setQosRequirements(Map<String,Object> group, Module module) throws ParsingException{
        
        List<Map<String, Object>> policies = (List<Map<String, Object>>) group.get(POLICIES_KEY);
        
        Map<String, Object> qosRequirements = null;

        for (Map<String, Object> policy : policies) {
            for (String key : policy.keySet()) {
                if (key.equals(QOS_REQUIREMENT_POLICY)) {
                    qosRequirements = (Map<String, Object>) policy
                            .get(QOS_REQUIREMENT_POLICY);
                }
            }
        }
        
        if (qosRequirements != null) {
            for (String requirement : qosRequirements.keySet()) {
                if (requirement.equals(RESPONSE_TIME_REQUIREMENT)) {
                    Map<String, Object> condition = (Map<String, Object>) qosRequirements
                            .get(requirement);
                    if (condition.keySet().size() > 1) {
                        throw new ParsingException(
                                "Error parsing the ADP: found more than 1 condition for a qos requirment.");
                    } else {
                        for (String key : condition.keySet()) {
                            module.setResponseTimeMillis(Double
                                    .parseDouble(condition.get(key)
                                            .toString().split(" ")[0]));
                        }
                    }
                } else if (requirement.equals(AVAILABILITY_REQUIREMENT)) {
                    Map<String, Object> condition = (Map<String, Object>) qosRequirements
                            .get(requirement);
                    if (condition.keySet().size() > 1) {
                        throw new ParsingException(
                                "Error parsing the ADP: found more than 1 condition for a qos requirment of module.");
                    } else {
                        for (String key : condition.keySet()) {
                            module.setAvailability(Double
                                    .parseDouble(condition.get(key)
                                            .toString().split(" ")[0]));
                        }
                    }
                } else if (requirement.equals(COST_REQUIREMENT)) {
                    Map<String, Object> condition = (Map<String, Object>) qosRequirements
                            .get(requirement);
                    if (condition.keySet().size() > 1) {
                        throw new ParsingException(
                                "Error parsing the ADP: found more than 1 condition for a qos requirment of module.");
                    } else {
                        for (String key : condition.keySet()) {
                            module.setCostMonth(Double.parseDouble(condition
                                    .get(key).toString().split(" ")[0]));
                        }
                    }
                } else if (requirement.equals(WORKLOAD_REQUIREMENT)) {
                    Map<String, Object> condition = (Map<String, Object>) qosRequirements
                            .get(requirement);
                    if (condition.keySet().size() > 1) {
                        throw new ParsingException(
                                "Error parsing the ADP: found more than 1 condition for a qos requirment of module.");
                    } else {
                        for (String key : condition.keySet()) {
                            module.setWorkloadMinute(Double
                                    .parseDouble(condition.get(key)
                                            .toString().split(" ")[0]));
                        }
                    }
                }
            }
            }
    }
    
    private String getNodeTemplateTypeName(Map<String,Object> nodeTemplate){
        return (String) nodeTemplate.get(TYPE_KEY);
    }
    
    private String getNodeTemplateLanguage(Map<String,Object> nodeTemplate){
        Map<String,Object> properties = (Map<String,Object>) nodeTemplate.get(PROPERTIES_KEY);
        return (String) properties.get(LANGUAGE_KEY);
               
    }
    
    private String getNodeTemplateHost(Map<String,Object> nodeTemplate){
        List<Map<String,Object>> requirements = (List<Map<String,Object>>) nodeTemplate.get(REQUIREMENTS_KEY);
        
        for(Map<String,Object> requirement : requirements){
            for(String key: requirement.keySet()){
                if(key.equals(HOST_KEY)){
                    return (String) requirement.get(key);
                }
            }
        }
        
        return null;
        
    }

    private Map<String, Object> getGroupsFromAdp(Map<String, Object> appMap) {
        Map<String, Object> groupsMap;
        try {
            groupsMap = (Map<String, Object>) appMap.get(GROUPS_KEY);

        } catch (NullPointerException E) {
            logger.error("It was not found '" + "groups");
            throw new RuntimeException("It was not found '" + "groups");
        }
        return groupsMap;
    }

    private Map<String, Object> getNodeTemplatesFromAdp(Map<String, Object> adp) {
        Map<String, Object> topology;
        Map<String, Object> nodeTemplates;
        
        
        try {
            topology = (Map<String,Object>) adp.get(TOPOLOGY_KET);
            nodeTemplates = (Map<String, Object>) topology.get(NODE_TEMPLATES_KEY);

        } catch (NullPointerException E) {
            logger.error("It was not found '" + "node_templates");
            throw new RuntimeException("It was not found '" + "groups");
        }
        return nodeTemplates;
    }

    private List<String> getGroupMembers(Map<String, Object> group) {

        List<String> toReturn = null;

        for (String key : group.keySet()) {
            if (key.equals(MEMBERS_KEY)) {
                toReturn = (List<String>) group.get(key);
            }
        }

        return toReturn;
    }
}
