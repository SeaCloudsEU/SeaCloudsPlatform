package eu.seaclouds.platform.planner.optimizer;

import java.util.ArrayList;
import java.util.List;

public class TopologyElement {

	private String name;
	private List<TopologyElementCalled> dependences;
	
	
	public TopologyElement(String name){
		this.name=name;
		dependences = new ArrayList<TopologyElementCalled>();
	}
	
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
