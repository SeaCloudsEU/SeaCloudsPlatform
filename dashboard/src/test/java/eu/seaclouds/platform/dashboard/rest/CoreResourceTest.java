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

import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class CoreResourceTest extends AbstractResourceTest<CoreResource> {

    private final CoreResource coreResource = new CoreResource(getDeployerProxy(), getMonitorProxy(), getPlannerProxy(), getSlaProxy());

    @Test
    public void testGetSeaCloudsInformation() throws Exception {
        assertNotNull(coreResource.getSeaCloudsInformation().getEntity());
    }
}