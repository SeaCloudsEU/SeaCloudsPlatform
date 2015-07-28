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
import brooklyn.entity.cloudfoundry.services.generic.GenericService;
import brooklyn.entity.proxying.EntitySpec;
import brooklyn.test.Asserts;
import com.google.common.collect.ImmutableList;
import org.cloudfoundry.client.lib.domain.CloudServiceInstance;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;
import org.cloudfoundry.client.lib.domain.CloudServicePlan;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ServiceCredentialLiveTest extends AbstractCloudFoundryPaasLocationLiveTest  {


    @DataProvider(name = "serviceDescriptionProvider")
    private Object[][] ServiceOfferingSupplier() throws Exception {

        Object[][] freeServicesResult;

        super.setUp();
        cloudFoundryPaasLocation.setUpClient();
        List<Object[]> freeServiceOffering = getFreeServiceOfferings(cloudFoundryPaasLocation
                .getCloudFoundryClient()
                .getServiceOfferings());

        freeServicesResult = new Object[freeServiceOffering.size()][];
        freeServiceOffering.toArray(freeServicesResult);
        return freeServicesResult;
    }

    private List<Object[]> getFreeServiceOfferings(List<CloudServiceOffering> serviceOfferings){
        ArrayList<Object[]> list = new ArrayList<Object[]>();
        for(CloudServiceOffering ser: serviceOfferings){
            if(hasFreePlan(ser)){
                    list.add(new Object[]{ser});
            }
        }
        return list;
    }

    private boolean hasFreePlan(CloudServiceOffering serviceOffering){
        return findFreePlan(serviceOffering)!=null;
    }

    private String findFreePlan(CloudServiceOffering serviceOffering){
        String freePlan=null;

        for(CloudServicePlan plan: serviceOffering.getCloudServicePlans()) {
            if(plan.isFree()){
                return plan.getName();
            }
        }
        return freePlan;
    }

    @Test(groups = {"Live"}, dataProvider = "serviceDescriptionProvider")
    protected void instanceServiceTest2(CloudServiceOffering serviceOffering) {

        String serviceInstanceName= "test-brooklyn-"+serviceOffering.getName();
        String freePlan = findFreePlan(serviceOffering);
        String serviceTypeId = serviceOffering.getName();

        final CloudFoundryService service = app
                .createAndManageChild(EntitySpec.create(GenericService.class)
                        .configure("serviceInstanceName", serviceInstanceName)
                        .configure("plan", freePlan)
                        .configure("serviceType", serviceTypeId)
                        .location(cloudFoundryPaasLocation));

        app.start(ImmutableList.of(cloudFoundryPaasLocation));

        final CloudServiceInstance serviceInstance = cloudFoundryPaasLocation
                .getCloudFoundryClient()
                .getServiceInstance(serviceInstanceName);

        Asserts.succeedsEventually(new Runnable() {
            @Override
            public void run() {
                assertNotNull(serviceInstance);
                assertTrue(serviceInstance.getCredentials().isEmpty());
            }
        });
    }

    
}
