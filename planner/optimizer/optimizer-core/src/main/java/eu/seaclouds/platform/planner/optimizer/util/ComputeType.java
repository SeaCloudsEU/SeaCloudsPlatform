package eu.seaclouds.platform.planner.optimizer.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ComputeType {

   static Logger log = LoggerFactory.getLogger(ComputeType.class);
   
   public static final Map<String,Object> computeType = createComputeType();

   private static Map<String, Object> createComputeType() {
      Map<String,Object> hardwareIdMap=new HashMap<String,Object>();
      hardwareIdMap.put(TOSCAkeywords.TYPE_TAG_IN_TYPES_DESCRIPTION, TOSCAkeywords.TYPE_OF_HARDWARE_ID);
      hardwareIdMap.put(TOSCAkeywords.TYPE_PROPERTIES_REQUIRED, TOSCAkeywords.IS_HARDWARE_PROPERTY_REQUIRED);
      
      Map<String,Object> computePropertiesMap=new HashMap<String,Object>();
      computePropertiesMap.put(TOSCAkeywords.TYPE_PROPERTIES_HARDWARE_ID, hardwareIdMap);
      
      Map<String,Object> computeMap=new HashMap<String,Object>();
      computeMap.put(TOSCAkeywords.IS_TYPE_DERIVED_TAG, TOSCAkeywords.TOSCA_COMPUTE_TYPE);
      computeMap.put(TOSCAkeywords.TYPE_DESCRIPTION_TAG, TOSCAkeywords.TYPE_DESCRIPTION_CONTENT);
      computeMap.put(TOSCAkeywords.TYPE_PROPERTIES, computePropertiesMap);
      return computeMap;
      
   }
   
   public static void addComputeToTypes(Map<String,Object> typesMap){
      log.debug("Adding the Compute Map to types map");
      typesMap.put(TOSCAkeywords.COMPUTE_TYPE, computeType);
   }
   
   
   
}
