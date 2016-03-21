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

import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class SeaCloudsApplicationDataTest {
    private static final String TOSCA_DAM_FILE_PATH = "fixtures/tosca-dam.yml";
    private static final String DESCRIPTION = "web-chat.tomca-dc-compute.mysql-compute.na";
    private static final String AGREEMENT_ID = "appid";
    private static final Set<String> MONITORING_RULES_IDS =
            Sets.newHashSet("appid_mysql_server_cpu_utilization", "appid_mysql_server_ram_utilization");


    private Map toscaDamMap;
    private SeaCloudsApplicationData applicationData;

    //TODO: Modify this class when we will take Objects as and input for the setters instead of strings.

    @BeforeMethod
    public void setUp() throws Exception {
        URL resource = Resources.getResource(TOSCA_DAM_FILE_PATH);
        toscaDamMap = (Map) new Yaml().load(FileUtils.openInputStream(new File(resource.getFile())));
    }

    @Test
    public void testExtractName() {
        String toscaDamName = SeaCloudsApplicationData.extractName(toscaDamMap);
        assertEquals(toscaDamName, DESCRIPTION);
    }

    @Test
    public void testExtractAgreementId() throws Exception {
       assertEquals(SeaCloudsApplicationData.extractAgreementId(toscaDamMap), AGREEMENT_ID);
    }

    @Test
    public void testExtractMonitoringRulesIds() throws Exception {
        assertEquals(SeaCloudsApplicationData.extractMonitoringRulesIds(toscaDamMap), MONITORING_RULES_IDS);
    }

    @Test
    public void testCreateSeaCloudsApplicationData() throws Exception {
        SeaCloudsApplicationData application = new SeaCloudsApplicationData(toscaDamMap);
        assertEquals(application.getName(), DESCRIPTION);
        assertEquals(application.getAgreementId(), AGREEMENT_ID);
        assertEquals(application.getMonitoringRulesIds(), MONITORING_RULES_IDS);
    }

}