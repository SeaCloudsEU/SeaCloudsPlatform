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
import eu.seaclouds.platform.planner.optimizer.util.YAMLgroupsOptimizerParser;
import eu.seaclouds.platform.planner.optimizer.util.YAMLoptimizerParser;

@Test(enabled = false)
public class OptimizerTOSCASeptember2015Test extends AbstractTest {

   @BeforeClass
   public void createObjects() {

      log = LoggerFactory.getLogger(OptimizerTOSCASeptember2015Test.class);

      log.info("Starting TEST optimizer for the TOSCA syntax of September 2015");

      openInputFiles();

   }

   public void testPresenceSolutionBlind() {

      log.info("=== TEST for SOLUTION GENERATION of BLIND optimizer STARTED (syntax September 2015)===");

      optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE, SearchMethodName.BLINDSEARCH);

      String[] arrayDam = optimizer.optimize(appModel, suitableCloudOffer);
      for (int damnum = 0; damnum < arrayDam.length; damnum++) {

         try {
            checkCorrectness(arrayDam[damnum]);
         } catch (Exception e) {
            log.error("There was an error in the check of correctness. Solution was: " + arrayDam[damnum]);
            throw e;
         }
         saveFile(TestConstants.OUTPUT_FILENAME + SearchMethodName.BLINDSEARCH + damnum + ".yaml", arrayDam[damnum]);
      }

      log.info("=== TEST for SOLUTION GENERATION of BLIND optimizer FINISEHD ===");

   }

   public void testPresenceSolutionHillClimb() {

      log.info("=== TEST for SOLUTION GENERATION of HILLCLIMB optimizer STARTED ===");

      optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE, SearchMethodName.HILLCLIMB);

      String[] arrayDam = optimizer.optimize(appModel, suitableCloudOffer);
      for (int damnum = 0; damnum < arrayDam.length; damnum++) {

         try {
            checkCorrectness(arrayDam[damnum]);
         } catch (Exception e) {
            log.error("There was an error in the check of correctness. Solution was: " + arrayDam[damnum]);
            throw e;
         }
         saveFile(TestConstants.OUTPUT_FILENAME + SearchMethodName.HILLCLIMB + damnum + ".yaml", arrayDam[damnum]);

      }

      log.info("=== TEST for SOLUTION GENERATION of HILLCLIMB optimizer FINISEHD ===");

   }

   public void testPresenceSolutionAnneal() {

      log.info("=== TEST for SOLUTION GENERATION of ANNEAL optimizer STARTED ===");

      optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE, SearchMethodName.ANNEAL);

      String[] arrayDam = optimizer.optimize(appModel, suitableCloudOffer);
      for (int damnum = 0; damnum < arrayDam.length; damnum++) {

         try {
            checkCorrectness(arrayDam[damnum]);
         } catch (Exception e) {
            log.error("There was an error in the check of correctness. Solution was: " + arrayDam[damnum]);
            throw e;
         }
         saveFile(TestConstants.OUTPUT_FILENAME + SearchMethodName.ANNEAL + damnum + ".yaml", arrayDam[damnum]);

      }

      log.info("=== TEST for SOLUTION GENERATION of ANNEAL optimizer FINISEHD ===");

   }

   private void checkCorrectness(String dam) {

      Assert.assertFalse("Dam was not created, optimize method returns null", dam == null);
      String damLines[] = dam.split(System.getProperty("line.separator"));

      Assert.assertTrue("Dam was not created", damLines.length > 1);

      int numServices = 0;
      int numSuitableServicesFound = 0;

      for (String line : damLines) {
         if ((line != null) && (line.contains(TOSCAkeywords.SUITABLE_SERVICES))) {

            numServices++;
            String suitableServicesLine[] = line.split(TestConstants.OPEN_SQUARE_BRACKET);

            for (String suitableLine : suitableServicesLine) {
               if ((suitableLine != null) && suitableLine.contains(TestConstants.CLOSE_SQUARE_BRACKET)) {
                  String suitableService = suitableLine.substring(0,
                        suitableLine.indexOf(TestConstants.CLOSE_SQUARE_BRACKET));
                  Assert.assertTrue("Suitable service is the empty string", suitableService != "");
                  Assert.assertTrue("Suitable service chosen does not belong to the cloud offer",
                        suitableCloudOffer.contains(suitableService));
                  numSuitableServicesFound++;
               }
            }

         }
      }
      Assert.assertEquals("Optimizer did not find any of the services", numServices, numSuitableServicesFound);

      checkPoliciesIfAndOnlyCanScale(dam);

   }

   private void checkPoliciesIfAndOnlyCanScale(String dam) {

      Map<String, Object> appMap = YAMLoptimizerParser.getMAPofAPP(dam);
      Map<String, Object> nodesMap = YAMLoptimizerParser.getModuleMapFromAppMap(appMap);
      Map<String, Object> groupsMap = YAMLoptimizerParser.getGroupMapFromAppMap(appMap);

      for (Map.Entry<String, Object> entry : nodesMap.entrySet()) {
         if (canScale((Map<String, Object>) entry.getValue()) && (requirementsSatisfied(appMap, groupsMap))) {
            Assert.assertNotNull(
                  getAutoScalingPolicy(YAMLgroupsOptimizerParser.findGroupOfMemberName(entry.getKey(), groupsMap)));
         }
      }

   }

   private boolean requirementsSatisfied(Map<String, Object> appMap, Map<String, Object> groupsMap) {
      try {
         Map<String, Object> qualitySol = YAMLgroupsOptimizerParser.getPolicySubInfoFromGroupInfo(
               YAMLgroupsOptimizerParser.findGroupOfMemberName(YAMLoptimizerParser.getInitialElementName(appMap),
                     groupsMap),
               TOSCAkeywords.EXPECTED_QUALITY_PROPERTIES);
         return (boolean) qualitySol.get(TOSCAkeywords.OVERALL_QOS_FITNESS);
      } catch (Exception E) {
         // Something among the many pieces of information for specifying the
         // expected QoS was not present
         // So the it was not specified that the requirements were satisfied.
         return false;
      }
   }

   private Object getAutoScalingPolicy(Map<String, Object> groupMap) {
      return YAMLgroupsOptimizerParser.getPolicySubInfoFromGroupInfo(groupMap, TOSCAkeywords.AUTOSCALING_TAG);
   }

   @SuppressWarnings("unchecked")
   private boolean canScale(Map<String, Object> node) {
      try {
         return (boolean) ((Map<String, Object>) node.get(TOSCAkeywords.MODULE_PROPERTIES_TAG))
               .get(TOSCAkeywords.MODULE_AUTOSCALE_PROPERTY);

      } catch (Exception E) {

         return false;
      }
   }

   @AfterClass
   public void testFinishced() {
      log.info("===== ALL TESTS FOR OPTIMIZER USING SEPTEMBER (last update in DECEMBER) 2015 TOSCA FINISHED ===");
   }

}
