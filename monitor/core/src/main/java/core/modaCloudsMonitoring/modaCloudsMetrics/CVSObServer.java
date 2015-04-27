package core.modaCloudsMonitoring.modaCloudsMetrics;

import it.polimi.modaclouds.monitoring.metrics_observer.MetricsObServer;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public class CVSObServer extends MetricsObServer {

	public CVSObServer( int listeningPort ){

		super( listeningPort, "/v1/results", CVSResultHandler.class );
	}
}
