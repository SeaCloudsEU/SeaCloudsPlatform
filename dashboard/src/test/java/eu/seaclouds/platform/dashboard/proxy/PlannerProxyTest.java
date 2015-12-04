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
import it.polimi.tower4clouds.rules.MonitoringRules;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.util.UUID;

import static org.testng.Assert.*;

public class PlannerProxyTest extends AbstractProxyTest<PlannerProxy> {
    private final String RANDOM_STRING = UUID.randomUUID().toString();

    @Test
    public void testGetMonitoringRulesByTemplateId() throws Exception {
        String xml = TestUtils.getStringFromPath(TestFixtures.MONITORING_RULES_PATH);

        getMockWebServer().enqueue(new MockResponse()
                        .setBody(xml)
                        .setHeader("Accept", MediaType.TEXT_PLAIN)
                        .setHeader("Content-Type", MediaType.APPLICATION_XML)
        );

        MonitoringRules monitoringRules = getProxy().getMonitoringRulesByTemplateId(RANDOM_STRING);
        assertEquals(ObjectMapperHelpers.XmlToObject(xml, MonitoringRules.class), monitoringRules);
    }

    @Test
    public void testGetAdps() throws Exception {
        String aam = TestUtils.getStringFromPath(TestFixtures.AAM_PATH);
        String adps = TestUtils.getStringFromPath(TestFixtures.ADPS_PATH);

        getMockWebServer().enqueue(new MockResponse()
                        .setBody(adps)
                        .setHeader("Accept", MediaType.TEXT_PLAIN)
                        .setHeader("Content-Type", MediaType.APPLICATION_XML)
        );

        assertEquals(adps, getProxy().getAdps(aam));
    }

    @Test
    public void testGetDam() throws Exception {
        String adp = TestUtils.getStringFromPath(TestFixtures.ADP_PATH);
        String dam = TestUtils.getStringFromPath(TestFixtures.TOSCA_DAM_PATH);

        getMockWebServer().enqueue(new MockResponse()
                        .setBody(dam)
                        .setHeader("Accept", MediaType.TEXT_PLAIN)
                        .setHeader("Content-Type", MediaType.APPLICATION_XML)
        );

        assertEquals(dam, getProxy().getDam(adp));
    }

    @Override
    public PlannerProxy getProxy() {
        return getSupport().getConfiguration().getPlannerProxy();
    }
}