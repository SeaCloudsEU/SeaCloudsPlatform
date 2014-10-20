package brooklyn.rest.client;

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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author MBarrientos
 */
public class BrooklynApiTest {

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

    private String webServerAndDbYaml = "" +
            "name: appserver-w-db\n" +
            "location: localhost\n" +
            "services:\n" +
            "- type: brooklyn.entity.webapp.jboss.JBoss7Server\n" +
            "  name: AppServer HelloWorld \n" +
            "  brooklyn.config:\n" +
            "    wars.root: http://search.maven.org/remotecontent?filepath=io/brooklyn/example/brooklyn-example-hello-world-sql-webapp/0.6.0/brooklyn-example-hello-world-sql-webapp-0.6.0.war\n" +
            "    http.port: 8080+\n" +
            "    java.sysprops: \n" +
            "      brooklyn.example.db.url: $brooklyn:formatString(\"jdbc:%s%s?user=%s\\\\&password=%s\",\n" +
            "         component(\"db\").attributeWhenReady(\"datastore.url\"), \"visitors\", \"brooklyn\", \"br00k11n\")\n" +
            "- type: brooklyn.entity.database.mysql.MySqlNode\n" +
            "  id: db\n" +
            "  name: DB HelloWorld Visitors\n" +
            "  brooklyn.config:\n" +
            "    datastore.creation.script.url: https://github.com/brooklyncentral/brooklyn/raw/master/usage/launcher/src/test/resources/visitors-creation-script.sql";


    @BeforeClass
    public void setup(){
        api = new BrooklynApi(BROOKLYN_ENDPOINT);
        applicationApi = api.getApplicationApi();

        // Deploy WebServer + DB application
        Response appDeployedRes = applicationApi.createFromYaml(webServerAndDbYaml);
        brooklyn.rest.domain.TaskSummary res = BrooklynApi.getEntity(appDeployedRes, brooklyn.rest.domain.TaskSummary.class);


        appId = res.getEntityId();
        appName = res.getEntityDisplayName();
        moduleId = applicationApi.getDescendants(appId,".*jboss.*").iterator().next().getId();

        sensorApi = api.getSensorApi();
        sensorSummaryList = sensorApi.list(appId, moduleId);

        application = new Application(appId,appName);
        webServerModule = new ApplicationModule(moduleId,application,application);

    }

    @Test
    public void brooklynApiTest(){
        Object res = sensorApi.get(appId, moduleId, "java.metrics.physicalmemory.free", false);
    }

    @Test
    public void wrongSensorNameTest(){
        Object res = sensorApi.get(appId, moduleId, "this.is.not.a.sensor", false);
        Assert.assertNull(res);
    }

    @Test
    public void registerAgent() throws MonitorConnectorException {
        Manager manager = Manager.getInstance();
        manager.registerApplication(application);
        manager.addMonitoringAgent(application, new BrooklynConnector(webServerModule, BROOKLYN_ENDPOINT));
    }

    @Test(expectedExceptions = MetricNotFoundException.class)
    public void wrongSensorTest() throws MetricNotFoundException, MonitorConnectorException {
        Manager manager = Manager.getInstance();
        manager.addMonitoringAgent(webServerModule, new BrooklynConnector(webServerModule, BROOKLYN_ENDPOINT));
        manager.getMetricValue(webServerModule, "this.is.not.a.metric.id");
    }

    @Test
    public void sensorTest() throws MetricNotFoundException, MonitorConnectorException {
        Manager manager = Manager.getInstance();
        manager.addMonitoringAgent(webServerModule, new BrooklynConnector(webServerModule, BROOKLYN_ENDPOINT));
        manager.getMetricValue(webServerModule, MetricCatalog.PREDEFINED_METRICS.JAVA_HEAP_COMMITED.getValue().getId());
    }

    @AfterClass
    public void destroy(){
        applicationApi.delete(appId);
    }
}
