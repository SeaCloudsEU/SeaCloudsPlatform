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
package eu.seaclouds.planner.matchmaker.constraints;

import eu.seaclouds.planner.matchmaker.Pair;
import eu.seaclouds.planner.matchmaker.PropertyValue;
import org.apache.commons.lang.StringUtils;

public abstract class Constraint<T> {
    protected final String name;
    protected final ConstraintTypes type;
    protected final T value;

    public Constraint(String name, ConstraintTypes type, T value){ //TODO: remove name from constraint?
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public boolean checkConstraint(PropertyValue prop){
        return this.value.getClass().equals(prop.getValue().getClass());
    }

}
