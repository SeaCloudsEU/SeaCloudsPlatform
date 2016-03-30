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
package eu.seaclouds.platform.planner.core.application.topology.nodetemplate.host;

import com.google.common.collect.ImmutableList;
import org.apache.brooklyn.util.collections.MutableMap;

import java.util.List;
import java.util.Map;

//TODO: it should be refactorized as an interface and a new class should be created to represent CF
public class PlatformNodeTemplate extends AbstractHostNodeTemplate {

    private static final List<String> SUPPORTED_TYPES =
            ImmutableList.of("seaclouds.nodes.Platform.Cloud_Foundry");

    private static final String CLOUDFOUNDRY = "cloudfoundry";

    private static final String USER_PROP_NAME = "user";
    private static final String USER_PROP_DEFAULT_VALUE = "<AKA_YOUR_USER_EMAIL>";

    private static final String PASSWORD_PROP_NAME = "password";
    private static final String PASSWORD_PROP_DEFAULT_VALUE = "<password>";

    private static final String ORG_PROP_NAME = "org";
    private static final String ORG_PROP_DEFAULT_VALUE = "<organization>";

    private static final String ENDPOINT_PROP_NAME = "endpoint";
    private static final String ENDPOINT_PROP_DEFAULT_VALUE = "<API_endpoint>";

    private static final String SPACE_PROP_NAME = "space";
    private static final String SPACE_PROP_DEFAULT_VALUE = "<space>";

    private static final String ADDRESS_PROP_NAME = "address";
    private static final String ADDRESS_PROP_DEFAULT_VALUE = "<platform_address>";

    public PlatformNodeTemplate(Map<String, Object> applicationTemplate, String nodeTemplateId) {
        super(applicationTemplate, nodeTemplateId);
    }

    public static boolean isSupported(String type) {
        return SUPPORTED_TYPES.contains(type);
    }

    @Override
    public String getType() {
        return getModuleType();
    }

    @Override
    public Map<String, Object> getLocationPolicyProperties() {
        return createLocationPolicy();
    }

    private Map<String, Object> createLocationPolicy() {
        Map<String, Object> locationPolicyDescription = MutableMap.of();
        locationPolicyDescription.put(BROOKLYN_LOCATION, createLocationDescription());
        return locationPolicyDescription;
    }

    private Map<String, Object> createLocationDescription() {
        Map<String, Object> locationDescription = MutableMap.of();
        locationDescription.put(CLOUDFOUNDRY, createLocationConfigration());
        return locationDescription;
    }

    private Map<String, Object> createLocationConfigration(){
        Map<String, Object> locationConfiguration = MutableMap.of();
        locationConfiguration.put(USER_PROP_NAME,USER_PROP_DEFAULT_VALUE);
        locationConfiguration.put(PASSWORD_PROP_NAME,PASSWORD_PROP_DEFAULT_VALUE);
        locationConfiguration.put(ORG_PROP_NAME,ORG_PROP_DEFAULT_VALUE);
        locationConfiguration.put(ENDPOINT_PROP_NAME,ENDPOINT_PROP_DEFAULT_VALUE);
        locationConfiguration.put(SPACE_PROP_NAME,SPACE_PROP_DEFAULT_VALUE);
        locationConfiguration.put(ADDRESS_PROP_NAME,ADDRESS_PROP_DEFAULT_VALUE);
        return locationConfiguration;
    }


    private Map<String, Object> createSimpleLocationPolicy() {
        Map<String, Object> locationPolicyDescription = MutableMap.of();
        locationPolicyDescription.put(BROOKLYN_LOCATION, "cloudfoundry-instance");
        return locationPolicyDescription;
    }
}
