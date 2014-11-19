package seaclouds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import seaclouds.matchmaker.Matchmaker;
import seaclouds.utils.TOSCAYamlParser;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
        System.out.println( "Hello Planner!\n-----" );
        InputStream userInput = null;
        
        if (args.length > 0){
        	try {
        		userInput = new FileInputStream(new File(args[0]));
			} catch (FileNotFoundException e) {
				// TODO: handle exception
				System.err.println("Yaml file expected for user input. Using default file");
				
			}
        	
        }
        
		//userInput = new FileInputStream(new File("/home/michela/Documenti/sampleYaml.yaml"));
        userInput = new FileInputStream(new File("/home/michela/Dropbox/SeaCloudsPersonal/documentsProduced/integrationMeeting/nuroCase.yaml"));
                
        TOSCAYamlParser userAppModel = new TOSCAYamlParser (userInput);
        
        Map<String, Object> userNodeTemplateList = userAppModel.getNodeTemplates();
        Matchmaker mm = new Matchmaker();

        //TODO: wrap NodeTemplate in a class

        //retrieve node templates names
        Set<String> TnodeTemplatesKeynames = userNodeTemplateList.keySet(); 
        
        //retrieve node templates requirements
        for (String key : TnodeTemplatesKeynames){
        	System.out.println("\n\nProcessing requirements for node template " + key);

        	//extract node templates
        	Map<String, Object> TnodeTemplate = (Map) userNodeTemplateList.get(key);
        	if (!TnodeTemplate.containsKey("requirements")){
        		System.err.println("No requirements specified for node template: " + key + "\n.");
        	}
        	else {
        		//retrieve requirements list
        		List TrequirementsList = (List) TnodeTemplate.get("requirements");
        		
        		for (int i = 0; i < TrequirementsList.size(); i++){
        			//extract requirement name
        			Map<String, Object> reqDescription = (Map) TrequirementsList.get(i);
        			Set<String> reqKeynames = reqDescription.keySet();
        			String[] TreqKeys = reqKeynames.toArray(new String[0]);
        			
        			//TOSCA requirement names: dependency, connection, hosting...
        			String reqName = TreqKeys[0];
        			
        			//extract requirement value
        			String reqValue = (String) reqDescription.get(reqName);
        			
        			System.out.println("Searching a match for requirement: " + reqName + ": " + reqValue);
        			//TODO: look for matching inside the definition file itself
        			if (userNodeTemplateList.containsKey(reqValue)){
        				//extract service description
        				Map<String, Object> serviceDescription = (Map) userNodeTemplateList.get(reqValue);

        				//check if serviceDescription includes reqName in capabilities
        				Map<String, Object> capabilitiesList = (Map) serviceDescription.get("capabilities");
        				if (capabilitiesList.containsKey(reqName)){
        					//TODO: check if capability: offName: offValue match with the name of the node asking for requirement
        					System.out.println("Requirement already satisfied inside the definitions file\n");
        				}
        			}
        			else {
                		//match requirements for a single node template and a single requirement (one list of services for each requirement in each node)
        				Map<String, Object> suitableServiceList = mm.match(reqName, reqValue, reqDescription);
        				//add the list of suitable services to the requirement section
        				if (!suitableServiceList.isEmpty()){
        					//alternatively is possible to include the whole service descriptions
        					Set<String> suitableServiceNames = (Set<String>) suitableServiceList.keySet();
        					reqDescription.put("suitableServices", suitableServiceNames);
        					System.out.println("Found " + suitableServiceList.size() + " service(s) for this requirement");
        					//update reqDescription
        					TrequirementsList.set(i, reqDescription);
        					//update TnodeTemplate
        					TnodeTemplate.put("requirements", TrequirementsList);
        					//update TOSCAdefinitions
        					userAppModel.setNodeTemplate(key, TnodeTemplate);
        		
        				}
        				
        				else {
        					System.out.println("Found no service(s) for this requirement");
        				}
        				
        				//System.out.print(true);
        			}
        		}
        	
        	}
        	
        }
        
        System.out.println();
        byte[] text = new byte[20];
        System.in.read(text);
        
        
        userAppModel.writeYaml();
        
        System.out.println( "-----\nBye bye Planner!" );

        
        
        
    }
}