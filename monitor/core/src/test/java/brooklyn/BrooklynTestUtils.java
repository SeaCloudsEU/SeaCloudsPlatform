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
package brooklyn;

import brooklyn.rest.api.ApplicationApi;
import brooklyn.rest.api.SensorApi;
import brooklyn.rest.client.BrooklynApi;
import brooklyn.rest.domain.SensorSummary;
import brooklyn.rest.domain.Status;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import javax.ws.rs.core.Response;
import java.util.List;

public class BrooklynTestUtils {

    public static final String BROOKLYN_ENDPOINT = "http://devtest.scenic.uma.es:8081";

    private static BrooklynTestUtils instance;

    private static BrooklynApi api;
    private static ApplicationApi applicationApi;
    private static SensorApi sensorApi;
    private static List<SensorSummary> sensorSummaryList;
    private static String appId;
    private static String appName;

    public static BrooklynApi getApi() {
        return api;
    }

    public static ApplicationApi getApplicationApi() {
        return applicationApi;
    }

    public static String getAppName() {
        return appName;
    }

    public static SensorApi getSensorApi() {
        return sensorApi;
    }

    public static String getAppId() {
        return appId;
    }

    public static BrooklynTestUtils getInstance(){
        if (instance == null){
            instance = new BrooklynTestUtils();
        }
        return instance;
    }

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

    @BeforeSuite
    public void setup(){
        System.out.println("BrooklynTestUtils.setup()");

        api = new BrooklynApi(BROOKLYN_ENDPOINT);
        applicationApi = api.getApplicationApi();

        // Deploy WebServer + DB application
        Response appDeployedRes = applicationApi.createFromYaml(webServerAndDbYaml);
        brooklyn.rest.domain.TaskSummary res = BrooklynApi.getEntity(appDeployedRes, brooklyn.rest.domain.TaskSummary.class);

        appId = res.getEntityId();
        appName = res.getEntityDisplayName();
        sensorApi = api.getSensorApi();
    }

    @AfterSuite
    public void destroy(){
        System.out.println("BrooklynTestUtils.destroy()");

        applicationApi.delete(appId);
    }

    /**
     * Checks if the application (appId) is running.
     * TODO: Implement similar method to check module status
     * @param appId
     * @return true if the application is running
     */
    public static boolean isAppRunning(String appId){
        return api.getApplicationApi().get(appId).getStatus().equals(Status.RUNNING);
    }
}
