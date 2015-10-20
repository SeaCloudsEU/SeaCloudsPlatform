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

package eu.seaclouds.platform.planner.optimizerTest.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethodName;
import eu.seaclouds.platform.planner.optimizerTest.TestConstants;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;

@Test(enabled = TestConstants.EnabledTestOpenShift)
public class OptimizerServiceOpenShiftTest {

  
   private static final String BASE_URL = "http://planner-seacloudspolimi.rhcloud.com/optimizer0.8-oct2015/optimizer/";
   private static String appModel;
   private static String suitableCloudOffer;

   private String NL = System.getProperty("line.separator");

   static Logger log;

   @BeforeClass
   public void createObjects() {
      

      log = LoggerFactory.getLogger(OptimizerServiceOpenShiftTest.class);

      log.info("Starting TEST optimizer SERVICE DEPLOYED IN OPENSHIFT");

      final String dir = System.getProperty("user.dir");
      log.debug("Trying to open files: current executino dir = " + dir);

      try {
         appModel = filenameToString(dir+"/../optimizer-core/"+TestConstants.APP_MODEL_FILENAME_OPENSHIFT);
      } catch (IOException e) {
         log.error("File for APPmodel not found");
         e.printStackTrace();
      }

      try {
         suitableCloudOffer = filenameToString(dir+"/../optimizer-core/"+TestConstants.CLOUD_OFFER_FILENAME_OPENSHIFT);
      } catch (IOException e) {
         log.error("File for Cloud Offers not found");
         e.printStackTrace();
      }

   }

   private static String filenameToString(String path) throws IOException {
      byte[] encoded = Files.readAllBytes(Paths.get(path));
      return new String(encoded, StandardCharsets.UTF_8);
   }

   @Test(enabled = TestConstants.EnabledTestOpenShift) //check also if the class is enabled
   public void testPresenceSolutionBlind() {

      log.info("=== TEST for SOLUTION GENERATION of BLIND optimizer service DEPLOYED IN OPENSHIFT STARTED ===");

      String url = BASE_URL + "optimize";

      HttpClient client = new HttpClient();
      PostMethod method = new PostMethod(url);

      method.addParameter("aam", appModel);
      method.addParameter("offers", suitableCloudOffer);
      method.addParameter("optmethod", SearchMethodName.BLINDSEARCH.toString());

      executeAndCheck(client, method, SearchMethodName.BLINDSEARCH.toString());

      log.info("=== TEST for SOLUTION GENERATION of BLIND optimizer service DEPLOYED IN OPENSHIFT FINISEHD ===");

   }
   
   @Test(enabled = TestConstants.EnabledTestOpenShift)
   public void testPresenceSolutionHill() {

      log.info("=== TEST for SOLUTION GENERATION of HILLCLIMB optimizer service DEPLOYED IN OPENSHIFT STARTED ===");

      String url = BASE_URL + "optimize";

      HttpClient client = new HttpClient();
      PostMethod method = new PostMethod(url);

      method.addParameter("aam", appModel);
      method.addParameter("offers", suitableCloudOffer);
      method.addParameter("optmethod", SearchMethodName.HILLCLIMB.toString());
      executeAndCheck(client, method, SearchMethodName.HILLCLIMB.toString());

      log.info("=== TEST for SOLUTION GENERATION of HILLCLIMB optimizer service DEPLOYED IN OPENSHIFT FINISEHD ===");

   }
   
   @Test(enabled = TestConstants.EnabledTestOpenShift)
   public void testPresenceSolutionAnneal() {

      log.info("=== TEST for SOLUTION GENERATION of ANNEAL optimizer service STARTED ===");

      String url = BASE_URL + "optimize";

      HttpClient client = new HttpClient();
      PostMethod method = new PostMethod(url);

      method.addParameter("aam", appModel);
      method.addParameter("offers", suitableCloudOffer);
      method.addParameter("optmethod", SearchMethodName.ANNEAL.toString());

      executeAndCheck(client, method,  SearchMethodName.ANNEAL.toString());

      log.info("=== TEST for SOLUTION GENERATION of ANNEAL optimizer service DEPLOYED IN OPENSHIFT FINISEHD ===");

   }
   

   private void executeAndCheck(HttpClient client, PostMethod method, String methodUsed) {
      InputStream in = null;
      int numOutputs = 0;
      String outputLine;
      String completeOutput = "";
      try {
         int statusCode = client.executeMethod(method);
         log.info("StatusCode was: " + statusCode);
         if (statusCode != -1) {
            in = method.getResponseBodyAsStream();
         }
         Assert.assertNotNull(in);
         BufferedReader br = new BufferedReader(new InputStreamReader(in));

         while ((outputLine = br.readLine()) != null) {
            completeOutput += NL + outputLine;

            if (outputLine.contains("---")) {
               numOutputs++;
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      Assert.assertTrue(numOutputs == 5,
            "Optimizer has not generated 5 output ADPs for method "+ methodUsed + " . The complete output is" + completeOutput);
      log.debug("Complete output is: " + completeOutput);
   }

   @Test(enabled = TestConstants.EnabledTestOpenShift)
   public void testPresenceHeartbeat() {

      log.info("=== TEST for presence of OPTIMIZER service heartbeat in " + BASE_URL +"===");

      String outputLine;
      String completeOutput = "";

      try {

         URL url = new URL(BASE_URL + "heartbeat");
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("GET");

         Assert.assertTrue(conn.getResponseCode() == 200,
               "Error requesting heartbeat in " + BASE_URL +". Instead of 200 we have received code " + conn.getResponseCode());

         BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

         while ((outputLine = br.readLine()) != null) {
            completeOutput += NL + outputLine;
         }

         conn.disconnect();

      } catch (MalformedURLException e) {

         e.printStackTrace();

      } catch (IOException e) {

         e.printStackTrace();

      }

      Assert.assertTrue(completeOutput.contains("alive"),
            "Optimizer NOT alive. Response of heartbeat is: " + completeOutput);

      log.info("=== TEST for presence of OPTIMIZER service heartbeat in " + BASE_URL +" FINISHED ===");

   }

   @Test(enabled = false)
   public void testPresenceHeartbeatSNAPSHOT() {


      log.info("=== TEST for presence of OPTIMIZER service DEPLOYED IN OPENSHIFT heartbeat in "+ BASE_URL +" ===");

      String outputLine;
      String completeOutput = "";

      try {

         URL url = new URL(BASE_URL + "heartbeat");
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("GET");

         Assert.assertTrue(conn.getResponseCode() == 200,
               "Error requesting heartbeat in " + BASE_URL + " . Instead of 200 we have received code " + conn.getResponseCode());

         BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

         while ((outputLine = br.readLine()) != null) {
            completeOutput += NL + outputLine;
         }

         conn.disconnect();

      } catch (MalformedURLException e) {

         e.printStackTrace();

      } catch (IOException e) {

         e.printStackTrace();

      }

      Assert.assertTrue(completeOutput.contains("alive"),
            "Optimizer NOT alive. Response of heartbeat is: " + completeOutput);

      log.info("=== TEST for presence of OPTIMIZER service heartbeat IN OPENSHIFT "+ BASE_URL +" FINISHED ===");

   }
   
   @Test(enabled = false)
   public void testPresenceHeartbeatDIRECT() {

      String urlstring="http://localhost:8080/optimizer/";
      log.info("=== TEST for presence of OPTIMIZER service heartbeat IN OPENSHIFT "+ "urlstring "+"===");

      String outputLine;
      String completeOutput = "";

      try {

         URL url = new URL(urlstring + "heartbeat");
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("GET");

         Assert.assertTrue(conn.getResponseCode() == 200,
               "Error requesting heartbeat in " + urlstring + " . Instead of 200 we have received code " + conn.getResponseCode());

         BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

         while ((outputLine = br.readLine()) != null) {
            completeOutput += NL + outputLine;
         }

         conn.disconnect();

      } catch (MalformedURLException e) {

         e.printStackTrace();

      } catch (IOException e) {

         e.printStackTrace();

      }

      Assert.assertTrue(completeOutput.contains("alive"),
            "Optimizer NOT alive. Response of heartbeat is: " + completeOutput);

      log.info("=== TEST for presence of OPTIMIZER SERVICE DEPLOYED IN OPENSHIFT service heartbeatin "+ "urlstring "+" FINISHED ===");

   }
   
   
   private String fromInputStreamToString(InputStream in) {
      StringWriter writer = new StringWriter();
      try {
         IOUtils.copy(in, writer);
      } catch (IOException e) {
         log.error("It was not possible to transform the inputStream to a String");
         e.printStackTrace();
      }
      return writer.toString();

   }

   @AfterClass
   public void testFinishced() {
      log.info("=============Test optimizer service DEPLOYED IN OPENSHIFT finished ==========");
   }

}
