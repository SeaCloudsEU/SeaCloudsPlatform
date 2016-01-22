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

package eu.seaclouds.platform.planner.optimizer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MMtoOptModelTransformer {

   
   private static Logger          log = LoggerFactory.getLogger(MMtoOptModelTransformer.class);
   
   
   public static String transformModel(String MMinput){
      
      List<Object> input = YAMLmatchmakerToOptimizerParser.getListofOptions(MMinput);
      
      List<Object> output = new ArrayList<Object>();
      
      for(Object item : input){
         Map<String,Object> mapItem = (Map<String,Object>) item;
         translateModule(mapItem);
      }
      return YAMLmatchmakerToOptimizerParser.fromListtoYAMLstring(input);
      
   }


   private static void translateModule(Map<String,Object> module) {
     
      for(Map.Entry<String, Object> entry : module.entrySet()){
         List<Map<String,Object>> offersOfModule = (List<Map<String,Object>>) entry.getValue();
         translateModuleOffers(offersOfModule);
      }
      
   }


   private static void translateModuleOffers(List<Map<String,Object>> offersOfModule) {
     
      for(Map<String,Object> offer : offersOfModule){
         
         for(Map.Entry<String, Object> entry : offer.entrySet()){
            Map.Entry<String, Object> entryValueInMAPformat = (transformStringToMapAndFilter((String)entry.getValue()));
            offer.remove(entry.getKey());
            offer.put(entryValueInMAPformat.getKey(),entryValueInMAPformat.getValue());
         }
         
      }
      
   }


   private static Map.Entry<String, Object> transformStringToMapAndFilter(String value) {
      Map<String,Object> mapOfOfferInformation = YAMLoptimizerParser.getMAPofAPP(value);
      for(Map.Entry<String, Object> entry : mapOfOfferInformation.entrySet()){
         return entry;
      }
      return null;
   }
   
}
