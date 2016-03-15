package eu.seaclouds.platform.planner.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringDamGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringInfo;
import org.apache.brooklyn.util.text.Identifiers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
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
    public static final String CLOUD_FOUNDRY = "CloudFoundry";
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
    public static final String BROOKLYN_TYPES_MAPPING = "mapping/brooklyn-types-mapping.yaml";
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

    private DeployerTypesResolver deployerTypesResolver;

    private Map<String, MonitoringInfo> monitoringInfoByApplication = new HashMap<>();
    private String monitorUrl;
    private String monitorPort;
    private String slaUrl;
    private String influxdbUrl;
    private String influxdbPort;
    private SlaAgreementManager agreementManager;
    private Map<String, Object> template;


    private DamGenerator(Builder builder) {
        this.monitorUrl = builder.monitorUrl;
        this.monitorPort = builder.monitorPort;
        this.slaUrl = builder.slaUrl;
        this.influxdbUrl = builder.influxdbUrl;
        this.influxdbPort = builder.influxdbPort;
        init();
    }

    private void init() {
        agreementManager = new SlaAgreementManager(slaUrl);
    }

    public String generateDam(String adp) {

        String applicationInfo;
        Map<String, Object> adpYaml =
                manageTemplateMetada((Map<String, Object>) getYamlParser().load(adp));

        template = translateAPD(adpYaml);
        MonitoringInfo monitoringInfo = generateMonitoringInfo();
        addMonitorInfo(monitoringInfo);
        manageNodeTypes();

        applicationInfo = getAgreementManager().generateAgreeemntId(template);

        addApplicationInfo(applicationInfo);
        manageGroups();

        return getYamlParser().dump(template);
    }

    private void manageNodeTypes() {
        if (template.containsKey(NODE_TYPES)) {
            template.remove(NODE_TYPES);
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
        MonitoringDamGenerator monDamGen = null;

        try {
            monDamGen = new MonitoringDamGenerator(
                    new URL("http://" + monitorUrl + ":" + monitorPort + ""),
                    new URL("http://" + influxdbUrl + ":" + influxdbPort + ""));
        } catch (MalformedURLException e) {
            log.error(e.getMessage());
        }
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
        DeployerTypesResolver deployerTypesResolver = getDeployerIaaSTypeResolver();
        Map<String, Object> damUsedNodeTypes = new HashMap<>();
        Map<String, ArrayList<String>> groups = new HashMap<>();
        Map<String, Object> ADPgroups = (Map<String, Object>) adpYaml.get(GROUPS);
        Map<String, Object> topologyTemplate = (Map<String, Object>) adpYaml.get(TOPOLOGY_TEMPLATE);
        Map<String, Object> nodeTemplates = (Map<String, Object>) topologyTemplate.get(NODE_TEMPLATES);
        Map<String, Object> nodeTypes = (Map<String, Object>) adpYaml.get(NODE_TYPES);

        for (String moduleName : nodeTemplates.keySet()) {
            Map<String, Object> module = (Map<String, Object>) nodeTemplates.get(moduleName);

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
            //type replacement
            String moduleType = (String) module.get("type");
            if (nodeTypes.containsKey(moduleType)) {
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

        adpYaml.put(NODE_TYPES, damUsedNodeTypes);

        //get brookly location from host
        for (String group : groups.keySet()) {
            HashMap<String, Object> policyGroup = new HashMap<>();
            policyGroup.put(MEMBERS, Arrays.asList(group));

            HashMap<String, Object> cloudOffering = (HashMap<String, Object>) nodeTemplates.get(group);
            HashMap<String, Object> properties = (HashMap<String, Object>) cloudOffering.get(PROPERTIES);

            String location = (String) properties.get(LOCATION);
            String region = (String) properties.get(REGION);
            String hardwareId = (String) properties.get(HARDWARE_ID);

            ArrayList<HashMap<String, Object>> policy = new ArrayList<>();
            HashMap<String, Object> p = new HashMap<>();
            if (location != null) {
                p.put(BROOKLYN_LOCATION, location + (location.equals(CLOUD_FOUNDRY) ? "" : ":" + region));
            } else {
                p.put(BROOKLYN_LOCATION, group);
            }
            policy.add(p);

            policyGroup.put(POLICIES, policy);

            ADPgroups.put(ADD_BROOKLYN_LOCATION + group, policyGroup);
        }
        return adpYaml;
    }

    public DeployerTypesResolver getDeployerIaaSTypeResolver() {
        try {
            if (deployerTypesResolver == null) {
                deployerTypesResolver = new DeployerTypesResolver(Resources
                        .getResource(BROOKLYN_TYPES_MAPPING).toURI().toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return deployerTypesResolver;
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

    private Yaml getYamlParser() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(options);
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

        public DamGenerator build() {
            return new DamGenerator(this);
        }
    }

    public class SlaAgreementManager {

        private static final String SLA_GEN_OP = "/seaclouds/templates";

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
    }

}
