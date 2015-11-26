package eu.seaclouds.platform.planner.optimizer.util;

import java.util.HashMap;
import java.util.Map;

public class ScalingPolicy {

   
   public static Map<String, Object> createPolicy(double wklLowerBound, double wklUpperBound, int minPoolSize, int maxPoolSize){
      Map<String, Object> autoscaleValues = new HashMap<String,Object>();
      
      autoscaleValues.put(TOSCAkeywords.AUTOSCALE_TYPE, TOSCAkeywords.BROOKLYN_AUTOSCALER_POLICY);
      autoscaleValues.put(TOSCAkeywords.AUTOSCALE_METRIC, TOSCAkeywords.BOOKLYN_AUTOSCALER_METRIC_ARRIVALRATE_PER_SECOND);
      autoscaleValues.put(TOSCAkeywords.AUTOSCALE_METRIC_LOWERBOUND, wklLowerBound);
      autoscaleValues.put(TOSCAkeywords.AUTOSCALE_METRIC_UPPERBOUND, wklUpperBound);
      autoscaleValues.put(TOSCAkeywords.AUTOSCALE_POOL_MINIMUM_SIZE, minPoolSize);
      autoscaleValues.put(TOSCAkeywords.AUTOSCALE_POOL_MAXIMUM_SIZE, maxPoolSize);
      
      Map<String, Object> policy = new HashMap<String,Object>();
      policy.put(TOSCAkeywords.AUTOSCALING_TAG, autoscaleValues);
      
      return policy;
   }
   
}
