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
package eu.seaclouds.platform.planner.core.template.policies;

import com.google.common.collect.ImmutableList;
import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringInfo;
import eu.seaclouds.platform.planner.core.DamGenerator;
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

public class SeaCloudsManagementPolicy {

    static Logger log = LoggerFactory.getLogger(SeaCloudsManagementPolicy.class);

    public static final String MEMBERS = "members";
    public static final String POLICIES = "policies";

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
    public static final String TYPE = "type";

    private String slaEndpoint;
    private String t4cEndpoint;
    private String influxdbEndpoint;
    private String influxdbDatabase;
    private String influxdbUsername;
    private String influxdbPassword;
    private String grafanaEndpoint;
    private String grafanaUsername;
    private String grafanaPassword;
    private DamGenerator.SlaAgreementManager agreementManager;


    public SeaCloudsManagementPolicy(Builder builder) {
        slaEndpoint = builder.slaEndpoint;
        t4cEndpoint = builder.t4cEndpoint;
        influxdbEndpoint = builder.influxdbEndpoint;
        influxdbDatabase = builder.influxdbDatabase;
        influxdbUsername = builder.influxdbUsername;
        influxdbPassword = builder.influxdbPassword;
        grafanaEndpoint = builder.grafanaEndpoint;
        grafanaUsername = builder.grafanaUsername;
        grafanaPassword = builder.grafanaPassword;
        agreementManager = builder.agreementManager;
    }

    public Map<String, Object> getPolicy(MonitoringInfo monitoringInfo,
                                            String applicationMonitorId) {

        Map<String, Object> seaCloudsPolicyConfiguration = MutableMap.of();
        seaCloudsPolicyConfiguration.put(TYPE, SEACLOUDS_MANAGEMENT_POLICY);
        seaCloudsPolicyConfiguration.put(SLA_ENDPOINT, slaEndpoint);
        seaCloudsPolicyConfiguration.put(SLA_AGREEMENT, encodeAgreement(applicationMonitorId));
        seaCloudsPolicyConfiguration.put(T4C_ENDPOINT, t4cEndpoint);
        seaCloudsPolicyConfiguration.put(T4C_RULES, encodeBase64MonitoringRules(monitoringInfo));
        seaCloudsPolicyConfiguration.put(INFLUXDB_ENDPOINT, influxdbEndpoint);

        seaCloudsPolicyConfiguration.put(INFLUXDB_DATABASE, influxdbDatabase);
        seaCloudsPolicyConfiguration.put(INFLUXDB_USERNAME, influxdbUsername);
        seaCloudsPolicyConfiguration.put(INFLUXDB_PASSWORD, influxdbPassword);

        seaCloudsPolicyConfiguration.put(GRAFANA_ENDPOINT, grafanaEndpoint);
        seaCloudsPolicyConfiguration.put(GRAFANA_USERNAME, grafanaUsername);
        seaCloudsPolicyConfiguration.put(GRAFANA_PASSWORD, grafanaPassword);

        Map<String, Object> seaCloudsPolicy = MutableMap.of();
        seaCloudsPolicy.put(SEACLOUDS_APPLICATION_CONFIGURATION_POLICY, seaCloudsPolicyConfiguration);

        Map<String, Object> seaCloudsApplicationGroup = MutableMap.of();
        seaCloudsApplicationGroup.put(MEMBERS, ImmutableList.of());
        seaCloudsApplicationGroup.put(POLICIES, ImmutableList.of(seaCloudsPolicy));

        return seaCloudsApplicationGroup;
    }

    private String encodeAgreement(String applicationMonitorId) {
        String agreement = agreementManager.getAgreement(applicationMonitorId);
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

    public static class Builder {

        private String slaEndpoint;
        private String t4cEndpoint;
        private String influxdbEndpoint;
        private String influxdbDatabase;
        private String influxdbUsername;
        private String influxdbPassword;
        private String grafanaEndpoint;
        private String grafanaUsername;
        private String grafanaPassword;
        private DamGenerator.SlaAgreementManager agreementManager;

        public Builder() {
        }

        public Builder t4cEndpoint(String t4cEndpoint) {
            this.t4cEndpoint = t4cEndpoint;
            return this;
        }

        public Builder agreementManager(DamGenerator.SlaAgreementManager agreementManager) {
            this.agreementManager = agreementManager;
            return this;
        }

        public Builder slaEndpoint(String slaEndpoint) {
            this.slaEndpoint = slaEndpoint;
            return this;
        }

        public Builder influxdbEndpoint(String influxdbEndpoint) {
            this.influxdbEndpoint = influxdbEndpoint;
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

        public SeaCloudsManagementPolicy build() {
            return new SeaCloudsManagementPolicy(this);
        }
    }


}
