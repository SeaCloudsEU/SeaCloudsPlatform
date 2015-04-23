package core.RESTGraphite;

public class Metric {
	private String name;
	private double value;
	private long timestamp;
	
	public Metric(String name, double value, long timestamp) {
		this.name = name;
		this.value = value;
		this.timestamp = timestamp;
	}
	
	public String getName() {
		return name;
	}
	
	public double getValue() {
		return value;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
}
