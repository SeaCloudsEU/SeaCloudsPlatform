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

package eu.seaclouds.platform.dashboard.rest;

import eu.seaclouds.platform.dashboard.model.SeaCloudsApplicationData;
import eu.seaclouds.platform.dashboard.model.SeaCloudsApplicationDataStorage;
import org.apache.brooklyn.rest.domain.EntitySummary;
import org.apache.brooklyn.rest.domain.SensorSummary;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class MonitorResourceTest extends AbstractResourceTest<MonitorResource> {
    private final DeployerResource deployerResource = new DeployerResource(getDeployerProxy(), getMonitorProxy(), getSlaProxy(), getPlannerProxy());
    private final MonitorResource monitorResource = new MonitorResource(getMonitorProxy(), getDeployerProxy());
    private SeaCloudsApplicationData applicationData;

    @Override
    @BeforeMethod
    public void setUpMethod() throws Exception {
        super.setUpMethod();

        applicationData = (SeaCloudsApplicationData) deployerResource.addApplication(getDam()).getEntity();
        SeaCloudsApplicationDataStorage.getInstance().addSeaCloudsApplicationData(applicationData);
    }

    @Test
    public void testGetSensors() throws Exception {
        List<Map<String, Object>> response =
                (List<Map<String, Object>>) monitorResource.getEntitySensorMapList(applicationData.getSeaCloudsApplicationId()).getEntity();

        for (Map<String, Object> entityMetricsPair : response) {
            List sensors = (List) entityMetricsPair.get("sensors");
            assertEquals(sensors.size(), 5);
        }
    }

    @Test
    public void testGetMetrics() throws Exception {
        List<Map<String, Object>> response =
                (List<Map<String, Object>>) monitorResource.getEntityMetricMapList(applicationData.getSeaCloudsApplicationId()).getEntity();

        for (Map<String, Object> entityMetricsPair : response) {
            List sensors = (List) entityMetricsPair.get("metrics");
            assertEquals(sensors.size(), 2);
        }
    }

    @Test
    public void testGetMetricValue() throws Exception {
        String response = (String) monitorResource.getMetricValue(applicationData.getSeaCloudsApplicationId(), AbstractResourceTest.RANDOM_STRING, AbstractResourceTest.RANDOM_STRING).getEntity();
        assertEquals(Double.valueOf(response), 0.7d);
    }
}