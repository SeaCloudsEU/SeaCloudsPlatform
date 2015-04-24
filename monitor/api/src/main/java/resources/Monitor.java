package resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

import com.google.common.io.ByteStreams;

import core.ControllerImpl;
import core.TxtFileReader;
import core.TxtFileWriter;
import core.WindowsBatchFileExecution;
import core.RESTCalls.RESTGet;
import core.RESTCalls.RESTPost;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */

@Path("/monitor")
public class Monitor {

	//Constants.
	private static final String INITIALIZATION_CONFIGURATION_FILE = "./resources/initialization.properties";
	private static final String SEACLOUDS_FOLDER = "./SeaClouds";
	private static final String INITIALIZATION_CONFIGURATION_FILE_ON_SERVER = SEACLOUDS_FOLDER + "/storedInitialization.properties";

	private static final String LOCAL_FUSEKI = "/core/lib/jena-fuseki-1.1.1";
	private static final String SERVER_FUSEKI = "/jena-fuseki-1.1.1";

	private static final String LOCAL_RESOURCES = "/core/resources";
	private static final String SERVER_RESOURCES = "/resources";

	private static final String LOCAL_MONITORING_MANAGER = "/core/lib/";
	private static final String SERVER_MONITORING_MANAGER = "/";

	private static final String MONITORING_RULES_FILE = "/core/resources/monitoringRules";

	private static final String DEPLOYMENT_MODEL_FILE = "/core/resources/deploymentModel.json";

	private static final String LOCAL_DATA_COLLECTORS = "/core/lib/";
	private static final String DATA_COLLECTORS_FILE_NAME = "data-collector-1.3-SNAPSHOT.jar";
	private static final String DATA_COLLECTORS_INSTALLATION_FILE_NAME = "dataCollectorsInstallation.bat";


	@POST
	@Produces("text/plain")
	@Path("/initialize")//http://localhost:8080/monitor-api/rest/initialize xxxxxx
	public String initialize( String configurationFileContent ) {

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
     	String SLAServiceURIRulesReady = properties.getProperty( "SLAServiceURIRulesReady" ); //System.out.println( "\nSLAServiceURI = " + SLAServiceURI );
     	String SLAServiceURIReplanning = properties.getProperty( "SLAServiceURIReplanning" ); 
		String DashboardURIRulesReady = properties.getProperty( "DashboardURIRulesReady" );
		String DashboardURIReplanning = properties.getProperty( "DashboardURIReplanning" );
		String PlannerURIRulesReady = properties.getProperty( "PlannerURIRulesReady" );
		String PlannerURIReplanning = properties.getProperty( "PlannerURIReplanning" );


		Controller controller = new ControllerImpl();

		String msg = controller.initializeMonitor( IPofKB, IPofDA, IPofMM, portOfKB, portOfDA, portOfMM, privatePortOfMM, SLAServiceURIRulesReady, SLAServiceURIReplanning, DashboardURIRulesReady, DashboardURIReplanning, PlannerURIRulesReady, PlannerURIReplanning );


		return "[INFO] Monitor REST Service: Initializing monitor...\n\n" + configurationFileContent + "\n" + msg;
	}

	@GET
	@Produces("text/plain")
	@Path("/clear")//http://localhost:8080/monitor-api/rest/clear
	public String clear() {

		TxtFileWriter.write( "", INITIALIZATION_CONFIGURATION_FILE_ON_SERVER );


		return "[INFO] Monitor controller: The initialization data have been cleared.";
	}


	@GET
	@Produces("text/plain")
	@Path("/initiate")//http://localhost:8080/monitor-api/rest/initiate
	public String initiate() {

		Controller controller = new ControllerImpl();

		String msg = controller.initiateMonitor();


		return "[INFO] Monitor REST Service: Initiating monitor...\n" + msg;
	}

	@POST
	@Produces("text/plain")
	@Path("/initiate")//http://localhost:8080/monitor-api/rest/initiate/xxxxxx
	public String initiate( String serverSourcesPath ) {

		//serverSourcesPath = serverSourcesPath.replace( '+', '/' ); //System.out.println( "\nserverSourcesPath = " + serverSourcesPath );

		copySourceFiles( serverSourcesPath );


		Controller controller = new ControllerImpl();

		String msg = controller.initiateMonitor();


		return "[INFO] Monitor REST Service: Initiating monitor...\n" + msg;
	}


	@POST
	@Produces("text/plain")
	@Path("/installMonitoringRules")//http://localhost:8080/monitor-api/rest/installMonitoringRules xxxxxx
	public String installMonitoringRules( String monitoringRules ) {

		//System.out.println( "\nmonitoringRules = " + monitoringRules );

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

				String SLAServiceURIRulesReady = properties.getProperty( "SLAServiceURIRulesReady" ); //System.out.println( "\nSLAServiceURIRulesReady = " + SLAServiceURIRulesReady );
				String DashboardURIRulesReady = properties.getProperty( "DashboardURIRulesReady" );
				String PlannerURIRulesReady = properties.getProperty( "PlannerURIRulesReady" );

				if( SLAServiceURIRulesReady == null ) msg1 = "[ERROR] Monitor REST Service: there is no calling information about the SLA Service.";

				else 	msg1 = RESTPost.httpPost( SLAServiceURIRulesReady, monitoringRules, "xml" );

				if( msg1 != null ) msg += msg1; //System.out.println( "\nmsg1 = " + msg1 ); System.out.println( "\nmsg = " + msg );


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
	@Path("/installDeploymentModel")//http://localhost:8080/monitor-api/rest/installDeploymentModel xxxxxx
	public String installDeploymentModel( String deploymentModel ) {

		//System.out.println( "\ndeploymentModel = " + deploymentModel );

		Controller controller = new ControllerImpl();

		String msg = controller.installDeploymentModel( deploymentModel );

		if( msg == null ) msg = "";


		return "[INFO] Monitor REST Service: Installing deployment model...\n\n" + deploymentModel + "\n" + msg;
	}


	@GET
	@Produces("text/plain")
	@Path("/getAllMetrics")//http://localhost:8080/monitor-api/rest/getAllMetrics
	public String getAllMetrics() {

		Controller controller = new ControllerImpl();

		String[] metrics = controller.getAllMetrics();


		String response = "";

		for( int i = 0; metrics != null && i < metrics.length; ++i ) response += metrics[ i ] + "\n";


		return response;
	}

	@GET
	@Produces("text/plain")
	@Path("/getRunningMetrics")//http://localhost:8080/monitor-api/rest/getRunningMetrics
	public String getRunningMetrics() {

		Controller controller = new ControllerImpl();

		String[] metrics = controller.getRunningMetrics();


		String response = "";

		for( int i = 0; metrics != null && i < metrics.length; ++i ) response += metrics[ i ] + "\n";


		return response;
	}


	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/getDataCollectors")//http://localhost:8080/monitor-api/rest/getDataCollector/metricName
	public Response getDataCollectors() {//It returns the content of the execution file of all data collectors.

		//System.out.println( "\nmetricName = " + metricName );


		Controller controller = new ControllerImpl();

		File file = controller.getDataCollectors(); 
		
		
		ResponseBuilder response = Response.ok( (Object) file );

	    response.header( "Content-Disposition", "attachment; filename=" + DATA_COLLECTORS_FILE_NAME );


	    return response.build();
	}


	@POST
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/getDataCollector/{metricName}")//http://localhost:8080/monitor-api/rest/getDataCollector/metricName
	public Response getDataCollector( @PathParam("metricName") String metricName ) {//It returns the content of the execution file of a data collector.

		//System.out.println( "\nmetricName = " + metricName );


		Controller controller = new ControllerImpl();

		File file = controller.getDataCollector( metricName ); 


		ResponseBuilder response = Response.ok( (Object) file );

	    response.header( "Content-Disposition", "attachment; filename=" + DATA_COLLECTORS_FILE_NAME );


	    return response.build();
	}

	@POST
	@Produces("text/plain")
	@Path("/getDataCollectorInstallationFile/{metricName}")//http://localhost:8080/monitor-api/rest/getDataCollectorInstallationFile/metricName
	public String getDataCollectorInstallationFile( @PathParam("metricName") String metricName ) {//It returns the content of the installation file of a data collector as a String.

		//System.out.println( "\nmetricName = " + metricName );


		Controller controller = new ControllerImpl();

		return controller.getDataCollectorInstallationFile( metricName ); 
	}


	@POST
	@Produces("text/plain")
	@Path("/uninstallMonitoringRule/{id}")//http://localhost:8080/monitor-api/rest/uninstallMonitoringRule/id
	public String uninstallMonitoringRule( @PathParam("id") String id ) {

		//System.out.println( "\nid= " + id );


		Controller controller = new ControllerImpl();

		String msg = controller.uninstallMonitoringRule( id );


		return "[INFO] Monitor REST Service: Uninstalling monitoring rule...\n\n" + msg;
	}


	@POST
	@Produces("text/plain")
	@Path("/addObserver/{metricName}/{portOfObserver}")//http://localhost:8080/monitor-api/rest/addObserver/metricName/portOfObserver callbackURL
	public String addObserver( @PathParam("metricName") String metricName, @PathParam("portOfObserver") String portOfObserver, String callbackURL ) {

		//System.out.println( "metricName= " + metricName );
		//System.out.println( "portOfObserver= " + portOfObserver );
		//System.out.println( "callbackURL= " + callbackURL );


		Controller controller = new ControllerImpl();

		String msg = controller.addObserver( metricName, portOfObserver, callbackURL );


		return "[INFO] Monitor REST Service: Adding observer...\n\n" + msg;
	}


	@POST
	@Produces("text/plain")
	@Path("/sendReplanningEvent")//http://localhost:8080/monitor-api/rest/sendReplanningEvent xxxxxx
	public String sendReplanningEvent( String replanningEvent ) {

		//System.out.println( "\nreplanningEvent = " + replanningEvent );


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

				String SLAServiceURIReplanning = properties.getProperty( "SLAServiceURIReplanning" ); //System.out.println( "\nSLAServiceURIRulesReady = " + SLAServiceURIRulesReady );
				String DashboardURIReplanning = properties.getProperty( "DashboardURIReplanning" );
				String PlannerURIReplanning = properties.getProperty( "PlannerURIReplanning" );

				if( SLAServiceURIReplanning == null ) msg1 = "[ERROR] Monitor REST Service: there is no calling information about the SLA Service.";

				else 	msg1 = RESTPost.httpPost( SLAServiceURIReplanning, replanningEvent, "xml" );

				if( msg1 != null ) msg += msg1; //System.out.println( "\nmsg1 = " + msg1 ); System.out.println( "\nmsg = " + msg );


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


	public static void main( String[] args ) {

		String metricName = "CPUUtilization", metricNameForObserver = "FrontendCPUUtilization1";

		boolean initiated = false, firstTime = true;


     	try {

     		String msg = null;

			File currentDir = new File( System.getProperty( "user.dir" ) );

			String parentDir = currentDir.getParent(); //System.out.println( "\nparentDir = " + parentDir );


     		System.out.println( "\nSeaClouds Monitoring Platform Menu:\n" );

     		while( true ){

     			if( firstTime || ! initiated ) System.out.println( "1. Initialize and initiate the monitoring platform" );
     			System.out.println( "2: Install monitoring rules (in XML format)" );
     			System.out.println( "3: Install a deployment model (in JSON format)" );
     			System.out.println( "4: Get all monitoring metrics" );
     			System.out.println( "5: Get running monitoring metrics" );
     			System.out.println( "6: Retrieve the executation file of all data collectors" );
     			System.out.println( "7: Retrieve the executation file of a data collector (providing the name of a metric)" );
     			System.out.println( "8: Retrieve the installation file of the data collector(s) and install it" );
     			System.out.println( "9: Uninstall monitoring rule" );
     			System.out.println( "10: Add observer" );
     			System.out.println( "11: Send replanning event" );
     			System.out.println( "12: Exit" );
     			System.out.print( "\nChoice = " );

     			BufferedReader input = new BufferedReader( new InputStreamReader( System.in ) );

    			String answer = input.readLine();

    			System.out.println();


     			int answerInt = 0;

     			if( answer == null || ( answer != null && answer.equals( "" ) ) ) answerInt = 0;

     			else answerInt = Integer.parseInt( answer );


     			if( answerInt == 1 && ! initiated ){

     				File initializationFile = new File( INITIALIZATION_CONFIGURATION_FILE );

     				if( ! initializationFile.exists() ){

     					msg = "[INFO] Monitor REST Service Main: Initialization file does not exist.\n"; System.out.println( msg );

     					initiated = false;
     				}

     				else{

     					msg = RESTGet.httpGet( "http://localhost:8080/monitor-api/rest/monitor/clear" ); System.out.println( msg );

     					String configurationFileContent = TxtFileReader.read( initializationFile ); //System.out.println( "\n\n[INFO] Monitor REST Service Client: \nconfigurationFileContent = " + configurationFileContent );

     					msg = RESTPost.httpPost( "http://localhost:8080/monitor-api/rest/monitor/initialize", configurationFileContent, "text/plain" ); System.out.println( msg );


     					//String parentDirTransformed = parentDir.replace( '\\', '/' ).replace( '/', '+' ); //System.out.println( "\nparentDirTransformed = " + parentDirTransformed );

     					msg = RESTPost.httpPost( "http://localhost:8080/monitor-api/rest/monitor/initiate", parentDir, "text/plain" );

     					Thread.sleep( 15000 );

     					initiated = true;

     					System.out.println( msg );
     				}
     			}

     			else if( firstTime ){

     				System.out.println( "[ERROR] Monitor REST Service Main: You should firstly initialize and initiate the monitoring platform.\n" );

     				System.out.println( "Have you already initialized and initiated the monitoring platform (0/1)?" ); System.out.print( "\nAnswer = " );

         			input = new BufferedReader( new InputStreamReader( System.in ) );

        			answer = input.readLine();

        			System.out.println();


         			int internalAnswerInt = 0;

         			if( answer == null || ( answer != null && answer.equals( "" ) ) ) internalAnswerInt = 0;

         			else internalAnswerInt = Integer.parseInt( answer );


         			if( internalAnswerInt == 1 ) initiated = true;

         			else initiated = false;
     			}

     			firstTime = false;


     			if( initiated ){

     				if( answerInt == 2 ){

     					//System.out.println( "\nProvide the ID (1-10) of the monitoring rules file." ); System.out.print( "ID = " );

     					//input = new BufferedReader( new InputStreamReader( System.in ) );

     					//answer = input.readLine();


     					answerInt = 1;

     					//if( answer == null || ( answer != null && answer.equals( "" ) ) ) answerInt = 1;

     					//else answerInt = Integer.parseInt( answer );


     					if( answerInt >=1 && answerInt <= 10){

     						File monitoringRules = new File( parentDir + MONITORING_RULES_FILE + answerInt + ".xml" ); System.out.println( "\nmonitoringRules = " + monitoringRules + "\n" );

     						String monitoringRulesContent = TxtFileReader.read( monitoringRules );

     						msg = RESTPost.httpPost( "http://localhost:8080/monitor-api/rest/monitor/installMonitoringRules", monitoringRulesContent, "xml" );

     						Thread.sleep( 5000 );

     						System.out.println( msg );
     					}
     				}

     				else if( answerInt == 3 ){

     					File deploymentModel = new File( parentDir + DEPLOYMENT_MODEL_FILE ); System.out.println( "\ndeploymentModel = " + deploymentModel + "\n" );

         				String deploymentModelContent = TxtFileReader.read( deploymentModel );

         				msg = RESTPost.httpPost( "http://localhost:8080/monitor-api/rest/monitor/installDeploymentModel", deploymentModelContent, "xml" );

         				Thread.sleep( 5000 );

         				System.out.println( msg );
     				}

     				else if( answerInt == 4 ){

     					String response = RESTGet.httpGet( "http://localhost:8080/monitor-api/rest/monitor/getAllMetrics" );

     					String[] metrics = response.split( "\n" );

     					System.out.println( "\n\tMetrics:" );

         				for( int i = 0; metrics != null && i < metrics.length; ++i ){

         					int counter = i + 1;

         					System.out.println( "\t" + counter + ": " + metrics[ i ] );
         				}

         				System.out.println();
     				}

     				else if( answerInt == 5 ){

     					String response = RESTGet.httpGet( "http://localhost:8080/monitor-api/rest/monitor/getRunningMetrics" );

     					String[] metrics = response.split( "\n" );

     					System.out.println( "\n\tMetrics:" );

         				for( int i = 0; metrics != null && i < metrics.length; ++i ){

         					int counter = i + 1;

         					System.out.println( "\t" + counter + ": " + metrics[ i ] );
         				}

         				System.out.println();
     				}

     				else if( answerInt == 6 ){

     					InputStream data = RESTGet.httpGetResponse( "http://localhost:8080/monitor-api/rest/monitor/getDataCollectors" );

             			OutputStream output = new FileOutputStream( DATA_COLLECTORS_FILE_NAME );

         			    ByteStreams.copy( data, output );

         			   Thread.sleep( 5000 );

             			System.out.println( "[INFO] Monitor REST Service Main: The executation file of data collector has been retrieved.\n" ); //System.out.println( "\ndataCollector = " + fileContent + "\n" );
     				}

     				else if( answerInt == 7 ){

     					String response = RESTGet.httpGet( "http://localhost:8080/monitor-api/rest/monitor/getAllMetrics" );

     					String[] metrics = response.split( "\n" );

     					System.out.println( "\n\tMetrics:" );

         				for( int i = 0; metrics != null && i < metrics.length; ++i ){

         					int counter = i + 1;

         					System.out.println( "\t" + counter + ": " + metrics[ i ] );
         				}

         				System.out.print( "\n\tMetric name = " );

             			input = new BufferedReader( new InputStreamReader( System.in ) );

             			answer = input.readLine();

            			System.out.println();


            			if( answer != null && ! answer.equals( "" ) ) metricName = metrics[ Integer.parseInt( answer ) - 1 ];

             			System.out.println( "[INFO] Monitor REST Service Main: Retrieving the executation file of the data collector for the metric '" + metricName + "'...\n" );


     					InputStream data = RESTPost.httpPostResponse( "http://localhost:8080/monitor-api/rest/monitor/getDataCollector/" + metricName );

             			OutputStream output = new FileOutputStream( DATA_COLLECTORS_FILE_NAME );

         			    ByteStreams.copy( data, output );

             			//TxtFileWriter.write( fileContent, DATA_COLLECTORS_FILE_NAME );

         			   Thread.sleep( 5000 );

             			System.out.println( "[INFO] Monitor REST Service Main: The executation file of the data collector for the metric '" + metricName + "' has been retrieved.\n" ); //System.out.println( "\ndataCollector = " + fileContent + "\n" );
     				}

     				else if( answerInt == 8 ){

             			String fileContent = RESTPost.httpPost( "http://localhost:8080/monitor-api/rest/monitor/getDataCollectorInstallationFile/" + metricName ); System.out.println( "\ninstallationFileOfDataCollector = " + fileContent + "\n" );

             			TxtFileWriter.write( fileContent, DATA_COLLECTORS_INSTALLATION_FILE_NAME );

             			WindowsBatchFileExecution.execute( DATA_COLLECTORS_INSTALLATION_FILE_NAME );

    					Thread.sleep( 5000 );

    					System.out.println( "[INFO] Monitor REST Service Main: The installation file of data collector has been retrieved and executed.\n" );
     				}

     				else if( answerInt == 9 ){

     					System.out.println( "Provide the id of the monitoring rule" ); System.out.print( "\nID = " );

             			input = new BufferedReader( new InputStreamReader( System.in ) );

            			answer = input.readLine();

            			System.out.println();


     					msg = RESTPost.httpPost( "http://localhost:8080/monitor-api/rest/monitor/uninstallMonitoringRule/" + answer );

     					Thread.sleep( 5000 );

     					System.out.println( msg );
     				}

     				else if( answerInt == 10){

     					System.out.println( "Provide the metric name" ); System.out.print( "\nMetric name = " );

             			input = new BufferedReader( new InputStreamReader( System.in ) );

            			answer = input.readLine();

            			if( answer != null && ! answer.equals( "" ) ) metricNameForObserver = answer;

            			System.out.println();


            			String port = "8123";

            			System.out.println( "Provide the port of the observer (default = 8123)" ); System.out.print( "\nPort = " );

             			input = new BufferedReader( new InputStreamReader( System.in ) );

            			answer = input.readLine();

            			if( answer != null && ! answer.equals( "" ) ) port = answer;

            			System.out.println();


            			String callbackURL = "http://localhost:8123";

            			System.out.println( "Provide the callback IP (default = localhost)" ); System.out.print( "\nURL = " );

             			input = new BufferedReader( new InputStreamReader( System.in ) );

            			answer = input.readLine();

            			if( answer != null && ! answer.equals( "" ) ) callbackURL = "http://" + answer + ":" + port;

            			System.out.println();


     					msg = RESTPost.httpPost( "http://localhost:8080/monitor-api/rest/monitor/addObserver/" + metricNameForObserver + "/" + port, callbackURL );

     					Thread.sleep( 5000 );

     					System.out.println( msg );
     				}

     				else if( answerInt == 11 ){

     					String replanningEvent = "Dummy event";

     					msg = RESTPost.httpPost( "http://localhost:8080/monitor-api/rest/monitor/sendReplanningEvent", replanningEvent, "xml" );

     					Thread.sleep( 5000 );

     					System.out.println( msg );
     				}

     				else if( answerInt == 12 ){

     					System.out.println( "[INFO] Monitor REST Service Main: The client of the monitoring platform is terminated.\n" );

     					System.exit( 0 );
     				}
     			}
     		}
		}

		catch( Exception ex ){

			ex.printStackTrace();
		}


        //Get plain text.
        //System.out.println(service.path("rest").path("monitor/initiate").accept(MediaType.TEXT_PLAIN).get(String.class));

        // The HTML  
        //System.out.println(service.path("rest").path("testMessage").accept(MediaType.TEXT_HTML).get(String.class));  
    }

	private static Properties read( String initilizationFile ){

		Properties properties = null;


		File file = new File( initilizationFile );

		if( file.exists() ){ //System.out.println( "\n\nExists\n\n" );

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

	private static void copySourceFiles( String serverSourcesPath ){

		TxtFileWriter.copyFolder( new File( serverSourcesPath + LOCAL_RESOURCES ), new File( SEACLOUDS_FOLDER + SERVER_RESOURCES ) );

		//TxtFileWriter.copyFolder( new File( serverSourcesPath + LOCAL_CSPARQL ), new File( SEACLOUDS_FOLDER + SERVER_CSPARQL ) );

		TxtFileWriter.copyFolder( new File( serverSourcesPath + LOCAL_FUSEKI ), new File( SEACLOUDS_FOLDER + SERVER_FUSEKI ) );

		TxtFileWriter.copyFolder( new File( serverSourcesPath + LOCAL_MONITORING_MANAGER ), new File( SEACLOUDS_FOLDER + SERVER_MONITORING_MANAGER ) );

		TxtFileWriter.copyFolder( new File( serverSourcesPath + LOCAL_DATA_COLLECTORS ), new File( SEACLOUDS_FOLDER ) );

		//TxtFileWriter.copyFolder( new File( serverSourcesPath + LOCAL_SIGAR ), new File( SEACLOUDS_FOLDER + SERVER_SIGAR ) );

		//TxtFileWriter.copyFolder( new File( serverSourcesPath + LOCAL_METRICS_OBSERVER ), new File( SEACLOUDS_FOLDER + SERVER_METRICS_OBSERVER ) );
	}

    /*private static URI getBaseURI(){

		return UriBuilder.fromUri("http://localhost:8080/monitor-api/rest/").build();  
	}*/

	//Convert InputStream to Arary of String.
	/*private static String[] getArrayOfStringFromInputStream( InputStream inputStream ){

			BufferedReader br = null;

			List<String> lines = new ArrayList<String>();

			String line;


			try{

				br = new BufferedReader( new InputStreamReader( inputStream ) );

				while( (line = br.readLine() ) != null ) lines.add( line );


			}

			catch ( IOException ex ){

				ex.printStackTrace();
			}


			return lines.toArray( new String[ lines.size() ]);
	}*/
}
