package eu.seaclouds.platform.planner.optimizer;

import java.util.HashMap;
import java.util.Map;


public class Topology {

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
	
}
