package seaclouds.matchmaker;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seaclouds.utils.TOSCAYamlParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Matchmaker {

    static Logger log = LoggerFactory.getLogger(Matchmaker.class);


    public Map<String, Object> match(String reqName, String reqValue, Map reqDescription) throws FileNotFoundException {
		// TODO Auto-generated method stub

        System.out.println("-----\nMatchmaking start");

		//TODO: ask the discoverer to find services with reqName and reqValue

        InputStream cloudInput;
        cloudInput = getClass().getResourceAsStream("/computeServices.yaml");

        String nuroCaseYaml = null;
        try {
            nuroCaseYaml = CharStreams.toString(new InputStreamReader(cloudInput, Charsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Closeables.closeQuietly(cloudInput);
            TOSCAYamlParser cloudModel = new TOSCAYamlParser(nuroCaseYaml);

        Map<String, Object> cloudOfferedServiceList = cloudModel.getNodeTemplates();
        Map<String, Object> suitableServiceList = new LinkedHashMap<>();
        log.info("\n" + cloudOfferedServiceList.size() + " service(s) available");

        for (Entry<String, Object> service: cloudOfferedServiceList.entrySet()){

        	String serviceName = (String) service.getKey();
        	Map<String, Object> serviceDescription = (Map) service.getValue();

        	//1. check if the service offers a capability equals to reqName and check if the service is of type included in reqValue
        	//note: a service can offer more than a single capability, thus they are stored into a map
        	Map<String, Object> capabilitiesList = (Map) serviceDescription.get("capabilities");
        	String offServiceType = (String) serviceDescription.get("type");

        	if (capabilitiesList.containsKey(reqName) && offServiceType.equals(reqValue)){
        		if(matchProperties(reqDescription, serviceDescription)){
        			log.info("Service suitable");
        			suitableServiceList.put(serviceName, serviceDescription);
        			//put serviceDescription somewhere or return its unique identifier
        		} else {
                    log.info("Service not suitable");
                }
        	} else {
                log.info("Service not suitable");
            }
        }

		//TODO: return a map with matching services for each module/requirement

		log.info("-----\nMatchmaking end");
		return suitableServiceList;
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

							log.info("Not implemented yet");
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
			return true;
		} else {
			return false;
		}
	}

	private boolean operatorMatch(Object value, Object offValue, String operator) {
		// TODO Auto-generated method stub
		switch (operator) {

		case "equal":
			return offValue.equals(value) ;

		case "greater_then":
			if (value.getClass().getSimpleName().equals("Integer")){
				Integer val = (Integer) value;
				return ((Integer) offValue > val);
			} else if (value.getClass().getSimpleName().equals("Double")){
				Double val = (Double) value;
				return ((Double) offValue > val);
			} else {
                return false;
            }

		case "greater_or_equal":
			//value comparable
			if (value.getClass().getSimpleName().equals("Integer")){
				Integer val = (Integer) value;
				return ((Integer)offValue >= val);
			} else if (value.getClass().getSimpleName().equals("Double")){
				Double val = (Double) value;
				return ((Double) offValue >= val);
			} else {
                return false;
            }

		case "less_than":
			if (value.getClass().getSimpleName().equals("Integer")){
				Integer val = (Integer) value;
                return (Integer) offValue < val;
			} else if (value.getClass().getSimpleName().equals("Double")){
				Double val = (Double) value;
                return (Double) offValue < val;
			} else {
                return false;
            }

		case "less_or_equal":
			if (value.getClass().getSimpleName().equals("Integer")){
				Integer val = (Integer) value;
                return (Integer) offValue <= val;
			} else if (value.getClass().getSimpleName().equals("Double")){
				Double val = (Double) value;
                return (Double) offValue <= val;
			} else return false;

		case "in_range":
			//dual scalar comparable: value is a list with two elements
			List<Object> dualScalar = (List<Object>) value;
			if (dualScalar.get(0).getClass().getSimpleName().equals("Integer")){
				Integer min = (Integer) dualScalar.get(0);
				Integer max = (Integer) dualScalar.get(1);
                return (Integer) offValue >= min && (Integer) offValue <= max;
			}
			else if (dualScalar.get(0).getClass().getSimpleName().equals("Double")){
				Double min = (Double) dualScalar.get(0);
				Double max = (Double) dualScalar.get(1);
				if((Double)offValue >= min && (Integer)offValue <= max){
					log.info("Valid!");
					return true;
				} else {
                    return false;
                }
			} else {
                return false;
            }

		case "valid_values":
			//TODO
			//any
			//warning: offered can be a single value OR a list '-.-
			//extract the list of validvlues
			//extract the list of offValue (can be a single value)
			Boolean isValid = false;
			//compare each valid value. ; if validvalue is present in offvalue-> isValid = true;
			//return isValid;
			log.info("Operator not implemented yet");
			break;

		case "length":
			//TODO
			//string in the specification (?) integer
			log.info("Operator not implemented yet");
			break;

		case "min_length":
			//TODO
			//as above
			log.info("Operator not implemented yet");
			break;

		case "max_length":
			//TODO
			//as above
			log.info("Operator not implemented yet");
			break;

		default:
			//TODO: handle exception
			log.info("Operator not implemented yet");
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
