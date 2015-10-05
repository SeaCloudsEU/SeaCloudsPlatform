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

import org.apache.brooklyn.entity.cloudfoundry.webapp.php.PhpCloudFoundryPaasWebApp;
import org.apache.brooklyn.location.cloudfoundry.PaasLocationConfig;
import com.google.common.collect.ImmutableList;
import org.apache.brooklyn.api.entity.EntitySpec;
import org.apache.brooklyn.core.entity.Attributes;
import org.apache.brooklyn.core.entity.lifecycle.Lifecycle;
import org.apache.brooklyn.core.entity.trait.Startable;
import org.apache.brooklyn.test.Asserts;
import org.apache.brooklyn.util.exceptions.PropagatedRuntimeException;
import org.testng.annotations.Test;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;


public class PhpWebCloudFoundryLiveTest extends AbstractCloudFoundryPaasLocationLiveTest {

    private final String APPLICATION_URL = checkNotNull(getClass().getClassLoader()
            .getResource("phpHelloWorld")).getFile();


    @Test(groups = {"Live"})
    protected void deployApplicationTest() throws Exception {
        final PhpCloudFoundryPaasWebApp server = app.
                createAndManageChild(EntitySpec.create(PhpCloudFoundryPaasWebApp.class)
                        .configure("application-name", APPLICATION_NAME)
                        .configure("application-url", APPLICATION_URL)
                        .location(cloudFoundryPaasLocation));

        app.start(ImmutableList.of(cloudFoundryPaasLocation));

        Asserts.succeedsEventually(new Runnable() {
            public void run() {
                assertTrue(server.getAttribute(Startable.SERVICE_UP));
                assertTrue(server.getAttribute(PhpCloudFoundryPaasWebApp
                        .SERVICE_PROCESS_IS_RUNNING));

                assertNotNull(server.getAttribute(Attributes.MAIN_URI));
                assertNotNull(server.getAttribute(PhpCloudFoundryPaasWebApp.ROOT_URL));

                assertEquals(server.getAttribute(PhpCloudFoundryPaasWebApp.DISK),
                        PaasLocationConfig.REQUIRED_DISK.getDefaultValue());
                assertEquals(server.getAttribute(PhpCloudFoundryPaasWebApp.INSTANCES_NUM),
                        PaasLocationConfig.REQUIRED_INSTANCES.getDefaultValue());
                assertEquals(server.getAttribute(PhpCloudFoundryPaasWebApp.MEMORY),
                        PaasLocationConfig.REQUIRED_MEMORY.getDefaultValue());
            }
        });
    }

    @Test(groups = {"Live"})
    protected void stopApplicationTest() throws Exception {
        final PhpCloudFoundryPaasWebApp server = app.
                createAndManageChild(EntitySpec.create(PhpCloudFoundryPaasWebApp.class)
                        .configure("application-name", "stopped"+APPLICATION_NAME)
                        .configure("application-url", APPLICATION_URL)
                        .location(cloudFoundryPaasLocation));

        app.start(ImmutableList.of(cloudFoundryPaasLocation));
        Asserts.succeedsEventually(new Runnable() {
            public void run() {
                assertTrue(server.getAttribute(Startable.SERVICE_UP));
                app.stop();
                assertEquals(server.getAttribute(PhpCloudFoundryPaasWebApp
                        .SERVICE_STATE_ACTUAL), Lifecycle.STOPPED);
                assertFalse(server.getAttribute(Startable.SERVICE_UP));
                assertNull(server.getAttribute(PhpCloudFoundryPaasWebApp
                        .SERVICE_PROCESS_IS_RUNNING));
            }
        });
    }

    @Test(groups = {"Live"})
    protected void wrongApplicationOnFireStatusTest() throws Exception {
        final PhpCloudFoundryPaasWebApp server = app.
                createAndManageChild(EntitySpec.create(PhpCloudFoundryPaasWebApp.class)
                        .configure("application-name", "wrong-"+APPLICATION_NAME)
                        .configure("application-path", APPLICATION_URL + "wrong")
                        .location(cloudFoundryPaasLocation));

        Asserts.succeedsEventually(new Runnable() {
            public void run() {
                try {
                    app.start(ImmutableList.of(cloudFoundryPaasLocation));
                } catch (PropagatedRuntimeException e) {
                    assertEquals(server.getAttribute(PhpCloudFoundryPaasWebApp
                            .SERVICE_STATE_ACTUAL), Lifecycle.ON_FIRE);
                }
            }
        });
    }


}
