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

import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringInfo;
import eu.seaclouds.platform.planner.core.DamGenerator;
import eu.seaclouds.platform.planner.core.application.ApplicationFacade;
import eu.seaclouds.platform.planner.core.application.agreements.AgreementGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SlaInformationDecorator implements ApplicationFacadeDecorator {

    public static final String SLA_INFO_GROUPNAME = "sla_gen_info";
    public static final String SEACLOUDS_APPLICATION_INFORMATION_POLICY_TYPE = "seaclouds.policies.app.information";
    public static final String SEACLOUDS_APPLICATION_POLICY_NAME = "seaclouds.app.information";

    private ApplicationFacade applicationFacade;
    private AgreementGenerator agreementGenerator;

    @Override
    public void apply(ApplicationFacade applicationFacade) {
        this.applicationFacade = applicationFacade;
        this.agreementGenerator = applicationFacade.getAgreementGenerator();
        String agreementId = agreementGenerator.generateAgreeemntId(applicationFacade.templateToString(), applicationFacade.getMonitoringInfo());
        applicationFacade.addSlaInformation(agreementId);
        addApplicationInfo(agreementId);
    }

    public void addApplicationInfo(String applicationInfoId) {
        HashMap<String, Object> appGroup = new HashMap<>();
        appGroup.put(DamGenerator.MEMBERS, Arrays.asList(DamGenerator.APPLICATION));

        //TODO: split in some methods or objects
        Map<String, Object> policy = new HashMap<>();
        HashMap<String, String> policyProperties = new HashMap<>();
        policyProperties.put(DamGenerator.ID, applicationInfoId);
        policyProperties.put(DamGenerator.TYPE, SEACLOUDS_APPLICATION_INFORMATION_POLICY_TYPE);
        policy.put(SEACLOUDS_APPLICATION_POLICY_NAME, policyProperties);

        ArrayList<Map<String, Object>> policiesList = new ArrayList<>();
        policiesList.add(policy);

        appGroup.put(DamGenerator.POLICIES, policiesList);
        applicationFacade.addGroup(SLA_INFO_GROUPNAME, appGroup);
    }

}
