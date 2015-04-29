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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import api.controller.Controller;
import core.ControllerImpl;
import core.TxtFileWriter;
import core.RESTCalls.RESTPost;


@Path("/monitor")
public class Monitor {

	private static final String SEACLOUDS_FOLDER = "SeaClouds";
	private static final String INITIALIZATION_CONFIGURATION_FILE_ON_SERVER = SEACLOUDS_FOLDER + "/storedInitialization.properties";

	private static final String LOCAL_FUSEKI = "/lib/jena-fuseki-1.1.1";
	private static final String SERVER_FUSEKI = "/jena-fuseki-1.1.1";

	private static final String LOCAL_CSPARQL = "/lib/rsp-services-csparql-0.4.6.2-modaclouds";
	private static final String SERVER_CSPARQL = "/rsp-services-csparql-0.4.6.2-modaclouds";

	private static final String LOCAL_RESOURCES = "/resources";

	private static final String LOCAL_MONITORING_MANAGER = "/core/lib/";
	private static final String SERVER_MONITORING_MANAGER = "/";

	private static final String LOCAL_DATA_COLLECTORS = "/core/lib/";
	private static final String DATA_COLLECTORS_FILE_NAME = "data-collector-1.3-SNAPSHOT.jar";


	@POST
	@Produces("text/plain")
	@Path("/initialize")
	public String initialize( String configurationFileContent ) {

		delete( new File( SEACLOUDS_FOLDER ) );

		new File( SEACLOUDS_FOLDER ).mkdirs();


		TxtFileWriter.write( configurationFileContent, INITIALIZATION_CONFIGURATION_FILE_ON_SERVER );

		Properties properties = read( INITIALIZATION_CONFIGURATION_FILE_ON_SERVER );

     	String IPofKB = properties.getProperty( "IPofKB" );
     	String IPofDA= properties.getProperty( "IPofDA" );
     	String IPofMM= properties.getProperty( "IPofMM" );
     	String portOfKB= properties.getProperty( "portOfKB" );
     	String portOfDA= properties.getProperty( "portOfDA" );
     	String portOfMM= properties.getProperty( "portOfMM" );
     	String privatePortOfMM= properties.getProperty( "privatePortOfMM" );
     	String SLAServiceURIRulesReady = properties.getProperty( "SLAServiceURIRulesReady" );
     	String SLAServiceURIReplanning = properties.getProperty( "SLAServiceURIReplanning" ); 
		String DashboardURIRulesReady = properties.getProperty( "DashboardURIRulesReady" );
		String DashboardURIReplanning = properties.getProperty( "DashboardURIReplanning" );
		String PlannerURIRulesReady = properties.getProperty( "PlannerURIRulesReady" );
		String PlannerURIReplanning = properties.getProperty( "PlannerURIReplanning" );


		Controller controller = new ControllerImpl();

		String msg = controller.initializeMonitor( IPofKB, IPofDA, IPofMM, portOfKB, portOfDA, portOfMM, privatePortOfMM, SLAServiceURIRulesReady, SLAServiceURIReplanning, DashboardURIRulesReady, DashboardURIReplanning, PlannerURIRulesReady, PlannerURIReplanning );


		return "[INFO] Monitor REST Service: Initializing monitor...\n\n" + msg;
	}

	@GET
	@Produces("text/plain")
	@Path("/clear")
	public String clear() {

		TxtFileWriter.write( "", INITIALIZATION_CONFIGURATION_FILE_ON_SERVER );


		return "[INFO] Monitor controller: The initialization data have been cleared.";
	}


	@GET
	@Produces("text/plain")
	@Path("/initiate")
	public String initiate() {

		Controller controller = new ControllerImpl();

		String msg = controller.initiateMonitor();


		return "[INFO] Monitor REST Service: Initiating monitor...\n" + msg;
	}

	@POST
	@Produces("text/plain")
	@Path("/initiate")
	public String initiate( String serverSourcesPath ) {

		String msg = copySourceFiles( serverSourcesPath );


		if( msg == null ){

			Controller controller = new ControllerImpl();

			controller.initiateMonitor();
		}


		return msg; 
	}


	@POST
	@Produces("text/plain")
	@Path("/installMonitoringRules")
	public String installMonitoringRules( String monitoringRules ) {

		Controller controller = new ControllerImpl();
		String msg1 = controller.installMonitoringRules( monitoringRules );
		if( msg1 == null ) msg1 = "";

		String msg2 = notifyForMonitoringRules( monitoringRules );
		if( msg2 == null ) msg2 = "";


		return "[INFO] Monitor REST Service: Installing monitoring rules...\n\n" + monitoringRules + "\n" + msg1 + "\n" + msg2;
	}

	private String notifyForMonitoringRules( String monitoringRules ) {

		String msg = "", msg1 = "", msg2 = "", msg3 = "";


		Properties properties = read( INITIALIZATION_CONFIGURATION_FILE_ON_SERVER );

		if( properties == null ) msg = "[ERROR] Monitor REST Service: there is no calling information about the external SeaClouds components.";

		else{

			try{

				String SLAServiceURIRulesReady = properties.getProperty( "SLAServiceURIRulesReady" );
				String DashboardURIRulesReady = properties.getProperty( "DashboardURIRulesReady" );
				String PlannerURIRulesReady = properties.getProperty( "PlannerURIRulesReady" );

				if( SLAServiceURIRulesReady == null ) msg1 = "[ERROR] Monitor REST Service: there is no calling information about the SLA Service.";

				else 	msg1 = RESTPost.httpPost( SLAServiceURIRulesReady, monitoringRules, "xml" );

				if( msg1 != null ) msg += msg1;


				if( DashboardURIRulesReady == null ) msg2 = "[ERROR] Monitor REST Service: there is no calling information about the Dashboard.";

				else 	msg2 = RESTPost.httpPost( DashboardURIRulesReady, monitoringRules, "xml" );

				if( msg2 != null ) msg += "\n" + msg2;


				if( PlannerURIRulesReady == null ) msg3 = "[ERROR] Monitor REST Service: there is no calling information about the Planner.";

				else 	msg3 = RESTPost.httpPost( PlannerURIRulesReady, monitoringRules, "xml" );

				if( msg3 != null ) msg += "\n" + msg3;
			}

			catch( Exception ex ){

				ex.printStackTrace();
			}
		}


		return msg;
	}


	@POST
	@Produces("text/plain")
	@Path("/installDeploymentModel")
	public String installDeploymentModel( String deploymentModel ) {

		Controller controller = new ControllerImpl();

		String msg = controller.installDeploymentModel( deploymentModel );

		if( msg == null ) msg = "";


		return "[INFO] Monitor REST Service: Installing deployment model...\n\n" + deploymentModel + "\n" + msg;
	}


	@GET
	@Produces("text/plain")
	@Path("/getAllMetrics")
	public String getAllMetrics() {

		Controller controller = new ControllerImpl();

		String[] metrics = controller.getAllMetrics();


		String response = "";

		for( int i = 0; metrics != null && i < metrics.length; ++i ) response += metrics[ i ] + "\n";


		return response;
	}

	@GET
	@Produces("text/plain")
	@Path("/getRunningMetrics")
	public String getRunningMetrics() {

		Controller controller = new ControllerImpl();

		String[] metrics = controller.getRunningMetrics();


		String response = "";

		for( int i = 0; metrics != null && i < metrics.length; ++i ) response += metrics[ i ] + "\n";


		return response;
	}


	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/getDataCollectors")
	public Response getDataCollectors() {

		Controller controller = new ControllerImpl();

		File file = controller.getDataCollectors(); 
		
		
		ResponseBuilder response = Response.ok( (Object) file );

	    response.header( "Content-Disposition", "attachment; filename=" + DATA_COLLECTORS_FILE_NAME );


	    return response.build();
	}


	@POST
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/getDataCollector/{metricName}")
	public Response getDataCollector( @PathParam("metricName") String metricName ) {

		Controller controller = new ControllerImpl();

		File file = controller.getDataCollector( metricName ); 


		ResponseBuilder response = Response.ok( (Object) file );

	    response.header( "Content-Disposition", "attachment; filename=" + DATA_COLLECTORS_FILE_NAME );


	    return response.build();
	}

	@POST
	@Produces("text/plain")
	@Path("/getDataCollectorInstallationFile/{metricName}")
	public String getDataCollectorInstallationFile( @PathParam("metricName") String metricName ) {

		Controller controller = new ControllerImpl();


		return controller.getDataCollectorInstallationFile( metricName ); 
	}


	@POST
	@Produces("text/plain")
	@Path("/uninstallMonitoringRule/{id}")
	public String uninstallMonitoringRule( @PathParam("id") String id ) {

		Controller controller = new ControllerImpl();

		String msg = controller.uninstallMonitoringRule( id );


		return "[INFO] Monitor REST Service: Uninstalling monitoring rule...\n\n" + msg;
	}


	@POST
	@Produces("text/plain")
	@Path("/addObserver/{metricName}/{portOfObserver}")
	public String addObserver( @PathParam("metricName") String metricName, @PathParam("portOfObserver") String portOfObserver, String callbackURL ) {

		Controller controller = new ControllerImpl();

		String msg = controller.addObserver( metricName, portOfObserver, callbackURL );


		return "[INFO] Monitor REST Service: Adding observer...\n\n" + msg;
	}


	@POST
	@Produces("text/plain")
	@Path("/sendReplanningEvent")
	public String sendReplanningEvent( String replanningEvent ) {

		String msg = notifyForReplanningEvent( replanningEvent );

		if( msg == null ) msg = "";


		return "[INFO] Monitor REST Service: Sending replanning event...\n\n" + replanningEvent + "\n" + msg;
	}

	private String notifyForReplanningEvent( String replanningEvent ) {

		String msg = "", msg1 = "", msg2 = "", msg3 = "";


		Properties properties = read( INITIALIZATION_CONFIGURATION_FILE_ON_SERVER );

		if( properties == null ) msg = "[ERROR] Monitor REST Service: there is no calling information about the external SeaClouds components.";

		else{

			try{

				String SLAServiceURIReplanning = properties.getProperty( "SLAServiceURIReplanning" );
				String DashboardURIReplanning = properties.getProperty( "DashboardURIReplanning" );
				String PlannerURIReplanning = properties.getProperty( "PlannerURIReplanning" );

				if( SLAServiceURIReplanning == null ) msg1 = "[ERROR] Monitor REST Service: there is no calling information about the SLA Service.";

				else 	msg1 = RESTPost.httpPost( SLAServiceURIReplanning, replanningEvent, "xml" );

				if( msg1 != null ) msg += msg1;


				if( DashboardURIReplanning == null ) msg2 = "[ERROR] Monitor REST Service: there is no calling information about the Dashboard.";

				else 	msg2 = RESTPost.httpPost( DashboardURIReplanning, replanningEvent, "xml" );

				if( msg2 != null ) msg += "\n" + msg2;


				if( PlannerURIReplanning == null ) msg3 = "[ERROR] Monitor REST Service: there is no calling information about the Planner.";

				else 	msg3 = RESTPost.httpPost( PlannerURIReplanning, replanningEvent, "xml" );

				if( msg3 != null ) msg += "\n" + msg3;
			}

			catch( Exception ex ){

				ex.printStackTrace();
			}
		}


		return msg;
	}

	private static Properties read( String initilizationFile ){

		Properties properties = null;


		File file = new File( initilizationFile );

		if( file.exists() ){

			properties = new Properties();

			try{

				properties.load( new FileInputStream( initilizationFile ) );
			}
 
			catch( IOException ex ){

				ex.printStackTrace();

 
				properties = null;
			}
		}


 		return properties;
	}

	private static void delete( File folder ){

	    if( folder.exists() ){

	      File[] files = folder.listFiles();

	      for( int i = 0; files != null && i < files.length; i++ ){

	    	  if( files[ i ].isDirectory() ) delete( files[ i ] );

	    	  else files[ i ].delete();
	      }

	      folder.delete(); 
	    }
	}

	private static String copySourceFiles( String serverSourcesPath ){

		String msg = null;


		TxtFileWriter.copyFolder( new File( serverSourcesPath + "/api/" + LOCAL_RESOURCES ), new File( SEACLOUDS_FOLDER + LOCAL_RESOURCES ) );


		if( ! new File( serverSourcesPath + "/api/" + LOCAL_FUSEKI ).exists() ) msg = "\n\n[ERROR] Monitor REST Service: You should download, unzip, and copy the folder 'jena-fuseki-1.1.1' inside the folder 'lib'\n(please download it from the URL: http://archive.apache.org/dist/jena/binaries/jena-fuseki-1.1.1-distribution.zip).\n\n";

		else TxtFileWriter.copyFolder( new File( serverSourcesPath + "/api/" + LOCAL_FUSEKI ), new File( SEACLOUDS_FOLDER + SERVER_FUSEKI ) );


		if( ! new File( serverSourcesPath + "/api/" + LOCAL_CSPARQL ).exists() ) msg = "\n\n[ERROR] Monitor REST Service: You should download, unzip, and copy the folder 'rsp-services-csparql-0.4.6.2-modaclouds' inside the folder 'lib'\n(please download it from the URL: http://www.cs.uoi.gr/~dathanas/rsp-services-csparql-0.4.6.2-modaclouds-distribution.zip).\n\n";

		else TxtFileWriter.copyFolder( new File( serverSourcesPath + "/api/" + LOCAL_CSPARQL ), new File( SEACLOUDS_FOLDER + SERVER_CSPARQL ) );


		TxtFileWriter.copyFolder( new File( serverSourcesPath + LOCAL_MONITORING_MANAGER ), new File( SEACLOUDS_FOLDER + SERVER_MONITORING_MANAGER ) );

		TxtFileWriter.copyFolder( new File( serverSourcesPath + LOCAL_DATA_COLLECTORS ), new File( SEACLOUDS_FOLDER ) );


		return msg;
	}
}
