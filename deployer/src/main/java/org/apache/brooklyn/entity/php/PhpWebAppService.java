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
package org.apache.brooklyn.entity.php;

import java.util.List;
import org.apache.brooklyn.config.ConfigKey;
import org.apache.brooklyn.core.config.BasicConfigKey;
import org.apache.brooklyn.core.config.ConfigKeys;
import org.apache.brooklyn.core.config.MapConfigKey;
import org.apache.brooklyn.entity.webapp.WebAppService;
import org.apache.brooklyn.util.core.flags.SetFromFlag;


public interface PhpWebAppService extends WebAppService {

    @SetFromFlag("tarball.url")
    public static final ConfigKey<String> TARBALL_URL = new BasicConfigKey<String>(
            String.class, "php.tarball.url ", "The path where the deploment artifact (tarball) is stored (supporting file: and classpath: prefixes)");

    @SetFromFlag("git.url")
    public static final ConfigKey<String> GIT_URL = new BasicConfigKey<String>(
            String.class, "php.git.url ", "The Git repository where the application source code is stored (gitRepo)");

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SetFromFlag("app.name")
    public static final ConfigKey<String> APP_NAME = new BasicConfigKey(
            String.class, "php.app.name", "The name of the PHP application");

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SetFromFlag("main.file")
    public static final ConfigKey<List<String>> APP_START_FILE = new BasicConfigKey(
            List.class, "php.app.start.file", "PHP application file to start e.g. main.php, or launch.php");

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SetFromFlag("config.file.template")
    public static final ConfigKey<String> CONFIG_FILE_TEMPLATE = new BasicConfigKey(
            String.class, "php.config.template", "The name of the PHP application");

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SetFromFlag("config.file")
    public static final ConfigKey<String> CONFIG_FILE = new BasicConfigKey(
            String.class, "php.db.connection.file.config", "The name of the PHP application");
    
    @SetFromFlag("config.params")
    public static final MapConfigKey<String> PHP_CONFIG_PARAMS = new MapConfigKey<String>(String.class,
            "php.config.params", "Configuration parameters to be replaced on {CONFIG_FILE}.");

    @SetFromFlag("php.version")
    public static final ConfigKey<String> SUGGESTED_PHP_VERSION = ConfigKeys.newStringConfigKey(
            "php.version", "PHP version used", "5.5.9");


}
