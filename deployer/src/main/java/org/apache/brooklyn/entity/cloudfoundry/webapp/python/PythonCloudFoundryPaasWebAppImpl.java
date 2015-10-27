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
package org.apache.brooklyn.entity.cloudfoundry.webapp.python;

import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.entity.cloudfoundry.webapp.CloudFoundryWebAppImpl;
import org.apache.brooklyn.util.collections.MutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PythonCloudFoundryPaasWebAppImpl extends CloudFoundryWebAppImpl implements PythonCloudFoundryPaasWebApp {

    private static final Logger log = LoggerFactory.getLogger(PythonCloudFoundryPaasWebAppImpl.class);


    public PythonCloudFoundryPaasWebAppImpl() {
        super(MutableMap.of(), null);
    }

    public PythonCloudFoundryPaasWebAppImpl(Entity parent) {
        this(MutableMap.of(), parent);
    }

    public PythonCloudFoundryPaasWebAppImpl(Map properties) {
        this(properties, null);
    }

    public PythonCloudFoundryPaasWebAppImpl(Map properties, Entity parent) {
        super(properties, parent);
    }

    @Override
    public Class getDriverInterface() {

        return PythonPaasWebAppDriver.class;
    }

    @Override
    public PythonPaasWebAppDriver getDriver() {
        return (PythonPaasWebAppDriver) super.getDriver();
    }

    @Override
    public String getBuildpack(){
        return getConfig(PythonCloudFoundryPaasWebApp.BUILDPACK);
    }
    
}
