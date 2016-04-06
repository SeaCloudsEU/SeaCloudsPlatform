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
package org.apache.brooklyn.entity.cloudfoundry.webapp.java;


import org.apache.brooklyn.api.entity.ImplementedBy;
import org.apache.brooklyn.api.sensor.AttributeSensor;
import org.apache.brooklyn.config.ConfigKey;
import org.apache.brooklyn.core.config.ConfigKeys;
import org.apache.brooklyn.core.config.MapConfigKey;
import org.apache.brooklyn.core.entity.trait.Resizable;
import org.apache.brooklyn.core.sensor.Sensors;
import org.apache.brooklyn.entity.cloudfoundry.webapp.CloudFoundryWebApp;
import org.apache.brooklyn.util.core.flags.SetFromFlag;

/**
 * Java webapp entity for being deployed in a CloudFoundry location.
 */
@ImplementedBy(JavaCloudFoundryPaasWebAppImpl.class)
public interface JavaCloudFoundryPaasWebApp extends CloudFoundryWebApp, Resizable {

    @SetFromFlag("buildpack")
    ConfigKey<String> BUILDPACK = ConfigKeys.newStringConfigKey(
            "cloudFoundryWebApp.application.buildpack", "URL of the required buildpack",
            "https://github.com/cloudfoundry/java-buildpack.git");

    @SetFromFlag("jm.resource")
    ConfigKey<String> MAIN_MONITOR_RESOURCE = ConfigKeys.newStringConfigKey(
            "app.monitor.resource", "Main resource that will be used to monitor the app",
            "/ GET");

    public static final AttributeSensor<String> MONITOR_URL =
            Sensors.newStringSensor("app.monitor.url", "URL for monitoring the app");

    public static final AttributeSensor<Long> USED_MEMORY =
            Sensors.newLongSensor("app.usedmemory", "Memory used by Application");

    public static final AttributeSensor<Double> DURATION_SUM =
            Sensors.newDoubleSensor("app.resource.durationsum", "Total time used by a resource");

    public static final AttributeSensor<Double> RESOURCE_HITS =
            Sensors.newDoubleSensor("app.resource.hits", "Total time that a resource was used");

    public static final AttributeSensor<Double> RESOURCE_LATENCY =
            Sensors.newDoubleSensor("app.resource.latency", "Latency");

    public static final AttributeSensor<Double> SERVER_PROCESSING_TIME =
            Sensors.newDoubleSensor("app.server.processingtime", "");

    public static final AttributeSensor<Double> SERVER_REQUESTS =
            Sensors.newDoubleSensor("app.server.requests", "");

    public static final AttributeSensor<Double> SERVER_LATENCY =
            Sensors.newDoubleSensor("app.server.latency", "Latency");

    public static final AttributeSensor<Double> REQUEST_PER_SECOND =
            Sensors.newDoubleSensor("app.server.requestpersecond", "Request per second");

}
