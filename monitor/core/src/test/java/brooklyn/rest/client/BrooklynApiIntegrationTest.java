/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package brooklyn.rest.client;

import brooklyn.BrooklynTestUtils;
import brooklyn.rest.api.ApplicationApi;
import brooklyn.rest.api.SensorApi;
import brooklyn.rest.client.BrooklynApi;
import brooklyn.rest.domain.ApplicationSummary;
import brooklyn.rest.domain.SensorSummary;
import core.BrooklynConnector;
import core.Manager;
import metrics.MetricCatalog;
import model.Application;
import model.ApplicationModule;
import model.Module;
import model.exceptions.MetricNotFoundException;
import model.exceptions.MonitorConnectorException;
import org.testng.Assert;
import org.testng.TestNG;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author MBarrientos
 */
@Test(groups = { "Integration" })
public class BrooklynApiIntegrationTest {

    public static final String BROOKLYN_ENDPOINT = "http://devtest.scenic.uma.es:8081";

    private BrooklynApi api;
    private ApplicationApi applicationApi;
    private SensorApi sensorApi;
    private List<SensorSummary> sensorSummaryList;
    private String appId;
    private String appName;
    private String moduleId;

    private Application application;
    private Module webServerModule;

    @BeforeClass
    public void setup(){
        System.out.println("BrooklynApiTest.setup()");

        api = BrooklynTestUtils.getApi();
        applicationApi = BrooklynTestUtils.getApplicationApi();
        appId = BrooklynTestUtils.getAppId();
        appName = BrooklynTestUtils.getAppName();

        moduleId = applicationApi.getDescendants(appId,".*jboss.*").iterator().next().getId();

        sensorApi = api.getSensorApi();
        sensorSummaryList = sensorApi.list(appId, moduleId);

        application = new Application(appId,appName);
        webServerModule = new ApplicationModule(moduleId,application,application);
    }

    @Test
    public void brooklynApiTest(){
        // Requires application to be completely deployed and running.
        if(BrooklynTestUtils.isAppRunning(appId)) {
            Object res = sensorApi.get(appId, moduleId, "java.metrics.physicalmemory.free", false);
            Assert.assertNotNull(res);
        }
    }

    @Test
    public void wrongSensorNameTest(){
        // Requires application to be completely deployed and running.
        Object res = sensorApi.get(appId, moduleId, "this.is.not.a.sensor", false);
        Assert.assertNull(res);
    }

    @Test
    public void registerAgent() throws MonitorConnectorException {
        Manager manager = Manager.getInstance();
        manager.registerApplication(application);
        manager.addMonitoringAgent(application, new BrooklynConnector(webServerModule, BROOKLYN_ENDPOINT));
    }

    @Test( expectedExceptions = MetricNotFoundException.class)
    public void wrongSensorTest() throws MetricNotFoundException, MonitorConnectorException {
        Manager manager = Manager.getInstance();
        manager.addMonitoringAgent(webServerModule, new BrooklynConnector(webServerModule, BROOKLYN_ENDPOINT));
        manager.getMetricValue(webServerModule, "this.is.not.a.metric.id");
    }

    @Test
    public void sensorTest() throws MetricNotFoundException, MonitorConnectorException {
        Manager manager = Manager.getInstance();
        manager.addMonitoringAgent(webServerModule, new BrooklynConnector(webServerModule, BROOKLYN_ENDPOINT));
        manager.getMetricValue(webServerModule, MetricCatalog.PredefinedMetrics.WEBAPP_REQUESTS_TOTAL.getValue().getId());
    }
}
