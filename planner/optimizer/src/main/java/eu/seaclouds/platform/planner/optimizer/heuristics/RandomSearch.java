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

import java.util.List;
import java.util.Map;

public class RandomSearch extends AbstractHeuristic implements Heuristic {

	
	
	public RandomSearch() {
		super();
		
	}

	public RandomSearch(int maxIter) {
		super(maxIter);
	}

	/*
	 * (non-Javadoc)
	 * @see test.restws.Heuristic#computeOptimalSolution(test.restws.SuitableOptions, java.util.Map)
	 */
	@Override
	public void computeOptimalSolution(SuitableOptions cloudOffers,	Map<String, Object> applicationMap) {
		
		//We pass an Empty solution instead of a null value or create a new method that does not consider the current one. 
		//This way may help for replanning, when even the first attempt for solution will be based on the current deployment
		 Solution bestSol = findSolution(new Solution(), cloudOffers, applicationMap);
		 
		 Solution currentSol=bestSol;
		 
		 int i=0;
		 while(i<getMaxIterNoImprove()){
		 
			currentSol = findSolution(currentSol, cloudOffers, applicationMap);
			 
			 if(super.fitness(currentSol, applicationMap)>super.fitness(bestSol, applicationMap)){			 
				 bestSol= currentSol;
				 i=0;
			 }
			 
			 i++;
		 }
		 
		 super.addSolutionToAppMap(currentSol, applicationMap);

	}



	private Solution findSolution(Solution baseSolution, SuitableOptions cloudOffers, Map<String, Object> applicationMap) {
		
		Solution newSolution = new Solution();
		
		for(List<String> l : cloudOffers.getListIterator()){
			
			double itemToUse = Math.floor(Math.random()*l.size());
			//newSolution.addItem(name, cloudOption);
		}
		
		for(String modName : cloudOffers.getStringIterator()){
			int itemToUse = (int) Math.floor(Math.random()*cloudOffers.getSizeOfSuitableOptions(modName));
			
			newSolution.addItem(modName, cloudOffers.getIthSuitableOptionForModuleName(modName,itemToUse));
		}
		
		return newSolution;
		
	}

}
