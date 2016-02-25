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

import org.junit.Assert;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import eu.seaclouds.platform.planner.optimizer.Optimizer;
import eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethodName;
import eu.seaclouds.platform.planner.optimizer.util.TOSCAkeywords;
import eu.seaclouds.platform.planner.optimizer.util.YAMLoptimizerParser;

public class OptimizerTOSCADecember2015UniqueSolutionsTest extends AbstractTest {

   @BeforeClass
   public void createObjects() {

      log = LoggerFactory.getLogger(OptimizerTOSCADecember2015UniqueSolutionsTest.class);

      log.info("Starting TEST optimizer for the TOSCA syntax of September 2015");
      openInputFiles(TestConstants.APP_MODEL_FILENAME_SINGLE_MODULE,
            TestConstants.CLOUD_OFFER_FILENAME_IN_JSON_SINGLE_OFFER);
   }

   @Test(enabled = true)
   public void testExecutePassingUniqueOptionToBlind() {

      log.info("=== TEST for SOLUTION GENERATION of BLIND optimizer SINGLE ADP - STARTED (syntax December 2015)===");

      optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE, SearchMethodName.BLINDSEARCH);

      executeAndSave(appModel, suitableCloudOffer, SearchMethodName.BLINDSEARCH);

      log.info("=== TEST for SOLUTION GENERATION with POLICIES of BLIND optimizer SINGLE ADP - FINISEHD ===");

   }

   @Test(enabled = true)
   public void testExecutePassingUniqueOptionToHillClimb() {

      log.info("=== TEST for SOLUTION GENERATION of HILLCLIMB optimizer SINGLE ADP - STARTED ===");

      optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE, SearchMethodName.HILLCLIMB);

      executeAndSave(appModel, suitableCloudOffer, SearchMethodName.HILLCLIMB);

      log.info("=== TEST for SOLUTION GENERATION with POLICIES of HILLCLIMB optimizer SINGLE ADP FINISEHD ===");

   }

   @Test(enabled = true)
   public void testExecutePassingUniqueOptionToAnneal() {

      log.info("=== TEST for SOLUTION GENERATION of ANNEAL optimizer SINGLE ADP - STARTED ===");

      optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE, SearchMethodName.ANNEAL);

      executeAndSave(appModel, suitableCloudOffer, SearchMethodName.ANNEAL);

      log.info("=== TEST for SOLUTION GENERATION with POLICIES of ANNEAL optimizer SINGLE ADP - FINISEHD ===");

   }

   public void executeAndSave(String appModel2, String suitableCloudOffer2, SearchMethodName methodName) {
      String[] arrayAdp = optimizer.optimize(appModel, suitableCloudOffer, null);
      checkNotDuplicatedAdps(arrayAdp);
      for (int adpnum = 0; adpnum < arrayAdp.length; adpnum++) {

         saveFile(TestConstants.OUTPUT_FILENAME + methodName + "NOduplicates" + adpnum + ".yaml",
               arrayAdp[adpnum]);
      }

   }

   private void checkNotDuplicatedAdps(String[] arrayAdp) {
      // load apds in maps
      Map<String, Object>[] appMap = (Map<String, Object>[]) new Map[arrayAdp.length];
      Map<String, Object>[] nodesMap = (Map<String, Object>[]) new Map[arrayAdp.length];

      for (int i = 0; i < arrayAdp.length; i++) {
         appMap[i] = YAMLoptimizerParser.getMAPofAPP(arrayAdp[i]);
         nodesMap[i] = YAMLoptimizerParser.getModuleMapFromAppMap(appMap[i]);
      }

      Map<String, Object> currentMap;
      for (int i = 0; i < nodesMap.length; i++) {
         currentMap = nodesMap[i];
         nodesMap[i] = null;
         Assert.assertFalse(
               "Map with info " + YAMLoptimizerParser.fromMAPtoYAMLstring(currentMap)
                     + "  was found duplicated in position " + i + " and another superior ",
               arrayContainsMapInfo(currentMap, nodesMap));

         nodesMap[i] = currentMap;
      }

   }

   private boolean arrayContainsMapInfo(Map<String, Object> currentMap, Map<String, Object>[] nodesMap) {

      for (int i = 0; i < nodesMap.length; i++) {
         if (isEqual(currentMap, nodesMap[i])) {
            return true;
         }
      }
      return false;

   }

   private boolean isEqual(Map<String, Object> current, Map<String, Object> other) {
      if (other == null) {
         return false;
      }

      if (current == null) {
         return false;
      }

      if (other == current) {
         return true;
      }

      if (current.size() != other.size()) {
         return false;
      }
      for (Map.Entry<String, Object> entry : current.entrySet()) {
         try {
            String modname = entry.getKey();
            if (!(other.containsKey(modname) && equalHostAndNumberInstances((Map<String, Object>) entry.getValue(),
                  (Map<String, Object>) other.get(modname)))) {
               return false;
            }

         } catch (Exception E) {
            // some comparison went wrong, but modname existed (given by the
            // order of the AND)
            // Solutions are not equal (one should be consistent and the other
            // should not).
            return false;
         }
      }

      return true;
   }

   private boolean equalHostAndNumberInstances(Map<String, Object> currentInfoMod, Map<String, Object> otherInfoMod) {
      int numInstancesCurrent = 0;
      String hostNameCurrent = "";

      for (Map<String, Object> requirement : ((List<Map<String, Object>>) currentInfoMod
            .get(TOSCAkeywords.MODULE_REQUIREMENTS))) {
         try {
            if (requirement.containsKey(TOSCAkeywords.MODULE_REQUIREMENTS_HOST)) {
               hostNameCurrent = (String) requirement.get(TOSCAkeywords.MODULE_REQUIREMENTS_HOST);
               numInstancesCurrent = (int) requirement.get(TOSCAkeywords.MODULE_PROPOSED_INSTANCES);
            }

         } catch (Exception E) {// Nothing to do
            // it was not a module with host or number of instances
         }
      }

      int numInstancesOther = 0;
      String hostNameOther = "";
      for (Map<String, Object> requirement : ((List<Map<String, Object>>) otherInfoMod
            .get(TOSCAkeywords.MODULE_REQUIREMENTS))) {
         try {
            if (requirement.containsKey(TOSCAkeywords.MODULE_REQUIREMENTS_HOST)) {
               hostNameOther = (String) requirement.get(TOSCAkeywords.MODULE_REQUIREMENTS_HOST);
               numInstancesOther = (int) requirement.get(TOSCAkeywords.MODULE_PROPOSED_INSTANCES);
            }

         } catch (Exception E) {// Nothing to do
            // it was not a module with host or number of instances
         }
      }

      // if hostnames are not equal, return false
      if ((hostNameCurrent == null) || (!hostNameCurrent.equals(hostNameOther))) {
         return false;
      }

      // host names equal. Return whether the number of instances is also equal.
      return (numInstancesCurrent == numInstancesOther);

   }

   @AfterClass
   public void testFinishced() {
      log.info("===== ALL TESTS FOR OPTIMIZER USING DECEMBER 2015 TOSCA FINISHED ===");
   }

}
