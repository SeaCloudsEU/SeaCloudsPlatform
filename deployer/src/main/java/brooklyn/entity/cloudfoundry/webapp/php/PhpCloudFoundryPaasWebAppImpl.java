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

import brooklyn.entity.Entity;
import brooklyn.entity.cloudfoundry.webapp.CloudFoundryWebAppImpl;
import brooklyn.util.collections.MutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PhpCloudFoundryPaasWebAppImpl extends CloudFoundryWebAppImpl implements PhpCloudFoundryPaasWebApp {

    private static final Logger log = LoggerFactory.getLogger(PhpCloudFoundryPaasWebAppImpl.class);


    public PhpCloudFoundryPaasWebAppImpl() {
        super(MutableMap.of(), null);
    }

    public PhpCloudFoundryPaasWebAppImpl(Entity parent) {
        this(MutableMap.of(), parent);
    }

    public PhpCloudFoundryPaasWebAppImpl(Map properties) {
        this(properties, null);
    }

    public PhpCloudFoundryPaasWebAppImpl(Map properties, Entity parent) {
        super(properties, parent);
    }

    @Override
    public Class getDriverInterface() {

        return PhpPaasWebAppDriver.class;
    }

    @Override
    public PhpPaasWebAppDriver getDriver() {
        return (PhpPaasWebAppDriver) super.getDriver();
    }

    @Override
    public String getBuildpack(){
        return getConfig(PhpCloudFoundryPaasWebApp.BUILDPACK);
    }
    
}
