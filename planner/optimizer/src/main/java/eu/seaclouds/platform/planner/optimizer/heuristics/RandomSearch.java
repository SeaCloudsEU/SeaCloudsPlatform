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

import eu.seaclouds.platform.planner.optimizer.Solution;
import eu.seaclouds.platform.planner.optimizer.SuitableOptions;
import eu.seaclouds.platform.planner.optimizer.Topology;
import eu.seaclouds.platform.planner.optimizer.util.YAMLoptimizerParser;

public class RandomSearch extends AbstractHeuristic implements SearchMethod {

	
	
	public RandomSearch() {
		super();
		
	}

	public RandomSearch(int maxIter) {
		super(maxIter);
	}

	/*
	 * (non-Javadoc)
	 * @see eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethod#computeOptimalSolution(eu.seaclouds.platform.planner.optimizer.SuitableOptions, java.util.Map)
	 */
	@Override
	public Map<String, Object>[] computeOptimizationProblem(SuitableOptions cloudOffers,	Map<String, Object> applicationMap, Topology topology, int numPlansToGenerate) {
		
		//To findSolution method, we pass an Empty solution instead of a null value to or create a new method that does not consider the current one. 
		//This way may help for replanning, when even the first attempt for solution will be based on the current deployment
		 Solution[] bestSols = findSolutions(null, cloudOffers, applicationMap,numPlansToGenerate);
		 		 
		 setFitnessOfSolutions(bestSols,applicationMap,topology,cloudOffers);		 
		 
		 
		 
		 Solution[] currentSol= new Solution[1];
		 int numItersNoImprovement=0;
		 while(numItersNoImprovement<super.getMaxIterNoImprove()){
			
			 currentSol[0] = findSolution(cloudOffers, applicationMap);
			 currentSol[0].setSolutionFitness(super.fitness(currentSol[0], applicationMap, topology, cloudOffers));
			
			 if(currentSol[0].getSolutionFitness()> super.getMinimumFitnessOfSolutions(bestSols)){	
				 if(!currentSol[0].isContainedIn(bestSols)){
					 insertOrdered(bestSols,currentSol[0]);
					 numItersNoImprovement=0;
				 }
			 }
			 
			 numItersNoImprovement++;
		 }
		 
		 return HashMapOfFoundSolutionsWithThresholds(bestSols, applicationMap, topology, cloudOffers, numPlansToGenerate);

	}


	private Map<String, Object>[] HashMapOfFoundSolutionsWithThresholds(Solution[] bestSols, Map<String, Object> applicMap,Topology topology, 
																		SuitableOptions cloudOffers,int numPlansToGenerate) {
		 @SuppressWarnings("unchecked")
		Map<String, Object>[] solutions = new HashMap[numPlansToGenerate];
		 for(int i=0; i<bestSols.length; i++){
			 
			 Map<String, Object> baseAppMap = YAMLoptimizerParser.cloneYAML(applicMap);
			 
			 super.addSolutionToAppMap(bestSols[i], baseAppMap);
		 
			 HashMap<String,ArrayList<Double>> thresholds = super.createReconfigurationThresholds(bestSols[i], baseAppMap, topology, cloudOffers);
			 YAMLoptimizerParser.AddReconfigurationThresholds(thresholds,baseAppMap);
			 
			 solutions[i]=baseAppMap;
		 }
		 return solutions;		 
	}

	private void insertOrdered(Solution[] bestSols, Solution solution) {

		int i=bestSols.length-1;
		
		while(bestSols[i].getSolutionFitness()<solution.getSolutionFitness()){ 
			bestSols[i]=bestSols[i-1];
			i--; 
		}
		bestSols[i]=solution;
		
	}


	private Solution[] findSolutions(Solution baseSolution, SuitableOptions cloudOffers, Map<String, Object> applicationMap,int numPlansToGenerate) {
		
		Solution[] newSolutions = new Solution[numPlansToGenerate];
		
		for(int newSolIndex=0; newSolIndex<newSolutions.length; newSolIndex++){
			
			newSolutions[newSolIndex]=findSolution(cloudOffers, applicationMap);
		}
		
		return newSolutions;
		
	}
	
	private Solution findSolution(SuitableOptions cloudOffers, Map<String, Object> applicationMap) {
		
		Solution currentSolution= new Solution();
		for(String modName : cloudOffers.getStringIterator()){
			
			//TODO Consider also playing with the amount of instances used of a suitable option. 
			int itemToUse = (int) Math.floor(Math.random()*cloudOffers.getSizeOfSuitableOptions(modName));
			
			currentSolution.addItem(modName, cloudOffers.getIthSuitableOptionForModuleName(modName,itemToUse));
		}
		
		return currentSolution;
	}

}
