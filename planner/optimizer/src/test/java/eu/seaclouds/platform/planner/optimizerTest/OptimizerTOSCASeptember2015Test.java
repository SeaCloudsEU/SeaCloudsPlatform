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

public class OptimizerTOSCASeptember2015Test {

   private static Optimizer    optimizer;
   private static String       appModel;
   private static String       suitableCloudOffer;


  
   
   static Logger               log;

@BeforeClass
public void createObjects() {

   System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, TOSCAkeywords.LOG_LEVEL);
   log                   = LoggerFactory
         .getLogger(OptimizerTOSCASeptember2015Test.class);

   log.info("Starting TEST optimizer for the TOSCA syntax of July 2015");

   final String dir = System.getProperty("user.dir");
   log.debug("Trying to open files: current executino dir = " + dir);

   try {
      appModel = filenameToString(TestConstants.APP_MODEL_FILENAME);
   } catch (IOException e) {
      log.error("File for APPmodel not found");
      e.printStackTrace();
   }

   try {
      suitableCloudOffer = filenameToString(TestConstants.CLOUD_OFFER_FILENAME);
   } catch (IOException e) {
      log.error("File for Cloud Offers not found");
      e.printStackTrace();
   }

}

private static String filenameToString(String path) throws IOException {
   byte[] encoded = Files.readAllBytes(Paths.get(path));
   return new String(encoded, StandardCharsets.UTF_8);
}

@Test(enabled=true)
public void testPresenceSolutionBlind() {

   log.info("=== TEST for SOLUTION GENERATION of BLIND optimizer STARTED (syntax July 2015)===");

   optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE,
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
      saveFile(TestConstants.OUTPUT_FILENAME + SearchMethodName.BLINDSEARCH + damnum
            + ".yaml", arrayDam[damnum]);
   }

   log.info("=== TEST for SOLUTION GENERATION of BLIND optimizer FINISEHD ===");

}

@Test(enabled=true)
public void testPresenceSolutionHillClimb() {

   log.info("=== TEST for SOLUTION GENERATION of HILLCLIMB optimizer STARTED ===");

   optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE,
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
      saveFile(TestConstants.OUTPUT_FILENAME + SearchMethodName.HILLCLIMB + damnum
            + ".yaml", arrayDam[damnum]);

   }

   log.info("=== TEST for SOLUTION GENERATION of HILLCLIMB optimizer FINISEHD ===");

}


@Test(enabled=true)
public void testPresenceSolutionAnneal() {

   log.info("=== TEST for SOLUTION GENERATION of ANNEAL optimizer STARTED ===");

   optimizer = new Optimizer(TestConstants.NUM_PLANS_TO_GENERATE,
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
      saveFile(TestConstants.OUTPUT_FILENAME + SearchMethodName.ANNEAL + damnum
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
         String suitableServicesLine[] = line.split(TestConstants.OPEN_SQUARE_BRACKET);

         for (String suitableLine : suitableServicesLine) {
            if ((suitableLine != null)
                  && suitableLine.contains(TestConstants.CLOSE_SQUARE_BRACKET)) {
               String suitableService = suitableLine.substring(0,
                     suitableLine.indexOf(TestConstants.CLOSE_SQUARE_BRACKET));
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


@AfterClass
public void testFinishced() {
   log.info("===== ALL TESTS FOR OPTIMIZER USING JULY 2015 TOSCA FINISHED ===");
}
   

}
