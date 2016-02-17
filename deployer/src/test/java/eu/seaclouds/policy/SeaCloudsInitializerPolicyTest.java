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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.apache.brooklyn.api.entity.EntitySpec;
import org.apache.brooklyn.api.location.LocationSpec;
import org.apache.brooklyn.api.mgmt.LocationManager;
import org.apache.brooklyn.api.mgmt.ManagementContext;
import org.apache.brooklyn.api.policy.Policy;
import org.apache.brooklyn.api.policy.PolicySpec;
import org.apache.brooklyn.core.entity.Entities;
import org.apache.brooklyn.core.entity.factory.ApplicationBuilder;
import org.apache.brooklyn.core.entity.trait.Startable;
import org.apache.brooklyn.core.test.entity.TestApplication;
import org.apache.brooklyn.entity.php.PhpWebAppSoftwareProcess;
import org.apache.brooklyn.entity.php.httpd.PhpHttpdServer;
import org.apache.brooklyn.entity.software.base.EmptySoftwareProcess;
import org.apache.brooklyn.entity.software.base.VanillaSoftwareProcess;
import org.apache.brooklyn.location.ssh.SshMachineLocation;
import org.apache.brooklyn.test.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.Assert.*;

public class SeaCloudsInitializerPolicyTest {
    private static final Logger log = LoggerFactory.getLogger(SeaCloudsInitializerPolicyTest.class);
    private static final String BASE64_AGREEMENT = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/Pg0KPHdzYWc6QWdyZWVtZW50IHhtbG5zOnNsYT0iaHR0cDovL3NsYS5hdG9zLmV1IiB4bWxuczp3c2FnPSJodHRwOi8vd3d3LmdnZi5vcmcvbmFtZXNwYWNlcy93cy1hZ3JlZW1lbnQiIHdzYWc6QWdyZWVtZW50SWQ9ImFwcGlkIj4NCiAgICA8d3NhZzpOYW1lPnVzZXItc2VhY2xvdWRzLUNoYXQgQXBwbGljYXRpb24gdXNpbmcgSkJvc3M8L3dzYWc6TmFtZT4NCiAgICA8d3NhZzpDb250ZXh0Pg0KICAgICAgICA8d3NhZzpBZ3JlZW1lbnRJbml0aWF0b3I+dXNlcjwvd3NhZzpBZ3JlZW1lbnRJbml0aWF0b3I+DQogICAgICAgIDx3c2FnOkFncmVlbWVudFJlc3BvbmRlcj5zZWFjbG91ZHM8L3dzYWc6QWdyZWVtZW50UmVzcG9uZGVyPg0KICAgICAgICA8d3NhZzpTZXJ2aWNlUHJvdmlkZXI+QWdyZWVtZW50UmVzcG9uZGVyPC93c2FnOlNlcnZpY2VQcm92aWRlcj4NCiAgICAgICAgPHdzYWc6RXhwaXJhdGlvblRpbWU+MjAxNy0wMi0wOFQxNjo0MToyOSswMDAwPC93c2FnOkV4cGlyYXRpb25UaW1lPg0KICAgICAgICA8c2xhOlNlcnZpY2U+Q2hhdCBBcHBsaWNhdGlvbiB1c2luZyBKQm9zczwvc2xhOlNlcnZpY2U+DQogICAgPC93c2FnOkNvbnRleHQ+DQogICAgPHdzYWc6VGVybXM+DQogICAgICAgIDx3c2FnOkFsbD4NCiAgICAgICAgICAgIDx3c2FnOlNlcnZpY2VEZXNjcmlwdGlvblRlcm0vPg0KICAgICAgICAgICAgPHdzYWc6R3VhcmFudGVlVGVybSB3c2FnOk5hbWU9ImFwcGlkX215c3FsX3NlcnZlcl9jcHVfdXRpbGl6YXRpb24iPg0KICAgICAgICAgICAgICAgIDx3c2FnOlNlcnZpY2VTY29wZSB3c2FnOlNlcnZpY2VOYW1lPSJzZXJ2aWNlIj5hcHBpZF9teXNxbF9zZXJ2ZXI8L3dzYWc6U2VydmljZVNjb3BlPg0KICAgICAgICAgICAgICAgIDx3c2FnOlNlcnZpY2VMZXZlbE9iamVjdGl2ZT4NCiAgICAgICAgICAgICAgICAgICAgPHdzYWc6S1BJVGFyZ2V0Pg0KICAgICAgICAgICAgICAgICAgICAgICAgPHdzYWc6S1BJTmFtZT5hcHBpZF9teXNxbF9zZXJ2ZXIvQ1BVVXRpbGl6YXRpb248L3dzYWc6S1BJTmFtZT4NCiAgICAgICAgICAgICAgICAgICAgICAgIDx3c2FnOkN1c3RvbVNlcnZpY2VMZXZlbD57ImNvbnN0cmFpbnQiOiAiYXBwaWRfbXlzcWxfc2VydmVyX2NwdV91dGlsaXphdGlvbiBOT1RfRVhJU1RTIiwgInFvcyI6ICJNRVRSSUMgTEUgMC41IiB9PC93c2FnOkN1c3RvbVNlcnZpY2VMZXZlbD4NCiAgICAgICAgICAgICAgICAgICAgPC93c2FnOktQSVRhcmdldD4NCiAgICAgICAgICAgICAgICA8L3dzYWc6U2VydmljZUxldmVsT2JqZWN0aXZlPg0KICAgICAgICAgICAgPC93c2FnOkd1YXJhbnRlZVRlcm0+DQogICAgICAgIDwvd3NhZzpBbGw+DQogICAgPC93c2FnOlRlcm1zPg0KPC93c2FnOkFncmVlbWVudD4NCg==";
    private static final String BASE64_RULES = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/Pg0KPG1vbml0b3JpbmdSdWxlcyB4bWxucz0iaHR0cDovL3d3dy5tb2RhY2xvdWRzLmV1L3hzZC8xLjAvbW9uaXRvcmluZ19ydWxlc19zY2hlbWEiIHhtbG5zOnhzaT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEtaW5zdGFuY2UiIHhzaTpzY2hlbWFMb2NhdGlvbj0iaHR0cDovL3d3dy5tb2RhY2xvdWRzLmV1L3hzZC8xLjAvbW9uaXRvcmluZ19ydWxlc19zY2hlbWEiPg0KICAgIDwhLS0gTW9uaXRvcmluZyBydWxlIGlkIHBhdHRlcm4gYXBwaWRfdG9zY2Fsb2NhdGlvbm5hbWVfbWV0cmljIC0tPg0KICAgIDxtb25pdG9yaW5nUnVsZSBpZD0iYXBwaWRfbXlzcWxfc2VydmVyX2NwdV91dGlsaXphdGlvbiIgdGltZVN0ZXA9IjEwIiB0aW1lV2luZG93PSIxMCI+DQogICAgICAgIDxtb25pdG9yZWRUYXJnZXRzPg0KICAgICAgICAgICAgPG1vbml0b3JlZFRhcmdldCB0eXBlPSJhcHBpZF9teXNxbF9zZXJ2ZXIiIGNsYXNzPSJWTSIvPg0KICAgICAgICA8L21vbml0b3JlZFRhcmdldHM+DQogICAgICAgIDxjb2xsZWN0ZWRNZXRyaWMgbWV0cmljTmFtZT0iQ1BVVXRpbGl6YXRpb24iPg0KICAgICAgICAgICAgPHBhcmFtZXRlciBuYW1lPSJzYW1wbGluZ1RpbWUiPjEwPC9wYXJhbWV0ZXI+DQogICAgICAgICAgICA8cGFyYW1ldGVyIG5hbWU9InNhbXBsaW5nUHJvYmFiaWxpdHkiPjE8L3BhcmFtZXRlcj4NCiAgICAgICAgPC9jb2xsZWN0ZWRNZXRyaWM+DQogICAgICAgIDxhY3Rpb25zPg0KICAgICAgICAgICAgPCEtLSBPdXRwdXRtZXRyaWMgcGF0dGVybiBhcHBpZF90b3NjYWxvY2F0aW9ubmFtZV9tZXRyaWNuYW1lX21ldHJpYyAtLT4NCiAgICAgICAgICAgIDxhY3Rpb24gbmFtZT0iT3V0cHV0TWV0cmljIj4NCiAgICAgICAgICAgICAgICA8cGFyYW1ldGVyIG5hbWU9Im1ldHJpYyI+YXBwaWRfbXlzcWxfc2VydmVyX2NwdV91dGlsaXphdGlvbl9tZXRyaWM8L3BhcmFtZXRlcj4NCiAgICAgICAgICAgICAgICA8cGFyYW1ldGVyIG5hbWU9InZhbHVlIj5NRVRSSUM8L3BhcmFtZXRlcj4NCiAgICAgICAgICAgICAgICA8cGFyYW1ldGVyIG5hbWU9InJlc291cmNlSWQiPklEPC9wYXJhbWV0ZXI+DQogICAgICAgICAgICA8L2FjdGlvbj4NCiAgICAgICAgPC9hY3Rpb25zPg0KICAgIDwvbW9uaXRvcmluZ1J1bGU+DQogICAgPG1vbml0b3JpbmdSdWxlIGlkPSJhcHBpZF9teXNxbF9zZXJ2ZXJfcmFtX3V0aWxpemF0aW9uIiB0aW1lU3RlcD0iMTAiIHRpbWVXaW5kb3c9IjEwIj4NCiAgICAgICAgPG1vbml0b3JlZFRhcmdldHM+DQogICAgICAgICAgICA8bW9uaXRvcmVkVGFyZ2V0IHR5cGU9ImFwcGlkX215c3FsX3NlcnZlciIgY2xhc3M9IlZNIi8+DQogICAgICAgIDwvbW9uaXRvcmVkVGFyZ2V0cz4NCiAgICAgICAgPGNvbGxlY3RlZE1ldHJpYyBtZXRyaWNOYW1lPSJNZW1Vc2VkIj4NCiAgICAgICAgICAgIDxwYXJhbWV0ZXIgbmFtZT0ic2FtcGxpbmdUaW1lIj4xMDwvcGFyYW1ldGVyPg0KICAgICAgICAgICAgPHBhcmFtZXRlciBuYW1lPSJzYW1wbGluZ1Byb2JhYmlsaXR5Ij4xPC9wYXJhbWV0ZXI+DQogICAgICAgIDwvY29sbGVjdGVkTWV0cmljPg0KICAgICAgICA8YWN0aW9ucz4NCiAgICAgICAgICAgIDxhY3Rpb24gbmFtZT0iT3V0cHV0TWV0cmljIj4NCiAgICAgICAgICAgICAgICA8cGFyYW1ldGVyIG5hbWU9Im1ldHJpYyI+YXBwaWRfbXlzcWxfc2VydmVyX3JhbV91dGlsaXphdGlvbl9tZXRyaWM8L3BhcmFtZXRlcj4NCiAgICAgICAgICAgICAgICA8cGFyYW1ldGVyIG5hbWU9InZhbHVlIj5NRVRSSUM8L3BhcmFtZXRlcj4NCiAgICAgICAgICAgICAgICA8cGFyYW1ldGVyIG5hbWU9InJlc291cmNlSWQiPklEPC9wYXJhbWV0ZXI+DQogICAgICAgICAgICA8L2FjdGlvbj4NCiAgICAgICAgPC9hY3Rpb25zPg0KICAgIDwvbW9uaXRvcmluZ1J1bGU+DQo8L21vbml0b3JpbmdSdWxlcz4NCg==";

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
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() throws Exception {
        log.info("Destroy all {}", new Object[]{this});
        if (app != null) {
            app.stop();
        }
    }


    @Test(expectedExceptions = Exception.class)
    public  void testAttachPolicyToEntity(){
        EmptySoftwareProcess entity = app.createAndManageChild(EntitySpec.create(EmptySoftwareProcess.class));
        entity.policies().add(PolicySpec.create(SeaCloudsInitializerPolicy.class));
        app.start(ImmutableList.of(loc));
    }

    @Test
    public void testAttachPolicyToApplication() {
        app.createAndManageChild(EntitySpec.create(EmptySoftwareProcess.class));

        // TODO: Ataching policies to the app level is unsupported on Brooklyn TOSCA Right now.
        app.policies().add(PolicySpec.create(SeaCloudsInitializerPolicy.class)
                .configure(SeaCloudsInitializerPolicy.SLA_ENDPOINT, "http://52.36.119.104:9003")
                .configure(SeaCloudsInitializerPolicy.SLA_AGREEMENT, BASE64_AGREEMENT)
                .configure(SeaCloudsInitializerPolicy.T4C_ENDPOINT, "http://52.48.187.2:8170")
                .configure(SeaCloudsInitializerPolicy.T4C_RULES, BASE64_RULES)
                .configure(SeaCloudsInitializerPolicy.INFLUXDB_ENDPOINT, "http://52.48.187.2:8086")
                .configure(SeaCloudsInitializerPolicy.INFLUXDB_DATABASE, "tower4clouds")
                .configure(SeaCloudsInitializerPolicy.INFLUXDB_USERNAME, "root")
                .configure(SeaCloudsInitializerPolicy.INFLUXDB_PASSWORD, "root")
                .configure(SeaCloudsInitializerPolicy.GRAFANA_ENDPOINT, "http://52.48.187.2:3000")
                .configure(SeaCloudsInitializerPolicy.GRAFANA_USERNAME, "admin")
                .configure(SeaCloudsInitializerPolicy.GRAFANA_PASSWORD, "admin")
        );

        app.start(ImmutableList.of(loc));

        assertTrue(Iterables.getOnlyElement(app.policies()) instanceof SeaCloudsInitializerPolicy);

        Asserts.succeedsEventually(new Runnable() {
            public void run() {
                assertTrue(app.getAttribute(Startable.SERVICE_UP));
                assertNotNull(app.getAttribute(SeaCloudsInitializerPolicy.SLA_ID));

                assertNotNull(app.getAttribute(SeaCloudsInitializerPolicy.T4C_IDS));
                assertFalse(app.getAttribute(SeaCloudsInitializerPolicy.T4C_IDS).isEmpty());
            }
        });
    }


}