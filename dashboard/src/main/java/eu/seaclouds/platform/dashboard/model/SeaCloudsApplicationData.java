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
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.parser.data.wsag.Agreement;
import it.polimi.tower4clouds.rules.MonitoringRule;
import it.polimi.tower4clouds.rules.MonitoringRules;
import org.apache.brooklyn.rest.domain.ApplicationSummary;
import org.apache.brooklyn.rest.domain.Status;
import org.apache.brooklyn.rest.domain.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.Serializable;
import java.util.*;

public class SeaCloudsApplicationData implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(SeaCloudsApplicationData.class);

    private static final String YAML_DESCRIPTION_TAG = "description";
    private static final String YAML_TOPOLOGY_TEMPLATE_TAG = "topology_template";
    private static final String YAML_GROUPS_TEMPLATE_TAG = "groups";
    private static final String YAML_POLICIES_TAG = "policies";
    private static final String YAML_MONITORING_INFORMATION_TAG = "monitoringInformation";
    private static final String YAML_AGREEMENT_TAG = "sla_gen_info";

    private static final String YAML_ID_TAG = "id";


    private final String seaCloudsApplicationId;
    private final String name;
    private final Map toscaDamMap;
    private final String monitoringRulesTemplateId;
    private final String agreementTemplateId;
    private String deployerApplicationId;
    private Set<String> monitoringRulesIds;
    private String agreementId;

    private Status deploymentStatus;
    private IGuaranteeTerm.GuaranteeTermStatusEnum agreementStatus;


    public SeaCloudsApplicationData(String toscaDam) {
        this.seaCloudsApplicationId = UUID.randomUUID().toString();
        Yaml yamlParser = new Yaml();
        this.toscaDamMap = (Map) yamlParser.load(toscaDam);
        this.name = extractName(this.toscaDamMap);
        this.monitoringRulesTemplateId = extractMonitoringRulesemplateId(this.toscaDamMap);
        this.agreementTemplateId = extractAgreementTemplateId(this.toscaDamMap);

    }

    SeaCloudsApplicationData(Map toscaDamMap) {
        this.seaCloudsApplicationId = UUID.randomUUID().toString();
        this.toscaDamMap = toscaDamMap;
        this.name = extractName(this.toscaDamMap);
        this.monitoringRulesTemplateId = extractMonitoringRulesemplateId(this.toscaDamMap);
        this.agreementTemplateId = extractAgreementTemplateId(this.toscaDamMap);

    }

    static String extractName(Map toscaDamMap) {
        return (String) toscaDamMap.get(YAML_DESCRIPTION_TAG);
    }

    static String extractAgreementTemplateId(Map toscaDamMap) {
        Map topologyTemplate = (Map) toscaDamMap.get(YAML_TOPOLOGY_TEMPLATE_TAG);
        Map groups = (Map) topologyTemplate.get(YAML_GROUPS_TEMPLATE_TAG);
        Map monitoringInformation = (Map) groups.get(YAML_AGREEMENT_TAG);

        if(monitoringInformation != null){
            Map policies = (Map)((List)  monitoringInformation.get(YAML_POLICIES_TAG)).get(0);
            return (String) policies.get(YAML_ID_TAG);
        } else{
            LOG.warn("This TOSCA doesn't contain any SLA Agreement Template ID");
            return null;
        }

    }

    static String extractMonitoringRulesemplateId(Map toscaDamMap) {
        Map topologyTemplate = (Map) toscaDamMap.get(YAML_TOPOLOGY_TEMPLATE_TAG);
        Map groups = (Map) topologyTemplate.get(YAML_GROUPS_TEMPLATE_TAG);
        Map slaGenInfo = (Map) groups.get(YAML_MONITORING_INFORMATION_TAG);

        if(slaGenInfo != null){
            Map policies = (Map) ((List) slaGenInfo.get(YAML_POLICIES_TAG)).get(0);
            return (String) policies.get(YAML_ID_TAG);
        } else{
            LOG.warn("This TOSCA doesn't contain any MonitoringRules ID");
            return null;
        }
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
    public String getMonitoringRulesTemplateId() {
        return monitoringRulesTemplateId;
    }

    @JsonProperty
    public String getAgreementTemplateId() {
        return agreementTemplateId;
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
