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

import core.OperatingSystem;
import core.UnixFileExecution;

public class MODACloudsDataCollectors {

	public static File getDataCollectorsJar(String serverDataCollectorsPath,
			String dataCollectorsFileName) {

		return new File(serverDataCollectorsPath + "/" + dataCollectorsFileName);
	}

	public static File getDataCollectorJar(String metricName,
			String serverDataCollectorsPath, String dataCollectorsFileName) {

		return new File(serverDataCollectorsPath + "/" + dataCollectorsFileName);
	}

	public static String formInstallationExecutionCommand(String metricName,
			String IPofKB, String portOfKB, String IPofDA, String portOfDA,
			String dataCollectorsFileName, String appID, String vmID) {

		String installationExecutionCommand = "";// clearEnvironmentVariables();

		if (OperatingSystem.isWindows()) {

			installationExecutionCommand += "setx MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_IP \""
					+ IPofKB + "\" /M";

			installationExecutionCommand += "\n"
					+ "setx MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_PORT \""
					+ portOfKB + "\" /M";

			installationExecutionCommand += "\n"
					+ "setx MODACLOUDS_KNOWLEDGEBASE_DATASET_PATH \"/modaclouds/kb\" /M";

			installationExecutionCommand += "\n"
					+ "setx MODACLOUDS_MONITORING_DDA_ENDPOINT_IP \"" + IPofDA
					+ "\" /M";

			installationExecutionCommand += "\n"
					+ "setx MODACLOUDS_MONITORING_DDA_ENDPOINT_PORT \""
					+ portOfDA + "\" /M";

			installationExecutionCommand += "\n"
					+ "setx MODACLOUDS_KNOWLEDGEBASE_SYNC_PERIOD \"10\" /M";

			installationExecutionCommand += "\n"
					+ "setx MODACLOUDS_MONITORED_APP_ID \"" + appID + "\" /M";

			installationExecutionCommand += "\n"
					+ "setx MODACLOUDS_MONITORED_VM_ID \"" + vmID + "\" /M";
			
			installationExecutionCommand += "\n"
					+ getExecutionCommand(metricName, dataCollectorsFileName);
		}
		else if (OperatingSystem.isUnix()){
			System.err.println("To initialize for the case of Unix");
		}
		else if (OperatingSystem.isLinux()){
			installationExecutionCommand += "echo \"export MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_IP="+ IPofKB+"\" ~/.bashrc";

			installationExecutionCommand += "\n"
					+ "echo \"export MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_PORT="+ portOfKB+"\" >> ~/.bashrc";

			installationExecutionCommand += "\n"
					+ "echo \"export MODACLOUDS_KNOWLEDGEBASE_DATASET_PATH=modaclouds/kb\" >> ~/.bashrc";

			installationExecutionCommand += "\n"
					+ "echo \"export MODACLOUDS_MONITORING_DDA_ENDPOINT_IP=" + IPofDA+"\" >> ~/.bashrc";

			installationExecutionCommand += "\n"
					+ "echo \"export MODACLOUDS_MONITORING_DDA_ENDPOINT_PORT="+ portOfDA+"\" >> ~/.bashrc";

			installationExecutionCommand += "\n"
					+ "echo \"export MODACLOUDS_KNOWLEDGEBASE_SYNC_PERIOD=10\" >> ~/.bashrc";

			installationExecutionCommand += "\n"
					+ "echo \"export MODACLOUDS_MONITORED_APP_ID="+ appID+"\" >> ~/.bashrc";

			installationExecutionCommand += "\n"
					+ "echo \"export MODACLOUDS_MONITORED_VM_ID=" + vmID+"\" >> ~/.bashrc";
			
			installationExecutionCommand += "\n"
					+ "source ~/.bashrc";
			
			installationExecutionCommand += "\n"
					+ getExecutionLinuxCommand(metricName, dataCollectorsFileName);
		}
		

		return installationExecutionCommand;
	}

	private static String getExecutionCommand(String metricName,
			String dataCollectorsFileName) {

		String collector = ModacloudsMonitor.findCollector(metricName);

		Map<String, String> collectorExecutionCommandMapping = new HashMap<String, String>();

		collectorExecutionCommandMapping.put("sigar",
				"lib/hyperic-sigar-1.6.4/sigar-bin/lib");

		String executionCommand = collectorExecutionCommandMapping
				.get(collector);

		if (executionCommand == null)

			executionCommand = "START CMD /C CALL java -jar "
					+ dataCollectorsFileName + " kb\n" + "exit";

		else
			executionCommand = "START CMD /C CALL java -Djava.library.path="
					+ executionCommand + " -jar " + dataCollectorsFileName
					+ " kb\n" + "exit";

		return executionCommand != null ? executionCommand : "";
	}
	
	private static String getExecutionLinuxCommand(String metricName,
			String dataCollectorsFileName) {

		String collector = ModacloudsMonitor.findCollector(metricName);

		Map<String, String> collectorExecutionCommandMapping = new HashMap<String, String>();

		collectorExecutionCommandMapping.put("sigar",
				"lib/hyperic-sigar-1.6.4/sigar-bin/lib");

		String executionCommand = collectorExecutionCommandMapping
				.get(collector);

		if (executionCommand == null)

			executionCommand = "nohup java -jar "
					+ dataCollectorsFileName + " kb >dc.out &\n" + "exit";

		else
			executionCommand = "nohup java -Djava.library.path="
					+ executionCommand + " -jar " + dataCollectorsFileName
					+ " kb >dc.out &\n" + "exit";

		return executionCommand != null ? executionCommand : "";
	}
}
