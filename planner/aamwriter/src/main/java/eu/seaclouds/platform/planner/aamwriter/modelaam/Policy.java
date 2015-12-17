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
package eu.seaclouds.platform.planner.aamwriter.modelaam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public interface Policy {

    public static class AbstractPolicy<T> extends LinkedHashMap<String, Map<String, T>> implements Policy {
        private static final long serialVersionUID = 1L;
        
        private Map<String, T> _properties = new LinkedHashMap<String, T>();
    
        public AbstractPolicy(String name) {
            this.put(name, _properties);
        }
        
        protected Map<String, T> getProperties() {
            return _properties;
        }
    }

    public static class QoSInfo extends AbstractPolicy<String> {
        private static final long serialVersionUID = 1L;
        
        public static class Attributes {
            public static final String NAME = "QoSInfo";
            public static final String EXECUTION_TIME = "execution_time";
            public static final String BENCHMARK_PLATFORM = "benchmark_platform";
        }
        
        public QoSInfo(String executionTimeInMs, String benchmarkPlatform) {
            super(Attributes.NAME);
            getProperties().put(Attributes.EXECUTION_TIME, executionTimeInMs + " ms");
            getProperties().put(Attributes.BENCHMARK_PLATFORM, benchmarkPlatform);
        }
    }

    public static class AppQoSRequirements extends AbstractPolicy<Constraint> {
        private static final long serialVersionUID = 1L;
        
        private ConstraintBuilder constraintBuilder = new ConstraintBuilder();
    
        public static class Attributes {
            public static final String NAME = "AppQoSRequirements";
            public static final String RESPONSE_TIME = "response_time";
            public static final String AVAILABILITY = "availability";
            public static final String COST = "cost";
            public static final String WORKLOAD = "workload";
        }
        
        public AppQoSRequirements(double rt, double availability, double cost, double workload) {
            super(Attributes.NAME);
            
            getProperties().put(Attributes.RESPONSE_TIME, buildConstraint(Constraint.Names.LT, rt, "ms"));
            getProperties().put(Attributes.AVAILABILITY, buildConstraint(Constraint.Names.GT, availability, ""));
            getProperties().put(Attributes.COST, buildConstraint(Constraint.Names.LE, cost, "euros_per_month"));
            getProperties().put(Attributes.WORKLOAD, buildConstraint(Constraint.Names.LE, workload, "req_per_min"));
        }
        
        private Constraint buildConstraint(String operator, double threshold, String unit) {
            return constraintBuilder.buildConstraint(operator, threshold, unit);
        }
    }
    
    public static class ModuleQoSRequirements extends AbstractPolicy<Constraint> {
        private static final long serialVersionUID = 1L;

        private ConstraintBuilder constraintBuilder = new ConstraintBuilder();
        
        public static class Attributes {
            public static final String NAME = "QoSRequirements";
        }
        
        public ModuleQoSRequirements() {
            super(Attributes.NAME);
            
        }
        
        public void addConstraint(String metricName, String operator, double threshold, String unit) {
            getProperties().put(metricName, buildConstraint(operator, threshold, unit));
        }

        private Constraint buildConstraint(String operator, double threshold, String unit) {
            return constraintBuilder.buildConstraint(operator, threshold, unit);
        }
    }

    public static class Dependencies extends AbstractPolicy<String> {
        private static final long serialVersionUID = 1L;
    
        public static class Attributes {
            public static final String NAME = "dependencies";
        }
        
        public Dependencies(List<Policy.Dependency> dependencies) {
            super(Attributes.NAME);
            for (Policy.Dependency dep : dependencies) {
                getProperties().put(dep.getOperation().getName(), dep.getCalls());
            }
        }
    }

    public static class Dependency {
        private Operation operation;
        private String calls;
    
        public Dependency(Operation operation, String calls) {
            this.operation = operation;
            this.calls = calls;
        }
        
        public Operation getOperation() {
            return operation;
        }
        
        public String getCalls() {
            return calls;
        }
    }
    
    static class ConstraintBuilder {
        private Constraint buildConstraint(String operator, double threshold, String unit) {
            Constraint c;
            if ("".equals(unit)) {
                c = new Constraint(operator, threshold);
            }
            else {
                c = new Constraint(operator, threshold + " " + unit);
            }
            return c;
        }
    }
}