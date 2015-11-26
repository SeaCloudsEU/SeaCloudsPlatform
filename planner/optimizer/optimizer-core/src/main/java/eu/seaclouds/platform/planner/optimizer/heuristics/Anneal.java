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
      Solution[] bestSols = findInitialRandomSolutions(cloudOffers, numPlansToGenerate, topology);
      super.setFitnessOfSolutions(bestSols, requirements, topology, cloudOffers);
      if (logAnneal.isDebugEnabled()) {
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

         Solution currentSol = super.findRandomSolution(cloudOffers, topology);
         currentSol.setSolutionFitness(super.fitness(currentSol, requirements, topology, cloudOffers));
         // This method can decrease the fitness, so we will return the best
         // solution found along the way
         Solution bestSolSoFar = currentSol;

         int iterationsWithoutChangeState = 0;

         double temperature = INITIAL_TEMPERATURE;
         while (iterationsWithoutChangeState < MAX_ITERATIONS_NOT_CHANGE_STATE) {

            logAnneal.debug("iterationsWithoutchange=" + iterationsWithoutChangeState + " and numItersNoImprovement="
                  + numItersNoImprovement);

            Solution neighbor = getRandomNeighbour(currentSol, cloudOffers, topology);
            // neighbor may be null if it did not find any other possible
            // available.
            if (neighbor == null) {
               logAnneal.debug("Not found neighbors for solution " + currentSol);
               iterationsWithoutChangeState++;
            } else {
               neighbor.setSolutionFitness(super.fitness(neighbor, requirements, topology, cloudOffers));

               if (neighbourShouldBeSelected(currentSol.getSolutionFitness(), neighbor.getSolutionFitness(),
                     temperature)) {

                  logAnneal.debug("Changing from solution: " + currentSol.toString());
                  logAnneal.debug(" to solution: " + neighbor.toString());

                  currentSol = neighbor;
                  if (currentSol.getSolutionFitness() > bestSolSoFar.getSolutionFitness()) {
                     bestSolSoFar = currentSol;
                  }
                  iterationsWithoutChangeState = 0;
               } else {
                  iterationsWithoutChangeState++;
               }
            }
            currentIterNum++;
            temperature = getTemperature(currentIterNum, INITIAL_TEMPERATURE, temperature);

         }

         logAnneal.debug("Solution do not find suitable neighbors for a while. Solution found is: "
               + bestSolSoFar.toString() + " with fitness " + bestSolSoFar.getSolutionFitness() + ". It took "
               + currentIterNum + " iterations to find it and finished with temperature=" + temperature);

         // Too many iterations without changing state. Try to include the the
         // best one found so far.
         //
         if (super.solutionShouldBeIncluded(bestSolSoFar, bestSols)) {
            super.insertOrdered(bestSols, bestSolSoFar);
            numItersNoImprovement = 0;

            if (logAnneal.isDebugEnabled()) {
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

      logAnneal.debug("Evaluating whether to choose a neighbor. CurrentFitness=" + currentFitness + " neighbourfitness="
            + neighborFitness);

      // if neighbour Is Not Only Marginally Better . The marginality has been
      // set to 0.01 arbitrarily
      if (neighborFitness > (currentFitness + 0.01)) {
         return true;
      }

      double random = Math.random();

      double probability = calculateProbabilityExponential(currentFitness, neighborFitness, temperature);

      logAnneal
            .debug("probability of change=" + probability + " randomNumber=" + random + " temperature=" + temperature);

      probability = calculateProbabilityExponentialMaxAHalf(currentFitness, neighborFitness, temperature);

      logAnneal.debug(
            "probability of change method2=" + probability + " randomNumber=" + random + " temperature=" + temperature);

      return probability > random;

   }

   private double calculateProbabilityExponentialMaxAHalf(double currentFitness, double neighborFitness,
         double temperature) {
      // The 0.01 is to accommodate the previous marginality
      return 1.0 / (1.0 + (1.0 / calculateProbabilityExponential(currentFitness + 0.01, neighborFitness, temperature)));
   }

   private double calculateProbabilityExponential(double currentFitness, double neighborFitness, double temperature) {
      // neighbor is worse than current, calculate an exponential distribution.
      return Math.expm1(-1.0 * (currentFitness - neighborFitness) / temperature) + 1.0;

   }

   private Solution exploredSol;
   private Solution[] neighborsOfExplored;

   private Solution getRandomNeighbour(Solution currentSol, SuitableOptions cloudOffers, Topology topology) {

      if (exploredSol == null) {
         neighborsOfExplored = super.findNeighbors(currentSol, cloudOffers, topology);
         exploredSol = currentSol;
      }
      if (!currentSol.equals(exploredSol)) {
         neighborsOfExplored = super.findNeighbors(currentSol, cloudOffers, topology);
         exploredSol = currentSol;
      }
      // at this point in neighbors is the useful list of neighbours.

      if (neighborsOfExplored == null) {
         return null;
      } else {
         int neighborIndexChosen = (int) Math.floor(Math.random() * (double) neighborsOfExplored.length);
         return neighborsOfExplored[neighborIndexChosen];
      }

   }

}
