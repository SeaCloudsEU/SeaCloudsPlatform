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

package eu.seaclouds.platform.planner.optimizer.heuristics;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.platform.planner.optimizer.Solution;
import eu.seaclouds.platform.planner.optimizer.SuitableOptions;
import eu.seaclouds.platform.planner.optimizer.Topology;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityAnalyzer;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityInformation;
import eu.seaclouds.platform.planner.optimizer.util.YAMLoptimizerParser;



public abstract class AbstractHeuristic implements SearchMethod {

	static Logger log = LoggerFactory.getLogger(AbstractHeuristic.class);
	
	private int MAX_ITER_NO_IMPROVE = 200;
	private double MAX_TIMES_IMPROVE_REQUIREMENT=20;
	
	
	public AbstractHeuristic(int maxIter){
		MAX_ITER_NO_IMPROVE=maxIter;
		
	}
	
	public AbstractHeuristic(){	}
	
	/*@Override
	public abstract void computeOptimalSolution(SuitableOptions cloudOffers,
			Map<String, Object> applicationMap); 
	*/
	
	public void setMaxIterNoImprove(int value){
		MAX_ITER_NO_IMPROVE=value;
	}
	
	public int getMaxIterNoImprove(){
		return MAX_ITER_NO_IMPROVE;
	}

	private QualityInformation requirements=null ;
	
	
	
	
	/**
	 * @param bestSol
	 * @param applicationMap
	 * @param topology
	 * @param cloudCharacteristics
	 * @return the fitness value of the solution. If the solution does not satisfy the requirements, it returns -infty
	 */
	public double fitness(Solution bestSol,Map<String, Object> applicationMap, Topology topology, SuitableOptions cloudCharacteristics) {
		
		loadQualityRequirements(applicationMap);

		
		QualityAnalyzer qualityAnalyzer = new QualityAnalyzer();
		
		//calculates how well it satisfies performance reuquirement. Method computePerformance returns a structure because, beyond response time 
		//information, other performance-related information can be useful for guiding the search method towards better solutions
		double perfGoodness=1;
		if(requirements.existResponseTimeRequirement()){
			perfGoodness=requirements.getResponseTime() / qualityAnalyzer.computePerformance(bestSol,topology, requirements.getWorkload(),cloudCharacteristics).getResponseTime();
		}
		
		
		//calculates how well it satisfies availability reuquirement, if it exists
		double availGoodness=1;	
		if(requirements.existAvailabilityRequirement()){
		availGoodness= (1.0 - requirements.getAvailability()) / (1.0 - qualityAnalyzer.computeAvailability(bestSol, topology, cloudCharacteristics));
		}
		
		//calculates how well it satisfies cost reuquirement, if it exists
		double costGoodness=1;
		if(requirements.existCostRequirement()){
			costGoodness= requirements.getCost() / qualityAnalyzer.computeCost(bestSol, cloudCharacteristics);
		}
		
		if((perfGoodness>=1)&&(availGoodness>=1)&&(costGoodness>=1)){
			return Math.min(MAX_TIMES_IMPROVE_REQUIREMENT,perfGoodness) + 
					Math.min(MAX_TIMES_IMPROVE_REQUIREMENT, availGoodness) + 
					Math.min(MAX_TIMES_IMPROVE_REQUIREMENT,costGoodness);
		}
		else{
			//some requirement was not satisfied, so the solution cannot be considered. 
			//If a value of goodness is less than one it meant that the requirement was specified but not satisfied;
			return Double.NEGATIVE_INFINITY;
		}
		
	}
	
	/**
	 * @param sol
	 * @param applicationMap
	 * @param topology
	 * @param cloudCharacteristics
	 * This method uses performance evaluation techniques to propose the thresholds to reconfigure modules of the system until expiring the cost
	 */
	public HashMap<String,ArrayList<Double>> createReconfigurationThresholds(Solution sol,Map<String, Object> applicationMap, Topology topology, SuitableOptions cloudCharacteristics){
		
		
		loadQualityRequirements(applicationMap);

		if(requirements.existResponseTimeRequirement()){
			QualityAnalyzer qualityAnalyzer = new QualityAnalyzer();
			
			//A HashMap with all the keys of module names, and associated an arraylist with the thresholds for reconfigurations. 
			HashMap<String,ArrayList<Double>> thresholds = new HashMap<String,ArrayList<Double>>();
		
			thresholds = qualityAnalyzer.computeThresholds(sol,topology, requirements,cloudCharacteristics);
			
			return thresholds;
		}
		else{//There are not performance requirements, so no thresholds are created. 
			return null;
		}
		
	}

	
	private void loadQualityRequirements(Map<String, Object> applicationMap) {
		if(requirements==null){
			requirements= YAMLoptimizerParser.getQualityRequirements(applicationMap);
		}
		//Maybe the previous operation did not work because Requirements could not be found in the YAML. Follow an ad-hoc solution to get some requirements
		if(requirements==null){
			log.error("Quality requirements not found in the input document. Loading dummy quality requirements for testing purposes");
			requirements=YAMLoptimizerParser.getQualityRequirementsForTesting();
			
		}
		
		if(requirements.existResponseTimeRequirement()){
			loadWorkload(applicationMap);
		}
		
		
	}

	private void loadWorkload(Map<String, Object> applicationMap) {
		if(requirements.getWorkload()<0.0){
			requirements.setWorkload(YAMLoptimizerParser.getApplicationWorkload(applicationMap));
		}
		//Maybe the previous operation did not work correctly because the workload could not be found in the YAML. Follow an ad-hoc solution to get some requirements
		if(!requirements.hasValidWorkload()){
			log.error("Valid workload information not found in the input document. Loading dummy quality requirements for testing purposes");
			requirements.setWorkload(YAMLoptimizerParser.getApplicationWorkloadTest());
		}
		
	}

	public void addSolutionToAppMap(Solution currentSol,Map<String, Object> applicationMap) {
		
		for(String solkey :  currentSol){
			
			YAMLoptimizerParser.CleanSuitableOfferForModule(solkey, applicationMap);
			YAMLoptimizerParser.AddSuitableOfferForModule(solkey, currentSol.getCloudOfferNameForModule(solkey),applicationMap);
		}
		
	}
	
	
	
}
