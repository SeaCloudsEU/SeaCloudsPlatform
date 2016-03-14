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
package org.apache.brooklyn.entity.cloudfoundry.webapp;

import org.apache.brooklyn.entity.cloudfoundry.CloudFoundryEntity;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.api.sensor.AttributeSensor;
import org.apache.brooklyn.config.ConfigKey;
import org.apache.brooklyn.core.annotation.Effector;
import org.apache.brooklyn.core.annotation.EffectorParam;
import org.apache.brooklyn.core.config.BasicConfigKey;
import org.apache.brooklyn.core.config.ConfigKeys;
import org.apache.brooklyn.core.config.MapConfigKey;
import org.apache.brooklyn.core.effector.MethodEffector;
import org.apache.brooklyn.core.sensor.BasicAttributeSensor;
import org.apache.brooklyn.core.sensor.Sensors;
import org.apache.brooklyn.util.core.flags.SetFromFlag;
import org.apache.brooklyn.util.text.Identifiers;

import java.util.List;

/**
 * Generic web application to be deployed on a CloudFoundry location.
 */
public interface CloudFoundryWebApp extends CloudFoundryEntity {

    @SetFromFlag("application-name")
    ConfigKey<String> APPLICATION_NAME = ConfigKeys.newStringConfigKey(
            "cloudFoundryWebApp.application.name", "Name of the application");

    @SetFromFlag("application-url")
    ConfigKey<String> APPLICATION_URL = ConfigKeys.newStringConfigKey(
            "cloudFoundryWebApp.application.url", "URI of the application");

    @SuppressWarnings("unchecked")
    @SetFromFlag("bind")
    ConfigKey<List<Entity>> NAMED_SERVICES = new BasicConfigKey(List.class,
            "cloudFoundry.webapp.boundServices",
            "List of names of the services that should be bound to this application, " +
                    "providing credentials for its usage");
    
    @SuppressWarnings("unchecked")
    @SetFromFlag("bound_services")
    public static final AttributeSensor<List<String>> BOUND_SERVICES =
            new BasicAttributeSensor<List<String>>(new TypeToken<List<String>>() {},
            "cloudFoundry.webapp.boundServices",
            "List of names of the services that were bound to this application, " +
                    "providing credentials for its usage");

    @SetFromFlag("env")
    public static final MapConfigKey<String> ENV =
            new MapConfigKey<String>(String.class, "cloudfoundry.webapp.env",
                    "List of user-defined environment variables",
                    ImmutableMap.<String, String>of());
    
    AttributeSensor<String> ROOT_URL =
            Sensors.newStringSensor("webapp.url", "URL of the application");

    public static final AttributeSensor<String> VCAP_SERVICES =
            Sensors.newStringSensor("webapp.vcap.services",
                    "JSON information related to services bound to the application, " +
                            "such as credentials, endpoint information, selected plan, etc.");

    public static final AttributeSensor<Integer> INSTANCES_NUM =
            Sensors.newIntegerSensor("app.running.instances",
                    "Instances which are used to run the application");

    public static final AttributeSensor<Integer> MEMORY =
            Sensors.newIntegerSensor("app.running.ram",
                    "Current RAM assigned to the application MB");

    public static final AttributeSensor<Integer> DISK =
            Sensors.newIntegerSensor("app.running.disk", "Assigned disk to the application (MB)");

    /**
     * @return URL of the CloudFoundry Buildpack needed for building the application
     */
    public String getBuildpack();

    public static final MethodEffector<Void> DEPLOY =
            new MethodEffector<Void>(CloudFoundryWebApp.class, "setEnv");

    @Effector(description="Set an environment variable that can be retrieved by the web application")
    public void setEnv(@EffectorParam(name = "name", description = "Name of the variable") String key,
                       @EffectorParam(name = "value", description = "Value of the environment variable") String value);

    @Effector(description="Set the instances number that will be user by the web application")
        public void setInstancesNumber(@EffectorParam(name = "instancesNumber", description = "Number of " +
            "instance that are being used by the application") int instancesNumber);

    @Effector(description="Set the disk quota that will be used by the web application")
    public void setDiskQuota(@EffectorParam(name = "diskQuota", description = "Disk amount" +
            " that will be used by the web application") int diskQuota);

    @Effector(description="Set an Ram Memory limit for the web application")
    public void setAmountMemory(@EffectorParam(name = "memory", description = "Disk amount" +
            " that will be used by the web application") int memory);

}
