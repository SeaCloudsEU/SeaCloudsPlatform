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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import eu.seaclouds.platform.planner.optimizer.util.TOSCAkeywords;
import eu.seaclouds.platform.planner.optimizer.util.YAMLmatchmakerToOptimizerParser;

public class MMoutputTest {

   private static final String OUTPUT_MM_FILENAME = "./src/test/java/eu/seaclouds/platform/planner/optimizerTest/resources/MMoutputV-15-09.yml";

   private static String appModel;

   static Logger log;

   @BeforeClass
   public void createObjects() {

      System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, TOSCAkeywords.LOG_LEVEL);
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

}
