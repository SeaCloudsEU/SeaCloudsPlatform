package eu.seaclouds.platform.planner.core;

import alien4cloud.tosca.parser.ParsingException;
import com.google.common.io.Resources;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;


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
    public void planTest() throws URISyntaxException, IOException, ParsingException {
        Planner planner = new Planner();
        String aam = new Scanner(new File(Resources.getResource("aam_atos_case_study10Dec.yml").toURI())).useDelimiter("\\Z").next();
        /* this file contains just a couple of offerings useful for the used AAM */
        String offerings = new Scanner(new File(Resources.getResource("offer_all.yaml").toURI())).useDelimiter("\\Z").next();
        String[] plans = planner.plan(aam, offerings);

        /* this file contains an ADP with deploy information expected to be in the generated plans */
        String expectedADP = new Scanner(new File(Resources.getResource("adp_atos_case_study10Dec.yml").toURI())).useDelimiter("\\Z").next();
        Yaml expectedADPYml = new Yaml();
        Map<String, Map<String, Object>> expectedADPYaml = (Map<String, Map<String, Object>>) expectedADPYml.load(expectedADP);
        Map<String, Object> expectedADPNodeTemplates = (Map<String, Object>) expectedADPYaml.get("topology_template").get("node_templates");

        Assert.assertNotNull(plans);
        Assert.assertTrue(plans.length>=1); 

        for (String plan : plans) {
            Yaml yml = new Yaml();
            Map<String, Map<String, Object>> adpYaml = (Map<String, Map<String, Object>>) yml.load(plan);
            Map<String, Object> planNodeTemplates = (Map<String, Object>) adpYaml.get("topology_template").get("node_templates");

            /* Check host value for www */
            ArrayList<LinkedHashMap> wwwRequirements = ((Map<String, ArrayList<LinkedHashMap>>) planNodeTemplates.get("www")).get("requirements");
            ArrayList<LinkedHashMap> expectedWwwRequirements = ((Map<String, ArrayList<LinkedHashMap>>) expectedADPNodeTemplates.get("www")).get("requirements");

            for (int i = 0; i < wwwRequirements.size(); i++) {
                LinkedHashMap requirement = wwwRequirements.get(i);
                LinkedHashMap expectedRequirement = expectedWwwRequirements.get(i);
                Assert.assertEquals(requirement.get("host"), expectedRequirement.get("host"));
            }

            /* Check host value for webservices */
            ArrayList<LinkedHashMap> webservicesRequirements = ((Map<String, ArrayList<LinkedHashMap>>) planNodeTemplates.get("webservices")).get("requirements");
            ArrayList<LinkedHashMap> expectedWebservicesRequirements = ((Map<String, ArrayList<LinkedHashMap>>) expectedADPNodeTemplates.get("webservices")).get("requirements");

            for (int i = 0; i < webservicesRequirements.size(); i++) {
                LinkedHashMap requirement = webservicesRequirements.get(i);
                LinkedHashMap expectedRequirement = expectedWebservicesRequirements.get(i);
                Assert.assertEquals(requirement.get("host"), expectedRequirement.get("host"));
            }

            /* Check host value for db1 */
            ArrayList<LinkedHashMap> db1Requirements = ((Map<String, ArrayList<LinkedHashMap>>) planNodeTemplates.get("db1")).get("requirements");
            ArrayList<LinkedHashMap> expectedDb1Requirements = ((Map<String, ArrayList<LinkedHashMap>>) expectedADPNodeTemplates.get("db1")).get("requirements");


            for (int i = 0; i < db1Requirements.size(); i++) {
                LinkedHashMap requirement = db1Requirements.get(i);
                LinkedHashMap expectedRequirement = expectedDb1Requirements.get(i);
                Assert.assertEquals(requirement.get("host"), expectedRequirement.get("host"));
            }
        }
    }

}