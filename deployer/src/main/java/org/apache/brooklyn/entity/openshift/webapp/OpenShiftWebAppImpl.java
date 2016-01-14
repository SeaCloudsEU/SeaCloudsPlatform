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

import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.entity.openshift.OpenShiftEntityImpl;
import org.apache.brooklyn.util.collections.MutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class OpenShiftWebAppImpl extends OpenShiftEntityImpl
        implements OpenShiftWebApp{

    private static final Logger log = LoggerFactory.getLogger(OpenShiftWebAppImpl.class);

    public OpenShiftWebAppImpl() {
        super(MutableMap.of(), null);
    }

    public OpenShiftWebAppImpl(Entity parent) {
        this(MutableMap.of(), parent);
    }

    public OpenShiftWebAppImpl(Map properties) {
        this(properties, null);
    }

    public OpenShiftWebAppImpl(Map properties, Entity parent) {
        super(properties, parent);
    }

    @Override
    public Class getDriverInterface(){
        //This method shoud be abstract
        return PaasWebAppDriver.class;
    }

    @Override
    public String getCartridge(){
        //TODO shoudl be abstract, and the class that implements it shoudl to use a ConfigKey to
        //specify this value
        return "jbossas-7";
    }

    @Override
    public PaasWebAppDriver getDriver() {
        return (PaasWebAppDriver) super.getDriver();
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    protected void connectSensors() {
        super.connectSensors();
    }

    @Override
    protected void disconnectSensors() {
        super.disconnectSensors();
    }


}