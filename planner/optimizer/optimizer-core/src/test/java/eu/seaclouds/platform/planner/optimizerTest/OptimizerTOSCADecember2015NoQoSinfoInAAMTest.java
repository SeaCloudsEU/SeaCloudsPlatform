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

public class OptimizerTOSCADecember2015NoQoSinfoInAAMTest extends AbstractTest {

   private static final String TEST_CHARACTERISTIC            = " AAM WITHOUT QoSInfo ";
   private static final String OUTPUT_FILENAME_CHARACTERISTIC = "NoQoSinfo";

   @BeforeClass
   public void createObjects() {

      log = LoggerFactory.getLogger(OptimizerTOSCADecember2015NoQoSinfoInAAMTest.class);

      log.info("Starting TEST optimizer for the TOSCA syntax of September 2015 Of an " + TEST_CHARACTERISTIC);
      openInputFiles(TestConstants.APP_MODEL_FILENAME_NO_QOSINFO_NO_AUTOSCALE,
            TestConstants.CLOUD_OFFER_FILENAME_IN_JSON);
   }

   @Test(enabled = true)
   public void testNoQoSinTemplateBlind() {

      log.info("=== TEST for SOLUTION GENERATION of BLIND optimizer " + TEST_CHARACTERISTIC
            + " - STARTED (syntax December 2015)===");

      optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE, SearchMethodName.BLINDSEARCH);

      executeAndSave(appModel, suitableCloudOffer, SearchMethodName.BLINDSEARCH);

      log.info("=== TEST for SOLUTION GENERATION  of BLIND optimizer " + TEST_CHARACTERISTIC
            + "   - FINISEHD ===");

   }

   @Test(enabled = true)
   public void testNoQoSinTemplateHillClimb() {

      log.info("=== TEST for SOLUTION GENERATION of HILLCLIMB optimizer" + TEST_CHARACTERISTIC + "   - STARTED ===");

      optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE, SearchMethodName.HILLCLIMB);

      executeAndSave(appModel, suitableCloudOffer, SearchMethodName.HILLCLIMB);

      log.info("=== TEST for SOLUTION GENERATION of HILLCLIMB optimizer " + TEST_CHARACTERISTIC
            + "   FINISEHD ===");

   }

   @Test(enabled = true)
   public void testNoQoSinTemplateAnneal() {

      log.info("=== TEST for SOLUTION GENERATION of ANNEAL optimizer" + TEST_CHARACTERISTIC + "   - STARTED ===");

      optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE, SearchMethodName.ANNEAL);

      executeAndSave(appModel, suitableCloudOffer, SearchMethodName.ANNEAL);

      log.info("=== TEST for SOLUTION GENERATION  of ANNEAL optimizer " + TEST_CHARACTERISTIC
            + "   - FINISEHD ===");

   }

   public void executeAndSave(String appModel2, String suitableCloudOffer2, SearchMethodName methodName) {
      String[] arrayAdp = optimizer.optimize(appModel, suitableCloudOffer, null);
      checkNonExistenceOfAutoscalingPolicies(arrayAdp);
      for (int adpnum = 0; adpnum < arrayAdp.length; adpnum++) {

         saveFile(TestConstants.OUTPUT_FILENAME + methodName + OUTPUT_FILENAME_CHARACTERISTIC + adpnum + ".yaml",
               arrayAdp[adpnum]);
      }

   }

   private void checkNonExistenceOfAutoscalingPolicies(String[] arrayAdp) {

      for (int i = 0; i < arrayAdp.length; i++) {
         Map<String, Object> adpGroupsMap = YAMLoptimizerParser.getGroupMapFromAppMap(YAMLoptimizerParser.getMAPofAPP(arrayAdp[i]));
              

         Assert.assertFalse("ERROR: some module had autoscaling policy when it shouldn't. ADP" + i + " with content: " + arrayAdp[i],
               someGroupHasAutoscaling(adpGroupsMap));
         ;
      }

   }

   @SuppressWarnings("unchecked")
   private boolean someGroupHasAutoscaling(Map<String, Object> adpGroupsMap) {
      
      for(Map.Entry<String,Object> group : adpGroupsMap.entrySet()){
         //check if a group had austoscaling
         if(groupHasAutoscaling((Map<String,Object>)group.getValue())){return true;}
      }
      
      return false;
   }

   private boolean groupHasAutoscaling(Map<String, Object> groupInfo) {
     
      @SuppressWarnings("unchecked")
      List<Map<String,Object>> policies = (List<Map<String,Object>>) groupInfo.get(TOSCAkeywords.GROUP_ELEMENT_POLICY_TAG);
      for(Map<String,Object> policy : policies){
         //check if some of the policies was autoscaling
         if(policy.containsKey(TOSCAkeywords.AUTOSCALING_TAG)){
            return true;
         }
      }
      return false;
   }

   
  
   @AfterClass
   public void testFinishced() {
      log.info("===== ALL TESTS FOR OPTIMIZER FOR " + TEST_CHARACTERISTIC + " USING DECEMBER 2015 TOSCA FINISHED ===");
   }

}
