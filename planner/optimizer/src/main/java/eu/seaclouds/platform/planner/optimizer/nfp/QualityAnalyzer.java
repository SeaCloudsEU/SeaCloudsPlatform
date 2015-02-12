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


import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.platform.planner.optimizer.Solution;
import eu.seaclouds.platform.planner.optimizer.SuitableOptions;
import eu.seaclouds.platform.planner.optimizer.Topology;
import eu.seaclouds.platform.planner.optimizer.TopologyElement;
import eu.seaclouds.platform.planner.optimizer.TopologyElementCalled;

public class QualityAnalyzer {

	static Logger log = LoggerFactory.getLogger(QualityAnalyzer.class);
	
	private QualityInformation properties=null;
	
	public QualityAnalyzer() {
		
		properties = new QualityInformation();
	}

	

	
	
	public QualityInformation computePerformance(Solution bestSol, Topology topology, double workload, SuitableOptions cloudCharacteristics) {
		// TODO Auto-generated method stub
		double [][] routes = getRoutingMatrix(topology);
		double [] workloadsModules = getWorkloadsArray(routes, workload);
		
		//calculate the workload received by each core of a module: consider both number of cores 
		//of a cloud offer and number of instances for such module
		double [] workloadsModulesByCoresAndNumInstances = weighModuleWorkloadByCoresAndNumInstances(workloadsModules,topology,bestSol,cloudCharacteristics);
		
		double[] mus = getMusOfSelectedCloudOffers(bestSol,topology,cloudCharacteristics);
				
		
		//after computing, save the performance info in properties.performance
		return null;
	}

	
	
	private double[] getMusOfSelectedCloudOffers(Solution bestSol,Topology topology, SuitableOptions cloudCharacteristics) {
		
		double[] mus = new double[topology.size()];
		for(int i=0; i<mus.length; i++){
			String moduleName= topology.getElementIndex(i).getName();
			String cloudChosenForModule=bestSol.getCloudOfferNameForModule(moduleName);
			mus[i]=cloudCharacteristics.getCloudCharacteristics(moduleName, cloudChosenForModule).getPerformance();
		}
		
		return mus;
		
		
	}





	private double[] weighModuleWorkloadByCoresAndNumInstances(double[] workloadsModules, Topology topology, Solution bestSol,SuitableOptions cloudCharacteristics) {
		double[] ponderatedWorkloads = new double[workloadsModules.length];
		for(int i=0; i<workloadsModules.length; i++){
			
			String moduleName= topology.getElementIndex(i).getName();
			double numInstances = bestSol.getCloudInstancesForModule(moduleName);
			
			String cloudChosenForModule= bestSol.getCloudOfferNameForModule(moduleName);
			
			double numCores= cloudCharacteristics.getCloudCharacteristics(moduleName, cloudChosenForModule).getNumCores();
			ponderatedWorkloads[i]= workloadsModules[i]/(numInstances*numCores);
		}
		
		return ponderatedWorkloads;
	}





	private double[] getWorkloadsArray(double[][] routing, double workload) {
		
		double[] workloadsReceived = new double[routing.length];
		
		boolean initialWorkloadIsSet=false;
		
		while(!completedWorkloadsCalculation(workloadsReceived)){
			
			//See which workloads can be calculated with the current information
			for(int i=0; i<routing.length; i++){
				
				//If module i can be calculated (and was not calculate previously)
				if((workloadsReceived[i]==0.0) && (workloadCanBeCalculated(routing,workloadsReceived,i))){
					
					log.debug("Calculating the received workload of Module: " + i);
					//The first case; i.e,. the one that receive requests directly, "workload" has to be added to the array
					if(!initialWorkloadIsSet){
						initialWorkloadIsSet=true;
						workloadsReceived[i]=workload;
					}
					
					for(int callingIndex=0; callingIndex<routing.length; callingIndex++){
						workloadsReceived[i]+= routing[callingIndex][i]*workloadsReceived[callingIndex];
					}
				}
			}
		
		}
		
		return workloadsReceived;
		
		
		
		
	}





	private boolean workloadCanBeCalculated(double[][] routing,	double[] workloads, int indexCol) {
		
		log.debug("Checking if it can be calculeted workload of module: " + indexCol);
		
		for(int row=0; row<routing.length; row++){
			if((routing[row][indexCol]!=0)&&(workloads[row]==0.0)){
				//There is some calling module whose workload has not been calculated yet.
				log.debug("Worload calculation checked: it couldn't. Failed in row " + row +" indexCol " + indexCol + " value of routing in this positon is: " + routing[row][indexCol]);
				try {
				    TimeUnit.MILLISECONDS.sleep(500);               
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
				return false;
			}
		}
		log.debug("Worload calculation checked: it could");
		return true;
	}





	private boolean completedWorkloadsCalculation(double[] workloads) {
		for(int i=0; i<workloads.length; i++){
			if(workloads[i]==0.0){
				return false;
			}
		}
		return true;
	}





	private double[][] getRoutingMatrix(Topology topology) {
		
		double[][] routing = new double[topology.size()][topology.size()];
		
		TopologyElement initialElement = topology.getInitialElement();
		int indexOfInitial= topology.indexOf(initialElement);
		
		//Easier if the index of initial element is 0. So, set it
		if(indexOfInitial!=0){
			topology.replaceElementsIndexes(initialElement,0);
		}
		
		if(topology.indexOf(initialElement)!=0){
			log.warn("Index replacements in topology elements did not work!");;
		}
		
		//TODO: improve this solution implementing an iterator over the modules of the topology: now  
		// allowed operation topology.getModules() breaks the encapsulation of data by topology class
		for(TopologyElement e : topology.getModules()){
			
			//TODO: improve this solution implementing an iterator over the modules of the topology: now  
			// allowed operation initialElement.getDependences() breaks the encapsulation of data by TopologyElement class
			for(TopologyElementCalled c: e.getDependences()){
				routing[topology.indexOf(e)][topology.indexOf(c.getElement())]=c.getProbCall();
			}
		}
		
		return routing;
		
			
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
