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
package eu.seaclouds.location.openshift;


import org.apache.brooklyn.api.entity.EntitySpec;
import org.apache.brooklyn.core.entity.Attributes;
import org.apache.brooklyn.core.entity.trait.Startable;
import org.apache.brooklyn.entity.openshift.webapp.OpenShiftWebApp;
import com.google.common.collect.ImmutableList;
import org.apache.brooklyn.test.Asserts;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = {"Live"})
public class JavaWebOpenShiftLiveTest extends AbstractOpenShiftPaasLocationLiveTest {

    private final String GIT_REPOSITORY_URL = "https://github.com/kiuby88/Chat-Application-Pivotal-Example.git";

    protected void deployApplicationTest() throws Exception {
        final OpenShiftWebApp server = app.
                createAndManageChild(EntitySpec.create(OpenShiftWebApp.class)
                        .configure("application-name", APPLICATION_NAME)
                        .configure("git-url-repo", GIT_REPOSITORY_URL)
                        .location(openShiftPaasLocation));

        app.start(ImmutableList.of(openShiftPaasLocation));

        Asserts.succeedsEventually(new Runnable() {
            public void run() {
                assertTrue(server.getAttribute(Startable.SERVICE_UP));
                assertTrue(server.getAttribute(OpenShiftWebApp
                        .SERVICE_PROCESS_IS_RUNNING));

                assertNotNull(server.getAttribute(Attributes.MAIN_URI));
                assertNotNull(server.getAttribute(OpenShiftWebApp.ROOT_URL));

            }
        });
    }


}
