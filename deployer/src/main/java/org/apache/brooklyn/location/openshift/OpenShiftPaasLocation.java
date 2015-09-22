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
package org.apache.brooklyn.location.openshift;

import com.openshift.client.ConnectionBuilder;
import com.openshift.client.IOpenShiftConnection;
import org.apache.brooklyn.config.ConfigKey;
import org.apache.brooklyn.core.config.ConfigKeys;
import org.apache.brooklyn.core.location.AbstractLocation;
import org.apache.brooklyn.location.paas.PaasLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OpenShiftPaasLocation extends AbstractLocation
        implements PaasLocation {


    public static final Logger LOG = LoggerFactory.getLogger(OpenShiftPaasLocation.class);

    public static ConfigKey<String> OS_USER = ConfigKeys.newStringConfigKey("user");
    public static ConfigKey<String> OS_PASSWORD = ConfigKeys.newStringConfigKey("password");

    IOpenShiftConnection client;

    public OpenShiftPaasLocation() {
        super();
    }

    @Override
    public void init() {
        super.init();
    }

    public void setUpClient() {
        try{
        if (client == null) {
            client = new ConnectionBuilder().credentials(getConfig(OS_USER),
                    getConfig(OS_PASSWORD)).disableSSLCertificateChecks().create();
        }
        } catch(IOException e){
            throw new RuntimeException("Error during location client setup on location" + this);
        }
    }

    @Override
    public String getPaasProviderName() {
        return "OpenShift";
    }

    public IOpenShiftConnection getOpenShiftClient() {
        return client;
    }

}
