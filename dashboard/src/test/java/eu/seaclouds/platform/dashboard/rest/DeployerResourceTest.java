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
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class DeployerResourceTest extends AbstractResourceTest<DeployerResource> {

    private final DeployerResource deployerResource = new DeployerResource(getDeployerProxy(), getMonitorProxy(), getSlaProxy(), getPlannerProxy());

    @Test
    public void testAddApplication() throws Exception {
        SeaCloudsApplicationData application = (SeaCloudsApplicationData) deployerResource.addApplication(getDam()).getEntity();
        assertNotNull(application.getName());
        assertNotNull(application.getToscaDam());
        assertNotNull(application.getAgreementId());
        assertNotNull(application.getAgreementTemplateId());
        assertNotNull(application.getDeployerApplicationId());
        assertNotNull(application.getMonitoringRulesIds());
        assertNotNull(application.getMonitoringRulesTemplateId());
    }

    @Test
    public void testListApplications() throws Exception {
        List<SeaCloudsApplicationData> list = (List<SeaCloudsApplicationData>) deployerResource.listApplications().getEntity();
        assertTrue(list.isEmpty());
        deployerResource.addApplication(getDam());
        deployerResource.addApplication(getDam());
        deployerResource.addApplication(getDam());
        list = (List<SeaCloudsApplicationData>) deployerResource.listApplications().getEntity();
        assertEquals(list.size(), 3);
    }

    @Test
    public void testGetApplication() throws Exception {
        assertNull(deployerResource.getApplication("this-app-doesn't-exist").getEntity());
        SeaCloudsApplicationData application = (SeaCloudsApplicationData) deployerResource.addApplication(getDam()).getEntity();
        assertNotNull(deployerResource.getApplication(application.getSeaCloudsApplicationId()));
    }

    @Test
    public void testRemoveApplication() throws Exception {
        SeaCloudsApplicationData application = (SeaCloudsApplicationData) deployerResource.addApplication(getDam()).getEntity();
        deployerResource.addApplication(getDam());
        deployerResource.addApplication(getDam());

        assertNotNull(deployerResource.getApplication(application.getSeaCloudsApplicationId()).getEntity());

        List<SeaCloudsApplicationData> list = (List<SeaCloudsApplicationData>) deployerResource.listApplications().getEntity();
        assertEquals(list.size(), 3);

        deployerResource.removeApplication(application.getSeaCloudsApplicationId());
        list = (List<SeaCloudsApplicationData>) deployerResource.listApplications().getEntity();
        assertEquals(list.size(), 2);

        assertNull(deployerResource.getApplication(application.getSeaCloudsApplicationId()).getEntity());
    }
}