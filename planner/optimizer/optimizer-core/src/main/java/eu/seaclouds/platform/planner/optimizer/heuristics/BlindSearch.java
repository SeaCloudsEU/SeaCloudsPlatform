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

public class BlindSearch extends AbstractHeuristic implements SearchMethod {

   static Logger logBlind = LoggerFactory.getLogger(BlindSearch.class);

   public BlindSearch() {
      super();

   }

   public BlindSearch(int maxIter) {
      super(maxIter);
   }

   @Override
   public Solution[] computeOptimizationProblemForAllDifferentSolutions(SuitableOptions cloudOffers,
         QualityInformation requirements, Topology topology, int numPlansToGenerate) {
      return filterUniqueSolutions(computeOptimizationProblem(cloudOffers, requirements, topology, numPlansToGenerate));
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

      logBlind.debug("Starting the computeOptimizationProblem in BLIND");
      Solution[] bestSols = findInitialRandomSolutions(cloudOffers, numPlansToGenerate, topology);
      logBlind.debug("Initial random solutions found");
      super.setFitnessOfSolutions(bestSols, requirements, topology, cloudOffers);
      super.sortSolutionsByFitnessAndReplaceNaN(bestSols);

      logBlind.debug("Fitness of initial solutions calculated");
      Solution[] currentSol = new Solution[1];
      int numItersNoImprovement = 0;
      while (numItersNoImprovement < super.getMaxIterNoImprove()) {

         currentSol[0] = super.findRandomSolution(cloudOffers, topology);
         currentSol[0].setSolutionFitness(super.fitness(currentSol[0], requirements, topology, cloudOffers));

         if (logBlind.isTraceEnabled()) {
            logBlind.trace(
                  "Start checking the presence of quality attached to solutions after generating a new random solution in BLIND. With sol= "
                        + currentSol[0].toString());
            super.checkQualityAttachedToSolutions(currentSol);
         }
         if (currentSol[0].getSolutionFitness() > super.getMinimumFitnessOfSolutions(bestSols)) {
            logBlind.debug(
                  "Quality of found solution is larger than the best solutions so far {} > {} and can be included: {} ",
                  currentSol[0].getSolutionFitness(), super.getMinimumFitnessOfSolutions(bestSols),
                  currentSol[0].toString());

            if (!currentSol[0].isContainedIn(bestSols)) {
               super.insertOrdered(bestSols, currentSol[0]);

               logBlind.debug(
                     "Better solution found after {} iterations. Adding fitness of {} . Resulting bestsols fitness are: {}",
                     numItersNoImprovement, currentSol[0].getSolutionFitness(), printFitnessArray(bestSols));

               numItersNoImprovement = 0;
            }
         }

         numItersNoImprovement++;
      }

      return bestSols;

   }

}
