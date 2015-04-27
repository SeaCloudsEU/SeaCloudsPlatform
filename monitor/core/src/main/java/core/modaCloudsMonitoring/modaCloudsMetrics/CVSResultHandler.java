package core.modaCloudsMonitoring.modaCloudsMetrics;

import it.polimi.modaclouds.monitoring.metrics_observer.MonitoringDatum;
import it.polimi.modaclouds.monitoring.metrics_observer.MonitoringDatumHandler;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public class CVSResultHandler extends MonitoringDatumHandler {

	@Override
	public void getData( List<MonitoringDatum> monitoringData ){

		if( monitoringData == null || ( monitoringData != null && monitoringData.isEmpty() ) ) System.out.println("ObserverTimestamp,ResourceId,Metric,Value,Timestamp");

		else{

			System.out.println( "ObserverTimestamp, ResourceId, Metric, Value, Timestamp" );

			String observerTimestamp = Long.toString( new Date().getTime() );

			for( MonitoringDatum monitoringDatum : monitoringData ){

				System.out.println(observerTimestamp + ","
						+ monitoringDatum.getResourceId() + ","
						+ monitoringDatum.getMetric() + ","
						+ monitoringDatum.getValue() + ","
						+ monitoringDatum.getTimestamp());
			}
		}
	}

}
