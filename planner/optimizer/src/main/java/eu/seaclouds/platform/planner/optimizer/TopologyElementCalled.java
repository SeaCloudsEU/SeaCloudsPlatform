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
