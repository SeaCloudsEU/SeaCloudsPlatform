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
package eu.seaclouds.common.entities.modaclouds.manager;

import brooklyn.catalog.Catalog;
import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.java.UsesJava;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.entity.trait.HasShortName;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey.StringAttributeSensorAndConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.util.flags.SetFromFlag;

@Catalog(name = "MODAClouds Monitoring Manager", description = "MODAClouds Monitoring Manager", iconUrl =
        "classpath:///modaclouds.png")
@ImplementedBy(MODACloudsMonitoringManagerImpl.class)
public interface MODACloudsMonitoringManager extends SoftwareProcess, UsesJava, HasShortName {

    @SetFromFlag("version")
    ConfigKey<String> SUGGESTED_VERSION =
            ConfigKeys.newConfigKeyWithDefault(SoftwareProcess.SUGGESTED_VERSION, "0.2");

    @SetFromFlag("downloadUrl")
    BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = new StringAttributeSensorAndConfigKey(
            SoftwareProcess.DOWNLOAD_URL,
            "https://github.com/deib-polimi/tower4clouds/releases/download/v${version}/manager-server-${version}.tar.gz");
    
    @SetFromFlag("modacloudsMonitoringManagerPort")
    PortAttributeSensorAndConfigKey MODACLOUDS_MM_PORT = new PortAttributeSensorAndConfigKey("modaclouds.mm.port",
            "MODAClouds Monitoring Manager port", "8170+");

    @SetFromFlag("modacloudsDdaIp")
    ConfigKey<String> MODACLOUDS_DDA_IP = ConfigKeys.newStringConfigKey("modaclouds.dda.ip", "127.0.0.1");

    @SetFromFlag("modacloudsDdaPort")
    ConfigKey<Integer> MODACLOUDS_DDA_PORT = ConfigKeys.newIntegerConfigKey("modaclouds.dda.port", "", 8175);
    
    @SetFromFlag("modacloudsHistoryDBIp")
    ConfigKey<String> MODACLOUDS_HISTORYDB_IP = ConfigKeys.newStringConfigKey("modaclouds.historydb.ip", "127.0.0.1");

    @SetFromFlag("modacloudsHistoryDBPort")
    ConfigKey<Integer> MODACLOUDS_HISTORYDB_PORT = ConfigKeys.newIntegerConfigKey("modaclouds.historydb.port", "", 31337);

}
