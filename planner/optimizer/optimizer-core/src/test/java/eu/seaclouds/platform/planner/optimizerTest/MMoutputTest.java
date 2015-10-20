/**
 * Copyright 2015 SeaClouds
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import eu.seaclouds.platform.planner.optimizer.util.YAMLmatchmakerToOptimizerParser;

public class MMoutputTest {

   private static final String OUTPUT_MM_FILENAME = "./src/test/resources/MMoutputV-15-09.yml";
   private static final String outputMMexample = "tosca_definitions_version: tosca_simple_yaml_1_0_0_wd03\ndescription: \ntemplate_name: \ntemplate_version: 1.0.0-SNAPSHOT\ntemplate_author: \n\nimports:\n  - tosca-normative-types:1.0.0.wd03-SNAPSHOT\n\ntopology_template:\n  node_templates:\n    BareMetalCloud_1gb_2_8_ghz_irwindale_36gb_scsi_15000rpm_miami_fl_usa:\n      type: seaclouds.Nodes.Compute.BareMetalCloud_1gb_2_8_ghz_irwindale_36gb_scsi_15000rpm_miami_fl_usa\n      properties:\n        resource_type: compute\n        hardwareId: \"1gb-2.8_ghz_irwindale-36gb_scsi_15000rpm\"\n        location: \"baremetalcloud:compute\"\n        region: \"miami-fl-usa\"\n        availability: 0.99999\n        country: United States\n        city: MIAMI\n        cost: 0.08 USD/hour\n        disk_size: 36\n        num_disks: 1\n        num_cpus: 1\n        ram: 1\n        disk_type: scsi";

   private static String appModel;

   static Logger log;

   @BeforeClass
   public void createObjects() {

      log = LoggerFactory.getLogger(MMoutputTest.class);

      log.info("Starting TEST optimizer for the TOSCA syntax of September 2015 regarding MATCHMAKER output");

      final String dir = System.getProperty("user.dir");
      log.debug("Trying to open files: current executino dir = " + dir);

      try {
         appModel = filenameToString(OUTPUT_MM_FILENAME);
      } catch (IOException e) {
         log.error("Output MM File for not found");
         e.printStackTrace();
      }

   }

   private static String filenameToString(String path) throws IOException {
      byte[] encoded = Files.readAllBytes(Paths.get(path));
      return new String(encoded, StandardCharsets.UTF_8);
   }

   @Test(enabled = true)
   public void testLoadMMoutput() {

      log.info("=== TEST for load info from MM===");

      List<Object> mapOffers = YAMLmatchmakerToOptimizerParser.getListofOptions(appModel);
      Assert.assertNotNull(mapOffers);
      log.info("=== List created===");
      String againToString = YAMLmatchmakerToOptimizerParser.fromListtoYAMLstring(mapOffers);
      Assert.assertNotNull(againToString);

      Assert.assertTrue(mapOffers.size() > 0);

      log.info("=== TEST for load info from MM FINISHED===");

   }

   @Test(enabled = true)
   public void testLoadMMoutputTOSCAString() {
      log.info("=== TEST for load info from MM of the TOSCA String===");
      Yaml yamlApp = new Yaml();
      Map<String, Object> cloudInfo = (Map<String, Object>) yamlApp.load(outputMMexample);
      log.debug("Read the information from MM: " + yamlApp.dump(cloudInfo));
      Assert.assertNotNull(cloudInfo, "Read of the output as String gave a null");
   }
}
