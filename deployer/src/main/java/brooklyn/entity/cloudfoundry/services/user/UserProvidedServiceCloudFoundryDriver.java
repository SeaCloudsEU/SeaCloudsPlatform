/*
 * Copyright 2015 SeaClouds
 * Contact: dev@seaclouds-project.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package brooklyn.entity.cloudfoundry.services.user;


import brooklyn.entity.cloudfoundry.services.CloudFoundryServiceImpl;
import brooklyn.entity.cloudfoundry.services.PaasServiceCloudFoundryDriver;
import brooklyn.entity.cloudfoundry.webapp.CloudFoundryWebAppImpl;
import brooklyn.location.cloudfoundry.CloudFoundryPaasLocation;
import java.util.Iterator;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserProvidedServiceCloudFoundryDriver extends PaasServiceCloudFoundryDriver implements UserProvidedServiceDriver {

    public static final Logger log = LoggerFactory.getLogger(UserProvidedServiceCloudFoundryDriver.class);

    public UserProvidedServiceCloudFoundryDriver(CloudFoundryServiceImpl entity, CloudFoundryPaasLocation location) {
        super(entity, location);
    }

    @Override
    public void launch() {
        if (!isServiceCreated(getEntity().getServiceName())) {
            serviceInstance.setName(getEntity().getServiceName());
            getClient().createUserProvidedService(serviceInstance, getEntity().getConfig(UserProvidedService.CREDENTIALS));
        } else {
            // Service exists already, maybe we should UPDATE it?
        }
    }

    @Override
    public void operation(CloudFoundryWebAppImpl app) {
    }
    
    private boolean isServiceCreated(String serviceName){
        boolean found = false;
        Iterator<CloudService> services = getClient().getServices().iterator();
        while (!found && services.hasNext()){
            found = services.next().getName().equals(serviceName);
        }
        return found;
    }

    @Override
    public UserProvidedServiceImpl getEntity() {
        return (UserProvidedServiceImpl) super.getEntity();
    }


}
