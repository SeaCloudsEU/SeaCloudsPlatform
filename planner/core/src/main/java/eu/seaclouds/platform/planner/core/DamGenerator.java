package eu.seaclouds.platform.planner.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringDamGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringInfo;
import it.polimi.tower4clouds.rules.MonitoringRules;
import org.apache.brooklyn.util.collections.MutableList;
import org.apache.brooklyn.util.collections.MutableMap;
import org.apache.brooklyn.util.exceptions.Exceptions;
import org.apache.brooklyn.util.text.Identifiers;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;


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
public class DamGenerator {

    static Logger log = LoggerFactory.getLogger(DamGenerator.class);

    private static final String SLA_INFO_GROUPNAME = "sla_gen_info";
    private static final String MONITOR_INFO_GROUPNAME = "monitoringInformation";

    public static final String ADD_BROOKLYN_LOCATION = "add_brooklyn_location_";
    public static final String BROOKLYN_LOCATION = "brooklyn.location";
    public static final String LOCATION = "location";
    public static final String REGION = "region";
    public static final String HARDWARE_ID = "hardwareId";
    public static final String TYPE = "type";
    public static final String RESOURCE_TYPE = "resource_type";
    public static final String PLATFORM = "platform";;
    public static final String POLICIES = "policies";
    public static final String GROUPS = "groups";
    public static final String HOST = "host";
    public static final String INSTANCES_POC = "instancesPOC";
    public static final String REQUIREMENTS = "requirements";
    public static final String MEMBERS = "members";
    public static final String ID = "id";
    public static final String APPLICATION = "application";
    public static final String TOPOLOGY_TEMPLATE = "topology_template";
    public static final String NODE_TEMPLATES = "node_templates";
    public static final String NODE_TYPES = "node_types";
    public static final String PROPERTIES = "properties";
    public static final String IAAS_BROOKLYN_TYPES_MAPPING = "mapping/brooklyn-types-mapping.yaml";
    public static final String PAAS_BROOKLYN_TYPES_MAPPING = "mapping/brooklyn-paas-types-mapping.yaml";
    public static final String BROOKLYN_POLICY_TYPE = "brooklyn.location";
    public static final String IMPORTS = "imports";
    public static final String TOSCA_NORMATIVE_TYPES = "tosca-normative-types";
    public static final String TOSCA_NORMATIVE_TYPES_VERSION = "1.0.0.wd06-SNAPSHOT";
    public static final String SEACLOUDS_NODE_TYPES = "seaclouds-types";
    public static final String SEACLOUDS_NODE_TYPES_VERSION = "0.8.0-SNAPSHOT";

    public static final String TEMPLATE_NAME = "template_name";
    public static final String TEMPLATE_NAME_PREFIX = "seaclouds.app.";
    public static final String TEMPLATE_VERSION = "template_version";
    public static final String DEFAULT_TEMPLATE_VERSION = "1.0.0-SNAPSHOT";
    public static final String SEACLOUDS_MONITORING_RULES_ID_POLICY = "seaclouds.policies.monitoringrules";
    public static final String MONITORING_RULES_POLICY_NAME = "monitoringrules.information.policy";
    public static final String SEACLOUDS_APPLICATION_INFORMATION_POLICY_TYPE = "seaclouds.policies.app.information";
    public static final String SEACLOUDS_APPLICATION_POLICY_NAME = "seaclouds.app.information";
    public static final String SEACLOUDS_NODE_PREFIX = "seaclouds.nodes";

    public static final String SEACLOUDS_APPLICATION_CONFIGURATION =
            "seaclouds_configuration_policy";
    public static final String SEACLOUDS_APPLICATION_CONFIGURATION_POLICY =
            "configuration";
    public static final String SEACLOUDS_MANAGEMENT_POLICY =
            "eu.seaclouds.policy.SeaCloudsManagementPolicy";

    private DeployerTypesResolver deployerIaasTypesResolver;
    private DeployerTypesResolver deployerPaasTypesResolver;

    private Map<String, MonitoringInfo> monitoringInfoByApplication = new HashMap<>();
    private String monitorUrl;
    private String monitorPort;
    private String slaEndpoint;
    private String influxdbUrl;
    private String influxdbPort;
    private String infludbDatabase;
    private String influxdbUsername;
    private String influxdbPassword;
    private String grafanaUsername;
    private String grafanaPassword;
    private String grafanaEndpoint;

    private SlaAgreementManager agreementManager;
    private Map<String, Object> template;

    private DamGenerator(Builder builder) {
        this.monitorUrl = builder.monitorUrl;
        this.monitorPort = builder.monitorPort;
        this.slaEndpoint = builder.slaUrl;
        this.influxdbUrl = builder.influxdbUrl;
        this.influxdbPort = builder.influxdbPort;
        this.infludbDatabase = builder.influxdbDatabase;
        this.influxdbUsername = builder.influxdbUsername;
        this.influxdbPassword = builder.influxdbPassword;
        this.grafanaUsername = builder.grafanaUsername;
        this.grafanaPassword = builder.grafanaPassword;
        this.grafanaEndpoint = builder.grafanaEndpoint;
        init();
    }

    private void init() {
        agreementManager = new SlaAgreementManager(slaEndpoint);
    }

    public DeployerTypesResolver getDeployerIaaSTypeResolver() {
        try {
            if (deployerIaasTypesResolver == null) {
                deployerIaasTypesResolver = new DeployerTypesResolver(Resources
                        .getResource(IAAS_BROOKLYN_TYPES_MAPPING).toURI().toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return deployerIaasTypesResolver;
    }

    public DeployerTypesResolver getDeployerPaaSTypeResolver() {
        try {
            if (deployerPaasTypesResolver == null) {
                deployerPaasTypesResolver = new DeployerTypesResolver(Resources
                        .getResource(PAAS_BROOKLYN_TYPES_MAPPING).toURI().toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return deployerPaasTypesResolver;
    }

    public Yaml getYamlParser() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(options);
    }

    public String generateDam(String adp) {
        String applicationInfoId;
        Map<String, Object> adpYaml =
                manageTemplateMetada((Map<String, Object>) getYamlParser().load(adp));

        template = translateAPD(adpYaml);
        MonitoringInfo monitoringInfo = generateMonitoringInfo();
        addMonitorInfo(monitoringInfo);
        manageNodeTemplatesForPaas();
        manageNodeTypes();
        applicationInfoId = getAgreementManager().generateAgreeemntId(template);
        addApplicationInfo(applicationInfoId);
        relationManagement();

        addSeaCloudsPolicy(monitoringInfo, applicationInfoId);

        manageGroups();

        return getYamlParser().dump(template);
    }

    private void manageNodeTemplatesForPaas() {
        Map<String, Object> topologyTemplate = (Map<String, Object>) template.get(TOPOLOGY_TEMPLATE);
        Map<String, Object> nodeTemplates = (Map<String, Object>) topologyTemplate.get(NODE_TEMPLATES);
        ArrayList<String> modulesToRemove = new ArrayList<>();


        for (String moduleName : nodeTemplates.keySet()) {
            Map<String, Object> module = (Map<String, Object>) nodeTemplates.get(moduleName);

            Map<String, Object> properties = (Map<String, Object>) module.get(PROPERTIES);

            /* it's a PaaS offering and must be removed */
            if (properties != null && properties.containsKey(RESOURCE_TYPE) && properties.get(RESOURCE_TYPE).equals(PLATFORM)) {
                modulesToRemove.add(moduleName);
            } else {
                String hostName = getHostName(module, nodeTemplates);
                Map<String, Object> host = (Map<String, Object>) nodeTemplates.get(hostName);

                if (host != null) {
                    /* getting the type of the offering on which the module will be deployed */
                    Map<String, Object> hostProperties = (Map<String, Object>) host.get(PROPERTIES);
                    String resourceType = (String) hostProperties.get(RESOURCE_TYPE);

                    /* if the module will be deployed on PaaS host requirement must be removed */
                    if (resourceType.equals(PLATFORM)) {
                        removeHostRequirement(module);
                    }
                }
            }
        }

        /* removing PaaS offerings */
        for (String moduleName : modulesToRemove) {
            nodeTemplates.remove(moduleName);
        }
    }

    private void removeHostRequirement(Map<String, Object> module) {
        List<Map<String, Object>> requirements = (List<Map<String, Object>>) module.get(REQUIREMENTS);
        Map<String, Object> hostRequirement = null;

        /* getting the offering where the module will be deployed */
        for (Map<String, Object> requirement : requirements) {
            if (requirement.containsKey(HOST)) {
                hostRequirement = requirement;
            }
        }

        if (hostRequirement != null) {
            requirements.remove(hostRequirement);
        }
    }

    private void manageNodeTypes() {
        if (template.containsKey(NODE_TYPES)) {
            template.remove(NODE_TYPES);
        }

        Map<String, Object> topologyTemplate = (Map<String, Object>) template.get(TOPOLOGY_TEMPLATE);
        Map<String, Object> nodeTemplates = (Map<String, Object>) topologyTemplate.get(NODE_TEMPLATES);

        //Solve offerings Types issue
        for (Map.Entry<String, Object> nodeTemplateEntry : nodeTemplates.entrySet()) {
            Map<String, Object> nodeTemplate = (Map<String, Object>) nodeTemplateEntry.getValue();
            String nodeTemplateType = (String) nodeTemplate.get(TYPE);
            if (nodeTemplateType.contains("seaclouds.nodes.Compute")) {
                nodeTemplate.put(TYPE, getDeployerIaaSTypeResolver().resolveNodeType("seaclouds.nodes.Compute"));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void manageGroups() {
        Map groups = (Map) template.remove(GROUPS);
        addPoliciesTypeIfNotPresent(groups);
        ((Map) template.get(TOPOLOGY_TEMPLATE)).put(GROUPS, groups);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> manageTemplateMetada(Map<String, Object> template) {
        if (template.containsKey(IMPORTS)) {
            List<String> imports = (List<String>) template.get(IMPORTS);
            if (imports != null) {
                String importedNormativeTypes = null;
                for (String dependency : imports) {
                    if (dependency.contains(TOSCA_NORMATIVE_TYPES)) {
                        importedNormativeTypes = dependency;
                    }
                }
                if ((importedNormativeTypes != null) && (!importedNormativeTypes.equals(TOSCA_NORMATIVE_TYPES + ":" + TOSCA_NORMATIVE_TYPES_VERSION))) {
                    imports.remove(importedNormativeTypes);
                    imports.add(TOSCA_NORMATIVE_TYPES + ":" + TOSCA_NORMATIVE_TYPES_VERSION);
                }
                imports.add(SEACLOUDS_NODE_TYPES + ":" + SEACLOUDS_NODE_TYPES_VERSION);
            }
        }

        if (!template.containsKey(TEMPLATE_NAME)) {
            template.put(TEMPLATE_NAME, TEMPLATE_NAME_PREFIX + Identifiers.makeRandomId(8));
        }

        if (!template.containsKey(TEMPLATE_VERSION)) {
            template.put(TEMPLATE_VERSION, DEFAULT_TEMPLATE_VERSION);
        }
        return template;
    }

    public MonitoringInfo generateMonitoringInfo() {
        MonitoringDamGenerator monDamGen;
        monDamGen = new MonitoringDamGenerator(getMonitoringEndpoint(), getInfluxDbEndpoint());
        return monDamGen.generateMonitoringInfo(getYamlParser().dump(template));
    }

    public void addMonitorInfo(MonitoringInfo monitoringInfo) {
        String generatedApplicationId = UUID.randomUUID().toString();

        monitoringInfoByApplication.put(generatedApplicationId, monitoringInfo);

        HashMap<String, Object> appGroup = new HashMap<>();
        appGroup.put(MEMBERS, Arrays.asList(APPLICATION));
        Map<String, Object> policy = new HashMap<>();

        HashMap<String, String> policyProperties = new HashMap<>();
        policyProperties.put(ID, generatedApplicationId);
        policyProperties.put(TYPE, SEACLOUDS_MONITORING_RULES_ID_POLICY);
        policy.put(MONITORING_RULES_POLICY_NAME, policyProperties);

        ArrayList<Map<String, Object>> policiesList = new ArrayList<>();
        policiesList.add(policy);

        appGroup.put(POLICIES, policiesList);

        template = (Map<String, Object>) getYamlParser().load(monitoringInfo.getReturnedAdp());
        Map<String, Object> groups = (Map<String, Object>) template.get(GROUPS);
        groups.put(MONITOR_INFO_GROUPNAME, appGroup);
    }

    public Map<String, Object> translateAPD(Map<String, Object> adpYaml) {
        Map<String, Object> damUsedNodeTypes = new HashMap<>();
        Map<String, ArrayList<String>> groups = new HashMap<>();
        Map<String, Object> ADPgroups = (Map<String, Object>) adpYaml.get(GROUPS);
        Map<String, Object> topologyTemplate = (Map<String, Object>) adpYaml.get(TOPOLOGY_TEMPLATE);
        Map<String, Object> nodeTemplates = (Map<String, Object>) topologyTemplate.get(NODE_TEMPLATES);
        Map<String, Object> nodeTypes = (Map<String, Object>) adpYaml.get(NODE_TYPES);

        for (String moduleName : nodeTemplates.keySet()) {
            Map<String, Object> module = (Map<String, Object>) nodeTemplates.get(moduleName);

            manageArtifacts(module);
            typeReplacement(damUsedNodeTypes, nodeTemplates, nodeTypes, module);
            manageRequirements(groups, moduleName, module);
        }

        adpYaml.put(NODE_TYPES, damUsedNodeTypes);

        //get brookly location from host
        for (String group : groups.keySet()) {
            manageGroups(ADPgroups, nodeTemplates, group);
        }
        return adpYaml;
    }

    private void manageArtifacts(Map<String, Object> module) {
        ArrayList<Map<String, Object>> artifactsList = (ArrayList<Map<String, Object>>) module.get("artifacts");
        if (artifactsList != null) {
            Map<String, Object> artifacts = artifactsList.get(0);
            artifacts.remove("type");

            Set<String> artifactKeys = artifacts.keySet();
            if (artifactKeys.size() > 1) {
                throw new IllegalArgumentException();
            }

            String[] keys = artifactKeys.toArray(new String[1]);

            Map<String, Object> properties = (Map<String, Object>) module.get("properties");
            properties.put(keys[0], artifacts.get(keys[0]));

            module.remove("artifacts");
        }
    }

    private void typeReplacement(Map<String, Object> damUsedNodeTypes,
                                 Map<String, Object> nodeTemplates,
                                 Map<String, Object> nodeTypes,
                                 Map<String, Object> module) {
        DeployerTypesResolver deployerTypesResolver;

        String moduleType = (String) module.get("type");
        if (nodeTypes.containsKey(moduleType)) {
            String hostName = getHostName(module, nodeTemplates);
            Map<String, Object> host = (Map<String, Object>) nodeTemplates.get(hostName);

            if (host != null) {
                /* getting the type of the offering on which the module will be deployed */
                Map<String, Object> properties = (Map<String, Object>) host.get(PROPERTIES);
                String resourceType = (String) properties.get(RESOURCE_TYPE);

                /* choose right DeployerTypesResolver based on what kind of deploy is needed (PaaS or IaaS) */
                if (resourceType.equals(PLATFORM)) {
                    deployerTypesResolver = getDeployerPaaSTypeResolver();
                } else {
                    deployerTypesResolver = getDeployerIaaSTypeResolver();
                }

                /* getting module type */
                Map<String, Object> type = (HashMap<String, Object>) nodeTypes.get(moduleType);
                String sourceType = (String) type.get("derived_from");
                String targetType = deployerTypesResolver.resolveNodeType(sourceType);

                if (targetType != null) {
                    module.put("type", targetType);
                    if (deployerTypesResolver.getNodeTypeDefinition(targetType) != null) {
                        damUsedNodeTypes.put(targetType,
                                deployerTypesResolver.getNodeTypeDefinition(targetType));
                    } else {
                        log.error("TargetType definition " + targetType + "was not found" +
                                "so it will not added to DAM");
                    }
                } else {
                    damUsedNodeTypes.put(moduleType, nodeTypes.get(moduleType));
                }
            }
        }
    }

    private void manageRequirements(Map<String, ArrayList<String>> groups, String moduleName, Map<String, Object> module) {
        if (module.keySet().contains(REQUIREMENTS)) {
            List<Map<String, Object>> requirements = (ArrayList<Map<String, Object>>) module.get(REQUIREMENTS);
            for (Map<String, Object> req : requirements) {
                if (req.keySet().contains(HOST)) {
                    String host = (String) req.get(HOST);
                    if (!groups.keySet().contains(host)) {
                        groups.put(host, new ArrayList<String>());
                    }
                    groups.get(host).add(moduleName);
                }
                req.remove(INSTANCES_POC);
            }
        }
    }

    private void manageGroups(Map<String, Object> ADPgroups, Map<String, Object> nodeTemplates, String group) {
        HashMap<String, Object> policyGroup = new HashMap<>();

        HashMap<String, Object> cloudOffering = (HashMap<String, Object>) nodeTemplates.get(group);
        HashMap<String, Object> properties = (HashMap<String, Object>) cloudOffering.get(PROPERTIES);

        String location = (String) properties.get(LOCATION);
        String region = (String) properties.get(REGION);
        String resourceType = (String) properties.get(RESOURCE_TYPE);

        if (resourceType.equals(PLATFORM)) {
            policyGroup.put(MEMBERS, getModuleHostedOn(group, nodeTemplates));
        } else {
            policyGroup.put(MEMBERS, Arrays.asList(group));
        }

        ArrayList<HashMap<String, Object>> policy = new ArrayList<>();
        HashMap<String, Object> p = new HashMap<>();
        if (location != null) {
            if (resourceType.equals(PLATFORM)) {
                HashMap<String, Object> authenticationMap = new HashMap<>();
                authenticationMap.put(location, new HashMap<>());
                p.put(BROOKLYN_LOCATION, authenticationMap);
            } else {
                String iaasLocation = location + ":" + region;
                p.put(BROOKLYN_LOCATION, iaasLocation);
            }

        } else {
            p.put(BROOKLYN_LOCATION, group);
        }
        policy.add(p);

        policyGroup.put(POLICIES, policy);

        ADPgroups.put(ADD_BROOKLYN_LOCATION + group, policyGroup);
    }

    private ArrayList<String> getModuleHostedOn(String offeringName, Map<String, Object> nodeTemplates) {
        ArrayList<String> modulesHostedOn = new ArrayList<>();

        for (String moduleName : nodeTemplates.keySet()) {
            Map<String, Object> module = (Map<String, Object>) nodeTemplates.get(moduleName);
            String host = getHostName(module, nodeTemplates);

            if (host != null && host.equals(offeringName))
                modulesHostedOn.add(moduleName);
        }

        return modulesHostedOn;
    }

    private String getHostName(Map<String, Object> module, Map<String, Object> nodeTemplates) {
        List<Map<String, Object>> requirements = (List<Map<String, Object>>) module.get(REQUIREMENTS);
        String host = null;

        if (requirements != null) {
            /* getting the offering where the module will be deployed */
            for (Map<String, Object> requirement : requirements) {
                if (requirement.containsKey(HOST)) {
                    host = (String) requirement.get(HOST);
                }
            }
        }


        return host;
    }

    public URL getMonitoringEndpoint() {
        try {
            return new URL("http://" + monitorUrl + ":" + monitorPort + "");
        } catch (MalformedURLException e) {
            Exceptions.propagateIfFatal(e);
            log.warn("Error creating MonitoringEndpoint: http://" + monitorUrl + ":" + monitorPort);
        }
        return null;
    }

    public URL getInfluxDbEndpoint() {
        try {
            return new URL("http://" + influxdbUrl + ":" + influxdbPort + "");

        } catch (MalformedURLException e) {
            Exceptions.propagateIfFatal(e);
            log.warn("Error creating InfluxDbEndpoint: http://" + influxdbUrl + ":" + influxdbPort);
        }
        return null;
    }


    public void addSeaCloudsPolicy(MonitoringInfo monitoringInfo,
                                   String applicationMonitorId) {
        Map<String, Object> seaCloudsPolicyConfiguration = MutableMap.of();
        seaCloudsPolicyConfiguration.put(TYPE, SEACLOUDS_MANAGEMENT_POLICY);
        seaCloudsPolicyConfiguration.put("slaEndpoint", slaEndpoint);
        seaCloudsPolicyConfiguration.put("slaAgreement", encodeAgreement(applicationMonitorId));
        seaCloudsPolicyConfiguration.put("t4cEndpoint", getMonitoringEndpoint().toString());
        seaCloudsPolicyConfiguration.put("t4cRules", encodeBase64MonitoringRules(monitoringInfo));
        seaCloudsPolicyConfiguration.put("influxdbEndpoint", getInfluxDbEndpoint().toString());

        seaCloudsPolicyConfiguration.put("influxdbDatabase", infludbDatabase);
        seaCloudsPolicyConfiguration.put("influxdbUsername", influxdbUsername);
        seaCloudsPolicyConfiguration.put("influxdbPassword", influxdbPassword);

        seaCloudsPolicyConfiguration.put("grafanaEndpoint", grafanaEndpoint);
        seaCloudsPolicyConfiguration.put("grafanaUsername", grafanaUsername);
        seaCloudsPolicyConfiguration.put("grafanaPassword", grafanaPassword);

        Map<String, Object> seaCloudsPolicy = MutableMap.of();
        seaCloudsPolicy.put(SEACLOUDS_APPLICATION_CONFIGURATION_POLICY, seaCloudsPolicyConfiguration);

        Map<String, Object> seaCloudsApplicationGroup = MutableMap.of();
        seaCloudsApplicationGroup.put(MEMBERS, ImmutableList.of());
        seaCloudsApplicationGroup.put(POLICIES, ImmutableList.of(seaCloudsPolicy));

        Map<String, Object> groups = (Map<String, Object>) template.get(GROUPS);
        groups.put(SEACLOUDS_APPLICATION_CONFIGURATION, seaCloudsApplicationGroup);

    }

    public String encodeAgreement(String applicationMonitorId) {
        String agreement = agreementManager.getAgreement(applicationMonitorId);
        return Base64.encodeBase64String(agreement.getBytes());
    }

    public static String encodeBase64MonitoringRules(MonitoringInfo monitoringInfo) {
        StringWriter sw = new StringWriter();
        String encodeMonitoringRules;
        JAXBContext jaxbContext;
        String marshalledMonitoringRules = null;
        try {
            jaxbContext = JAXBContext.newInstance(MonitoringRules.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            jaxbMarshaller.marshal(monitoringInfo.getApplicationMonitoringRules(), sw);
            marshalledMonitoringRules = sw.toString();
        } catch (JAXBException e) {
            log.error("Monitoring rules {} can not be marshalled by addSeaCloudsPolicy in " +
                            "DamGenerator",
                    monitoringInfo.getApplicationMonitoringRules());
        }

        encodeMonitoringRules = Base64
                .encodeBase64String(marshalledMonitoringRules.getBytes());
        return encodeMonitoringRules;
    }

    public void addApplicationInfo(String applicationInfoId) {

        Map<String, Object> groups = (Map<String, Object>) template.get(GROUPS);
        HashMap<String, Object> appGroup = new HashMap<>();
        appGroup.put(MEMBERS, Arrays.asList(APPLICATION));

        Map<String, Object> policy = new HashMap<>();
        HashMap<String, String> policyProperties = new HashMap<>();
        policyProperties.put(ID, applicationInfoId);
        policyProperties.put(TYPE, SEACLOUDS_APPLICATION_INFORMATION_POLICY_TYPE);
        policy.put(SEACLOUDS_APPLICATION_POLICY_NAME, policyProperties);

        ArrayList<Map<String, Object>> policiesList = new ArrayList<>();
        policiesList.add(policy);

        appGroup.put(POLICIES, policiesList);
        groups.put(SLA_INFO_GROUPNAME, appGroup);
    }

    /*This method filters current requirements and avoid non-host requirements. It is only a temporal
    * fix. Relations have to be managed ASAP*/
    public void relationManagement() {
        Map<String, Object> topologyTemplate = (Map<String, Object>) template.get(TOPOLOGY_TEMPLATE);
        Map<String, Object> nodeTemplates = (Map<String, Object>) topologyTemplate.get(NODE_TEMPLATES);

        for (Map.Entry<String, Object> nodeTemplateEntry : nodeTemplates.entrySet()) {

            Map<String, Object> nodeTemplate = (Map<String, Object>) nodeTemplateEntry.getValue();

            List<Map<String, Object>> requirements = (List<Map<String, Object>>) nodeTemplate.get(REQUIREMENTS);
            List<Map<String, Object>> fixedRequirements = MutableList.of();
            if (requirements != null) {
                for (Map<String, Object> requirement : requirements) {
                    if (requirement.containsKey("host")) {
                        fixedRequirements.add(requirement);
                    }
                }
                nodeTemplate.put(REQUIREMENTS, fixedRequirements);
            }
        }


    }

    @SuppressWarnings("unchecked")
    public void addPoliciesTypeIfNotPresent(Map<String, Object> groups) {

        for (Map.Entry<String, Object> entryGroup : groups.entrySet()) {
            List<Map<String, Object>> policies =
                    (List<Map<String, Object>>) ((Map<String, Object>) entryGroup.getValue()).get(POLICIES);
            if (policies != null) {
                for (Map<String, Object> policy : policies) {
                    String policyName = getPolicyName(policy);
                    if (!isLocationPolicy(policy)
                            && !(policy.get(policyName) instanceof String)) {
                        Map<String, Object> policyProperties = getPolicyProperties(policy);

                        if (getPolicyType(policyProperties) == null) {
                            policyProperties.put(TYPE, ((Object) "seaclouds.policies." + policyName));
                        } else {
                            translatePolicyToDeployerPolicy(policyProperties);
                        }
                    }
                }
            }
        }
    }

    private boolean isLocationPolicy(Map<String, Object> policy) {

        return policy.containsKey(BROOKLYN_POLICY_TYPE);
    }

    private String getPolicyType(Map<String, Object> policyProperties) {
        return (String) policyProperties.get(TYPE);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getPolicyProperties(Map<String, Object> policy) {
        if (policy != null) {
            for (Map.Entry<String, Object> policyEntry : policy.entrySet()) {
                return (Map<String, Object>) policyEntry.getValue();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String getPolicyName(Map<String, Object> policy) {
        if (policy != null) {
            for (Map.Entry<String, Object> policyEntry : policy.entrySet()) {
                return policyEntry.getKey();
            }
        }
        return null;
    }

    private Map<String, Object> translatePolicyToDeployerPolicy(Map<String, Object> policyProperties) {
        String deployerPolicyType = getDeployerIaaSTypeResolver()
                .resolvePolicyType(getPolicyType(policyProperties));
        if (deployerPolicyType != null) {
            policyProperties = resolverDeployerTypesInProperties(policyProperties);
            policyProperties.remove(TYPE);
            policyProperties.put(TYPE, deployerPolicyType);
        }
        return policyProperties;
    }

    private Map<String, Object> resolverDeployerTypesInProperties(Map<String, Object> properties) {
        String property, propertyName;
        for (Map.Entry<String, Object> entry : ImmutableMap.copyOf(properties).entrySet()) {
            if (entry.getValue() instanceof String) {
                property = (String) entry.getValue();
                propertyName = entry.getKey();
                if (property.contains(SEACLOUDS_NODE_PREFIX)) {
                    properties.remove(propertyName);
                    properties.put(propertyName, resolverDeployerTypesInAProperty(property));
                }
            }
        }
        return properties;
    }

    private String resolverDeployerTypesInAProperty(String property) {
        String[] slices = property.split("\"|\\s+|-|\\(|\\)|,");
        for (String slice : slices) {
            if (getDeployerIaaSTypeResolver().resolveNodeType(slice) != null) {
                property = property
                        .replaceAll(slice, getDeployerIaaSTypeResolver().resolveNodeType(slice));
            }
        }
        return property;
    }

    public SlaAgreementManager getAgreementManager() {
        return agreementManager;
    }

    public void setAgreementManager(SlaAgreementManager agManager) {
        this.agreementManager = agManager;
    }


    public static class Builder {

        private String monitorUrl;
        private String monitorPort;
        private String slaUrl;
        private String influxdbUrl;
        private String influxdbPort;
        private String grafanaEndpoint;
        private String influxdbDatabase;
        private String influxdbUsername;
        private String influxdbPassword;
        private String grafanaUsername;
        private String grafanaPassword;

        public Builder() {
        }

        public Builder monitorUrl(String monitorUrl) {
            this.monitorUrl = monitorUrl;
            return this;
        }

        public Builder monitorPort(String monitorPort) {
            this.monitorPort = monitorPort;
            return this;
        }

        public Builder slaUrl(String slaUrl) {
            this.slaUrl = slaUrl;
            return this;
        }

        public Builder influxdbUrl(String influxdbUrl) {
            this.influxdbUrl = influxdbUrl;
            return this;
        }

        public Builder influxdbPort(String influxdbPort) {
            this.influxdbPort = influxdbPort;
            return this;
        }

        public Builder influxdbDatabase(String influxdbDatabase) {
            this.influxdbDatabase = influxdbDatabase;
            return this;
        }

        public Builder influxdbUsername(String influxdbUsername) {
            this.influxdbUsername = influxdbUsername;
            return this;
        }

        public Builder influxdbPassword(String influxdbPassword) {
            this.influxdbPassword = influxdbPassword;
            return this;
        }

        public Builder grafanaUsername(String grafanaUsername) {
            this.grafanaUsername = grafanaUsername;
            return this;
        }


        public Builder grafanaPassword(String grafanaPassword) {
            this.grafanaPassword = grafanaPassword;
            return this;
        }

        public Builder grafanaEndpoint(String grafanaEndpoint) {
            this.grafanaEndpoint = grafanaEndpoint;
            return this;
        }

        public DamGenerator build() {
            return new DamGenerator(this);
        }
    }

    public class SlaAgreementManager {

        private static final String SLA_GEN_OP = "/seaclouds/templates";
        private static final String GET_AGREEMENT_OP = "/seaclouds/commands/fromtemplate";

        private String slaUrl;

        public SlaAgreementManager(String slaUrl) {
            this.slaUrl = slaUrl;
        }

        public String generateAgreeemntId(Map<String, Object> template) {
            String result = null;
            String slaInfoResponse = new HttpHelper(slaUrl)
                    .postInBody(SLA_GEN_OP, getYamlParser().dump(template));
            checkNotNull(slaInfoResponse, "Error getting SLA info");
            try {
                ApplicationMonitorId applicationMonitoringId = new ObjectMapper()
                        .readValue(slaInfoResponse, ApplicationMonitorId.class);
                result = applicationMonitoringId.getId();
            } catch (IOException e) {
                log.error("Error AgreementTemplateId during dam generation {}", this);
            }
            return result;
        }

        public String getAgreement(String applicationMonitorId) {
            List<NameValuePair> paremeters = MutableList.of((NameValuePair)
                    new BasicNameValuePair("templateId", applicationMonitorId));
            return new HttpHelper(slaEndpoint).getRequest(GET_AGREEMENT_OP, paremeters);

        }
    }

}
