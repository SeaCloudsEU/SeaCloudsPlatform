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

import org.junit.Assert;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import eu.seaclouds.platform.planner.optimizer.Optimizer;
import eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethodName;
import eu.seaclouds.platform.planner.optimizer.util.TOSCAkeywords;

public class OptimizerTOSCAjuly2015Test extends AbstractTest {

   private static final String APP_MODEL_FILENAME_JULY15   = "./src/test/java/eu/seaclouds/platform/planner/optimizerTest/resources/aam.yml";
   private static final String CLOUD_OFFER_FILENAME_JULY15 = "./src/test/java/eu/seaclouds/platform/planner/optimizerTest/resources/cloudOfferWithQoS.yaml";
   private static final String OUTPUT_FILENAME_JULY15      = "./src/test/java/eu/seaclouds/platform/planner/optimizerTest/resources/target/outputNewTOSCA";
   private static final String OPEN_SQUARE_BRACKET         = "[";
   private static final String CLOSE_SQUARE_BRACKET        = "]";

   private static final int NUM_PLANS_TO_GENERATE = 5;

   @BeforeClass
   public void createObjects() {

      log = LoggerFactory.getLogger(OptimizerTOSCAjuly2015Test.class);

      log.info("Starting TEST optimizer for the TOSCA syntax of July 2015");

      final String dir = System.getProperty("user.dir");
      log.debug("Trying to open files: current executino dir = " + dir);

      try {
         appModel = filenameToString(APP_MODEL_FILENAME_JULY15);
      } catch (IOException e) {
         log.error("File for APPmodel not found");
         e.printStackTrace();
      }

      try {
         suitableCloudOffer = filenameToString(CLOUD_OFFER_FILENAME_JULY15);
      } catch (IOException e) {
         log.error("File for Cloud Offers not found");
         e.printStackTrace();
      }

   }

   @Test(enabled = false)
   public void testPresenceSolutionBlind() {

      log.info("=== TEST for SOLUTION GENERATION of BLIND optimizer STARTED (syntax July 2015)===");

      optimizer = new Optimizer(NUM_PLANS_TO_GENERATE, SearchMethodName.BLINDSEARCH);

      String[] arrayDam = optimizer.optimize(appModel, suitableCloudOffer);
      for (int damnum = 0; damnum < arrayDam.length; damnum++) {

         try {
            checkCorrectness(arrayDam[damnum]);
         } catch (Exception e) {
            log.error("There was an error in the check of correctness. Solution was: " + arrayDam[damnum]);
            throw e;
         }
         saveFile(OUTPUT_FILENAME_JULY15 + SearchMethodName.BLINDSEARCH + damnum + ".yaml", arrayDam[damnum]);
      }

      log.info("=== TEST for SOLUTION GENERATION of BLIND optimizer FINISEHD ===");

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
            String suitableServicesLine[] = line.split(OPEN_SQUARE_BRACKET);

            for (String suitableLine : suitableServicesLine) {
               if ((suitableLine != null) && suitableLine.contains(CLOSE_SQUARE_BRACKET)) {
                  String suitableService = suitableLine.substring(0, suitableLine.indexOf(CLOSE_SQUARE_BRACKET));
                  Assert.assertTrue("Suitable service is the empty string", suitableService != "");
                  Assert.assertTrue("Suitable service chosen does not belong to the cloud offer",
                        suitableCloudOffer.contains(suitableService));
                  numSuitableServicesFound++;
               }
            }

         }
      }
      Assert.assertEquals("Optimizer did not find any of the services", numServices, numSuitableServicesFound);

   }

   @AfterClass
   public void testFinishced() {
      log.info("===== ALL TESTS FOR OPTIMIZER USING JULY 2015 TOSCA FINISHED ===");
   }

}
