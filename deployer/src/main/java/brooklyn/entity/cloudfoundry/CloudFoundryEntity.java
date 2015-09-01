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
package brooklyn.entity.cloudfoundry;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.BrooklynConfigKeys;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.Lifecycle;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.entity.trait.Startable;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.Sensors;
import brooklyn.location.cloudfoundry.CloudFoundryPaasLocation;
import brooklyn.util.flags.SetFromFlag;
import brooklyn.util.time.Duration;

@ImplementedBy(CloudFoundryEntityImpl.class)
public interface CloudFoundryEntity extends Entity, Startable {

    @SetFromFlag("startTimeout")
    ConfigKey<Duration> START_TIMEOUT = BrooklynConfigKeys.START_TIMEOUT;

    @SetFromFlag("maxRebindSensorsDelay")
    ConfigKey<Duration> MAXIMUM_REBIND_SENSOR_CONNECT_DELAY = ConfigKeys
            .newConfigKey(Duration.class, "cloudFoundryWebApp.maxSensorRebindDelay",
                    "The maximum delay to apply when reconnecting sensors when rebinding to " +
                            "this entity. Brooklyn will wait a random amount of time, up to the " +
                            "value of this config key, to avoid a thundering herd problem when " +
                            "the entity shares its machine with several others. Set to null or " +
                            "to 0 to disable any delay.",
                    Duration.TEN_SECONDS);

    AttributeSensor<CloudFoundryPaasLocation> PAAS_LOCATION = Sensors.newSensor(
            CloudFoundryPaasLocation.class, "cloudFoundryWebApp.paasLocation",
            "Location used to deploy the application");

    AttributeSensor<Boolean> SERVICE_PROCESS_IS_RUNNING = Sensors.newBooleanSensor(
            "service.process.isRunning",
            "Whether the process for the service is confirmed as running");

    AttributeSensor<Lifecycle> SERVICE_STATE_ACTUAL = Attributes.SERVICE_STATE_ACTUAL;


}
