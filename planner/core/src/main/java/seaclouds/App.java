package seaclouds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import seaclouds.matchmaker.Matchmaker;
import seaclouds.utils.TOSCAYamlParser;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws FileNotFoundException
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
        userInput = new FileInputStream(new File("/home/michela/Documenti/nuroCase.yaml"));
                
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
        		//TODO: look for matching inside the definition file itself
        		//match requirements for a single node template
        		mm.match(TrequirementsList);
        	}
        	
        }
        
        System.out.println( "-----\nBye bye Planner!" );

        
        
        
    }
}