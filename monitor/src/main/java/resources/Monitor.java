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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api.controller.Controller;
import core.ControllerImpl;
import core.TxtFileWriter;
import core.RESTCalls.RESTPost;

@Path("/api")
public class Monitor {

	private static final String SEACLOUDS_FOLDER = "SeaClouds";
	private static final String INITIALIZATION_CONFIGURATION_FILE_ON_SERVER = SEACLOUDS_FOLDER
			+ "/storedInitialization.properties";

	private static final String SERVER_RESOURCES = "/resources";

	private static final String DATA_COLLECTORS_FILE_NAME = "data-collector-1.3-SNAPSHOT.jar";

	static Logger log = LoggerFactory.getLogger(Monitor.class);

	@POST
	@Produces("text/plain")
	@Path("/initialize")
	public String initialize(String configurationFileContent) {

		delete(new File(SEACLOUDS_FOLDER));

		new File(SEACLOUDS_FOLDER).mkdirs();

		TxtFileWriter.write(configurationFileContent,
				INITIALIZATION_CONFIGURATION_FILE_ON_SERVER);

		Properties properties = read(INITIALIZATION_CONFIGURATION_FILE_ON_SERVER);

		String IPofKB = properties.getProperty("IPofKB");
		String IPofDA = properties.getProperty("IPofDA");
		String IPofMM = properties.getProperty("IPofMM");
		String portOfKB = properties.getProperty("portOfKB");
		String portOfDA = properties.getProperty("portOfDA");
		String portOfMM = properties.getProperty("portOfMM");
		String privatePortOfMM = properties.getProperty("privatePortOfMM");
		String SLAServiceURIRulesReady = properties
				.getProperty("SLAServiceURIRulesReady");
		String SLAServiceURIReplanning = properties
				.getProperty("SLAServiceURIReplanning");
		String DashboardURIRulesReady = properties
				.getProperty("DashboardURIRulesReady");
		String DashboardURIReplanning = properties
				.getProperty("DashboardURIReplanning");
		String PlannerURIRulesReady = properties
				.getProperty("PlannerURIRulesReady");
		String PlannerURIReplanning = properties
				.getProperty("PlannerURIReplanning");

		Controller controller = new ControllerImpl();

		String msg = controller.initializeMonitor(IPofKB, IPofDA, IPofMM,
				portOfKB, portOfDA, portOfMM, privatePortOfMM,
				SLAServiceURIRulesReady, SLAServiceURIReplanning,
				DashboardURIRulesReady, DashboardURIReplanning,
				PlannerURIRulesReady, PlannerURIReplanning);

		return msg;
	}

	@GET
	@Produces("text/plain")
	@Path("/clear")
	public String clear() {

		TxtFileWriter.write("", INITIALIZATION_CONFIGURATION_FILE_ON_SERVER);

		return "[INFO] Monitor controller: The initialization data have been cleared.";
	}

	@POST
	@Path("/initiate")
	public void initiate() {

		downloadFiles();

		Controller controller = new ControllerImpl();

		controller.initiateMonitor();
	}

	@POST
	@Produces("text/plain")
	@Path("/installMonitoringRules")
	public String installMonitoringRules(String monitoringRules) {

		Controller controller = new ControllerImpl();

		controller.installMonitoringRules(monitoringRules);

		String msg = null;

		try {
			notifyForMonitoringRules(monitoringRules);
		} catch (Exception ex) {
			msg = ex.getMessage();
		}

		return msg;
	}

	private void notifyForMonitoringRules(String monitoringRules)
			throws Exception {

		Properties properties = read(INITIALIZATION_CONFIGURATION_FILE_ON_SERVER);

		if (properties != null) {

			String SLAServiceURIRulesReady = properties
					.getProperty("SLAServiceURIRulesReady");
			String DashboardURIRulesReady = properties
					.getProperty("DashboardURIRulesReady");
			String PlannerURIRulesReady = properties
					.getProperty("PlannerURIRulesReady");

			if (SLAServiceURIRulesReady != null)
				RESTPost.httpPost(SLAServiceURIRulesReady);

			if (DashboardURIRulesReady != null)
				RESTPost.httpPost(DashboardURIRulesReady, monitoringRules,
						"xml");

			if (PlannerURIRulesReady != null)
				RESTPost.httpPost(PlannerURIRulesReady, monitoringRules, "xml");
		}
	}

	@POST
	@Path("/installDeploymentModel")
	public void installDeploymentModel(String deploymentModel) {

		Controller controller = new ControllerImpl();

		controller.installDeploymentModel(deploymentModel);
	}

	@GET
	@Produces("text/plain")
	@Path("/getAllMetrics")
	public String getAllMetrics() {

		Controller controller = new ControllerImpl();

		String[] metrics = controller.getAllMetrics();

		String response = "";

		for (int i = 0; metrics != null && i < metrics.length; ++i)
			response += metrics[i] + "\n";

		return response;
	}

	@GET
	@Produces("text/plain")
	@Path("/getRunningMetrics")
	public String getRunningMetrics() {

		Controller controller = new ControllerImpl();

		String[] metrics = controller.getRunningMetrics();

		String response = "";

		for (int i = 0; metrics != null && i < metrics.length; ++i)
			response += metrics[i] + "\n";

		return response;
	}

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/getDataCollectors")
	public Response getDataCollectors() {

		Controller controller = new ControllerImpl();

		File file = controller.getDataCollectors();

		ResponseBuilder response = Response.ok((Object) file);

		response.header("Content-Disposition", "attachment; filename="
				+ DATA_COLLECTORS_FILE_NAME);

		return response.build();
	}

	@POST
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/getDataCollector/{metricName}")
	public Response getDataCollector(@PathParam("metricName") String metricName) {

		Controller controller = new ControllerImpl();

		File file = controller.getDataCollector(metricName);

		ResponseBuilder response = Response.ok((Object) file);

		response.header("Content-Disposition", "attachment; filename="
				+ DATA_COLLECTORS_FILE_NAME);

		return response.build();
	}

	@POST
	@Produces("text/plain")
	@Path("/getDataCollectorInstallationFile/{metricName}/{appID}/{vmID}")
	public String getDataCollectorInstallationFile(
			@PathParam("metricName") String metricName,
			@PathParam("appID") String appID, @PathParam("vmID") String vmID) {

		Controller controller = new ControllerImpl();

		return controller.getDataCollectorInstallationFile(metricName, appID,
				vmID);
	}

	@POST
	@Path("/uninstallMonitoringRule/{id}")
	public void uninstallMonitoringRule(@PathParam("id") String id) {

		Controller controller = new ControllerImpl();

		controller.uninstallMonitoringRule(id);
	}

	@POST
	@Path("/addObserver/{metricName}/{portOfObserver}")
	public void addObserver(@PathParam("metricName") String metricName,
			@PathParam("portOfObserver") String portOfObserver,
			String callbackURL) {

		Controller controller = new ControllerImpl();

		controller.addObserver(metricName, portOfObserver, callbackURL);
	}

	@POST
	@Produces("text/plain")
	@Path("/sendReplanningEvent")
	public String sendReplanningEvent(String replanningEvent) {

		String msg = notifyForReplanningEvent(replanningEvent);

		if (msg == null)
			msg = "";

		return "[INFO] Monitor REST Service: Sending replanning event...\n\n"
				+ replanningEvent + "\n" + msg;
	}

	private String notifyForReplanningEvent(String replanningEvent) {

		String msg = "", msg1 = "", msg2 = "", msg3 = "";

		Properties properties = read(INITIALIZATION_CONFIGURATION_FILE_ON_SERVER);

		if (properties == null)
			msg = "[ERROR] Monitor REST Service: there is no calling information about the external SeaClouds components.";

		else {

			try {

				String SLAServiceURIReplanning = properties
						.getProperty("SLAServiceURIReplanning");
				String DashboardURIReplanning = properties
						.getProperty("DashboardURIReplanning");
				String PlannerURIReplanning = properties
						.getProperty("PlannerURIReplanning");

				if (SLAServiceURIReplanning == null)
					msg1 = "[ERROR] Monitor REST Service: there is no calling information about the SLA Service.";

				else
					msg1 = RESTPost.httpPost(SLAServiceURIReplanning,
							replanningEvent, "xml");

				if (msg1 != null)
					msg += msg1;

				if (DashboardURIReplanning == null)
					msg2 = "[ERROR] Monitor REST Service: there is no calling information about the Dashboard.";

				else
					msg2 = RESTPost.httpPost(DashboardURIReplanning,
							replanningEvent, "xml");

				if (msg2 != null)
					msg += "\n" + msg2;

				if (PlannerURIReplanning == null)
					msg3 = "[ERROR] Monitor REST Service: there is no calling information about the Planner.";

				else
					msg3 = RESTPost.httpPost(PlannerURIReplanning,
							replanningEvent, "xml");

				if (msg3 != null)
					msg += "\n" + msg3;
			}

			catch (Exception ex) {

				ex.printStackTrace();
			}
		}

		return msg;
	}

	private static Properties read(String initilizationFile) {

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

	private static void delete(File folder) {

		if (folder.exists()) {

			File[] files = folder.listFiles();

			for (int i = 0; files != null && i < files.length; i++) {

				if (files[i].isDirectory())
					delete(files[i]);

				else
					files[i].delete();
			}

			folder.delete();
		}
	}

	private static void downloadFiles() {

		new File(SEACLOUDS_FOLDER + SERVER_RESOURCES).mkdirs();

		log.info("Downloading monitoring_metrics.xml...");
		System.out.println("Downloading monitoring_metrics.xml...");
		download(SEACLOUDS_FOLDER + SERVER_RESOURCES
				+ "/monitoring_metrics.xml",
				"http://www.cs.uoi.gr/~dathanas/monitoring_metrics.xml");

		log.info("Downloading jena-fuseki-1.1.1.zip...");
		System.out.println("Downloading jena-fuseki-1.1.1.zip...");
		download(
				SEACLOUDS_FOLDER + "/jena-fuseki-1.1.1.zip",
				"http://archive.apache.org/dist/jena/binaries/jena-fuseki-1.1.1-distribution.zip");

		try {
			log.info("Unzipping jena-fuseki-1.1.1.zip...");
			System.out.println("Unzipping jena-fuseki-1.1.1.zip...");
			unzip(SEACLOUDS_FOLDER + "/jena-fuseki-1.1.1.zip", SEACLOUDS_FOLDER);
		} catch (Exception ex) {
		}

		log.info("Downloading rsp-services-csparql-0.4.6.2-modaclouds.zip...");
		System.out
				.println("Downloading rsp-services-csparql-0.4.6.2-modaclouds.zip...");
		download(
				SEACLOUDS_FOLDER + "/"
						+ "rsp-services-csparql-0.4.6.2-modaclouds.zip",
				"http://www.cs.uoi.gr/~dathanas/rsp-services-csparql-0.4.6.2-modaclouds-distribution.zip");

		try {
			log.info("Unzipping rsp-services-csparql-0.4.6.2-modaclouds.zip...");
			System.out
					.println("Unzipping rsp-services-csparql-0.4.6.2-modaclouds.zip...");
			unzip(SEACLOUDS_FOLDER
					+ "/rsp-services-csparql-0.4.6.2-modaclouds.zip",
					SEACLOUDS_FOLDER);
		} catch (Exception ex) {
		}

		log.info("Downloading monitoring-manager-1.4.jar...");
		System.out.println("Downloading monitoring-manager-1.4.jar...");
		download(SEACLOUDS_FOLDER + "/" + "monitoring-manager-1.4.jar",
				"http://www.cs.uoi.gr/~dathanas/monitoring-manager-1.4.jar");

		log.info("Downloading data-collector-1.3-SNAPSHOT.jar...");
		System.out.println("Downloading data-collector-1.3-SNAPSHOT.jar...");
		download(SEACLOUDS_FOLDER + "/" + "data-collector-1.3-SNAPSHOT.jar",
				"http://www.cs.uoi.gr/~dathanas/data-collector-1.3-SNAPSHOT.jar");
	}

	private static void download(String file, String url) {

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

			FileOutputStream fos = new FileOutputStream(file);
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
