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

package core.modaCloudsMonitoring.modaCloudsMonitoringInitiation;

import java.io.File;

import javax.swing.JOptionPane;

import core.OperatingSystem;
import core.TxtFileWriter;
import core.WindowsBatchFileExecution;

public class MODACloudsMonitoringInitiation {

	private static final String INIT_BATCH_FILE = "./init.bat";

	private static final String SERVER_FUSEKI = "/jena-fuseki-1.1.1";

	private static final String SERVER_MONITORING_METRICS = "/resources/monitoring_metrics.xml";

	private static final String SERVER_CSPARQL = "/rsp-services-csparql-0.4.6.2-modaclouds";

	public static void initiate(String IPofKB, String portOfKB, String IPofDA,
			String portOfDA, String IPofMM, String portOfMM,
			String privatePortOfMM, String seaCloudsFolder) {

		if (OperatingSystem.isWindows()) {

			createBatchfile(IPofKB, portOfKB, IPofDA, portOfDA, IPofMM,
					portOfMM, privatePortOfMM, seaCloudsFolder);

			WindowsBatchFileExecution.execute(INIT_BATCH_FILE);

			new File(INIT_BATCH_FILE).delete();
		}

		else if (OperatingSystem.isUnix())
			JOptionPane.showMessageDialog(null,
					"To initialize for the case of Unix", "Unix",
					JOptionPane.ERROR_MESSAGE);
	}

	private static void createBatchfile(String IPofKB, String portOfKB,
			String IPofDA, String portOfDA, String IPofMM, String portOfMM,
			String privatePortOfMM, String seaCloudsFolder) {

		File file = new File(seaCloudsFolder + SERVER_FUSEKI + "/ds/tdb.lock");

		String content = "set \"MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_IP="
				+ IPofKB
				+ "\""
				+ "\n"
				+ "set \"MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_PORT="
				+ portOfKB
				+ "\""
				+ "\n"
				+ "set \"MODACLOUDS_KNOWLEDGEBASE_DATASET_PATH \"/modaclouds/kb\""
				+ "\n"
				+ "set \"MODACLOUDS_MONITORING_DDA_ENDPOINT_IP="
				+ IPofDA
				+ "\""
				+ "\n"
				+ "set \"MODACLOUDS_MONITORING_DDA_ENDPOINT_PORT="
				+ portOfDA
				+ "\""
				+ "\n"
				+ "set \"MODACLOUDS_MONITORING_MANAGER_PORT="
				+ portOfMM
				+ "\""
				+ "\n"
				+ "set \"MODACLOUDS_MONITORING_MANAGER_PRIVATE_PORT="
				+ privatePortOfMM
				+ "\""
				+ "\n"
				+ "set \"MODACLOUDS_MONITORING_MANAGER_PRIVATE_IP="
				+ IPofMM
				+ "\""
				+ "\n"
				+ "set \"MODACLOUDS_MONITORING_MONITORING_METRICS_FILE="
				+ new File(seaCloudsFolder + SERVER_MONITORING_METRICS)
						.getAbsolutePath() + "\"\n" +

				"cd " + seaCloudsFolder + SERVER_FUSEKI + "\n" +

				"mkdir \"ds\"\n" +

				"del \"" + file.getAbsolutePath() + "\"\n" +

				"START CMD /C CALL fuseki-server.bat --update --port "
				+ portOfKB + " --loc ./ds /modaclouds/kb\n" +

				"cd .." + SERVER_CSPARQL + "\n"
				+ "START CMD /C CALL java -jar rsp-services-csparql.jar\n" +

				"cd ..\n"
				+ "START CMD /C CALL java -jar monitoring-manager-1.4.jar\n"
				+ "exit";

		TxtFileWriter.write(content, INIT_BATCH_FILE);
	}
}
