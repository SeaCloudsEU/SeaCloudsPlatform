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

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.cloudfoundry.CloudFoundryEntity;
import brooklyn.entity.cloudfoundry.webapp.CloudFoundryWebAppImpl;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.AttributeSensorAndConfigKey;
import brooklyn.event.basic.Sensors;
import brooklyn.location.cloudfoundry.CloudFoundryPaasLocation;
import brooklyn.util.flags.SetFromFlag;
import brooklyn.util.text.Identifiers;

@ImplementedBy(CloudFoundryServiceImpl.class)
public interface CloudFoundryService extends CloudFoundryEntity {

    public static final AttributeSensor<String> SERVICE_TYPE_ID= Sensors
            .newStringSensor("cloudFoundry.service.type.id", "Type Id of the service");

    @SetFromFlag("serviceInstanceName")
    ConfigKey<String> SERVICE_INSTANCE_NAME = ConfigKeys.newStringConfigKey(
            "cloudFoundry.service.instance.name", "Given name for the service instance",
            "cf-service-" + Identifiers.makeRandomId(8));

    @SetFromFlag("plan")
    AttributeSensorAndConfigKey<String, String> PLAN = ConfigKeys.newStringSensorAndConfigKey(
            "cloudFoundry.service.plan", "Selected plan for the service");

    AttributeSensor<CloudFoundryPaasLocation> PAAS_LOCATION = Sensors.newSensor(
            CloudFoundryPaasLocation.class, "cloudFoundry.service.paasLocation",
            "Location used to deploy the service");

    /**
     * Retrieves the Service Type in cloud foundry, for example ClearDb.
     * @return
     */
    public String getServiceTypeId();


    /**
     * Operation of this service
     */
    public void operation(CloudFoundryWebAppImpl app);

    /**
     * Injects a set of sensors inside {@link brooklyn.entity.cloudfoundry.webapp.CloudFoundryWebAppImpl} that provides the credentials.
     * @param webapp the application that contains the credentials for this service.
     */
    public void setBindingCredentialsFromApp(CloudFoundryWebAppImpl webapp);
}
