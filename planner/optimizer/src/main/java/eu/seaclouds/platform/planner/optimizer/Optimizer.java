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



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Optimizer  {
    
	
	private static Logger log = LoggerFactory.getLogger(Optimizer.class);

	

//Optimizer uses its previously generated plan as a source when replanning.
private String previousPlan=null;


public String optimize(	 String appModel, String suitableCloudOffer){


	String outputPlan="Plan generation was not possible";
	
	if(previousPlan==null){
		OptimizerInitialDeployment initialOptimizer = new OptimizerInitialDeployment(); 
		
		try{
			outputPlan=initialOptimizer.optimize(appModel, suitableCloudOffer);
			previousPlan=outputPlan;
		}
		catch(Error E){
			log.error("Error optimizing the initial deployment");
			
		}
	}
	else{
		Reoptimizer optimizerReplanning = new Reoptimizer();
		
		try{
		outputPlan=optimizerReplanning.optimize(appModel,suitableCloudOffer);
		previousPlan=outputPlan;
		}
		catch(Error E){
			log.error("Error optimizing the Replanning");
			
		}
	}
		return outputPlan;
	
	
}
	
}
