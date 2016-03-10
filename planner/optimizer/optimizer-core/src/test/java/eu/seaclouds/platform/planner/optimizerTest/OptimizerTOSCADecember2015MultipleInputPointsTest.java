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

public class OptimizerTOSCADecember2015MultipleInputPointsTest extends AbstractTest {

   private static final String TEST_CHARACTERISTIC            = " APP MULTIPLE ENTRY POINTS ";
   private static final String OUTPUT_FILENAME_CHARACTERISTIC = "DisconnectedGraph";

   @BeforeClass
   public void createObjects() {

      log = LoggerFactory.getLogger(OptimizerTOSCADecember2015MultipleInputPointsTest.class);

      log.info("Starting TEST optimizer for the TOSCA syntax of September 2015 Of an " + TEST_CHARACTERISTIC);
      openInputFiles(TestConstants.APP_MODEL_FILENAME_MULTIPLE_INPUT_POINT,
            TestConstants.CLOUD_OFFER_FILENAME_IN_JSON_ATOS_7_MODULES);
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

   public void executeAndSave(String appModel2, String suitableCloudOffer2, SearchMethodName methodName) {
      String[] arrayAdp = optimizer.optimize(appModel, suitableCloudOffer, null);
      checkAllAppModulesHaveHost(arrayAdp, appModel);
      for (int adpnum = 0; adpnum < arrayAdp.length; adpnum++) {

         saveFile(TestConstants.OUTPUT_FILENAME + methodName + OUTPUT_FILENAME_CHARACTERISTIC + adpnum + ".yaml",
               arrayAdp[adpnum]);
      }

   }

   private void checkAllAppModulesHaveHost(String[] arrayAdp, String appModel) {

      Map<String, Object> aamNodesMap = YAMLoptimizerParser
            .getModuleMapFromAppMap((Map<String, Object>) YAMLoptimizerParser.getMAPofAPP(appModel));
      Set<String> modulesInAAM = aamNodesMap.keySet();
      for (int i = 0; i < arrayAdp.length; i++) {
         Map<String, Object> adpNodesMap = YAMLoptimizerParser
               .getModuleMapFromAppMap((Map<String, Object>) YAMLoptimizerParser.getMAPofAPP(arrayAdp[i]));

         Assert.assertTrue("Not all modules are included in ADP solution " + i + " with content: " + arrayAdp[i],
               allModulesHaveHost(adpNodesMap, modulesInAAM));
         ;
      }

   }

   private boolean allModulesHaveHost(Map<String, Object> adpNodesMap, Set<String> modulesInAAM) {
      for(String modName : modulesInAAM){
        
         if(adpNodesMap.containsKey(modName)){
            Map<String,Object> moduleInfo = (Map<String,Object>) adpNodesMap.get(modName);
            try{
               List<Map<String,Object>> requirements = (List<Map<String, Object>>) moduleInfo.get(TOSCAkeywords.MODULE_REQUIREMENTS);
               //check whether there is a host information in any of the list of module requirements 
               boolean hostPresent=false;
               for(Map<String,Object> req : requirements){
                  
                if(req.containsKey(TOSCAkeywords.MODULE_REQUIREMENTS_HOST)){
                 hostPresent=true;   
                 Assert.assertTrue("The host module " +modName +" which was: "+ ((String) req.get(TOSCAkeywords.MODULE_REQUIREMENTS_HOST)) + " was not found as a module", 
                       adpNodesMap.containsKey((String) req.get(TOSCAkeywords.MODULE_REQUIREMENTS_HOST)));
                }
                
               }
               if(!hostPresent){return false;}
            
               
            }
            catch(Exception E){
               //Some of the requirements.host did not exist
               log.info("It was not found a host for module: " + moduleInfo);
               return false;
            }
            
         }
         else{return false;}
      }
      //Nothing unexpected was found
      return true;
   }

  
   @AfterClass
   public void testFinishced() {
      log.info("===== ALL TESTS FOR OPTIMIZER FOR " + TEST_CHARACTERISTIC + " USING DECEMBER 2015 TOSCA FINISHED ===");
   }

}
