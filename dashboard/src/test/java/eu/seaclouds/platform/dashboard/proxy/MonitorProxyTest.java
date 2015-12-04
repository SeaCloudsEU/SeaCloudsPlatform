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

import com.google.common.io.Resources;
import com.squareup.okhttp.mockwebserver.MockResponse;
import eu.seaclouds.platform.dashboard.util.ObjectMapperHelpers;
import eu.seaclouds.platform.dashboard.utils.TestFixtures;
import eu.seaclouds.platform.dashboard.utils.TestUtils;
import it.polimi.tower4clouds.rules.MonitoringRules;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.net.URL;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class MonitorProxyTest extends AbstractProxyTest<MonitorProxy> {
    private final String RANDOM_STRING = UUID.randomUUID().toString();

    @Override
    public MonitorProxy getProxy() {
        return getSupport().getConfiguration().getMonitorProxy();
    }

    @Test
    public void testListMonitoringRules() throws Exception {
        URL resource = Resources.getResource(TestFixtures.MONITORING_RULES_PATH);
        String xml = FileUtils.readFileToString(new File(resource.getFile()));

        getMockWebServer().enqueue(new MockResponse()
                        .setBody(xml)
                        .setHeader("Accept", MediaType.APPLICATION_XML)
                        .setHeader("Content-Type", MediaType.APPLICATION_XML)
        );
        assertEquals(ObjectMapperHelpers.XmlToObject(xml, MonitoringRules.class), getProxy().listMonitoringRules());
    }

    @Test
    public void testAddMonitoringRules() throws Exception {
        String xml = TestUtils.getStringFromPath(TestFixtures.MONITORING_RULES_PATH);

        getMockWebServer().enqueue(new MockResponse()
                        .setHeader("Accept", MediaType.APPLICATION_XML)
                        .setHeader("Content-Type", MediaType.APPLICATION_XML)
        );

        assertNotNull(getProxy().addMonitoringRules(ObjectMapperHelpers.XmlToObject(xml, MonitoringRules.class)));

    }

    @Test
    public void testRemoveMonitoringRule() throws Exception {
        String xml = TestUtils.getStringFromPath(TestFixtures.MONITORING_RULES_PATH);
        getMockWebServer().enqueue(new MockResponse()
                        .setHeader("Accept", MediaType.APPLICATION_XML)
                        .setHeader("Content-Type", MediaType.APPLICATION_XML)
        );

        ObjectMapperHelpers.XmlToObject(xml, MonitoringRules.class);
        assertNotNull(getProxy().removeMonitoringRule(RANDOM_STRING));
    }

}