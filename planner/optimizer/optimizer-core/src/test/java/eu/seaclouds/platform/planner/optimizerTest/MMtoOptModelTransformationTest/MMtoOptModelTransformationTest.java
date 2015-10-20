package eu.seaclouds.platform.planner.optimizerTest.MMtoOptModelTransformationTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import eu.seaclouds.platform.planner.optimizer.util.MMtoOptModelTransformer;
import eu.seaclouds.platform.planner.optimizerTest.TestConstants;


public class MMtoOptModelTransformationTest {

   static Logger log;
   String cloudInfoString="";
   @BeforeClass
   public void createObjects() {

      log = LoggerFactory.getLogger(MMtoOptModelTransformationTest.class);
      log.info("Starting TEST MM to Optimizer model transformation");

      final String dir = System.getProperty("user.dir");
      log.debug("Trying to open files: current executino dir = " + dir);

    


   }
   
   @Test(enabled = TestConstants.EnabledTest)
   public void testTransformationReadBytes() {
      log.info("Starting TEST MM to Optimizer model transformation of a file read like readAllBytes");
      try {
         cloudInfoString = filenameToStringReadBytes(TestConstants.CLOUD_OFFER_FILENAME_IN_JSON);
      } catch (IOException e) {
         log.error("File for Cloud Offers not found in " + TestConstants.CLOUD_OFFER_FILENAME_IN_JSON );
         e.printStackTrace();
      }
      log.debug("The input is: " + cloudInfoString);
      String output= MMtoOptModelTransformer.transformModel(cloudInfoString);
      Assert.assertNotNull("Output of the transformation is NULL", output); 
      log.debug("The output is: " + output);
      log.info("Finishing TEST MM to Optimizer model transformation of a file read like readAllBytes");
   }
   
   @Test(enabled = TestConstants.EnabledTest)
   public void testTransformationScanner() {
      log.info("Starting TEST MM to Optimizer model transformation of a file read like Scanner");
      try {
         cloudInfoString = filenameToStringScanner(TestConstants.CLOUD_OFFER_FILENAME_IN_JSON);
      } catch (IOException e) {
         log.error("File for Cloud Offers not found in " + TestConstants.CLOUD_OFFER_FILENAME_IN_JSON );
         e.printStackTrace();
      }
      log.debug("The input is: " + cloudInfoString);
      String output= MMtoOptModelTransformer.transformModel(cloudInfoString);
      Assert.assertNotNull("Output of the transformation is NULL", output); 
      log.debug("The output is: " + output);
      log.info("Finishing TEST MM to Optimizer model transformation of a file read like Scanner");
   }
   
   
   private String filenameToStringScanner(String outputMmJson) throws FileNotFoundException {
      Scanner scanner = new Scanner(new File(outputMmJson));
      scanner.useDelimiter("\n");
      String line="";
      while (scanner.hasNext()) {
          line += scanner.next()+System.lineSeparator();
      }
      return line;
   }

   private static String filenameToStringReadBytes(String path) throws IOException {
      byte[] encoded = Files.readAllBytes(Paths.get(path));
      return new String(encoded, StandardCharsets.UTF_8);
   }
   
   @AfterClass
   public void testFinishced() {
      log.info("===== ALL TESTS FOR MODEL TRANSFORMATION BETWEEN MATCHMAKER OPTIMIZER FINISHED ===");
   }
   
}
