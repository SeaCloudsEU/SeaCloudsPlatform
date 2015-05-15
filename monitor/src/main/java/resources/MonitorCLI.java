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

package resources;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.io.ByteStreams;

import core.TxtFileReader;
import core.TxtFileWriter;
import core.WindowsBatchFileExecution;
import core.RESTCalls.RESTGet;
import core.RESTCalls.RESTPost;

public class MonitorCLI {

	private static final String INITIALIZATION_CONFIGURATION_FILE = "resources/initialization.properties";
	private static final String MONITORING_RULES_FILE = "resources/chat-WebApplication-monitoringRules.xml";
	private static final String DEPLOYMENT_MODEL_FILE = "resources/chat-WebApplication.json";
	private static final String LOCAL_SIGAR = "lib/hyperic-sigar-1.6.4";
	private static final String DATA_COLLECTORS_FILE_NAME = "data-collector-1.3-SNAPSHOT.jar";
	private static final String DATA_COLLECTORS_INSTALLATION_FILE_NAME = "dataCollectorsInstallation.bat";

	public static void main(String[] args) {

		String metricName = "AppAvailable";
		String metricNameForObserver = "AppAvailabilityViolation";
		String appID = "chat-WebApplication_ID";
		String vmID = "tomcat_server_VM_ID";

		boolean initiated = false, firstTime = true;

		try {

			String msg = null;

			System.out.println("\nSeaClouds Monitoring Platform Menu:\n");

			while (true) {

				if (firstTime || !initiated)
					System.out
							.println("1. Initialize and initiate the monitoring platform");
				System.out
						.println("2: Install monitoring rules (in XML format)");
				System.out
						.println("3: Install a deployment model (in JSON format)");
				System.out.println("4: Get all monitoring metrics");
				System.out.println("5: Get running monitoring metrics");
				System.out
						.println("6: Retrieve the executation file of all data collectors");
				System.out
						.println("7: Retrieve the executation file of a data collector (providing the name of a metric)");
				System.out
						.println("8: Retrieve the installation file of the data collector(s) and install it");
				System.out.println("9: Uninstall monitoring rule");
				System.out.println("10: Add observer");
				System.out.println("11: Send replanning event");
				System.out
						.println("12: Execute existing installation file of data collector(s)");
				System.out.println("13: Exit");
				System.out.print("\nChoice = ");

				BufferedReader input = new BufferedReader(
						new InputStreamReader(System.in));

				String answer = input.readLine();

				System.out.println();

				int answerInt = 0;

				if (answer == null || (answer != null && answer.equals("")))
					answerInt = 0;

				else
					answerInt = Integer.parseInt(answer);

				if (answerInt == 1 && !initiated) {

					File initializationFile = new File(
							INITIALIZATION_CONFIGURATION_FILE);

					if (!initializationFile.exists()) {

						msg = "[INFO] Monitor REST Service Main: Initialization file does not exist.\n";
						System.out.println(msg);

						initiated = false;
					}

					else {

						msg = RESTGet
								.httpGet("http://localhost:8080/monitor/api/clear");

						String configurationFileContent = TxtFileReader
								.read(initializationFile);

						msg = RESTPost.httpPost(
								"http://localhost:8080/monitor/api/initialize",
								configurationFileContent, "text/plain");

						if (msg != null) {

							System.err.println(msg);

							System.exit(-1);
						}

						RESTPost.httpPost("http://localhost:8080/monitor/api/initiate");

						initiated = true;
					}
				}

				else if (firstTime) {

					System.out
							.println("[ERROR] Monitor REST Service Main: You should firstly initialize and initiate the monitoring platform.\n");

					System.out
							.println("Have you already initialized and initiated the monitoring platform (0/1)?");
					System.out.print("\nAnswer = ");

					input = new BufferedReader(new InputStreamReader(System.in));

					answer = input.readLine();

					System.out.println();

					int internalAnswerInt = 0;

					if (answer == null || (answer != null && answer.equals("")))
						internalAnswerInt = 0;

					else
						internalAnswerInt = Integer.parseInt(answer);

					if (internalAnswerInt == 1)
						initiated = true;

					else
						initiated = false;
				}

				firstTime = false;

				if (initiated) {

					if (answerInt == 2) {

						File monitoringRules = new File(MONITORING_RULES_FILE);
						System.out.println("\nmonitoringRules = "
								+ monitoringRules + "\n");

						String monitoringRulesContent = TxtFileReader
								.read(monitoringRules);

						msg = RESTPost
								.httpPost(
										"http://localhost:8080/monitor/api/installMonitoringRules",
										monitoringRulesContent, "xml");

						if (msg != null) {

							System.err.println(msg);

							System.exit(-1);
						}
					}

					else if (answerInt == 3) {

						File deploymentModel = new File(DEPLOYMENT_MODEL_FILE);
						System.out.println("\ndeploymentModel = "
								+ deploymentModel + "\n");

						String deploymentModelContent = TxtFileReader
								.read(deploymentModel);

						msg = RESTPost
								.httpPost(
										"http://localhost:8080/monitor/api/installDeploymentModel",
										deploymentModelContent, "xml");

						if (msg != null) {

							System.err.println(msg);

							System.exit(-1);
						}
					}

					else if (answerInt == 4) {

						String response = RESTGet
								.httpGet("http://localhost:8080/monitor/api/getAllMetrics");

						String[] metrics = response.split("\n");

						System.out.println("\n\tMetrics:");

						for (int i = 0; metrics != null && i < metrics.length; ++i) {

							int counter = i + 1;

							System.out.println("\t" + counter + ": "
									+ metrics[i]);
						}

						System.out.println();
					}

					else if (answerInt == 5) {

						String response = RESTGet
								.httpGet("http://localhost:8080/monitor/api/getRunningMetrics");

						String[] metrics = response.split("\n");

						System.out.println("\n\tMetrics:");

						for (int i = 0; metrics != null && i < metrics.length; ++i) {

							int counter = i + 1;

							System.out.println("\t" + counter + ": "
									+ metrics[i]);
						}

						System.out.println();
					}

					else if (answerInt == 6) {

						InputStream data = RESTGet
								.httpGetResponse("http://localhost:8080/monitor/api/getDataCollectors");

						OutputStream output = new FileOutputStream(
								DATA_COLLECTORS_FILE_NAME);

						ByteStreams.copy(data, output);

						System.out
								.println("[INFO] Monitor REST Service Main: The executation file of data collector has been retrieved.\n");
					}

					else if (answerInt == 7) {

						String response = RESTGet
								.httpGet("http://localhost:8080/monitor/api/getAllMetrics");

						String[] metrics = response.split("\n");

						System.out.println("\n\tMetrics:");

						for (int i = 0; metrics != null && i < metrics.length; ++i) {

							int counter = i + 1;

							System.out.println("\t" + counter + ": "
									+ metrics[i]);
						}

						System.out.print("\n\tMetric name = ");

						input = new BufferedReader(new InputStreamReader(
								System.in));

						answer = input.readLine();

						System.out.println();

						if (answer != null && !answer.equals(""))
							metricName = metrics[Integer.parseInt(answer) - 1];

						System.out
								.println("[INFO] Monitor REST Service Main: Retrieving the executation file of the data collector for the metric '"
										+ metricName + "'...\n");

						InputStream data = RESTPost
								.httpPostResponse("http://localhost:8080/monitor/api/getDataCollector/"
										+ metricName);

						OutputStream output = new FileOutputStream(
								DATA_COLLECTORS_FILE_NAME);

						ByteStreams.copy(data, output);

						System.out
								.println("[INFO] Monitor REST Service Main: The executation file of the data collector for the metric '"
										+ metricName
										+ "' has been retrieved.\n");
					}

					else if (answerInt == 8) {

						String fileContent = RESTPost
								.httpPost("http://localhost:8080/monitor/api/getDataCollectorInstallationFile/"
										+ metricName + "/" + appID + "/" + vmID);

						TxtFileWriter.write(fileContent,
								DATA_COLLECTORS_INSTALLATION_FILE_NAME);

						if (!new File(LOCAL_SIGAR).exists()
								&& metricName.equals("CPUUtilization")) {

							System.out
									.println("Downloading hyperic-sigar-1.6.4.zip...");

							download("hyperic-sigar-1.6.4.zip",
									"https://magelan.googlecode.com/files/hyperic-sigar-1.6.4.zip");

							unzip("lib/hyperic-sigar-1.6.4.zip", "lib");
						}

						WindowsBatchFileExecution
								.execute(DATA_COLLECTORS_INSTALLATION_FILE_NAME);

						System.out
								.println("[INFO] Monitor REST Service Main: The installation file of data collector has been retrieved and executed.\n");
					}

					else if (answerInt == 9) {

						System.out
								.println("Provide the id of the monitoring rule");
						System.out.print("\nID = ");

						input = new BufferedReader(new InputStreamReader(
								System.in));

						answer = input.readLine();

						System.out.println();

						msg = RESTPost
								.httpPost("http://localhost:8080/monitor/api/uninstallMonitoringRule/"
										+ answer);

						System.out.println(msg);
					}

					else if (answerInt == 10) {

						System.out.println("Provide the metric name");
						System.out.print("\nMetric name = ");

						input = new BufferedReader(new InputStreamReader(
								System.in));

						answer = input.readLine();

						if (answer != null && !answer.equals(""))
							metricNameForObserver = answer;

						System.out.println();

						String port = "8123";

						System.out
								.println("Provide the port of the observer (default = 8123)");
						System.out.print("\nPort = ");

						input = new BufferedReader(new InputStreamReader(
								System.in));

						answer = input.readLine();

						if (answer != null && !answer.equals(""))
							port = answer;

						System.out.println();

						String callbackURL = "http://localhost:8123";

						System.out
								.println("Provide the callback IP (default = localhost)");
						System.out.print("\nURL = ");

						input = new BufferedReader(new InputStreamReader(
								System.in));

						answer = input.readLine();

						if (answer != null && !answer.equals(""))
							callbackURL = "http://" + answer + ":" + port;

						System.out.println();

						msg = RESTPost.httpPost(
								"http://localhost:8080/monitor/api/addObserver/"
										+ metricNameForObserver + "/" + port,
								callbackURL);

						if (msg != null) {

							System.err.println(msg);

							System.exit(-1);
						}
					}

					else if (answerInt == 11) {

						String replanningEvent = "Dummy event";

						msg = RESTPost
								.httpPost(
										"http://localhost:8080/monitor/api/sendReplanningEvent",
										replanningEvent, "xml");

						System.out.println(msg);
					}

					else if (answerInt == 12) {

						if (!new File(LOCAL_SIGAR).exists()
								&& metricName.equals("CPUUtilization")) {

							System.out
									.println("Downloading hyperic-sigar-1.6.4.zip...");

							download("lib/hyperic-sigar-1.6.4.zip",
									"https://magelan.googlecode.com/files/hyperic-sigar-1.6.4.zip");

							System.out
									.println("Unzipping hyperic-sigar-1.6.4.zip...");
							unzip("lib/hyperic-sigar-1.6.4.zip", "lib");
						}

						WindowsBatchFileExecution
								.execute(DATA_COLLECTORS_INSTALLATION_FILE_NAME);

						System.out
								.println("[INFO] Monitor REST Service Main: The installation file of data collector has been executed.\n");
					}

					else if (answerInt == 13) {

						Runtime.getRuntime().exec("taskkill /im cmd.exe");

						System.out
								.println("[INFO] Monitor REST Service Main: The client of the monitoring platform is terminated.\n");

						System.exit(0);
					}
				}
			}
		}

		catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	private static void download(String fileName, String url) {

		try {

			URL link = new URL(url);

			InputStream in = new BufferedInputStream(link.openStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n = 0;
			while (-1 != (n = in.read(buf)))
				out.write(buf, 0, n);

			out.close();
			in.close();
			byte[] response = out.toByteArray();

			FileOutputStream fos = new FileOutputStream("lib/" + fileName);
			fos.write(response);
			fos.close();
		}

		catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	private static void unzip(String zipFilePath, String destDirectory)
			throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(
				zipFilePath));
		ZipEntry entry = zipIn.getNextEntry();
		while (entry != null) {
			String filePath = destDirectory + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				extractFile(zipIn, filePath);
			} else {
				File dir = new File(filePath);
				dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	private static void extractFile(ZipInputStream zipIn, String filePath)
			throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(filePath));
		byte[] bytesIn = new byte[4096];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}
}
