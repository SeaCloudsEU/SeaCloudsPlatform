/**
 * Copyright 2014 SeaClouds
 * Contact: Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package core.modaCloudsMonitoring.modaCloudsDataCollectors;

import imperial.modaclouds.monitoring.datacollectors.monitors.ModacloudsMonitor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import core.OperatingSystem;

public class MODACloudsDataCollectors {

	public static File getDataCollectorsJar(String serverDataCollectorsPath, String dataCollectorsFileName ){

		return new File( serverDataCollectorsPath + "/" + dataCollectorsFileName );
	}

	public static File getDataCollectorJar( String metricName, String serverDataCollectorsPath, String dataCollectorsFileName ){

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
			installationExecutionCommand += "\n" + "set \"MODACLOUDS_KNOWLEDGEBASE_SYNC_PERIOD=10\"";
			installationExecutionCommand += "\n" + "set \"MODACLOUDS_MONITORED_APP_ID=dathanas\"";
			installationExecutionCommand += "\n" + "set \"MODACLOUDS_MONITORED_VM_ID=frontend1\"";
		}

		else JOptionPane.showMessageDialog( null, "To initialize for the case of Unix", "Unix", JOptionPane.ERROR_MESSAGE );//TODO


		installationExecutionCommand += "\n" + getExecutionCommand( metricName, dataCollectorsFileName );


		return installationExecutionCommand;
	}

	private static String getExecutionCommand( String metricName, String dataCollectorsFileName ){

		String collector = ModacloudsMonitor.findCollector( metricName );


		Map<String,String> collectorExecutionCommandMapping = new HashMap<String,String>();

		collectorExecutionCommandMapping.put( "sigar", "lib/hyperic-sigar-1.6.4/sigar-bin/lib" );


		String executionCommand = collectorExecutionCommandMapping.get( collector );

		if ( executionCommand == null ) System.err.println( "Metric name = " + metricName + " was NOT found." );

		else executionCommand = "START CMD /C CALL java -Djava.library.path=" + executionCommand + " -jar ./" + dataCollectorsFileName + " kb" +
												   "\nexit";


		return executionCommand;
	}
}
