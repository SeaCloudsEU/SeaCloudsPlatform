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


public class QualityInformation {

	
	private double respTime=0;
	private double availability=0;
	private double cost=0;
	private double workload=0;
	
	

	public double getResponseTime() {
		return respTime;
	}
	
	public void setResponseTime(double respTime) {
		this.respTime = respTime;
	}

	public boolean existResponseTimeRequirement(){
		return respTime!=0.0;
	}
	
	public double getAvailability() {	
		return availability;
	}
	
	
	
	public void setAvailability(double availability) {
		this.availability = availability;
	}
	
	public boolean existAvailabilityRequirement(){
		return availability!=0.0;
	}
	


	public void setCost(double cost) {
		this.cost = cost;
	}


	public double getCost() {
		
		return cost;
	}

	public boolean existCostRequirement(){
		return cost!=0.0;
	}
	
	
	public void setWorkload(double applicationWorkload) {
		workload=applicationWorkload;
		
	}


	public double getWorkload() {
		return workload;
	}

	public boolean hasValidWorkload() {
		return workload>0;
	}





}
