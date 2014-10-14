import brooklyn.management.ManagementContext;
import brooklyn.rest.api.ApplicationApi;
import brooklyn.rest.api.SensorApi;
import brooklyn.rest.client.BrooklynApi;
import brooklyn.rest.domain.ApplicationSummary;
import brooklyn.rest.domain.SensorSummary;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;
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
    private String moduleId;

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
        BrooklynApi.getEntity(appDeployedRes, String.class);
        appId = applicationApi.list().iterator().next().getId();
        moduleId = applicationApi.getDescendants(appId,".*jboss.*").iterator().next().getId();

        sensorApi = api.getSensorApi();
        sensorSummaryList = sensorApi.list(appId, moduleId);
    }

    @Test(groups = {"BrooklynApi"})
    public void brooklynApiTest(){
        Object res = sensorApi.get(appId, moduleId, "java.metrics.physicalmemory.free", false);
    }

    @Test(groups = {"BrooklynApi"})
    public void wrongSensorNameTest(){
        Object res = sensorApi.get(appId, moduleId, "this.is.not.a.sensor", false);
        Assert.assertNull(res);
    }

    @AfterClass
    public void destroy(){
        applicationApi.delete(appId);
    }
}
