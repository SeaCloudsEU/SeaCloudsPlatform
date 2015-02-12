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


import java.util.HashSet;
import java.util.Set;

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
		
		double [][] routes = getRoutingMatrix(topology);
		double [] workloadsModules = getWorkloadsArray(routes, workload);
		double [] numVisitsModule = getNumVisitsArray(workloadsModules,workload);
		
		//calculate the workload received by each core of a module: consider both number of cores 
		//of a cloud offer and number of instances for such module
		double [] workloadsModulesByCoresAndNumInstances = weighModuleWorkloadByCoresAndNumInstances(workloadsModules,topology,bestSol,cloudCharacteristics);
		
		double[] mus = getMusOfSelectedCloudOffers(bestSol,topology,cloudCharacteristics);
		
		double respTime = getSystemRespTime(numVisitsModule, workloadsModulesByCoresAndNumInstances,mus);
		
		//after computing, save the performance info in properties.performance
		properties.setResponseTime(respTime);
		
		return properties;
	}

	
	
	private double getSystemRespTime(double[] numVisitsModule,double[] workloadsModulesByCoresAndNumInstances, double[] mus) {
		
		double respTime =0;
		
		for(int i=0; i<numVisitsModule.length; i++){
			
			respTime+= calculateRespTimeModule(numVisitsModule[i],workloadsModulesByCoresAndNumInstances[i], mus[i]);
			
		}
		
		return respTime;
	}





	/*
	 * Here they are used the Queueing Network theory basics.
	 */
	private double calculateRespTimeModule(double visits, double lambda, double mu) {
		
		double utilization = lambda/mu;
		double respTimeVisit = (1.0/mu)/(1.0-utilization);
		
		return visits * respTimeVisit;
		
	}





	private double[] getNumVisitsArray(double[] workloadsModules,double workload) {
		
		double[] numVisits = new double[workloadsModules.length];
		
		for(int i=0; i<numVisits.length; i++){
			numVisits[i]=workloadsModules[i]/workload;
		}
		
		return numVisits;
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
		
		
		
		for(int row=0; row<routing.length; row++){
			if((routing[row][indexCol]!=0)&&(workloads[row]==0.0)){
				//There is some calling module whose workload has not been calculated yet.
				return false;
			}
		}
		
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
	
		


	//HashSet to check if an element has been visited. It is used for checking for loops in the topology.  

	private Set<String> visited;
	


	/**
	 * @param bestSol
	 * @param topology
	 * @param cloudCharacteristics
	 * @return The calculated availability of the system. 
	 * 
	 * This method will be recursive. The availablity of the system will be the product of the availability of its first module and the modules it requests.
	 * It will not work if there are cycles in the topology
	 */
	public double computeAvailability(Solution bestSol, Topology topology, SuitableOptions cloudCharacteristics) {
		
			
		visited = new HashSet<String>();
		
		TopologyElement initialElement = topology.getInitialElement();
		
		visited.add(initialElement.getName());
		
		String cloudUsedInitialElement = bestSol.getCloudOfferNameForModule(initialElement.getName());
		double instancesUsedInitialElement = bestSol.getCloudInstancesForModule(initialElement.getName());
		double availabilityInitialElementInstance = cloudCharacteristics.getCloudCharacteristics(initialElement.getName(), cloudUsedInitialElement).getAvailability();
		
		double unavailabilityInitialElement = Math.pow((1.0-availabilityInitialElementInstance), instancesUsedInitialElement);
		double availabilityInitialElement = 1.0 - unavailabilityInitialElement;
		
		double systemAvailability = availabilityInitialElement;
		
		for(TopologyElementCalled c : topology.getInitialElement().getDependences()){
			systemAvailability= systemAvailability * calculateAvailabilityRecursive(c,bestSol,topology,cloudCharacteristics);
		}
		
		//after computing, save the availability info in properties.availability
		properties.setAvailability(systemAvailability);
		
		
		return systemAvailability;
	}

	private double calculateAvailabilityRecursive(TopologyElementCalled c, Solution bestSol, Topology topology,	SuitableOptions cloudCharacteristics) {
		
		if(visited.contains(c.getElement().getName())){
			log.warn("Availability evaluation: Revisting the availability of a module which was already visited. Expect weird behaviors or infinite loops");
			return 1;
		}
		
		visited.add(c.getElement().getName());
		
		String cloudUsedForElement = bestSol.getCloudOfferNameForModule(c.getElement().getName());
		double instancesUsedForElement = bestSol.getCloudInstancesForModule(c.getElement().getName());
		double availabilityElementInstance = cloudCharacteristics.getCloudCharacteristics(c.getElement().getName(), cloudUsedForElement).getAvailability();
		double unavailabilityElement = Math.pow((1.0-availabilityElementInstance), instancesUsedForElement);
		
		double availabilityElement= 1.0 - unavailabilityElement;
		
		for(TopologyElementCalled cc : c.getElement().getDependences()){
			availabilityElement = availabilityElement * calculateAvailabilityRecursive(cc, bestSol,topology,cloudCharacteristics);
		}
		
		double callAvailability = c.getProbCall()*availabilityElement + (1.0 - c.getProbCall());
		
		return callAvailability;
	}





	public double computeCost(Solution bestSol, SuitableOptions cloudCharacteristics) {
		
		double cost=0.0;
		
		for(String moduleName : bestSol){
			
			String cloudUsedForElement = bestSol.getCloudOfferNameForModule(moduleName);
			double instancesUsedForElement = bestSol.getCloudInstancesForModule(moduleName);
			double costElementInstance = cloudCharacteristics.getCloudCharacteristics(moduleName, cloudUsedForElement).getCost();
			
			cost += (instancesUsedForElement*costElementInstance);
			
		}

		//after computing, save the cost info in properties.availability
		properties.setCost(cost);
		return cost;
		
	}

}
