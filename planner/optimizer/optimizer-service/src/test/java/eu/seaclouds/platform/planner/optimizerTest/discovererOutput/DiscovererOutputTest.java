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

package eu.seaclouds.platform.planner.optimizerTest.discovererOutput;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethodName;
import eu.seaclouds.platform.planner.optimizerTest.TestConstants;

public class DiscovererOutputTest {

   
   private static final String NL = System.lineSeparator();
   private static String appModel;

   static Logger log;

   @BeforeClass
   public void createObjects() {

      log = LoggerFactory.getLogger(DiscovererOutputTest.class);
   }

   @Test(enabled = false)
   public void testPresenceHeartbeat() {
      log.info("=== TEST for RETRIEVING DATA FROM DISCOVERER  STARTED ===");

      String url = null;

      HttpClient client = new HttpClient();
      PostMethod method = new PostMethod(url);

      method.addParameter("oid", "2775488370683472268");

      executeAndCheck(client, method);

      log.info("=== TEST for RETRIEVING DATA FROM DISCOVERER  FINISEHD ===");
   }

   private void executeAndCheck(HttpClient client, PostMethod method) {
      InputStream in = null;
      int numLines = 0;
      String outputLine;
      String completeOutput = "";
      try {
         int statusCode = client.executeMethod(method);

         if (statusCode != -1) {
            in = method.getResponseBodyAsStream();
         }
         Assert.assertNotNull(in);
         BufferedReader br = new BufferedReader(new InputStreamReader(in));

         while ((outputLine = br.readLine()) != null) {
            completeOutput += NL + outputLine;
            log.info(outputLine);
            numLines++;
            
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      Assert.assertNotNull(completeOutput);

   }

}
