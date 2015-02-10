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
		return name;
	}
	
	public void addElementCalled(TopologyElementCalled e){
		dependences.add(e);
	}
	
	public void addElementCalled(TopologyElement e){
		TopologyElementCalled elementCalled = new TopologyElementCalled(e);
		dependences.add(elementCalled);
	}

}
