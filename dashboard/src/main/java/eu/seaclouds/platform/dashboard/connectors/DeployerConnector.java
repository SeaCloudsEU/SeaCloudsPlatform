/*
 * Copyright 2014 SeaClouds
 * Contact: dev@seaclouds-project.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.seaclouds.platform.dashboard.connectors;

import brooklyn.rest.client.BrooklynApi;
import eu.seaclouds.platform.dashboard.ConfigParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instantiates connection with Deployer engine (Brooklyn)
 */
public class DeployerConnector {
    static Logger log = LoggerFactory.getLogger(DeployerConnector.class);

    private static BrooklynApi brooklynApi;

    public static BrooklynApi getConnection() {
        if (brooklynApi == null) {
            if (ConfigParameters.DEPLOYER_ENDPOINT == null){
                log.error("Deployer endpoint is not properly set");
            } else {
                log.debug("Connecting to Deployer at " + ConfigParameters.DEPLOYER_ENDPOINT);

                if (ConfigParameters.DEPLOYER_USERNAME == null){
                    log.info("Login username not defined. Accessing without login credentials...");
                    brooklynApi = new BrooklynApi(
                            ConfigParameters.DEPLOYER_ENDPOINT);
                } else {
                    brooklynApi = new BrooklynApi(
                            ConfigParameters.DEPLOYER_ENDPOINT,
                            ConfigParameters.DEPLOYER_USERNAME,
                            ConfigParameters.DEPLOYER_PASSWORD);
                }
            }
        }
        return brooklynApi;
    }
}
