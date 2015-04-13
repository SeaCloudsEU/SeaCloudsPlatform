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

import java.util.ArrayList;
import java.util.List;

public class TopologyElement {

	private String name;
	private List<TopologyElementCalled> dependences;
	
	//the execution time experienced in a machine of performance=1 .
	//It is calculated as the execution time provided multiplied by the discovered performance of the machine.
	private double execTimeInPowerlessMachine;
	
	public TopologyElement(String name){
		this.name=name;
		dependences = new ArrayList<TopologyElementCalled>();
	}
	
	public TopologyElement(String name, double execTime){
		this.name=name;
		this.execTimeInPowerlessMachine=execTime;
		dependences = new ArrayList<TopologyElementCalled>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String newname){
		this.name=newname;
	}
	
	public void addElementCalled(TopologyElementCalled e){
		dependences.add(e);
	}
	
	public void setExecTime(double d) {
		this.execTimeInPowerlessMachine=d;
	}
	
	public double getDefaultExecutionTime(){
		return this.execTimeInPowerlessMachine;
	}
	
	
	public void addElementCalled(TopologyElement e){
		TopologyElementCalled elementCalled = new TopologyElementCalled(e);
		dependences.add(elementCalled);
	}

	
	/**
	 * @param dependenceName
	 * @return true if the element execution depends on (i.e., calls) the module called dependenceName. False otherwise
	 */
	public boolean dependsOn(String dependenceName) {
		
		for(TopologyElementCalled e : dependences){
			if(e.getElement().getName().equals(dependenceName)){
				return true;
			}
		}
		
		return false;
		
	}

	//TODO: This operation can break encapsulation. Since it is only used to iterate over the elements in the list
	// implement an iterator in this class over the dependences list
	public List<TopologyElementCalled> getDependences() {
		return dependences;
	}

	public boolean canScale() {
		// TODO Auto-generated method stub
		// TODO If it is agreed that some modules cannot scale out and then is also defined the language in which we represent such characteristic, 
		// this method should implement the checking of whether a module can scale. Up to now, it is assumed that all can scale out, so the 
		// structure is created. 
		return true;
	}

	public void setExecTimeMillis(double d) {
		setExecTime(d/1000.0);
		
	}



}
