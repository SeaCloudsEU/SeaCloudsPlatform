package core.modaCloudsMonitoring.modaCloudsDataCollectors;

import imperial.modaclouds.monitoring.datacollectors.monitors.ModacloudsMonitor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import core.OperatingSystem;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public class MODACloudsDataCollectors {

	public static File getDataCollectorsJar(String serverDataCollectorsPath, String dataCollectorsFileName ){

		return new File( serverDataCollectorsPath + "/" + dataCollectorsFileName );
	}

	public static File getDataCollectorJar( String metricName, String serverDataCollectorsPath, String dataCollectorsFileName ){

		//TODO: to select data collector based on metric name.


		return new File( serverDataCollectorsPath + "/" + dataCollectorsFileName );
	}

	public static String formInstallationExecutionCommand( String metricName, String IPofKB, String portOfKB, String IPofDA, String portOfDA, String dataCollectorsFileName ){

		String installationExecutionCommand = "";

		if( OperatingSystem.isWindows() ){

			installationExecutionCommand = "set \"MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_IP=" + IPofKB + "\"";
			installationExecutionCommand += "\n" + "set \"MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_PORT=" + portOfKB + "\"";
			installationExecutionCommand += "\n" + "set \"MODACLOUDS_KNOWLEDGEBASE_DATASET_PATH \"/modaclouds/kb\"";
			installationExecutionCommand += "\n" + "set \"MODACLOUDS_MONITORING_DDA_ENDPOINT_IP=" + IPofDA + "\"";
			installationExecutionCommand += "\n" + "set \"MODACLOUDS_MONITORING_DDA_ENDPOINT_PORT=" + portOfDA + "\"";
			installationExecutionCommand += "\n" + "set \"MODACLOUDS_KNOWLEDGEBASE_SYNC_PERIOD=10\"";//TODO: to parameterize this.
			installationExecutionCommand += "\n" + "set \"MODACLOUDS_MONITORED_APP_ID=dathanas\"";//TODO: to parameterize this.
			installationExecutionCommand += "\n" + "set \"MODACLOUDS_MONITORED_VM_ID=frontend1\"";//TODO: to parameterize this.
		}

		else JOptionPane.showMessageDialog( null, "To initialize for the case of Unix", "Unix", JOptionPane.ERROR_MESSAGE );//TODO


		installationExecutionCommand += "\n" + getExecutionCommand( metricName, dataCollectorsFileName ); //System.out.println( "installationExecutionCommand = " + installationExecutionCommand );


		return installationExecutionCommand;
	}

	private static String getExecutionCommand( String metricName, String dataCollectorsFileName ){

		String collector = ModacloudsMonitor.findCollector( metricName );


		Map<String,String> collectorExecutionCommandMapping = new HashMap<String,String>();

		collectorExecutionCommandMapping.put( "sigar", "lib/hyperic-sigar-1.6.4/sigar-bin/lib/" );
			/*metricCollectorMapping.put("cpustolen", "sigar");
				metricCollectorMapping.put("memused", "sigar");
				metricCollectorMapping.put("threads_running", "mysql");
				metricCollectorMapping.put("threads_cached", "mysql");
				metricCollectorMapping.put("threads_connected", "mysql");
				metricCollectorMapping.put("threads_created", "mysql");
				metricCollectorMapping.put("queries", "mysql");
				metricCollectorMapping.put("bytes_received", "mysql");
				metricCollectorMapping.put("bytes_sent", "mysql");
				metricCollectorMapping.put("connections", "mysql");
				metricCollectorMapping.put("aborted_connects", "mysql");
				metricCollectorMapping.put("aborted_clients", "mysql");
				metricCollectorMapping.put("table_locks_immediate", "mysql");
				metricCollectorMapping.put("table_locks_waited", "mysql");
				metricCollectorMapping.put("com_insert", "mysql");
				metricCollectorMapping.put("com_delete", "mysql");
				metricCollectorMapping.put("com_update", "mysql");
				metricCollectorMapping.put("com_select", "mysql");
				metricCollectorMapping.put("qcache_hits", "mysql");
				metricCollectorMapping.put("diskreadopscloudwatch", "cloudwatch");
				metricCollectorMapping.put("cpuutilizationcloudwatch", "cloudwatch");
				metricCollectorMapping.put("diskreadopscloudwatch", "cloudwatch");
				metricCollectorMapping.put("diskwriteopscloudwatch", "cloudwatch");
				metricCollectorMapping.put("diskreadbytescloudwatch", "cloudwatch");
				metricCollectorMapping.put("diskwritebytescloudwatch", "cloudwatch");
				metricCollectorMapping.put("networkincloudwatch", "cloudwatch");
				metricCollectorMapping.put("networkoutcloudwatch", "cloudwatch");
				metricCollectorMapping.put("peakthreadcountjmx","jmx");
				metricCollectorMapping.put("heapmemoryusedjmx", "jmx");
				metricCollectorMapping.put("uptimejmx", "jmx");
				metricCollectorMapping.put("cpuutilizationcollectl", "collectl");
				metricCollectorMapping.put("contextswitchcollectl", "collectl");
				metricCollectorMapping.put("cpuutilstolencollectl", "collectl");
				metricCollectorMapping.put("interruptscollectl", "collectl");
				metricCollectorMapping.put("maxprocscollectl", "collectl");
				metricCollectorMapping.put("maxprocsqueuecollectl", "collectl");
				metricCollectorMapping.put("memusedcollectl", "collectl");
				metricCollectorMapping.put("memSwapspaceusedcollectl", "collectl");
				metricCollectorMapping.put("networkinbytescollectl", "collectl");
				metricCollectorMapping.put("networkoutbytescollectl", "collectl");
				metricCollectorMapping.put("generalcost", "cost");
				metricCollectorMapping.put("ec2-spotprice", "ec2-spotPrice");
				metricCollectorMapping.put("responseinfo", "ofbiz");
				metricCollectorMapping.put("startuptime", "startupTime");
				metricCollectorMapping.put("logfile", "logFile");
				metricCollectorMapping.put("detailedcost", "detailedCost");
				metricCollectorMapping.put("vmavailable", "vmavailability");
				metricCollectorMapping.put("appavailable", "appavailability");
				metricCollectorMapping.put("flexi", "flexi");
				metricCollectorMapping.put("haproxylog", "haproxy");*/


		String executionCommand = collectorExecutionCommandMapping.get( collector );

		if ( executionCommand == null ) System.err.println( "Metric name = " + metricName + " was NOT found." );

		else executionCommand = "START CMD /C CALL java -Djava.library.path=" + executionCommand + " -jar " + dataCollectorsFileName + " kb" +
												   "\nexit";


		return executionCommand;
	}
}
