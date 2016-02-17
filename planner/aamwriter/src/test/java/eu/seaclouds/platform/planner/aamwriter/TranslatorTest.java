/**
 * Copyright 2015 Atos
 * Contact: Seaclouds
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.seaclouds.platform.planner.aamwriter;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeClass;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import eu.seaclouds.platform.planner.aamwriter.modelaam.Aam;
import eu.seaclouds.platform.planner.aamwriter.modelaam.Constraint;
import eu.seaclouds.platform.planner.aamwriter.modelaam.Policy;
import eu.seaclouds.platform.planner.aamwriter.modeldesigner.DGraph;
import eu.seaclouds.platform.planner.aamwriter.modeldesigner.DLink;
import static org.testng.AssertJUnit.*;

@SuppressWarnings({"unused", "rawtypes", "unchecked"})
public class TranslatorTest {
    
    private static final String N7 = "php_tarball";
    private static final String N6 = "php_git";
    private static final String N5 = "db_postgre";
    private static final String N4 = "db_mariadb";
    private static final String N3 = "db_mysql";
    private static final String N2 = "webservices";
    private static final String N1 = "www";
    private static final String N7_TYPE = "sc_req.php_tarball";
    private static final String N6_TYPE = "sc_req.php_git";
    private static final String N5_TYPE = "sc_req.db_postgre";
    private static final String N4_TYPE = "sc_req.db_mariadb";
    private static final String N3_TYPE = "sc_req.db_mysql";
    private static final String N2_TYPE = "sc_req.webservices";
    private static final String N1_TYPE = "sc_req.www";
    private Object deserializedYaml;
    private Map<String, Map> nodeTemplates;
    private Map<String, Map> nodeTypes;
    private Map<String, Map> groups;
    private Map<String, Object> n1;
    private Map<String, Object> n2;
    private Map<String, Object> n3;
    private Map<String, Object> n4;
    private Map<String, Object> n5;
    private Map<String, Object> n6;
    private Map<String, Object> n7;
    private Map<String, Object> t1;
    private Map<String, Object> t2;
    private Map<String, Object> t3;
    private Map<String, Object> t4;
    private Map<String, Object> t5;
    private Map<String, Object> t6;
    private Map<String, Object> t7;
    private DGraph graph;
    
    @BeforeMethod
    public void beforeMethod() {
    }

    @BeforeClass
    public void beforeClass() throws Exception {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);

        JSONObject root = (JSONObject) TestUtils.loadJson("/translator.json");
        graph = new DGraph(root);

        Aam aam = new Translator().translate(graph);
        String serializedYaml = yaml.dump(aam);
        System.out.println(serializedYaml);
        
        Map<String, Map> deserializedYaml = (Map) yaml.load(serializedYaml);
        Map<String, Map> topology = deserializedYaml.get("topology_template");
        nodeTemplates = topology.get("node_templates");
        nodeTypes = deserializedYaml.get("node_types");
        groups = deserializedYaml.get("groups");
        
        n1 = nodeTemplates.get(N1);
        n2 = nodeTemplates.get(N2);
        n3 = nodeTemplates.get(N3);
        n4 = nodeTemplates.get(N4);
        n5 = nodeTemplates.get(N5);
        n6 = nodeTemplates.get(N6);
        n7 = nodeTemplates.get(N7);
        
        t1 = nodeTypes.get(N1_TYPE);
        t2 = nodeTypes.get(N2_TYPE);
        t3 = nodeTypes.get(N3_TYPE);
        t4 = nodeTypes.get(N4_TYPE);
        t5 = nodeTypes.get(N5_TYPE);
        t6 = nodeTypes.get(N6_TYPE);
        t7 = nodeTypes.get(N7_TYPE);
    }

    @Test
    public void testNodeTemplates() {
        assertEquals(7, nodeTemplates.size());
        assertNotNull(n1);
        assertNotNull(n2);
        assertNotNull(n3);
        assertNotNull(n4);
        assertNotNull(n5);
        assertNotNull(n6);
        assertNotNull(n7);
        assertEquals(N1_TYPE, n1.get("type"));
        assertEquals(N2_TYPE, n2.get("type"));
        assertEquals(N3_TYPE, n3.get("type"));
        assertEquals(N4_TYPE, n4.get("type"));
        assertEquals(N5_TYPE, n5.get("type"));
    }
    
    @Test
    public void testNodeTypes() {
        
        assertEquals(7, nodeTypes.size());
        assertNotNull(t1);
        assertNotNull(t2);
        assertNotNull(t3);
        assertNotNull(t4);
        assertNotNull(t5);
        assertNotNull(t6);
        assertNotNull(t7);
        
        assertEquals("seaclouds.nodes.webapp.tomcat.TomcatServer", t1.get("derived_from"));
        assertEquals("seaclouds.nodes.SoftwareComponent", t2.get("derived_from"));
        assertEquals("seaclouds.nodes.database.mysql.MySqlNode", t3.get("derived_from"));
        assertEquals("seaclouds.nodes.database.mariadb.MariaDbNode", t4.get("derived_from"));
        assertEquals("seaclouds.nodes.database.postgresql.PostgreSqlNode", t5.get("derived_from"));

    }
    
    @Test
    public void testIaaS() {
        
        List<Map> constraints = getConstraints(t2, "resource_type");
        assertNotNull(constraints);
        
        Object term;
        String string = "equal";
        Map constraint = searchArray(constraints, string);

        assertNotNull(constraint);
        assertEquals("compute", constraint.get(string));
    }

    @Test
    public void testPaaS() {
        
        List<Map> constraints = getConstraints(t1, "resource_type");
        assertNotNull(constraints);
        
        Object term;
        String string = "equal";
        Map constraint = searchArray(constraints, string);

        assertNotNull(constraint);
        assertEquals("platform", constraint.get(string));
    }
    
    @Test
    public void testConnections() {
        DLink l1;
        DLink l2;
        
        l1 = graph.getLinksFrom(graph.getNode(N1)).get(0);
        checkConnection(n1, N2, l1);
        l2 = graph.getLinksFrom(graph.getNode(N2)).get(0);
        checkConnection(n2, N3, l2);
    }
    
    @Test
    public void testLanguageIsPropertyOfNodeTemplate() {
        checkProperty(n1, "language", "JAVA");
        checkProperty(n2, "language", "JAVA");
    }
    
    @Test
    public void testPassThroughProperties() {
        checkProperty(n1, "location", graph.getNode("www").getOtherProperties().get("location"));
        checkProperty(n1, "location_option", graph.getNode("www").getOtherProperties().get("location_option"));

        checkProperty(n2, "location", graph.getNode("webservices").getOtherProperties().get("location"));
        checkProperty(n2, "location_option", graph.getNode("webservices").getOtherProperties().get("location_option"));

        checkProperty(n1, "autoscale", graph.getNode("www").getOtherProperties().get("autoscale"));
        checkProperty(n3, "autoscale", graph.getNode(N3).getOtherProperties().get("autoscale"));
    }
    
    @Test
    public void testQosIsNotPropertyOfNodeTemplate() {
        assertNull(getProperty(n1, "qos"));
        assertNull(getProperty(n2, "qos"));
        assertNull(getProperty(n3, "qos"));
    }
    
    @Test
    public void testMysqlArtifact() {
        checkArtifact(n3, "creationScriptUrl", graph.getNode(N3).getArtifact());
    }
    
    @Test
    public void testNonMysqlArtifact() {
        checkArtifact(n4, "db_create", graph.getNode(N4).getArtifact());
        checkArtifact(n5, "db_create", graph.getNode(N5).getArtifact());
    }
    
    @Test
    public void testWarArtifact() {
        checkArtifact(n1, "wars.root", graph.getNode(N1).getArtifact());
    }
    
    @Test
    public void testPhpArtifact() {
        checkArtifact(n6, "git.url", graph.getNode(N6).getArtifact());
        checkArtifact(n7, "tarball.url", graph.getNode(N7).getArtifact());
    }
    
    @Test
    public void testApplicationQoSInAppQoSRequirements() {

        List<Map> frontend_policies = (List<Map>) groups.get("operation_www").get("policies");
        String reqname = Policy.AppQoSRequirements.Attributes.NAME;
        Map reqs = searchArray(frontend_policies, reqname);
        
        if (reqs == null) {
            fail("Requirements " + reqname + " not found");
        }

        checkQoSConstraint(
                (Map<String, Object>)reqs.get(reqname),
                "response_time", graph.getRequirements().getResponseTime(), Constraint.Names.LT);
        checkQoSConstraint(
                (Map<String, Object>)reqs.get(reqname),
                "availability", graph.getRequirements().getAvailability(), Constraint.Names.GT);
        checkQoSConstraint(
                (Map<String, Object>)reqs.get(reqname),
                "cost", graph.getRequirements().getCost(), Constraint.Names.LE);
        checkQoSConstraint(
                (Map<String, Object>)reqs.get(reqname),
                "workload", graph.getRequirements().getWorkload(), Constraint.Names.LE);
    }
    
    @Test
    public void testModuleQoSInQoSRequirements() {
        
        List<Map> frontend_policies = (List<Map>) groups.get("operation_webservices").get("policies");
        String reqname = Policy.ModuleQoSRequirements.Attributes.NAME;
        Map reqs = searchArray(frontend_policies, reqname);
        
        if (reqs == null) {
            fail("Requirements " + reqname + " not found");
        }
        checkQoSConstraint(
                (Map<String, Object>)reqs.get(reqname),
                MonitoringMetrics.AVERAGE_RESPONSE_TIME.getMetricName(), 1000, Constraint.Names.LT);
        checkQoSConstraint(
                (Map<String, Object>)reqs.get(reqname),
                MonitoringMetrics.APP_AVAILABLE.getMetricName(), 99.8, Constraint.Names.GT);
    }
    
    @Test
    public void testCredentialsFileInPropertiesOfFromNode() {
        for (DLink l : graph.getLinks()) {
            String credsFile = l.getCredentialsFile();
            
            Map<String, Object> from = nodeTemplates.get(l.getSource().getName());
            if (!credsFile.isEmpty()) {
                checkProperty(from, DLink.Attributes.CREDENTIALS_FILE, credsFile);
            }
        }
    }
    
    private Object getProperty(Map<String, Object> nodeTemplate, String propertyName) {
        Map properties = (Map)nodeTemplate.get("properties");
        Object actual = properties.get(propertyName);
        return actual;
    }

    private void checkLanguage(String expected, Map<String, Object> actualNodeTemplate) {
        
        String language = (String) getProperty(actualNodeTemplate, "language");
        assertEquals(expected, language);
    }
    
    private void checkProperty(Map<String, Object> nodeTemplate, String propertyName, Object expected) {
        Object actual = getProperty(nodeTemplate, propertyName);
        assertEquals(expected, actual);
    }
    
    private void checkQoSConstraint(Map<String, Object> requirements,
            String expectedName, double expectedValue, String expectedOperator) {
        
        assertTrue(requirements.containsKey(expectedName));
        Map<String, String> constraint = (Map<String, String>)requirements.get(expectedName);
        assertTrue(constraint.containsKey(expectedOperator));
        Object constraintValue = constraint.get(expectedOperator);
        
        Double actualValue;
        if (constraintValue instanceof String) {
            String[] parts = ((String) constraintValue).split(" ");
            actualValue = Double.parseDouble(parts[0]);
        } else {
            actualValue = (Double) constraintValue;
        }
        assertEquals(expectedValue, actualValue);
    }
    
    private void checkArtifact(Map<String, Object> nodeTemplate, String propName, String expectedPropValue) {
        List<Map> artifacts = (List<Map>) nodeTemplate.get("artifacts");
        Map<String, Object> artifact = searchArray(artifacts, propName);
        assertNotNull(artifact);
        String actualValue = (String) artifact.get(propName);
        assertEquals(expectedPropValue, actualValue);
    }

    private void checkConnection(Map from, String to, DLink l) {
        List<Map> requirements = (List)from.get("requirements");
        Map endpoint = searchArray(requirements, "endpoint");
        assertEquals(to, endpoint.get("endpoint"));
        if (!l.getOperationType().isEmpty()) {
            assertEquals(l.getOperationType(), endpoint.get("type"));
        }
    }
    
    private Map<String, Object> searchArray(List<Map> array, String key) {
        for (Map item: array) {
            if (item.containsKey(key)) {
                return item;
            }
        }
        return null;
    }
    
    private List<Map> getConstraints(Map<String, Object> m, String propertyName) {
        Map<String, Map> properties = (Map)m.get("properties");
        Map<String, List> property = properties.get(propertyName);
        List<Map> constraints = property.get("constraints");
        
        return constraints;
    }

}
