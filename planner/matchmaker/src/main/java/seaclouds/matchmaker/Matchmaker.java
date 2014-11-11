package seaclouds.matchmaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import seaclouds.utils.TOSCAYamlParser;

public class Matchmaker {

	public void match(List requirementsList) throws FileNotFoundException {
		// TODO Auto-generated method stub

        System.out.println("-----\nMatchmaking start");
		
		for (int i = 0; i < requirementsList.size(); i++){
			//extract requirement name
			Map<String, Object> reqDescription = (Map) requirementsList.get(i);
			Set<String> reqKeynames = reqDescription.keySet();
			String[] TreqKeys = reqKeynames.toArray(new String[0]);
			
			//TOSCA requirement names: dependency, connection, hosting...
			String reqName = TreqKeys[0];
			
			//extract requirement name
			String reqValue = (String) reqDescription.get(reqName);
			
			System.out.println("Searching a match for requirement: " + reqName + ": " + reqValue);
			
			//TODO: ask the discoverer to find services with reqName and reqValue
			
			//parse cloud input into a map (like user input)
			InputStream cloudInput = new FileInputStream(new File("/home/michela/Documenti/outputAWScompute.c1.medium.yaml"));
	        TOSCAYamlParser cloudModel = new TOSCAYamlParser (cloudInput);
	        Map<String, Object> cloudOfferedServiceList = cloudModel.getNodeTemplates();
	        
	        /*
	        System.out.println("\n" + cloudOfferedServiceList.size() + " services offered.");
	        for (Entry<String, Object> service : cloudOfferedServiceList.entrySet()){
	        	System.out.println (service.getKey() + "=" + service.getValue());
	        
	        }
	        */
	        
	       
	        for (Entry<String, Object> service: cloudOfferedServiceList.entrySet()){
	        	
	        	Map<String, Object> serviceDescription = (Map) service.getValue();
	        	
	        	//1. check if the service offers a capability equals to reqName and check if the service is of type included in reqValue

	        	//note: a service can offer more than a single capability, thus they are stored into a map
	        	Map<String, Object> capabilitiesList = (Map) serviceDescription.get("capabilities");
	        	String offServiceType = (String) serviceDescription.get("type");

	        	if (capabilitiesList.containsKey(reqName) && offServiceType.equals(reqValue)){
	        			
	        		if(matchProperties(reqDescription, serviceDescription)){
	        			 
	        			System.out.print("This service is suitable"); 
	        		}
	        		
	    	        
	        		       			      	        		
	        	}
	        	else System.out.println("Service not suitable");
	        }
	        
	        
	        
	        
	        
	       
			
		}
		
		System.out.println("-----\nMatchmaking end");
	}

	private boolean matchProperties(Map<String, Object> reqDescription,
			Map<String, Object> serviceDescription) {
		// TODO Auto-generated method stub
   		//2. matchmaking properties (reqDescripion vs serviceDescription) //3. matchmaking values	
		List reqProperties = (List) reqDescription.get("constraints");
		Map <String, Object> serviceProperties= (Map) serviceDescription.get("properties");
		
		for (int i = 0; i < reqProperties.size(); i++){
			//each property is a map
			Map<String, Object> property = (Map<String, Object>) reqProperties.get(i);
			//read property name
			for (String name : property.keySet()){
				if (serviceProperties.containsKey(name)){
					System.out.println("Property found: " + name);
				}
			}
			

		}
		
		
		
		return false;
	}

}
