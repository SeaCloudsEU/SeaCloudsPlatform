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

public final class Constraint extends LinkedHashMap<String, Object> {
    private static final long serialVersionUID = 1L;
    
    public static class Names {
        public static final String EQ = "equal";
        public static final String GT = "greater_than";
        public static final String GE = "greater_or_equal";
        public static final String LT = "less_than";
        public static final String LE = "less_or_equal";
        public static final String BETWEEN = "in_range";
    }
    
    public Constraint(String operator, Object value) {
        this.put(operator, value);
    }
}