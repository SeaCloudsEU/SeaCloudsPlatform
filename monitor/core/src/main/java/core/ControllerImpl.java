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

package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import api.controller.Controller;
import core.modaCloudsMonitoring.modaCloudsDataCollectors.MODACloudsDataCollectors;
import core.modaCloudsMonitoring.modaCloudsDeploymentModel.MODACloudsDeploymentModel;
import core.modaCloudsMonitoring.modaCloudsMetrics.MODACloudsMetrics;
import core.modaCloudsMonitoring.modaCloudsMetrics.MODACloudsObservers;
import core.modaCloudsMonitoring.modaCloudsMonitoringInitiation.MODACloudsMonitoringInitiation;
import core.modaCloudsMonitoring.modaCloudsMonitoringRules.MODACloudsMonitoringRules;

public class ControllerImpl implements Controller {

	private static final String SEACLOUDS_FOLDER = "SeaClouds";
	private static final String INITIALIZATION_CONFIGURATION_FILE_ON_SERVER = SEACLOUDS_FOLDER
			+ "/storedInitialization.properties";

	private static final String SERVER_DATA_COLLECTORS_PATH = "SeaClouds/";
	private static final String DATA_COLLECTORS_FILE_NAME = "data-collector-1.3-SNAPSHOT.jar";

	public String initializeMonitor(String SLAServiceURIRulesReady,
			String SLAServiceURIReplanning, String DashboardURIRulesReady,
			String DashboardURIReplanning, String PlannerURIRulesReady,
			String PlannerURIReplanning) {

		storeInitializationData(null, null, null, null, null, null, null,
				SLAServiceURIRulesReady, SLAServiceURIReplanning,
				DashboardURIRulesReady, DashboardURIReplanning,
				PlannerURIRulesReady, PlannerURIReplanning);

		return null;
	}

	public String initializeMonitor(String IPofKB, String IPofDA,
			String IPofMM, String portOfKB, String portOfDA, String portOfMM,
			String privatePortOfMM, String SLAServiceURIRulesReady,
			String SLAServiceURIReplanning, String DashboardURIRulesReady,
			String DashboardURIReplanning, String PlannerURIRulesReady,
			String PlannerURIReplanning) {

		storeInitializationData(IPofKB, IPofDA, IPofMM, portOfKB, portOfDA,
				portOfMM, privatePortOfMM, SLAServiceURIRulesReady,
				SLAServiceURIReplanning, DashboardURIRulesReady,
				DashboardURIReplanning, PlannerURIRulesReady,
				PlannerURIReplanning);

		return null;
	}

	private void storeInitializationData(String IPofKB, String IPofDA,
			String IPofMM, String portOfKB, String portOfDA, String portOfMM,
			String privatePortOfMM, String SLAServiceURIRulesReady,
			String SLAServiceURIReplanning, String DashboardURIRulesReady,
			String DashboardURIReplanning, String PlannerURIRulesReady,
			String PlannerURIReplanning) {

		String fileContent = "";

		if (IPofKB != null)
			fileContent += "IPofKB=" + IPofKB;
		if (IPofDA != null)
			fileContent += "\nIPofDA=" + IPofDA;
		if (IPofMM != null)
			fileContent += "\nIPofMM=" + IPofMM;
		if (portOfKB != null)
			fileContent += "\nportOfKB=" + portOfKB;
		if (portOfDA != null)
			fileContent += "\nportOfDA=" + portOfDA;
		if (portOfMM != null)
			fileContent += "\nportOfMM=" + portOfMM;
		if (privatePortOfMM != null)
			fileContent += "\nprivatePortOfMM=" + privatePortOfMM;
		if (SLAServiceURIRulesReady != null)
			fileContent += "\nSLAServiceURIRulesReady="
					+ SLAServiceURIRulesReady;
		if (SLAServiceURIReplanning != null)
			fileContent += "\nSLAServiceURIReplanning="
					+ SLAServiceURIReplanning;
		if (DashboardURIRulesReady != null)
			fileContent += "\nDashboardURIRulesReady=" + DashboardURIRulesReady;
		if (DashboardURIReplanning != null)
			fileContent += "\nDashboardURIReplanning=" + DashboardURIReplanning;
		if (PlannerURIRulesReady != null)
			fileContent += "\nPlannerURIRulesReady=" + PlannerURIRulesReady;
		if (PlannerURIReplanning != null)
			fileContent += "\nPlannerURIReplanning=" + PlannerURIReplanning;

		new File(SEACLOUDS_FOLDER).mkdirs();

		TxtFileWriter.write(fileContent,
				INITIALIZATION_CONFIGURATION_FILE_ON_SERVER);
	}

	public String initiateMonitor() {

		String msg = null;

		Properties properties = readInitializationData(INITIALIZATION_CONFIGURATION_FILE_ON_SERVER);

		String IPofKB = properties.getProperty("IPofKB");

		if (IPofKB != null) {

			String IPofDA = properties.getProperty("IPofDA");
			String IPofMM = properties.getProperty("IPofMM");
			String portOfKB = properties.getProperty("portOfKB");
			String portOfDA = properties.getProperty("portOfDA");
			String portOfMM = properties.getProperty("portOfMM");
			String privatePortOfMM = properties.getProperty("privatePortOfMM");

			msg = MODACloudsMonitoringInitiation.initiate(IPofKB, portOfKB,
					IPofDA, portOfDA, IPofMM, portOfMM, privatePortOfMM,
					SEACLOUDS_FOLDER);
		}

		return msg;
	}

	private static Properties readInitializationData(String initilizationFile) {

		Properties properties = null;

		File file = new File(initilizationFile);

		if (file.exists()) {

			properties = new Properties();

			try {

				properties.load(new FileInputStream(initilizationFile));
			}

			catch (IOException ex) {

				ex.printStackTrace();

				properties = null;
			}
		}

		return properties;
	}

	public void installMonitoringRules(String monitoringRules) {

		Properties properties = readInitializationData(INITIALIZATION_CONFIGURATION_FILE_ON_SERVER);

		String IPofMM = properties.getProperty("IPofMM");

		if (IPofMM != null) {

			String portOfMM = properties.getProperty("portOfMM");

			MODACloudsMonitoringRules.installMonitoringRules(IPofMM, portOfMM,
					monitoringRules);
		}
	}

	public void installDeploymentModel(String deploymentModel) {

		Properties properties = readInitializationData(INITIALIZATION_CONFIGURATION_FILE_ON_SERVER);

		String IPofKB = properties.getProperty("IPofKB");

		if (IPofKB != null) {

			String IPofMM = properties.getProperty("IPofMM");
			String portOfMM = properties.getProperty("portOfMM");

			MODACloudsDeploymentModel.installDeploymentModel(IPofMM, portOfMM,
					deploymentModel);
		}
	}

	public String[] getRunningMetrics() {

		Properties properties = readInitializationData(INITIALIZATION_CONFIGURATION_FILE_ON_SERVER);

		String IPofMM = properties.getProperty("IPofMM");
		String portOfMM = properties.getProperty("portOfMM");

		if (IPofMM != null && portOfMM != null)
			return MODACloudsMetrics.getRunningMetrics(IPofMM, portOfMM);

		return null;
	}

	public String[] getAllMetrics() {

		return MODACloudsMetrics.getAllMetrics();
	}

	public File getDataCollectors() {

		return MODACloudsDataCollectors.getDataCollectorsJar(
				SERVER_DATA_COLLECTORS_PATH, DATA_COLLECTORS_FILE_NAME);
	}

	public File getDataCollector(String metricName) {

		return MODACloudsDataCollectors.getDataCollectorJar(metricName,
				SERVER_DATA_COLLECTORS_PATH, DATA_COLLECTORS_FILE_NAME);
	}

	public String getDataCollectorInstallationFile(String metricName,
			String appID, String vmID) {

		Properties properties = readInitializationData(INITIALIZATION_CONFIGURATION_FILE_ON_SERVER);

		String IPofKB = properties.getProperty("IPofKB");
		String portOfKB = properties.getProperty("portOfKB");
		String IPofDA = properties.getProperty("IPofDA");
		String portOfDA = properties.getProperty("portOfDA");

		if (IPofKB != null && portOfKB != null && IPofDA != null
				&& portOfDA != null)
			return MODACloudsDataCollectors.formInstallationExecutionCommand(
					metricName, IPofKB, portOfKB, IPofDA, portOfDA,
					DATA_COLLECTORS_FILE_NAME, appID, vmID);

		return null;
	}

	public void uninstallMonitoringRule(String id) {

		Properties properties = readInitializationData(INITIALIZATION_CONFIGURATION_FILE_ON_SERVER);

		String IPofMM = properties.getProperty("IPofMM");
		String portOfMM = properties.getProperty("portOfMM");

		if (IPofMM != null && portOfMM != null)
			MODACloudsMonitoringRules.uninstallMonitoringRules(IPofMM,
					portOfMM, id);
	}

	public void addObserver(String metricName, String portOfObserver,
			String callbackURL) {

		Properties properties = readInitializationData(INITIALIZATION_CONFIGURATION_FILE_ON_SERVER);

		String IPofMM = properties.getProperty("IPofMM");
		String portOfMM = properties.getProperty("portOfMM");

		if (IPofMM != null && portOfMM != null) {

			MODACloudsObservers.addObserver(IPofMM, portOfMM, metricName,
					callbackURL);

			MODACloudsObservers.startObserver(portOfObserver);
		}
	}
}
