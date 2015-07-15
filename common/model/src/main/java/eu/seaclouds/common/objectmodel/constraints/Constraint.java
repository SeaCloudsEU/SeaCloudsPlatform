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
package eu.seaclouds.common.objectmodel.constraints;

import eu.seaclouds.common.objectmodel.Feature;


public class Constraint<T> {
    String name;
    T value;
    ConstraintTypes type;

    public Constraint(String name, ConstraintTypes type, T value){
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName(){ return name; }

    public T getValue(){
        return value;
    }

    public ConstraintTypes getType() { return type;}

    public boolean checkConstraint(Feature f){
        return (f.getValue().getClass().equals(value.getClass()) &&
                f.getName().equals(name));
    }

    @Override public String toString() {
        return name + ": {" + type + " " + value.toString() + " }";
    }
}
