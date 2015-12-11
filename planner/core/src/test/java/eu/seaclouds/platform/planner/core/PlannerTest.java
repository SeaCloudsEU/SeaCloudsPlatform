package eu.seaclouds.platform.planner.core;

import alien4cloud.tosca.model.ArchiveRoot;
import alien4cloud.tosca.parser.ParsingResult;
import com.google.common.io.Resources;
import eu.seaclouds.common.tosca.ToscaSerializer;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.testng.Assert.*;

/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@Test
public class PlannerTest {
    @Test
    public void addBenchmarkInfoTest () throws Exception {
        String aam = new Scanner(new File(Resources.getResource("aams/aam1.yml").toURI())).useDelimiter("\\Z").next();
        assertNotNull(aam);
        String offerings = new Scanner(new File(Resources.getResource("offerings/all_offerings.yaml").toURI())).useDelimiter("\\Z").next();
        assertNotNull(offerings);

        Yaml yml = new Yaml();
        Map<String, Object> offeringsYaml = (Map<String, Object>) yml.load(offerings);
        Map<String, Object> nodeTemplates =((Map<String, Map<String, Object>>) offeringsYaml.get("topology_template")).get("node_templates");
        assertNotNull(nodeTemplates);

        Planner p = new Planner();
        String newAAM = p.addBenchmarkInfo(aam, nodeTemplates);
        assertNotEquals(aam, newAAM);
    }

}