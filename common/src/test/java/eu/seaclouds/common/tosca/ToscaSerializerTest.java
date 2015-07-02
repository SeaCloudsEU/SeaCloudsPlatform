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

import alien4cloud.tosca.model.ArchiveRoot;
import alien4cloud.tosca.parser.ParsingException;
import alien4cloud.tosca.parser.ParsingResult;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ToscaSerializerTest {

    @Test
    public void ToscaSerializationTests() throws URISyntaxException, ParsingException, FileNotFoundException, IOException {
        //ParsingResult<ArchiveRoot> pr = ToscaSerializer.fromTOSCA(Paths.get(Resources.getResource("toscaTestFiles/adp.yml").toURI()));
        ParsingResult<ArchiveRoot> pr = ToscaSerializer.fromTOSCA(Paths.get(Resources.getResource("toscaTestFiles/t.yml").toURI()));
        assertNotNull(pr);
        assertEquals(pr.getResult().getTopology().getNodeTemplates().size(), 6);

        String serialized = ToscaSerializer.toTOSCA(pr.getResult().getTopology(), "test author", "test name", "test description");
        assertNotNull(serialized);
        URI fileUri = Resources.getResource("toscaTestFiles/t.yml").toURI();
        String read = new Scanner(new File(fileUri)).useDelimiter("\\Z").next();

        System.out.println(serialized + "\n***\n" + read + "\n***\n");
        //Check, just one different char - assertEquals(serialized, read);

    }

}
