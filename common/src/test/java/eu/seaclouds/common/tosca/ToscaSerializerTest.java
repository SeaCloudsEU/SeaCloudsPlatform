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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Scanner;

import org.testng.annotations.Test;

import com.google.common.io.Resources;

import alien4cloud.tosca.model.ArchiveRoot;
import alien4cloud.tosca.parser.ParsingException;
import alien4cloud.tosca.parser.ParsingResult;

@Test(groups = {"Live"})
public class ToscaSerializerTest {

    public void ToscaSerializationTests() throws URISyntaxException, ParsingException, IOException {
        ParsingResult<ArchiveRoot> pr = ToscaSerializer.fromTOSCA(Paths.get(Resources.getResource("toscaTestFiles/t.yml").toURI()));
        assertNotNull(pr);
        assertEquals(pr.getResult().getTopology().getNodeTemplates().size(), 6);


        URI fileUri = Resources.getResource("toscaTestFiles/t.yml").toURI();
        String read = new Scanner(new File(fileUri)).useDelimiter("\\Z").next();

        ParsingResult<ArchiveRoot> pr2 = ToscaSerializer.fromTOSCA(read);
        assertNotNull(pr2);

        assertEquals(pr.getResult().getTopology().getNodeTemplates().size(),
                pr2.getResult().getTopology().getNodeTemplates().size());


        String serialized = ToscaSerializer.toTOSCA(pr.getResult().getTopology(), "test author", "test name", "test description");
        assertNotNull(serialized);
        ParsingResult<ArchiveRoot> pr3 = ToscaSerializer.fromTOSCA(serialized);

        assertEquals(pr.getResult().getTopology().getNodeTemplates().size(),
                pr3.getResult().getTopology().getNodeTemplates().size());


    }

}
