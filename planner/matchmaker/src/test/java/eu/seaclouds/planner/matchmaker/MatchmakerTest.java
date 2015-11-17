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
import eu.seaclouds.common.tosca.ToscaSerializer;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;
import sun.java2d.loops.ScaledBlit;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

import static org.testng.AssertJUnit.*;
@Test
public class MatchmakerTest {

    @Test
    public void testMultipleConstraints() throws Exception{
        String aam = new Scanner(new File(Resources.getResource("aams/multiplereq.yml").toURI())).useDelimiter("\\Z").next();
        assertNotNull(aam);
        String offerings = new Scanner(new File(Resources.getResource("offering_examples2.yaml").toURI())).useDelimiter("\\Z").next();
        assertNotNull(offerings);

        ParsingResult<ArchiveRoot> aamResult = ToscaSerializer.fromTOSCA(aam);
        ParsingResult<ArchiveRoot> offeringsResult = ToscaSerializer.fromTOSCA(offerings);
        Map<String, NodeTemplate> offNodeTemplates = offeringsResult.getResult().getTopology().getNodeTemplates();

        Map<String, Pair<NodeTemplate, String>> off = new HashMap<>();

        for(String node : offNodeTemplates.keySet()){
            off.put(node, new Pair<NodeTemplate, String>(offNodeTemplates.get(node), ""));
        }

        Matchmaker mm = new Matchmaker();
        Map<String, HashSet<String>> res = mm.match(aamResult, off);

        assertEquals(res.get("MessageDatabase").size(), 3);
    }

    @Test
    public void testFilterOfferings() throws  Exception {
        ToscaParser parser = new ToscaParserSupplier().get();
        assertNotNull(parser);
        ParsingResult<ArchiveRoot> aamResult = parser.parseFile(Paths.get(Resources.getResource("aams/offeringsfilteraam.yml").toURI()));

        assertNotNull(aamResult);
        ParsingResult<ArchiveRoot> offeringResult = parser.parseFile(Paths.get(Resources.getResource("global.yaml").toURI()));
        Map<String, Pair<NodeTemplate, String>> offeringsToMatch = new HashMap<>();
        Map<String, NodeTemplate> offeringsNt =  offeringResult.getResult().getTopology().getNodeTemplates();
        for(String ntn : offeringsNt.keySet()){
            offeringsToMatch.put(ntn, new Pair<NodeTemplate, String>(offeringsNt.get(ntn), ntn));
        }

        Matchmaker mm = new Matchmaker();
        Map<String, HashSet<String>> res  = mm.match(aamResult, offeringsToMatch);
        assertNotNull(res);

        for(String moduleName : res.keySet()){
            for(String of : res.get(moduleName)){
                NodeTemplate nt = offeringsNt.get(of);
                AbstractPropertyValue offeringProperty = nt.getProperties().get("num_cpus");
                String spv = ((ScalarPropertyValue) offeringProperty).getValue();
                assertTrue(new Integer("2").compareTo(new Integer(spv)) >= 0); ;
            }
        }

        ParsingResult<ArchiveRoot> aamResult2 = parser.parseFile(Paths.get(Resources.getResource("aams/offeringsfilteraam2.yml").toURI()));
        assertNotNull(aamResult2);

        res  = mm.match(aamResult2, offeringsToMatch);
        assertNotNull(res);
        for(String moduleName : res.keySet()){
            for(String of : res.get(moduleName)){
                NodeTemplate nt = offeringsNt.get(of);
                AbstractPropertyValue offeringProperty = nt.getProperties().get("num_cpus");
                String spv = ((ScalarPropertyValue) offeringProperty).getValue();
                assertTrue(new Integer("2").compareTo(new Integer(spv)) > 0); ;
            }
        }

        ParsingResult<ArchiveRoot> aamResult3 = parser.parseFile(Paths.get(Resources.getResource("aams/offeringsfilteraam3.yml").toURI()));
        assertNotNull(aamResult3);

        res  = mm.match(aamResult3, offeringsToMatch);
        assertNotNull(res);
        for(String moduleName : res.keySet()){
            for(String of : res.get(moduleName)){
                NodeTemplate nt = offeringsNt.get(of);
                AbstractPropertyValue offeringProperty = nt.getProperties().get("num_cpus");
                String spv = ((ScalarPropertyValue) offeringProperty).getValue();
                assertTrue(new Integer("2").compareTo(new Integer(spv)) == 0); ;
            }
        }


}



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


    @Test
    public void testMatchmakerOutput() throws Exception {
        ToscaParser parser = new ToscaParserSupplier().get();
        assertNotNull(parser);
        ParsingResult<ArchiveRoot> aam = parser.parseFile(Paths.get(Resources.getResource("aams/atos_aam.yml").toURI()));

        ParsingResult<ArchiveRoot> offerings = parser.parseFile(Paths.get(Resources.getResource("aams/atos_offerings.yml").toURI()));

        Map<String, Pair<NodeTemplate, String>> offeringsToMatch = new HashMap<>();
        Map<String, NodeTemplate> offeringsNt =  offerings.getResult().getTopology().getNodeTemplates();
        for(String ntn : offeringsNt.keySet()){
            offeringsToMatch.put(ntn, new Pair<NodeTemplate, String>(offeringsNt.get(ntn), ntn));
        }

        Map<String, HashSet<String>> res = new Matchmaker().match(aam, offeringsToMatch);

        assertNotNull(res);
        assertEquals(res.get("webservices").size(), 1);
        assertEquals(res.get("db1").size(), 1);
        assertEquals(res.get("www").size(), 2);
    }

    @Test
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


}