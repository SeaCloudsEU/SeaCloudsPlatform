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



package eu.seaclouds.platform.planner.optimizer;

import java.util.Map; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.platform.planner.optimizer.heuristics.HillClimb;
import eu.seaclouds.platform.planner.optimizer.heuristics.RandomSearch;
import eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethod;
import eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethodName;
import eu.seaclouds.platform.planner.optimizer.util.YAMLoptimizerParser;



public class OptimizerInitialDeployment {
	
	
	private SearchMethod engine; 
	static Logger log = LoggerFactory.getLogger(OptimizerInitialDeployment.class);

	public OptimizerInitialDeployment(){
		engine = new RandomSearch();
		
	}
	
	public OptimizerInitialDeployment(SearchMethodName name) {
		
		
		switch(name){
		case RANDOM: engine=new RandomSearch();
					break;
		case HILLCLIMB: engine= new HillClimb(); break;
					
		//case SIMANNEALING: engine= new SimAnnealing(); break;
		default: engine =new RandomSearch();
		//TODO:Complete with more methods
		}
		
	}


	public String[] optimize(String appModel, String suitableCloudOffer, int numPlansToGenerate) {
		
		//Get app characteristics
		Map<String, Object> appMap = YAMLoptimizerParser.GetMAPofAPP(appModel);		 
		
		//Get cloud offers
		SuitableOptions appInfoSuitableOptions = YAMLoptimizerParser.GetSuitableCloudOptionsAndCharacteristicsForModules(appModel,suitableCloudOffer);
		
		//TODO: Obtain Application topology. At 10/02/2015 this information is not included in the YAML. It's not possible to retrieve it
		Topology topology = YAMLoptimizerParser.getApplicationTopology(appMap);
		
		//TODO: Remove the following temporal management of the lack of topology. Create an incorrect and ad-hoc one to keep the system working		
		if(topology==null){
			log.error("Topology could not be parsed. Not known quantity of calls between modules. Assuming the dummy case where"
					+ "all modules are called in sequence. The order of calls is random}");
			topology = createAdHocTopologyFromSuitableOptions(appInfoSuitableOptions);
		}
		
		
		//Compute solution
		Map<String,Object>[] mapSolutions = engine.computeOptimizationProblem(appInfoSuitableOptions.clone(), appMap, topology,numPlansToGenerate);
		
		if(mapSolutions==null){
			log.error("Map returned by Search engine is null");
		}
		
		String[] stringSolutions = new String[numPlansToGenerate];
		for(int i=0; i<mapSolutions.length; i++){
			YAMLoptimizerParser.ReplaceSuitableServiceByHost(mapSolutions[i]);
			stringSolutions[i]=YAMLoptimizerParser.FromMAPtoYAMLstring(mapSolutions[i]);
		}
		
		 return stringSolutions;
	}

	
	//TODO: Remove this method to avoid finishing weird executions when the YAML does not contain all the information. 
	// Later it will be better an exception than a weird result. 
	private Topology createAdHocTopologyFromSuitableOptions(SuitableOptions appInfoSuitableOptions) {
		
		Topology topology = new Topology();
		
		TopologyElement current=null;
		TopologyElement previous=null;
		
		for(String moduleName : appInfoSuitableOptions.getStringIterator()){
			
			if(current==null){
				//first element treated. None of them needs to point at it
				current= new TopologyElement(moduleName);
				topology.addModule(current);
				
			}
			else{//There were explored already other modules
				previous=current;
				current = new TopologyElement(moduleName);
				previous.addElementCalled(current);
				topology.addModule(current);
			}		
			
		}
		
		return topology;
		
	}


}
