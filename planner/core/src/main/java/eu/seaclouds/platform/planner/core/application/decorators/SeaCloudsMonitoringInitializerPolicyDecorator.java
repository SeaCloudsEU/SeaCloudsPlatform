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
package eu.seaclouds.platform.planner.core.application.decorators;

import java.util.List;
import java.util.Map;

import org.apache.brooklyn.util.collections.MutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.seaclouds.platform.planner.core.DamGenerator;
import eu.seaclouds.platform.planner.core.DamGeneratorConfigBag;
import eu.seaclouds.platform.planner.core.application.ApplicationFacade;

public class SeaCloudsMonitoringInitializerPolicyDecorator implements ApplicationFacadeDecorator {

    static Logger log = LoggerFactory.getLogger(SeaCloudsMonitoringInitializerPolicyDecorator.class);

    public static final String SEACLOUDS_APPLICATION_CONFIGURATION_POLICY =
            "configuration";
    public static final String SEACLOUDS_MONITORING_POLICY =
            "eu.seaclouds.policy.SeaCloudsMonitoringInitializationPolicies";
    public static final String SEACLOUDS_DC_ENDPOINT = "seacloudsdc.endpoint";
    public static final String TARGET_NODES = "targetEntities";

    private static final String TARGET_PROPERTY =  "language";

    private ApplicationFacade applicationFacade;
    private DamGeneratorConfigBag configBag;

    @Override
    public void apply(ApplicationFacade applicationFacade) {
        this.applicationFacade = applicationFacade;
        configBag = applicationFacade.getConfigBag();

        addSeaCloudsMonitoringGroup();
    }

    private void addSeaCloudsMonitoringGroup() {
        Map<String, Object> policyGroupValue = getPolicyGroupValues();

        applicationFacade.addGroup(
                DamGenerator.SEACLOUDS_MONITORING_INITIALIZATION,
                policyGroupValue);
    }

    public Map<String, Object> getPolicyGroupValues() {

        Map<String, Object> seaCloudsPolicyConfiguration = MutableMap.of();
        seaCloudsPolicyConfiguration.put(DamGenerator.TYPE, SEACLOUDS_MONITORING_POLICY);
        seaCloudsPolicyConfiguration.put(SEACLOUDS_DC_ENDPOINT, configBag.getSeacloudsDcEndPoint());
        seaCloudsPolicyConfiguration.put(TARGET_NODES,
                applicationFacade.filterNodeTemplatesByPropertyAndValues(TARGET_PROPERTY, getAllowedTargetValues()));

        Map<String, Object> seaCloudsPolicy = MutableMap.of();
        seaCloudsPolicy.put(SEACLOUDS_APPLICATION_CONFIGURATION_POLICY, seaCloudsPolicyConfiguration);

        Map<String, Object> seaCloudsApplicationGroup = MutableMap.of();
        seaCloudsApplicationGroup.put(DamGenerator.MEMBERS, ImmutableList.of());
        seaCloudsApplicationGroup.put(DamGenerator.POLICIES, ImmutableList.of(seaCloudsPolicy));

        return seaCloudsApplicationGroup;
    }

    private List<Object> getAllowedTargetValues(){
        return ImmutableList.<Object>of("JAVA", "PHP");
    }




}
