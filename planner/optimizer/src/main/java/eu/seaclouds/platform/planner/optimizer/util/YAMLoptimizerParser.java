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

import java.util.ArrayList;  
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import eu.seaclouds.platform.planner.optimizer.CloudOffer;
import eu.seaclouds.platform.planner.optimizer.SuitableOptions;
import eu.seaclouds.platform.planner.optimizer.Topology;
import eu.seaclouds.platform.planner.optimizer.TopologyElement;
import eu.seaclouds.platform.planner.optimizer.TopologyElementCalled;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityInformation;


public class YAMLoptimizerParser {

	static Logger log = LoggerFactory.getLogger(YAMLoptimizerParser.class);
	private static int BeeingTooVerboseWithLackOfInformationInCloudOffers=3;

	
	
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
public static SuitableOptions GetSuitableCloudOptionsAndCharacteristicsForModules(String appModel, String suitableCloudOffers) {
	
	Map<String, Object> appMap = GetMAPofAPP(appModel);
	 
	 appMap=(Map<String, Object>) appMap.get(TOSCAkeywords.NODE_TEMPLATE);
	 
	 SuitableOptions options = new SuitableOptions();
	 
	 //FOR EACH OF THE APP MODULES (but in this level there are more concepts than only the modules)
	 for (Map.Entry<String, Object> entry : appMap.entrySet())
	 {
	   String potentialModuleName =   entry.getKey();
	   List<String> potentialListOfOffersNames =GetListOfSuitableOptionsForAlreadyFoundModule(entry.getValue()); 
			  // lookForSuitableOffersOfPotentialModuleName(entry.getValue(),1);
	   
	   if(potentialListOfOffersNames!=null){
		   log.debug("Found suitable options, saving their reference. Module name= " + potentialModuleName + " cloud offers=" + potentialListOfOffersNames.toString());
		   List<CloudOffer> potentialListOfOfferCharacteristics = getCloudOfferCharacteristcisByName(potentialListOfOffersNames,suitableCloudOffers);
		   options.addSuitableOptions(potentialModuleName, potentialListOfOffersNames, potentialListOfOfferCharacteristics);
	   }
		   
	  }
	    
	 
	
	return options;
}





private static List<CloudOffer> getCloudOfferCharacteristcisByName(List<String> potentialListOfOffers, String suitableCloudOffers) {
	
	Map<String, Object> cloudOffersMap = GetMAPofAPP(suitableCloudOffers);
	
	List<CloudOffer> potentiaListOfCloudOffersWithCharacteristics = new ArrayList<CloudOffer>();
	
	//for each offer, look for its characteristics
	for(String potentialOffer: potentialListOfOffers){
		CloudOffer cloudOfferCharacteristics=getAllCharacteristicsOfCloudOffer(potentialOffer,cloudOffersMap);
	
		//Iff the offer was found, add its characteristics to the list
		if(cloudOfferCharacteristics!=null){
			potentiaListOfCloudOffersWithCharacteristics.add(cloudOfferCharacteristics);
		}
		else{ //If it was not found, add an identificative Element 
			potentiaListOfCloudOffersWithCharacteristics.add(new CloudOffer(potentialOffer + " (CLOUD OFFER NOT EXISTENT IN FILE WITH CLOUD OFFERS)"));
			log.warn("Cloud offer " + potentialOffer + " was not found among cloud offer options. Potential subsequent error");
		}
	}
	
	return potentiaListOfCloudOffersWithCharacteristics;
	
	
}


@SuppressWarnings("unchecked")
private static CloudOffer getAllCharacteristicsOfCloudOffer(String potentialOffer,	Map<String, Object> cloudOffersMap) {

	
	 
	Map<String, Object> cloudMap=(Map<String, Object>) cloudOffersMap.get(TOSCAkeywords.NODE_TEMPLATE);
	 
	CloudOffer offer= new CloudOffer(potentialOffer);
	offer.setAvailability(getPropertyOfCloudOffer(TOSCAkeywords.CLOUD_OFFER_PROPERTY_AVAILABILITY,(Map<String, Object>)cloudMap.get(potentialOffer)));
	offer.setPerformance(getPropertyOfCloudOffer(TOSCAkeywords.CLOUD_OFFER_PROPERTY_PERFORMANCE,(Map<String, Object>)cloudMap.get(potentialOffer)));
	offer.setCost(getPropertyOfCloudOffer(TOSCAkeywords.CLOUD_OFFER_PROPERTY_COST,(Map<String, Object>)cloudMap.get(potentialOffer)));
	 
	return offer;
}


private static double getPropertyOfCloudOffer(String cloudOfferProperty, Map<String, Object> singleOfferMap) {
	
	Map<String, Object> propertiesOfOffer = (Map<String, Object>)singleOfferMap.get(TOSCAkeywords.CLOUD_OFFER_PROPERTIES_TAG);
	
	double valueOfProperty=0.0;
	
	
	if(propertiesOfOffer.containsKey(cloudOfferProperty)){
		//If there is an error here, treat the value returned in the Map as List<String> instead of as String; i.e., add a .get(0)
		valueOfProperty = Double.valueOf((propertiesOfOffer.get(cloudOfferProperty).toString())).doubleValue();
	}
	else{
		//Many times it will not exist the value and it will return 0
		//Try to make theo output less verbose
		if(BeeingTooVerboseWithLackOfInformationInCloudOffers>0){
		log.info("Property " + cloudOfferProperty + " not found. REAL SOLUTION CANNOT BE COMPUTED in case that " 
				+ cloudOfferProperty + " requirement existed in the system");
		BeeingTooVerboseWithLackOfInformationInCloudOffers--;
		}
		//valueOfProperty=0.99;
	}
	
	return valueOfProperty;
	
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


public static Map<String, Object> getMAPofCloudOffers(String cloudOfferString) {
	 Yaml yamlApp = new Yaml();
	 Map<String, Object> cloudOfferFileMap = (Map<String, Object>) yamlApp.load(cloudOfferString);
	 if(!cloudOfferFileMap.containsKey(TOSCAkeywords.NODE_TEMPLATE)){
		 log.warn("YAML with Cloud offers information malformed (not found template). Expecting errors in the execution");
		 return null;
	 }
	
	 Map<String, Object>cloudOffers = null;
	
	 try{
		cloudOffers = (Map<String, Object>) cloudOfferFileMap.get(TOSCAkeywords.NODE_TEMPLATE);
	}catch(ClassCastException E){
		log.warn("YAML with Cloud offers information malformed (mapping of information of cloud offers). Expecting errors in the execution");
		cloudOffers=null;
	}
	 return cloudOffers;
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


@SuppressWarnings("unchecked")
public static QualityInformation getQualityRequirements(Map<String, Object> applicationMap) {
	
	Map<String,Object> appReqs=null; 
	
	if(!applicationMap.containsKey(TOSCAkeywords.APP_QOS_REQUIREMENTS)){
		return null;
	}
		
	try{
		appReqs= (Map<String,Object>) applicationMap.get(TOSCAkeywords.APP_QOS_REQUIREMENTS);
	}
	catch(ClassCastException E){
		 log.warn("Application QoS requirements tag found, but it did not contain the right MAP format");
		 return null;
	}
	
	QualityInformation quality = new QualityInformation();
	
	//check availability
	if(appReqs.containsKey(TOSCAkeywords.APP_AVAILABILITY_REQUIREMENTS)){
		quality.setAvailability(Double.valueOf(((String) (appReqs.get(TOSCAkeywords.APP_AVAILABILITY_REQUIREMENTS)))).doubleValue());
	}
	
	//check performance
	if(appReqs.containsKey(TOSCAkeywords.APP_PERFORMANCE_REQUIREMENTS)){
		quality.setResponseTime(Double.valueOf(((String) (appReqs.get(TOSCAkeywords.APP_PERFORMANCE_REQUIREMENTS)))).doubleValue());
	}
	
	//check cost
	if(appReqs.containsKey(TOSCAkeywords.APP_COST_REQUIREMENTS)){
		quality.setCost(Double.valueOf(((String) (appReqs.get(TOSCAkeywords.APP_COST_REQUIREMENTS)))).doubleValue());
	}
	
	//check whether any of them existed
	if(quality.existAvailabilityRequirement() || quality.existCostRequirement() || quality.existResponseTimeRequirement()){
		return quality;
	}
	else{
		log.info("There was not found any quality requirement in the application");
		return null;
	}


	
}


@SuppressWarnings("unchecked")
public static double getApplicationWorkload(Map<String, Object> applicationMap) {
	
	Map<String,Object> appReqs=null; 
	
	if(!applicationMap.containsKey(TOSCAkeywords.APP_QOS_REQUIREMENTS)){
		return -1;
	}
		
	try{
		appReqs= (Map<String,Object>) applicationMap.get(TOSCAkeywords.APP_QOS_REQUIREMENTS);
	}
	catch(ClassCastException E){
		 log.warn("Application QoS requirements tag found, but it did not contain the right MAP format");
		 return -1;
	}
	
	//check existence of workload information
	if(appReqs.containsKey(TOSCAkeywords.APP_EXPECTED_WORKLOAD)){
		return (Double.valueOf(((String) (appReqs.get(TOSCAkeywords.APP_EXPECTED_WORKLOAD)))).doubleValue());
	}
	else{
		return -1;
	}
	

}


public static Topology getApplicationTopology(Map<String, Object> appMap, Map<String, Object> allCloudOffers) {
	
	Map.Entry<String,Object> initialElement = getInitialElement(appMap);
	
	Topology topology = new Topology();
	
	//gets the topology of the connected graph to element passed as argument. 
	return getApplicationTopologyRecursive(initialElement.getKey(), 
										(Map<String, Object>) initialElement.getValue(),topology,(Map<String, Object>) appMap.get(TOSCAkeywords.NODE_TEMPLATE),
										allCloudOffers);
	

}


private static Topology getApplicationTopologyRecursive(String elementName, Map<String, Object> element, Topology topology,Map<String, Object> modules, Map<String, Object> allCloudOffers) {
	
	// TODO Waiting for the decision on how topology is stored in the YAML
	if(topology.contains(elementName)){
		return topology;
	}
	
	TopologyElement newelement= new TopologyElement(elementName);
	double hostPerformance = getPerformanceOfOfferByName(YAMLmodulesOptimizerParser.getMeasuredPerformanceHost(element),allCloudOffers);
	newelement.setExecTime(YAMLmodulesOptimizerParser.getMeasuredExecTime(element)*hostPerformance);
	
	
	//The module does not have requiremetns
	if(!YAMLmodulesOptimizerParser.ModuleHasModuleRequirements(element, modules)){
		//Include it directly
		topology.addModule(newelement);
		return topology;
	}
	
	
	//module has requirements
	for(String moduleReqName : YAMLmodulesOptimizerParser.ModuleRequirementsOfAModule(element,modules)){
		//For each requiremnt of teh element (that is not its host but it's a module in the system)
		if(topology.contains(moduleReqName)){
			
			//The dependence may already exist as well... Check it. (Correction: if the topology is creted in the proper way, the dependence cannot exist yet)
			//Read the operational profile for the number of calls. 
			
			double opProfileBetweenModules=YAMLmodulesOptimizerParser.getOpProfileWithModule(element,moduleReqName);
					//create the dependence between these two modules by addelementcalled.
			newelement.addElementCalled(new TopologyElementCalled(topology.getModule(moduleReqName),opProfileBetweenModules));
			
		}
		else{
			//Recursive call for the moduleReqNAme, and this element and associate with this element. 
			topology=getApplicationTopologyRecursive(moduleReqName, (Map<String,Object>) modules.get(moduleReqName),topology,modules,allCloudOffers);
			double opProfileBetweenModules=YAMLmodulesOptimizerParser.getOpProfileWithModule(element,moduleReqName);
			//create the dependence between these two modules by addelementcalled.
			newelement.addElementCalled(new TopologyElementCalled(topology.getModule(moduleReqName),opProfileBetweenModules));
		}
	}
	
	//add it as a element of the topology (only if it is not already included), with its qos characteristics (performance)
	topology.addModule(newelement);
	return topology;
	 
	
	
}


@SuppressWarnings("unchecked")
private static double getPerformanceOfOfferByName(String offername, Map<String, Object> allCloudOffers) {
	

		 
		if(!allCloudOffers.containsKey(offername)){
			return 0.0;
		}
		
		
		Map<String, Object> offer =null; 
		double offerperformance = 0.0;
		try{
			offer = (Map<String, Object>) allCloudOffers.get(offername);
			offerperformance=Double.valueOf((String)offer.get(TOSCAkeywords.CLOUD_OFFER_PROPERTY_PERFORMANCE)).doubleValue();
		}catch(ClassCastException E){
			return 0.0;
		}
		return offerperformance; 
}



public static QualityInformation getQualityRequirementsForTesting() {
	
	// TODO This method should not exist in the future, when quality requirements exist as input
	log.error("Dummy requirements are: responseTime=1second , availability=0.9, cost=10");
	
	QualityInformation requirements= new QualityInformation();
	
	requirements= new QualityInformation();
	requirements.setResponseTime(1.0);
	requirements.setAvailability(0.9);
	requirements.setCost(10.0);
	requirements.setWorkload(-1.0);
	
	return requirements;
}


public static double getApplicationWorkloadTest() {
	log.error("Dummy workload is assumed to be 10 requests per second");
		return 10.0;
}


public static void AddReconfigurationThresholds(	HashMap<String, ArrayList<Double>> thresholds,	Map<String, Object> applicationMap) {
	if(thresholds!=null){
		applicationMap.put(TOSCAkeywords.RECONFIGURATION_WORKLOAD_TAG, thresholds);
	}
}



/**
 * @param yamlMap
 * @return a clone of the Map
 * It uses the funcionaalities to save as string "dump" and load to create a new Map 
 */
public static Map<String, Object> cloneYAML(Map<String, Object> yamlMap) {
	
	String stringyaml =YAMLoptimizerParser.FromMAPtoYAMLstring(yamlMap);
	Map<String, Object> newMap = YAMLoptimizerParser.GetMAPofAPP(stringyaml);	
	
	return newMap;
	
}


@SuppressWarnings("unchecked")
private static Map.Entry<String,Object> getInitialElement(Map<String, Object> appMap) {
	//We assume that The initial element is such one that is not required by anyone.
	
	appMap=(Map<String, Object>) appMap.get(TOSCAkeywords.NODE_TEMPLATE);
	 
	 
	 //FOR EACH OF THE APP MODULES (but in this level there are more concepts than only the modules)
	 for (Map.Entry<String, Object> entry : appMap.entrySet())
	 {
	   String potentialModuleName =   entry.getKey();
	   
	   //If it has requirements but nobody requires it..
	   if(YAMLmodulesOptimizerParser.ModuleHasModuleRequirements(entry.getValue(),appMap) && (!moduleIsRequiredByOthers(appMap,potentialModuleName))){
		   return entry;
	   }
	 }
	  	
	   log.warn("Initial element not found unveiling the typology. Possible circular dependences in the design. Please, state clearly which the initial element is");
	   return null;
}


private static boolean moduleIsRequiredByOthers(Map<String, Object> appMap, String potentialModuleName) {
	
	for (Map.Entry<String, Object> entry : appMap.entrySet()){
		if(YAMLmodulesOptimizerParser.ModuleRequirementFromTo(entry.getValue(),potentialModuleName)){
			return true;
		}
	 }
	return false;
}



	

	



	
}

