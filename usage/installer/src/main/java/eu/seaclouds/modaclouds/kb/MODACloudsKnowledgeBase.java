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
package eu.seaclouds.modaclouds.kb;

import java.net.URI;

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

@Catalog(name = "MODAClouds Knowledge Base", description = "MODAClouds Knowledge Base", iconUrl = "classpath:///modaclouds.png")
@ImplementedBy(MODACloudsKnowledgeBaseImpl.class)
public interface MODACloudsKnowledgeBase extends SoftwareProcess, HasShortName {

   @SetFromFlag("version")
   ConfigKey<String> SUGGESTED_VERSION =
           ConfigKeys.newConfigKeyWithDefault(SoftwareProcess.SUGGESTED_VERSION, "1.1.1");

   @SetFromFlag("downloadUrl")
   BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = new StringAttributeSensorAndConfigKey(
           SoftwareProcess.DOWNLOAD_URL, "http://archive.apache.org/dist/jena/binaries/jena-fuseki-${version}-distribution.tar.gz");

   @SetFromFlag("modacloudsKnowledgeBasePort")
   PortAttributeSensorAndConfigKey MODACLOUDS_KB_PORT = new PortAttributeSensorAndConfigKey("modaclouds.kb.port",
           "MODAClouds Knowledge Base port", "3030+");

   @SetFromFlag("modacloudsKnowledgeBasePath")
   BasicAttributeSensorAndConfigKey<String> MODACLOUDS_KB_PATH = new StringAttributeSensorAndConfigKey("modaclouds.kb.path", "MODAClouds Knowledge Base", "/modaclouds/kb");

   @SetFromFlag("modacloudsKnowledgeBaseDatastore")
   ConfigKey<String> MODACLOUDS_KB_DATASTORE_FOLDER = ConfigKeys.newStringConfigKey("modaclouds.kb.datastore",
           "MODAClouds Knowledge Base Datastore", "/tmp");

   AttributeSensor<URI> KB_CONSOLE_URI = Attributes.MAIN_URI;

}
