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

import org.json.simple.JSONObject;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
import eu.seaclouds.platform.planner.aamwriter.modeldesigner.DGraph;
import eu.seaclouds.platform.planner.aamwriter.modeldesigner.DLink;
import eu.seaclouds.platform.planner.aamwriter.modeldesigner.DNode;

public class DesignerModelTest {

    private final DNode[] EMPTY_NODES = {};
    private final DLink[] EMPTY_LINKS = {};
    private JSONObject root;
    private DGraph graph;

    @Test
    public void testLoadGraph() throws Exception {
        root = (JSONObject) TestUtils.loadJson("/3tier.json");
        graph = new DGraph(root);

        graph = new DGraph(root);
        assertNotNull(graph);

        assertNotNull(graph.getNodes());

        assertEquals(graph.getNodes().size(), 3);
        DNode[] nodes = graph.getNodes().toArray(EMPTY_NODES);

        assertEquals(nodes[0].getName(), "www");
        assertEquals(nodes[1].getName(), "webservices");
        assertEquals(nodes[2].getName(), "db1");

        assertEquals(graph.getLinks().size(), 2);
        DLink[] links = graph.getLinks().toArray(EMPTY_LINKS);

        assertEquals(links[0].getSource(), nodes[0]);
        assertEquals(links[0].getTarget(), nodes[1]);

        assertEquals(links[1].getSource(), nodes[1]);
        assertEquals(links[1].getTarget(), nodes[2]);
    }

    @Test
    public void testFrontendNodeIsEmpty() throws Exception {
        root = (JSONObject) TestUtils.loadJson("/3tier.json");
        graph = new DGraph(root);
        
        assertEquals(graph.getFrontendNode().getName(), "www");
        assertTrue(graph.getNode("www").isFrontend());
        assertFalse(graph.getNode("db1").isFrontend());
        
    }

    @Test
    public void testFrontendNodeIsFilled() throws Exception {
        root = (JSONObject) TestUtils.loadJson("/frontend.json");
        graph = new DGraph(root);
        
        assertEquals(graph.getFrontendNode().getName(), "webservices");
        assertFalse(graph.getNode("www").isFrontend());
        assertTrue(graph.getNode("webservices").isFrontend());
    }
    
    @Test
    public void testWrontFrontendName() throws Exception {
        root = (JSONObject) TestUtils.loadJson("/wrong_frontend.json");
        graph = new DGraph(root);
        
        assertEquals(graph.getFrontendNode().getName(), "www");
        assertFalse(graph.getNode("webservices").isFrontend());
        assertTrue(graph.getNode("www").isFrontend());
        assertFalse(graph.getNode("db1").isFrontend());
    }
}
