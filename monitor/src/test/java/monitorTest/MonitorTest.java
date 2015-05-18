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

package monitorTest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import resources.Monitor;
import core.OperatingSystem;
import core.TxtFileReader;
import core.TxtFileWriter;
import core.WindowsBatchFileExecution;

public class MonitorTest {

	private static final String SEACLOUDS_FOLDER = "SeaClouds";
	private static final String INITIALIZATION_CONFIGURATION_FILE = "resources/initializationForTesting.properties";
	private static final String MONITORING_RULES_FILE = "resources/chat-WebApplication-monitoringRules.xml";
	private static final String DEPLOYMENT_MODEL_FILE = "resources/chat-WebApplication.json";
	private static final String DATA_COLLECTORS_FILE_NAME = "data-collector-1.3-SNAPSHOT.jar";
	private static final String DATA_COLLECTORS_INSTALLATION_FILE_NAME = "dataCollectorsInstallation.bat";

	static Logger log = LoggerFactory.getLogger(MonitorTest.class);

	@Test
	public void test() {

		if (!OperatingSystem.isWindows())
			Assert.assertNotEquals(OperatingSystem.getOsName(),
					OperatingSystem.windows);
		else {

			String metricName = "AppAvailable";
			String appID = "chat-WebApplication_ID";
			String vmID = "tomcat_server_VM_ID";

			new File(SEACLOUDS_FOLDER).mkdirs();

			System.out
					.println("1. Initialize and initiate the monitoring platform...");
			log.info("1. Initialize and initiate the monitoring platform...");

			File initializationFile = new File(
					INITIALIZATION_CONFIGURATION_FILE);

			String configurationFileContent = TxtFileReader
					.read(initializationFile);

			Monitor monitor = new Monitor();

			String msg = monitor.initialize(configurationFileContent);
			Assert.assertEquals(msg, null);

			monitor.initiate();
			try {
				Thread.sleep(20000);
			} catch (InterruptedException ex) {
				log.error(ex.getMessage());
				System.out.println(ex.getMessage());
			}

			System.out
					.println("2: Install monitoring rules (in XML format)...");
			log.info("2: Install monitoring rules (in XML format)...");
			File monitoringRules = new File(MONITORING_RULES_FILE);
			String monitoringRulesContent = TxtFileReader.read(monitoringRules);
			msg = monitor.installMonitoringRules(monitoringRulesContent);
			try {
				Thread.sleep(4000);
			} catch (InterruptedException ex) {
				log.error(ex.getMessage());
				System.out.println(ex.getMessage());
			}
			Assert.assertEquals(msg, null);

			System.out
					.println("3: Install a deployment model (in JSON format)...");
			log.info("3: Install a deployment model (in JSON format)...");
			File deploymentModel = new File(DEPLOYMENT_MODEL_FILE);
			String deploymentModelContent = TxtFileReader.read(deploymentModel);
			monitor.installDeploymentModel(deploymentModelContent);
			try {
				Thread.sleep(4000);
			} catch (InterruptedException ex) {
				log.error(ex.getMessage());
				System.out.println(ex.getMessage());
			}

			System.out
					.println("4: Retrieve the executation file of all data collectors...");
			log.info("4: Retrieve the executation file of all data collectors...");
			File file = null;
			try {
				Response response = monitor.getDataCollectors();
				file = (File) response.getEntity();
				copyFile(file, new File(DATA_COLLECTORS_FILE_NAME));
				Thread.sleep(4000);
			} catch (InterruptedException ex) {
				log.error(ex.getMessage());
			} catch (IOException ex) {
				log.error(ex.getMessage());
				System.out.println(ex.getMessage());
			}
			Assert.assertNotEquals(file, null);

			System.out
					.println("5: Retrieve the installation file of the data collector(s) and install it");
			log.info("5: Retrieve the installation file of the data collector(s) and install it");
			String fileContent = null;
			try {
				fileContent = monitor.getDataCollectorInstallationFile(
						metricName, appID, vmID);
				TxtFileWriter.write(fileContent,
						DATA_COLLECTORS_INSTALLATION_FILE_NAME);

				log.info("Downloading hyperic-sigar-1.6.4.zip...");
				System.out.println("Downloading hyperic-sigar-1.6.4.zip...");
				download(SEACLOUDS_FOLDER + "/hyperic-sigar-1.6.4.zip",
						"https://magelan.googlecode.com/files/hyperic-sigar-1.6.4.zip");

				log.info("Unzipping hyperic-sigar-1.6.4.zip...");
				System.out.println("Unzipping hyperic-sigar-1.6.4.zip...");
				unzip(SEACLOUDS_FOLDER + "/hyperic-sigar-1.6.4.zip",
						SEACLOUDS_FOLDER);

				WindowsBatchFileExecution
						.execute(DATA_COLLECTORS_INSTALLATION_FILE_NAME);

				Thread.sleep(5000);

				Runtime.getRuntime().exec("taskkill /im cmd.exe");
			} catch (InterruptedException ex) {
				log.error(ex.getMessage());
				System.out.println(ex.getMessage());
			} catch (IOException ex) {
				log.error(ex.getMessage());
				System.out.println(ex.getMessage());
			}
			Assert.assertNotEquals(fileContent, null);

			log.info("Delete resources...");
			System.out.println("Delete resources...");

			try {
				Thread.sleep(15000);
			} catch (Exception ex) {
				log.error(ex.getMessage());
			}

			File folder = new File("lib");
			delete(folder, folder, null);

			folder = new File("SeaClouds");
			delete(folder, folder, null);

			file = new File(DATA_COLLECTORS_FILE_NAME);
			file.delete();

			file = new File(DATA_COLLECTORS_INSTALLATION_FILE_NAME);
			file.delete();

			folder = new File("WebContent/WEB-INF/lib");
			delete(folder, folder, null);
			String fileName = "monitor-core-0.1.0-SNAPSHOT.jar";
			delete(folder, folder, fileName);

			file = new File("SeaClouds");
			while (file.exists())
				delete(file, null, null);
		}
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

	private void unzip(String zipFilePath, String destDirectory)
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

	private void extractFile(ZipInputStream zipIn, String filePath)
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

	public static void copyFile(File sourceFile, File destFile)
			throws IOException {

		if (!destFile.exists())
			destFile.createNewFile();

		FileChannel source = null, destination = null;

		try {

			source = new FileInputStream(sourceFile).getChannel();

			destination = new FileOutputStream(destFile).getChannel();

			destination.transferFrom(source, 0, source.size());
		}

		finally {

			if (source != null)
				source.close();

			if (destination != null)
				destination.close();
		}
	}

	static private void delete(File folder, File exception1, String exception2) {

		if (folder.exists()) {

			File[] files = folder.listFiles();

			for (int i = 0; files != null && i < files.length; i++) {

				if (files[i].isDirectory())
					delete(files[i], exception1, exception2);

				else
					files[i].delete();
			}

			if (folder != exception1 && !folder.getName().equals(exception2))
				folder.delete();
		}
	}
}
