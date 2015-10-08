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

package eu.seaclouds.platform.planner.optimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.platform.planner.optimizer.heuristics.Anneal;
import eu.seaclouds.platform.planner.optimizer.heuristics.HillClimb;
import eu.seaclouds.platform.planner.optimizer.heuristics.BlindSearch;
import eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethod;
import eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethodName;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityAnalyzer;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityInformation;
import eu.seaclouds.platform.planner.optimizer.util.TOSCAkeywords;
import eu.seaclouds.platform.planner.optimizer.util.YAMLmatchmakerToOptimizerParser;
import eu.seaclouds.platform.planner.optimizer.util.YAMLoptimizerParser;

public class OptimizerInitialDeployment {

   private SearchMethod engine;
   static Logger log;

   private static final boolean IS_DEBUG = false;

   public OptimizerInitialDeployment() {
      engine = new BlindSearch();

   }

   public OptimizerInitialDeployment(SearchMethodName name) {

      System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, TOSCAkeywords.LOG_LEVEL);
      log = LoggerFactory.getLogger(OptimizerInitialDeployment.class);

      switch (name) {
      case BLINDSEARCH:
         engine = new BlindSearch();
         break;
      case HILLCLIMB:
         engine = new HillClimb();
         break;
      case ANNEAL:
         engine = new Anneal();
         break;
      default:
         engine = new BlindSearch();
         // TODO:Complete with more methods
      }

   }

   public String[] optimize(String appModel, String suitableCloudOffer, int numPlansToGenerate) {

      // Get app characteristics
      Map<String, Object> appMap = YAMLoptimizerParser.getMAPofAPP(appModel);

      // Get cloud offers
      if (IS_DEBUG) {
         log.debug("Getting cloud optoins and characteristics");
      }

      SuitableOptions appInfoSuitableOptions = YAMLmatchmakerToOptimizerParser
            .getSuitableCloudOptionsAndCharacteristicsForModules(appModel, suitableCloudOffer);

      if (IS_DEBUG) {
         log.debug("Getting application Topology");
      }

      Topology topology = null;
      try {
         topology = YAMLoptimizerParser.getApplicationTopology(appMap, appInfoSuitableOptions);
      } catch (Exception E) {
         log.error("There was an exception while reading topology. More errors ahead");
      }
      // TODO: Remove the following temporal management of the lack of
      // topology.
      // Create an incorrect and ad-hoc one to keep the system working
      if (topology == null) {
         log.error(
               "Topology could not be parsed. Not known quantity of calls between modules. Assuming the dummy case where"
                     + "all modules are called in sequence. The order of calls is random}");
         topology = createAdHocTopologyFromSuitableOptions(appInfoSuitableOptions);
      }

      // Read the quality requirements of the application
      if (IS_DEBUG) {
         log.debug("Getting application Requirements");
      }
      QualityInformation requirements = loadQualityRequirements(appMap);
      if (IS_DEBUG) {
         log.debug("following requirements found: " + requirements.toString());

      }

      // Create the skeleton of the numPlansToGenerate solutions to fill
      // This is only to avoid using the appMap or call to the TOSCA parser
      // inside

      // Compute solution
      Solution[] solutions = engine.computeOptimizationProblem(appInfoSuitableOptions.clone(), requirements, topology,
            numPlansToGenerate);

      if (solutions == null) {
         log.error("Map returned by Search engine is null");
      }

      Map<String, Object>[] appMapSolutions = hashMapOfFoundSolutionsWithThresholds(solutions, appMap, topology,
            appInfoSuitableOptions, numPlansToGenerate, requirements);

      if (IS_DEBUG) {
         log.debug("Before ReplaceSuitableServiceByHost");
      }

      String[] stringSolutions = new String[numPlansToGenerate];
      for (int i = 0; i < appMapSolutions.length; i++) {
         YAMLoptimizerParser.replaceSuitableServiceByHost(appMapSolutions[i]);
         stringSolutions[i] = YAMLoptimizerParser.fromMAPtoYAMLstring(appMapSolutions[i]);
      }

      return stringSolutions;
   }

   private Map<String, Object>[] hashMapOfFoundSolutionsWithThresholds(Solution[] bestSols,
         Map<String, Object> applicMap, Topology topology, SuitableOptions cloudOffers, int numPlansToGenerate,
         QualityInformation requirements) {

      if (IS_DEBUG) {
         engine.checkQualityAttachedToSolutions(bestSols);
      }
      @SuppressWarnings("unchecked")
      Map<String, Object>[] solutions = new HashMap[numPlansToGenerate];

      for (int i = 0; i < bestSols.length; i++) {

         Map<String, Object> baseAppMap = YAMLoptimizerParser.cloneYAML(applicMap);

         addSolutionToAppMap(bestSols[i], baseAppMap);

         if (IS_DEBUG) {
            log.debug("Before creating reconfiguration thesholds");
         }

         HashMap<String, ArrayList<Double>> thresholds = createReconfigurationThresholds(bestSols[i], baseAppMap,
               topology, cloudOffers, requirements);

         if (IS_DEBUG) {
            log.debug("Before adding the reconfiguration thesholds to the map");
         }
         YAMLoptimizerParser.addReconfigurationThresholds(thresholds, baseAppMap);

         solutions[i] = baseAppMap;
      }
      return solutions;
   }

   private void addSolutionToAppMap(Solution currentSol, Map<String, Object> applicationMap) {

      if (IS_DEBUG) {
         log.debug("Adding solution" + currentSol.toString() + "to MAP ");
      }
      for (String solkey : currentSol) {


         if (IS_DEBUG) {
            String instances = "";
            try {
               instances = String.valueOf(currentSol.getCloudInstancesForModule(solkey));
            } catch (Exception E) {
               instances = "Exception!";
            }
            log.debug("Before adding instances to '" + solkey + "': cloudOffer="
                  + currentSol.getCloudOfferNameForModule(solkey) + " instances=" + instances);

         }
         try {
            YAMLoptimizerParser.addSuitableOfferForModule(solkey, currentSol.getCloudOfferNameForModule(solkey),
                  currentSol.getCloudInstancesForModule(solkey), applicationMap);
         } catch (Exception E) {
            YAMLoptimizerParser.addSuitableOfferForModule(solkey, currentSol.getCloudOfferNameForModule(solkey), -1,
                  applicationMap);
         }
         if (IS_DEBUG) {
            log.debug("Added '" + solkey + "' to solution");
         }
      }

      if (IS_DEBUG) {
         log.debug("Adding the quality of the solution to the group of the intial element");
      }
      YAMLoptimizerParser.addQualityOfSolution(currentSol, applicationMap);

   }

   /**
    * @param sol
    * @param applicationMap
    * @param topology
    * @param cloudCharacteristics
    *           This method uses performance evaluation techniques to propose
    *           the thresholds to reconfigure modules of the system until
    *           expiring the cost
    */
   public HashMap<String, ArrayList<Double>> createReconfigurationThresholds(Solution sol,
         Map<String, Object> applicationMap, Topology topology, SuitableOptions cloudCharacteristics,
         QualityInformation requirements) {

      if (IS_DEBUG) {
         log.debug("Starting the creation of reconfiguration thresholds");
      }

      loadQualityRequirements(applicationMap);
      QualityAnalyzer qualityAnalyzer = new QualityAnalyzer();

      // if the solution does not satisfy the performance requirements,
      // nothing
      // to do
      if (IS_DEBUG) {
         log.debug("Create reconfiguration Thresholds method is going to call the compute Performance");
      }
      double perfGoodness = requirements.getResponseTime() / qualityAnalyzer
            .computePerformance(sol, topology, requirements.getWorkload(), cloudCharacteristics).getResponseTime();

      if (IS_DEBUG) {
         log.debug("Create reconfiguration Thresholds method has finished its call to compute Performance");
      }

      if ((requirements.existResponseTimeRequirement()) && (perfGoodness >= 1.0)) {// response
         // time
         // requirements
         // are
         // satisfied
         // if
         // perfGoodness>=1.0

         // A HashMap with all the keys of module names, and associated an
         // arraylist with the thresholds for reconfigurations.
         HashMap<String, ArrayList<Double>> thresholds = new HashMap<String, ArrayList<Double>>();

         if (IS_DEBUG) {
            log.debug("Starting teh computation of reconfiguration thresholds");
         }
         thresholds = qualityAnalyzer.computeThresholds(sol, topology, requirements, cloudCharacteristics);

         if (IS_DEBUG) {
            log.debug("Finishing the creation of reconfiguration thresholds");
         }
         return thresholds;
      } else {// There are not performance requirements, so no thresholds are
              // created.
         log.info("Finishing the creation of reconfiguration thresholds because there "
               + "were not performance requirements or solution could not satisfy performance. Solution: "
               + sol.toString() + " quality attributes: " + sol.getSolutionQuality().toString());
         return null;
      }

   }

   // TODO: Remove this method to avoid finishing weird executions when the
   // YAML
   // does not contain all the information.
   // Later it will be better an exception than a weird result.
   private Topology createAdHocTopologyFromSuitableOptions(SuitableOptions appInfoSuitableOptions) {

      Topology topology = new Topology();

      TopologyElement current = null;
      TopologyElement previous = null;

      for (String moduleName : appInfoSuitableOptions.getStringIterator()) {

         if (current == null) {
            // first element treated. None of them needs to point at it
            current = new TopologyElement(moduleName);
            topology.addModule(current);

         } else {// There were explored already other modules
            previous = current;
            current = new TopologyElement(moduleName);
            previous.addElementCalled(current);
            topology.addModule(current);
         }

      }

      return topology;

   }

   private QualityInformation loadQualityRequirements(Map<String, Object> applicationMap) {

      QualityInformation requirements = YAMLoptimizerParser.getQualityRequirements(applicationMap);

      // Maybe the previous operation did not work because Requirements could
      // not be found in the YAML. Follow an ad-hoc solution to get some
      // requirements
      if (requirements == null) {
         log.error(
               "Quality requirements not found in the input document. Loading dummy quality requirements for testing purposes");
         requirements = YAMLoptimizerParser.getQualityRequirementsForTesting();

      }

      if (requirements.existResponseTimeRequirement()) {
         loadWorkload(applicationMap, requirements);
      }

      return requirements;

   }

   private void loadWorkload(Map<String, Object> applicationMap, QualityInformation requirements) {
      if (requirements.getWorkload() <= 0.0) {
         requirements.setWorkloadMinute(YAMLoptimizerParser.getApplicationWorkload(applicationMap));
      }
      // Maybe the previous operation did not work correctly because the
      // workload could not be found in the YAML. Follow an ad-hoc solution to
      // get some requirements
      if (!requirements.hasValidWorkload()) {
         log.error(
               "Valid workload information not found in the input document. Loading dummy quality requirements for testing purposes");
         requirements.setWorkloadMinute(YAMLoptimizerParser.getApplicationWorkloadTest());
      }

   }
}
