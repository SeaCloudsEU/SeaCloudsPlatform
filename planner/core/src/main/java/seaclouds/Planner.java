package seaclouds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seaclouds.matchmaker.Matchmaker;
import seaclouds.utils.TOSCAYamlParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jose on 27/11/14.
 */
public class Planner {

    static Logger log = LoggerFactory.getLogger(Planner.class);

    private String applicationTopologyFile;
    //private InputStream topologyInputStrm = null;
    private TOSCAYamlParser applicationModel =null;
    private Map<String, Object> chosenServices =null;

    public Planner(String applicationTopologyFile) throws FileNotFoundException {
        this.applicationTopologyFile = applicationTopologyFile;
        chosenServices=new LinkedHashMap<String, Object>();
    }

    public void plan() throws IOException {
        buildApplicationModel();
        findBestDeploymentServices();
    }

    private void buildApplicationModel() throws FileNotFoundException {
        applicationModel = new TOSCAYamlParser (applicationTopologyFile);
    }

    private void findBestDeploymentServices() throws IOException {

        Set<String> TnodeTemplatesKeynames = applicationModel.getNodeTemplateKeyNames();
        Map<String, Object> chosenServices = new LinkedHashMap<String, Object>();

        for(String key : TnodeTemplatesKeynames){


            log.info("Processing requirements for node template " + key);
            findBestDeploymentServicesForANodeTemplate(key);
        }

        //System.out.println();
        //byte[] text = new byte[20];
        //System.in.read(text);

        //add chosen services to the yaml. TB replaced by the optimizer decision
        for (String key : chosenServices.keySet())
        {  	//TODO: replace with userAppModel.addNodeTemplate();
            applicationModel.getNodeTemplates().put(key, chosenServices.get(key));
        }

        //TODO: OPTIMIZER
        //optimizer select the best orchestration of services for application modules

        applicationModel.writeYaml();

    }

    private void findBestDeploymentServicesForANodeTemplate(String key) throws FileNotFoundException {

        //TODO refactor to NodeTemplateRequirements
        List TrequirementsList = applicationModel.getNodeTemplateRequirements(key);
        //TODO michela added here a message: error if not requirements

        for (int i = 0; i < TrequirementsList.size(); i++) {

            Map<String, Object> reqDescription = (Map) TrequirementsList.get(i);
            Set<String> reqKeynames = reqDescription.keySet();
            String[] TreqKeys = reqKeynames.toArray(new String[0]);

            //TOSCA requirement names: dependency, connection, hosting...
            String reqName = TreqKeys[0];
            //extract requirement value
            String reqValue = (String) reqDescription.get(reqName);
            System.out.println("Searching a match for requirement: " + reqName + ": " + reqValue);

            if(requireAnOtherNodeTempalte(reqValue)){

                log.warn("Requirement already satisfied inside the definitions file " + reqValue);
            }
            else{
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
        if (!suitableServiceList.isEmpty()){
            //alternatively is possible to include the whole service descriptions
            Set<String> suitableServiceNames = (Set<String>) suitableServiceList.keySet();


        					/* adding ALL the suitable services in a list IN the yaml
        					 * replaced by automagic selection of a suitable service
        					reqDescription.put("suitableServices", suitableServiceNames);
        					System.out.println("Found " + suitableServiceList.size() + " service(s) for this requirement");
        					//update reqDescription
        					TrequirementsList.set(i, reqDescription);
        					//update TnodeTemplate
        					TnodeTemplate.put("requirements", TrequirementsList);
        					//update TOSCAdefinitions
        					userAppModel.setNodeTemplate(key, TnodeTemplate);
        					*/

            //automagic selection of services for each requirement in each module
            //TODO: to be replaced by the OPTIMAL selection of services by the optimizer

            //remove constraints
            reqDescription.remove("constraints");

            if (key.equals("nuroDatabase")){
                //System.out.println("this requires Hp");
                for (String name: suitableServiceNames){
                    if (name.contains("HP")){
                        reqDescription.put("host", name);
                        chosenServices.put(name, suitableServiceList.get(name));
                        break;
                    }
                }
            }

            //select the first AWS service available
            else if (key.equals("webServer")){
                //System.out.println("this requires AWS");
                for (String name: suitableServiceNames){
                    if (name.contains("AWS")){
                        reqDescription.put("host", name);
                        chosenServices.put(name, suitableServiceList.get(name));
                        break;
                    }
                }
            }

            else {
                log.warn("This warning should never appear for the nuroCaseStudy");
            }
        }

        else {
            log.warn("Found no service(s) for this requirement");
        }
    }


    private boolean requireAnOtherNodeTempalte(String requirementValue){
        return applicationModel.getNodeTemplate(requirementValue)!=null;
    }

//    private void voidvoid() {
//
//        Matchmaker mm = new Matchmaker();
//
//        Set<String> TnodeTemplatesKeynames = applicationModel.getNodeTemplateKeyNames();
//        Map<String, Object> chosenServices = new LinkedHashMap<String, Object>();
//
//        //retrieve node templates requirements
//        for (String key : TnodeTemplatesKeynames){
//            //System.out.println("\n\nProcessing requirements for node template " + key);
//            //extract node templates
//            Map<String, Object> TnodeTemplate = applicationModel.getNodeTemplate(key);
//
//            if (!TnodeTemplate.containsKey("requirements")){
//                System.err.println("No requirements specified for node template: " + key + "\n.");
//            }
//            else {
//                //retrieve requirements list
//                List TrequirementsList = (List) TnodeTemplate.get("requirements");
//
//                for (int i = 0; i < TrequirementsList.size(); i++){
//                    //extract requirement name
//                    Map<String, Object> reqDescription = (Map) TrequirementsList.get(i);
//                    Set<String> reqKeynames = reqDescription.keySet();
//                    String[] TreqKeys = reqKeynames.toArray(new String[0]);
//
//
//
//
//                    //TOSCA requirement names: dependency, connection, hosting...
//                    String reqName = TreqKeys[0];
//
//                    //extract requirement value
//                    String reqValue = (String) reqDescription.get(reqName);
//
//                    System.out.println("Searching a match for requirement: " + reqName + ": " + reqValue);
//                    //TODO: look for matching inside the definition file itself
//
//
//
//                    if (userNodeTemplateList.containsKey(reqValue)){
//                        //extract service description
//                        Map<String, Object> serviceDescription = (Map) userNodeTemplateList.get(reqValue);
//
//                        //check if serviceDescription includes reqName in capabilities
//                        Map<String, Object> capabilitiesList = (Map) serviceDescription.get("capabilities");
//                        if (capabilitiesList.containsKey(reqName)){
//                            //TODO: check if capability: offName: offValue match with the name of the node asking for requirement
//                            System.out.println("Requirement already satisfied inside the definitions file\n");
//                        }
//                    }
//                    else {
//
//
//                        //HEREEEEEEEEEEEEEEEEEE
//                        //match requirements for a single node template and a single requirement (one list of services for each requirement in each node)
//                        Map<String, Object> suitableServiceList = mm.match(reqName, reqValue, reqDescription);
//                        //add the list of suitable services to the requirement section
//                        if (!suitableServiceList.isEmpty()){
//                            //alternatively is possible to include the whole service descriptions
//                            Set<String> suitableServiceNames = (Set<String>) suitableServiceList.keySet();
//
//
//        					/* adding ALL the suitable services in a list IN the yaml
//        					 * replaced by automagic selection of a suitable service
//        					reqDescription.put("suitableServices", suitableServiceNames);
//        					System.out.println("Found " + suitableServiceList.size() + " service(s) for this requirement");
//        					//update reqDescription
//        					TrequirementsList.set(i, reqDescription);
//        					//update TnodeTemplate
//        					TnodeTemplate.put("requirements", TrequirementsList);
//        					//update TOSCAdefinitions
//        					userAppModel.setNodeTemplate(key, TnodeTemplate);
//        					*/
//
//
//                            //automagic selection of services for each requirement in each module
//                            //TODO: to be replaced by the OPTIMAL selection of services by the optimizer
//
//                            //remove constraints
//                            reqDescription.remove("constraints");
//
//                            if (key.equals("nuroDatabase")){
//                                //System.out.println("this requires Hp");
//                                for (String name: suitableServiceNames){
//                                    if (name.contains("HP")){
//                                        reqDescription.put("host", name);
//                                        chosenServices.put(name, suitableServiceList.get(name));
//                                        break;
//                                    }
//                                }
//
//
//
//                            }
//
//                            //select the first AWS service available
//                            else if (key.equals("webServer")){
//                                //System.out.println("this requires AWS");
//                                for (String name: suitableServiceNames){
//                                    if (name.contains("AWS")){
//                                        reqDescription.put("host", name);
//                                        chosenServices.put(name, suitableServiceList.get(name));
//                                        break;
//                                    }
//
//                                }
//
//                            }
//
//                            else {
//                                System.err.println("This warning should never appear for the nuroCaseStudy");
//                            }
//
//                        }
//
//                        else {
//                            System.out.println("Found no service(s) for this requirement");
//                        }
//
//                        //System.out.print(true);
//                    }
//                }
//
//            }
//
//        }
//    }


}
