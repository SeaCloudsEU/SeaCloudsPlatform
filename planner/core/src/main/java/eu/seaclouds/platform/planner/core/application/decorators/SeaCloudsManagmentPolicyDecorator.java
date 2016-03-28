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
package eu.seaclouds.platform.planner.core.application.decorators;

import com.google.common.collect.ImmutableList;
import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringInfo;
import eu.seaclouds.platform.planner.core.DamGenerator;
import eu.seaclouds.platform.planner.core.DamGeneratorConfigBag;
import eu.seaclouds.platform.planner.core.application.ApplicationFacade;
import eu.seaclouds.platform.planner.core.application.agreements.AgreementGenerator;
import it.polimi.tower4clouds.rules.MonitoringRules;
import org.apache.brooklyn.util.collections.MutableMap;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.Map;

public class SeaCloudsManagmentPolicyDecorator implements ApplicationFacadeDecorator {

    static Logger log = LoggerFactory.getLogger(SeaCloudsManagmentPolicyDecorator.class);

    public static final String SLA_ENDPOINT = "slaEndpoint";
    public static final String SLA_AGREEMENT = "slaAgreement";
    public static final String T4C_ENDPOINT = "t4cEndpoint";
    public static final String T4C_RULES = "t4cRules";
    public static final String INFLUXDB_ENDPOINT = "influxdbEndpoint";
    public static final String INFLUXDB_DATABASE = "influxdbDatabase";
    public static final String INFLUXDB_USERNAME = "influxdbUsername";
    public static final String INFLUXDB_PASSWORD = "influxdbPassword";
    public static final String GRAFANA_ENDPOINT = "grafanaEndpoint";
    public static final String GRAFANA_USERNAME = "grafanaUsername";
    public static final String GRAFANA_PASSWORD = "grafanaPassword";

    public static final String SEACLOUDS_APPLICATION_CONFIGURATION_POLICY =
            "configuration";
    public static final String SEACLOUDS_MANAGEMENT_POLICY =
            "eu.seaclouds.policy.SeaCloudsManagementPolicy";

    private ApplicationFacade applicationFacade;
    private DamGeneratorConfigBag configBag;
    private AgreementGenerator agreementGenerator;

    @Override
    public void apply(ApplicationFacade applicationFacade) {
        this.applicationFacade = applicationFacade;
        configBag = applicationFacade.getConfigBag();
        agreementGenerator = applicationFacade.getAgreementGenerator();

        addSeaCloudsManagementGroup();
    }

    private void addSeaCloudsManagementGroup() {
        Map<String, Object> policyGroupValue = getPolicyGroupValues(
                applicationFacade.getMonitoringInfo(),
                applicationFacade.getApplicationSlaId());

        applicationFacade.addGroup(
                DamGenerator.SEACLOUDS_APPLICATION_CONFIGURATION,
                policyGroupValue);
    }

    public Map<String, Object> getPolicyGroupValues(MonitoringInfo monitoringInfo,
                                                    String applicationMonitorId) {

        Map<String, Object> seaCloudsPolicyConfiguration = MutableMap.of();
        seaCloudsPolicyConfiguration.put(DamGenerator.TYPE, SEACLOUDS_MANAGEMENT_POLICY);
        seaCloudsPolicyConfiguration.put(SLA_ENDPOINT, configBag.getSlaEndpoint());
        seaCloudsPolicyConfiguration.put(SLA_AGREEMENT, encodeAgreement(applicationMonitorId));
        seaCloudsPolicyConfiguration.put(T4C_ENDPOINT, configBag.getMonitorEndpoint().toString());
        seaCloudsPolicyConfiguration.put(T4C_RULES, encodeBase64MonitoringRules(monitoringInfo));
        seaCloudsPolicyConfiguration.put(INFLUXDB_ENDPOINT, configBag.getInfluxDbEndpoint().toString());

        seaCloudsPolicyConfiguration.put(INFLUXDB_DATABASE, configBag.getInfluxdbDatabase());
        seaCloudsPolicyConfiguration.put(INFLUXDB_USERNAME, configBag.getInfluxdbUsername());
        seaCloudsPolicyConfiguration.put(INFLUXDB_PASSWORD, configBag.getInfluxdbPassword());

        seaCloudsPolicyConfiguration.put(GRAFANA_ENDPOINT, configBag.getGrafanaEndpoint());
        seaCloudsPolicyConfiguration.put(GRAFANA_USERNAME, configBag.getGrafanaUsername());
        seaCloudsPolicyConfiguration.put(GRAFANA_PASSWORD, configBag.getGrafanaPassword());

        Map<String, Object> seaCloudsPolicy = MutableMap.of();
        seaCloudsPolicy.put(SEACLOUDS_APPLICATION_CONFIGURATION_POLICY, seaCloudsPolicyConfiguration);

        Map<String, Object> seaCloudsApplicationGroup = MutableMap.of();
        seaCloudsApplicationGroup.put(DamGenerator.MEMBERS, ImmutableList.of());
        seaCloudsApplicationGroup.put(DamGenerator.POLICIES, ImmutableList.of(seaCloudsPolicy));

        return seaCloudsApplicationGroup;
    }

    private String encodeAgreement(String applicationMonitorId) {
        String agreement = agreementGenerator.getAgreement(applicationMonitorId);
        return Base64.encodeBase64String(agreement.getBytes());
    }

    private static String encodeBase64MonitoringRules(MonitoringInfo monitoringInfo) {
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


}
