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

package eu.seaclouds.platform.planner.optimizer.nfp;

import eu.seaclouds.platform.planner.optimizer.Solution;

public class QualityAnalyzer {

	QualityInformation properties=null;
	
	public QualityAnalyzer() {
		
		properties = new QualityInformation();
	}

	

	
	
	public QualityInformation computePerformance(Solution bestSol) {
		// TODO Auto-generated method stub
		//after computing, save the performance infor in properties.performance
		return null;
	}

	public double computeAvailability(Solution bestSol) {
		// TODO Auto-generated method stub
		//after computing, save the availability info in properties.availability
		return 0;
	}

	public int computeCost(Solution bestSol) {
		//after computing, save the availability info in properties.availability
		// TODO Auto-generated method stub
		return 0;
	}

}
