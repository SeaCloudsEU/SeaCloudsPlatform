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

   static Logger logHill = LoggerFactory.getLogger(HillClimb.class);

   public HillClimb() {
      super();

   }

   public HillClimb(int maxIter) {
      super(maxIter);
   }
   
   
   
   
   @Override
   public Solution[] computeOptimizationProblemForAllDifferentSolutions(SuitableOptions cloudOffers, QualityInformation requirements,
         Topology topology, int numPlansToGenerate) {
      return filterUniqueSolutions(computeOptimizationProblem(cloudOffers,requirements,topology,numPlansToGenerate));
   }

   /*
    * (non-Javadoc)
    * 
    * @see eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethod#
    * computeOptimalSolution
    * (eu.seaclouds.platform.planner.optimizer.SuitableOptions, java.util.Map)
    */
   @Override
   public Solution[] computeOptimizationProblem(SuitableOptions cloudOffers, QualityInformation requirements,
         Topology topology, int numPlansToGenerate) {

      // To findSolution method, we pass an Empty solution instead of a null
      // value to or create a new method that does not consider the current one.
      // This way may help for replanning, when even the first attempt for
      // solution will be based on the current deployment

      cloudOffers.sortDescendingPerformance();
      Solution[] bestSols = super.findInitialRandomSolutions(cloudOffers, numPlansToGenerate, topology);
      super.setFitnessOfSolutions(bestSols, requirements, topology, cloudOffers);
      if (logHill.isDebugEnabled()) {
         logHill.debug(
               "Start checking the presence of quality attached to solutions after the first generation HILLCLIMB");
         super.checkQualityAttachedToSolutions(bestSols);
      }
      super.sortSolutionsByFitness(bestSols);

      int numItersNoImprovement = 0;
      while (numItersNoImprovement < super.getMaxIterNoImprove()) { // each
                                                                    // iteration
                                                                    // finds a
                                                                    // peak

         Solution currentSol = super.findRandomSolution(cloudOffers, topology);
         currentSol.setSolutionFitness(super.fitness(currentSol, requirements, topology, cloudOffers));

         boolean neighborsImprove = true;
         int pathLengthTraversed = 0;
         while (neighborsImprove) {
            Solution[] candidates = super.findNeighbors(currentSol, cloudOffers, topology);
            if (candidates == null) {
               logHill.debug("New candidates not found");
               neighborsImprove = false;
               pathLengthTraversed = 0;
            } else {
               logHill.debug(
                     "Found " + candidates.length + " neighbors of the solution: Are " + Arrays.toString(candidates));

               super.setFitnessOfSolutions(candidates, requirements, topology, cloudOffers);
               Solution bestCandidate = super.getSolutionWithMaximumFitness(candidates);
               if (bestCandidate.getSolutionFitness() > currentSol.getSolutionFitness()) {
                  currentSol = bestCandidate;
                  pathLengthTraversed++;

                  if (pathLengthTraversed > 100) {
                     logHill.debug("Something weird is happening, iteration: " + pathLengthTraversed
                           + "without finding local maxima: Fitness: " + currentSol.getSolutionFitness() + " Solution: "
                           + currentSol.toString());
                  }

               } else {// there is not any better neighbor. See if this solution
                       // can be included among the set of best solutions
                  neighborsImprove = false;
                  pathLengthTraversed = 0;
               }
            }
         }

         // We have a local maxima in currentSol, let's see if it is better than
         // the previous maxima.
         if (super.solutionShouldBeIncluded(currentSol, bestSols)) {

            logHill.debug("After " + numItersNoImprovement
                  + " iterations we have found a local maxima solution that is going to be included: "
                  + currentSol.toString());

            super.insertOrdered(bestSols, currentSol);
            numItersNoImprovement = 0;

            if (logHill.isDebugEnabled()) {
               logHill.debug(
                     "Start checking the presence of quality attached to solutions after adding a solution in HILLCLIMB");
               super.checkQualityAttachedToSolutions(bestSols);
            }

         } else {

            logHill.debug("After " + numItersNoImprovement
                  + " iterations we have found a local maxima, but it will not be included: " + currentSol.toString());

            numItersNoImprovement++;
         }

      }

      if (logHill.isDebugEnabled()) {
         logHill.debug(
               "Start checking the presence of quality attached to solutions after adding a solution in HILLCLIMB");
         super.checkQualityAttachedToSolutions(bestSols);
      }

      return bestSols;

   }


}
