/*
 * Copyright 2015 SeaClouds
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
package org.apache.brooklyn.entity.php.httpd;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.brooklyn.api.catalog.Catalog;
import org.apache.brooklyn.api.entity.ImplementedBy;
import org.apache.brooklyn.api.objs.HasShortName;
import org.apache.brooklyn.api.sensor.AttributeSensor;
import org.apache.brooklyn.config.ConfigKey;
import org.apache.brooklyn.core.config.ConfigKeys;
import org.apache.brooklyn.core.config.MapConfigKey;
import org.apache.brooklyn.core.sensor.BasicAttributeSensorAndConfigKey;
import org.apache.brooklyn.core.sensor.Sensors;
import org.apache.brooklyn.entity.php.PhpWebAppService;
import org.apache.brooklyn.entity.php.PhpWebAppSoftwareProcess;
import org.apache.brooklyn.entity.software.base.SoftwareProcess;
import org.apache.brooklyn.util.core.flags.SetFromFlag;

@Catalog(name = "Apache2 HTTPD Server", description = "Apache2 HTTPD Project is an open-source HTTP server")
@ImplementedBy(PhpHttpdServerImpl.class)
public interface PhpHttpdServer extends PhpWebAppSoftwareProcess, PhpWebAppService, HasShortName {

    @SetFromFlag("version")
    ConfigKey<String> SUGGESTED_VERSION =
            ConfigKeys.newConfigKeyWithDefault(SoftwareProcess.SUGGESTED_VERSION, "2.4.7");
    
    //TODO Review This definition
    @SetFromFlag("configDir")
    BasicAttributeSensorAndConfigKey<String> CONFIG_DIR = new BasicAttributeSensorAndConfigKey.StringAttributeSensorAndConfigKey(
            SoftwareProcess.INSTALL_DIR, "/etc/apache2");
    //TODO Review This definition

    @SetFromFlag("configuration.dir")
    BasicAttributeSensorAndConfigKey<String> CONFIGURATION_DIR =
            new BasicAttributeSensorAndConfigKey.StringAttributeSensorAndConfigKey("httpd.configuration.dir", "Configuration Dir", "/etc/apache2");

    @SetFromFlag("sites.available.folder")
    BasicAttributeSensorAndConfigKey<String> SITES_AVAILABLE =
            new BasicAttributeSensorAndConfigKey.StringAttributeSensorAndConfigKey("httpd.configuration.dir.available.sites", "Folder that contains the configuration and pointed to " +
                    "the deploy and run folders where deploy the apps ", "/sites-available");
    
    @SetFromFlag("site.configuration.filename")
    ConfigKey<String> SITE_CONFIGURATION_FILE =
            ConfigKeys.newStringConfigKey("site.configuration.filename", "Filename for the site configuration");

    @SetFromFlag("deploy.run.dir")
    ConfigKey<String> DEPLOY_RUN_DIR =
            ConfigKeys.newConfigKey("httpd.deploy.run.dir", "Folder to deploy and run the App", "/var/www");

    @SetFromFlag("defaultGroup")
    public static final ConfigKey<String> DEFAULT_GROUP = ConfigKeys.newStringConfigKey(
            "httpd.default.group", "Default user group for applications deployed in DEPLOY_RUN_DIR", "www-data");
    
    @SetFromFlag("server.status.url")
    ConfigKey<String> SERVER_STATUS_URL =
            ConfigKeys.newConfigKey("httpd.monitor.url", "Relative path showing default server status", "server-status?auto");

    @SetFromFlag("php.env.variables")
    MapConfigKey<String> PHP_ENV_VARIABLES =
            new MapConfigKey<String>(String.class, "http.php.env.variables",
                    "PHP env variables properties", ImmutableMap.<String, String>of());

    @SetFromFlag("monitor.url.isUp")
    AttributeSensor<Boolean> SERVER_STATUS_IS_UP =
            Sensors.newBooleanSensor("webapp.monitor.up", "Httpd status service is up and running");

    @SetFromFlag("traffic.kb")
    AttributeSensor<Long> TOTAL_KBYTE =
            Sensors.newLongSensor("webapp.total.kb", "Total server traffic in KB");

    @SetFromFlag("cpu.load")
    AttributeSensor<Double> CPU_LOAD =
            Sensors.newDoubleSensor("webapp.cpu.load", "CPU load percent");
    
    @SetFromFlag("request.perSec")
    AttributeSensor<Double> REQUEST_PER_SEC =
            Sensors.newDoubleSensor("webapp.reqs.perSec", "Requests per sec being managed by the server");

    @SetFromFlag("bytes.perSec")
    AttributeSensor<Double> BYTES_PER_SEC =
            Sensors.newDoubleSensor("webapp.bytes.perSec", "Bytes per second");

    @SetFromFlag("bytes.perReq")
    AttributeSensor<Double> BYTES_PER_REQ =
            Sensors.newDoubleSensor("webapp.bytes.per.req", "Bytes per requests");

    @SetFromFlag("busy.workers")
    AttributeSensor<Integer> BUSY_WORKERS =
            Sensors.newIntegerSensor("webapp.busy.workers", "Number of busy worker");


}
