package eu.seaclouds.platform.planner.optimizer;

public class CloudOffer {

	private String name;
	private double performance;
	private double availability;
	private double cost;
	
	
	public CloudOffer(String name, double performance, double availability,	double cost) {
		
		this.name = name;
		this.performance = performance;
		this.availability=availability;
		this.cost = cost;
	}

	public CloudOffer(String name) {
		this.name = name;
		this.performance = 0;
		this.availability=0;
		this.cost = 0;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public double getPerformance() {
		return performance;
	}


	public void setPerformance(double performance) {
		this.performance = performance;
	}


	public double getAvailability() {
		return availability;
	}


	public void setAvailability(double availability) {
		this.availability = availability;
	}


	public double getCost() {
		return cost;
	}


	public void setCost(double cost) {
		this.cost = cost;
	}

	public CloudOffer clone(){
		return new CloudOffer(name,performance,availability,	cost);
	}


	
	
}
