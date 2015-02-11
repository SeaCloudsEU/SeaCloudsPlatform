
/*
 * Initial version of topology that use hashmaps. Difficulties to save index of modules for matrix. I chaged to use lists instead of 
 * hasmaps because the elements there are just a few elements in topology (so traversing a list is not much costly) and we get the 
 * possiblity to use indexes of elemnens as their position in the list
 */

package eu.seaclouds.platform.planner.optimizer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class Topology {

	static Logger log = LoggerFactory.getLogger(Topology.class);
	
	//A hashMap indexed by the module name, which is in turn duplicated because it is contained insiede TopologyElement
	private Map<String,TopologyElement> modules;
	
	public Topology(){
		modules = new HashMap<String,TopologyElement>();
	}
	
	public void addModule(TopologyElement e){

		modules.put(e.getName(), e);
	}
	
	public TopologyElement getModule(String name){
		return modules.get(name);
	}

	public int size() {
		return modules.size();
	}

	public TopologyElement getInitialElement() {
		//We assume that The initial element is such one that is not called by anyone.
		
		
		for(Entry<String, TopologyElement> pointed: modules.entrySet()){
			boolean isInitial=true;
			
			//Check if any of the elements in the topology depends on (calls) it
			for(Entry<String, TopologyElement> pointer: modules.entrySet()){
				if(pointer.getValue().dependsOn(pointed.getKey())){
					isInitial=false;
				}
				
			}
			
			if(isInitial){
				return pointed.getValue();
			}
			
		}
		
		log.warn("Initial element not found. Possible circular dependences in the design. Please, state clearly which is the initial element");
		return null;
	}
	
}
