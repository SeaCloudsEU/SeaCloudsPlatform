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

public interface SearchMethod {

   /**
    * @param cloudOffers
    * @param applicationMap
    *           It changes the elements in CloudOffers, which reference the map
    *           in applicationMap
    * @param topology
    * @param numPlansToGenerate
    * @return
    */
   
   public Solution[] computeOptimizationProblem(
         SuitableOptions cloudOffers, QualityInformation requirements,
         Topology topology, int numPlansToGenerate);
   
   public void checkQualityAttachedToSolutions(Solution[] solutions);

}
