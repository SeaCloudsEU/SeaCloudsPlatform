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
import eu.seaclouds.platform.planner.aamwriter.modeldesigner.DGraph;
import static org.testng.AssertJUnit.*;

@SuppressWarnings({"unused", "rawtypes", "unchecked"})
public class TranslatorTest {
    
    private static final String N3 = "db1";
    private static final String N2 = "webservices";
    private static final String N1 = "www";
    private static final String N3_TYPE = "sc_req.db1";
    private static final String N2_TYPE = "sc_req.webservices";
    private static final String N1_TYPE = "sc_req.www";
    private Object deserializedYaml;
    private Map<String, Map> nodeTemplates;
    private Map<String, Map> nodeTypes;
    private Map<String, Map> groups;
    private Map<String, Object> n1;
    private Map<String, Object> n2;
    private Map<String, Object> n3;
    private Map<String, Object> t1;
    private Map<String, Object> t2;
    private Map<String, Object> t3;
    private DGraph graph;
    
    @BeforeMethod
    public void beforeMethod() {
    }

    @BeforeClass
    public void beforeClass() throws Exception {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);

        JSONObject root = (JSONObject) TestUtils.loadJson("/3tier.json");
        graph = new DGraph(root);

        Aam aam = new Translator().translate(graph);
        String serializedYaml = yaml.dump(aam);
        System.out.println(serializedYaml);
        
        Map<String, Map> deserializedYaml = (Map) yaml.load(serializedYaml);
        Map<String, Map> topology = deserializedYaml.get("topology_template");
        nodeTemplates = topology.get("node_templates");
        nodeTypes = deserializedYaml.get("node_types");
        groups = topology.get("groups");
        
        n1 = nodeTemplates.get(N1);
        n2 = nodeTemplates.get(N2);
        n3 = nodeTemplates.get(N3);
        
        t1 = nodeTypes.get(N1_TYPE);
        t2 = nodeTypes.get(N2_TYPE);
        t3 = nodeTypes.get(N3_TYPE);
    }

    @Test
    public void testNodeTemplates() {
        assertEquals(3, nodeTemplates.size());
        assertNotNull(n1);
        assertNotNull(n2);
        assertNotNull(n3);
        assertEquals(N1_TYPE, n1.get("type"));
        assertEquals(N2_TYPE, n2.get("type"));
        assertEquals(N3_TYPE, n3.get("type"));
    }
    
    @Test
    public void testNodeTypes() {
        
        assertEquals(3, nodeTypes.size());
        assertNotNull(t1);
        assertNotNull(t2);
        assertNotNull(t3);
        
        assertEquals("seaclouds.nodes.webapp.tomcat.TomcatServer", t1.get("derived_from"));
        assertEquals("seaclouds.nodes.SoftwareComponent", t2.get("derived_from"));
        assertEquals("seaclouds.nodes.database.mysql.MySqlNode", t3.get("derived_from"));

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
        checkConnection(n1, N2);
        checkConnection(n2, N3);
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
    }
    
    @Test
    public void testQosIsNotPropertyOfNodeTemplate() {
        assertNull(getProperty(n1, "qos"));
        assertNull(getProperty(n2, "qos"));
        assertNull(getProperty(n3, "qos"));
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

    private void checkConnection(Map from, String to) {
        List<Map> requirements = (List)from.get("requirements");
        Map endpoint = searchArray(requirements, "endpoint");
        assertEquals(to, endpoint.get("endpoint"));
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
