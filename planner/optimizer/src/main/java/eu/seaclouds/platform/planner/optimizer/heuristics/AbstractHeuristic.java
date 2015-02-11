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


import java.util.Map;

import eu.seaclouds.platform.planner.optimizer.Solution;
import eu.seaclouds.platform.planner.optimizer.SuitableOptions;
import eu.seaclouds.platform.planner.optimizer.Topology;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityAnalyzer;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityInformation;
import eu.seaclouds.platform.planner.optimizer.util.YAMLoptimizerParser;



public abstract class AbstractHeuristic implements SearchMethod {

	private int MAX_ITER_NO_IMPROVE = 200;
	
	
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

	public double fitness(Solution bestSol,Map<String, Object> applicationMap, Topology topology, SuitableOptions cloudCharacteristics) {
		
		
		
		QualityInformation requirements = YAMLoptimizerParser.getQualityRequirements(applicationMap);
		requirements.setWorkload(YAMLoptimizerParser.getApplicationWorkload(applicationMap));
		QualityAnalyzer qualityAnalyzer = new QualityAnalyzer();
		
		//calculates how well it satisfies performance reuquirement
		double perfGoodness= requirements.getResponseTime() / qualityAnalyzer.computePerformance(bestSol,topology, requirements.getWorkload(),cloudCharacteristics).getResponseTime() ;
		//calculates how well it satisfies performance reuquirement
		double availGoodness= (1.0 - requirements.getAvailability()) / (1.0 - qualityAnalyzer.computeAvailability(bestSol));
		//calculates how well it satisfies performance reuquirement
		double costGoodness= requirements.getCost() / qualityAnalyzer.computeCost(bestSol);
		
		if((perfGoodness>=0)&&(availGoodness>=0)&&(costGoodness>=0)){
			return perfGoodness + availGoodness + costGoodness;
		}
		else{
			//some requirement was not satisfied, so the solution cannot be considered
			return Double.NEGATIVE_INFINITY;
		}
		
	}

	
	public void addSolutionToAppMap(Solution currentSol,Map<String, Object> applicationMap) {
		
		for(String solkey :  currentSol){
			
			YAMLoptimizerParser.CleanSuitableOfferForModule(solkey, applicationMap);
			YAMLoptimizerParser.AddSuitableOfferForModule(solkey, currentSol.getCloudOfferNameForModule(solkey),applicationMap);
		}
		
	}
	
	
	
}
