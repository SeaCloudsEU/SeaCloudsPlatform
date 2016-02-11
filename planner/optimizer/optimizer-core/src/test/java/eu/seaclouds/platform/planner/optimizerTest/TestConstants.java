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


package eu.seaclouds.platform.planner.optimizerTest;

public abstract class TestConstants {

   public static final boolean EnabledTest = true;
   
   public static final String APP_MODEL_FILENAME    = "./src/test/resources/aam_atos_case_study22Jan16.yml";

   
   public static final String CLOUD_OFFER_FILENAME_IN_DIRECTMAP  = "./src/test/resources/MMoutputV-15-09.yml";

   public static final String OUTPUT_FILENAME       = "./src/test/target/outputNewTOSCAwithPolicies";
   public static final String OPEN_SQUARE_BRACKET   = "[";
   public static final String CLOSE_SQUARE_BRACKET  = "]";
   public static final double MAX_MILLIS_EXECUTING  = 20000;

   public static final int    NUM_PLANS_TO_GENERATE = 5;

   public static final String CLOUD_OFFER_FILENAME_IN_JSON = "./src/test/resources/mmOutputExample20Oct.json";
   
   
   public static final String APP_MODEL_FILENAME_SINGLE_MODULE    = "./src/test/resources/aam_singleModule11Feb16.yml";
   public static final String CLOUD_OFFER_FILENAME_IN_JSON_SINGLE_OFFER = "./src/test/resources/mmOutputExample20OctSingleOffer.json";
   
}
