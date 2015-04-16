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

package eu.seaclouds.platform.planner.optimizer.util;

public class TOSCAkeywords {

   public static final String NODE_TEMPLATE                       = "node_templates";
   public static final String SUITABLE_SERVICES                   = "suitableServices";
   public static final String MODULE_REQUIREMENTS                 = "requirements";
   public static final String MODULE_REQUIREMENTS_CONSTRAINTS     = "constraints";
   public static final String MODULE_REQUIREMENTS_HOST            = "host";
   public static final String CLOUD_OFFER_PROPERTIES_TAG          = "properties";
   public static final String MODULE_REQUIREMENTS_INSTANCES       = "instances";

   // These ones have not been defined yet. I temporarily use this definition
   public static final String CLOUD_OFFER_PROPERTY_AVAILABILITY   = "availabilityPOC";
   public static final String CLOUD_OFFER_PROPERTY_PERFORMANCE    = "performancePOC";
   public static final String CLOUD_OFFER_PROPERTY_COST           = "costPOC";
   public static final String LATENCY_INTER_DATACENTER_MILLIS     = "latencyExternalPOC";
   public static final String LATENCY_INTRA_DATACENTER_MILLIS     = "latencyInternalPOC";

   public static final String RECONFIGURATION_WORKLOAD_TAG        = "ReconfigurationsPOC";
   public static final String EXPECTED_QUALITY_PROPERTIES         = "ExpectedQualityPOC";
   public static final String EXPECTED_QOS_PERFORMANCE_MILLIS     = "expectedExecutionTimePOC";
   public static final String EXPECTED_QOS_AVAILABILITY           = "expectedAvailabilityPOC";
   public static final String EXPECTED_QOS_COST_HOUR              = "expectedCostPOC";
   public static final String OVERALL_QOS_FITNESS                 = "fitness";

   public static final String APP_QOS_REQUIREMENTS                = "QoSrequirementsPOC";
   public static final String APP_PERFORMANCE_REQUIREMENTS        = "responseTimePOC";
   public static final String APP_AVAILABILITY_REQUIREMENTS       = "availabilityPOC";
   public static final String APP_COST_REQUIREMENTS_MONTH         = "costPOC";
   public static final String APP_EXPECTED_WORKLOAD_MINUTE        = "workloadPOC";

   public static final String MODULE_QOS_PROPERTIES               = "QoSpropertiesPOC";
   public static final String MODULE_QOS_PERFORMANCE_MILLIS       = "executionTimePOC";
   public static final String MODULE_QOS_PERFORMANCE_LOCATION     = "executionTimeMeasuredInPOC";
   public static final String MODULE_QOS_OPERATIONAL_PROFILE      = "OpProfilePOC";

   public static final String CLOUD_OFFER_PROVIDER_NAME_SEPARATOR = "\\.";

}
