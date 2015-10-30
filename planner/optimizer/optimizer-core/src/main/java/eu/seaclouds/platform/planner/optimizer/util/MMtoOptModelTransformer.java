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
      
      ArrayList<Object> output = new ArrayList<Object>();
      
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
