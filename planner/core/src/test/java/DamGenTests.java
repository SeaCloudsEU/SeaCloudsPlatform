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

import com.google.common.io.Resources;
import eu.seaclouds.platform.planner.core.HttpHelper;
import eu.seaclouds.platform.planner.core.Planner;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;
import sun.net.www.http.HttpClient;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

@Test
public class DamGenTests {

    @Test
    public void damTranslation() throws Exception{
        //String adp = new Scanner(new File(Resources.getResource("example_adp.yml").toURI())).useDelimiter("\\Z").next();
        String adp = new Scanner(new File(Resources.getResource("generated_adp.yml").toURI())).useDelimiter("\\Z").next();
        Yaml yml =new Yaml();

        ArrayList<Object> groupsToAdd = new ArrayList<>();
        HashMap<String, ArrayList<String>> groups = new HashMap<>();

        Map<String, Map<String, Object>> adpYaml = (Map<String, Map<String, Object>>) yml.load(adp);
        assertNotNull(adpYaml);
        Map<String, Object> ADPgroups = adpYaml.get("groups");

        Map<String, Object> nodeTemplates = (Map<String, Object>) adpYaml.get("topology_template").get("node_templates");
        Map<String, Object> nodeTypes = (Map<String, Object>) adpYaml.get("node_types");
        assertNotNull(nodeTemplates);

        for(String moduleName:nodeTemplates.keySet()){
            Map<String, Object> module = (Map<String, Object>) nodeTemplates.get(moduleName);

            //type replacement
            String moduleType = (String) module.get("type");
            if(nodeTypes.containsKey(moduleType)){
                Map<String, Object> type = (HashMap<String, Object>) nodeTypes.get(moduleType);
                String oldType = (String) type.get("derived_from");
                if(oldType.startsWith("seaclouds.nodes.")){
                    String newType = oldType.replaceAll("seaclouds.nodes.", "org.apache.brooklyn.entity.");
                    module.put("type", newType);
                }
                assertNotNull(type);
            }


            if(module.keySet().contains("requirements")){
                ArrayList<Map<String, Object> > requirements = (ArrayList<Map<String, Object> >) module.get("requirements");
                assertNotNull(requirements);
                for(Map<String, Object> req : requirements){
                    if(req.keySet().contains("host")){
                        String host = (String) req.get("host");
                        if(!groups.keySet().contains(host)){
                            groups.put(host, new ArrayList<String>());
                        }
                        groups.get(host).add(moduleName);
                    }
                }
            }
        }
        assertNotNull(groups);

        //get brookly location from host
        int blidx = 1;
        for(String group: groups.keySet()){
            HashMap<String, Object> policyGroup = new HashMap<>();
            policyGroup.put("members", groups.get(group));

            HashMap<String, Object> cloudOffering = (HashMap<String, Object>) nodeTemplates.get(group);
            HashMap<String, Object> properties = (HashMap<String, Object>) cloudOffering.get("properties");
            String location = (String) properties.get("location");
            String region = (String) properties.get("region");
            String hardwareId = (String) properties.get("hardwareId");


            ArrayList<HashMap<String, Object>> policy = new ArrayList<>();
            HashMap<String, Object> p = new HashMap<>();
            p.put("brooklyn.location", location + ":" + region);
            policy.add(p);

            policyGroup.put("policies", policy);

            HashMap<String, Object> finalGroup = new HashMap<>();

            ADPgroups.put("add_brooklyn_location" + blidx++ ,policyGroup);
        }

        String finalDam = yml.dump(adpYaml);
        assertNotNull(finalDam);
    }
}
