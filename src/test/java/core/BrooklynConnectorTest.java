package core;

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
public class BrooklynConnectorTest {
    private static final String BROOKLYN_ENDPOINT = "http://devtest.scenic.uma.es:8081";
    BrooklynApi api = new BrooklynApi(BROOKLYN_ENDPOINT);
    String appId;

    private static final String webServerAndDbYaml = "" +
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

    private BrooklynConnector connector;


    @BeforeClass
    public void setup() throws MonitorConnectorException {

        // Deploy WebServer + DB application
        Response appDeployedRes = api.getApplicationApi().createFromYaml(webServerAndDbYaml);
        brooklyn.rest.domain.TaskSummary res = BrooklynApi.getEntity(appDeployedRes, brooklyn.rest.domain.TaskSummary.class);

        appId = res.getEntityId();
        String appName = res.getEntityDisplayName();
        String moduleId = api.getApplicationApi().getDescendants(appId, ".*jboss.*").iterator().next().getId();

        Application application = new Application(appId,appName);
        Module  webServerModule = new ApplicationModule(moduleId,application,application);

        connector = new BrooklynConnector(webServerModule, BROOKLYN_ENDPOINT);
    }



    @Test
    public void checkAvailableMetrics() throws Exception {
        Assert.assertNotEquals(0, connector.getAvailableMetrics().size());
    }


    @Test
    public void checkGetValue() throws Exception {
        for(Metric metric : connector.getAvailableMetrics()){
            Assert.assertNotNull(connector.getValue(metric));
        }
    }

    @AfterClass
    public void destroy(){
        api.getApplicationApi().delete(appId);
    }

}
