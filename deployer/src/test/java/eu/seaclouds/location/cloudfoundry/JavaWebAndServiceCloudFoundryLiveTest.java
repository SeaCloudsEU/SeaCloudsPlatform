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

import org.apache.brooklyn.entity.cloudfoundry.services.CloudFoundryService;
import org.apache.brooklyn.entity.cloudfoundry.services.sql.cleardb.ClearDbService;
import org.apache.brooklyn.entity.cloudfoundry.webapp.CloudFoundryWebApp;
import org.apache.brooklyn.entity.cloudfoundry.webapp.java.JavaCloudFoundryPaasWebApp;
import org.apache.brooklyn.location.cloudfoundry.PaasLocationConfig;
import com.google.common.collect.ImmutableList;
import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.api.entity.EntitySpec;
import org.apache.brooklyn.core.entity.Attributes;
import org.apache.brooklyn.core.entity.trait.Startable;
import org.apache.brooklyn.test.Asserts;
import org.apache.brooklyn.util.exceptions.PropagatedRuntimeException;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;


@Test(groups = {"Live"})
public class JavaWebAndServiceCloudFoundryLiveTest extends AbstractCloudFoundryPaasLocationLiveTest {


    private final String SQL_ARTIFACT_NAME = "chat-database.sql";
    private final String APPLICATION_ARTIFACT_NAME = "brooklyn-example-hello-world-webapp.war";

    private final String SQL_ARTIFACT_URL = getClasspathUrlForResource(SQL_ARTIFACT_NAME);
    private final String APPLICATION_ARTIFACT_URL =
            getClasspathUrlForResource(APPLICATION_ARTIFACT_NAME);

    private final String SERVICE_NAME = APPLICATION_SERVICE_NAME+"-mysql";
    private final String SERVICE_TYPE_ID = "cleardb";
    private final String SERVICE_PLAN = "spark";

    @Test(groups = {"Live"})
    protected void deployAppWithServicesTest() throws Exception {

        //List<String> servicesToBind=new LinkedList<String>();
        List<Entity> servicesToBind=new LinkedList<Entity>();
        final CloudFoundryService service = app
                .createAndManageChild(EntitySpec.create(ClearDbService.class)
                        .configure("serviceInstanceName", SERVICE_NAME)
                        .configure("plan", SERVICE_PLAN)
                        .configure("creationScriptUrl", SQL_ARTIFACT_URL)
                        .location(cloudFoundryPaasLocation));

        servicesToBind.add(service);
        final JavaCloudFoundryPaasWebApp server = app
                .createAndManageChild(EntitySpec.create(JavaCloudFoundryPaasWebApp.class)
                        .configure("application-name", APPLICATION_NAME + "-withServices")
                        .configure("application-url", APPLICATION_ARTIFACT_URL)
                        .configure("bind", servicesToBind)
                        .location(cloudFoundryPaasLocation));

        app.start(ImmutableList.of(cloudFoundryPaasLocation));

        Asserts.succeedsEventually(new Runnable() {
            public void run() {

                assertEquals(server.getAttribute(CloudFoundryWebApp.BOUND_SERVICES).size(), 1);

                assertEquals(service.getAttribute(CloudFoundryService.SERVICE_TYPE_ID),
                        SERVICE_TYPE_ID);
                assertEquals(service.getConfig(CloudFoundryService.PLAN), SERVICE_PLAN);
                assertEquals(service.getConfig(CloudFoundryService.SERVICE_INSTANCE_NAME),
                        SERVICE_NAME);

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

    @Test(expectedExceptions = PropagatedRuntimeException.class)
    protected void deployAppWithNotAvailableServicesEntityTest() throws Exception {
        List<String> servicesToBind=new LinkedList<String>();

            servicesToBind.add("NotExistingService");
            final JavaCloudFoundryPaasWebApp server = app
                    .createAndManageChild(EntitySpec.create(JavaCloudFoundryPaasWebApp.class)
                            .configure("application-name",
                                    APPLICATION_NAME + "-withNotAvailableService")
                            .configure("application-url", APPLICATION_ARTIFACT_URL)
                            .configure("bind", servicesToBind)
                            .location(cloudFoundryPaasLocation));
            app.start(ImmutableList.of(cloudFoundryPaasLocation));
    }


}
