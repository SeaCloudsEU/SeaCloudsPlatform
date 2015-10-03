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
package eu.seaclouds.location.cloudfoundry;

import static org.testng.Assert.assertTrue;

import org.apache.brooklyn.entity.cloudfoundry.services.user.UserProvidedService;
import org.apache.brooklyn.entity.cloudfoundry.webapp.java.JavaCloudFoundryPaasWebApp;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.brooklyn.api.entity.EntitySpec;
import org.apache.brooklyn.api.sensor.AttributeSensor;
import org.apache.brooklyn.core.entity.trait.Startable;
import org.apache.brooklyn.test.Asserts;
import org.testng.annotations.Test;

public class UserProvidedServiceTest extends AbstractCloudFoundryPaasLocationLiveTest {
    private final String APPLICATION_ARTIFACT_NAME = "brooklyn-example-hello-world-webapp.war";
    
    private final String APPLICATION_ARTIFACT_URL =
            getClasspathUrlForResource(APPLICATION_ARTIFACT_NAME);
    
    @Test(groups = {"Live"})
    protected void testEmptyCredentials() {
        final UserProvidedService service = app.
                createAndManageChild(EntitySpec.create(UserProvidedService.class)
                        .configure("serviceInstanceName", APPLICATION_SERVICE_NAME)
                        .configure("credentials", ImmutableMap.<String, Object>of())
                        .location(cloudFoundryPaasLocation));

        app.start(ImmutableList.of(cloudFoundryPaasLocation));

        Asserts.succeedsEventually(new Runnable() {
            @Override
            public void run() {
                assertTrue(service.getAttribute(Startable.SERVICE_UP));
                assertTrue(service.getCredentials().equals(ImmutableMap.<String, Object>of()));
            }
        });
    }

    @Test(groups = {"Live"})
    protected void testWithCredentials() {
        final UserProvidedService service = app.
                createAndManageChild(EntitySpec.create(UserProvidedService.class)
                        .configure("serviceInstanceName", APPLICATION_SERVICE_NAME)
                        .configure("credentials", ImmutableMap.<String, Object>builder()
                                .put("key1", "val1")
                                .put("key2", "val2")
                                .build())
                        .location(cloudFoundryPaasLocation));

        app.start(ImmutableList.of(cloudFoundryPaasLocation));

        Asserts.succeedsEventually(new Runnable() {
            @Override
            public void run() {
                assertTrue(service.getAttribute(Startable.SERVICE_UP));
                assertTrue(service.getCredentials().size() == 2);
            }
        });
    }
    
    @Test(groups = {"Live"})
    protected void testBindingToWebapp() {
        final UserProvidedService service = app.
                createAndManageChild(EntitySpec.create(UserProvidedService.class)
                        .configure("serviceInstanceName", APPLICATION_SERVICE_NAME)
                        .configure("credentials", ImmutableMap.<String, Object>builder()
                                .put("key1", "val1")
                                .put("key2", "val2")
                                .build())
                        .location(cloudFoundryPaasLocation));
        
        final JavaCloudFoundryPaasWebApp server = app.
                createAndManageChild(EntitySpec.create(JavaCloudFoundryPaasWebApp.class)
                        .configure("application-name", APPLICATION_NAME)
                        .configure("application-url", APPLICATION_ARTIFACT_URL)
                        .configure("bind", ImmutableList.of(service))
                        .location(cloudFoundryPaasLocation));
        app.start(ImmutableList.of(cloudFoundryPaasLocation));

        Asserts.succeedsEventually(new Runnable() {
            @Override
            public void run() {
                assertTrue(service.getAttribute(Startable.SERVICE_UP));
                assertTrue(service.getCredentials().size() == 2);
                assertTrue(service.getAttribute((AttributeSensor) service.getEntityType().getSensor(APPLICATION_NAME + ".credentials.key1")).equals("val1"));
                assertTrue(service.getAttribute((AttributeSensor) service.getEntityType().getSensor(APPLICATION_NAME + ".credentials.key2")).equals("val2"));
            }
        });
    }
}
