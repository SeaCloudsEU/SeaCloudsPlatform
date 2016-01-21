/*
 *  Copyright 2014 SeaClouds
 *  Contact: SeaClouds
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package eu.seaclouds.platform.dashboard.model;

import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.net.URL;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class SeaCloudsApplicationDataTest {
    private static final String TOSCA_DAM_WITHOUT_SLA_MR_FILE_PATH = "fixtures/tosca-dam-without-mr-and-sla.yml";
    private static final String TOSCA_DAM_FILE_PATH = "fixtures/tosca-dam.yml";
    private static final String DESCRIPTION = "Sample 3-tier application";
    private static final String MONITORING_RULES_TEMPLATE_ID = "3e63723c-9715-457a-9aeb-2ae1b274e8b1";
    private static final String AGREEMENT_TEMPLATE_ID = "3e63723c-9715-457a-9aeb-2ae1b274e8b2";

    private Map toscaDamMap;
    private Map toscaDamMapWithoutMRandSLA;

    private SeaCloudsApplicationData applicationData;

    //TODO: Modify this class when we will take Objects as and input for the setters instead of strings.

    @BeforeMethod
    public void setUp() throws Exception {
        URL resource = Resources.getResource(TOSCA_DAM_FILE_PATH);
        toscaDamMap = (Map) new Yaml().load(FileUtils.openInputStream(new File(resource.getFile())));
        resource = Resources.getResource(TOSCA_DAM_WITHOUT_SLA_MR_FILE_PATH);
        toscaDamMapWithoutMRandSLA = (Map) new Yaml().load(FileUtils.openInputStream(new File(resource.getFile())));
    }

    @Test
    public void testExtractName() {
        String toscaDamName = SeaCloudsApplicationData.extractName(toscaDamMap);
        assertEquals(toscaDamName, DESCRIPTION);
    }

    @Test
    public void testExtractAgreementTemplateId() {
        String toscaAgreementTemplateId = SeaCloudsApplicationData.extractAgreementTemplateId(toscaDamMap);
        assertEquals(toscaAgreementTemplateId, AGREEMENT_TEMPLATE_ID);

        toscaAgreementTemplateId = SeaCloudsApplicationData.extractAgreementTemplateId(toscaDamMapWithoutMRandSLA);
        assertNull(toscaAgreementTemplateId);
    }

    @Test
    public void testExtractMonitoringRulesemplateId() {
        String monitoringRulesId = SeaCloudsApplicationData.extractMonitoringRulesemplateId(toscaDamMap);
        assertEquals(monitoringRulesId, MONITORING_RULES_TEMPLATE_ID);

        monitoringRulesId = SeaCloudsApplicationData.extractMonitoringRulesemplateId(toscaDamMapWithoutMRandSLA);
        assertNull(monitoringRulesId);
    }

    @Test
    public void testCreateSeaCloudsApplicationData(){
        SeaCloudsApplicationData application = new SeaCloudsApplicationData(toscaDamMap);
        assertEquals(application.getName(), DESCRIPTION);
        assertEquals(application.getAgreementTemplateId(), AGREEMENT_TEMPLATE_ID);
        assertEquals(application.getMonitoringRulesTemplateId(), MONITORING_RULES_TEMPLATE_ID);
    }

}