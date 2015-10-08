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

package eu.seaclouds.platform.planner.optimizerTest.nfp;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import eu.seaclouds.platform.planner.optimizer.CloudOffer;
import eu.seaclouds.platform.planner.optimizer.Solution;
import eu.seaclouds.platform.planner.optimizer.SuitableOptions;
import eu.seaclouds.platform.planner.optimizer.Topology;
import eu.seaclouds.platform.planner.optimizer.TopologyElement;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityAnalyzer;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityInformation;
import eu.seaclouds.platform.planner.optimizer.util.TOSCAkeywords;
import eu.seaclouds.platform.planner.optimizerTest.TestConstants;

public class QualityAnalyzerTest {

   private static QualityAnalyzer analyzer;

   static Logger log;

   @BeforeClass
   public void createObjects() {

      System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, TOSCAkeywords.LOG_LEVEL);

      log = LoggerFactory.getLogger(QualityAnalyzerTest.class);

      log.info("Starting TEST quality analyzer");
      analyzer = new QualityAnalyzer();

      final String dir = System.getProperty("user.dir");
      log.debug("Trying to open files: current executino dir = " + dir);

   }

   @Test(enabled = TestConstants.EnabledTest)
   public void testPerformanceEvaluation() {

      log.info("==== TEST for PERFORMANCE EVALUATION starts ====");

      Solution bestSol = createSolution();
      Topology topology = createTopology();

      double workload = 10;

      SuitableOptions cloudCharacteristics = createSuitableOptions();

      QualityInformation qInfo = analyzer.computePerformance(bestSol, topology, workload, cloudCharacteristics);

      Assert.assertTrue("Compute performance returned null", qInfo != null);

      log.info("Testing performance. Returned application response time is " + qInfo.getResponseTime());

      log.info("==== TEST for PERFORMANCE EVALUATION finishes ====");

   }

   @Test(enabled = TestConstants.EnabledTest)
   public void testAvailabilityEvaluation() {

      log.info("==== TEST for AVAILABILITY EVALUATION starts ====");

      Solution bestSol = createSolution();
      Topology topology = createTopology();

      SuitableOptions cloudCharacteristics = createSuitableOptions();

      double availability = analyzer.computeAvailability(bestSol, topology, cloudCharacteristics);

      Assert.assertTrue("Compute availability returned an impossible value", availability >= 0.0);
      Assert.assertTrue("Compute availability returned an impossible value", availability <= 1.0);

      log.info("Testing availability. Returned application availability is " + availability);

      log.info("==== TEST for AVAILABILITY EVALUATION finishes ====");

   }

   @Test(enabled = TestConstants.EnabledTest)
   public void testCostEvaluation() {

      log.info("==== TEST for COST EVALUATION starts ====");

      Solution bestSol = createSolution();

      SuitableOptions cloudCharacteristics = createSuitableOptions();

      double cost = analyzer.computeCost(bestSol, cloudCharacteristics);

      Assert.assertTrue("Compute cost returned an impossible value", cost >= 0.0);

      log.info("Testing cost. Returned application cost is " + cost);

      log.info("==== TEST for COST EVALUATION finishes ====");

   }

   @Test(enabled = TestConstants.EnabledTest)
   public void testReconfigurationThresholds() {

      log.info("==== TEST for RECONFIGURATION THRESHOLDS starts ====");
      Solution bestSol = createSolution();
      Topology topology = createTopology();
      SuitableOptions cloudCharacteristics = createSuitableOptions();

      QualityInformation requirements = new QualityInformation();
      requirements.setResponseTimeSecs(1000.0);
      requirements.setWorkload(10.0);
      requirements.setCostHour(40.0);

      HashMap<String, ArrayList<Double>> thresholds = analyzer.computeThresholds(bestSol, topology, requirements,
            cloudCharacteristics);
      Assert.assertTrue("Compute thresholds returns null", thresholds != null);

      log.info("Testing thresholds. Returned hashMap is " + thresholds);

      log.info("==== TEST for RECONFIGURATION THRESHOLDS starts ====");
   }

   private static Solution createSolution() {

      Solution sol = new Solution();
      sol.addItem("Module1", "CloudOffer1");
      sol.addItem("Module2", "CloudOffer2");

      return sol;

   }

   private static Topology createTopology() {

      TopologyElement e1 = new TopologyElement("Module1", 1.0); // name and
                                                                // execution
                                                                // time
      TopologyElement e2 = new TopologyElement("Module2", 1.0); // name and
                                                                // execution
                                                                // time

      e1.addElementCalled(e2);

      Topology topology = new Topology();
      topology.addModule(e1);
      topology.addModule(e2);

      return topology;

   }

   private static SuitableOptions createSuitableOptions() {

      CloudOffer offer1 = new CloudOffer("CloudOffer1", 20, 0.99, 2);
      CloudOffer offer2 = new CloudOffer("CloudOffer2", 30, 0.95, 3);

      SuitableOptions solutions = new SuitableOptions();

      ArrayList<String> optionsNamesMod1 = new ArrayList<String>();
      optionsNamesMod1.add("CloudOffer1");

      ArrayList<String> optionsNamesMod2 = new ArrayList<String>();
      optionsNamesMod2.add("CloudOffer2");

      ArrayList<CloudOffer> optionsMod1 = new ArrayList<CloudOffer>();
      optionsMod1.add(offer1);

      ArrayList<CloudOffer> optionsMod2 = new ArrayList<CloudOffer>();
      optionsMod2.add(offer2);

      solutions.addSuitableOptions("Module1", optionsNamesMod1, optionsMod1);
      solutions.addSuitableOptions("Module2", optionsNamesMod2, optionsMod2);

      return solutions;
   }

   @AfterClass
   public void testFinishced() {
      log.info("Test finished");
   }

}
