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
package eu.seaclouds.common.entities.modaclouds.manager;

import brooklyn.catalog.Catalog;
import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.entity.trait.HasShortName;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey.StringAttributeSensorAndConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.util.flags.SetFromFlag;
import java.net.URI;

@Catalog(name = "MODAClouds Monitoring Manager", description = "MODAClouds Monitoring Manager", iconUrl =
        "classpath:///modaclouds.png")
@ImplementedBy(MODACloudsMonitoringManagerImpl.class)
public interface MODACloudsMonitoringManager extends SoftwareProcess, HasShortName {

    @SetFromFlag("version")
    ConfigKey<String> SUGGESTED_VERSION =
            ConfigKeys.newConfigKeyWithDefault(SoftwareProcess.SUGGESTED_VERSION, "1.7");

    @SetFromFlag("downloadUrl")
    BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = new StringAttributeSensorAndConfigKey(
            SoftwareProcess.DOWNLOAD_URL,
            "https://github.com/deib-polimi/modaclouds-monitoring-manager/releases/download/v${version}/monitoring-manager-${version}-distribution.tar.gz");

    @SetFromFlag("modacloudsMonitoringManagerPort")
    PortAttributeSensorAndConfigKey MODACLOUDS_MM_PORT = new PortAttributeSensorAndConfigKey("modaclouds.mm.port",
            "MODAClouds Monitoring Manager port", "8170+");

    @SetFromFlag("modacloudsDdaIp")
    ConfigKey<String> MODACLOUDS_DDA_IP = ConfigKeys.newStringConfigKey("modaclouds.dda.ip", "127.0.0.1");

    @SetFromFlag("modacloudsDdaPort")
    ConfigKey<Integer> MODACLOUDS_DDA_PORT = ConfigKeys.newIntegerConfigKey("modaclouds.dda.port", "", 8175);

    @SetFromFlag("modacloudsKbIp")
    ConfigKey<String> MODACLOUDS_KB_IP = ConfigKeys.newStringConfigKey("modaclouds.kb.ip", "127.0.0.1");

    @SetFromFlag("modacloudsKbPort")
    ConfigKey<Integer> MODACLOUDS_KB_PORT = ConfigKeys.newIntegerConfigKey("modaclouds.kb.port", "", 3030);

    @SetFromFlag("modacloudsKbDatasetPath")
    ConfigKey<String> MODACLOUDS_KB_DATASET_PATH = ConfigKeys.newStringConfigKey("modaclouds.kb.dataset.path",
            "/modaclouds/kb");

   AttributeSensor<URI> KB_CONSOLE_URI = Attributes.MAIN_URI;

}
