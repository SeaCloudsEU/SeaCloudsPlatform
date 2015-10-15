/**
 * Copyright 2015 Atos
 * Contact: Seaclouds
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
package eu.seaclouds.platform.planner.aamwriter.modeldesigner;

import org.json.simple.JSONObject;

public class DRequirements {

    public static final class Attributes {
        public static final String RESPONSE_TIME = "response_time";
        public static final String AVAILABILITY = "availability";
        public static final String COST = "cost";
        public static final String WORKLOAD = "workload";
    }
    
    @SuppressWarnings("unused")
    private DGraph graph;

    private double responseTime;
    private double availability;
    private double cost;
    private double workload;

    public DRequirements(JSONObject jnode, DGraph graph) {
        this.graph = graph;

        this.responseTime = parseDouble(jnode, Attributes.RESPONSE_TIME); 
        this.availability = parseDouble(jnode, Attributes.AVAILABILITY);
        this.cost = parseDouble(jnode, Attributes.COST);
        this.workload = parseDouble(jnode, Attributes.WORKLOAD);
    }
    
    private double parseDouble(JSONObject jnode, String key) {
        double result = Double.parseDouble(jnode.get(key).toString());
        
        return result;
    }

    @Override
    public String toString() {
        return String.format("DRequirements [responseTime=%s, availability=%s, cost=%s, workload=%s]",
                responseTime, availability, cost, workload);
    }
    
    public double getResponseTime() {
        return responseTime;
    }
    
    public double getAvailability() {
        return availability;
    }
    
    public double getCost() {
        return cost;
    }
    
    public double getWorkload() {
        return workload;
    }
}
