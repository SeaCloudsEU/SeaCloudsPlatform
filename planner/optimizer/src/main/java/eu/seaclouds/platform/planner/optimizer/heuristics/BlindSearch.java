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

import java.util.Map;

import eu.seaclouds.platform.planner.optimizer.Solution;
import eu.seaclouds.platform.planner.optimizer.SuitableOptions;
import eu.seaclouds.platform.planner.optimizer.Topology;

public class BlindSearch extends AbstractHeuristic implements SearchMethod {


   public BlindSearch() {
      super();

   }

   public BlindSearch(int maxIter) {
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
   public Map<String, Object>[] computeOptimizationProblem(
         SuitableOptions cloudOffers, Map<String, Object> applicationMap,
         Topology topology, int numPlansToGenerate) {

      // To findSolution method, we pass an Empty solution instead of a null
      // value to or create a new method that does not consider the current one.
      // This way may help for replanning, when even the first attempt for
      // solution will be based on the current deployment
      Solution[] bestSols = findSolutions(null, cloudOffers, applicationMap,
            numPlansToGenerate);

      super.setFitnessOfSolutions(bestSols, applicationMap, topology,
            cloudOffers);
      super.sortSolutionsByFitness(bestSols);

      Solution[] currentSol = new Solution[1];
      int numItersNoImprovement = 0;
      while (numItersNoImprovement < super.getMaxIterNoImprove()) {

         currentSol[0] = super.findRandomSolution(cloudOffers, applicationMap);
         currentSol[0].setSolutionFitness(super.fitness(currentSol[0],
               applicationMap, topology, cloudOffers));

         if (currentSol[0].getSolutionFitness() > super
               .getMinimumFitnessOfSolutions(bestSols)) {
            if (!currentSol[0].isContainedIn(bestSols)) {
               super.insertOrdered(bestSols, currentSol[0]);
               numItersNoImprovement = 0;
            }
         }

         numItersNoImprovement++;
      }

      return super.hashMapOfFoundSolutionsWithThresholds(bestSols,
            applicationMap, topology, cloudOffers, numPlansToGenerate);

   }

   private Solution[] findSolutions(Solution baseSolution,
         SuitableOptions cloudOffers, Map<String, Object> applicationMap,
         int numPlansToGenerate) {

      Solution[] newSolutions = new Solution[numPlansToGenerate];

      for (int newSolIndex = 0; newSolIndex < newSolutions.length; newSolIndex++) {

         newSolutions[newSolIndex] = super.findRandomSolution(cloudOffers,
               applicationMap);
      }

      return newSolutions;

   }

}
