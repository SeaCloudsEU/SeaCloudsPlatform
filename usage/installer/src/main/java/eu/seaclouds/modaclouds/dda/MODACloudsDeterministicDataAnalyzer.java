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
package eu.seaclouds.modaclouds.dda;

import java.net.URI;

import org.apache.brooklyn.api.catalog.Catalog;
import org.apache.brooklyn.config.ConfigKey;
import org.apache.brooklyn.core.entity.Attributes;
import org.apache.brooklyn.core.config.ConfigKeys;
import org.apache.brooklyn.entity.software.base.SoftwareProcess;
import org.apache.brooklyn.entity.java.UsesJava;
import org.apache.brooklyn.api.entity.ImplementedBy;
import org.apache.brooklyn.api.objs.HasShortName;
import org.apache.brooklyn.api.sensor.AttributeSensor;
import org.apache.brooklyn.core.sensor.BasicAttributeSensorAndConfigKey;
import org.apache.brooklyn.core.sensor.BasicAttributeSensorAndConfigKey.StringAttributeSensorAndConfigKey;
import org.apache.brooklyn.core.sensor.PortAttributeSensorAndConfigKey;
import org.apache.brooklyn.util.core.flags.SetFromFlag;

@Catalog(name = "MODAClouds Deterministic Data Analyzer", description = "MODAClouds Deterministic Data Analyzer", iconUrl =
        "classpath:///modaclouds.png")
@ImplementedBy(MODACloudsDeterministicDataAnalyzerImpl.class)
public interface MODACloudsDeterministicDataAnalyzer extends SoftwareProcess, UsesJava, HasShortName {

   @SetFromFlag("version")
   ConfigKey<String> SUGGESTED_VERSION =
           ConfigKeys.newConfigKeyWithDefault(SoftwareProcess.SUGGESTED_VERSION, "0.2");

   @SetFromFlag("downloadUrl")
   BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = new StringAttributeSensorAndConfigKey(
           SoftwareProcess.DOWNLOAD_URL, "https://github" +
           ".com/deib-polimi/tower4clouds/releases/download/v${version}/data-analyzer-${version}.tar.gz");
   
   @SetFromFlag("modacloudsDeterministicDataAnalyzerPort")
   PortAttributeSensorAndConfigKey MODACLOUDS_DDA_PORT = new PortAttributeSensorAndConfigKey("modaclouds.dda.port",
           "MODAClouds Deterministic Data Analyzer port", "8175+");

   AttributeSensor<URI> DDA_CONSOLE_URI = Attributes.MAIN_URI;
}
