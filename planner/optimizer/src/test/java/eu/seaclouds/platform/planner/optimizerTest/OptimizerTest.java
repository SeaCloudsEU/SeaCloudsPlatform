/**
 * Copyright 2014 SeaClouds
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import eu.seaclouds.platform.planner.optimizer.Optimizer;
import eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethodName;
import eu.seaclouds.platform.planner.optimizer.util.TOSCAkeywords;

public class OptimizerTest {

   private static Optimizer    optimizer;
   private static String       appModel;
   private static String       suitableCloudOffer;
   private static final String APP_MODEL_FILENAME    = "./src/test/java/eu/seaclouds/platform/planner/optimizerTest/resources/MatchmakeroutputWithQoSAutomatic.yaml";
   private static final String CLOUD_OFFER_FILENAME  = "./src/test/java/eu/seaclouds/platform/planner/optimizerTest/resources/cloudOfferWithQoS.yaml";
   private static final String OUTPUT_FILENAME       = "./src/test/java/eu/seaclouds/platform/planner/optimizerTest/resources/target/output";
   private static final String OPEN_SQUARE_BRACKET   = "[";
   private static final String CLOSE_SQUARE_BRACKET  = "]";
   private static final double MAX_MILLIS_EXECUTING  = 20000;

   private static final int    NUM_PLANS_TO_GENERATE = 5;

   static Logger               log                   = LoggerFactory
                                                           .getLogger(OptimizerTest.class);

   @BeforeClass
   public void createObjects() {

      log.info("Starting TEST optimizer");

      final String dir = System.getProperty("user.dir");
      log.debug("Trying to open files: current executino dir = " + dir);

      try {
         appModel = filenameToString(APP_MODEL_FILENAME);
      } catch (IOException e) {
         log.error("File for APPmodel not found");
         e.printStackTrace();
      }

      try {
         suitableCloudOffer = filenameToString(CLOUD_OFFER_FILENAME);
      } catch (IOException e) {
         log.error("File for Cloud Offers not found");
         e.printStackTrace();
      }

   }

   private static String filenameToString(String path) throws IOException {
      byte[] encoded = Files.readAllBytes(Paths.get(path));
      return new String(encoded, StandardCharsets.UTF_8);
   }

   @Test
   public void testPresenceSolutionBlind() {

      log.info("=== TEST for SOLUTION GENERATION of BLIND optimizer STARTED ===");

      optimizer = new Optimizer(NUM_PLANS_TO_GENERATE,
            SearchMethodName.BLINDSEARCH);

      String[] arrayDam = optimizer.optimize(appModel, suitableCloudOffer);
      for (int damnum = 0; damnum < arrayDam.length; damnum++) {

         try {
            checkCorrectness(arrayDam[damnum]);
         } catch (Exception e) {
            log.error("There was an error in the check of correctness. Solution was: "
                  + arrayDam[damnum]);
            throw e;
         }
         saveFile(OUTPUT_FILENAME + SearchMethodName.BLINDSEARCH + damnum
               + ".yaml", arrayDam[damnum]);
      }

      log.info("=== TEST for SOLUTION GENERATION of BLIND optimizer FINISEHD ===");

   }

   @Test
   public void testPresenceSolutionHillClimb() {

      log.info("=== TEST for SOLUTION GENERATION of HILLCLIMB optimizer STARTED ===");

      optimizer = new Optimizer(NUM_PLANS_TO_GENERATE,
            SearchMethodName.HILLCLIMB);

      String[] arrayDam = optimizer.optimize(appModel, suitableCloudOffer);
      for (int damnum = 0; damnum < arrayDam.length; damnum++) {

         try {
            checkCorrectness(arrayDam[damnum]);
         } catch (Exception e) {
            log.error("There was an error in the check of correctness. Solution was: "
                  + arrayDam[damnum]);
            throw e;
         }
         saveFile(OUTPUT_FILENAME + SearchMethodName.HILLCLIMB + damnum
               + ".yaml", arrayDam[damnum]);

      }

      log.info("=== TEST for SOLUTION GENERATION of HILLCLIMB optimizer FINISEHD ===");

   }

   
   @Test
   public void testPresenceSolutionAnneal() {

      log.info("=== TEST for SOLUTION GENERATION of ANNEAL optimizer STARTED ===");

      optimizer = new Optimizer(NUM_PLANS_TO_GENERATE,
            SearchMethodName.ANNEAL);

      String[] arrayDam = optimizer.optimize(appModel, suitableCloudOffer);
      for (int damnum = 0; damnum < arrayDam.length; damnum++) {

         try {
            checkCorrectness(arrayDam[damnum]);
         } catch (Exception e) {
            log.error("There was an error in the check of correctness. Solution was: "
                  + arrayDam[damnum]);
            throw e;
         }
         saveFile(OUTPUT_FILENAME + SearchMethodName.ANNEAL + damnum
               + ".yaml", arrayDam[damnum]);

      }

      log.info("=== TEST for SOLUTION GENERATION of ANNEAL optimizer FINISEHD ===");

   }
   
   private void checkCorrectness(String dam) {

      Assert.assertFalse("Dam was not created, optimize method returns null",
            dam == null);
      String damLines[] = dam.split(System.getProperty("line.separator"));

      Assert.assertTrue("Dam was not created", damLines.length > 1);

      int numServices = 0;
      int numSuitableServicesFound = 0;

      for (String line : damLines) {
         if ((line != null) && (line.contains(TOSCAkeywords.SUITABLE_SERVICES))) {

            numServices++;
            String suitableServicesLine[] = line.split(OPEN_SQUARE_BRACKET);

            for (String suitableLine : suitableServicesLine) {
               if ((suitableLine != null)
                     && suitableLine.contains(CLOSE_SQUARE_BRACKET)) {
                  String suitableService = suitableLine.substring(0,
                        suitableLine.indexOf(CLOSE_SQUARE_BRACKET));
                  Assert.assertTrue("Suitable service is the empty string",
                        suitableService != "");
                  Assert.assertTrue(
                        "Suitable service chosen does not belong to the cloud offer",
                        suitableCloudOffer.contains(suitableService));
                  numSuitableServicesFound++;
               }
            }

         }
      }
      Assert.assertEquals("Optimizer did not find any of the services",
            numServices, numSuitableServicesFound);

   }

   private void saveFile(String outputFilename, String dam) {
      PrintWriter out = null;
      try {
         File file = new File(outputFilename);
         if (!file.exists()){
             file.getParentFile().mkdirs();
             file.createNewFile();
         }
         out = new PrintWriter(new FileWriter(file));
         out.println(dam);
      } catch (IOException e) {
          e.printStackTrace();
      } finally {
         if (out != null) {
            out.close();
         }
      }

   }

   @Test
   public void testPerformanceComplete() {

      optimizer = new Optimizer();

      log.info("=== TEST for PERFORMANCE of optimizer STARTED ===");
      long startTime = System.currentTimeMillis();
      optimizer.optimize(appModel, suitableCloudOffer);
      long finishTime = System.currentTimeMillis();

      log.debug("Optimizer execution time= "
            + (((double) (finishTime - startTime)) / 1000.0) + " seconds");
      Assert.assertTrue("Otimizer does not have good Performance. More than "
                      + ((double) MAX_MILLIS_EXECUTING) / 1000.1 + " seconds",
              (finishTime - startTime) < MAX_MILLIS_EXECUTING);
      log.info("=== TEST for PERFORMANCE of optimizer FINISHED===");

   }

   @AfterClass
   public void testFinishced() {
      log.info("===== ALL TESTS FOR OPTIMIZER FINISHED ===");
   }
}
