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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import eu.seaclouds.platform.planner.optimizer.Optimizer;
import eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethodName;
import eu.seaclouds.platform.planner.optimizer.util.TOSCAkeywords;
import eu.seaclouds.platform.planner.optimizer.util.YAMLoptimizerParser;

public class OptimizerTOSCADecember2015ReplicatedHostTest extends AbstractTest {

   private static final String TEST_CHARACTERISTIC            = " REPLICATED HOST FOR MODULES ";
   private static final String OUTPUT_FILENAME_CHARACTERISTIC = "SameHostOfferForModules";

   @BeforeClass
   public void createObjects() {

      log = LoggerFactory.getLogger(OptimizerTOSCADecember2015ReplicatedHostTest.class);

      log.info("Starting TEST optimizer for the TOSCA syntax of September 2015 Of an " + TEST_CHARACTERISTIC);
      openInputFiles(TestConstants.APP_MODEL_FILENAME,
            TestConstants.CLOUD_OFFER_FILENAME_IN_JSON_ATOS_SAME_OFFER_FOR_ALL_MODULES);
   }

   @Test(enabled = true)
   public void testDisconnectedGraphBlind() {

      log.info("=== TEST for SOLUTION GENERATION of BLIND optimizer " + TEST_CHARACTERISTIC
            + " - STARTED (syntax December 2015)===");

      optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE, SearchMethodName.BLINDSEARCH);

      executeAndSave(appModel, suitableCloudOffer, SearchMethodName.BLINDSEARCH);

      log.info("=== TEST for SOLUTION GENERATION  of BLIND optimizer " + TEST_CHARACTERISTIC
            + "   - FINISEHD ===");

   }

   @Test(enabled = true)
   public void testDisconnectedGraphHillClimb() {

      log.info("=== TEST for SOLUTION GENERATION of HILLCLIMB optimizer" + TEST_CHARACTERISTIC + "   - STARTED ===");

      optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE, SearchMethodName.HILLCLIMB);

      executeAndSave(appModel, suitableCloudOffer, SearchMethodName.HILLCLIMB);

      log.info("=== TEST for SOLUTION GENERATION of HILLCLIMB optimizer " + TEST_CHARACTERISTIC
            + "   FINISEHD ===");

   }

   @Test(enabled = true)
   public void testDisconnectedGraphAnneal() {

      log.info("=== TEST for SOLUTION GENERATION of ANNEAL optimizer" + TEST_CHARACTERISTIC + "   - STARTED ===");

      optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE, SearchMethodName.ANNEAL);

      executeAndSave(appModel, suitableCloudOffer, SearchMethodName.ANNEAL);

      log.info("=== TEST for SOLUTION GENERATION  of ANNEAL optimizer " + TEST_CHARACTERISTIC
            + "   - FINISEHD ===");

   }

   public void executeAndSave(String appModel, String suitableCloudOffer, SearchMethodName methodName) {
      String[] arrayAdp = optimizer.optimize(appModel, suitableCloudOffer, null);
      checkDoubleNodeTemplatesInADPthanInAAM(arrayAdp, appModel);
      for (int adpnum = 0; adpnum < arrayAdp.length; adpnum++) {

         saveFile(TestConstants.OUTPUT_FILENAME + methodName + OUTPUT_FILENAME_CHARACTERISTIC + adpnum + ".yaml",
               arrayAdp[adpnum]);
      }

   }

   private void checkDoubleNodeTemplatesInADPthanInAAM(String[] arrayAdp, String appModel) {

      Map<String, Object> aamNodesMap = YAMLoptimizerParser
            .getModuleMapFromAppMap((Map<String, Object>) YAMLoptimizerParser.getMAPofAPP(appModel));
      int numberOfNodesAam= aamNodesMap.size();

      for (int i = 0; i < arrayAdp.length; i++) {
         Map<String, Object> adpNodesMap = YAMLoptimizerParser
               .getModuleMapFromAppMap((Map<String, Object>) YAMLoptimizerParser.getMAPofAPP(arrayAdp[i]));

         Assert.assertTrue("There is not a dedicated host in the ADP for each module. Solution number " + i + " with content: " + arrayAdp[i],
               (numberOfNodesAam*2)==(adpNodesMap.size()));
         
      }

   }

  
   @AfterClass
   public void testFinishced() {
      log.info("===== ALL TESTS FOR OPTIMIZER FOR " + TEST_CHARACTERISTIC + " USING DECEMBER 2015 TOSCA FINISHED ===");
   }

}
