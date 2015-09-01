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

import brooklyn.entity.cloudfoundry.services.CloudFoundryService;
import brooklyn.entity.cloudfoundry.services.sql.cleardb.ClearDbService;
import brooklyn.entity.cloudfoundry.webapp.java.JavaCloudFoundryPaasWebApp;
import brooklyn.entity.proxying.EntitySpec;
import brooklyn.entity.trait.Startable;
import brooklyn.test.Asserts;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = {"Live"})
public class CloudFoundryClearDbServiceLiveTest extends AbstractCloudFoundryPaasLocationLiveTest  {

    protected void instanceServiceTest() {
        final CloudFoundryService service = app.
                createAndManageChild(EntitySpec.create(ClearDbService.class)
                        .configure("serviceInstanceName", APPLICATION_SERVICE_NAME)
                        .configure("plan", "spark")
                        .location(cloudFoundryPaasLocation));

        app.start(ImmutableList.of(cloudFoundryPaasLocation));
        
        Asserts.succeedsEventually(new Runnable() {
            @Override
            public void run() {
                assertTrue(service.getAttribute(Startable.SERVICE_UP));
                assertTrue(service.getAttribute(JavaCloudFoundryPaasWebApp
                        .SERVICE_PROCESS_IS_RUNNING));
                assertEquals(service.getAttribute(CloudFoundryService.SERVICE_TYPE_ID), "cleardb");
                assertEquals(service.getConfig(CloudFoundryService.PLAN), "spark");
                assertEquals(service.getConfig(CloudFoundryService.SERVICE_INSTANCE_NAME),
                        APPLICATION_SERVICE_NAME);
            }
        });
    }
    
    
}
