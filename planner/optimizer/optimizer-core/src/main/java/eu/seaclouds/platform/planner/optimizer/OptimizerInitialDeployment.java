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

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.platform.planner.optimizer.heuristics.Anneal;
import eu.seaclouds.platform.planner.optimizer.heuristics.BlindSearch;
import eu.seaclouds.platform.planner.optimizer.heuristics.HillClimb;
import eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethod;
import eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethodName;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityAnalyzer;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityInformation;
import eu.seaclouds.platform.planner.optimizer.util.DefaultConstants;
import eu.seaclouds.platform.planner.optimizer.util.YAMLmatchmakerToOptimizerParser;
import eu.seaclouds.platform.planner.optimizer.util.YAMLoptimizerParser;

public class OptimizerInitialDeployment {

   private static final String NL = System.lineSeparator();

   private SearchMethod engine;

   static Logger log;

   public OptimizerInitialDeployment() {
      engine = new BlindSearch();

   }

   public OptimizerInitialDeployment(SearchMethodName name) {

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

   private String openBenchmarkOffersDefaultFile() {
      byte[] encoded;
      try {
         encoded = Files.readAllBytes(Paths.get(DefaultConstants.DEFAULT_BENCHMARK_INFORMATION_PATH));
         return new String(encoded, StandardCharsets.UTF_8);
      } catch (Exception e) {
         log.debug("File with the information of Benchmark Platforms not found");
         log.debug(
               "Exception type " + e.getClass() + " message: " + e.getMessage() + " Exception cause: " + e.getCause());
         log.debug("Current execution dir=" + System.getProperty("user.dir") + " and looking for file: "
               + DefaultConstants.DEFAULT_BENCHMARK_INFORMATION_PATH);

      }

      try {
         
         InputStream in = this.getClass().getClassLoader()
               .getResourceAsStream(DefaultConstants.DEFAULT_BENCHMARK_INFORMATION_PACKAGE);
         byte[] buffer = new byte[in.available()];
         in.read(buffer);
         return new String(buffer, StandardCharsets.UTF_8);
      } catch (Exception e) {
         log.error("Packaged File with the information of Benchmark Platforms not found");
         log.error(
               "Exception type " + e.getClass() + " message: " + e.getMessage() + " Exception cause: " + e.getCause());
         log.error("Current execution dir=" + System.getProperty("user.dir") );

      }
      return null;
   }

   public String[] optimize(String appModel, String suitableCloudOffer, String benchmarkPlatformsYaml,
         int numPlansToGenerate, double hyst) {

      log.debug("Optimization method started. Inputs received");
      log.debug("AAM is: " + appModel);
      log.debug("Suitable offers are: " + suitableCloudOffer);
      log.debug("Informaton of Benchmark platform is: " + benchmarkPlatformsYaml);

      // Get app characteristics
      Map<String, Object> appMap = YAMLoptimizerParser.getMAPofAPP(appModel);

      // Get cloud offers

      log.debug("Getting cloud options and characteristics");

      SuitableOptions appInfoSuitableOptions = YAMLmatchmakerToOptimizerParser
            .getSuitableCloudOptionsAndCharacteristicsForModules(appModel, suitableCloudOffer);

      // Get benchmark platform
      log.debug("Getting the benchmark Platform");
      Map<String, CloudOffer> benchmarkPlatforms = getBenchmarkPlatforms(benchmarkPlatformsYaml);

      log.debug("Getting application Topology");

      Topology topology = null;
      try {
         topology = YAMLoptimizerParser.getApplicationTopology(appMap, appInfoSuitableOptions, benchmarkPlatforms);
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

      log.debug("Getting application Requirements");

      QualityInformation requirements = loadQualityRequirements(appMap);

      log.debug("following requirements found: " + requirements.toString());

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
            appInfoSuitableOptions, numPlansToGenerate, requirements, suitableCloudOffer, hyst);

      log.debug("Before ReplaceSuitableServiceByHost and adding the seaclouds.nodes.Compute type information");

      String[] stringSolutions = new String[numPlansToGenerate];
      for (int i = 0; i < appMapSolutions.length; i++) {
         YAMLoptimizerParser.addComputeTypeToTypes(appMapSolutions[i]);
         YAMLoptimizerParser.replaceSuitableServiceByHost(appMapSolutions[i]);
         stringSolutions[i] = YAMLoptimizerParser.fromMAPtoYAMLstring(appMapSolutions[i]);
      }

      return stringSolutions;
   }

   /**
    * @param benchmarkPlatformsYaml
    * @return a Map of benchmark platforms containing both the default ones and
    *         the obtained from the discoverer.
    */
   private Map<String, CloudOffer> getBenchmarkPlatforms(String benchmarkPlatformsYaml) {
      Map<String, CloudOffer> benchmarkPlatforms = YAMLmatchmakerToOptimizerParser
            .createBenchmarkPlatforms(openBenchmarkOffersDefaultFile());
      if (benchmarkPlatformsYaml != null) {
         benchmarkPlatforms.putAll(YAMLmatchmakerToOptimizerParser.createBenchmarkPlatforms(benchmarkPlatformsYaml));
      }

      return benchmarkPlatforms;
   }

   private Map<String, Object>[] hashMapOfFoundSolutionsWithThresholds(Solution[] bestSols,
         Map<String, Object> applicMap, Topology topology, SuitableOptions cloudOffers, int numPlansToGenerate,
         QualityInformation requirements, String suitableCloudOffer, double hyst) {

      if (log.isDebugEnabled()) {
         engine.checkQualityAttachedToSolutions(bestSols);
      }
      @SuppressWarnings("unchecked")
      Map<String, Object>[] solutions = new HashMap[numPlansToGenerate];

      for (int i = 0; i < bestSols.length; i++) {

         Map<String, Object> baseAppMap = YAMLoptimizerParser.cloneYAML(applicMap);

         log.debug("Solution number " + i + " shape before addSolutionToAppMap:");
         log.debug(YAMLoptimizerParser.fromMAPtoYAMLstring(baseAppMap));

         addSolutionToAppMap(bestSols[i], baseAppMap, suitableCloudOffer);

         log.debug("Solution number" + i + " shape after addSolutionToAppMap:");
         log.debug(YAMLoptimizerParser.fromMAPtoYAMLstring(baseAppMap));
         log.debug("Before creating reconfiguration thesholds");

         HashMap<String, ArrayList<Double>> thresholds = createReconfigurationThresholds(bestSols[i], baseAppMap,
               topology, cloudOffers, requirements);

         log.debug("Before adding the reconfiguration thesholds to the map. Thresholds found are: ");
         log.debug(showThresholds(thresholds));

         // if they are wanted all the thresholds instead of ontly the interva,
         // use
         // addReconfigurationThresholds mehtod from YAMLoptimizerParser class.
         if (thresholds != null) {
            addWorkloadAndPoolSizeBoundsForScalableModules(bestSols[i], thresholds, baseAppMap, topology, hyst);
            log.debug("After adding the reconfiguration thesholds to the map. Thresholds found are: ");
         } else {
            log.debug(
                  "Reconfiguration thresholds were not added because they cannot be computed (Performance not satisfied in initial solution)");
         }

         solutions[i] = baseAppMap;
      }
      return solutions;
   }

   private void addWorkloadAndPoolSizeBoundsForScalableModules(Solution solution,
         HashMap<String, ArrayList<Double>> thresholds, Map<String, Object> baseAppMap, Topology topology,
         double hyst) {

      // for each entry in thresholds
      for (Map.Entry<String, ArrayList<Double>> entry : thresholds.entrySet()) {
         log.debug("Adding the reconfiguration thresholds for module: " + entry.getKey());

         // if it can scale.
         if ((topology.getModule(entry.getKey()).canScale()) && (entry.getValue().size() > 0)) {
            // get number of instances in solution.
            int numInstances = solution.getCloudInstancesForModule(entry.getKey());
            // divide the first threshold by the number of instances. Thats the
            // upper bound.
            double upperWklBound = entry.getValue().get(0) / (double) numInstances;
            // the lower bound is the proportion of this specified in hysteresis
            double lowerWklBound = upperWklBound * hyst;
            // maximum pool is the number of instances plus the size of
            // thresholds list
            int maxPoolSize = entry.getValue().size() + numInstances;
            log.debug("Adding upperWkl= " + upperWklBound + " lowerWkl=" + lowerWklBound + " poolsize=" + maxPoolSize);
            YAMLoptimizerParser.addScalingPolicyToModule(entry.getKey(), baseAppMap, lowerWklBound, upperWklBound, 1,
                  maxPoolSize);
            YAMLoptimizerParser.changeModuleToScalableType(entry.getKey(), baseAppMap);
         } else {
            log.debug("Module " + entry.getKey() + " was not scalable");
         }

      }
   }

   private String showThresholds(HashMap<String, ArrayList<Double>> thresholds) {

      String out = "";
      if (thresholds == null) {
         out += "Thresholds was null (meaning that it WAS NOT an empty set, but pointed to null)";
         return out;
      }

      for (Map.Entry<String, ArrayList<Double>> threshold : thresholds.entrySet()) {
         out += threshold.getKey() + ": {";
         ArrayList<Double> list = threshold.getValue();

         // multiply each element by the ratio between seconds and minutes (60)
         for (int i = 0; i < list.size(); i++) {

            out += list.get(i) + ", ";
         }
         out += "}" + NL;
      }
      return out;
   }

   private void addSolutionToAppMap(Solution currentSol, Map<String, Object> applicationMap,
         String suitableCloudOffer) {

      log.debug("Adding solution" + currentSol.toString() + "to MAP ");

      for (String solkey : currentSol) {

         String instances = "";
         try {
            instances = String.valueOf(currentSol.getCloudInstancesForModule(solkey));
         } catch (Exception E) {
            instances = "Exception!";
         }
         log.debug("Before adding instances to '" + solkey + "': cloudOffer="
               + currentSol.getCloudOfferNameForModule(solkey) + " instances=" + instances);

         try {
            YAMLoptimizerParser.addSuitableOfferForModule(solkey, currentSol.getCloudOfferNameForModule(solkey),
                  currentSol.getCloudInstancesForModule(solkey), applicationMap);
         } catch (Exception E) {
            YAMLoptimizerParser.addSuitableOfferForModule(solkey, currentSol.getCloudOfferNameForModule(solkey), -1,
                  applicationMap);
         }

         log.debug("Added '" + solkey + "' to solution");

         Map<String, Object> cloudInfo = YAMLmatchmakerToOptimizerParser.getOfferInformationOfModule(solkey,
               currentSol.getCloudOfferNameForModule(solkey), suitableCloudOffer);
         YAMLoptimizerParser.addNodeTemplate(currentSol.getCloudOfferNameForModule(solkey), cloudInfo, applicationMap);
      }

      log.debug("Solution number shape after adding the host and befor adding the quality addSolutionToAppMap:");
      log.debug(YAMLoptimizerParser.fromMAPtoYAMLstring(applicationMap));
      log.debug("Adding the quality of the solution to the group of the intial element");

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
   private HashMap<String, ArrayList<Double>> createReconfigurationThresholds(Solution sol,
         Map<String, Object> applicationMap, Topology topology, SuitableOptions cloudCharacteristics,
         QualityInformation requirements) {

      log.debug("Starting the creation of reconfiguration thresholds");

      loadQualityRequirements(applicationMap);
      QualityAnalyzer qualityAnalyzer = new QualityAnalyzer();

      // if the solution does not satisfy the performance requirements,
      // nothing
      // to do
      log.debug("Create reconfiguration Thresholds method is going to call the compute Performance");

      double perfGoodness = requirements.getResponseTime() / qualityAnalyzer
            .computePerformance(sol, topology, requirements.getWorkload(), cloudCharacteristics).getResponseTime();

      log.debug("Create reconfiguration Thresholds method has finished its call to compute Performance");

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

         log.debug("Starting teh computation of reconfiguration thresholds");

         thresholds = qualityAnalyzer.computeThresholds(sol, topology, requirements, cloudCharacteristics);

         log.debug("Finishing the creation of reconfiguration thresholds");
         if (thresholds == null || thresholds.isEmpty()) {
            log.debug("Set of thresholds is empty");
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
