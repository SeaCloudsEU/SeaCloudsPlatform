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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.platform.planner.optimizer.Solution;
import eu.seaclouds.platform.planner.optimizer.SuitableOptions;
import eu.seaclouds.platform.planner.optimizer.Topology;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityInformation;

public class HillClimb extends AbstractHeuristic implements SearchMethod {

   @SuppressWarnings("hiding")
   private static boolean IS_DEBUG = AbstractHeuristic.IS_DEBUG || false;

   static Logger                logHill  = LoggerFactory
                                               .getLogger(HillClimb.class);

   public HillClimb() {
      super();

   }

   public HillClimb(int maxIter) {
      super(maxIter);
   }
   


 
   
   /*
    * (non-Javadoc)
    * 
    * @see eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethod#
    * computeOptimalSolution
    * (eu.seaclouds.platform.planner.optimizer.SuitableOptions, java.util.Map)
    */
   @Override
   public Solution[] computeOptimizationProblem(
         SuitableOptions cloudOffers, QualityInformation requirements,
         Topology topology, int numPlansToGenerate) {

      // To findSolution method, we pass an Empty solution instead of a null
      // value to or create a new method that does not consider the current one.
      // This way may help for replanning, when even the first attempt for
      // solution will be based on the current deployment

      cloudOffers.sortDescendingPerformance();
      Solution[] bestSols = super.findInitialRandomSolutions(cloudOffers,
            numPlansToGenerate);
      super.setFitnessOfSolutions(bestSols, requirements, topology,
            cloudOffers);
      if (IS_DEBUG) {
         logHill
               .debug("Start checking the presence of quality attached to solutions after the first generation HILLCLIMB");
         super.checkQualityAttachedToSolutions(bestSols);
      }
      super.sortSolutionsByFitness(bestSols);

      int numItersNoImprovement = 0;
      while (numItersNoImprovement < super.getMaxIterNoImprove()) { // each
                                                                    // iteration
                                                                    // finds a
                                                                    // peak

         Solution currentSol = super.findRandomSolution(cloudOffers);
         currentSol.setSolutionFitness(super.fitness(currentSol,
               requirements, topology, cloudOffers));

         boolean neighborsImprove = true;
         int pathLengthTraversed = 0;
         while (neighborsImprove) {
            Solution[] candidates = findNeighbors(currentSol, cloudOffers, topology);
            if (IS_DEBUG) {
               logHill.debug("Found " + candidates.length
                     + " neighbors of the solution: Are "
                     + Arrays.toString(candidates));
            }
            super.setFitnessOfSolutions(candidates, requirements, topology,
                  cloudOffers);
            Solution bestCandidate = super
                  .getSolutionWithMaximumFitness(candidates);
            if (bestCandidate.getSolutionFitness() > currentSol
                  .getSolutionFitness()) {
               currentSol = bestCandidate;
               pathLengthTraversed++;
               if (IS_DEBUG) {
                  if (pathLengthTraversed > 100) {
                     logHill.debug("Something weird is happening, iteration: "
                           + pathLengthTraversed
                           + "without finding local maxima: Fitness: "
                           + currentSol.getSolutionFitness() + " Solution: "
                           + currentSol.toString());
                  }
               }
            } else {// there is not any better neighbor. See if this solution
                    // can be included among the set of best solutions
               neighborsImprove = false;
               pathLengthTraversed = 0;
            }
         }

         // We have a local maxima in currentSol, let's see if it is better than
         // the previous maxima.
         if (super.solutionShouldBeIncluded(currentSol, bestSols)) {

            if (IS_DEBUG) {
               logHill
                     .debug("After "
                           + numItersNoImprovement
                           + " iterations we have found a local maxima solution that is going to be included: "
                           + currentSol.toString());
            }
            super.insertOrdered(bestSols, currentSol);
            numItersNoImprovement = 0;

            if (IS_DEBUG) {
               logHill
                     .debug("Start checking the presence of quality attached to solutions after adding a solution in HILLCLIMB");
               super.checkQualityAttachedToSolutions(bestSols);
            }

         } else {
            if (IS_DEBUG) {
               logHill
                     .debug("After "
                           + numItersNoImprovement
                           + " iterations we have found a local maxima, but it will not be included: "
                           + currentSol.toString());
            }
            numItersNoImprovement++;
         }

      }

      if (IS_DEBUG) {
         logHill
               .debug("Start checking the presence of quality attached to solutions after adding a solution in HILLCLIMB");
         super.checkQualityAttachedToSolutions(bestSols);
      }

      return bestSols; 

   }
   
   
   
   
   
   /*
    * (non-Javadoc)
    * 
    * @see eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethod#
    * computeOptimalSolution
    * (eu.seaclouds.platform.planner.optimizer.SuitableOptions, java.util.Map)
    */
/*   @Override
   public Map<String, Object>[] computeOptimizationProblem(
         SuitableOptions cloudOffers, Map<String, Object> applicationMap,
         Topology topology, int numPlansToGenerate) {

      // To findSolution method, we pass an Empty solution instead of a null
      // value to or create a new method that does not consider the current one.
      // This way may help for replanning, when even the first attempt for
      // solution will be based on the current deployment

      cloudOffers.sortDescendingPerformance();
      Solution[] bestSols = super.findInitialRandomSolutions(cloudOffers,
            numPlansToGenerate);
      super.setFitnessOfSolutions(bestSols, applicationMap, topology,
            cloudOffers);
      if (IS_DEBUG) {
         logHill
               .debug("Start checking the presence of quality attached to solutions after the first generation HILLCLIMB");
         super.checkQualityAttachedToSolutions(bestSols);
      }
      super.sortSolutionsByFitness(bestSols);

      int numItersNoImprovement = 0;
      while (numItersNoImprovement < super.getMaxIterNoImprove()) { // each
                                                                    // iteration
                                                                    // finds a
                                                                    // peak

         Solution currentSol = super.findRandomSolution(cloudOffers);
         currentSol.setSolutionFitness(super.fitness(currentSol,
               applicationMap, topology, cloudOffers));

         boolean neighborsImprove = true;
         int pathLengthTraversed = 0;
         while (neighborsImprove) {
            Solution[] candidates = findNeighbors(currentSol, cloudOffers,
                  applicationMap, topology);
            if (IS_DEBUG) {
               logHill.debug("Found " + candidates.length
                     + " neighbors of the solution: Are "
                     + Arrays.toString(candidates));
            }
            super.setFitnessOfSolutions(candidates, applicationMap, topology,
                  cloudOffers);
            Solution bestCandidate = super
                  .getSolutionWithMaximumFitness(candidates);
            if (bestCandidate.getSolutionFitness() > currentSol
                  .getSolutionFitness()) {
               currentSol = bestCandidate;
               pathLengthTraversed++;
               if (IS_DEBUG) {
                  if (pathLengthTraversed > 100) {
                     logHill.debug("Something weird is happening, iteration: "
                           + pathLengthTraversed
                           + "without finding local maxima: Fitness: "
                           + currentSol.getSolutionFitness() + " Solution: "
                           + currentSol.toString());
                  }
               }
            } else {// there is not any better neighbor. See if this solution
                    // can be included among the set of best solutions
               neighborsImprove = false;
               pathLengthTraversed = 0;
            }
         }

         // We have a local maxima in currentSol, let's see if it is better than
         // the previous maxima.
         if (super.solutionShouldBeIncluded(currentSol, bestSols)) {

            if (IS_DEBUG) {
               logHill
                     .debug("After "
                           + numItersNoImprovement
                           + " iterations we have found a local maxima solution that is going to be included: "
                           + currentSol.toString());
            }
            super.insertOrdered(bestSols, currentSol);
            numItersNoImprovement = 0;

            if (IS_DEBUG) {
               logHill
                     .debug("Start checking the presence of quality attached to solutions after adding a solution in HILLCLIMB");
               super.checkQualityAttachedToSolutions(bestSols);
            }

         } else {
            if (IS_DEBUG) {
               logHill
                     .debug("After "
                           + numItersNoImprovement
                           + " iterations we have found a local maxima, but it will not be included: "
                           + currentSol.toString());
            }
            numItersNoImprovement++;
         }

      }

      if (IS_DEBUG) {
         logHill
               .debug("Start checking the presence of quality attached to solutions after adding a solution in HILLCLIMB");
         super.checkQualityAttachedToSolutions(bestSols);
      }

      return super.hashMapOfFoundSolutionsWithThresholds(bestSols,
            applicationMap, topology, cloudOffers, numPlansToGenerate);

   }*/

   /**
    * @param currentSol
    * @param cloudOffers
    *           - Sorted by performance
    * @param topology
    * @return an array of solutuions that are neighbors of currentSol
    */
   private Solution[] findNeighbors(Solution currentSol,
         SuitableOptions cloudOffers, 
         Topology topology) {

      ArrayList<Solution> neighbors = new ArrayList<Solution>();
      // neighbors are:

      // For each module
      for (String modulename : currentSol) {

         // Same cloud provider and same machines, change the number of
         // instances of one module (+1 o -1 (in case there is more than one))
         findNeighborsByNumInstances(neighbors, currentSol, modulename,
               topology);

         // Same cloud provider, change the type of machine with the same
         // instances (+1 o -1 if sorted by peformance or cost)
         // Here is the thing of single step or two steps heuristic
         findNeighborsByTypeInstanceInSameProvider(neighbors, currentSol,
               modulename, cloudOffers);

         // Change the cloud provider (to +1 -1 in availability) and choose the
         // most similar type of machines for them as the currently
         // used (1 st step of the current) for performance or cost
         findNeighborsByCloudProvider(neighbors, currentSol, modulename,
               cloudOffers);
      }

      return neighbors.toArray(new Solution[neighbors.size()]);
   }

   private void findNeighborsByCloudProvider(ArrayList<Solution> neighbors,
         Solution currentSol, String modulename, SuitableOptions cloudOffers) {

      Solution neighborSol;
      String currentCloudOffer = currentSol
            .getCloudOfferNameForModule(modulename);
      // +1
      if (cloudOffers
            .existsAlternativeCloudProviderForModuleWithHigherAvailability(
                  modulename, currentCloudOffer)) {
         neighborSol = currentSol.clone();
         neighborSol
               .modifyCloudOfferOfModule(
                     modulename,
                     cloudOffers
                           .getOfferImmediateHigherAvailabilityOfSameProviderSimilarPerformance(
                                 modulename, currentCloudOffer));
         neighbors.add(neighborSol);
      }
      // -1
      if (cloudOffers
            .existsAlternativeCloudProviderForModuleWithLowerAvailability(
                  modulename, currentCloudOffer)) {
         neighborSol = currentSol.clone();
         neighborSol
               .modifyCloudOfferOfModule(
                     modulename,
                     cloudOffers
                           .getOfferImmediateLowerAvailabilityOfSameProviderSimilarPerformance(
                                 modulename, currentCloudOffer));
         neighbors.add(neighborSol);
      }
   }

   private void findNeighborsByTypeInstanceInSameProvider(
         ArrayList<Solution> neighbors, Solution currentSol, String modulename,
         SuitableOptions cloudOffers) {

      Solution neighborSol;
      String currentCloudOffer = currentSol
            .getCloudOfferNameForModule(modulename);
      // +1
      if (cloudOffers.existsOfferWithBetterPerformanceOfSameProvider(
            modulename, currentCloudOffer)) {
         neighborSol = currentSol.clone();

         neighborSol.modifyCloudOfferOfModule(modulename, cloudOffers
               .getOfferImmediateHigherPerformanceOfSameProvider(modulename,
                     currentCloudOffer));
         neighbors.add(neighborSol);
      }
      // -1
      if (cloudOffers.existsOfferWithWorsePerformanceOfSameProvider(modulename,
            currentCloudOffer)) {
         neighborSol = currentSol.clone();
         neighborSol.modifyCloudOfferOfModule(modulename, cloudOffers
               .getOfferImmediateLowerPerformanceOfSameProvider(modulename,
                     currentCloudOffer));
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
   private void findNeighborsByNumInstances(ArrayList<Solution> neighbors,
         Solution currentSol, String modulename, Topology topology) {
      Solution neighborSol;

      // +1
      // TODO: Here it should be checked if there is a maximum number of
      // replicas allowed for a module (see calculation thresholds)
      // At this point it is a boolean that specifies whether a module can scale
      // out.
      if (topology.getModule(modulename).canScale()) {
         neighborSol = currentSol.clone();
         neighborSol.modifyNumInstancesOfModule(modulename,
               currentSol.getCloudInstancesForModule(modulename) + 1);
         neighbors.add(neighborSol);
      }

      // -1
      if (currentSol.getCloudInstancesForModule(modulename) > 1) {
         neighborSol = currentSol.clone();
         neighborSol.modifyNumInstancesOfModule(modulename,
               currentSol.getCloudInstancesForModule(modulename) - 1);
         neighbors.add(neighborSol);
      }

   }

}
