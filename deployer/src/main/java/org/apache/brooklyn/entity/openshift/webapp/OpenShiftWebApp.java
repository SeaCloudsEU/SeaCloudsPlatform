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
package org.apache.brooklyn.entity.openshift.webapp;

import com.google.common.collect.ImmutableMap;
import org.apache.brooklyn.api.entity.ImplementedBy;
import org.apache.brooklyn.api.sensor.AttributeSensor;
import org.apache.brooklyn.config.ConfigKey;
import org.apache.brooklyn.core.config.ConfigKeys;
import org.apache.brooklyn.core.config.MapConfigKey;
import org.apache.brooklyn.core.sensor.Sensors;
import org.apache.brooklyn.entity.openshift.OpenShiftEntity;
import org.apache.brooklyn.util.core.flags.SetFromFlag;
import org.apache.brooklyn.util.text.Identifiers;

@ImplementedBy(OpenShiftWebAppImpl.class)
public interface OpenShiftWebApp extends OpenShiftEntity {

    @SetFromFlag("application-name")
    ConfigKey<String> APPLICATION_NAME = ConfigKeys.newStringConfigKey(
            "openshiftWebApp.application.name", "Name of the application"
            , "os-app-" + Identifiers.makeRandomId(8));

    @SetFromFlag("git-url-repo")
    ConfigKey<String> GIT_REPOSITORY_URL = ConfigKeys.newStringConfigKey(
            "openshiftWebApp.application.git.url",
            "URL of repository which contains the application");

    //TODO, should it be moved to Location config?
    @SetFromFlag("application-domain")
    ConfigKey<String> DOMAIN = ConfigKeys.newStringConfigKey(
            "openshiftWebApp.application.domain",
            "Application domain used by the user", "brooklyndomain");

    @SetFromFlag("env")
    public static final MapConfigKey<String> ENV =
            new MapConfigKey<String>(String.class, "openshift.webapp.env",
                    "List of user-defined environment variables",
                    ImmutableMap.<String, String>of());

    AttributeSensor<String> ROOT_URL =
            Sensors.newStringSensor("webapp.url", "URL of the application");

    /**
     * @return URL of the OpenShift Cartridge needed for building the application
     */
    public String getCartridge();

}