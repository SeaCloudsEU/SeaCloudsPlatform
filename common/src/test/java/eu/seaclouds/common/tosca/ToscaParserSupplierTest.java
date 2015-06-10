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
package eu.seaclouds.common.tosca;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.io.Resources;

import alien4cloud.tosca.model.ArchiveRoot;
import alien4cloud.tosca.parser.ParsingException;
import alien4cloud.tosca.parser.ParsingResult;
import alien4cloud.tosca.parser.ToscaParser;

public class ToscaParserSupplierTest {

    private static final String TOSCA_VERSION = "tosca_simple_yaml_1_0_0_wd03";

    @Test
    public void testCreation() {
        final ToscaParser toscaParser = new ToscaParserSupplier().get();
        assertNotNull(toscaParser);
        assertTrue(toscaParser instanceof ToscaParser);
    }

    @Test
    public void testNodeType() throws URISyntaxException, ParsingException {
        ToscaParser toscaParser = new ToscaParserSupplier().get();
        ParsingResult<ArchiveRoot> parsingResult = toscaParser.parseFile(Paths.get(Resources.getResource("tosca/SimpleProfile_wd03/parsing/tosca-node-type.yml").toURI()));
        ArchiveRoot archiveRoot = parsingResult.getResult();
        assertNotNull(archiveRoot);
        Assert.assertNotNull(archiveRoot.getArchive());
        Assert.assertEquals(TOSCA_VERSION, archiveRoot.getArchive().getToscaDefinitionsVersion());
        Assert.assertEquals(1, archiveRoot.getNodeTypes().size());
        Assert.assertNull(archiveRoot.getRelationshipTypes());
    }
}
