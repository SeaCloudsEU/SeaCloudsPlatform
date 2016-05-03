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
package eu.seaclouds.policy;

import org.apache.brooklyn.api.location.LocationSpec;
import org.apache.brooklyn.api.mgmt.LocationManager;
import org.apache.brooklyn.api.mgmt.ManagementContext;
import org.apache.brooklyn.core.entity.factory.ApplicationBuilder;
import org.apache.brooklyn.core.test.entity.TestApplication;
import org.apache.brooklyn.location.ssh.SshMachineLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class AbstractSeaCloudsPoliciesLiveTests {

    private static final Logger log = LoggerFactory.getLogger(AbstractSeaCloudsPoliciesLiveTests.class);

    //TODO: use mock
    public static final String SLA_ENDPOINT = "http://52.36.119.104:9003";
    public static final String T4C_ENDPOINT = "http://52.48.187.2:8170";
    public static final String INFLUXDB_ENDPOINT = "http://52.48.187.2:8086";
    public static final String INFLUXDB_DATABASE = "tower4clouds";
    public static final String INFLUXDB_USERNAME = "root";
    public static final String INFLUXDB_PASSWORD = "root";
    public static final String GRAFANA_ENDPOINT = "http://52.48.187.2:3000";
    public static final String GRAFANA_USERNAME = "admin";
    public static final String GRAFANA_PASSWORD = "admin";
    public static final String SEACLOUDS_DC_ENDPOINT = "http://52.48.187.2:8176/";

    protected SshMachineLocation loc;
    protected ManagementContext managementContext;
    protected LocationManager locationManager;
    protected TestApplication app;

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        app = ApplicationBuilder.newManagedApp(TestApplication.class);
        managementContext = app.getManagementContext();
        locationManager = managementContext.getLocationManager();
        loc = locationManager.createLocation(LocationSpec.create(SshMachineLocation.class)
                .configure("address", "localhost"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() throws Exception {
        log.info("Destroy all {}", new Object[]{this});
        if (app != null) {
            app.stop();
        }
    }

}
