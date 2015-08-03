/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
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
package eu.seaclouds.planner.matchmaker;

import alien4cloud.model.components.AbstractPropertyValue;
import alien4cloud.model.components.IndexedNodeType;
import alien4cloud.model.components.PropertyDefinition;
import alien4cloud.model.components.ScalarPropertyValue;
import alien4cloud.model.topology.NodeTemplate;
import alien4cloud.model.topology.Topology;
import alien4cloud.tosca.model.ArchiveRoot;
import alien4cloud.tosca.parser.ParsingResult;
import alien4cloud.tosca.parser.ToscaParser;
import com.google.common.io.Resources;
import eu.seaclouds.common.tosca.ToscaParserSupplier;
import org.testng.annotations.Test;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;

import static org.testng.AssertJUnit.*;

public class MatchmakerTest {

    @Test
    public void testPropertyMatch() throws Exception {
        ToscaParser parser = new ToscaParserSupplier().get();
        assertNotNull(parser);
        ParsingResult<ArchiveRoot> pr = parser.parseFile(Paths.get(Resources.getResource("aams/simpleAAM.yml").toURI()));

        Topology t = pr.getResult().getTopology();
        assertNotNull(t);

        Map<String, NodeTemplate> nts = t.getNodeTemplates();
        assertNotNull(nts);
        assertEquals(1, nts.size());

        NodeTemplate testTemplate = nts.get("simpleModule");
        assertNotNull(testTemplate);

        Map<String, AbstractPropertyValue> properties = testTemplate.getProperties();
        assertEquals(1, properties.size());

        AbstractPropertyValue p = properties.get("num_cpus");
        assertNotNull(p);
        assertEquals("2", ((ScalarPropertyValue) p).getValue());
        assertTrue(true);
    }

    //@Test
    public void testSimpleMatchingProp() throws Exception{
        ToscaParser parser = new ToscaParserSupplier().get();
        assertNotNull(parser);
        ParsingResult<ArchiveRoot> pr = parser.parseFile(Paths.get(Resources.getResource("aams/matchingProp.yml").toURI()));

        Topology t = pr.getResult().getTopology();
        assertNotNull(t);

        NodeTemplate offering = t.getNodeTemplates().get("my_app");
        assertNotNull(offering);

        IndexedNodeType reqModule = pr.getResult().getNodeTypes().get("moduleReq");
        assertNotNull(reqModule);

        Matchmaker mm = new Matchmaker();

        Map<String, HashSet<String>> matchmakeRes = mm.match(pr.getResult().getNodeTypes(), t.getNodeTemplates());
        assertNotNull(matchmakeRes);
        assertEquals(1, matchmakeRes.size());
        assertEquals(1, matchmakeRes.get("moduleReq").size());

        AbstractPropertyValue offProp = offering.getProperties().get("num_cpus");
        assertNotNull(offProp);

        PropertyDefinition reqProp = reqModule.getProperties().get("num_cpus");
        assertNotNull(reqProp);
    }

    @Test
    public void demoAAMTest() throws Exception {
        ToscaParser parser = new ToscaParserSupplier().get();
        assertNotNull(parser);
        ParsingResult<ArchiveRoot> pr = parser.parseFile(Paths.get(Resources.getResource("aams/DemoAAM.yml").toURI()));

        ParsingResult<ArchiveRoot> pr2 = parser.parseFile(Paths.get(Resources.getResource("aams/Offerings.yml").toURI()));

        Map<String, HashSet<String>> res = new Matchmaker().match(pr, pr2.getResult().getTopology().getNodeTemplates());

        assertNotNull(res);
        assertEquals(1, res.get("java_ee_server").size());
        assertEquals(1, res.get("db").size());
    }

}