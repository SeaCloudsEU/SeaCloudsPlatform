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
package seaclouds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seaclouds.matchmaker.Matchmaker;
import seaclouds.utils.TOSCAYamlParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Planner {

    static Logger log = LoggerFactory.getLogger(Planner.class);

    private String applicationTopologyFile;
    private TOSCAYamlParser applicationModel = null;
    private Map<String, Object> chosenServices = null;

    public Planner(String applicationTopologyFile) throws FileNotFoundException {
        this.applicationTopologyFile = applicationTopologyFile;
        chosenServices = new LinkedHashMap<String, Object>();
    }

    public String plan() throws IOException {
        buildApplicationModel();
        return findBestDeploymentServices();
    }

    private void buildApplicationModelFromYamlParser(TOSCAYamlParser parser) {
        applicationModel = parser;
    }

    private void buildApplicationModel() throws FileNotFoundException {
        applicationModel = new TOSCAYamlParser(applicationTopologyFile);
    }

    private String findBestDeploymentServices() throws IOException {

        Set<String> TnodeTemplatesKeynames = applicationModel.getNodeTemplateKeyNames();
        Map<String, Object> chosenServices = new LinkedHashMap<String, Object>();

        for (String key : TnodeTemplatesKeynames) {
            log.info("Processing requirements for node template " + key);
            findBestDeploymentServicesForANodeTemplate(key);
        }

        //add chosen services to the yaml. TB replaced by the optimizer decision
        for (String key : chosenServices.keySet()) {    //TODO: replace with userAppModel.addNodeTemplate();
            applicationModel.getNodeTemplates().put(key, chosenServices.get(key));
        }

        //TODO: OPTIMIZER
        //optimizer select the best orchestration of services for application modules

        applicationModel.writeYaml();
        return applicationModel.getYaml();
    }

    private void findBestDeploymentServicesForANodeTemplate(String key) throws FileNotFoundException {

        //TODO refactor to NodeTemplateRequirements
        List TrequirementsList = applicationModel.getNodeTemplateRequirements(key);
        //TODO michela added here a message: error if not requirements

        for (Object aTrequirementsList : TrequirementsList) {

            Map<String, Object> reqDescription = (Map) aTrequirementsList;
            Set<String> reqKeynames = reqDescription.keySet();
            String[] TreqKeys = reqKeynames.toArray(new String[0]);

            //TOSCA requirement names: dependency, connection, hosting...
            String reqName = TreqKeys[0];
            //extract requirement value
            String reqValue = (String) reqDescription.get(reqName);
            log.info("Searching a match for requirement: " + reqName + ": " + reqValue);

            if (requireAnOtherNodeTemplate(reqValue)) {
                log.warn("Requirement already satisfied inside the definitions file " + reqValue);
            } else {
                selectADeploymentService(key, reqDescription);
            }
        }
    }

    private void selectADeploymentService(String key, Map<String, Object> reqDescription)
            throws FileNotFoundException {

        Matchmaker mm = new Matchmaker();

        Set<String> reqKeynames = reqDescription.keySet();
        String[] TreqKeys = reqKeynames.toArray(new String[0]);

        //TOSCA requirement names: dependency, connection, hosting...
        String reqName = TreqKeys[0];
        //extract requirement value
        String reqValue = (String) reqDescription.get(reqName);
        log.info("Searching a match for requirement: " + reqName + ": " + reqValue);


        //match requirements for a single node template and a single requirement (one list of services for each requirement in each node)
        Map<String, Object> suitableServiceList = mm.match(reqName, reqValue, reqDescription);
        //add the list of suitable services to the requirement section
        if (!suitableServiceList.isEmpty()) {
            //alternatively is possible to include the whole service descriptions
            Set<String> suitableServiceNames = suitableServiceList.keySet();

            //automagic selection of services for each requirement in each module
            //TODO: to be replaced by the OPTIMAL selection of services by the optimizer

            //remove constraints
            reqDescription.remove("constraints");

            if (key.equals("nuroDatabase")) {
                for (String name : suitableServiceNames) {
                    if (name.contains("HP")) {
                        reqDescription.put("host", name);
                        chosenServices.put(name, suitableServiceList.get(name));
                        break;
                    }
                }
            }

            //select the first AWS service available
            else if (key.equals("webServer")) {
                for (String name : suitableServiceNames) {
                    if (name.contains("AWS")) {
                        reqDescription.put("host", name);
                        chosenServices.put(name, suitableServiceList.get(name));
                        break;
                    }
                }
            }
        } else {
            log.warn("Found no service(s) for this requirement");
        }
    }


    private boolean requireAnOtherNodeTemplate(String requirementValue) {
        return applicationModel.getNodeTemplate(requirementValue) != null;
    }

}
