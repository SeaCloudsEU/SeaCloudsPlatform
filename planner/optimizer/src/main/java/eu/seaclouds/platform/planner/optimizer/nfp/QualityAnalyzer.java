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

package eu.seaclouds.platform.planner.optimizer.nfp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.platform.planner.optimizer.CloudOffer;
import eu.seaclouds.platform.planner.optimizer.Solution;
import eu.seaclouds.platform.planner.optimizer.SuitableOptions;
import eu.seaclouds.platform.planner.optimizer.Topology;
import eu.seaclouds.platform.planner.optimizer.TopologyElement;
import eu.seaclouds.platform.planner.optimizer.TopologyElementCalled;

public class QualityAnalyzer {

   private final boolean IS_DEBUG;
   private boolean defaultDebug = false;
   static Logger log = LoggerFactory.getLogger(QualityAnalyzer.class);

   private QualityInformation properties = null;

   private final double MAX_TIMES_WORKLOAD_FOR_THRESHOLDS;
   private final double WORKLOAD_INCREMENT_FOR_SEARCH;

   public QualityAnalyzer() {

      properties = new QualityInformation();
      MAX_TIMES_WORKLOAD_FOR_THRESHOLDS = 10.0;
      WORKLOAD_INCREMENT_FOR_SEARCH = 1.0;
      IS_DEBUG = defaultDebug;
   }

   public QualityAnalyzer(boolean debug) {

      properties = new QualityInformation();
      MAX_TIMES_WORKLOAD_FOR_THRESHOLDS = 10.0;
      WORKLOAD_INCREMENT_FOR_SEARCH = 1.0;
      IS_DEBUG = debug;
   }

   public QualityAnalyzer(double maxWorkload) {

      properties = new QualityInformation();
      MAX_TIMES_WORKLOAD_FOR_THRESHOLDS = maxWorkload;
      WORKLOAD_INCREMENT_FOR_SEARCH = 1.0;
      IS_DEBUG = defaultDebug;
   }

   public QualityAnalyzer(double maxWorkload, boolean debug) {

      properties = new QualityInformation();
      MAX_TIMES_WORKLOAD_FOR_THRESHOLDS = maxWorkload;
      WORKLOAD_INCREMENT_FOR_SEARCH = 1.0;
      IS_DEBUG = debug;
   }

   public QualityAnalyzer(double maxWorkload, double workloadIncrement) {

      properties = new QualityInformation();
      MAX_TIMES_WORKLOAD_FOR_THRESHOLDS = maxWorkload;
      WORKLOAD_INCREMENT_FOR_SEARCH = workloadIncrement;
      IS_DEBUG = defaultDebug;
   }

   public QualityAnalyzer(double maxWorkload, double workloadIncrement, boolean debug) {

      properties = new QualityInformation();
      MAX_TIMES_WORKLOAD_FOR_THRESHOLDS = maxWorkload;
      WORKLOAD_INCREMENT_FOR_SEARCH = workloadIncrement;
      IS_DEBUG = debug;
   }

   /**
    * @return This method returns the summary of all the quality values (one for
    *         each property) already calculated by the object. NOTE that it does
    *         not calculate any new property value
    */
   public QualityInformation getAllComputedQualities() {
      return properties;
   }

   // routing matrix with the OpProfile (the item in 0 is the mainly called)
   private double[][] routes = null;

   public QualityInformation computePerformance(Solution bestSol, Topology topology, double workload,
         SuitableOptions cloudCharacteristics) {

      if (routes == null) {
         routes = getRoutingMatrix(topology);
      }
      double[] workloadsModules = getWorkloadsArray(routes, workload);
      double[] numVisitsModule = getNumVisitsArray(workloadsModules, workload);

      // calculate the workload received by each core of a module: consider both
      // number of cores
      // of a cloud offer and number of instances for such module
      double[] workloadsModulesByCoresAndNumInstances = weightModuleWorkloadByCoresAndNumInstances(workloadsModules,
            topology, bestSol, cloudCharacteristics);

      double[] mus = getMusOfSelectedCloudOffers(bestSol, topology, cloudCharacteristics);

      if (IS_DEBUG) {
         log.debug("Solution to check the mus is: " + bestSol.toString());
         log.debug("Mus of servers are: " + Arrays.toString(mus));
         log.debug("Num visits modules is: " + Arrays.toString(numVisitsModule));
         log.debug("Workload received of modules: " + Arrays.toString(workloadsModules));
         log.debug("Workload received of each execution unit by its numberOfInstances and Cores: "
               + Arrays.toString(workloadsModulesByCoresAndNumInstances));
      }
      double respTime = getSystemRespTime(numVisitsModule, workloadsModulesByCoresAndNumInstances, mus);

      if (IS_DEBUG) {
         log.debug("calculated response time of the solution " + bestSol.toString()
               + " without considering latencies is: " + respTime);
      }
      respTime += addNetworkDelays(bestSol, topology, numVisitsModule, cloudCharacteristics);
      if (IS_DEBUG) {
         log.debug("calculated response time of the solution" + bestSol.toString() + " is: " + respTime);
      }
      // after computing, save the performance info in properties.performance
      properties.setResponseTimeSecs(respTime);

      return properties;
   }

   private double addNetworkDelays(Solution bestSol, Topology topology, double[] numVisitsModule,
         SuitableOptions cloudCharacteristics) {

      double networkDelay = 0.0;

      for (int i = 0; i < topology.size(); i++) {
         TopologyElement element = topology.getElementIndex(i);

         double sumOfDelaysSingleModule = 0.0;

         for (TopologyElementCalled elementCalled : element) {
            sumOfDelaysSingleModule += elementCalled.getProbCall() * latencyBetweenElements(bestSol, element.getName(),
                  elementCalled.getElement().getName(), cloudCharacteristics);
         }

         if (IS_DEBUG) {
            log.debug("calculated network delay for module " + i + " in solution " + bestSol.toString()
                  + " is (numVisitsModule[i] * sumOfDelaysSingleModule): "
                  + numVisitsModule[i] * sumOfDelaysSingleModule);
         }
         networkDelay += numVisitsModule[i] * sumOfDelaysSingleModule;

      }

      return networkDelay;

   }

   private double latencyBetweenElements(Solution bestSol, String callingModuleName, String calledModuleName,
         SuitableOptions cloudCharacteristics) {

      String cloudOfferCallingElement = bestSol.getCloudProviderNameForModule(callingModuleName);
      String cloudOfferCalledElement = bestSol.getCloudProviderNameForModule(calledModuleName);

      if (CloudOffer.providerNameOfCloudOffer(cloudOfferCallingElement)
            .equals(CloudOffer.providerNameOfCloudOffer(cloudOfferCalledElement))) {

         if (IS_DEBUG) {
            log.debug("latency between modules " + callingModuleName + " and " + calledModuleName + " is "
                  + cloudCharacteristics.getLatencyIntraDatacenterSec());
         }

         return cloudCharacteristics.getLatencyIntraDatacenterSec();

      } else {

         if (IS_DEBUG) {
            log.debug("latency between modules " + callingModuleName + " and " + calledModuleName + " is "
                  + cloudCharacteristics.getLatencyInterCloudSec());
         }

         return cloudCharacteristics.getLatencyInterCloudSec();
      }

   }

   private double getSystemRespTime(double[] numVisitsModule, double[] workloadsModulesByCoresAndNumInstances,
         double[] mus) {

      double respTime = 0;

      for (int i = 0; i < numVisitsModule.length; i++) {

         respTime += calculateRespTimeModule(numVisitsModule[i], workloadsModulesByCoresAndNumInstances[i], mus[i]);

      }

      return respTime;
   }

   /*
    * Here they are used the Queueing Network theory basics.
    */
   private double calculateRespTimeModule(double visits, double lambda, double mu) {

      double utilization = lambda / mu;
      if (utilization > 1.0) {
         return Double.POSITIVE_INFINITY;
      }
      double respTimeVisit = (1.0 / mu) / (1.0 - utilization);

      return visits * respTimeVisit;

   }

   private double[] getNumVisitsArray(double[] workloadsModules, double workload) {

      double[] numVisits = new double[workloadsModules.length];

      for (int i = 0; i < numVisits.length; i++) {
         numVisits[i] = workloadsModules[i] / workload;
      }

      return numVisits;
   }

   private double[] getMusOfSelectedCloudOffers(Solution bestSol, Topology topology,
         SuitableOptions cloudCharacteristics) {

      double[] mus = new double[topology.size()];
      for (int i = 0; i < mus.length; i++) {

         String moduleName = topology.getElementIndex(i).getName();
         String cloudChosenForModule = bestSol.getCloudOfferNameForModule(moduleName);
         mus[i] = cloudCharacteristics.getCloudCharacteristics(moduleName, cloudChosenForModule).getPerformance()
               / topology.getElementIndex(i).getDefaultExecutionTime();

         if (IS_DEBUG) {
            log.debug("Default execution time of module " + i + " with name " + topology.getElementIndex(i).getName()
                  + " is " + topology.getElementIndex(i).getDefaultExecutionTime() + " and using cloud option "
                  + bestSol.getCloudOfferNameForModule(moduleName) + " with performance "
                  + cloudCharacteristics.getCloudCharacteristics(moduleName, cloudChosenForModule).getPerformance()
                  + " its Mu is " + mus[i]);
         }
      }

      return mus;

   }

   private double[] weightModuleWorkloadByCoresAndNumInstances(double[] workloadsModules, Topology topology,
         Solution bestSol, SuitableOptions cloudCharacteristics) {
      double[] ponderatedWorkloads = new double[workloadsModules.length];
      for (int i = 0; i < workloadsModules.length; i++) {

         String moduleName = topology.getElementIndex(i).getName();
         double numInstances = -1;
         try {
            numInstances = bestSol.getCloudInstancesForModule(moduleName);
         } catch (Exception E) {// nothing to do

         }

         String cloudChosenForModule = bestSol.getCloudOfferNameForModule(moduleName);

         double numCores = cloudCharacteristics.getCloudCharacteristics(moduleName, cloudChosenForModule).getNumCores();
         ponderatedWorkloads[i] = workloadsModules[i] / (numInstances * numCores);

         if (IS_DEBUG) {
            log.debug("Number of instances used for module " + moduleName + " is : " + numInstances
                  + " and num Cores of the offer is" + numCores);
         }
      }

      return ponderatedWorkloads;
   }

   private double[] getWorkloadsArray(double[][] routing, double workload) {

      double[] workloadsReceived = new double[routing.length];

      boolean initialWorkloadIsSet = false;

      while (!completedWorkloadsCalculation(workloadsReceived)) {

         // See which workloads can be calculated with the current information
         for (int i = 0; i < routing.length; i++) {

            // If module i can be calculated (and was not calculate previously)
            if ((workloadsReceived[i] == 0.0) && (workloadCanBeCalculated(routing, workloadsReceived, i))) {

               // Calculating the received workload of Module i
               // The first case; i.e,. the one that receive requests directly,
               // "workload" has to be added to the array
               if (!initialWorkloadIsSet) {
                  initialWorkloadIsSet = true;
                  workloadsReceived[i] = workload;
               }

               for (int callingIndex = 0; callingIndex < routing.length; callingIndex++) {
                  workloadsReceived[i] += routing[callingIndex][i] * workloadsReceived[callingIndex];
               }
            }
         }

      }

      return workloadsReceived;

   }

   private boolean workloadCanBeCalculated(double[][] routing, double[] workloads, int indexCol) {

      for (int row = 0; row < routing.length; row++) {
         if ((routing[row][indexCol] != 0) && (workloads[row] == 0.0)) {
            // There is some calling module whose workload has not been
            // calculated yet.
            return false;
         }
      }

      return true;
   }

   private boolean completedWorkloadsCalculation(double[] workloads) {
      for (int i = 0; i < workloads.length; i++) {
         if (workloads[i] == 0.0) {
            return false;
         }
      }
      return true;
   }

   private double[][] getRoutingMatrix(Topology topology) {

      double[][] routing = new double[topology.size()][topology.size()];

      TopologyElement initialElement = topology.getInitialElement();
      int indexOfInitial = topology.indexOf(initialElement);

      // Easier if the index of initial element is 0. So, set it
      if (indexOfInitial != 0) {
         topology.replaceElementsIndexes(initialElement, 0);
      }

      if (topology.indexOf(initialElement) != 0) {
         log.warn("Index replacements in topology elements did not work!");
      }

      // TODO: improve this solution implementing an iterator over the modules
      // of the topology: now
      // allowed operation topology.getModules() breaks the encapsulation of
      // data by topology class
      for (TopologyElement e : topology.getModules()) {

         // TODO: improve this solution implementing an iterator over the
         // modules of the topology: now
         // allowed operation initialElement.getDependences() breaks the
         // encapsulation of data by TopologyElement class
         for (TopologyElementCalled c : e.getDependences()) {
            routing[topology.indexOf(e)][topology.indexOf(c.getElement())] = c.getProbCall();
         }
      }

      return routing;

   }

   // HashSet to check if an element has been visited. It is used for checking
   // for loops in the topology.

   private Set<String> visited;

   /**
    * @param bestSol
    * @param topology
    * @param cloudCharacteristics
    * @return The calculated availability of the system.
    * 
    *         This method will be recursive. The availablity of the system will
    *         be the product of the availability of its first module and the
    *         modules it requests. It will not work if there are cycles in the
    *         topology
    */
   public double computeAvailability(Solution bestSol, Topology topology, SuitableOptions cloudCharacteristics) {

      visited = new HashSet<String>();

      TopologyElement initialElement = topology.getInitialElement();

      visited.add(initialElement.getName());

      String cloudUsedInitialElement = bestSol.getCloudOfferNameForModule(initialElement.getName());
      double instancesUsedInitialElement = -1;
      try {
         instancesUsedInitialElement = bestSol.getCloudInstancesForModule(initialElement.getName());
      } catch (Exception E) {// nothing to do
      }
      double availabilityInitialElementInstance = cloudCharacteristics
            .getCloudCharacteristics(initialElement.getName(), cloudUsedInitialElement).getAvailability();

      double unavailabilityInitialElement = Math.pow((1.0 - availabilityInitialElementInstance),
            instancesUsedInitialElement);
      double availabilityInitialElement = 1.0 - unavailabilityInitialElement;

      double systemAvailability = availabilityInitialElement;

      for (TopologyElementCalled c : topology.getInitialElement().getDependences()) {
         systemAvailability = systemAvailability
               * calculateAvailabilityRecursive(c, bestSol, topology, cloudCharacteristics);
      }

      if (IS_DEBUG) {
         log.debug("Finished calculation of availability of solution: " + bestSol.toString());
      }
      // after computing, save the availability info in properties.availability
      properties.setAvailability(systemAvailability);

      return systemAvailability;
   }

   private double calculateAvailabilityRecursive(TopologyElementCalled c, Solution bestSol, Topology topology,
         SuitableOptions cloudCharacteristics) {


      visited.add(c.getElement().getName());

      String cloudUsedForElement = bestSol.getCloudOfferNameForModule(c.getElement().getName());
      double instancesUsedForElement = -1;
      try {
         instancesUsedForElement = bestSol.getCloudInstancesForModule(c.getElement().getName());
      } catch (Exception E) {// nothing to do

      }
      double availabilityElementInstance = cloudCharacteristics
            .getCloudCharacteristics(c.getElement().getName(), cloudUsedForElement).getAvailability();
      double unavailabilityElement = Math.pow((1.0 - availabilityElementInstance), instancesUsedForElement);

      double availabilityElement = 1.0 - unavailabilityElement;

      for (TopologyElementCalled cc : c.getElement().getDependences()) {
         availabilityElement = availabilityElement
               * calculateAvailabilityRecursive(cc, bestSol, topology, cloudCharacteristics);
      }

      double callAvailability = c.getProbCall() * availabilityElement + (1.0 - c.getProbCall());

      return callAvailability;
   }

   public double computeCost(Solution bestSol, SuitableOptions cloudCharacteristics) {

      double cost = 0.0;

      for (String moduleName : bestSol) {

         String cloudUsedForElement = bestSol.getCloudOfferNameForModule(moduleName);

         double instancesUsedForElement = -1;
         try {
            instancesUsedForElement = bestSol.getCloudInstancesForModule(moduleName);
         } catch (Exception E) {// Nothing to do

         }

         double costElementInstance = cloudCharacteristics.getCloudCharacteristics(moduleName, cloudUsedForElement)
               .getCost();

         cost += (instancesUsedForElement * costElementInstance);

      }

      // after computing, save the cost info in properties.availability
      properties.setCostHour(cost);
      return cost;

   }

   public HashMap<String, ArrayList<Double>> computeThresholds(Solution solInput, Topology topology,
         QualityInformation requirements, SuitableOptions cloudCharacteristics) {

      HashMap<String, ArrayList<Double>> thresholds = new HashMap<String, ArrayList<Double>>();

      Solution modifSol = solInput.clone();
      double workload = 0;
      if ((requirements.hasValidWorkload()) && (requirements.existResponseTimeRequirement())) {

         workload = requirements.getWorkload();
      } else {// If no valid workload is specified, there cannot be created the
              // thresholds
         log.debug(
               "Reconfiguration Thresholds not created because Response Time requirement or expected workload was not found.");
         return null;
      }

      double[] mus = getMusOfSelectedCloudOffers(modifSol, topology, cloudCharacteristics);

      double limitWorkload = workload;
      boolean existModulesToScaleOut = true;
      while (continueGeneratingThresholds(limitWorkload, workload, modifSol, requirements, cloudCharacteristics,
            existModulesToScaleOut)) {
         // Stop condition is the highest allowed cost or, if cost is not
         // specified, ten times the expected worklaod

         if (IS_DEBUG) {
            log.debug("Creating threshold for workload above " + limitWorkload);
         }
         limitWorkload = findWorkloadForWhichRespTimeIsExceeded(requirements.getResponseTime(), limitWorkload, mus,
               modifSol, topology, cloudCharacteristics);
         // get highest utilization
         String moduleWithHighestUtilization = findHighestUtilizationModuleThatCanScale(limitWorkload, mus, modifSol,
               topology, cloudCharacteristics);

         // put the value in the hashMap. "moduleWithHighestUtilization" may be
         // null
         addThresholdToThresholds(thresholds, limitWorkload, moduleWithHighestUtilization);

         // modify the solution to use a resource more of the currently
         // specified. "moduleWithHighestUtilization" may be null
         addResourceToSolution(modifSol, moduleWithHighestUtilization);

         if (moduleWithHighestUtilization == null) {
            existModulesToScaleOut = false;
            // can be shortened to
            // "existModulesToScaleOut=(!(moduleWithHighestUtilization==null));"
            // but it is less readable in my opinion.
         }

      }
      return thresholds;
   }

   private void addResourceToSolution(Solution sol, String moduleWithHighestUtilization) {

      try {
         sol.modifyNumInstancesOfModule(moduleWithHighestUtilization,
               sol.getCloudInstancesForModule(moduleWithHighestUtilization) + 1);
      } catch (Exception E) {
         sol.modifyNumInstancesOfModule(moduleWithHighestUtilization, 0);
      }
   }

   private void addThresholdToThresholds(HashMap<String, ArrayList<Double>> thresholds, double limitWorkload,
         String moduleName) {

      if (moduleName == null) {
         return;
      }
      if (thresholds.containsKey(moduleName)) {
         thresholds.get(moduleName).add(limitWorkload);
      } else {// First time that this module is scaled out
         ArrayList<Double> list = new ArrayList<Double>();
         list.add(limitWorkload);
         thresholds.put(moduleName, list);
      }

   }

   /**
    * @param limitWorkload
    * @param routes
    * @param mus
    * @param cloudCharacteristics
    * @param topology
    * @param modifSol
    * @return The name of the module with highest utilization. This method makes
    *         a strong assumption that rows in routes matrix and elements in
    *         topology are in the same order (which should happen if no bugs
    *         were included programming)
    */
   private String findHighestUtilizationModuleThatCanScale(double limitWorkload, double[] mus, Solution sol,
         Topology topology, SuitableOptions cloudCharacteristics) {

      double[] workloadsModules = getWorkloadsArray(routes, limitWorkload);

      // calculate the workload received by each core of a module: consider both
      // number of cores
      // of a cloud offer and number of instances for such module
      double[] workloadsModulesByCoresAndNumInstances = weightModuleWorkloadByCoresAndNumInstances(workloadsModules,
            topology, sol, cloudCharacteristics);

      // find upper value
      double[] utilizations = utilizationOfEachModule(workloadsModulesByCoresAndNumInstances, mus);

      int maxUtilizationIndex = -1;
      for (int i = 0; i < utilizations.length; i++) {
         if (topology.getElementIndex(i).canScale()) {// It is a candidate
            if (maxUtilizationIndex == -1) {// choose it directly because it's
                                            // the first one that can scale
               maxUtilizationIndex = i;
            } else {
               if (utilizations[i] > utilizations[maxUtilizationIndex]) {
                  // It could be done as an OR in the previous condition, but
                  // this is
                  // a safe manner as the order in which Java evaluates
                  // disyuntive conditions
                  maxUtilizationIndex = i;
               }
            }
         }
      }

      if (maxUtilizationIndex > -1) {
         return topology.getElementIndex(maxUtilizationIndex).getName();
      } else {
         log.debug("None of the modules can scale. Thresholds cannot be calculated");
         return null;
      }
   }

   private double[] utilizationOfEachModule(double[] workloadsModulesByCoresAndNumInstances, double[] mus) {

      double[] utilizations = new double[mus.length];
      for (int i = 0; i < utilizations.length; i++) {
         utilizations[i] = workloadsModulesByCoresAndNumInstances[i] / mus[i];
      }

      return utilizations;
   }

   /*
    * Condition to stop generating thresholds
    */
   private boolean continueGeneratingThresholds(double limitWorkload, double workload, Solution sol,
         QualityInformation requirements, SuitableOptions cloudCharacteristics, boolean existModulesToScaleOut) {
      // Stop if the maximum number of scalings for every module has been
      // reached.
      // Stop also if the highest allowed cost or, if cost is not specified, ten
      // times the expected workload

      if (IS_DEBUG) {
         log.debug("checking if continue generating thresholds for: ");
      }

      if (!existModulesToScaleOut) {
         return false;
      }
      if (requirements.existCostRequirement()) {

         if (IS_DEBUG) {
            log.debug("  current cost: " + computeCost(sol, cloudCharacteristics) + " cost limit: "
                  + requirements.getCostHour());
         }

         return computeCost(sol, cloudCharacteristics) <= requirements.getCostHour();
      }

      if (IS_DEBUG) {
         log.debug("  limitWorkload: " + limitWorkload + " current workload "
               + (MAX_TIMES_WORKLOAD_FOR_THRESHOLDS * workload));
      }

      return limitWorkload <= (MAX_TIMES_WORKLOAD_FOR_THRESHOLDS * workload);

   }

   /**
    * @param workload
    * @param routes
    * @param mus
    * @param cloudCharacteristics
    * @param topology
    * @param modifSol
    * @return the first workload value for which the requirement of response
    *         time is not satisfied
    */
   private double findWorkloadForWhichRespTimeIsExceeded(double respTimeRequirement, double workload, double[] mus,
         Solution sol, Topology topology, SuitableOptions cloudCharacteristics) {

      double incWorkload = WORKLOAD_INCREMENT_FOR_SEARCH;

      double[] workloadsModules = getWorkloadsArray(routes, workload + incWorkload);
      double[] numVisitsModule = getNumVisitsArray(workloadsModules, workload + incWorkload);

      // calculate the workload received by each core of a module: consider both
      // number of cores
      // of a cloud offer and number of instances for such module
      double[] workloadsModulesByCoresAndNumInstances = weightModuleWorkloadByCoresAndNumInstances(workloadsModules,
            topology, sol, cloudCharacteristics);

      if (IS_DEBUG) {
         log.debug(
               "Response time is: " + getSystemRespTime(numVisitsModule, workloadsModulesByCoresAndNumInstances, mus)
                     + " and " + "the performance requirement is " + respTimeRequirement);
      }
      // find upper value. TODO: It may not stop if there are only delay centers
      // (not considered yet in the requirements).
      while (isValidRespTime(getSystemRespTime(numVisitsModule, workloadsModulesByCoresAndNumInstances, mus),
            respTimeRequirement)) {

         if (IS_DEBUG) {
            log.debug("Response time for workload " + (workload + incWorkload) + " is: "
                  + getSystemRespTime(numVisitsModule, workloadsModulesByCoresAndNumInstances, mus) + " and "
                  + "the performance requirement is " + respTimeRequirement);
         }
         incWorkload = incWorkload * 2.0;

         workloadsModules = getWorkloadsArray(routes, workload + incWorkload);
         numVisitsModule = getNumVisitsArray(workloadsModules, workload + incWorkload);

         // calculate the workload received by each core of a module: consider
         // both number of cores
         // of a cloud offer and number of instances for such module
         workloadsModulesByCoresAndNumInstances = weightModuleWorkloadByCoresAndNumInstances(workloadsModules, topology,
               sol, cloudCharacteristics);

      }

      // binary search between (floor(workload+incWorkload/2) ) and
      // (workload+incWorkload)
      double lowerWorkloadLimit = Math.floor(workload + (incWorkload / 2.0));
      double upperWorkloadLimit = workload + incWorkload;

      while (lowerWorkloadLimit + 2.0 < upperWorkloadLimit) {

         // TODO: I chose a value to add of 2.0 because the difference should be
         // 1.0 but there may be problems with the Double representation of
         // values. One
         // arrival more does not (should not) make any difference but the
         // comparison with
         // doubles works better.Check this for accurate version 2.0. Middle
         // point

         double workloadToCheck = (lowerWorkloadLimit + upperWorkloadLimit) / 2.0;
         workloadsModules = getWorkloadsArray(routes, workloadToCheck);
         numVisitsModule = getNumVisitsArray(workloadsModules, workloadToCheck);

         // calculate the workload received by each core of a module: consider
         // both number of cores
         // of a cloud offer and number of instances for such module
         workloadsModulesByCoresAndNumInstances = weightModuleWorkloadByCoresAndNumInstances(workloadsModules, topology,
               sol, cloudCharacteristics);

         // Set upper or lower limit according to binary search.
         double currentRespTime = getSystemRespTime(numVisitsModule, workloadsModulesByCoresAndNumInstances, mus);
         if (IS_DEBUG) {
            log.debug("Response time for workload " + workloadToCheck + " is: " + currentRespTime + " and "
                  + "the performance requirement is " + respTimeRequirement);
         }
         if (isValidRespTime(currentRespTime, respTimeRequirement)) {
            lowerWorkloadLimit = workloadToCheck;
         } else {
            upperWorkloadLimit = workloadToCheck;
         }
      }
      return lowerWorkloadLimit;
   }

   private boolean isValidRespTime(double rt, double rtreq) {
      return (rt >= 0) && (rt <= rtreq);
   }

}
