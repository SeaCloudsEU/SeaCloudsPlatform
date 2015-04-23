package api.controller;

import java.io.File;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public interface Controller {

	public String initializeMonitor( String SLAServiceURIRulesReady, String SLAServiceURIReplanning, String DashboardURIRulesReady, String DashboardURIReplanning, String PlannerURIRulesReady, String PlannerURIReplanning );//In case MODACLouds is installed in the localhost.

	public String initializeMonitor( String IPofKB, String IPofDA, String IPofMM, String portOfKB, String portOfDA, String portOfMM, String privatePortOfMM, String SLAServiceURIRulesReady, String SLAServiceURIReplanning, String DashboardURIRulesReady, String DashboardURIReplanning, String PlannerURIRulesReady, String PlannerURIReplanning );

	public String initiateMonitor();

	public String installMonitoringRules( String monitoringRules );

	public String installDeploymentModel( String deploymentModel );

	public String[] getRunningMetrics();

	public String[] getAllMetrics();

	public File getDataCollectors();//It returns a single execution file for all data collectors.

	public File getDataCollector( String metricName );

	public String getDataCollectorInstallationFile( String metricName );//It returns the content of the installation file of a data collector as a String.

	public String uninstallMonitoringRule( String id );

	public String addObserver( String metricName, String portOfObserver, String callbackURL );
}
