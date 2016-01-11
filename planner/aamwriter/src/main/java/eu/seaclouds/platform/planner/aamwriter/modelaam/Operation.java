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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Operation extends LinkedHashMap<String, Object> {
    
    public static final class Attributes {
        public static final String MEMBERS = "members";
        public static final String POLICIES = "policies";
    }
    private static final long serialVersionUID = 1L;

    private String name;
    private List<String> _members = new ArrayList<String>();
    private List<Policy> _policies = new ArrayList<Policy>();
    
    public Operation(String name, String... members) {
        this.name = name;
        for (String member : members) {
            addMember(member);
        }
    }
    
    public String getName() {
        return name;
    }
    
    public void addAppQoSRequirement(Policy.AppQoSRequirements requirements) {
        this.policies().add(requirements);
    }
    
    public void addModuleQoSRequirement(Policy.ModuleQoSRequirements requirements) {
        this.policies().add(requirements);
    }
    
    public void addQoSInfo(Policy.QoSInfo info) {
        this.policies().add(info);
    }
    
    public void addDependencies(Policy.Dependencies dependencies) {
        this.policies().add(dependencies);
    }
    
    private void addMember(String member) {
        this.members().add(member);
    }
    
    
    private List<String> members() {
        if (!this.containsKey(Attributes.MEMBERS)) {
            this.put(Attributes.MEMBERS, _members);
        }
        return _members;
    }
    
    private List<Policy> policies() {
        if (!this.containsKey(Attributes.POLICIES)) {
            this.put(Attributes.POLICIES, _policies);
        }
        return _policies;
    }
}