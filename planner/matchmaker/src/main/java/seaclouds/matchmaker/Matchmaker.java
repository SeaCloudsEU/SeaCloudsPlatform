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
			
			//extract requirement value
			String reqValue = (String) reqDescription.get(reqName);
			
			System.out.println("Searching a match for requirement: " + reqName + ": " + reqValue);
			
			//TODO: ask the discoverer to find services with reqName and reqValue
			
			//parse cloud input into a map (like user input)
			InputStream cloudInput = new FileInputStream(new File("/home/michela/Documenti/outputAWScompute.c1.medium.yaml"));
	        TOSCAYamlParser cloudModel = new TOSCAYamlParser (cloudInput);
	        Map<String, Object> cloudOfferedServiceList = cloudModel.getNodeTemplates();
	        
	      	       
	        for (Entry<String, Object> service: cloudOfferedServiceList.entrySet()){
	        	
	        	Map<String, Object> serviceDescription = (Map) service.getValue();
	        	
	        	//1. check if the service offers a capability equals to reqName and check if the service is of type included in reqValue

	        	//note: a service can offer more than a single capability, thus they are stored into a map
	        	Map<String, Object> capabilitiesList = (Map) serviceDescription.get("capabilities");
	        	String offServiceType = (String) serviceDescription.get("type");

	        	if (capabilitiesList.containsKey(reqName) && offServiceType.equals(reqValue)){
	        			
	        		if(matchProperties(reqDescription, serviceDescription)){
	        			 
	        			System.out.println("Service suitable"); 
	        			//put serviceDescription somewhere or return its unique identifier
	        		}
	        		
	    	        
	        		       			      	        		
	        	}
	        	else System.out.println("Service not suitable");
	        }
	       
			
		}
		//TODO: return a map with matching services for each module/requirement
		
		System.out.println("-----\nMatchmaking end");
	}

	
	private boolean matchProperties(Map<String, Object> reqDescription,
			Map<String, Object> serviceDescription) {
		// TODO Auto-generated method stub
   		//2. matchmaking properties (reqDescripion vs serviceDescription) //3. matchmaking values	
		List reqProperties = (List) reqDescription.get("constraints");
		Map serviceProperties= (Map) serviceDescription.get("properties");
		int suitableProperty = 0;
		
		for (int i = 0; i < reqProperties.size(); i++){
			//each property is a map
			Map<String, Object> property = (Map<String, Object>) reqProperties.get(i);
			
			//read property name
			for (String name : property.keySet()){
				if (serviceProperties.containsKey(name)){
					
					//extract property values
					Object reqValue = property.get(name);
					Object offValue = serviceProperties.get(name);
					String reqClass = reqValue.getClass().getSimpleName();
					String offClass = offValue.getClass().getSimpleName();	
					
					if (reqClass.equals("LinkedHashMap") || offClass.equals("LinkedHashMap")){
						if (reqClass.equals(offClass)){
							//TODO: both properties contain an operator
							//only permitted operator for off and req properties is "valid_values"
							Map<String, Object> mapR = (Map<String, Object>) reqValue;	
							Entry<String, String>[] entryR = (Entry<String, String>[]) mapR.entrySet().toArray(new Map.Entry[mapR.size()]);
							String operatorR =  entryR[0].getKey();
							Object valueR = entryR[0].getValue();
							
							Map<String, Object> mapO = (Map<String, Object>) offValue;	
							Entry<String, String>[] entryO = (Entry<String, String>[]) mapO.entrySet().toArray(new Map.Entry[mapO.size()]);
							String operator =  entryO[0].getKey();
							Object value = entryO[0].getValue();
							
							//at least one of the values in reqValue should be present in offValue
							
							System.out.println("Not implemented yet");
						}
						
						else if (reqClass.equals("LinkedHashMap")){
							//extract operator
							Map<String, Object> map = (Map<String, Object>) reqValue;	
							Entry<String, String>[] entry = (Entry<String, String>[]) map.entrySet().toArray(new Map.Entry[map.size()]);
							String operator =  entry[0].getKey();
							Object value = entry[0].getValue();
							
							if (operatorMatch(value, offValue, operator)) suitableProperty++;
						}
						
						else { //offClass contains "valid" operator and reqClass is a string
							Map<String, Object> map = (Map<String, Object>) offValue;	
							Entry<String, Object>[] entry = (Entry<String, Object>[]) map.entrySet().toArray(new Map.Entry[map.size()]);
							String operator =  entry[0].getKey(); //this should be a valid operator
							List value = (List) entry[0].getValue();
							
							Boolean isPresent = false;
							for (Object element : value){
								if (reqValue.equals(element)) isPresent = true;
							}
							
							if (isPresent) suitableProperty++;  //not sure if the same function is suitable
						}
					}
					
					
					if (reqClass.equals(offClass)){
						//exact match
						if(exactMatch (reqValue, offValue)) suitableProperty++;
					}
				}
			}
		}
		
		if (suitableProperty == reqProperties.size()) {
			System.out.println("Service suitable");
			return true;
		}
		else {
			System.out.println("Service not suitable");
			return false;
		}
	}

	private boolean operatorMatch(Object value, Object offValue, String operator) {
		// TODO Auto-generated method stub
		switch (operator) {

		case "equal":
			if (offValue.equals(value)) return true;
			else return false;

		case "greater_then":
			if (value.getClass().getSimpleName().equals("Integer")){
				Integer val = (Integer) value;
				if ((Integer)offValue > val) return true;
				else return false;
			}
			else if (value.getClass().getSimpleName().equals("Double")){
				Double val = (Double) value;
				if ((Double) offValue > val) return true;
				else return false;
			}
			else return false;

		case "greater_or_equal":
			//value comparable
			if (value.getClass().getSimpleName().equals("Integer")){
				Integer val = (Integer) value;
				if ((Integer)offValue >= val) return true;
				else return false;
			}
			else if (value.getClass().getSimpleName().equals("Double")){
				Double val = (Double) value;
				if ((Double) offValue >= val) return true;
				else return false;
			}
			else return false;

		case "less_than":
			if (value.getClass().getSimpleName().equals("Integer")){
				Integer val = (Integer) value;
				if ((Integer)offValue < val) return true;
				else return false;
			}
			else if (value.getClass().getSimpleName().equals("Double")){
				Double val = (Double) value;
				if ((Double) offValue < val) return true;
				else return false;
			}
			else return false;

		case "less_or_equal":
			if (value.getClass().getSimpleName().equals("Integer")){
				Integer val = (Integer) value;
				if ((Integer)offValue <= val) return true;
				else return false;
			}
			else if (value.getClass().getSimpleName().equals("Double")){
				Double val = (Double) value;
				if ((Double) offValue <= val) return true;
				else return false;
			}
			else return false;

		case "in_range":
			//dual scalar comparable: value is a list with two elements
			List<Object> dualScalar = (List<Object>) value;
			if (dualScalar.get(0).getClass().getSimpleName().equals("Integer")){
				Integer min = (Integer) dualScalar.get(0);
				Integer max = (Integer) dualScalar.get(1);
				if((Integer)offValue >= min && (Integer)offValue <= max){
					//System.out.println("Valid!");
					return true;
				}
				else return false;
			}
			else if (dualScalar.get(0).getClass().getSimpleName().equals("Double")){
				Double min = (Double) dualScalar.get(0);
				Double max = (Double) dualScalar.get(1);
				if((Double)offValue >= min && (Integer)offValue <= max){
					System.out.println("Valid!");
					return true;
				}
				else return false;
			}
			else return false;

		case "valid_values":
			//TODO
			//any
			//warning: offered can be a single value OR a list '-.-
			//extract the list of validvlues
			//extract the list of offValue (can be a single value)
			Boolean isValid = false;
			//compare each valid value. ; if validvalue is present in offvalue-> isValid = true;
			//return isValid;
			System.out.println("Operator not implemented yet");
			break;

		case "length":
			//TODO
			//string in the specification (?) integer
			System.out.println("Operator not implemented yet");
			break;

		case "min_length":
			//TODO
			//as above
			System.out.println("Operator not implemented yet");
			break;

		case "max_length":
			//TODO
			//as above
			System.out.println("Operator not implemented yet");
			break;

		default:
			//TODO: handle exception
			System.out.println("Operator not implemented yet");
			break;
		}
		return false;
	}

	private boolean exactMatch(Object reqValue, Object offValue) {
		// TODO Auto-generated method stub
		if (reqValue.equals(offValue)) return true;
		else return false;
	}

}
