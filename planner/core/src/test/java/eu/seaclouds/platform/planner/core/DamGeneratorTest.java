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
@SuppressWarnings("ALL")
@Test
public class DamGeneratorTest {

    private static final String FAKE_AGREEMENT_ID = "agreement-1234567890";

    static final String MONITOR_URL = "52.48.187.2";
    static final String MONITOR_PORT = "8170";
    static final String INFLUXDB_URL = "52.48.187.2";
    static final String INFLUXDB_PORT = "8086";
    static final String GRAFANA_URL = "52.48.187.2";
    static final String GRAFANA_PORT = "3000";
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

        DamGenerator damGenerator = new DamGenerator(MONITOR_URL, MONITOR_PORT, SLA_ENDPOINT, INFLUXDB_URL, INFLUXDB_PORT, GRAFANA_URL, GRAFANA_PORT);
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

        DamGenerator damGenerator = new DamGenerator(MONITOR_URL, MONITOR_PORT, SLA_ENDPOINT, INFLUXDB_URL, INFLUXDB_PORT, GRAFANA_URL, GRAFANA_PORT);
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

    @Test
    @SuppressWarnings("unchecked")
    public void testNuroDam() throws Exception {
        String adp = new Scanner(new File(Resources.getResource("nuro/nuro_adp.yml").toURI())).useDelimiter("\\Z").next();

        when(fakeAgreementManager.generateAgreeemntId(((Map<String, Object>) anyObject())))
                .thenReturn(FAKE_AGREEMENT_ID);

        DamGenerator damGenerator = new DamGenerator(MONITOR_URL, MONITOR_PORT, SLA_ENDPOINT, INFLUXDB_URL, INFLUXDB_PORT, GRAFANA_URL, GRAFANA_PORT);
        damGenerator.setAgreementManager(fakeAgreementManager);
        dam = damGenerator.generateDam(adp);
        template = (Map<String, Object>) yamlParser.load(dam);

        String expectedDamString = new Scanner(new File(Resources.getResource("nuro/nuro_dam.yml").toURI())).useDelimiter("\\Z").next();
        Map<String, Object> expectedDam = (Map<String, Object>) yamlParser.load(expectedDamString);

        assertNotNull(template);

        Map<String, Object> generatedTopologyTemplate = (Map<String, Object>) template.get(DamGenerator.TOPOLOGY_TEMPLATE);
        Map<String, Object> expectedTopologyTemplate = (Map<String, Object>) expectedDam.get(DamGenerator.TOPOLOGY_TEMPLATE);

        Map<String, Object> generatedNodeTemplates = (Map<String, Object>) generatedTopologyTemplate.get(DamGenerator.NODE_TEMPLATES);
        Map<String, Object> expectedNodeTemplates = (Map<String, Object>) expectedTopologyTemplate.get(DamGenerator.NODE_TEMPLATES);

        /* checkapplication modules */
        assertEquals(generatedNodeTemplates.get("www"), expectedNodeTemplates.get("www"));
        assertEquals(generatedNodeTemplates.get("db"), expectedNodeTemplates.get("db"));

        /* check data collector */
        assertEquals(generatedNodeTemplates.get("modacloudsDc_www"), expectedNodeTemplates.get("modacloudsDc_www"));
        assertEquals(generatedNodeTemplates.get("seacloudsDc_www"), expectedNodeTemplates.get("seacloudsDc_www"));
        assertEquals(generatedNodeTemplates.get("seacloudsDc_db"), expectedNodeTemplates.get("seacloudsDc_db"));

        /* check offerings */
        assertEquals(generatedNodeTemplates.get("Amazon_EC2_m1_small_eu_central_1"), expectedNodeTemplates.get("Amazon_EC2_m1_small_eu_central_1"));
        assertEquals(generatedNodeTemplates.get("Amazon_EC2_m4_10xlarge_eu_west_1"), expectedNodeTemplates.get("Amazon_EC2_m4_10xlarge_eu_west_1"));

        Map<String, Object> generatedGroups = (Map<String, Object>) generatedTopologyTemplate.get(DamGenerator.GROUPS);
        Map<String, Object> expectedGroups = (Map<String, Object>) expectedTopologyTemplate.get(DamGenerator.GROUPS);

        assertEquals(generatedGroups.get("operation_www"), expectedGroups.get("operation_www"));
        assertEquals(generatedGroups.get("operation_db"), expectedGroups.get("operation_db"));
        assertEquals(generatedGroups.get("add_brooklyn_location_Amazon_EC2_m1_small_eu_central_1"), expectedGroups.get("add_brooklyn_location_Amazon_EC2_m1_small_eu_central_1"));
        assertEquals(generatedGroups.get("add_brooklyn_location_Amazon_EC2_m4_10xlarge_eu_west_1"), expectedGroups.get("add_brooklyn_location_Amazon_EC2_m4_10xlarge_eu_west_1"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAtosDam() throws Exception {
        String adp = new Scanner(new File(Resources.getResource("atos/atos_adp.yml").toURI())).useDelimiter("\\Z").next();

        when(fakeAgreementManager.generateAgreeemntId(((Map<String, Object>) anyObject())))
                .thenReturn(FAKE_AGREEMENT_ID);

        DamGenerator damGenerator = new DamGenerator(MONITOR_URL, MONITOR_PORT, SLA_ENDPOINT, INFLUXDB_URL, INFLUXDB_PORT, GRAFANA_URL, GRAFANA_PORT);
        damGenerator.setAgreementManager(fakeAgreementManager);
        dam = damGenerator.generateDam(adp);
        template = (Map<String, Object>) yamlParser.load(dam);

        String expectedDamString = new Scanner(new File(Resources.getResource("atos/atos_dam.yml").toURI())).useDelimiter("\\Z").next();
        Map<String, Object> expectedDam = (Map<String, Object>) yamlParser.load(expectedDamString);

        assertNotNull(template);

        Map<String, Object> generatedTopologyTemplate = (Map<String, Object>) template.get(DamGenerator.TOPOLOGY_TEMPLATE);
        Map<String, Object> expectedTopologyTemplate = (Map<String, Object>) expectedDam.get(DamGenerator.TOPOLOGY_TEMPLATE);

        Map<String, Object> generatedNodeTemplates = (Map<String, Object>) generatedTopologyTemplate.get(DamGenerator.NODE_TEMPLATES);
        Map<String, Object> expectedNodeTemplates = (Map<String, Object>) expectedTopologyTemplate.get(DamGenerator.NODE_TEMPLATES);

        /* checkapplication modules */
        assertEquals(generatedNodeTemplates.get("www"), expectedNodeTemplates.get("www"));
        assertEquals(generatedNodeTemplates.get("webservices"), expectedNodeTemplates.get("webservices"));
        assertEquals(generatedNodeTemplates.get("db1"), expectedNodeTemplates.get("db1"));

        /* check data collector */
        assertEquals(generatedNodeTemplates.get("modacloudsDc_www"), expectedNodeTemplates.get("modacloudsDc_www"));
        assertEquals(generatedNodeTemplates.get("javaAppDc_www"), expectedNodeTemplates.get("javaAppDc_www"));
        assertEquals(generatedNodeTemplates.get("seacloudsDc_www"), expectedNodeTemplates.get("seacloudsDc_www"));
        assertEquals(generatedNodeTemplates.get("modacloudsDc_webservices"), expectedNodeTemplates.get("modacloudsDc_webservices"));
        assertEquals(generatedNodeTemplates.get("javaAppDc_webservices"), expectedNodeTemplates.get("javaAppDc_webservices"));
        assertEquals(generatedNodeTemplates.get("seacloudsDc_webservices"), expectedNodeTemplates.get("seacloudsDc_webservices"));
        assertEquals(generatedNodeTemplates.get("modacloudsDc_db1"), expectedNodeTemplates.get("modacloudsDc_db1"));
        assertEquals(generatedNodeTemplates.get("seacloudsDc_db1"), expectedNodeTemplates.get("seacloudsDc_db1"));

        /* check offerings */
        assertEquals(generatedNodeTemplates.get("Amazon_EC2_c1_xlarge_eu_central_1"), expectedNodeTemplates.get("Amazon_EC2_c1_xlarge_eu_central_1"));
        assertEquals(generatedNodeTemplates.get("Amazon_EC2_m4_large_eu_west_1"), expectedNodeTemplates.get("Amazon_EC2_m4_large_eu_west_1"));
        assertEquals(generatedNodeTemplates.get("Amazon_EC2_t2_micro_us_east_1"), expectedNodeTemplates.get("Amazon_EC2_t2_micro_us_east_1"));

        Map<String, Object> generatedGroups = (Map<String, Object>) generatedTopologyTemplate.get(DamGenerator.GROUPS);
        Map<String, Object> expectedGroups = (Map<String, Object>) expectedTopologyTemplate.get(DamGenerator.GROUPS);

        assertEquals(generatedGroups.get("operation_www"), expectedGroups.get("operation_www"));
        assertEquals(generatedGroups.get("operation_webservices"), expectedGroups.get("operation_webservices"));
        assertEquals(generatedGroups.get("operation_db1"), expectedGroups.get("operation_db1"));
        assertEquals(generatedGroups.get("add_brooklyn_location_Amazon_EC2_c1_xlarge_eu_central_1"), expectedGroups.get("add_brooklyn_location_Amazon_EC2_c1_xlarge_eu_central_1"));
        assertEquals(generatedGroups.get("add_brooklyn_location_Amazon_EC2_t2_micro_us_east_1"), expectedGroups.get("add_brooklyn_location_Amazon_EC2_t2_micro_us_east_1"));
        assertEquals(generatedGroups.get("add_brooklyn_location_Amazon_EC2_m4_large_eu_west_1"), expectedGroups.get("add_brooklyn_location_Amazon_EC2_m4_large_eu_west_1"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWebChatDam() throws Exception {
        String adp = new Scanner(new File(Resources.getResource("webchat/webchat_adp.yml").toURI())).useDelimiter("\\Z").next();

        when(fakeAgreementManager.generateAgreeemntId(((Map<String, Object>) anyObject())))
                .thenReturn(FAKE_AGREEMENT_ID);

        DamGenerator damGenerator = new DamGenerator(MONITOR_URL, MONITOR_PORT, SLA_ENDPOINT, INFLUXDB_URL, INFLUXDB_PORT, GRAFANA_URL, GRAFANA_PORT);
        damGenerator.setAgreementManager(fakeAgreementManager);
        dam = damGenerator.generateDam(adp);
        template = (Map<String, Object>) yamlParser.load(dam);

        String expectedDamString = new Scanner(new File(Resources.getResource("webchat/webchat_dam.yml").toURI())).useDelimiter("\\Z").next();
        Map<String, Object> expectedDam = (Map<String, Object>) yamlParser.load(expectedDamString);

        assertNotNull(template);

        Map<String, Object> generatedTopologyTemplate = (Map<String, Object>) template.get(DamGenerator.TOPOLOGY_TEMPLATE);
        Map<String, Object> expectedTopologyTemplate = (Map<String, Object>) expectedDam.get(DamGenerator.TOPOLOGY_TEMPLATE);

        Map<String, Object> generatedNodeTemplates = (Map<String, Object>) generatedTopologyTemplate.get(DamGenerator.NODE_TEMPLATES);
        Map<String, Object> expectedNodeTemplates = (Map<String, Object>) expectedTopologyTemplate.get(DamGenerator.NODE_TEMPLATES);

        /* checkapplication modules */
        assertEquals(generatedNodeTemplates.get("Chat"), expectedNodeTemplates.get("Chat"));
        assertEquals(generatedNodeTemplates.get("MessageDatabase"), expectedNodeTemplates.get("MessageDatabase"));

        /* check data collector */
        assertEquals(generatedNodeTemplates.get("modacloudsDc_Chat"), expectedNodeTemplates.get("modacloudsDc_Chat"));
        assertEquals(generatedNodeTemplates.get("javaAppDc_Chat"), expectedNodeTemplates.get("javaAppDc_Chat"));
        assertEquals(generatedNodeTemplates.get("seacloudsDc_Chat"), expectedNodeTemplates.get("seacloudsDc_Chat"));
        assertEquals(generatedNodeTemplates.get("modacloudsDc_MessageDatabase"), expectedNodeTemplates.get("modacloudsDc_MessageDatabase"));
        assertEquals(generatedNodeTemplates.get("seacloudsDc_MessageDatabase"), expectedNodeTemplates.get("seacloudsDc_MessageDatabase"));

        /* check offerings */
        assertEquals(generatedNodeTemplates.get("Amazon_EC2_c1_medium_sa_east_1"), expectedNodeTemplates.get("Amazon_EC2_c1_medium_sa_east_1"));
        assertEquals(generatedNodeTemplates.get("Amazon_EC2_c1_medium_us_west_2"), expectedNodeTemplates.get("Amazon_EC2_c1_medium_us_west_2"));

        Map<String, Object> generatedGroups = (Map<String, Object>) generatedTopologyTemplate.get(DamGenerator.GROUPS);
        Map<String, Object> expectedGroups = (Map<String, Object>) expectedTopologyTemplate.get(DamGenerator.GROUPS);

        assertEquals(generatedGroups.get("operation_Chat"), expectedGroups.get("operation_Chat"));
        assertEquals(generatedGroups.get("operation_MessageDatabase"), expectedGroups.get("operation_MessageDatabase"));
        assertEquals(generatedGroups.get("add_brooklyn_location_Amazon_EC2_c1_medium_us_west_2"), expectedGroups.get("add_brooklyn_location_Amazon_EC2_c1_medium_us_west_2"));
        assertEquals(generatedGroups.get("add_brooklyn_location_Amazon_EC2_c1_medium_sa_east_1"), expectedGroups.get("add_brooklyn_location_Amazon_EC2_c1_medium_sa_east_1"));
    }
}