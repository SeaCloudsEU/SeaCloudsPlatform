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
package org.apache.brooklyn.entity.cloudfoundry.services.user;


import org.apache.brooklyn.entity.cloudfoundry.services.CloudFoundryServiceImpl;
import org.apache.brooklyn.entity.cloudfoundry.webapp.CloudFoundryWebApp;
import org.apache.brooklyn.entity.cloudfoundry.webapp.CloudFoundryWebAppImpl;
import java.util.Map;

import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.core.sensor.Sensors;
import org.apache.brooklyn.util.collections.MutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserProvidedServiceImpl extends CloudFoundryServiceImpl implements UserProvidedService {

    public static final Logger log = LoggerFactory.getLogger(UserProvidedServiceImpl.class);

    public UserProvidedServiceImpl() {
        super(MutableMap.of(), null);
    }

    public UserProvidedServiceImpl(Entity parent) {
        this(MutableMap.of(), parent);
    }

    public UserProvidedServiceImpl(Map properties) {
        this(properties, null);
    }

    public UserProvidedServiceImpl(Map properties, Entity parent) {
        super(properties, parent);
    }

    @Override
    public Class getDriverInterface() {
        return UserProvidedServiceDriver.class;
    }

    @Override
    public UserProvidedServiceDriver getDriver() {
        return (UserProvidedServiceDriver) super.getDriver();
    }

    @Override
    public String getServiceTypeId() {
        return getConfig(SERVICE_TYPE);
    }

    @Override
    public void operation(CloudFoundryWebAppImpl app) {
        getDriver().operation(app);
    }

    @Override
    public void setBindingCredentialsFromApp(CloudFoundryWebAppImpl webapp) {
        String webappName = webapp.getConfig(CloudFoundryWebApp.APPLICATION_NAME);
        
        for (Map.Entry<String,Object> entry : getCredentials().entrySet()) {
            setAttribute(Sensors.newStringSensor(
                            createACredentialSensorName(webappName, entry.getKey())),
                    (String) entry.getValue()); 
        }
    }
    
    public Map<String,Object> getCredentials() {
        return getConfig(UserProvidedService.CREDENTIALS);
    }
    
    public String getServiceName() {
        return getConfig(UserProvidedService.SERVICE_INSTANCE_NAME);
    }

}
