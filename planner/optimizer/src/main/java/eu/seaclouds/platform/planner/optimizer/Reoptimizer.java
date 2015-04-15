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

import eu.seaclouds.platform.planner.optimizer.heuristics.BlindSearch;
import eu.seaclouds.platform.planner.optimizer.heuristics.HillClimb;
import eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethod;
import eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethodName;

public class Reoptimizer {

   private final SearchMethodName searchName;
   private SearchMethod           engine;

   static Logger                  log = LoggerFactory
                                            .getLogger(Reoptimizer.class);

   public Reoptimizer() {
      engine = new BlindSearch();
      searchName = SearchMethodName.BLINDSEARCH;
   }

   public Reoptimizer(SearchMethodName name) {
      searchName = name;

      switch (name) {
      case BLINDSEARCH:
         engine = new BlindSearch();
         break;
      case HILLCLIMB: engine= new HillClimb(); break;


      default:
         engine = new BlindSearch();
      }
   }

   public String[] optimize(String appModel, String suitableCloudOffer,
         int numPlansToGenerate_ArrayLength) {
      // TODO Auto-generated method stub
      return null;
   }

}
