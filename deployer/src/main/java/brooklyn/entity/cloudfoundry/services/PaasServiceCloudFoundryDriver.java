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
package brooklyn.entity.cloudfoundry.services;

import static com.google.common.base.Preconditions.checkNotNull;

import brooklyn.entity.cloudfoundry.PaasEntityCloudFoundryDriver;
import brooklyn.location.cloudfoundry.CloudFoundryPaasLocation;
import org.cloudfoundry.client.lib.domain.CloudEntity;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PaasServiceCloudFoundryDriver extends PaasEntityCloudFoundryDriver
        implements PaasServiceDriver {
    
    public static final Logger log = LoggerFactory.getLogger(PaasServiceCloudFoundryDriver.class);

    protected CloudService serviceInstance;
    protected String serviceInstanceName;
    protected String servicePlan;
    protected String serviceTypeId;

    public PaasServiceCloudFoundryDriver(CloudFoundryServiceImpl entity,
                                         CloudFoundryPaasLocation location) {
        super(entity, location);
    }

    @Override
    protected void init() {
        super.init();
        serviceTypeId = getEntity().getAttribute(CloudFoundryService.SERVICE_TYPE_ID);
        serviceInstanceName = getEntity().getConfig(CloudFoundryService.SERVICE_INSTANCE_NAME);
        servicePlan = getEntity().getConfig(CloudFoundryService.PLAN);
    }
    
    @Override
    public CloudFoundryServiceImpl getEntity() {
        return (CloudFoundryServiceImpl) super.getEntity();
    }

    public String getServiceInstanceName(){
        return serviceInstanceName;
    }

    @Override
    public boolean isRunning() {
        return (getClient().getService(serviceInstanceName) != null);
    }

    @Override
    public void start() {
        super.start();

        preLaunch();
        launch();
        postLaunch();
    }

    public void preLaunch() {
        serviceInstance = new CloudService(CloudEntity.Meta.defaultMeta(), serviceInstanceName);
    }

    public void launch() {
        serviceInstance.setLabel(serviceTypeId);
        //TODO check if the plan is an existing plan
        serviceInstance.setPlan(servicePlan);

        checkNotNull(getClient());
        getClient().createService(serviceInstance);
    }
    
    public void postLaunch() {
        // TODO: Setting up sensors here
        getEntity().setAttribute(CloudFoundryService.PLAN, serviceInstance.getPlan());
    }

    @Override
    public void restart() {
        stop();
        deleteService();
        start();
    }

    @Override
    public void stop() {
        deleteService();
    }

    @Override
    public void deleteService() {
        getClient().deleteService(serviceInstanceName);
    }


}
