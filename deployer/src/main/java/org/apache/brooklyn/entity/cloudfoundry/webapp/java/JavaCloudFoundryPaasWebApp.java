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


import org.apache.brooklyn.entity.cloudfoundry.webapp.CloudFoundryWebApp;
import org.apache.brooklyn.api.entity.ImplementedBy;
import org.apache.brooklyn.config.ConfigKey;
import org.apache.brooklyn.core.config.ConfigKeys;
import org.apache.brooklyn.core.config.MapConfigKey;
import org.apache.brooklyn.util.core.flags.SetFromFlag;

/**
 * Java webapp entity for being deployed in a CloudFoundry location.
 */
@ImplementedBy(JavaCloudFoundryPaasWebAppImpl.class)
public interface JavaCloudFoundryPaasWebApp extends CloudFoundryWebApp {

    @SetFromFlag("buildpack")
    ConfigKey<String> BUILDPACK= ConfigKeys.newStringConfigKey(
            "cloudFoundryWebApp.application.buildpack", "URL of the required buildpack",
            "https://github.com/cloudfoundry/java-buildpack.git");
    
    // TODO: I think that java.sysprops are dependent on the buildpack.
    @SetFromFlag("java.sysprops")
    MapConfigKey<String> JAVA_SYSPROPS = new MapConfigKey<String>(String.class,
            "cloudfoundry.java.sysprops",
            "System properties to be passed to the buildpack");
}
