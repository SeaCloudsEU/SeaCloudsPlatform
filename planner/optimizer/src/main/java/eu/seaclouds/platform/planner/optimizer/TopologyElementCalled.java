package eu.seaclouds.platform.planner.optimizer;

public class TopologyElementCalled {

	private double probCall=1.0;
	private double meanNumberCalls=1.0;
	
	private TopologyElement element;
	
	public TopologyElementCalled(TopologyElement e){
		element=e;
	}
	
	public TopologyElementCalled(TopologyElement e, double proCall, double ncall){
		element=e;
		probCall=proCall;
		meanNumberCalls=ncall;
	}

	public double getProbCall() {
		return probCall;
	}

	public void setProbCall(double probCall) {
		this.probCall = probCall;
	}

	public double getMeanNumberCalls() {
		return meanNumberCalls;
	}

	public void setMeanNumberCalls(double meanNumberCalls) {
		this.meanNumberCalls = meanNumberCalls;
	}

	public TopologyElement getElement() {
		return element;
	}

	public void setElement(TopologyElement element) {
		this.element = element;
	}
}
