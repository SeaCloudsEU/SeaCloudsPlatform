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
package org.apache.brooklyn.entity.openshift;

import org.apache.brooklyn.location.openshift.OpenShiftPaasLocation;
import com.openshift.client.IOpenShiftConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class PaasEntityOpenShiftDriver implements PaasEntityDriver {

    public static final Logger log = LoggerFactory
            .getLogger(PaasEntityOpenShiftDriver.class);

    private final OpenShiftPaasLocation location;
    OpenShiftEntityImpl entity;
    IOpenShiftConnection client;

    public PaasEntityOpenShiftDriver(OpenShiftEntityImpl entity,
                                     OpenShiftPaasLocation location) {
        this.entity = checkNotNull(entity, "entity");
        this.location = checkNotNull(location, "location");
        init();
    }

    protected void init() {}

    @Override
    public OpenShiftEntityImpl getEntity() {
        return entity;
    }

    @Override
    public OpenShiftPaasLocation getLocation() {
        return location;
    }

    protected IOpenShiftConnection getClient(){
        return client;
    }

    @Override
    public abstract boolean isRunning();

    @Override
    public void rebind() {
    }

    @Override
    public void start(){
        setUpClient();
    }

    protected void setUpClient() {
        if (client == null) {
            location.setUpClient();
            client = location.getOpenShiftClient();
            checkNotNull(client, "CloudFoundry client");
        }
    }

    @Override
    public abstract void restart();

    @Override
    public abstract void stop();
}
