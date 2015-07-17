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
package brooklyn.entity.cloudfoundry.webapp.php;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.cloudfoundry.webapp.CloudFoundryWebApp;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.util.flags.SetFromFlag;

/**
 * Php webapp entity for being deployed in a CloudFoundry location.
 */
@ImplementedBy(PhpCloudFoundryPaasWebAppImpl.class)
public interface PhpCloudFoundryPaasWebApp extends CloudFoundryWebApp {

    @SetFromFlag("buildpack")
    ConfigKey<String> BUILDPACK = ConfigKeys.newStringConfigKey(
            "cloudFoundryWebApp.application.buildpack", "URL of the required buildpack",
            "https://github.com/cloudfoundry/php-buildpack.git");

}
