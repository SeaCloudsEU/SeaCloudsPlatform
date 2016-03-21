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
package eu.seaclouds.platform.planner.core.template.host;

import eu.seaclouds.platform.planner.core.template.AbstractNodeTemplate;
import org.apache.brooklyn.util.collections.MutableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public abstract class AbstractHostNodeTemplate extends AbstractNodeTemplate
        implements HostNodeTemplate {


    public AbstractHostNodeTemplate(Map<String, Object> applicationTemplate, String nodeTemplateId) {
        super(applicationTemplate, nodeTemplateId);
    }

    @Override
    public String getLocationPolicyGroupName() {
        return ADD_BROOKLYN_LOCATION_PEFIX + nodeTemplateId;
    }

    public Map<String, Object> getLocationPolicyGroupValues() {
        Map<String, Object> policyGroupValues = MutableMap.of();
        policyGroupValues.put(MEMBERS, Arrays.asList(nodeTemplateId));

        ArrayList<Map<String, Object>> policyList = new ArrayList<>();
        policyList.add(getLocationPolicyProperties());
        policyGroupValues.put(POLICIES, policyList);
        return policyGroupValues;
    }

    protected abstract Map<String, Object> getLocationPolicyProperties();

}
