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

package eu.seaclouds.platform.planner.optimizer.heuristics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.platform.planner.optimizer.Solution;
import eu.seaclouds.platform.planner.optimizer.SuitableOptions;
import eu.seaclouds.platform.planner.optimizer.Topology;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityAnalyzer;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityInformation;
import eu.seaclouds.platform.planner.optimizer.util.YAMLoptimizerParser;

public abstract class AbstractHeuristic {

   static Logger                  log                           = LoggerFactory
                                                                      .getLogger(AbstractHeuristic.class);

   private int                    MAX_ITER_NO_IMPROVE           = 200;
   private double                 MAX_TIMES_IMPROVE_REQUIREMENT = 20;
   private static final int       DEFAULT_MAX_NUM_INSTANCES     = 10;
   protected static final boolean IS_DEBUG                      = false;

   public AbstractHeuristic(int maxIter) {
      MAX_ITER_NO_IMPROVE = maxIter;

   }

   public AbstractHeuristic() {
   }

   public void setMaxIterNoImprove(int value) {
      MAX_ITER_NO_IMPROVE = value;
   }

   public int getMaxIterNoImprove() {
      return MAX_ITER_NO_IMPROVE;
   }

   private QualityInformation requirements = null;

   /**
    * @param bestSol
    * @param applicationMap
    * @param topology
    * @param cloudCharacteristics
    * @return the fitness value of the solution. If the solution does not
    *         satisfy the requirements, it returns -infty
    */
   public double fitness(Solution bestSol, Map<String, Object> applicationMap,
         Topology topology, SuitableOptions cloudCharacteristics) {

      if(requirements==null){
         loadQualityRequirements(applicationMap);
      }
      QualityAnalyzer qualityAnalyzer = new QualityAnalyzer();

      // calculates how well it satisfies performance reuquirement. Method
      // computePerformance returns a structure because, beyond response time
      // information, other performance-related information can be useful for
      // guiding the search method towards better solutions
      double perfGoodness = 1;
      if (requirements.existResponseTimeRequirement()) {
         perfGoodness = requirements.getResponseTime()
               / qualityAnalyzer.computePerformance(bestSol, topology,
                     requirements.getWorkload(), cloudCharacteristics)
                     .getResponseTime();
      }

      // calculates how well it satisfies availability reuquirement, if it
      // exists
      double availGoodness = 1;
      if (requirements.existAvailabilityRequirement()) {
         availGoodness = (1.0 - requirements.getAvailability())
               / (1.0 - qualityAnalyzer.computeAvailability(bestSol, topology,
                     cloudCharacteristics));
      }

      // calculates how well it satisfies cost reuquirement, if it exists
      double costGoodness = 1;
      if (requirements.existCostRequirement()) {
         costGoodness = requirements.getCost()
               / qualityAnalyzer.computeCost(bestSol, cloudCharacteristics);
      }

      double fitness = 0.0;
      if ((perfGoodness >= 1) && (availGoodness >= 1) && (costGoodness >= 1)) {
         fitness = Math.min(MAX_TIMES_IMPROVE_REQUIREMENT, perfGoodness)
               + Math.min(MAX_TIMES_IMPROVE_REQUIREMENT, availGoodness)
               + Math.min(MAX_TIMES_IMPROVE_REQUIREMENT, costGoodness);
      } else {
         // some requirement was not satisfied, so the solution cannot be
         // considered.
         // If a value of goodness is less than one it meant that the
         // requirement was specified but not satisfied;
         // The value of fitness returned should be between 0 and 1 to show this
         // fact.

         // Each of the requirements specified add a value to the fitness
         // between 0 and 1/numExistingRequirements
         double partialFitness = 0.0;
         double numExistingRequirements = 0.0;
         if (requirements.existResponseTimeRequirement()) {
            partialFitness += Math.min(MAX_TIMES_IMPROVE_REQUIREMENT,
                  perfGoodness);
            numExistingRequirements++;
         }
         if (requirements.existAvailabilityRequirement()) {
            partialFitness += Math.min(MAX_TIMES_IMPROVE_REQUIREMENT,
                  availGoodness);
            numExistingRequirements++;
         }
         if (requirements.existCostRequirement()) {
            partialFitness += Math.min(MAX_TIMES_IMPROVE_REQUIREMENT,
                  costGoodness);
            numExistingRequirements++;
         }

         if(qualityAnalyzer.getAllComputedQualities()==null){
            log.warn("something werid is happening because quality values are null");
         }

         fitness= partialFitness
               / (MAX_TIMES_IMPROVE_REQUIREMENT * numExistingRequirements++);
      }
      
      bestSol.setSolutionQuality(qualityAnalyzer.getAllComputedQualities());
      return fitness;
      
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
   public HashMap<String, ArrayList<Double>> createReconfigurationThresholds(
         Solution sol, Map<String, Object> applicationMap, Topology topology,
         SuitableOptions cloudCharacteristics) {

      if (IS_DEBUG) {
         log.debug("Starting the creation of reconfiguration thresholds");
      }

      loadQualityRequirements(applicationMap);
      QualityAnalyzer qualityAnalyzer = new QualityAnalyzer();

      // if the solution does not satisfy the performance requirements, nothing
      // to do
      double perfGoodness = requirements.getResponseTime()
            / qualityAnalyzer.computePerformance(sol, topology,
                  requirements.getWorkload(), cloudCharacteristics)
                  .getResponseTime();

      if ((requirements.existResponseTimeRequirement())
            && (perfGoodness >= 1.0)) {// response time requirements are
                                       // satisfied if perfGoodness>=1.0

         // A HashMap with all the keys of module names, and associated an
         // arraylist with the thresholds for reconfigurations.
         HashMap<String, ArrayList<Double>> thresholds = new HashMap<String, ArrayList<Double>>();

         thresholds = qualityAnalyzer.computeThresholds(sol, topology,
               requirements, cloudCharacteristics);

         if (IS_DEBUG) {
            log.debug("Finishing the creation of reconfiguration thresholds");
         }
         return thresholds;
      } else {// There are not performance requirements, so no thresholds are
              // created.
         log.debug("Finishing the creation of reconfiguration thresholds because there were not performance requirements");
         return null;
      }

   }

   private void loadQualityRequirements(Map<String, Object> applicationMap) {
      if (requirements == null) {
         requirements = YAMLoptimizerParser
               .getQualityRequirements(applicationMap);
      }
      // Maybe the previous operation did not work because Requirements could
      // not be found in the YAML. Follow an ad-hoc solution to get some
      // requirements
      if (requirements == null) {
         log.error("Quality requirements not found in the input document. Loading dummy quality requirements for testing purposes");
         requirements = YAMLoptimizerParser.getQualityRequirementsForTesting();

      }

      if (requirements.existResponseTimeRequirement()) {
         loadWorkload(applicationMap);
      }

   }

   private void loadWorkload(Map<String, Object> applicationMap) {
      if (requirements.getWorkload() <= 0.0) {
         requirements.setWorkloadMinute(YAMLoptimizerParser
               .getApplicationWorkload(applicationMap));
      }
      // Maybe the previous operation did not work correctly because the
      // workload could not be found in the YAML. Follow an ad-hoc solution to
      // get some requirements
      if (!requirements.hasValidWorkload()) {
         log.error("Valid workload information not found in the input document. Loading dummy quality requirements for testing purposes");
         requirements.setWorkloadMinute(YAMLoptimizerParser
               .getApplicationWorkloadTest());
      }

   }

   public void addSolutionToAppMap(Solution currentSol,
         Map<String, Object> applicationMap) {

      for (String solkey : currentSol) {

         YAMLoptimizerParser
               .CleanSuitableOfferForModule(solkey, applicationMap);

         YAMLoptimizerParser.AddSuitableOfferForModule(solkey,
               currentSol.getCloudOfferNameForModule(solkey),
               currentSol.getCloudInstancesForModule(solkey), applicationMap);

         YAMLoptimizerParser.AddQualityOfSolution(currentSol, applicationMap);
      }

   }

   protected Solution[] mergeBestSolutions(Solution[] sols1, Solution[] sols2,
         int numPlansToGenerate) {
      // TODO: this method has never been tested
      sortSolutionsByFitness(sols1);
      sortSolutionsByFitness(sols2);

      Solution[] merged = new Solution[numPlansToGenerate];

      int index1 = 0;
      int index2 = 0;

      for (int i = 0; i < merged.length; i++) {

         if ((index1 < sols1.length) && (index2 < sols2.length)) {
            if (sols1[index1].getSolutionFitness() >= sols2[index2]
                  .getSolutionFitness()) {
               merged[i] = sols1[index1].clone();
               index1++;
            } else {
               merged[i] = sols2[index2].clone();
               index2++;
            }
         } else {
            if ((index1 >= sols1.length) && (index2 < sols2.length)) {
               merged[i] = sols2[index2].clone();
               index2++;
            }
            if ((index1 < sols1.length) && (index2 >= sols2.length)) {
               merged[i] = sols1[index1].clone();
               index1++;
            }

         }

      }
      return merged;
   }

   protected void sortSolutionsByFitness(Solution[] bestSols) {
      Arrays.sort(bestSols, Collections.reverseOrder());
   }

   protected void setFitnessOfSolutions(Solution[] bestSols,
         Map<String, Object> applicationMap, Topology topology,
         SuitableOptions cloudOffers) {
      for (int solindex = 0; solindex < bestSols.length; solindex++) {
         bestSols[solindex].setSolutionFitness(fitness(bestSols[solindex],
               applicationMap, topology, cloudOffers));
      }
   }

   /*
    * After some reading of Cloneable, I prefered to call directly the clone
    * method of solutions one by one, instead of using the clone() methods of
    * Arrays, which I was not sure what clone() methods of its elements was
    * going to invoke.
    */
   protected Solution[] cloneSolutions(Solution[] old) {

      Solution[] news = new Solution[old.length];
      for (int i = 0; i < old.length; i++) {
         news[i] = old[i].clone();
      }

      return news;

   }

   protected double getMinimumFitnessOfSolutions(Solution[] solutions) {

      return getSolutionWithMinimumFitness(solutions).getSolutionFitness();

   }

   protected double getMaximumFitnessOfSolutions(Solution[] solutions) {
      return getSolutionWithMaximumFitness(solutions).getSolutionFitness();
   }

   protected Solution getSolutionWithMaximumFitness(Solution[] solutions) {
      Solution maxFitSol = solutions[0];
      for (int i = 0; i < solutions.length; i++) {
         if (solutions[i].getSolutionFitness() > maxFitSol.getSolutionFitness()) {
            maxFitSol = solutions[i];
         }
      }

      return maxFitSol;

   }

   protected Solution getSolutionWithMinimumFitness(Solution[] solutions) {
      Solution minFitSol = solutions[0];
      for (int i = 0; i < solutions.length; i++) {
         if (solutions[i].getSolutionFitness() < minFitSol.getSolutionFitness()) {
            minFitSol = solutions[i];
         }
      }
      return minFitSol;
   }

   protected void insertOrdered(Solution[] bestSols, Solution solution) {

      if (solution.getSolutionFitness() < bestSols[bestSols.length - 1]
            .getSolutionFitness()) {
         return;
      }

      int currentPos = bestSols.length - 1;
      while ((currentPos > 0)
            && (bestSols[currentPos - 1].getSolutionFitness() <= solution
                  .getSolutionFitness())) {
         bestSols[currentPos] = bestSols[currentPos - 1];
         currentPos--;
      }

      bestSols[currentPos] = solution;

   }

   protected Map<String, Object>[] hashMapOfFoundSolutionsWithThresholds(
         Solution[] bestSols, Map<String, Object> applicMap, Topology topology,
         SuitableOptions cloudOffers, int numPlansToGenerate) {

      if(AbstractHeuristic.IS_DEBUG){
         checkQualityAttachedToSolutions(bestSols);
      }
      @SuppressWarnings("unchecked")
      Map<String, Object>[] solutions = new HashMap[numPlansToGenerate];

      for (int i = 0; i < bestSols.length; i++) {

         Map<String, Object> baseAppMap = YAMLoptimizerParser
               .cloneYAML(applicMap);

         addSolutionToAppMap(bestSols[i], baseAppMap);

         HashMap<String, ArrayList<Double>> thresholds = createReconfigurationThresholds(
               bestSols[i], baseAppMap, topology, cloudOffers);
         YAMLoptimizerParser.AddReconfigurationThresholds(thresholds,
               baseAppMap);

         solutions[i] = baseAppMap;
      }
      return solutions;
   }

   protected boolean solutionShouldBeIncluded(Solution sol, Solution[] sols) {
      return (sol.getSolutionFitness() > getMinimumFitnessOfSolutions(sols))
            && (!sol.isContainedIn(sols));
   }

   protected Solution findRandomSolution(SuitableOptions cloudOffers,
         Map<String, Object> applicationMap) {
      Solution currentSolution = new Solution();
      for (String modName : cloudOffers.getStringIterator()) {

         // element to use
         int itemToUse = (int) Math.floor(Math.random()
               * (double) cloudOffers.getSizeOfSuitableOptions(modName));

         // number of instances
         int numInstances = ((int) Math.floor(Math.random()
               * ((double) DEFAULT_MAX_NUM_INSTANCES))) + 1;

         currentSolution.addItem(modName, cloudOffers
               .getIthSuitableOptionForModuleName(modName, itemToUse),
               numInstances);
      }

      return currentSolution;
   }

   protected void checkQualityAttachedToSolutions(Solution[] bestSols) {
      
      for(int i=0; i<bestSols.length; i++){
         if(bestSols[i].getSolutionQuality()==null){
            log.info("Solution has its quality NULL" + bestSols[i].toString());
         }
      }
      
   }

}
