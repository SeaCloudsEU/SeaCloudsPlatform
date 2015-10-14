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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.platform.planner.optimizer.Solution;
import eu.seaclouds.platform.planner.optimizer.SuitableOptions;
import eu.seaclouds.platform.planner.optimizer.Topology;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityInformation;

public class Anneal extends AbstractHeuristic implements SearchMethod {

   private static final int MAX_ITERATIONS_NOT_CHANGE_STATE = 20;
   private static final double INITIAL_TEMPERATURE = 60.0; // MaxFitness... by
                                                           // convenience
   private static final double GEOM_TEMP_DECREMENT = 0.95;

   @SuppressWarnings("hiding")
   private static final boolean IS_DEBUG = AbstractHeuristic.IS_DEBUG || false;

   static Logger logAnneal = LoggerFactory.getLogger(Anneal.class);

   public Anneal(int maxIter) {
      super(maxIter);
   }

   public Anneal() {
   }

   @Override
   public Solution[] computeOptimizationProblem(SuitableOptions cloudOffers, QualityInformation requirements,
         Topology topology, int numPlansToGenerate) {

      cloudOffers.sortDescendingPerformance();
      Solution[] bestSols = super.findInitialRandomSolutions(cloudOffers, numPlansToGenerate);
      super.setFitnessOfSolutions(bestSols, requirements, topology, cloudOffers);
      if (IS_DEBUG) {
         logAnneal
               .debug("Start checking the presence of quality attached to solutions after the first generation ANNEAL");
         super.checkQualityAttachedToSolutions(bestSols);
      }
      super.sortSolutionsByFitness(bestSols);

      int numItersNoImprovement = 0;
      int currentIterNum = 1;
      while (numItersNoImprovement < super.getMaxIterNoImprove()) {
         // Maybe this value of MaxIterNoImprove is too high for theh Anneal
         // method

         Solution currentSol = super.findRandomSolution(cloudOffers);
         currentSol.setSolutionFitness(super.fitness(currentSol, requirements, topology, cloudOffers));
         // This method can decrease the fitness, so we will return the best
         // solution found along the way
         Solution bestSolSoFar = currentSol;

         int iterationsWithoutChangeState = 0;

         double temperature = INITIAL_TEMPERATURE;
         while (iterationsWithoutChangeState < MAX_ITERATIONS_NOT_CHANGE_STATE) {

            Solution neighbor = getRandomNeighbour(currentSol, cloudOffers, topology);
            neighbor.setSolutionFitness(super.fitness(neighbor, requirements, topology, cloudOffers));

            if (neighbourShouldBeSelected(currentSol.getSolutionFitness(), neighbor.getSolutionFitness(),
                  temperature)) {
               currentSol = neighbor;
               if (currentSol.getSolutionFitness() > bestSolSoFar.getSolutionFitness()) {
                  bestSolSoFar = currentSol;
               }
               iterationsWithoutChangeState = 0;
            } else {
               iterationsWithoutChangeState++;
            }

            currentIterNum++;
            temperature = getTemperature(currentIterNum, INITIAL_TEMPERATURE, temperature);
         }

         if (IS_DEBUG) {

            logAnneal.debug("Solution do not find suitable neighbors for a while. Solution found is: "
                  + bestSolSoFar.toString() + " with fitness " + bestSolSoFar.getSolutionFitness() + ". It took "
                  + currentIterNum + " iterations to find it and finished with temperature=" + temperature);

         }

         // Too many iterations without changing state. Try to include the the
         // best one found so far.
         //
         if (super.solutionShouldBeIncluded(bestSolSoFar, bestSols)) {
            super.insertOrdered(bestSols, bestSolSoFar);
            numItersNoImprovement = 0;

            if (IS_DEBUG) {
               logAnneal.debug("Found that solution " + bestSolSoFar.toString()
                     + " is to be included in the current list of solutions to return");

               logAnneal.debug(
                     "Start checking the presence of quality attached to solutions after adding a solution in ANNEAL");
               super.checkQualityAttachedToSolutions(bestSols);
            }

         } else {
            numItersNoImprovement++;
         }

         currentIterNum = 0;
      }

      return bestSols;

   }

   private double getTemperature(int currentIterNum, double initialTemperature, double temperature) {
      return temperature * GEOM_TEMP_DECREMENT;

   }

   protected boolean neighbourShouldBeSelected(double currentFitness, double neighborFitness, double temperature) {

      if (neighborFitness > currentFitness) {
         return true;
      }

      // neighbor is worse than current, calculate an exponential distribution
      double probability = Math.expm1(-1.0 * (currentFitness - neighborFitness) / temperature) + 1.0;
      double random = Math.random();
      return probability > random;

   }

   private Solution exploredSol;
   private Solution[] neighborsOfExplored;

   private Solution getRandomNeighbour(Solution currentSol, SuitableOptions cloudOffers, Topology topology) {

      if (exploredSol == null) {
         neighborsOfExplored = findNeighbors(currentSol, cloudOffers, topology);
         exploredSol = currentSol;
      }
      if (!currentSol.equals(exploredSol)) {
         neighborsOfExplored = findNeighbors(currentSol, cloudOffers, topology);
         exploredSol = currentSol;
      }
      // at this point in neighbors is the useful list of neighbours.

      int neighborIndexChosen = (int) Math.floor(Math.random() * (double) neighborsOfExplored.length);

      return neighborsOfExplored[neighborIndexChosen];

   }

   // TODO: The following code is repeated from HillClimb. A refactor will be
   // necessary.
   /**
    * @param currentSol
    * @param cloudOffers
    *           - Sorted by performance
    * @param topology
    * @return an array of solutuions that are neighbors of currentSol
    */
   private Solution[] findNeighbors(Solution currentSol, SuitableOptions cloudOffers, Topology topology) {

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

      return neighbors.toArray(new Solution[neighbors.size()]);
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

}
