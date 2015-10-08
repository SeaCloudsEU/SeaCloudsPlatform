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

package eu.seaclouds.platform.planner.optimizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethodName;

public class Optimizer {

   private static Logger          log = LoggerFactory.getLogger(Optimizer.class);
   private final int              NUMBER_OF_PLANS_GENERATED;
   private final SearchMethodName searchName;

   public Optimizer() {
      NUMBER_OF_PLANS_GENERATED = 5;
      searchName = SearchMethodName.BLINDSEARCH;
   }

   public Optimizer(int num) {
      NUMBER_OF_PLANS_GENERATED = num;
      searchName = SearchMethodName.BLINDSEARCH;
   }

   public Optimizer(SearchMethodName name) {
      NUMBER_OF_PLANS_GENERATED = 5;
      searchName = name;
   }

   public Optimizer(int num, SearchMethodName name) {
      NUMBER_OF_PLANS_GENERATED = num;
      searchName = name;
   }

   // Optimizer uses its previously generated plan as a source when replanning.
   private String[] previousPlans = null;

   public String[] optimize(String appModel, String suitableCloudOffer) {

      String[] outputPlans = new String[NUMBER_OF_PLANS_GENERATED];
      outputPlans[0] = "Plan generation was not possible";

      if (previousPlans == null) {
         OptimizerInitialDeployment initialOptimizer = new OptimizerInitialDeployment(searchName);

         try {
            outputPlans = initialOptimizer.optimize(appModel, suitableCloudOffer, NUMBER_OF_PLANS_GENERATED);
            previousPlans = outputPlans;
         } catch (Error E) {
            log.error("Error optimizing the initial deployment");
         }
      } else {
         Reoptimizer optimizerReplanning = new Reoptimizer(searchName);

         try{
            log.error("Calling a Replanning. The previously generated Plan will be used as a base");
            outputPlans = optimizerReplanning.optimize(appModel, suitableCloudOffer, NUMBER_OF_PLANS_GENERATED);
            previousPlans = outputPlans;
         } catch (Error E) {
            log.error("Error optimizing the Replanning");
            throw E;
         }

      }
      return outputPlans;

   }

}
