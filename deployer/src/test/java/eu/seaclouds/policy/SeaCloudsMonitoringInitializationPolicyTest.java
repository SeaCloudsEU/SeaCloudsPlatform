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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.net.MediaType;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.apache.brooklyn.api.entity.EntitySpec;
import org.apache.brooklyn.api.entity.ImplementedBy;
import org.apache.brooklyn.api.location.LocationSpec;
import org.apache.brooklyn.api.mgmt.LocationManager;
import org.apache.brooklyn.api.mgmt.ManagementContext;
import org.apache.brooklyn.api.policy.PolicySpec;
import org.apache.brooklyn.camp.brooklyn.BrooklynCampConstants;
import org.apache.brooklyn.core.entity.Attributes;
import org.apache.brooklyn.core.entity.Entities;
import org.apache.brooklyn.core.entity.factory.ApplicationBuilder;
import org.apache.brooklyn.core.entity.trait.Startable;
import org.apache.brooklyn.core.test.entity.TestApplication;
import org.apache.brooklyn.entity.software.base.EmptySoftwareProcess;
import org.apache.brooklyn.entity.software.base.EmptySoftwareProcessImpl;
import org.apache.brooklyn.location.ssh.SshMachineLocation;
import org.apache.brooklyn.test.Asserts;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SeaCloudsMonitoringInitializationPolicyTest {
    private static final Logger log = LoggerFactory.getLogger(SeaCloudsMonitoringInitializationPolicyTest.class);

    private MockWebServer mockWebServer;

    private SshMachineLocation loc;
    private ManagementContext managementContext;
    private LocationManager locationManager;
    private TestApplication app;

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        app = ApplicationBuilder.newManagedApp(TestApplication.class);
        managementContext = app.getManagementContext();

        locationManager = managementContext.getLocationManager();
        loc = locationManager.createLocation(LocationSpec.create(SshMachineLocation.class)
                .configure("address", "localhost"));

        mockWebServer = new MockWebServer();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() throws Exception {
        log.info("Destroy all {}", new Object[]{this});
        mockWebServer.shutdown();
        if (app != null) {
            Entities.destroyAll(app.getManagementContext());
        }
    }

    @Test
    public void testAttachPolicyToApplicationMock() {

        HttpUrl serverUrl = mockWebServer.url("/resource");

        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.ACCEPT, MediaType.JSON_UTF_8.toString())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString()));

        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.ACCEPT, MediaType.JSON_UTF_8.toString())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString()));

        final TestSoftwareWithSensors childX = app.createAndManageChild(
                EntitySpec.create(TestSoftwareWithSensors.class)
                        .configure(BrooklynCampConstants.PLAN_ID, "childX"));

        final TestSoftwareWithSensors childY = app.createAndManageChild(
                EntitySpec.create(TestSoftwareWithSensors.class)
                        .configure(BrooklynCampConstants.PLAN_ID, "childY"));

        app.createAndManageChild(
                EntitySpec.create(EmptySoftwareProcess.class)
                        .configure(BrooklynCampConstants.PLAN_ID, "childZ"));

        app.policies().add(PolicySpec.create(SeaCloudsMonitoringInitializationPolicies.class)
                .configure(SeaCloudsMonitoringInitializationPolicies.TARGET_ENTITIES, ImmutableList.of("childX", "childY"))
                .configure(SeaCloudsMonitoringInitializationPolicies.SEACLOUDS_DC_ENDPOINT, serverUrl.toString()));

        app.start(ImmutableList.of(loc));

        assertTrue(Iterables.getOnlyElement(app.policies()) instanceof SeaCloudsMonitoringInitializationPolicies);

        Asserts.succeedsEventually(new Runnable() {
            public void run() {
                assertTrue(app.getAttribute(Startable.SERVICE_UP));
                assertTrue(childX.getAttribute(Startable.SERVICE_UP));
                assertTrue(childY.getAttribute(Startable.SERVICE_UP));
                assertTrue(app.getAttribute(SeaCloudsMonitoringInitializationPolicies.MONITORING_CONFIGURED));
                assertEquals(mockWebServer.getRequestCount(), 2);
            }
        });
    }


    @ImplementedBy(TestSoftwareWithSensorsImpl.class)
    public interface TestSoftwareWithSensors extends EmptySoftwareProcess {

    }

    public static class TestSoftwareWithSensorsImpl extends EmptySoftwareProcessImpl
            implements TestSoftwareWithSensors {

        public TestSoftwareWithSensorsImpl() {
            super();
        }

        @Override
        protected void connectSensors() {
            super.connectSensors();
            this.sensors().set(Attributes.MAIN_URI, getFakeUrl());
        }

        private URI getFakeUrl() {
            return URI.create("http://127.0.0.1/");
        }
    }

}