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

import java.util.HashMap;
import java.util.Map;

public class ScalingPolicy {

   
   public static Map<String, Object> createPolicy(double wklLowerBound, double wklUpperBound, int minPoolSize, int maxPoolSize){
      Map<String, Object> autoscaleValues = new HashMap<String,Object>();
      
      autoscaleValues.put(TOSCAkeywords.AUTOSCALE_TYPE, TOSCAkeywords.AGNOSTIC_AUTOSCALER_POLICY);
      autoscaleValues.put(TOSCAkeywords.AUTOSCALE_METRIC, TOSCAkeywords.AGNOSTIC_AUTOSCALER_METRIC_ARRIVALRATE_PER_SECOND);
      autoscaleValues.put(TOSCAkeywords.AUTOSCALE_METRIC_LOWERBOUND, wklLowerBound);
      autoscaleValues.put(TOSCAkeywords.AUTOSCALE_METRIC_UPPERBOUND, wklUpperBound);
      autoscaleValues.put(TOSCAkeywords.AUTOSCALE_POOL_MINIMUM_SIZE, minPoolSize);
      autoscaleValues.put(TOSCAkeywords.AUTOSCALE_POOL_MAXIMUM_SIZE, maxPoolSize);
      
      Map<String, Object> policy = new HashMap<String,Object>();
      policy.put(TOSCAkeywords.AUTOSCALING_TAG, autoscaleValues);
      
      return policy;
   }
   
}
