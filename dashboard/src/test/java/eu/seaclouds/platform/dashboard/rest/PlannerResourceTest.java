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
import it.polimi.tower4clouds.rules.MonitoringRules;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PlannerResourceTest extends AbstractResourceTest<PlannerResource>{
    PlannerResource resource = new PlannerResource(getPlannerProxy());
    SeaCloudsApplicationData applicationData;

    @Override
    @BeforeMethod
    public void setUpMethod() throws Exception {
        super.setUpMethod();
        applicationData = new SeaCloudsApplicationData(getDam());
        SeaCloudsApplicationDataStorage.getInstance().addSeaCloudsApplicationData(applicationData);
    }

    @Test
    public void testGetMonitoringRulesById() throws Exception {
        MonitoringRules rules = (MonitoringRules) resource.getMonitoringRulesById(applicationData.getAgreementTemplateId()).getEntity();
        assertNotNull(rules);
    }

    @Test
    public void testGetAdps() throws Exception {
        assertNotNull(resource.getAdps(getTopology()).getEntity());
    }

    @Test
    public void testGetDam() throws Exception {
        String entity = (String) resource.getDam(getAdp()).getEntity();
        SeaCloudsApplicationData newApp = new SeaCloudsApplicationData(entity);
        assertEquals(newApp.getToscaDam(), applicationData.getToscaDam());
    }
}