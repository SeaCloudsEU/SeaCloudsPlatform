package seaclouds.utils;


import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class TOSCAYamlParser {


	Map<String, Object> TOSCAdefinitions;

	public TOSCAYamlParser(InputStream userInput) {
		
		Yaml userYaml = new Yaml();
		
		//TODO: add try-catch block (userInput can be invalid)
		TOSCAdefinitions = (Map) userYaml.load(userInput);
		
		//TODO: check if empty or not valid
		
		//TODO: check if it is a valid TOSCA file
	}
	
	public String getVersion(){
		//TODO: check existence?
		String keyname = "tosca_definitions_version";
		return (String) TOSCAdefinitions.get(keyname);
	}
	
	public String getNamespace(){
		//TODO:
		System.out.println("Warning: this function has not been defined yet");
		return null;		
	}
	
	public String getTemplateName(){
		String keyname = "template_name";
		if(!TOSCAdefinitions.containsKey(keyname)){
			System.out.println("This keyname has not been defined.");
			return null;
		}
		else return (String) TOSCAdefinitions.get(keyname);		
	}
	
	public String getTemplateAuthor(){
		String keyname = "template_author";
		if(!TOSCAdefinitions.containsKey(keyname)){
			System.out.println("This keyname has not been defined.");
			return null;
		}
		else return (String) TOSCAdefinitions.get(keyname);			
	}
	
	public String getTemplateVersion(){
		String keyname = "template_version";
		if(!TOSCAdefinitions.containsKey(keyname)){
			System.out.println("This keyname has not been defined.");
			return null;
		}
		else return (String) TOSCAdefinitions.get(keyname);		
	}
	
	public String getDescription(){
		String keyname = "description";
		if(!TOSCAdefinitions.containsKey(keyname)){
			System.out.println("This keyname has not been defined.");
			return null;
		}
		else return (String) TOSCAdefinitions.get(keyname);				
	}
	
	public String getImports(){
		//TODO:
		System.out.println("Warning: this function has not been defined yet");
		return null;			
	}
	
	public String getInputs(){
		//TODO:
		System.out.println("Warning: this function has not been defined yet");
		return null;		
	}
	
	public Map<String, Object> getNodeTemplates(){
		String keyname = "node_templates";
		if(!TOSCAdefinitions.containsKey(keyname)){
			System.out.println("This keyname has not been defined.");
			return null;
		}
		else return (Map<String, Object>) TOSCAdefinitions.get(keyname);		
	}
	
	public Map<String, Object> getNodeTypes(){
		String keyname = "node_types";
		if(!TOSCAdefinitions.containsKey(keyname)){
			System.out.println("This keyname has not been defined.");
			return null;
		}
		else return (Map<String, Object>) TOSCAdefinitions.get(keyname);		
	}
	
	public String getRelationshipTypes(){
		//TODO:
		System.out.println("Warning: this function has not been defined yet");
		return null;	
	}
	
	public String getCapabilityTypes(){
		//TODO:
		System.out.println("Warning: this function has not been defined yet");
		return null;		
	}
	
	public String getArtifactTypes(){
		//TODO:
		System.out.println("Warning: this function has not been defined yet");
		return null;
	}
	
	public String getOutputs(){
		//TODO:
		System.out.println("Warning: this function has not been defined yet");
		return null;	
	}
	
	public String getGroups(){
		//TODO:
		System.out.println("Warning: this function has not been defined yet");
		return null;	
	}

	
}



