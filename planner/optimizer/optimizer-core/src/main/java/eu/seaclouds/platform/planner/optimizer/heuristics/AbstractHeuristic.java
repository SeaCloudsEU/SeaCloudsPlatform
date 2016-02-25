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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.platform.planner.optimizer.Solution;
import eu.seaclouds.platform.planner.optimizer.SuitableOptions;
import eu.seaclouds.platform.planner.optimizer.Topology;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityAnalyzer;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityInformation;

public abstract class AbstractHeuristic {

   static Logger log = LoggerFactory.getLogger(AbstractHeuristic.class);

   private int MAX_ITER_NO_IMPROVE = 200;
   private double           MAX_TIMES_IMPROVE_REQUIREMENT = 20;
   private static final int DEFAULT_MAX_NUM_INSTANCES     = 10;

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
   public double fitness(Solution bestSol, QualityInformation qosRequirements, Topology topology,
         SuitableOptions cloudCharacteristics) {

      if (requirements == null) {
         requirements = qosRequirements;
      }
      QualityAnalyzer qualityAnalyzer = new QualityAnalyzer();

      // calculates how well it satisfies performance reuquirement. Method
      // computePerformance returns a structure because, beyond response time
      // information, other performance-related information can be useful for
      // guiding the search method towards better solutions
      double perfGoodness = 1;
      if (requirements.existResponseTimeRequirement()) {

         double computedPerformance = qualityAnalyzer
               .computePerformance(bestSol, topology, requirements.getWorkload(), cloudCharacteristics)
               .getResponseTime();
         perfGoodness = requirements.getResponseTime() / computedPerformance;

         if (log.isDebugEnabled()) {
            log.debug("Candidate Solution " + bestSol.toString() + " evaluated gave a response time of "
                  + computedPerformance + " while the requirements were " + requirements.getResponseTime()
                  + " and the workload was " + requirements.getWorkload());
         }
      }

      // calculates how well it satisfies availability reuquirement, if it
      // exists
      double availGoodness = 1;
      if (requirements.existAvailabilityRequirement()) {
         availGoodness = (1.0 - requirements.getAvailability())
               / (1.0 - qualityAnalyzer.computeAvailability(bestSol, topology, cloudCharacteristics));
      }

      // calculates how well it satisfies cost reuquirement, if it exists
      double costGoodness = 1;
      if (requirements.existCostRequirement()) {
         costGoodness = requirements.getCostHour() / qualityAnalyzer.computeCost(bestSol, cloudCharacteristics);
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
         double numSatisfiedRequirements = 0.0;
         if (requirements.existResponseTimeRequirement()) {
            numExistingRequirements++;
            if (perfGoodness >= 1) {
               numSatisfiedRequirements++;
            } else {
               partialFitness += perfGoodness;
            }
         }
         if (requirements.existAvailabilityRequirement()) {

            numExistingRequirements++;
            if (availGoodness >= 1) {
               numSatisfiedRequirements++;
            } else {
               partialFitness += availGoodness;
            }
         }
         if (requirements.existCostRequirement()) {

            numExistingRequirements++;
            if (costGoodness >= 1) {
               numSatisfiedRequirements++;
            } else {
               partialFitness += costGoodness;
            }
         }

         if (qualityAnalyzer.getAllComputedQualities() == null) {
            log.warn("something werid is happening because quality values are null");
         }

         // satisfied reqs fill at maximum their slot.
         // The rest of slots are filled by the proportion of global closeness
         // to the solution
         fitness = numSatisfiedRequirements / numExistingRequirements
               + (partialFitness * (1.0 - numSatisfiedRequirements / numExistingRequirements))
                     / (numExistingRequirements - numSatisfiedRequirements);

      }

      bestSol.setSolutionQuality(qualityAnalyzer.getAllComputedQualities());
      return fitness;

   }

   protected Solution[] mergeBestSolutions(Solution[] sols1, Solution[] sols2, int numPlansToGenerate) {
      // TODO: this method has never been tested

      sortSolutionsByFitness(sols1);
      sortSolutionsByFitness(sols2);

      Solution[] merged = new Solution[numPlansToGenerate];

      int index1 = 0;
      int index2 = 0;

      for (int i = 0; i < merged.length; i++) {

         if ((index1 < sols1.length) && (index2 < sols2.length)) {
            if (sols1[index1].getSolutionFitness() >= sols2[index2].getSolutionFitness()) {
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

   protected void setFitnessOfSolutions(Solution[] bestSols, QualityInformation requirements, Topology topology,
         SuitableOptions cloudOffers) {
      for (int solindex = 0; solindex < bestSols.length; solindex++) {
         bestSols[solindex].setSolutionFitness(fitness(bestSols[solindex], requirements, topology, cloudOffers));
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

      if (solution.getSolutionFitness() < bestSols[bestSols.length - 1].getSolutionFitness()) {
         return;
      }

      int currentPos = bestSols.length - 1;
      while ((currentPos > 0) && (bestSols[currentPos - 1].getSolutionFitness() <= solution.getSolutionFitness())) {
         bestSols[currentPos] = bestSols[currentPos - 1];
         currentPos--;
      }

      bestSols[currentPos] = solution;

   }

   protected boolean solutionShouldBeIncluded(Solution sol, Solution[] sols) {
      return (sol.getSolutionFitness() > getMinimumFitnessOfSolutions(sols)) && (!sol.isContainedIn(sols));
   }

   protected Solution findRandomSolution(SuitableOptions cloudOffers, Topology topology) {
      Solution currentSolution = new Solution();
      for (String modName : cloudOffers.getStringIterator()) {

         // element to use
         int itemToUse = (int) Math.floor(Math.random() * (double) cloudOffers.getSizeOfSuitableOptions(modName));

         // number of instances
         int numInstances = 1;
         if (topology.getModule(modName).canScale()) {
            numInstances = ((int) Math.floor(Math.random() * ((double) DEFAULT_MAX_NUM_INSTANCES))) + 1;
         }

         currentSolution.addItem(modName, cloudOffers.getIthSuitableOptionForModuleName(modName, itemToUse),
               numInstances);
      }

      return currentSolution;
   }

   public void checkQualityAttachedToSolutions(Solution[] solutions) {

      for (int i = 0; i < solutions.length; i++) {
         if (solutions[i].getSolutionQuality() == null) {
            log.debug("Solution has its quality NULL" + solutions[i].toString());
         }
      }

   }

   protected Solution[] findInitialRandomSolutions(SuitableOptions cloudOffers, int numPlansToGenerate,
         Topology topology) {

      Solution[] newSolutions = new Solution[numPlansToGenerate];

      for (int newSolIndex = 0; newSolIndex < newSolutions.length; newSolIndex++) {

         newSolutions[newSolIndex] = findRandomSolution(cloudOffers, topology);
      }

      return newSolutions;

   }

   /**
    * @param currentSol
    * @param cloudOffers
    *           - Sorted by performance
    * @param topology
    * @return an array of solutuions that are neighbors of currentSol
    */
   protected Solution[] findNeighbors(Solution currentSol, SuitableOptions cloudOffers, Topology topology) {

      ArrayList<Solution> neighbors = new ArrayList<Solution>();
      // neighbors are:

      // For each module
      for (String modulename : currentSol) {

         // Same cloud provider and same machines, change the number of
         // instances of one module (+1 o -1 (in case there is more than one))
         findNeighborsByNumInstances(neighbors, currentSol, modulename, topology);

         // Same cloud provider, change the type of machine with the same
         // instances (+1 o -1 if sorted by peformance or cost)
         // Here is the thing of single step or two steps heuristic
         findNeighborsByTypeInstanceInSameProvider(neighbors, currentSol, modulename, cloudOffers);

         // Change the cloud provider (to +1 -1 in availability) and choose the
         // most similar type of machines for them as the currently
         // used (1 st step of the current) for performance or cost
         findNeighborsByCloudProvider(neighbors, currentSol, modulename, cloudOffers);
      }

      if (neighbors.size() > 0) {
         return neighbors.toArray(new Solution[neighbors.size()]);
      } else {
         return null;
      }
   }

   private void findNeighborsByCloudProvider(ArrayList<Solution> neighbors, Solution currentSol, String modulename,
         SuitableOptions cloudOffers) {

      Solution neighborSol;
      String currentCloudOffer = currentSol.getCloudOfferNameForModule(modulename);
      // +1
      if (cloudOffers.existsAlternativeCloudProviderForModuleWithHigherAvailability(modulename, currentCloudOffer)) {
         neighborSol = currentSol.clone();
         neighborSol.modifyCloudOfferOfModule(modulename, cloudOffers
               .getOfferImmediateHigherAvailabilityOfSameProviderSimilarPerformance(modulename, currentCloudOffer));
         neighbors.add(neighborSol);
      }
      // -1
      if (cloudOffers.existsAlternativeCloudProviderForModuleWithLowerAvailability(modulename, currentCloudOffer)) {
         neighborSol = currentSol.clone();
         neighborSol.modifyCloudOfferOfModule(modulename, cloudOffers
               .getOfferImmediateLowerAvailabilityOfSameProviderSimilarPerformance(modulename, currentCloudOffer));
         neighbors.add(neighborSol);
      }
   }

   private void findNeighborsByTypeInstanceInSameProvider(ArrayList<Solution> neighbors, Solution currentSol,
         String modulename, SuitableOptions cloudOffers) {

      Solution neighborSol;
      String currentCloudOffer = currentSol.getCloudOfferNameForModule(modulename);
      // +1
      if (cloudOffers.existsOfferWithBetterPerformanceOfSameProvider(modulename, currentCloudOffer)) {
         neighborSol = currentSol.clone();

         neighborSol.modifyCloudOfferOfModule(modulename,
               cloudOffers.getOfferImmediateHigherPerformanceOfSameProvider(modulename, currentCloudOffer));
         neighbors.add(neighborSol);
      }
      // -1
      if (cloudOffers.existsOfferWithWorsePerformanceOfSameProvider(modulename, currentCloudOffer)) {
         neighborSol = currentSol.clone();
         neighborSol.modifyCloudOfferOfModule(modulename,
               cloudOffers.getOfferImmediateLowerPerformanceOfSameProvider(modulename, currentCloudOffer));
         neighbors.add(neighborSol);
      }

   }

   /**
    * @param neighbors
    * @param currentSol
    * @param modulename
    *           Uses same cloud provider and same machines, change the number of
    *           instances of one module (+1 o -1 (in case there is more than
    *           one))
    * @param topology
    */
   private void findNeighborsByNumInstances(ArrayList<Solution> neighbors, Solution currentSol, String modulename,
         Topology topology) {
      Solution neighborSol;

      // +1
      // TODO: Here it should be checked if there is a maximum number of
      // replicas allowed for a module (see calculation thresholds)
      // At this point it is a boolean that specifies whether a module can scale
      // out.
      if (topology.getModule(modulename).canScale()) {
         neighborSol = currentSol.clone();
         try {
            neighborSol.modifyNumInstancesOfModule(modulename, currentSol.getCloudInstancesForModule(modulename) + 1);
         } catch (Exception E) {
            // exception getting the cloud instances for module. Setting to 0.
            neighborSol.modifyNumInstancesOfModule(modulename, 0);
         }
         neighbors.add(neighborSol);
      }

      // -1
      try {
         if (currentSol.getCloudInstancesForModule(modulename) > 1) {
            neighborSol = currentSol.clone();
            neighborSol.modifyNumInstancesOfModule(modulename, currentSol.getCloudInstancesForModule(modulename) - 1);
            neighbors.add(neighborSol);
         }
      } catch (Exception E) {// nothing to do

      }

   }

   protected Solution[] filterUniqueSolutions(Solution[] sols) {
      int duplicatedElements = 0;

      // remove one by one from sols and check whether sols still contains the
      // removed one.
      for (int i = 0; i < sols.length; i++) {
         Solution current = sols[i];
         sols[i] = null;

         if (current.isContainedIn(sols)) {
            duplicatedElements++;
         } else {
            sols[i] = current;
         }
      }

      // now sols[] contains only different solutions and null values. Remove
      // the nulls.
      Solution[] newSols = new Solution[sols.length - duplicatedElements];
      int newSolsIndex = 0;
      for (int i = 0; i < sols.length; i++) {
         if (sols[i] != null) {
            newSols[newSolsIndex] = sols[i];
            newSolsIndex++;
         }
      }
      if (newSolsIndex != newSols.length) {
         log.warn(
               "Something weird happened removing duplicated solutions: the number of different solutions were {} but they are only returned {}",
               newSols.length, newSolsIndex);
      }
      return newSols;
   }

}
