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
package eu.seaclouds.platform.planner.core.facade.policies;


import com.google.common.collect.Iterators;
import com.google.common.io.Resources;
import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringInfo;
import eu.seaclouds.platform.planner.core.DamGenerator;
import eu.seaclouds.platform.planner.core.template.policies.SeaCloudsManagementPolicy;
import it.polimi.tower4clouds.rules.MonitoringRules;
import org.apache.brooklyn.util.text.Identifiers;
import org.apache.brooklyn.util.text.Strings;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class SeaCloudsManagementPolicyTest {

    static Logger log = LoggerFactory.getLogger(DamGenerator.class);

    private static final String FAKE_AGREEMENT_ID = "agreement-1234567890";
    private static final String FAKE_APPLICATION_ID = Identifiers.makeRandomId(12);
    private static final String MONITOR_URL = "52.48.187.2";
    private static final String MONITOR_PORT = "8170";
    private static final String MONITOR_ENDPOINT = "http://" + MONITOR_URL + ":" + MONITOR_PORT;
    private static final String SLA_ENDPOINT = "127.0.0.3:9003";

    private static final String INFLUXDB_URL = "52.48.187.2";
    private static final String INFLUXDB_PORT = "8086";
    private static final String INFLUXDB_ENDPOINT = "http://" + INFLUXDB_URL + ":" + INFLUXDB_PORT;
    private static final String INFLUXDB_DATABASE = "tower4clouds";
    private static final String INFLUXDB_USERNAME = "root";
    private static final String INFLUXDB_PASSWORD = "root";

    private static final String GRAFANA_USERNAME = "admin";
    private static final String GRAFANA_PASSWORD = "admin";
    private static final String GRAFANA_ENDPOINT = "http://127.0.0.4:1234";

    @Mock
    private DamGenerator.SlaAgreementManager fakeAgreementManager;

    @Mock
    private MonitoringInfo fakeMonitorInfo;

    @BeforeMethod
    public void setUp() throws URISyntaxException, FileNotFoundException {
        MonitoringRules monitoringRules = getEmptyMonitoringRulesFromResource("rules/fake-monitoring-rules.xml");
        MockitoAnnotations.initMocks(this);

        when(fakeAgreementManager.generateAgreeemntId(((Map<String, Object>) anyObject())))
                .thenReturn(FAKE_AGREEMENT_ID);
        String fakeAgreement = new Scanner(new File(Resources.getResource("agreements/mock_test_agreement.xml").toURI())).useDelimiter("\\Z").next();
        when(fakeAgreementManager.getAgreement(anyString())).thenReturn(fakeAgreement);
        when(fakeMonitorInfo.getApplicationMonitoringRules()).thenReturn(monitoringRules);
    }

    @Test
    public void testSeaCloudsPolicy() {
        SeaCloudsManagementPolicy policyFacade =
                new SeaCloudsManagementPolicy.Builder()
                        .agreementManager(fakeAgreementManager)
                        .slaEndpoint(SLA_ENDPOINT)
                        .t4cEndpoint(MONITOR_ENDPOINT)
                        .influxdbEndpoint(INFLUXDB_ENDPOINT)
                        .influxdbDatabase(INFLUXDB_DATABASE)
                        .influxdbUsername(INFLUXDB_USERNAME)
                        .influxdbPassword(INFLUXDB_PASSWORD)
                        .grafanaEndpoint(GRAFANA_ENDPOINT)
                        .grafanaUsername(GRAFANA_USERNAME)
                        .grafanaPassword(GRAFANA_PASSWORD)
                        .build();

        Map<String, Object> policyGroup = policyFacade.getPolicy(fakeMonitorInfo, FAKE_APPLICATION_ID);

        assertNotNull(policyGroup);
        assertEquals(policyGroup.size(), 2);
        assertNotNull(policyGroup.get(SeaCloudsManagementPolicy.MEMBERS));
        assertTrue(policyGroup.get(SeaCloudsManagementPolicy.MEMBERS) instanceof List);

        List members = (List) policyGroup.get(SeaCloudsManagementPolicy.MEMBERS);
        assertTrue(members.isEmpty());

        assertNotNull(policyGroup.get(SeaCloudsManagementPolicy.POLICIES));
        assertTrue(policyGroup.get(SeaCloudsManagementPolicy.POLICIES) instanceof List);

        List<Map<String, Object>> policies = (List<Map<String, Object>>) policyGroup
                .get(SeaCloudsManagementPolicy.POLICIES);

        assertEquals(policies.size(), 1);

        Map<String, Object> policy = Iterators.getOnlyElement(policies.iterator());
        assertTrue(policy.containsKey(SeaCloudsManagementPolicy
                .SEACLOUDS_APPLICATION_CONFIGURATION_POLICY));
        Map<String, Object> policyConfiguration = (Map<String, Object>) policy
                .get(SeaCloudsManagementPolicy.SEACLOUDS_APPLICATION_CONFIGURATION_POLICY);

        assertEquals(policyConfiguration.get(DamGenerator.TYPE),
                SeaCloudsManagementPolicy.SEACLOUDS_MANAGEMENT_POLICY);
        assertEquals(policyConfiguration.get(SeaCloudsManagementPolicy.SLA_ENDPOINT), SLA_ENDPOINT);
        assertFalse(Strings.isBlank((String) policyConfiguration.get(SeaCloudsManagementPolicy.SLA_AGREEMENT)));
        assertEquals(policyConfiguration.get(SeaCloudsManagementPolicy.T4C_ENDPOINT), MONITOR_ENDPOINT);
        assertFalse(Strings.isBlank((String) policyConfiguration.get(SeaCloudsManagementPolicy.T4C_RULES)));
        assertEquals(policyConfiguration.get(SeaCloudsManagementPolicy.INFLUXDB_ENDPOINT), INFLUXDB_ENDPOINT);
        assertEquals(policyConfiguration.get(SeaCloudsManagementPolicy.INFLUXDB_DATABASE), INFLUXDB_DATABASE);
        assertEquals(policyConfiguration.get(SeaCloudsManagementPolicy.INFLUXDB_USERNAME), INFLUXDB_USERNAME);
        assertEquals(policyConfiguration.get(SeaCloudsManagementPolicy.INFLUXDB_PASSWORD), INFLUXDB_PASSWORD);
        assertEquals(policyConfiguration.get(SeaCloudsManagementPolicy.GRAFANA_ENDPOINT), GRAFANA_ENDPOINT);
        assertEquals(policyConfiguration.get(SeaCloudsManagementPolicy.GRAFANA_USERNAME), GRAFANA_USERNAME);
        assertEquals(policyConfiguration.get(SeaCloudsManagementPolicy.GRAFANA_PASSWORD), GRAFANA_PASSWORD);
    }

    private MonitoringRules getEmptyMonitoringRulesFromResource(String resourcePath) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(MonitoringRules.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (MonitoringRules) jaxbUnmarshaller.unmarshal(Resources.getResource(resourcePath));
        } catch (JAXBException e) {
            log.error("Resource {} was not can be load as a MonitoringRule", resourcePath);
        }
        return null;
    }

}
