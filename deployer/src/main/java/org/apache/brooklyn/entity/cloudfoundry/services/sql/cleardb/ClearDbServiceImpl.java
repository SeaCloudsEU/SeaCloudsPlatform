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
package org.apache.brooklyn.entity.cloudfoundry.services.sql.cleardb;


import org.apache.brooklyn.entity.cloudfoundry.services.CloudFoundryServiceImpl;
import org.apache.brooklyn.entity.cloudfoundry.webapp.CloudFoundryWebApp;
import org.apache.brooklyn.entity.cloudfoundry.webapp.CloudFoundryWebAppImpl;
import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.core.sensor.Sensors;
import org.apache.brooklyn.util.collections.MutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ClearDbServiceImpl extends CloudFoundryServiceImpl implements ClearDbService {

    public static final Logger log = LoggerFactory.getLogger(ClearDbServiceImpl.class);

    private final static String SERVICE_TYPE_ID = "cleardb";

    public ClearDbServiceImpl() {
        super(MutableMap.of(), null);
    }

    public ClearDbServiceImpl(Entity parent) {
        this(MutableMap.of(), parent);
    }

    public ClearDbServiceImpl(Map properties) {
        this(properties, null);
    }

    public ClearDbServiceImpl(Map properties, Entity parent) {
        super(properties, parent);
    }

    @Override
    public Class getDriverInterface() {
        return ClearDbServiceDriver.class;
    }

    @Override
    public ClearDbServiceDriver getDriver() {
        return (ClearDbServiceDriver) super.getDriver();
    }

    @Override
    public String getServiceTypeId() {
        return SERVICE_TYPE_ID;
    }

    public String getCreationScriptUrl(){
        return getConfig(ClearDbService.CREATION_SCRIPT_URL);
    }

    /**
     * This operation carry out the set up of the database.
     */
    @Override
    public void operation(CloudFoundryWebAppImpl app) {
        getDriver().operation(app);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setBindingCredentialsFromApp(CloudFoundryWebAppImpl webapp) {

        String webappName = webapp.getConfig(CloudFoundryWebApp.APPLICATION_NAME);
        Map<String, String> credentials = getServiceCredentialsFromApp(webapp);

        setAttribute(Sensors.newStringSensor(
                        createACredentialSensorName(webappName, "jdbcUrl"),
                        "JDBC URL to access the database (from app " + webappName + ")"),
                credentials.get("jdbcUrl"));

        setAttribute(Sensors.newStringSensor(
                        createACredentialSensorName(webappName, "name"),
                        "name of the database created for app " + webappName),
                credentials.get("name"));

        setAttribute(Sensors.newStringSensor(
                        createACredentialSensorName(webappName, "hostname"),
                        "hostname of the database created for app " + webappName),
                credentials.get("hostname"));

        setAttribute(Sensors.newStringSensor(
                        createACredentialSensorName(webappName, "port"),
                        "HTTP port to access the database created for app " + webappName),
                credentials.get("port"));

        setAttribute(Sensors.newStringSensor(
                        createACredentialSensorName(webappName, "username"),
                        "username for the database created for app " + webappName),
                credentials.get("username"));

        setAttribute(Sensors.newStringSensor(
                        createACredentialSensorName(webappName, "password"),
                        "password for the database created for app " + webappName),
                credentials.get("password"));
    }


}
