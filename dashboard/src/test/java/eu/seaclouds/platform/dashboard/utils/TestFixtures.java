/*
 *  Copyright 2014 SeaClouds
 *  Contact: SeaClouds
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package eu.seaclouds.platform.dashboard.utils;

public interface TestFixtures {

    // Deployer fixtures
    String TOSCA_DAM_PATH = "fixtures/tosca-dam.yml";
    String APPLICATION_PATH = "fixtures/application.json";
    String TASK_SUMMARY_DEPLOY_PATH = "fixtures/task-summary-deploy.json";
    String TASK_SUMMARY_DELETE_PATH = "fixtures/task-summary-delete.json";
    String ENTITIES_PATH = "fixtures/entities.json";
    String SENSORS_SUMMARIES_PATH = "fixtures/sensors.json";


    // SLA fixtures
    String AGREEMENT_PATH_JSON = "fixtures/agreement.json";
    String TEMPLATE_PATH_JSON = "fixtures/agreement-template.json";
    String AGREEMENT_STATUS_PATH_JSON = "fixtures/agreement-status.json";
    String VIOLATIONS_JSON_PATH = "fixtures/violations.json";

    // Monitor fixtures
    String MONITORING_RULES_PATH = "fixtures/monitoring-rules.xml";

    // Planner fixtures
    String ADPS_PATH = "fixtures/adps.json";
    String ADP_PATH = "fixtures/adp.yml";
    String AAM_PATH = "fixtures/aam.yml";
    String DESIGNER_TOPOLOGY = "fixtures/designer-topology.json";
}
