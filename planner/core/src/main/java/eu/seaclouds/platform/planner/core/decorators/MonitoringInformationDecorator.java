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

import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringDamGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringInfo;
import eu.seaclouds.platform.planner.core.ApplicationFacade;
import eu.seaclouds.platform.planner.core.DamGenerator;
import eu.seaclouds.platform.planner.core.DamGeneratorConfigBag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MonitoringInformationDecorator implements ApplicationFacadeDecorator {

    private DamGeneratorConfigBag configBag;
    private ApplicationFacade applicationFacade;
    private MonitoringInfo monitoringInfo;

    @Override
    public void apply(ApplicationFacade applicationFacade) {
        this.applicationFacade = applicationFacade;
        configBag = applicationFacade.getConfigBag();
        monitoringInfo = generateMonitoringInfo();
        applicationFacade.addMonitoringInfo(monitoringInfo);
        addMonitorInfoToTemplate();
    }

    private MonitoringInfo generateMonitoringInfo() {
        MonitoringDamGenerator monDamGen = new MonitoringDamGenerator(configBag.getMonitorEndpoint(), configBag.getInfluxDbEndpoint());
        return monDamGen.generateMonitoringInfo(applicationFacade.templateToString());
    }

    private void addMonitorInfoToTemplate() {
        String generatedApplicationId = UUID.randomUUID().toString();

        //TODO: SPLIT in a new method policyGeneration
        HashMap<String, Object> appGroup = new HashMap<>();
        appGroup.put(DamGenerator.MEMBERS, Arrays.asList(DamGenerator.APPLICATION));
        Map<String, Object> policy = new HashMap<>();

        HashMap<String, String> policyProperties = new HashMap<>();
        policyProperties.put(DamGenerator.ID, generatedApplicationId);
        policyProperties.put(DamGenerator.TYPE, DamGenerator.SEACLOUDS_MONITORING_RULES_ID_POLICY);
        policy.put(DamGenerator.MONITORING_RULES_POLICY_NAME, policyProperties);

        ArrayList<Map<String, Object>> policiesList = new ArrayList<>();
        policiesList.add(policy);

        appGroup.put(DamGenerator.POLICIES, policiesList);

        applicationFacade.addGroup(DamGenerator.MONITOR_INFO_GROUPNAME, appGroup);
    }

}
