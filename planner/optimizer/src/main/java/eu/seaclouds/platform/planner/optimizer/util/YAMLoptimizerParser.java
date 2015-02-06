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

package eu.seaclouds.platform.planner.optimizer.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import eu.seaclouds.platform.planner.optimizer.SuitableOptions;


public class YAMLoptimizerParser {

	static Logger log = LoggerFactory.getLogger(YAMLoptimizerParser.class);
	
	
	
	public static void CleanSuitableOfferForModule(String modulename, Map<String, Object> appMap) {
		 
		List<String> suitableOptions = GetListOfSuitableOptionsForModule(modulename, appMap);
		
		if(suitableOptions==null){
			log.warn( "Module name " + modulename + " not found in the model");
			return;
		
		}
		
		log.debug( "Found list of suitable services: " + suitableOptions.toString() + " Removing it");
		while(!suitableOptions.isEmpty()){
			suitableOptions.remove(0);
		}
		log.debug("Removal complete");
		
	}
		
	
	private static List<String> GetListOfSuitableOptionsForModule(String modulename, Map<String, Object> appMap){
		
		
		//FOR EACH OF THE APP MODULES (but in this level there are more concepts than only the modules)
		 for (Map.Entry<String, Object> entry : appMap.entrySet()){
			
		   List<String> options = FindSuitableOptionsForEntry(modulename, entry);
		   if(options!=null){
			   return options;
		   }
		 }
		 return null;
	}
	
	
	@SuppressWarnings("unchecked")
	private static List<String>	FindSuitableOptionsForEntry(String modulename, Map.Entry<String, Object> entry){	 
		   		   
		   //If module found, go deeper to find for its suitable Options
		   if(modulename.equals(entry.getKey())){
			   log.debug("Found module " + modulename + " cleaning the potential options" ); 
			   return GetListOfSuitableOptionsForAlreadyFoundModule(entry.getValue());
		   }
		   
		   //If module has not been found, look in depth for the module name (only useful in case that the YAML changes the hierarchy and module names become deeper in the tree)
		   else{
			   
			   //If follow the try/catch because I like more than the "instanceof" way. Trying always to avoid the utilization of "instanceof"...
			  
			   Map<String, Object> moduleInfo=null;
			   try{
				  moduleInfo = (Map<String, Object>) entry.getValue();   
					}
			   catch(ClassCastException E){
					return null;
				}
			   
				List<String> options = GetListOfSuitableOptionsForModule(modulename,moduleInfo);
				if(options!=null){
					  return options;
				}
		   }
		   return null;
		 
	}
		 
		 

	@SuppressWarnings("unchecked")
	private static List<String> GetListOfSuitableOptionsForAlreadyFoundModule(Object appSubMap) {
		Map<String, Object> moduleInfo=null;
		try{
		 moduleInfo = (Map<String, Object>) appSubMap;
		}
		catch(ClassCastException E){
			//If it was not a Map, nothing to do, we are not in the correct part of the model. 
			return null;
		}
				
		 
		if(moduleInfo.containsKey(TOSCAkeywords.SUITABLE_SERVICES)){
			return (List<String>) moduleInfo.get(TOSCAkeywords.SUITABLE_SERVICES);
		
		}
		
		//The recursive part
		for(Map.Entry<String, Object> entry : moduleInfo.entrySet()){
			List<String> suitableOptions=GetListOfSuitableOptionsForAlreadyFoundModule(entry.getValue());
			if(suitableOptions!=null){
				return suitableOptions;
			}
			
		}
		return null;
		
	}
		
	
		 
	



@SuppressWarnings("unchecked")
public static SuitableOptions GetSuitableCloudOptionsForModules(String appModel) {
	
	Map<String, Object> appMap = GetMAPofAPP(appModel);
	 
	 appMap=(Map<String, Object>) appMap.get(TOSCAkeywords.NODE_TEMPLATE);
	 
	 SuitableOptions options = new SuitableOptions();
	 
	 //FOR EACH OF THE APP MODULES (but in this level there are more concepts than only the modules)
	 for (Map.Entry<String, Object> entry : appMap.entrySet())
	 {
	   String potentialModuleName =   entry.getKey();
	   List<String> potentialListOfOffers =GetListOfSuitableOptionsForAlreadyFoundModule(entry.getValue()); 
			  // lookForSuitableOffersOfPotentialModuleName(entry.getValue(),1);
	   
	   if(potentialListOfOffers!=null){
		   log.debug("Found suitable options, saving their reference. Module name= " + potentialModuleName + " cloud offers=" + potentialListOfOffers.toString());
		   options.addSuitableOptions(potentialModuleName, potentialListOfOffers);
	   }
		   
	  }
	    
	 
	
	return options;
}





public static void AddSuitableOfferForModule(String moduleName, String solutionName ,Map<String, Object> applicationMap) {
	
	List<String> options = GetListOfSuitableOptionsForModule(moduleName, applicationMap);
	log.debug( "Adding selected offer " + solutionName +" to module " +moduleName + " with current suitable options " + options.toString());
	options.add(solutionName);
}


@SuppressWarnings("unchecked")
public static Map<String, Object> GetMAPofAPP(String appModel) {
	 Yaml yamlApp = new Yaml();
	 return (Map<String, Object>) yamlApp.load(appModel);
}



public static String FromMAPtoYAMLstring(Map<String, Object> appMap) {
	Yaml yamlApp = new Yaml();
	return yamlApp.dump(appMap);
}


@SuppressWarnings("unchecked")
public static void ReplaceSuitableServiceByHost(Map<String, Object> appMap) {
	
	Map<String, Object> templates = (Map<String, Object>) appMap.get(TOSCAkeywords.NODE_TEMPLATE);
	
	//FOR EACH OF THE APP MODULES (again, in this level there are more concepts than only the modules)
	 for (Map.Entry<String, Object> entry : templates.entrySet())
	 {
	   
	   if(containsSingleSuitableService(entry)){
		   String suitableService= FindSuitableOptionsForEntry( entry.getKey(),entry).get(0);
		   Map<String, Object> moduleInfo=(Map<String, Object>)entry.getValue();
		   Map<String, Object> moduleRequirements=(Map<String, Object>) moduleInfo.get(TOSCAkeywords.MODULE_REQUIREMENTS);
		   moduleRequirements.remove(TOSCAkeywords.MODULE_REQUIREMENTS_CONSTRAINTS);
		   moduleRequirements.put(TOSCAkeywords.MODULE_REQUIREMENTS_HOST, suitableService);
	   }
		   
	  }
}


private static boolean containsSingleSuitableService(Entry<String, Object> module) {
	List<String> suitableService= FindSuitableOptionsForEntry( module.getKey(),module);
	if(suitableService!=null){
		if(suitableService.size()==1){
			return true;
		}
	}
	return false;
	
}
	

	



	
}

