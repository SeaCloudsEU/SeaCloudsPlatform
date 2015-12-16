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

import java.util.Map;

import org.junit.Assert;

import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import eu.seaclouds.platform.planner.optimizer.Optimizer;
import eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethodName;
import eu.seaclouds.platform.planner.optimizer.util.TOSCAkeywords;
import eu.seaclouds.platform.planner.optimizer.util.YAMLoptimizerParser;

public class OptimizerComputeTypeTest extends AbstractTest {

   @BeforeClass
   public void createObjects() {

      log = LoggerFactory.getLogger(OptimizerComputeTypeTest.class);

      log.info("Starting TEST optimizer for the TOSCA syntax of September 2015");

      openInputFiles();

   }

   @Test(enabled = true)
   public void testPresenceOfComputeTypeInSolutionBlind() {

      log.info("=== TESTS FOR OPTIMIZER INCLUSION OF 'COMPUTE' TYPE IN TYPES STARTED===");

      optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE, SearchMethodName.BLINDSEARCH);

      String[] arrayDam = optimizer.optimize(appModel, suitableCloudOffer);
      for (int damnum = 0; damnum < arrayDam.length; damnum++) {

         try {
            checkPresenceComputeType(arrayDam[damnum]);
         } catch (Exception e) {
            log.error("There was an error in the check of seaclouds.nodes.Compute type in node_types. Solution was: "
                  + arrayDam[damnum]);
            throw e;
         }
         saveFile(TestConstants.OUTPUT_FILENAME + SearchMethodName.BLINDSEARCH + damnum + ".yaml", arrayDam[damnum]);
      }

      log.info("=== TEST for SOLUTION GENERATION with COMPUTE type of BLIND optimizer FINISEHD ===");

   }

   @Test(enabled = true)
   public void testPresenceOfComputeTypeInSolutionClimb() {

      log.info("=== TEST for SOLUTION GENERATION of HILLCLIMB optimizer STARTED ===");

      optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE, SearchMethodName.HILLCLIMB);

      String[] arrayDam = optimizer.optimize(appModel, suitableCloudOffer);
      for (int damnum = 0; damnum < arrayDam.length; damnum++) {

         try {
            checkPresenceComputeType(arrayDam[damnum]);
         } catch (Exception e) {
            log.error("There was an error in the check of seaclouds.nodes.Compute type in node_types. Solution was: "
                  + arrayDam[damnum]);
            throw e;
         }
         saveFile(TestConstants.OUTPUT_FILENAME + SearchMethodName.HILLCLIMB + damnum + ".yaml", arrayDam[damnum]);

      }

      log.info("=== TEST for SOLUTION GENERATION with COMPUTE type of HILLCLIMB optimizer FINISEHD ===");

   }

   @Test(enabled = true)
   public void testPresenceOfComputeTypeInSolutionAnneal() {

      log.info("=== TEST for SOLUTION GENERATION of ANNEAL optimizer STARTED ===");

      optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE, SearchMethodName.ANNEAL);

      String[] arrayDam = optimizer.optimize(appModel, suitableCloudOffer);
      for (int damnum = 0; damnum < arrayDam.length; damnum++) {

         try {
            checkPresenceComputeType(arrayDam[damnum]);
         } catch (Exception e) {
            log.error("There was an error in the check of seaclouds.nodes.Compute type in node_types. Solution was: "
                  + arrayDam[damnum]);
            throw e;
         }
         saveFile(TestConstants.OUTPUT_FILENAME + SearchMethodName.ANNEAL + damnum + ".yaml", arrayDam[damnum]);

      }

      log.info("=== TEST for SOLUTION GENERATION with COMPUTE type of ANNEAL optimizer FINISEHD ===");

   }

   private void checkPresenceComputeType(String dam) {

      Assert.assertFalse("Dam was not created, optimize method returns null", dam == null);
      String damLines[] = dam.split(System.getProperty("line.separator"));

      Assert.assertTrue("Dam was not created", damLines.length > 1);

      Map<String, Object> appMap = YAMLoptimizerParser.getMAPofAPP(dam);
      Map<String, Object> typesMap = YAMLoptimizerParser.getTypesMapFromAppMap(appMap);

      Assert.assertTrue(typesMap.containsKey(TOSCAkeywords.COMPUTE_TYPE));

   }

   @AfterClass
   public void testFinishced() {
      log.info("===== ALL TESTS FOR OPTIMIZER INCLUSION OF 'COMPUTE' TYPE IN TYPES FINISHED ===");
   }

}
