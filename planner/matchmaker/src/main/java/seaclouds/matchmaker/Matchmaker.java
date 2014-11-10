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
		for (int i = 0; i < requirementsList.size(); i++){
			//extract requirement name
			Map<String, Object> userReq = (Map) requirementsList.get(i);
			Set<String> reqKeynames = userReq.keySet();
			String[] TreqKeys = reqKeynames.toArray(new String[0]);
			String reqName = TreqKeys[0];
			System.out.println("Searching a match for requirement: \n" + reqName);
			
			//extract requirement type
			String reqType = (String) userReq.get(reqName);
			
			//TODO: ask the discoverer to find services with reqName and reqType
			//parse cloud input into a map (like user input)
			InputStream cloudInput = new FileInputStream(new File("/home/michela/Documenti/outputAWScompute.c1.medium.yaml"));
	        TOSCAYamlParser cloudModel = new TOSCAYamlParser (cloudInput);
	        Map<String, Object> cloudOfferedServiceList = cloudModel.getNodeTemplates();
	        
	        System.out.println("\n" + cloudOfferedServiceList.size() + " services offered.");
	        for (Entry<String, Object> service : cloudOfferedServiceList.entrySet()){
	        	System.out.println (service.getKey() + "=" + service.getValue());
	        
	        }
	        System.out.println();
			
		}
		
	}

}
