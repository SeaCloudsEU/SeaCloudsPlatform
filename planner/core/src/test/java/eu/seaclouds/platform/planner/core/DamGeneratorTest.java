package eu.seaclouds.platform.planner.core;


import com.google.common.io.Resources;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

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
@Test
public class DamGeneratorTest {

    private static final String FAKE_AGREEMENT_ID = "agreement-1234567890";

    static final String MONITOR_URL = "127.0.0.1";
    static final String MONITOR_PORT = "8080";
    static final String INFLUXDB_URL = "127.0.0.1";
    static final String INFLUXDB_PORT = "8083";
    static final String SLA_ENDPOINT = "127.0.0.3:9003";

    Yaml yamlParser;
    String dam = null;
    Map<String, Object> template = null;

    @Mock
    private DamGenerator.SlaAgreementManager fakeAgreementManager;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yamlParser = new Yaml(options);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMetadataTemplate() throws Exception {
        String adp = new Scanner(new File(Resources.getResource("generated_adp.yml").toURI())).useDelimiter("\\Z").next();

        when(fakeAgreementManager.generateAgreeemntId(((Map<String, Object>) anyObject())))
                .thenReturn(FAKE_AGREEMENT_ID);

        DamGenerator damGenerator = new DamGenerator(MONITOR_URL, MONITOR_PORT, SLA_ENDPOINT, INFLUXDB_URL, INFLUXDB_PORT);
        damGenerator.setAgreementManager(fakeAgreementManager);
        dam = damGenerator.generateDam(adp);
        template = (Map<String, Object>) yamlParser.load(dam);

        assertNotNull(template);
        assertNotNull(template.get(DamGenerator.TEMPLATE_NAME));
        assertTrue(((String) template.get(DamGenerator.TEMPLATE_NAME)).contains(DamGenerator.TEMPLATE_NAME_PREFIX));

        assertNotNull(template.get(DamGenerator.TEMPLATE_VERSION));
        assertTrue(((String) template.get(DamGenerator.TEMPLATE_VERSION)).contains(DamGenerator.DEFAULT_TEMPLATE_VERSION));

        assertNotNull(template.get(DamGenerator.IMPORTS));
        assertTrue(template.get(DamGenerator.IMPORTS) instanceof List);
        List imports = (List) template.get(DamGenerator.IMPORTS);
        assertEquals(imports.size(), 2);
        assertTrue(imports.contains(DamGenerator.TOSCA_NORMATIVE_TYPES + ":" + DamGenerator.TOSCA_NORMATIVE_TYPES_VERSION));
        assertTrue(imports.contains(DamGenerator.SEACLOUDS_NODE_TYPES + ":" + DamGenerator.SEACLOUDS_NODE_TYPES_VERSION));

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGroupsAsTopologyChild() throws Exception {
        String adp = new Scanner(new File(Resources.getResource("generated_adp.yml").toURI())).useDelimiter("\\Z").next();

        when(fakeAgreementManager.generateAgreeemntId(((Map<String, Object>) anyObject())))
                .thenReturn(FAKE_AGREEMENT_ID);

        DamGenerator damGenerator = new DamGenerator(MONITOR_URL, MONITOR_PORT, SLA_ENDPOINT, INFLUXDB_URL, INFLUXDB_PORT);
        damGenerator.setAgreementManager(fakeAgreementManager);
        dam = damGenerator.generateDam(adp);
        template = (Map<String, Object>) yamlParser.load(dam);

        assertTrue(template.containsKey(DamGenerator.TOPOLOGY_TEMPLATE));
        Map<String, Object> topologyTemplate =
                (Map<String, Object>) template.get(DamGenerator.TOPOLOGY_TEMPLATE);

        assertTrue(topologyTemplate.containsKey(DamGenerator.GROUPS));
        Map<String, Object> topologyGroups =
                (Map<String, Object>) topologyTemplate.get(DamGenerator.GROUPS);

        assertNotNull(topologyGroups);
        assertEquals(topologyGroups.size(), 8);
        assertTrue(topologyGroups.containsKey("operation_www"));
        assertTrue(topologyGroups.containsKey("operation_webservices"));
        assertTrue(topologyGroups.containsKey("operation_db1"));
        assertTrue(topologyGroups.containsKey("add_brooklyn_location_Vultr_64gb_mc_atlanta"));
        assertTrue(topologyGroups.containsKey("add_brooklyn_location_Rapidcloud_io_Asia_HK"));
        assertTrue(topologyGroups.containsKey("add_brooklyn_location_App42_PaaS_America_US"));
        assertTrue(topologyGroups.containsKey("monitoringInformation"));
        assertTrue(topologyGroups.containsKey("sla_gen_info"));
    }


}