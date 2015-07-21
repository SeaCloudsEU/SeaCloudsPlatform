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

import brooklyn.entity.Application;
import brooklyn.entity.Entity;
import brooklyn.entity.basic.Entities;
import brooklyn.entity.proxying.EntitySpec;
import brooklyn.entity.trait.Startable;
import brooklyn.launcher.camp.SimpleYamlLauncher;
import brooklyn.location.basic.LocalhostMachineProvisioningLocation;
import brooklyn.test.Asserts;
import brooklyn.test.EntityTestUtils;
import brooklyn.test.entity.TestApplication;
import com.google.common.collect.ImmutableList;
import eu.seaclouds.common.entities.server.SeacloudsServer;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;

public class SeacloudsServerIntegrationTest {

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

        final SeacloudsServer dashboard = (SeacloudsServer)
                findEntityChildByDisplayName(app, "Dashboard");

        Asserts.succeedsEventually(new Runnable() {
            public void run() {
                EntityTestUtils.assertAttributeEqualsEventually(dashboard, Startable.SERVICE_UP, true);
                EntityTestUtils.assertAttributeEquals(dashboard, SeacloudsServer.DASHBOARD_PORT, 8000);
                EntityTestUtils.assertAttributeEquals(dashboard, SeacloudsServer.DASHBOARD_ADMIN_PORT, 8001);

                EntityTestUtils.assertConfigEquals(dashboard, SeacloudsServer.DEPLOYER_HOST, "localhost");
                EntityTestUtils.assertConfigEquals(dashboard, SeacloudsServer.DEPLOYER_PORT, 8081);
                EntityTestUtils.assertConfigEquals(dashboard, SeacloudsServer.DEPLOYER_USERNAME, "admin");
                EntityTestUtils.assertConfigEquals(dashboard, SeacloudsServer.DEPLOYER_PASSWORD, "p4ssw0rd");

                EntityTestUtils.assertConfigEquals(dashboard, SeacloudsServer.MONITOR_HOST, "localhost");
                EntityTestUtils.assertConfigEquals(dashboard, SeacloudsServer.MONITOR_PORT, 8170);

                EntityTestUtils.assertConfigEquals(dashboard, SeacloudsServer.SLA_HOST, "localhost");
                EntityTestUtils.assertConfigEquals(dashboard, SeacloudsServer.SLA_PORT, 8080);

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
        SeacloudsServer entity = app.createAndManageChild(
                EntitySpec.create(SeacloudsServer.class)
                        .configure(SeacloudsServer.DEPLOYER_HOST, "localhost")
                        .configure(SeacloudsServer.DEPLOYER_PORT, 8081)
                        .configure(SeacloudsServer.DEPLOYER_USERNAME, "user")
                        .configure(SeacloudsServer.DEPLOYER_PASSWORD, "password")
                        .configure(SeacloudsServer.MONITOR_HOST, "localhost")
                        .configure(SeacloudsServer.MONITOR_PORT, 8170)
                        .configure(SeacloudsServer.SLA_HOST, "localhost")
                        .configure(SeacloudsServer.SLA_PORT, 8080)
        );

        app.start(ImmutableList.of(localhostProvisioningLocation));

        EntityTestUtils.assertAttributeEqualsEventually(entity, Startable.SERVICE_UP, true);
        EntityTestUtils.assertAttributeEquals(entity, SeacloudsServer.DASHBOARD_PORT, 8000);
        EntityTestUtils.assertAttributeEquals(entity, SeacloudsServer.DASHBOARD_ADMIN_PORT, 8001);
        
        EntityTestUtils.assertConfigEquals(entity, SeacloudsServer.DEPLOYER_HOST, "localhost");
        EntityTestUtils.assertConfigEquals(entity, SeacloudsServer.DEPLOYER_PORT, 8081);
        EntityTestUtils.assertConfigEquals(entity, SeacloudsServer.DEPLOYER_USERNAME, "user");
        EntityTestUtils.assertConfigEquals(entity, SeacloudsServer.DEPLOYER_PASSWORD, "password");
        
        EntityTestUtils.assertConfigEquals(entity, SeacloudsServer.MONITOR_HOST, "localhost");
        EntityTestUtils.assertConfigEquals(entity, SeacloudsServer.MONITOR_PORT, 8170);
        
        EntityTestUtils.assertConfigEquals(entity, SeacloudsServer.SLA_HOST, "localhost");
        EntityTestUtils.assertConfigEquals(entity, SeacloudsServer.SLA_PORT, 8080);

        entity.stop();
        assertFalse(entity.getAttribute(Startable.SERVICE_UP));
    }

}

