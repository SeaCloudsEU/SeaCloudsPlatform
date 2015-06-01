/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.seaclouds.platform.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to parse Java system properties to static keys.
 */
public class ConfigParameters {
    static Logger log = LoggerFactory.getLogger(ConfigParameters.class);

    // Planner connection
    public static String PLANNER_ENDPOINT;

    // Monitor connection
    public static String MONITOR_ENDPOINT;

    // Deployer connection
    public static String DEPLOYER_ENDPOINT;
    public static String DEPLOYER_USERNAME;
    public static String DEPLOYER_PASSWORD;

    // SLA connection
    public static String SLA_ENDPOINT;


    static {
        /*
            Loading config keys from Java system properties
         */
        PLANNER_ENDPOINT = System.getenv("PLANNER_ENDPOINT");
        MONITOR_ENDPOINT = System.getenv("MONITOR_ENDPOINT");
        SLA_ENDPOINT = System.getenv("SLA_ENDPOINT");
        DEPLOYER_ENDPOINT = System.getenv("DEPLOYER_ENDPOINT");
        DEPLOYER_USERNAME = System.getenv("DEPLOYER_USERNAME");
        DEPLOYER_PASSWORD = System.getenv("DEPLOYER_PASSWORD");

    }

    // SLA connection

}