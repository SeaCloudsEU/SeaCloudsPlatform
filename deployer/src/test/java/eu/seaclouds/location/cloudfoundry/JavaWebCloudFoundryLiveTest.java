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

import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.Lifecycle;
import brooklyn.entity.cloudfoundry.webapp.java.JavaCloudFoundryPaasWebApp;
import brooklyn.entity.proxying.EntitySpec;
import brooklyn.entity.trait.Startable;
import brooklyn.location.cloudfoundry.PaasLocationConfig;
import brooklyn.test.Asserts;
import brooklyn.util.exceptions.PropagatedRuntimeException;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;


public class JavaWebCloudFoundryLiveTest extends AbstractCloudFoundryPaasLocationLiveTest {

    private final String APPLICATION_ARTIFACT_NAME = "brooklyn-example-hello-world-webapp.war";

    private final String APPLICATION_ARTIFACT_URL =
            getClasspathUrlForResource(APPLICATION_ARTIFACT_NAME);


    @Test(groups = {"Live"})
    protected void deployApplicationTest() throws Exception {
        final JavaCloudFoundryPaasWebApp server = app.
                createAndManageChild(EntitySpec.create(JavaCloudFoundryPaasWebApp.class)
                        .configure("application-name", APPLICATION_NAME)
                        .configure("application-url", APPLICATION_ARTIFACT_URL)
                        .location(cloudFoundryPaasLocation));

        app.start(ImmutableList.of(cloudFoundryPaasLocation));

        Asserts.succeedsEventually(new Runnable() {
            public void run() {
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
            }
        });
    }

    @Test(groups = {"Live"})
    protected void stopApplicationTest() throws Exception {
        final JavaCloudFoundryPaasWebApp server = app.
                createAndManageChild(EntitySpec.create(JavaCloudFoundryPaasWebApp.class)
                        .configure("application-name", "stopped" + APPLICATION_NAME)
                        .configure("application-url", APPLICATION_ARTIFACT_URL)
                        .location(cloudFoundryPaasLocation));

        app.start(ImmutableList.of(cloudFoundryPaasLocation));
        Asserts.succeedsEventually(new Runnable() {
            public void run() {
                assertTrue(server.getAttribute(Startable.SERVICE_UP));
                app.stop();
                assertEquals(server.getAttribute(JavaCloudFoundryPaasWebApp
                        .SERVICE_STATE_ACTUAL), Lifecycle.STOPPED);
                assertFalse(server.getAttribute(Startable.SERVICE_UP));
                assertNull(server.getAttribute(JavaCloudFoundryPaasWebApp
                        .SERVICE_PROCESS_IS_RUNNING));
            }
        });
    }

    @Test(groups = {"Live"})
    protected void wrongApplicationOnFireStatusTest() throws Exception {
        final JavaCloudFoundryPaasWebApp server = app.
                createAndManageChild(EntitySpec.create(JavaCloudFoundryPaasWebApp.class)
                        .configure("application-name", "wrong-" + APPLICATION_NAME)
                        .configure("application-path", APPLICATION_ARTIFACT_URL + "wrong")
                        .location(cloudFoundryPaasLocation));

        Asserts.succeedsEventually(new Runnable() {
            public void run() {
                try {
                    app.start(ImmutableList.of(cloudFoundryPaasLocation));
                } catch (PropagatedRuntimeException e) {
                    assertEquals(server.getAttribute(JavaCloudFoundryPaasWebApp
                            .SERVICE_STATE_ACTUAL), Lifecycle.ON_FIRE);
                }
            }
        });
    }


}
