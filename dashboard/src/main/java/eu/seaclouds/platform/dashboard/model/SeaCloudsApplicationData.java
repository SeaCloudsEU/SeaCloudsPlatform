/*
 *  Copyright 2014 SeaClouds
 *  Contact: SeaClouds
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package eu.seaclouds.platform.dashboard.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.io.BaseEncoding;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.seaclouds.platform.dashboard.util.ObjectMapperHelpers;
import it.polimi.tower4clouds.rules.MonitoringRule;
import it.polimi.tower4clouds.rules.MonitoringRules;
import org.apache.brooklyn.rest.domain.ApplicationSummary;
import org.apache.brooklyn.rest.domain.Status;
import org.apache.brooklyn.rest.domain.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import javax.xml.bind.JAXBException;
import java.io.Serializable;
import java.util.*;

public class SeaCloudsApplicationData implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(SeaCloudsApplicationData.class);

    private static final String YAML_DESCRIPTION_TAG = "template_name";
    private static final String YAML_TOPOLOGY_TEMPLATE_TAG = "topology_template";
    private static final String YAML_GROUPS_TEMPLATE_TAG = "groups";
    private static final String YAML_POLICIES_TAG = "policies";

    private static final String YAML_SEACLOUDS_POLICY_TAG = "seaclouds_configuration_policy";
    private static final String YAML_SEACLOUDS_POLICY_CONFIGURATION_TAG = "configuration";
    private static final String YAML_SEACLOUDS_AGREEMENT_TAG = "slaAgreement";
    private static final String YAML_SEACLOUDS_RULES_TAG = "t4cRules";

    private final String seaCloudsApplicationId;
    private final String name;
    private final Map toscaDamMap;
    private String deployerApplicationId;
    private Set<String> monitoringRulesIds;
    private String agreementId;

    private Status deploymentStatus;
    private IGuaranteeTerm.GuaranteeTermStatusEnum agreementStatus;


    public SeaCloudsApplicationData(String toscaDam) throws JAXBException {
        this((Map) new Yaml().load(toscaDam));
    }

    SeaCloudsApplicationData(Map toscaDamMap) throws JAXBException  {
        this.seaCloudsApplicationId = UUID.randomUUID().toString();
        this.toscaDamMap = toscaDamMap;
        this.name = extractName(this.toscaDamMap);
        this.agreementId = extractAgreementId(this.toscaDamMap);
        this.monitoringRulesIds = extractMonitoringRulesIds(this.toscaDamMap);
    }

    static String extractName(Map toscaDamMap) {
        return (String) toscaDamMap.get(YAML_DESCRIPTION_TAG);
    }

    static String extractAgreementId(Map toscaDamMap) throws JAXBException {
        Map topologyTemplate = (Map) toscaDamMap.get(YAML_TOPOLOGY_TEMPLATE_TAG);
        Map groups = (Map) topologyTemplate.get(YAML_GROUPS_TEMPLATE_TAG);
        Map seaCloudsConfigurationMember = (Map) groups.get(YAML_SEACLOUDS_POLICY_TAG);
        Map seaCloudsConfigurationPolicy = (Map) ((Map)((List)  seaCloudsConfigurationMember.get(YAML_POLICIES_TAG)).get(0)).get(YAML_SEACLOUDS_POLICY_CONFIGURATION_TAG);

        String agreementB64String = (String) seaCloudsConfigurationPolicy.get(YAML_SEACLOUDS_AGREEMENT_TAG);
        String agreementString =  new String(BaseEncoding.base64().decode(agreementB64String));
        Agreement agreement = ObjectMapperHelpers.XmlToObject(agreementString, Agreement.class);
        return agreement.getAgreementId();
    }

    static Set<String> extractMonitoringRulesIds(Map toscaDamMap) throws JAXBException {
        Set<String> returnSet = new HashSet<>();

        Map topologyTemplate = (Map) toscaDamMap.get(YAML_TOPOLOGY_TEMPLATE_TAG);
        Map groups = (Map) topologyTemplate.get(YAML_GROUPS_TEMPLATE_TAG);
        Map seaCloudsConfigurationMember = (Map) groups.get(YAML_SEACLOUDS_POLICY_TAG);
        Map seaCloudsConfigurationPolicy = (Map) ((Map)((List)  seaCloudsConfigurationMember.get(YAML_POLICIES_TAG)).get(0)).get(YAML_SEACLOUDS_POLICY_CONFIGURATION_TAG);

        String rulesB64String = (String) seaCloudsConfigurationPolicy.get(YAML_SEACLOUDS_RULES_TAG);
        String rulesString =  new String(BaseEncoding.base64().decode(rulesB64String));
        MonitoringRules rules = ObjectMapperHelpers.XmlToObject(rulesString, MonitoringRules.class);


        for(MonitoringRule rule : rules.getMonitoringRules()){
            returnSet.add(rule.getId());
        }

        return returnSet;
    }

    public void setDeployerApplicationId(ApplicationSummary application) {
        this.deployerApplicationId = application.getId();
    }

    public void setDeployerApplicationId(TaskSummary applicationTask) {
        this.deployerApplicationId = applicationTask.getEntityId();
    }

    public void setMonitoringRulesIds(MonitoringRules monitoringRules) {
        Set<String> rulesIdSet = new HashSet<>();
        for(MonitoringRule rule : monitoringRules.getMonitoringRules()){
            rulesIdSet.add(rule.getId());
        }

        this.monitoringRulesIds = rulesIdSet;
    }

    public void setAgreementId(Agreement agreement) {
        this.agreementId = agreement.getAgreementId();
    }

    @JsonProperty
    public String getSeaCloudsApplicationId() {
        return seaCloudsApplicationId;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public Set<String> getMonitoringRulesIds() {
        return monitoringRulesIds;
    }

    @JsonProperty
    public String getAgreementId() {
        return agreementId;
    }

    @JsonProperty
    public String getToscaDam() {
        Yaml yamlParser = new Yaml();
        return yamlParser.dump(toscaDamMap);
    }

    @JsonProperty
    public String getDeployerApplicationId() {
        return deployerApplicationId;
    }

    @JsonProperty
    public Status getDeploymentStatus() {
        return deploymentStatus;
    }

    @JsonProperty
    public IGuaranteeTerm.GuaranteeTermStatusEnum getAgreementStatus() {
        return agreementStatus;
    }

    public void setAgreementStatus(IGuaranteeTerm.GuaranteeTermStatusEnum agreementStatus) {
        this.agreementStatus = agreementStatus;
    }

    public void setDeploymentStatus(Status deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeaCloudsApplicationData that = (SeaCloudsApplicationData) o;

        return seaCloudsApplicationId.equals(that.seaCloudsApplicationId);

    }

    @Override
    public int hashCode() {
        return seaCloudsApplicationId.hashCode();
    }

}
