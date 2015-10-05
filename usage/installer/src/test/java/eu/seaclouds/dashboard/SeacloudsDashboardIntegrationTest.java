/*
 * Copyright 2014 SeaClouds
 * Contact: dev@seaclouds-project.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.seaclouds.dashboard;

import com.google.common.collect.ImmutableList;
import org.apache.brooklyn.api.entity.Application;
import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.api.entity.EntitySpec;
import org.apache.brooklyn.core.entity.Entities;
import org.apache.brooklyn.core.entity.trait.Startable;
import org.apache.brooklyn.core.test.entity.TestApplication;
import org.apache.brooklyn.launcher.camp.SimpleYamlLauncher;
import org.apache.brooklyn.location.localhost.LocalhostMachineProvisioningLocation;
import org.apache.brooklyn.test.Asserts;
import org.apache.brooklyn.test.EntityTestUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;

public class SeacloudsDashboardIntegrationTest {

    private TestApplication app;
    private LocalhostMachineProvisioningLocation localhostProvisioningLocation;

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        localhostProvisioningLocation = new LocalhostMachineProvisioningLocation();
        app = TestApplication.Factory.newManagedInstanceForTests();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() throws Exception {
        if (app != null) Entities.destroyAll(app.getManagementContext());
    }

    @Test(groups = "Integration")
    public void testFromYaml() {
        SimpleYamlLauncher launcher = new SimpleYamlLauncher();
        launcher.setShutdownAppsOnExit(true);
        Application app = launcher.launchAppYaml("dashboard-single.yaml").getApplication();

        final SeacloudsDashboard dashboard = (SeacloudsDashboard)
                findEntityChildByDisplayName(app, "Dashboard");

        Asserts.succeedsEventually(new Runnable() {
            public void run() {
                EntityTestUtils.assertAttributeEqualsEventually(dashboard, Startable.SERVICE_UP, true);
                EntityTestUtils.assertAttributeEquals(dashboard, SeacloudsDashboard.DASHBOARD_PORT, 8000);
                EntityTestUtils.assertAttributeEquals(dashboard, SeacloudsDashboard.DASHBOARD_ADMIN_PORT, 8001);

                EntityTestUtils.assertConfigEquals(dashboard, SeacloudsDashboard.DEPLOYER_HOST, "localhost");
                EntityTestUtils.assertConfigEquals(dashboard, SeacloudsDashboard.DEPLOYER_PORT, 8081);
                EntityTestUtils.assertConfigEquals(dashboard, SeacloudsDashboard.DEPLOYER_USERNAME, "admin");
                EntityTestUtils.assertConfigEquals(dashboard, SeacloudsDashboard.DEPLOYER_PASSWORD, "p4ssw0rd");

                EntityTestUtils.assertConfigEquals(dashboard, SeacloudsDashboard.MONITOR_HOST, "localhost");
                EntityTestUtils.assertConfigEquals(dashboard, SeacloudsDashboard.MONITOR_PORT, 8170);

                EntityTestUtils.assertConfigEquals(dashboard, SeacloudsDashboard.SLA_HOST, "localhost");
                EntityTestUtils.assertConfigEquals(dashboard, SeacloudsDashboard.SLA_PORT, 8080);

                dashboard.stop();
                Assert.assertFalse(dashboard.getAttribute(Startable.SERVICE_UP));
            }
        });
    }

    private Entity findEntityChildByDisplayName(Application app, String displayName) {
        for (Object entity : app.getChildren().toArray())
            if (((Entity) entity).getDisplayName().equals(displayName)) {
                return (Entity) entity;
            }
        return null;
    }

    @Test(groups = "Integration")
    public void testCanStartAndStop() throws Exception {
        SeacloudsDashboard entity = app.createAndManageChild(
                EntitySpec.create(SeacloudsDashboard.class)
                        .configure(SeacloudsDashboard.DEPLOYER_HOST, "localhost")
                        .configure(SeacloudsDashboard.DEPLOYER_PORT, 8081)
                        .configure(SeacloudsDashboard.DEPLOYER_USERNAME, "user")
                        .configure(SeacloudsDashboard.DEPLOYER_PASSWORD, "password")
                        .configure(SeacloudsDashboard.MONITOR_HOST, "localhost")
                        .configure(SeacloudsDashboard.MONITOR_PORT, 8170)
                        .configure(SeacloudsDashboard.SLA_HOST, "localhost")
                        .configure(SeacloudsDashboard.SLA_PORT, 8080)
        );

        app.start(ImmutableList.of(localhostProvisioningLocation));

        EntityTestUtils.assertAttributeEqualsEventually(entity, Startable.SERVICE_UP, true);
        EntityTestUtils.assertAttributeEquals(entity, SeacloudsDashboard.DASHBOARD_PORT, 8000);
        EntityTestUtils.assertAttributeEquals(entity, SeacloudsDashboard.DASHBOARD_ADMIN_PORT, 8001);
        
        EntityTestUtils.assertConfigEquals(entity, SeacloudsDashboard.DEPLOYER_HOST, "localhost");
        EntityTestUtils.assertConfigEquals(entity, SeacloudsDashboard.DEPLOYER_PORT, 8081);
        EntityTestUtils.assertConfigEquals(entity, SeacloudsDashboard.DEPLOYER_USERNAME, "user");
        EntityTestUtils.assertConfigEquals(entity, SeacloudsDashboard.DEPLOYER_PASSWORD, "password");
        
        EntityTestUtils.assertConfigEquals(entity, SeacloudsDashboard.MONITOR_HOST, "localhost");
        EntityTestUtils.assertConfigEquals(entity, SeacloudsDashboard.MONITOR_PORT, 8170);
        
        EntityTestUtils.assertConfigEquals(entity, SeacloudsDashboard.SLA_HOST, "localhost");
        EntityTestUtils.assertConfigEquals(entity, SeacloudsDashboard.SLA_PORT, 8080);

        entity.stop();
        assertFalse(entity.getAttribute(Startable.SERVICE_UP));
    }

}

