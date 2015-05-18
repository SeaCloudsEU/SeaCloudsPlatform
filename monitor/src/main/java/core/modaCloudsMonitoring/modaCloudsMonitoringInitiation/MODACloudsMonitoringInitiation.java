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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import core.OperatingSystem;
import core.TxtFileWriter;
import core.UnixFileExecution;
import core.WindowsBatchFileExecution;

public class MODACloudsMonitoringInitiation {

	private static final String INIT_BATCH_FILE = "./init.bat";

	private static final String EXPORT_UNIX_FILE = ".bashrc";

	private static final String INIT_UNIX_FILE = "init.sh";

	private static final String SERVER_FUSEKI = "/jena-fuseki-1.1.1";

	private static final String SERVER_MONITORING_METRICS = "/resources/monitoring_metrics.xml";

	private static final String SERVER_CSPARQL = "/rsp-services-csparql-0.4.6.2-modaclouds";

	public static String initiate(String IPofKB, String portOfKB,
			String IPofDA, String portOfDA, String IPofMM, String portOfMM,
			String privatePortOfMM, String seaCloudsFolder) {

		if (OperatingSystem.isWindows()) {

			createBatchfile(IPofKB, portOfKB, IPofDA, portOfDA, IPofMM,
					portOfMM, privatePortOfMM, seaCloudsFolder);

			WindowsBatchFileExecution.execute(INIT_BATCH_FILE);

			new File(INIT_BATCH_FILE).delete();
		}

		else if (OperatingSystem.isUnix()) {
			createUnixfile(IPofKB, portOfKB, IPofDA, portOfDA, IPofMM,
					portOfMM, privatePortOfMM, seaCloudsFolder);
			new File(EXPORT_UNIX_FILE).delete();
		} else if (OperatingSystem.isLinux()) {
			createUnixfile(IPofKB, portOfKB, IPofDA, portOfDA, IPofMM,
					portOfMM, privatePortOfMM, seaCloudsFolder);
			UnixFileExecution.execute(new File(EXPORT_UNIX_FILE)
					.getAbsolutePath(), new File(INIT_UNIX_FILE)
					.getAbsolutePath());

			// new File(INIT_UNIX_FILE).delete();
		}

		return null;
	}

	private static void createBatchfile(String IPofKB, String portOfKB,
			String IPofDA, String portOfDA, String IPofMM, String portOfMM,
			String privatePortOfMM, String seaCloudsFolder) {

		File file = new File(seaCloudsFolder + SERVER_FUSEKI + "/ds/tdb.lock");

		String content = "setx MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_IP \""
				+ IPofKB
				+ "\" /M"
				+ "\n"

				+ "setx MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_PORT \""
				+ portOfKB
				+ "\" /M"
				+ "\n"

				+ "setx MODACLOUDS_KNOWLEDGEBASE_DATASET_PATH \"/modaclouds/kb\" /M"
				+ "\n"

				+ "setx MODACLOUDS_MONITORING_DDA_ENDPOINT_IP \""
				+ IPofDA
				+ "\" /M"
				+ "\n"

				+ "setx MODACLOUDS_MONITORING_DDA_ENDPOINT_PORT \""
				+ portOfDA
				+ "\" /M"
				+ "\n"

				+ "setx MODACLOUDS_MONITORING_MANAGER_PORT \""
				+ portOfMM
				+ "\" /M"
				+ "\n"

				+ "setx MODACLOUDS_MONITORING_MANAGER_PRIVATE_PORT \""
				+ privatePortOfMM
				+ "\" /M"
				+ "\n"

				+ "setx MODACLOUDS_MONITORING_MANAGER_PRIVATE_IP \""
				+ IPofMM
				+ "\" /M"
				+ "\n"

				+ "setx MODACLOUDS_MONITORING_MONITORING_METRICS_FILE \""
				+ new File(seaCloudsFolder + SERVER_MONITORING_METRICS)
						.getAbsolutePath() + "\" /M \n"

				+ "cd " + seaCloudsFolder + SERVER_FUSEKI + "\n"

				+ "mkdir \"ds\"\n"

				+ "del \"" + file.getAbsolutePath() + "\"\n"

				+ "START CMD /C CALL fuseki-server.bat --update --port "
				+ portOfKB + " --loc ./ds /modaclouds/kb\n"

				+ "cd .." + SERVER_CSPARQL + "\n"
				+ "START CMD /C CALL java -jar rsp-services-csparql.jar\n" +

				"cd ..\n"
				+ "START CMD /C CALL java -jar monitoring-manager-1.4.jar\n"
				+ "exit";

		TxtFileWriter.write(content, INIT_BATCH_FILE);
	}

	private static void createUnixfile(String IPofKB, String portOfKB,
			String IPofDA, String portOfDA, String IPofMM, String portOfMM,
			String privatePortOfMM, String seaCloudsFolder) {

		File file = new File(seaCloudsFolder + SERVER_FUSEKI + "/ds/tdb.lock");

		String toExport = "export MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_IP="
				+ IPofKB
				+ "\n"

				+ "export MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_PORT="
				+ portOfKB
				+ "\n"

				+ "export MODACLOUDS_KNOWLEDGEBASE_DATASET_PATH=/modaclouds/kb "
				+ "\n"

				+ "export MODACLOUDS_MONITORING_DDA_ENDPOINT_IP="
				+ IPofDA
				+ "\n"

				+ "export MODACLOUDS_MONITORING_DDA_ENDPOINT_PORT="
				+ portOfDA
				+ "\n"

				+ "export MODACLOUDS_MONITORING_MANAGER_PORT="
				+ portOfMM
				+ "\n"

				+ "export MODACLOUDS_MONITORING_MANAGER_PRIVATE_PORT="
				+ privatePortOfMM
				+ "\n"

				+ "export MODACLOUDS_MONITORING_MANAGER_PRIVATE_IP="
				+ IPofMM
				+ "\n"

				+ "export MODACLOUDS_MONITORING_MONITORING_METRICS_FILE="
				+ new File(seaCloudsFolder + SERVER_MONITORING_METRICS)
						.getAbsolutePath() + "\n";

		String initContent = ""
				+ "cd "
				+ seaCloudsFolder
				+ SERVER_FUSEKI
				+ "\n"

				+ "mkdir \"ds\"\n"

				+ "rm -f \""
				+ file.getAbsolutePath()
				+ "\"\n"

				+ "chmod +x fuseki-server\n"

				+ "nohup ./fuseki-server --update --port "
				+ portOfKB
				+ " --loc ./ds /modaclouds/kb >> ~/logs/fuseki.log 2>&1 &\n"

				+ "cd .."
				+ SERVER_CSPARQL
				+ "\n"
				+ "nohup java -jar rsp-services-csparql.jar >> ~/logs/dda.log 2>&1 &\n"
				+

				"cd ..\n"
				+ "nohup java -jar monitoring-manager-1.4.jar >> ~/logs/mm.log 2>&1 &\n"
				+ "exit";

		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter(new File(EXPORT_UNIX_FILE).getAbsolutePath(),
						true)))) {
			out.println(toExport);
		} catch (IOException e) {
		}

		try (PrintWriter out = new PrintWriter(
				new BufferedWriter(new FileWriter(
						new File(INIT_UNIX_FILE).getAbsolutePath(), true)))) {
			out.println(initContent);
		} catch (IOException e) {
		}
	}
}
