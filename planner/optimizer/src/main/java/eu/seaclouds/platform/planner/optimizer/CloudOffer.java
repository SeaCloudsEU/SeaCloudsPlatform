package eu.seaclouds.platform.planner.optimizer;

public class CloudOffer {

	private String name;
	private double performance;
	private double availability;
	private double cost;
	private double numCores;
	
	
public CloudOffer(String name, double performance, double availability,	double cost, double numCores) {
		
		this.name = name;
		
		//We expect to save here the value MU (service rate) of the cloud offer (it works because, although it is not very modular to store 
		//info here, CloudOffers are members of a list of SuitableSolutions for module). 
		this.performance = performance;
		this.availability=availability;
		this.cost = cost;
		this.numCores=numCores;
	}
	
	//NumCores not specified, assuming 1
	public CloudOffer(String name, double performance, double availability,	double cost) {
		
		this(name,performance,availability,cost,1.0);
		
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

	public double getNumCores() {
		return numCores;
	}

	public void setNumCores(double numCores) {
		this.numCores = numCores;
	}


	
	
}
