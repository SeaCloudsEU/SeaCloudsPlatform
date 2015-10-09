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

import alien4cloud.model.components.constraints.ValidValuesConstraint;
import eu.seaclouds.planner.matchmaker.PropertyValue;

import java.util.Collection;

public class ConstraintValidValues<T> extends Constraint {


    public ConstraintValidValues(String name, Collection<T> values) {
       super(name, ConstraintTypes.ValidValues, values);
    }

    public boolean checkConstraint(PropertyValue prop) {
        Object o = prop.getValue();
        Collection<Object> vvalues = (Collection<Object>) value;
        for(Object i: vvalues)
            if(i.equals(o)) return true;

        return false;
    }
}
