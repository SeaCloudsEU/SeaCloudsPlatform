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


import eu.seaclouds.platform.planner.optimizer.Solution;
import eu.seaclouds.platform.planner.optimizer.SuitableOptions;
import eu.seaclouds.platform.planner.optimizer.Topology;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityInformation;

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
   public Solution[] computeOptimizationProblem(
         SuitableOptions cloudOffers, QualityInformation requirements,
         Topology topology, int numPlansToGenerate) {

      // To findSolution method, we pass an Empty solution instead of a null
      // value to or create a new method that does not consider the current one.
      // This way may help for replanning, when even the first attempt for
      // solution will be based on the current deployment
      Solution[] bestSols = findSolutions(null, cloudOffers,numPlansToGenerate);

      super.setFitnessOfSolutions(bestSols, requirements, topology,
            cloudOffers);
      super.sortSolutionsByFitness(bestSols);

      Solution[] currentSol = new Solution[1];
      int numItersNoImprovement = 0;
      while (numItersNoImprovement < super.getMaxIterNoImprove()) {

         currentSol[0] = super.findRandomSolution(cloudOffers);
         currentSol[0].setSolutionFitness(super.fitness(currentSol[0],
               requirements, topology, cloudOffers));

         if (AbstractHeuristic.IS_DEBUG) {
            log.debug("Start checking the presence of quality attached to solutions after generating a new random solution in BLIND. With sol= "
                  + currentSol[0].toString());
            super.checkQualityAttachedToSolutions(currentSol);
         }
         if (currentSol[0].getSolutionFitness() > super
               .getMinimumFitnessOfSolutions(bestSols)) {
            if (!currentSol[0].isContainedIn(bestSols)) {
               super.insertOrdered(bestSols, currentSol[0]);
               if (AbstractHeuristic.IS_DEBUG) {
                  log.debug("Better solution found after " + numItersNoImprovement + " iterations");
               }
               numItersNoImprovement = 0;
            }
         }

         numItersNoImprovement++;
      }

      
      return bestSols;

   }

   private Solution[] findSolutions(Solution baseSolution,
         SuitableOptions cloudOffers, int numPlansToGenerate) {

      Solution[] newSolutions = new Solution[numPlansToGenerate];

      for (int newSolIndex = 0; newSolIndex < newSolutions.length; newSolIndex++) {

         newSolutions[newSolIndex] = super.findRandomSolution(cloudOffers);
      }

      return newSolutions;

   }



}
