package core;

import brooklyn.BrooklynTestUtils;
import brooklyn.rest.client.BrooklynApi;
import brooklyn.rest.domain.ApplicationSummary;
import metrics.Metric;
import model.Application;
import model.ApplicationModule;
import model.Module;
import model.exceptions.MonitorConnectorException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

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
