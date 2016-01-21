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

package eu.seaclouds.platform.dashboard.proxy;

import com.squareup.okhttp.mockwebserver.MockResponse;
import eu.seaclouds.platform.dashboard.util.ObjectMapperHelpers;
import eu.seaclouds.platform.dashboard.utils.TestFixtures;
import eu.seaclouds.platform.dashboard.utils.TestUtils;
import org.apache.brooklyn.rest.domain.ApplicationSummary;
import org.apache.brooklyn.rest.domain.EntitySummary;
import org.apache.brooklyn.rest.domain.SensorSummary;
import org.apache.brooklyn.rest.domain.TaskSummary;
import org.testng.annotations.*;

import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class DeployerProxyTest extends AbstractProxyTest<DeployerProxy> {
    private final String RANDOM_STRING = UUID.randomUUID().toString();

    @Override
    public DeployerProxy getProxy() {
        return getSupport().getConfiguration().getDeployerProxy();
    }

    @Test
    public void testGetApplication() throws Exception {
        String json = TestUtils.getStringFromPath(TestFixtures.APPLICATION_PATH);

        getMockWebServer().enqueue(new MockResponse()
                        .setBody(json)
                        .setHeader("Accept", MediaType.APPLICATION_JSON)
                        .setHeader("Content-Type", MediaType.APPLICATION_JSON)
        );
        assertEquals(ObjectMapperHelpers.JsonToObject(json, ApplicationSummary.class), getProxy().getApplication(RANDOM_STRING));
    }

    @Test
    public void testRemoveApplication() throws Exception {
        String json = TestUtils.getStringFromPath(TestFixtures.TASK_SUMMARY_DELETE_PATH);

        getMockWebServer().enqueue(new MockResponse()
                        .setBody(json)
                        .setHeader("Accept", MediaType.APPLICATION_JSON)
                        .setHeader("Content-Type", MediaType.APPLICATION_JSON)
        );

        TaskSummary response = getProxy().removeApplication(RANDOM_STRING);

        // TaskSummary doesn't implement equals(), so we are going to check the IDs
        TaskSummary fixture = ObjectMapperHelpers.JsonToObject(json, TaskSummary.class);
        assertEquals(fixture.getId(), response.getId());
    }

    @Test
    public void testDeployApplication() throws Exception {
        String json = TestUtils.getStringFromPath(TestFixtures.TASK_SUMMARY_DEPLOY_PATH);
        String tosca = TestUtils.getStringFromPath(TestFixtures.TOSCA_DAM_PATH);

        getMockWebServer().enqueue(new MockResponse()
                        .setBody(json)
                        .setHeader("Accept", MediaType.APPLICATION_JSON)
                        .setHeader("Content-Type", MediaType.APPLICATION_JSON)
        );
        TaskSummary response = getProxy().deployApplication(tosca);

        // TaskSummary doesn't implement equals(), so we are going to check the IDs
        TaskSummary fixture = ObjectMapperHelpers.JsonToObject(json, TaskSummary.class);
        assertEquals(fixture.getId(), response.getId());
    }

    @Test
    public void testGetEntitiesFromApplication() throws Exception {
        String json = TestUtils.getStringFromPath(TestFixtures.ENTITIES_PATH);

        getMockWebServer().enqueue(new MockResponse()
                        .setBody(json)
                        .setHeader("Accept", MediaType.APPLICATION_JSON)
                        .setHeader("Content-Type", MediaType.APPLICATION_JSON)
        );
        List<EntitySummary> response = getProxy().getEntitiesFromApplication(RANDOM_STRING);

        List<EntitySummary> fixture = ObjectMapperHelpers.JsonToObjectCollection(json, EntitySummary.class);
        assertEquals(fixture, response);
    }

    @Test
    public void testGetEntitySensors() throws Exception {
        String json = TestUtils.getStringFromPath(TestFixtures.SENSORS_SUMMARIES_PATH);

        getMockWebServer().enqueue(new MockResponse()
                        .setBody(json)
                        .setHeader("Accept", MediaType.APPLICATION_JSON)
                        .setHeader("Content-Type", MediaType.APPLICATION_JSON)
        );
        List<SensorSummary> response = getProxy().getEntitySensors(RANDOM_STRING, RANDOM_STRING);

        List<SensorSummary> fixture = ObjectMapperHelpers.JsonToObjectCollection(json, SensorSummary.class);
        assertEquals(fixture, response);
    }

    @Test
    public void testGetEntitySensorsValue() throws Exception {
        getMockWebServer().enqueue(new MockResponse()
                        .setBody("0.7")
                        .setHeader("Accept", MediaType.APPLICATION_JSON)
                        .setHeader("Content-Type", MediaType.APPLICATION_JSON)
        );

        String response = getProxy().getEntitySensorsValue(RANDOM_STRING, RANDOM_STRING, RANDOM_STRING);
        assertEquals("0.7", response);
    }
}