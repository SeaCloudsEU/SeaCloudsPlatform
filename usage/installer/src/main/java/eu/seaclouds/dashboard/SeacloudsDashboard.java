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
package eu.seaclouds.dashboard;

import brooklyn.catalog.Catalog;
import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.java.UsesJava;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.entity.trait.HasShortName;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.location.basic.PortRanges;
import brooklyn.util.flags.SetFromFlag;

@Catalog(name = "SeaClouds Dashboard", description = "SeaClouds Dashboard", iconUrl =
        "classpath:///seaclouds.png")
@ImplementedBy(SeacloudsDashboardImpl.class)
public interface SeacloudsDashboard extends SoftwareProcess, UsesJava, HasShortName {

    @SetFromFlag("version")
    ConfigKey<String> SUGGESTED_VERSION =
            ConfigKeys.newConfigKeyWithDefault(SoftwareProcess.SUGGESTED_VERSION, "0.1.0-SNAPSHOT");

    @SetFromFlag("configUrl")
    BasicAttributeSensorAndConfigKey<String> CONFIG_URL = new BasicAttributeSensorAndConfigKey.StringAttributeSensorAndConfigKey(
            "dashboard.config.url", "A URL of a YAML file to use to configure the dashboard",
            "classpath://eu/seaclouds/dashboard/config.yml.template");
    
    @SetFromFlag("finalConfigName")
    ConfigKey<String> FINAL_CONFIG_NAME = ConfigKeys.newStringConfigKey("seaclouds.dashboard.config.final.name","Final name for configuration file", "config.yml");


    @SetFromFlag("downloadUrl")
    BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = new BasicAttributeSensorAndConfigKey.StringAttributeSensorAndConfigKey(
            Attributes.DOWNLOAD_URL, "https://oss.sonatype.org/service/local/artifact/maven/redirect?r=snapshots&g=eu.seaclouds-project&a=dashboard&v=0.1.0-SNAPSHOT&e=jar");

    @SetFromFlag("port")
    PortAttributeSensorAndConfigKey DASHBOARD_PORT = new PortAttributeSensorAndConfigKey(
            "seaclouds.dashboard.port", "", PortRanges.fromInteger(8000));

    @SetFromFlag("adminPort")
    PortAttributeSensorAndConfigKey DASHBOARD_ADMIN_PORT = new PortAttributeSensorAndConfigKey(
            "seaclouds.dashboard.adminPort", "", PortRanges.fromInteger(8001));
    
    @SetFromFlag("deployerHost")
    ConfigKey<String> DEPLOYER_HOST = 
            ConfigKeys.newStringConfigKey("seaclouds.dashboard.deployer.host", "Host address for the SeaClouds deployer");

    @SetFromFlag("deployerPort")
    ConfigKey<Integer> DEPLOYER_PORT =
            ConfigKeys.newIntegerConfigKey("seaclouds.dashboard.deployer.port", "Port for the SeaClouds deployer");
    
    @SetFromFlag("deployerUser")
    ConfigKey<String> DEPLOYER_USERNAME =
            ConfigKeys.newStringConfigKey("seaclouds.dashboard.deployer.user", "Endpoint address for the SeaClouds deployer");

    @SetFromFlag("deployerPassword")
    ConfigKey<String> DEPLOYER_PASSWORD =
            ConfigKeys.newStringConfigKey("seaclouds.dashboard.deployer.password", "Endpoint address for the SeaClouds deployer");

    @SetFromFlag("monitorHost")
    ConfigKey<String> MONITOR_HOST =
            ConfigKeys.newStringConfigKey("seaclouds.dashboard.monitor.host", "Host address for the SeaClouds monitor");

    @SetFromFlag("monitorPort")
    ConfigKey<Integer> MONITOR_PORT =
            ConfigKeys.newIntegerConfigKey("seaclouds.dashboard.monitor.port", "Port for the SeaClouds monitor");

    @SetFromFlag("deployerHost")
    ConfigKey<String> PLANNER_HOST =
            ConfigKeys.newStringConfigKey("seaclouds.dashboard.planner.host", "Host address for the SeaClouds planner");

    @SetFromFlag("deployerPort")
    ConfigKey<Integer> PLANNER_PORT =
            ConfigKeys.newIntegerConfigKey("seaclouds.dashboard.planner.port", "Port for the SeaClouds planner");

    @SetFromFlag("slaHost")
    ConfigKey<String> SLA_HOST =
            ConfigKeys.newStringConfigKey("seaclouds.dashboard.sla.host", "Host address for the SeaClouds SLA manager");

    @SetFromFlag("slaPort")
    ConfigKey<Integer> SLA_PORT =
            ConfigKeys.newIntegerConfigKey("seaclouds.dashboard.sla.port", "Port for the SeaClouds SLA manager");
    
}
