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

package api.controller;

import java.io.File;

public interface Controller {

	public String initializeMonitor(String SLAServiceURIRulesReady,
			String SLAServiceURIReplanning, String DashboardURIRulesReady,
			String DashboardURIReplanning, String PlannerURIRulesReady,
			String PlannerURIReplanning);

	public String initializeMonitor(String IPofKB, String IPofDA,
			String IPofMM, String portOfKB, String portOfDA, String portOfMM,
			String privatePortOfMM, String SLAServiceURIRulesReady,
			String SLAServiceURIReplanning, String DashboardURIRulesReady,
			String DashboardURIReplanning, String PlannerURIRulesReady,
			String PlannerURIReplanning);

	public String initiateMonitor();

	public void installMonitoringRules(String monitoringRules);

	public void installDeploymentModel(String deploymentModel);

	public String[] getRunningMetrics();

	public String[] getAllMetrics();

	public File getDataCollectors();

	public File getDataCollector(String metricName);

	public String getDataCollectorInstallationFile(String metricName);

	public void uninstallMonitoringRule(String id);

	public void addObserver(String metricName, String portOfObserver,
			String callbackURL);
}
