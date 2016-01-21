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

package eu.seaclouds.platform.dashboard.rest;

import eu.atos.sla.parser.data.GuaranteeTermsStatus;
import eu.atos.sla.parser.data.Violation;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.GuaranteeTerm;
import eu.seaclouds.platform.dashboard.model.SeaCloudsApplicationDataStorage;
import eu.seaclouds.platform.dashboard.proxy.DeployerProxy;
import eu.seaclouds.platform.dashboard.proxy.MonitorProxy;
import eu.seaclouds.platform.dashboard.proxy.PlannerProxy;
import eu.seaclouds.platform.dashboard.proxy.SlaProxy;
import eu.seaclouds.platform.dashboard.util.ObjectMapperHelpers;
import eu.seaclouds.platform.dashboard.utils.TestFixtures;
import eu.seaclouds.platform.dashboard.utils.TestUtils;
import it.polimi.tower4clouds.rules.MonitoringRules;
import org.apache.brooklyn.rest.domain.ApplicationSummary;
import org.apache.brooklyn.rest.domain.EntitySummary;
import org.apache.brooklyn.rest.domain.SensorSummary;
import org.apache.brooklyn.rest.domain.TaskSummary;
import org.mockito.Matchers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractResourceTest<T extends Resource> {
    protected static final String RANDOM_STRING = UUID.randomUUID().toString();
    private static final String DEPLOYER_ENDPOINT = "http://localhost:8081";
    private static final String MONITOR_ENDPOINT = "http://localhost:8170";
    private static final String PLANNER_ENDPOINT = "http://localhost:1234";
    private static final String SLA_ENDPOINT = "http://localhost:8080";

    private ApplicationSummary applicationSummary;
    private TaskSummary taskSummaryDeploy;
    private TaskSummary taskSummaryDelete;
    private List<SensorSummary> sensorSummaries;
    private List<EntitySummary> entitySummaries;
    private Agreement agreement;
    private GuaranteeTermsStatus agreementStatus;
    private List<Violation> agreementTermViolations;
    private MonitoringRules monitoringRules;
    private String topology;
    private String adp;
    private String adps;
    private String aam;
    private String dam;

    private final DeployerProxy deployerProxy = mock(DeployerProxy.class);
    private final MonitorProxy monitorProxy = mock(MonitorProxy.class);
    private final PlannerProxy plannerProxy = mock(PlannerProxy.class);
    private final SlaProxy slaProxy = mock(SlaProxy.class);

    private void initObjects() throws IOException, JAXBException {
        applicationSummary = ObjectMapperHelpers.JsonToObject(
                TestUtils.getStringFromPath(TestFixtures.APPLICATION_PATH), ApplicationSummary.class);
        taskSummaryDeploy = ObjectMapperHelpers.JsonToObject(
                TestUtils.getStringFromPath(TestFixtures.TASK_SUMMARY_DEPLOY_PATH), TaskSummary.class);
        taskSummaryDelete = ObjectMapperHelpers.JsonToObject(
                TestUtils.getStringFromPath(TestFixtures.TASK_SUMMARY_DEPLOY_PATH), TaskSummary.class);
        sensorSummaries = ObjectMapperHelpers.JsonToObjectCollection(
                TestUtils.getStringFromPath(TestFixtures.SENSORS_SUMMARIES_PATH), SensorSummary.class);
        entitySummaries = ObjectMapperHelpers.JsonToObjectCollection(
                TestUtils.getStringFromPath(TestFixtures.ENTITIES_PATH), EntitySummary.class);


        agreement = ObjectMapperHelpers.JsonToObjectJackson2(
                TestUtils.getStringFromPath(TestFixtures.AGREEMENT_PATH_JSON), Agreement.class);
        agreementStatus = ObjectMapperHelpers.JsonToObjectJackson2(
                TestUtils.getStringFromPath(TestFixtures.AGREEMENT_STATUS_PATH_JSON), GuaranteeTermsStatus.class);
        agreementTermViolations = ObjectMapperHelpers.JsonToObjectCollectionJackson2(TestUtils.getStringFromPath(TestFixtures.VIOLATIONS_JSON_PATH), Violation.class);

        monitoringRules = ObjectMapperHelpers.XmlToObject(TestUtils.getStringFromPath(TestFixtures.MONITORING_RULES_PATH), MonitoringRules.class);
        topology = TestUtils.getStringFromPath(TestFixtures.DESIGNER_TOPOLOGY);

        adp = TestUtils.getStringFromPath(TestFixtures.ADP_PATH);
        adps = TestUtils.getStringFromPath(TestFixtures.ADPS_PATH);
        aam = TestUtils.getStringFromPath(TestFixtures.AAM_PATH);
        dam = TestUtils.getStringFromPath(TestFixtures.TOSCA_DAM_PATH);
    }

    private void initMocks() throws IOException {

        when(deployerProxy.getEndpoint()).thenReturn(DEPLOYER_ENDPOINT);
        when(monitorProxy.getEndpoint()).thenReturn(MONITOR_ENDPOINT);
        when(plannerProxy.getEndpoint()).thenReturn(PLANNER_ENDPOINT);
        when(slaProxy.getEndpoint()).thenReturn(SLA_ENDPOINT);

        when(deployerProxy.getApplication(anyString())).thenReturn(applicationSummary);
        when(deployerProxy.deployApplication(anyString())).thenReturn(taskSummaryDeploy);
        when(deployerProxy.removeApplication(anyString())).thenReturn(taskSummaryDelete);
        when(deployerProxy.getEntitiesFromApplication(anyString())).thenReturn(entitySummaries);
        when(deployerProxy.getEntitySensors(anyString(), anyString())).thenReturn(sensorSummaries);
        when(deployerProxy.getEntitySensorsValue(anyString(), anyString(), anyString())).thenReturn("0.7");


        when(monitorProxy.addMonitoringRules(any(MonitoringRules.class))).thenReturn(RANDOM_STRING);
        when(monitorProxy.listMonitoringRules()).thenReturn(monitoringRules);
        when(monitorProxy.removeMonitoringRule(anyString())).thenReturn(RANDOM_STRING);

        when(plannerProxy.getMonitoringRulesByTemplateId(anyString())).thenReturn(monitoringRules);
        when(plannerProxy.getAdps(anyString())).thenReturn(adps);
        when(plannerProxy.getDam(anyString())).thenReturn(dam);

        when(slaProxy.addAgreement(Matchers.<Agreement>any())).thenReturn(RANDOM_STRING);
        when(slaProxy.getAgreement(anyString())).thenReturn(agreement);
        when(slaProxy.getAgreementByTemplateId(anyString())).thenReturn(agreement);
        when(slaProxy.getAgreementStatus(Matchers.<Agreement>any())).thenReturn(agreementStatus);
        when(slaProxy.getAgreementStatus(anyString())).thenReturn(agreementStatus);
        when(slaProxy.getGuaranteeTermViolations(Matchers.<Agreement>any(), Matchers.<GuaranteeTerm>any())).thenReturn(agreementTermViolations);
        when(slaProxy.notifyRulesReady(Matchers.<Agreement>any())).thenReturn(RANDOM_STRING);
        when(slaProxy.removeAgreement(anyString())).thenReturn(RANDOM_STRING);
    }

    @BeforeClass
    public void setupClass() throws Exception {
        initObjects();
        initMocks();
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
        SeaCloudsApplicationDataStorage.getInstance().clearDataStore();
    }

    public ApplicationSummary getApplicationSummary() {
        return applicationSummary;
    }

    public TaskSummary getTaskSummaryDeploy() {
        return taskSummaryDeploy;
    }

    public TaskSummary getTaskSummaryDelete() {
        return taskSummaryDelete;
    }

    public Agreement getAgreement() {
        return agreement;
    }

    public GuaranteeTermsStatus getAgreementStatus() {
        return agreementStatus;
    }

    public List<Violation> getAgreementTermViolations() {
        return agreementTermViolations;
    }

    public MonitoringRules getMonitoringRules() {
        return monitoringRules;
    }

    public String getTopology() {
        return topology;
    }

    public String getAdp() {
        return adp;
    }

    public String getAdps() {
        return adps;
    }

    public String getAam() {
        return aam;
    }

    public String getDam() {
        return dam;
    }

    public DeployerProxy getDeployerProxy() {
        return deployerProxy;
    }

    public MonitorProxy getMonitorProxy() {
        return monitorProxy;
    }

    public PlannerProxy getPlannerProxy() {
        return plannerProxy;
    }

    public SlaProxy getSlaProxy() {
        return slaProxy;
    }


    public List<SensorSummary> getSensorSummaries() {
        return sensorSummaries;
    }

    public List<EntitySummary> getEntitySummaries() {
        return entitySummaries;
    }
}
