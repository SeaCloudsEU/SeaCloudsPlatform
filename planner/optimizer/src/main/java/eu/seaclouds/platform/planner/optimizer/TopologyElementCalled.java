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

public class TopologyElementCalled {

	private double probCall=1.0;
	
	//NOT used in this version. The number of calls is fixed to 1. 
	//private double meanNumberCalls=1.0;
	
	private TopologyElement element;
	
	public TopologyElementCalled(TopologyElement e){
		//probability will keep its default value = 1.0
		element=e;
		
	}
	
	public TopologyElementCalled(TopologyElement e, double proCall){
		element=e;
		probCall=proCall;
	}

	public double getProbCall() {
		return probCall;
	}

	public void setProbCall(double probCall) {
		this.probCall = probCall;
	}


	public TopologyElement getElement() {
		return element;
	}

	public void setElement(TopologyElement element) {
		this.element = element;
	}
}
