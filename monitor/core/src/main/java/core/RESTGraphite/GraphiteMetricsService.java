package core.RESTGraphite;

import it.polimi.modaclouds.monitoring.metrics_observer.JSONMonitoringDataParser;
import it.polimi.modaclouds.monitoring.metrics_observer.MonitoringDatum;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.POST;
import javax.ws.rs.Path;


// The Java class will be hosted at the URI path "/graphiteMetrics"
@Path("/graphiteMetricsService")
public class GraphiteMetricsService {

    @POST
    @Path("/sendMetrics")
    //@Consumes(MediaType.APPLICATION_JSON,MediaType)
    //@Produces(MediaType.APPLICATION_JSON)
    public String sendMetrics(String json) {
		
    	Set<Metric> metrics = new HashSet<Metric>();
    	try {
    		List<MonitoringDatum> monitoringData = JSONMonitoringDataParser.jsonToMonitoringDatum(json);
			for(MonitoringDatum datum: monitoringData) {
				String metricPath = datum.getResourceId() + "." + datum.getMetric();
				double metricValue = Double.parseDouble(datum.getValue());
				long metricTimestamp = Long.parseLong(datum.getTimestamp()) / 1000;
				metrics.add(new Metric(metricPath, metricValue, metricTimestamp));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	if (!metrics.isEmpty()) {
    		System.out.println("Sending metrics... ");
    		GraphiteClient client = new GraphiteClient(System.getProperty("graphitehost"), Integer.parseInt(System.getProperty("graphiteport")));
    		client.sendMetrics(metrics);
    		System.out.println(" done.");
    	}
    	
        return "Sent!";
    }
}
