/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package eu.seaclouds.location.cloudfoundry;

import org.apache.brooklyn.api.entity.Application;
import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.api.policy.Policy;
import org.apache.brooklyn.api.sensor.AttributeSensor;
import org.apache.brooklyn.camp.brooklyn.BrooklynCampConstants;
import org.apache.brooklyn.core.entity.Attributes;
import org.apache.brooklyn.core.entity.trait.Startable;
import org.apache.brooklyn.entity.cloudfoundry.services.CloudFoundryService;
import org.apache.brooklyn.entity.cloudfoundry.webapp.CloudFoundryWebApp;
import org.apache.brooklyn.entity.cloudfoundry.webapp.java.JavaCloudFoundryPaasWebApp;
import org.apache.brooklyn.launcher.camp.SimpleYamlLauncher;
import org.apache.brooklyn.location.cloudfoundry.PaasLocationConfig;
import org.apache.brooklyn.test.Asserts;
import org.apache.brooklyn.util.text.Strings;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = {"Live"})
public class CloudFoundryYamlLiveTest {

    private final String JDBC_SENSOR = ".credentials.jdbcUrl";
    private final String NAME_SENSOR = ".credentials.name";
    private final String HOSTNAME_SENSOR = ".credentials.hostname";
    private final String USERNAME_SENSOR = ".credentials.username";
    private final String PASSWORD_SENSOR = ".credentials.password";
    private final String PORT_SENSOR = ".credentials.port";
    private final String SERVICE_TYPE_ID = "cleardb";
    private final String SERVICE_PLAN = "spark";

    public void deployWebappWithServicesFromYaml() {
        SimpleYamlLauncher launcher = new SimpleYamlLauncher();
        launcher.setShutdownAppsOnExit(true);
        Application app = launcher.launchAppYaml("cf-webapp-db.yaml").getApplication();

        final CloudFoundryService service = (CloudFoundryService)
                findChildEntitySpecByPlanId(app, "db");

        final CloudFoundryWebApp server = (CloudFoundryWebApp)
                findChildEntitySpecByPlanId(app, "webapp");

        Asserts.succeedsEventually(new Runnable() {
            public void run() {

                assertNotNull(server);
                assertNotNull(service);

                assertEquals(server.getAttribute(CloudFoundryWebApp.BOUND_SERVICES).size(), 1);
                assertTrue(server.getAttribute(Startable.SERVICE_UP));
                assertTrue(server.getAttribute(JavaCloudFoundryPaasWebApp
                        .SERVICE_PROCESS_IS_RUNNING));

                assertNotNull(server.getAttribute(Attributes.MAIN_URI));
                assertNotNull(server.getAttribute(JavaCloudFoundryPaasWebApp.ROOT_URL));

                assertEquals(server.getAttribute(JavaCloudFoundryPaasWebApp.DISK),
                        PaasLocationConfig.REQUIRED_DISK.getDefaultValue());
                assertEquals(server.getAttribute(JavaCloudFoundryPaasWebApp.INSTANCES_NUM),
                        PaasLocationConfig.REQUIRED_INSTANCES.getDefaultValue());
                assertEquals(server.getAttribute(JavaCloudFoundryPaasWebApp.MEMORY),
                        PaasLocationConfig.REQUIRED_MEMORY.getDefaultValue());

                assertNotNull(server.getAttribute(CloudFoundryWebApp.VCAP_SERVICES));
                assertFalse(Strings.isBlank(server.getAttribute(CloudFoundryWebApp.VCAP_SERVICES)));

                //service
                assertTrue(service.getAttribute(Startable.SERVICE_UP));
                assertTrue(service.getAttribute(JavaCloudFoundryPaasWebApp
                        .SERVICE_PROCESS_IS_RUNNING));

                assertEquals(service.getAttribute(CloudFoundryService.SERVICE_TYPE_ID),
                        SERVICE_TYPE_ID);
                assertEquals(service.getConfig(CloudFoundryService.PLAN), SERVICE_PLAN);
                assertEquals(service.getConfig(CloudFoundryService.SERVICE_INSTANCE_NAME),
                        "test-brooklyn-service-mysql-from-yaml");

                //dynamicSensors for credentials
                assertFalse(Strings.isBlank((String) findSensorValueByName(service,
                        "test-brooklyn-app-from-yaml" + JDBC_SENSOR)));
                assertFalse(Strings.isBlank((String) findSensorValueByName(service,
                        "test-brooklyn-app-from-yaml" + NAME_SENSOR)));
                assertFalse(Strings.isBlank((String) findSensorValueByName(service,
                        "test-brooklyn-app-from-yaml" + HOSTNAME_SENSOR)));
                assertFalse(Strings.isBlank((String) findSensorValueByName(service,
                        "test-brooklyn-app-from-yaml" + USERNAME_SENSOR)));
                assertFalse(Strings.isBlank((String) findSensorValueByName(service,
                        "test-brooklyn-app-from-yaml" + PASSWORD_SENSOR)));
                assertFalse(Strings.isBlank((String) findSensorValueByName(service,
                        "test-brooklyn-app-from-yaml" + PORT_SENSOR)));
            }
        });
    }

    @Test(enabled = false)
    public void deployWebappWithUserProvidedServicesFromYaml() {
        SimpleYamlLauncher launcher = new SimpleYamlLauncher();
        launcher.setShutdownAppsOnExit(true);
        Application app = launcher.launchAppYaml("webapp-db-user-provided.yaml").getApplication();

        final CloudFoundryService service = (CloudFoundryService)
                findChildEntitySpecByPlanId(app, "db");

        final CloudFoundryWebApp webapp = (CloudFoundryWebApp)
                findChildEntitySpecByPlanId(app, "webapp");

        Asserts.succeedsEventually(new Runnable() {
            public void run() {

                assertNotNull(webapp);
                assertNotNull(service);

                assertEquals(webapp.getAttribute(CloudFoundryWebApp.BOUND_SERVICES).size(), 1);
                assertTrue(webapp.getAttribute(Startable.SERVICE_UP));
                assertTrue(webapp.getAttribute(JavaCloudFoundryPaasWebApp
                        .SERVICE_PROCESS_IS_RUNNING));

                assertNotNull(webapp.getAttribute(Attributes.MAIN_URI));
                assertNotNull(webapp.getAttribute(JavaCloudFoundryPaasWebApp.ROOT_URL));

                assertEquals(webapp.getAttribute(JavaCloudFoundryPaasWebApp.DISK),
                        PaasLocationConfig.REQUIRED_DISK.getDefaultValue());
                assertEquals(webapp.getAttribute(JavaCloudFoundryPaasWebApp.INSTANCES_NUM),
                        PaasLocationConfig.REQUIRED_INSTANCES.getDefaultValue());
                assertEquals(webapp.getAttribute(JavaCloudFoundryPaasWebApp.MEMORY),
                        PaasLocationConfig.REQUIRED_MEMORY.getDefaultValue());

                assertNotNull(webapp.getAttribute(CloudFoundryWebApp.VCAP_SERVICES));
                assertFalse(Strings.isBlank(webapp.getAttribute(CloudFoundryWebApp.VCAP_SERVICES)));

                //db
                assertTrue(service.getAttribute(Startable.SERVICE_UP));
                assertTrue(service.getAttribute(JavaCloudFoundryPaasWebApp
                        .SERVICE_PROCESS_IS_RUNNING));

                assertEquals(service.getAttribute(CloudFoundryService.SERVICE_TYPE_ID),
                        SERVICE_TYPE_ID);
                assertEquals(service.getConfig(CloudFoundryService.PLAN), SERVICE_PLAN);
                assertEquals(service.getConfig(CloudFoundryService.SERVICE_INSTANCE_NAME),
                        "test-brooklyn-app-from-yaml");

                //dynamicSensors for credentials
                assertFalse(Strings.isBlank((String) findSensorValueByName(service,
                        "test-brooklyn-app-from-yaml" + JDBC_SENSOR)));
                assertFalse(Strings.isBlank((String) findSensorValueByName(service,
                        "test-brooklyn-app-from-yaml" + NAME_SENSOR)));
                assertFalse(Strings.isBlank((String) findSensorValueByName(service,
                        "test-brooklyn-app-from-yaml" + HOSTNAME_SENSOR)));
                assertFalse(Strings.isBlank((String) findSensorValueByName(service,
                        "test-brooklyn-app-from-yaml" + USERNAME_SENSOR)));
                assertFalse(Strings.isBlank((String) findSensorValueByName(service,
                        "test-brooklyn-app-from-yaml" + PASSWORD_SENSOR)));
                assertFalse(Strings.isBlank((String) findSensorValueByName(service,
                        "test-brooklyn-app-from-yaml" + PORT_SENSOR)));
            }
        });

    }

    public void testDeployWebappUsingServicesJMMonitoringFromYaml() {
        SimpleYamlLauncher launcher = new SimpleYamlLauncher();
        launcher.setShutdownAppsOnExit(false);
        Application app = launcher.launchAppYaml("cf-webapp-db-Monitoring.yaml").getApplication();

        final CloudFoundryService service = (CloudFoundryService)
                findChildEntitySpecByPlanId(app, "db");

        final CloudFoundryWebApp server = (CloudFoundryWebApp)
                findChildEntitySpecByPlanId(app, "webapp");

        assertNotNull(server);
        assertNotNull(service);

        Asserts.succeedsEventually(new Runnable() {
            public void run() {

                assertNotNull(server.getAttribute(CloudFoundryWebApp.SERVICE_PROCESS_IS_RUNNING));

                assertEquals(server.getAttribute(CloudFoundryWebApp.BOUND_SERVICES).size(), 1);
                assertTrue(server.getAttribute(Startable.SERVICE_UP));
                assertTrue(server.getAttribute(JavaCloudFoundryPaasWebApp
                        .SERVICE_PROCESS_IS_RUNNING));

                assertNotNull(server.getAttribute(Attributes.MAIN_URI));
                assertNotNull(server.getAttribute(JavaCloudFoundryPaasWebApp.ROOT_URL));

                assertEquals(server.getAttribute(JavaCloudFoundryPaasWebApp.DISK),
                        PaasLocationConfig.REQUIRED_DISK.getDefaultValue());
                assertEquals(server.getAttribute(JavaCloudFoundryPaasWebApp.INSTANCES_NUM),
                        PaasLocationConfig.REQUIRED_INSTANCES.getDefaultValue());
                assertEquals(server.getAttribute(JavaCloudFoundryPaasWebApp.MEMORY),
                        PaasLocationConfig.REQUIRED_MEMORY.getDefaultValue());

                assertNotNull(server.getAttribute(CloudFoundryWebApp.VCAP_SERVICES));
                assertFalse(Strings.isBlank(server.getAttribute(CloudFoundryWebApp.VCAP_SERVICES)));

                //service
                assertTrue(service.getAttribute(Startable.SERVICE_UP));
                assertTrue(service.getAttribute(JavaCloudFoundryPaasWebApp
                        .SERVICE_PROCESS_IS_RUNNING));

                assertEquals(service.getAttribute(CloudFoundryService.SERVICE_TYPE_ID),
                        SERVICE_TYPE_ID);
                assertEquals(service.getConfig(CloudFoundryService.PLAN), SERVICE_PLAN);
                assertEquals(service.getConfig(CloudFoundryService.SERVICE_INSTANCE_NAME),
                        "test-brooklyn-service-mysql");

                assertFalse(Strings.isBlank((String) findSensorValueByName(service,
                        "test-brooklyn-monitor-app.credentials.jdbcUrl")));
                assertFalse(Strings.isBlank((String) findSensorValueByName(service,
                        "test-brooklyn-monitor-app.credentials.name")));
                assertFalse(Strings.isBlank((String) findSensorValueByName(service,
                        "test-brooklyn-monitor-app.credentials.hostname")));
                assertFalse(Strings.isBlank((String) findSensorValueByName(service,
                        "test-brooklyn-monitor-app.credentials.username")));
                assertFalse(Strings.isBlank((String) findSensorValueByName(service,
                        "test-brooklyn-monitor-app.credentials.password")));
                assertFalse(Strings.isBlank((String) findSensorValueByName(service,
                        "test-brooklyn-monitor-app.credentials.port")));

                assertNotNull(server.getAttribute(JavaCloudFoundryPaasWebApp.SERVER_PROCESSING_TIME));
                assertNotNull(server.getAttribute(JavaCloudFoundryPaasWebApp.SERVER_REQUESTS));
            }
        });
    }

    public void testDeployWebappWitPolicyJMMonitoringFromYaml() {
        SimpleYamlLauncher launcher = new SimpleYamlLauncher();
        launcher.setShutdownAppsOnExit(false);
        Application app = launcher.launchAppYaml("cf-webchat-policy.yml").getApplication();

        final CloudFoundryWebApp server = (CloudFoundryWebApp)
                findChildEntitySpecByPlanId(app, "webapp");

        assertNotNull(server);

        Asserts.succeedsEventually(new Runnable() {
            public void run() {
                assertNotNull(server.getAttribute(CloudFoundryWebApp.SERVICE_PROCESS_IS_RUNNING));

                assertEquals(server.getAttribute(CloudFoundryWebApp.BOUND_SERVICES).size(), 0);
                assertTrue(server.getAttribute(Startable.SERVICE_UP));
                assertTrue(server.getAttribute(JavaCloudFoundryPaasWebApp
                        .SERVICE_PROCESS_IS_RUNNING));

                assertNotNull(server.getAttribute(Attributes.MAIN_URI));
                assertNotNull(server.getAttribute(JavaCloudFoundryPaasWebApp.ROOT_URL));

                assertEquals(server.getAttribute(JavaCloudFoundryPaasWebApp.DISK),
                        PaasLocationConfig.REQUIRED_DISK.getDefaultValue());
                assertEquals(server.getAttribute(JavaCloudFoundryPaasWebApp.INSTANCES_NUM),
                        PaasLocationConfig.REQUIRED_INSTANCES.getDefaultValue());
                assertEquals(server.getAttribute(JavaCloudFoundryPaasWebApp.MEMORY),
                        PaasLocationConfig.REQUIRED_MEMORY.getDefaultValue());

                assertNotNull(server.getPolicies());
                assertEquals(server.getPolicies().size(), 1);

                assertNotNull(server.getAttribute(JavaCloudFoundryPaasWebApp.SERVER_PROCESSING_TIME));
                assertNotNull(server.getAttribute(JavaCloudFoundryPaasWebApp.SERVER_REQUESTS));
            }
        });
    }

    private Entity findChildEntitySpecByPlanId(Application app, String planId) {
        for (Entity child : app.getChildren()) {
            String childPlanId = child.getConfig(BrooklynCampConstants.PLAN_ID);
            if ((childPlanId != null) && (childPlanId.equals(planId))) {
                return child;
            }
        }
        return null;
    }

    private Object findSensorValueByName(Entity entity, String sensorName) {
        AttributeSensor<Object> sensor = findSensorByName(entity, sensorName);
        return entity.getAttribute(sensor);
    }

    @SuppressWarnings("unchecked")
    private AttributeSensor<Object> findSensorByName(Entity entity, String sensorName) {
        return (AttributeSensor<Object>) entity.getEntityType().getSensor(sensorName);
    }


}
