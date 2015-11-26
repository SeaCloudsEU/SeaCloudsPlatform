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
import eu.seaclouds.platform.planner.optimizer.util.MMtoOptModelTransformer;

public class Optimizer {

   private static Logger log = LoggerFactory.getLogger(Optimizer.class);
   private final int NUMBER_OF_PLANS_GENERATED;
   private static final int DEFAULT_NUMBER_OF_PLANS_GENERATED = 5;
   private final SearchMethodName searchName;
   private final double HYSTERESIS_PROPORTION; 
   private static final double DEFAULT_HYSTERESIS_PROPORTION=0.5; 

   public Optimizer() {
      this(DEFAULT_NUMBER_OF_PLANS_GENERATED,SearchMethodName.BLINDSEARCH,DEFAULT_HYSTERESIS_PROPORTION);
   }

   public Optimizer(int num) {
      this(num,SearchMethodName.BLINDSEARCH,DEFAULT_HYSTERESIS_PROPORTION);
   }

   public Optimizer(SearchMethodName name) {
      this(DEFAULT_NUMBER_OF_PLANS_GENERATED,name,DEFAULT_HYSTERESIS_PROPORTION);
   }

   public Optimizer(int num, SearchMethodName name) {
      this(num,name,DEFAULT_HYSTERESIS_PROPORTION);
   }
   
   public Optimizer(int num, SearchMethodName name, double hysteresis){
      NUMBER_OF_PLANS_GENERATED = num;
      searchName = name;
      HYSTERESIS_PROPORTION=hysteresis;
   }

   // Optimizer uses its previously generated plan as a source when replanning.
   private String[] previousPlans = null;

   public String[] optimize(String appModel, String suitableCloudOffer) {

      String[] outputPlans = new String[NUMBER_OF_PLANS_GENERATED];
      outputPlans[0] = "Plan generation was not possible";

      if (previousPlans == null) {
         OptimizerInitialDeployment initialOptimizer = new OptimizerInitialDeployment(searchName);

         try {
            outputPlans = initialOptimizer.optimize(appModel,
                  MMtoOptModelTransformer.transformModel(suitableCloudOffer), NUMBER_OF_PLANS_GENERATED,HYSTERESIS_PROPORTION);
            previousPlans = outputPlans;

         } catch (Exception exc) {
            log.warn(
                  "Optimizer did not work in its expected input. Exception name was " + exc.getClass().getName() +" Trying with the assumption of former versions of Input ");
            outputPlans = initialOptimizer.optimize(appModel, suitableCloudOffer, NUMBER_OF_PLANS_GENERATED,HYSTERESIS_PROPORTION);
            previousPlans = outputPlans;
         } catch (Error E) {
            log.error("Error optimizing the initial deployment");
            E.printStackTrace();
         }

      } else {
         Reoptimizer optimizerReplanning = new Reoptimizer(searchName);

         try {
            log.error("Calling a Replanning. The previously generated Plan will be used as a base");
            outputPlans = optimizerReplanning.optimize(appModel, suitableCloudOffer, NUMBER_OF_PLANS_GENERATED,HYSTERESIS_PROPORTION);
            previousPlans = outputPlans;
         } catch (Error E) {
            log.error("Error optimizing the Replanning");
            throw E;
         }

      }
      return outputPlans;

   }

}
