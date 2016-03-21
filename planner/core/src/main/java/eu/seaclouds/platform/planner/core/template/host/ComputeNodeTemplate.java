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

import com.google.common.collect.ImmutableList;
import org.apache.brooklyn.util.collections.MutableMap;

import java.util.List;
import java.util.Map;

public class ComputeNodeTemplate extends AbstractHostNodeTemplate {

    public static final String JCLOUDS = "jclouds";
    public static final String LOCATION = "location";
    public static final String REGION = "region";
    public static final String HARDWARE_ID = "hardwareId";

    private static final List<String> SUPPORTED_TYPES =
            ImmutableList.of("tosca.nodes.Compute", "seaclouds.nodes.Compute");

    public ComputeNodeTemplate(Map<String, Object> applicationTemplate, String nodeTemplateId) {
        super(applicationTemplate, nodeTemplateId);
    }

    public static boolean isSupported(String type) {
        return SUPPORTED_TYPES.contains(type);
    }

    @Override
    public String getType() {
        return getDeployerTypesResolver().resolveNodeType(getModuleType());
    }

    @Override
    public Map<String, Object> getLocationPolicyProperties() {
        return createSimpleLocationPolicy();
    }

    private Map<String, Object> createLocationPolicy() {
        Map<String, Object> locationPolicyDescription = MutableMap.of();
        locationPolicyDescription.put(BROOKLYN_LOCATION, createLocationDescription());
        return locationPolicyDescription;
    }

    private Map<String, Object> createLocationDescription() {
        Map<String, Object> locationDescription = MutableMap.of();
        Map<String, Object> properties = getProperties();
        String location = (String) properties.get(LOCATION);

        locationDescription.put(JCLOUDS + ":" + location, createLocationConfigration());
        return locationDescription;
    }

    private Map<String, Object> createLocationConfigration() {
        Map<String, Object> locationProperties = MutableMap.of();
        Map<String, Object> properties = getProperties();
        String region = (String) properties.get(REGION);
        String hardwareId = (String) properties.get(HARDWARE_ID);

        locationProperties.put(REGION, region);
        locationProperties.put(HARDWARE_ID, hardwareId);
        return locationProperties;
    }

    protected Map<String, Object> createSimpleLocationPolicy() {
        Map<String, Object> locationPolicyDescription = MutableMap.of();
        Map<String, Object> properties = getProperties();
        String location = (String) properties.get(LOCATION);
        String region = (String) properties.get(REGION);

        locationPolicyDescription.put(BROOKLYN_LOCATION, location + ":" + region);
        return locationPolicyDescription;
    }
}
