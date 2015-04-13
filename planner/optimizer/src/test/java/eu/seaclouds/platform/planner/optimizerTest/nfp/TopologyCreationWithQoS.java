package eu.seaclouds.platform.planner.optimizerTest.nfp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.platform.planner.optimizer.util.TOSCAkeywords;
import eu.seaclouds.platform.planner.optimizer.util.YAMLmodulesOptimizerParser;
import eu.seaclouds.platform.planner.optimizer.util.YAMLoptimizerParser;

public class TopologyCreationWithQoS {

	private static final String APP_MODEL_FILENAME="./src/test/java/eu/seaclouds/platform/planner/optimizerTest/resources/Matchmakeroutput.yaml";
	private static final String APP_MODEL_OUTPUT_FILENAME="./src/test/java/eu/seaclouds/platform/planner/optimizerTest/resources/MatchmakeroutputWithQoSAutomatic.yaml";
	private static final String MACHINE_TESTED_MODULES="AWS.compute.c1.medium";

	private static final double RESPONSE_TIME_REQUIREMENT_VALUE_MILLIS = 2000;
	private static final double AVAILABILITY_REQUIREMENT_VALUE = 0.99;
	private static final double COST_REQUIREMENT_VALUE_MOTHLY = 1500;
	private static final double APP_EXPECTED_WORKLOAD_MINUTE = 100;
	private static final double NUM_UTILIZATIONS_OP_PROFILE = 1;
	
	private static final double[] POSSIBLE_EXEC_TIMES={50.0,25.0};

	static Logger log = LoggerFactory.getLogger(TopologyCreationWithQoS.class);
	

	public static void main(String[] args) {

		
		log.info("Starting TOPOLOGY creation");
		
		
		final String dir = System.getProperty("user.dir");
		log.debug("Trying to open files: current executino dir = " + dir);
		
		String appModel=null;
		try {
			appModel=filenameToString(APP_MODEL_FILENAME);
		} catch (IOException e) {
			log.error("File for APPmodel not found");
			e.printStackTrace();
		}
		
		Map<String,Object> appMap = YAMLoptimizerParser.GetMAPofAPP(appModel);
		HashMap<String,Object> qosRequsMap = createMapOfQoSreqs();
		appMap.put(TOSCAkeywords.APP_QOS_REQUIREMENTS, qosRequsMap);
		
		Map<String, Object> appModulesMap=(Map<String, Object>) appMap.get(TOSCAkeywords.NODE_TEMPLATE);
	
		addModulePerformanceAndOperationalProfile(appModulesMap);
		
		saveFile(YAMLoptimizerParser.FromMAPtoYAMLstring(appMap),APP_MODEL_OUTPUT_FILENAME);
		
	}
	
	
	private static void addModulePerformanceAndOperationalProfile(Map<String, Object> modulesMap) {
		
		 for (Map.Entry<String, Object> entry : modulesMap.entrySet()){
			 
			 //The next commands are executed even not knowing whether the entry is a module. 
			 Map<String,Object> moduleQoSprops = new HashMap<String,Object>();
			 moduleQoSprops.put(TOSCAkeywords.MODULE_QOS_PERFORMANCE_MILLIS, POSSIBLE_EXEC_TIMES[(int)Math.floor(Math.random()*(double)POSSIBLE_EXEC_TIMES.length)]);
			 moduleQoSprops.put(TOSCAkeywords.MODULE_QOS_PERFORMANCE_LOCATION, MACHINE_TESTED_MODULES);
			 
			 Map<String,Object> opprofile=createOpProfile(entry.getValue(),modulesMap);
			 if(opprofile!=null){//there were required modules
				 moduleQoSprops.put(TOSCAkeywords.MODULE_QOS_OPERATIONAL_PROFILE, opprofile);
				 
			 }
			 
			 //Now we add the information if it was a module with information (assuming all of them are modules)
			 try{
				 @SuppressWarnings("unchecked")
				Map<String,Object> moduleInfo = (Map<String,Object>) entry.getValue();
				 if(!YAMLmodulesOptimizerParser.moduleIsHostOfOther(entry.getKey(),modulesMap)){
					 moduleInfo.put(TOSCAkeywords.MODULE_QOS_PROPERTIES, moduleQoSprops);
				 }
			 }
			 catch(ClassCastException E){//It wasn't module with information in a Map. Do nothing
				}
			
			 
		 }
		
	}


	@SuppressWarnings("unchecked")
	private static Map<String, Object> createOpProfile(Object value, Map<String, Object> modulesMap) {
		 Map<String,Object> moduleInfo = null;
		 Map<String,Object> moduleReqs = null;
		 Map<String,Object> opprofile = new HashMap<String,Object>();
		try{
			 moduleInfo = (Map<String,Object>) value;	
			//value passed is a hashMap, like modules information;
			//If it does not have module requirements, it cannot have op profile
			 if(!moduleInfo.containsKey(TOSCAkeywords.MODULE_REQUIREMENTS)){
				   return null;
			 }
			 moduleReqs = (Map<String, Object>) moduleInfo.get(TOSCAkeywords.MODULE_REQUIREMENTS);
			 
		 }
		catch(ClassCastException E){//It wasn't module with information in a Map.
			return null;
		}
		
		
		   //if any of the module requirements has the name of potentialModuleName, then there is a requirement between modules. 
		   for (Map.Entry<String, Object> entry : moduleReqs.entrySet()){
			   try{
				   //If the requieremnt is a module, but not its "host"
				   if((YAMLmodulesOptimizerParser.isModuleName((String)entry.getValue(),modulesMap))&&(!entry.getKey().equals(TOSCAkeywords.MODULE_REQUIREMENTS_HOST))){
					   opprofile.put((String)entry.getValue(), NUM_UTILIZATIONS_OP_PROFILE);
				   }
			   }
			   catch(ClassCastException E){//It wasnt a string, maybe they were constraints
				}
			}
		   
		   //if there was found some requirement, return the opprofile. Otherwise return null;
		   if(opprofile.size()>0){
			   return opprofile;
		   }
		   else{
			   return null;
		   }
		
		
		
		
		
		
	}


	private static HashMap<String, Object> createMapOfQoSreqs() {
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put(TOSCAkeywords.APP_PERFORMANCE_REQUIREMENTS, RESPONSE_TIME_REQUIREMENT_VALUE_MILLIS);
		map.put(TOSCAkeywords.APP_AVAILABILITY_REQUIREMENTS, AVAILABILITY_REQUIREMENT_VALUE);
		map.put(TOSCAkeywords.APP_COST_REQUIREMENTS_MONTH, COST_REQUIREMENT_VALUE_MOTHLY);
		map.put(TOSCAkeywords.APP_EXPECTED_WORKLOAD_MINUTE, APP_EXPECTED_WORKLOAD_MINUTE);
		
		return map;
		
	}

	private static String filenameToString(String path)  throws IOException {
		  byte[] encoded = Files.readAllBytes(Paths.get(path));
		  return new String(encoded, StandardCharsets.UTF_8);
	}
	
	private static void saveFile(String dam,String outputFilename) {
		PrintWriter out =null;
		try {
			out = new PrintWriter(outputFilename);
			out.println(dam);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally
		{
		    if ( out != null){
				out.close( );
			}
		}
		
	}

}
