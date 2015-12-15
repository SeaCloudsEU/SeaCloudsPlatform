package eu.seaclouds.platform.planner.optimizer.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YAMLtypesOptimizerParser {

   static Logger log = LoggerFactory.getLogger(YAMLtypesOptimizerParser.class);

   public static void changeTypeToBeScalable(String typeName, Map<String, Object> typesMap) {

      try {
         Map<String, Object> typeInfo = (Map<String, Object>) typesMap.get(typeName);

         if (typeInfo.containsKey(TOSCAkeywords.IS_TYPE_DERIVED_TAG)) {
            typeInfo.put(TOSCAkeywords.IS_TYPE_DERIVED_TAG, TOSCAkeywords.NODE_TYPE_AUTOSCALABLE);
         }
      } catch (Exception E) {
         // Some of the information of the type declaration was not present.
         // Nothing to update in the type then. Logging the event
         if (log.isDebugEnabled()) {
            log.debug("Node type " + typeName + " could not be updated to be scalable becasue not all of its "
                  + "information was not found in node_types part of the AAM");
         }
      }

   }
   
   public static String getDerivedTypeFromTypesMap(String typeName, Map<String, Object> typesMap){
      try {
         Map<String, Object> typeInfo = (Map<String, Object>) typesMap.get(typeName);

         if (typeInfo.containsKey(TOSCAkeywords.IS_TYPE_DERIVED_TAG)) {
            return (String) typeInfo.get(TOSCAkeywords.IS_TYPE_DERIVED_TAG);
         }
         
      } catch (Exception E) {
         // Some of the information of the type declaration was not present.
         // Logging the event and returning null
         if (log.isDebugEnabled()) {
            log.debug("Node type " + typeName + " could not be updated to be scalable becasue not all of its "
                  + "information was not found in node_types part of the AAM");
         }
      }
      return null;
   }

   public static void addComputeType(Map<String, Object> typesMap) {
      
      ComputeType.addComputeToTypes(typesMap);
      
   }

}
