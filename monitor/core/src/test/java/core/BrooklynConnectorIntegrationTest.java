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
package core;

import brooklyn.BrooklynTestUtils;
import brooklyn.rest.client.BrooklynApi;
import metrics.Metric;
import model.Application;
import model.ApplicationModule;
import model.Module;
import model.exceptions.MonitorConnectorException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Adrian on 22/10/2014.
 */
@Test(groups = { "Integration" })
public class BrooklynConnectorIntegrationTest {

    BrooklynApi api;
    String appId;

    private BrooklynConnector connector;


    @BeforeClass
    public void setup() throws MonitorConnectorException {
        System.out.println("BrooklynConnectorTest.setup()");

        api = BrooklynTestUtils.getApi();
        appId = BrooklynTestUtils.getAppId();

        String appName = BrooklynTestUtils.getAppName();
        String moduleId = api.getApplicationApi().getDescendants(appId, ".*jboss.*").iterator().next().getId();

        Application application = new Application(appId,appName);
        Module  webServerModule = new ApplicationModule(moduleId,application,application);

        connector = new BrooklynConnector(webServerModule, BrooklynTestUtils.BROOKLYN_ENDPOINT);
    }

    @Test
    public void checkAvailableMetrics() throws Exception {
        Assert.assertNotEquals(0, connector.getAvailableMetrics().size());
    }

    @Test
    public void checkGetValue() throws Exception {
        // Requires application to be completely deployed and running.
        if (BrooklynTestUtils.isAppRunning(appId)){
            for (Metric metric : connector.getAvailableMetrics()) {
                Assert.assertNotNull(connector.getValue(metric));
            }
        }
    }

}
