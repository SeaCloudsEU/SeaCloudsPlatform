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
import eu.seaclouds.platform.planner.optimizer.util.YAMLmodulesOptimizerParser;
import eu.seaclouds.platform.planner.optimizer.util.YAMLoptimizerParser;
import eu.seaclouds.platform.planner.optimizer.util.YAMLtypesOptimizerParser;

public class OptimizerTOSCADecember2015AutoscalingPoliciesTest extends AbstractTest {

   @BeforeClass
   public void createObjects() {

      log = LoggerFactory.getLogger(OptimizerTOSCADecember2015AutoscalingPoliciesTest.class);

      log.info("Starting TEST optimizer for the TOSCA syntax of September 2015");
      openInputFiles();
   }

   @Test(enabled = true)
   public void testPresenceAutoscalingPoliciesInSolutionBlind() {

      log.info("=== TEST for SOLUTION GENERATION of BLIND optimizer STARTED (syntax December 2015)===");

      optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE, SearchMethodName.BLINDSEARCH);

      String[] arrayDam = optimizer.optimize(appModel, suitableCloudOffer);
      for (int damnum = 0; damnum < arrayDam.length; damnum++) {

         try {
            checkAutoscalingPolicies(arrayDam[damnum]);
         } catch (Exception e) {
            log.error("There was an error in the check of policies. Solution was: " + arrayDam[damnum]);
            throw e;
         }
         saveFile(TestConstants.OUTPUT_FILENAME + SearchMethodName.BLINDSEARCH + damnum + ".yaml", arrayDam[damnum]);
      }

      log.info("=== TEST for SOLUTION GENERATION with POLICIES of BLIND optimizer FINISEHD ===");

   }

   @Test(enabled = true)
   public void testPresenceAutoscalingPoliciesInSolutionHillClimb() {

      log.info("=== TEST for SOLUTION GENERATION of HILLCLIMB optimizer STARTED ===");

      optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE, SearchMethodName.HILLCLIMB);

      String[] arrayDam = optimizer.optimize(appModel, suitableCloudOffer);
      for (int damnum = 0; damnum < arrayDam.length; damnum++) {

         try {
            checkAutoscalingPolicies(arrayDam[damnum]);
         } catch (Exception e) {
            log.error("There was an error in the check of policies. Solution was: " + arrayDam[damnum]);
            throw e;
         }
         saveFile(TestConstants.OUTPUT_FILENAME + SearchMethodName.HILLCLIMB + damnum + ".yaml", arrayDam[damnum]);

      }

      log.info("=== TEST for SOLUTION GENERATION with POLICIES of HILLCLIMB optimizer FINISEHD ===");

   }

   @Test(enabled = true)
   public void testPresenceAutoscalingPoliciesInSolutionAnneal() {

      log.info("=== TEST for SOLUTION GENERATION of ANNEAL optimizer STARTED ===");

      optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE, SearchMethodName.ANNEAL);

      String[] arrayDam = optimizer.optimize(appModel, suitableCloudOffer);
      for (int damnum = 0; damnum < arrayDam.length; damnum++) {

         try {
            checkAutoscalingPolicies(arrayDam[damnum]);
         } catch (Exception e) {
            log.error("There was an error in the check of policies. Solution was: " + arrayDam[damnum]);
            throw e;
         }
         saveFile(TestConstants.OUTPUT_FILENAME + SearchMethodName.ANNEAL + damnum + ".yaml", arrayDam[damnum]);

      }

      log.info("=== TEST for SOLUTION GENERATION with POLICIES of ANNEAL optimizer FINISEHD ===");

   }

   private void checkAutoscalingPolicies(String dam) {

      Assert.assertFalse("Dam was not created, optimize method returns null", dam == null);
      String damLines[] = dam.split(System.getProperty("line.separator"));

      Assert.assertTrue("Dam was not created", damLines.length > 1);

      Map<String, Object> appMap = YAMLoptimizerParser.getMAPofAPP(dam);
      Map<String, Object> nodesMap = YAMLoptimizerParser.getModuleMapFromAppMap(appMap);
      Map<String, Object> typesMap = YAMLoptimizerParser.getTypesMapFromAppMap(appMap);
      Map<String, Object> groupsMap = YAMLoptimizerParser.getGroupMapFromAppMap(appMap);

      for (Map.Entry<String, Object> entry : nodesMap.entrySet()) {
         if (canScale(entry) && (requirementsSatisfied(appMap, groupsMap))) {
            Map<String, Object> autoscalingPolicy = getAutoScalingPolicy(
                  YAMLgroupsOptimizerParser.findGroupOfMemberName(entry.getKey(), groupsMap));
            if (autoscalingPolicy != null) {
               Assert.assertTrue(autoscalingPolicy.containsKey(TOSCAkeywords.AUTOSCALE_POOL_MAXIMUM_SIZE));
               Assert.assertTrue(autoscalingPolicy.containsKey(TOSCAkeywords.AUTOSCALE_METRIC));
               Assert.assertTrue(autoscalingPolicy.containsKey(TOSCAkeywords.AUTOSCALE_METRIC_LOWERBOUND));
               Assert.assertTrue(autoscalingPolicy.containsKey(TOSCAkeywords.AUTOSCALE_METRIC_UPPERBOUND));
               Assert.assertTrue(autoscalingPolicy.containsKey(TOSCAkeywords.AUTOSCALE_POOL_MINIMUM_SIZE));

               String typeOfNode = YAMLmodulesOptimizerParser.getModuleTypeFromModulesMap(entry.getKey(), nodesMap);
               Assert.assertNotNull("Type of node " + entry.getKey() + " was NULL", typeOfNode);
               
            }
         }
      }

   }

   private boolean requirementsSatisfied(Map<String, Object> appMap, Map<String, Object> groupsMap) {
      try {
         Map<String, Object> qualitySol = YAMLgroupsOptimizerParser.getPolicySubInfoFromGroupInfo(
               YAMLgroupsOptimizerParser.findGroupOfMemberName(YAMLoptimizerParser.getInitialElementName(appMap),
                     groupsMap),
               TOSCAkeywords.EXPECTED_QUALITY_PROPERTIES);
         return (boolean) (((Double) qualitySol.get(TOSCAkeywords.OVERALL_QOS_FITNESS)) > 1.0);
      } catch (Exception E) {
         // Something among the many pieces of information for specifying the
         // expected QoS was not present
         // So the it was not specified that the requirements were satisfied.
         log.info("Checking if requirements were satisfied we have received the exception: " + E.getClass());
         return false;
      }
   }

   private Map<String, Object> getAutoScalingPolicy(Map<String, Object> groupMap) {
      return YAMLgroupsOptimizerParser.getPolicySubInfoFromGroupInfo(groupMap, TOSCAkeywords.AUTOSCALING_TAG);
   }

   @SuppressWarnings("unchecked")
   private boolean canScale(Map.Entry<String, Object> module) {
      Map<String, Object> moduleInfo = (Map<String, Object>) module.getValue();
      try {
         return (boolean) ((Map<String, Object>) moduleInfo.get(TOSCAkeywords.MODULE_PROPERTIES_TAG))
               .get(TOSCAkeywords.MODULE_AUTOSCALE_PROPERTY);

      } catch (Exception E) {
         log.info("It was not found autoscale definition for module " + module.getKey());
         return false;
      }
   }

   @AfterClass
   public void testFinishced() {
      log.info("===== ALL TESTS FOR OPTIMIZER USING DECEMBER 2015 TOSCA FINISHED ===");
   }

}
