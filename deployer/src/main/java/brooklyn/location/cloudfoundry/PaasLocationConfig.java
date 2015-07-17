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
package brooklyn.location.cloudfoundry;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.Sensors;
import brooklyn.util.flags.SetFromFlag;

public interface PaasLocationConfig {

    @SetFromFlag("profile.instances")
    ConfigKey<Integer> REQUIRED_INSTANCES = ConfigKeys.newIntegerConfigKey(
            "profile.instances", "Required instances to deploy the application", 1);

    @SetFromFlag("profile.instances")
    ConfigKey<Integer> REQUIRED_MEMORY = ConfigKeys.newIntegerConfigKey(
            "profile.memory", "Required memory to deploy the application (MB)", 512);

    @SetFromFlag("profile.instances")
    ConfigKey<Integer> REQUIRED_DISK = ConfigKeys.newIntegerConfigKey(
            "profile.disk", "Required disk to deploy the application (MB)", 1024);

    public static final AttributeSensor<Integer> INSTANCES_NUM =
            Sensors.newIntegerSensor("app.running.instances",
                    "Instances which are used to run the application");

    public static final AttributeSensor<Integer> MEMORY =
            Sensors.newIntegerSensor("app.running.ram", "Current RAM assigned to the application MB");

    public static final AttributeSensor<Integer> DISK =
            Sensors.newIntegerSensor("app.running.disk", "Assigned disk to the application (MB)");


}
