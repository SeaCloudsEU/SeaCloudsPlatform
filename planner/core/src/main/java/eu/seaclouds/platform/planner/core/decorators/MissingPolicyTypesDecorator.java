/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.seaclouds.platform.planner.core.decorators;

import eu.seaclouds.platform.planner.core.ApplicationFacade;
import eu.seaclouds.platform.planner.core.DamGenerator;

import java.util.List;
import java.util.Map;

/**
 * This decorator ensures that each policy has a type
 */
//TODO: this decorator has to be refactoring. A GroupsFacade should be used
public class MissingPolicyTypesDecorator implements ApplicationFacadeDecorator {

    @Override
    public void apply(ApplicationFacade applicationFacade) {
        Map<String, Object> groups = applicationFacade.getGroups();
        addPoliciesTypeIfNotPresent(groups);
    }

    @SuppressWarnings("unchecked")
    public void addPoliciesTypeIfNotPresent(Map<String, Object> groups) {
        //TODO: split
        for (Map.Entry<String, Object> entryGroup : groups.entrySet()) {
            List<Map<String, Object>> policies =
                    (List<Map<String, Object>>) ((Map<String, Object>) entryGroup.getValue()).get(DamGenerator.POLICIES);
            if (policies != null) {
                for (Map<String, Object> policy : policies) {
                    String policyName = getPolicyName(policy);
                    if (!isLocationPolicy(policy)
                            && !(policy.get(policyName) instanceof String)) {
                        Map<String, Object> policyProperties = getPolicyProperties(policy);

                        if (getPolicyType(policyProperties) == null) {
                            policyProperties.put(DamGenerator.TYPE, ((Object) "seaclouds.policies." + policyName));
                        }
                    }
                }
            }
        }
    }

    private String getPolicyType(Map<String, Object> policyProperties) {
        return (String) policyProperties.get(DamGenerator.TYPE);
    }

    @SuppressWarnings("unchecked")
    private String getPolicyName(Map<String, Object> policy) {
        if (policy != null) {
            for (Map.Entry<String, Object> policyEntry : policy.entrySet()) {
                return policyEntry.getKey();
            }
        }
        return null;
    }

    private boolean isLocationPolicy(Map<String, Object> policy) {
        return policy.containsKey(DamGenerator.BROOKLYN_POLICY_TYPE);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getPolicyProperties(Map<String, Object> policy) {
        if (policy != null) {
            for (Map.Entry<String, Object> policyEntry : policy.entrySet()) {
                return (Map<String, Object>) policyEntry.getValue();
            }
        }
        return null;
    }


}
