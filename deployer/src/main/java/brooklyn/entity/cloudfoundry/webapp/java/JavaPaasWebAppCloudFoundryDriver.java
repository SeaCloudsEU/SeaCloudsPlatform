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
package brooklyn.entity.cloudfoundry.webapp.java;

import brooklyn.entity.cloudfoundry.LocalResourcesDownloader;
import brooklyn.entity.cloudfoundry.webapp.PaasWebAppCloudFoundryDriver;
import brooklyn.location.cloudfoundry.CloudFoundryPaasLocation;
import org.cloudfoundry.client.lib.domain.Staging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class JavaPaasWebAppCloudFoundryDriver extends PaasWebAppCloudFoundryDriver implements JavaPaasWebAppDriver {

    public static final Logger log = LoggerFactory.getLogger(JavaPaasWebAppCloudFoundryDriver.class);

    public JavaPaasWebAppCloudFoundryDriver(JavaCloudFoundryPaasWebAppImpl entity, CloudFoundryPaasLocation location) {
        super(entity, location);
    }

    @Override
    public JavaCloudFoundryPaasWebAppImpl getEntity() {
        return (JavaCloudFoundryPaasWebAppImpl) super.getEntity();
    }

    @Override
    public String getBuildpack() {
        return getEntity().getBuildpack();
    }

    @Override
    public void deploy() {
        List<String> uris = new ArrayList<String>();
        Staging staging;
        File war;
        try {
            staging = new Staging(null, getBuildpack());
            uris.add(inferApplicationDomainUri(getApplicationName()));

            war= LocalResourcesDownloader
                    .downloadResourceInLocalDir(getApplicationUrl());

            getClient().createApplication(getApplicationName(), staging,
                    getLocation().getConfig(CloudFoundryPaasLocation.REQUIRED_MEMORY),
                    uris, null);
            getClient().uploadApplication(getApplicationName(), war.getCanonicalPath());
        } catch (IOException e) {
            log.error("Error deploying application {} managed by driver {}",
                    new Object[]{getEntity(), this});
        }
    }




}


