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

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public class ControllerImpl implements Controller {

	//Constants.
	private static final String SEACLOUDS_FOLDER = "./SeaClouds";
	private static final String INITIALIZATION_CONFIGURATION_FILE_ON_SERVER = SEACLOUDS_FOLDER + "/storedInitialization.properties";

	private static final String SERVER_DATA_COLLECTORS_PATH = "./SeaClouds/dataCollectors";
	private static final String DATA_COLLECTORS_FILE_NAME = "data-collector-1.3-SNAPSHOT.jar";


	public String initializeMonitor( String SLAServiceURIRulesReady, String SLAServiceURIReplanning, String DashboardURIRulesReady, String DashboardURIReplanning, String PlannerURIRulesReady, String PlannerURIReplanning ){

		storeInitializationData( null, null, null, null, null, null, null, SLAServiceURIRulesReady, SLAServiceURIReplanning, DashboardURIRulesReady, DashboardURIReplanning, PlannerURIRulesReady, PlannerURIReplanning );


		return "[INFO] Monitor controller: MODACLouds monitoring platform has been initialized.";


		/*try{

			storeInitializationData( initializationData );
		}

		catch( IOException ex ){

			ex.printStackTrace();
		}*/


		/*try{

			System.out.println( "\nGive the IP of the knowledge base:" );
			System.out.print( "IP = " );

			BufferedReader input = new BufferedReader( new InputStreamReader( System.in ) );
			IPofKB = input.readLine();
			if( IPofKB == null || ( IPofKB != null && IPofKB.equals( "" ) ) ) IPofKB = EndpointIP.getIP(); 

			System.out.println( "\nGive the IP of the data analyzer:" );
			System.out.print( "IP = " );

			input = new BufferedReader( new InputStreamReader( System.in ) );
			IPofDA = input.readLine();
			if( IPofDA == null || ( IPofDA != null && IPofDA.equals( "" ) ) ) IPofDA = EndpointIP.getIP();


			System.out.println( "\nGive the IP of the monitoring manager:" );
			System.out.print( "IP = " );

			input = new BufferedReader( new InputStreamReader( System.in ) );
			IPofMM = input.readLine();
			if( IPofMM == null || ( IPofMM != null && IPofMM.equals( "" ) ) ) IPofMM = EndpointIP.getIP();


			System.out.println( "\nGive the metric name:" );
			System.out.print( "Metric name = " );

			input = new BufferedReader( new InputStreamReader( System.in ) );
			metricName = input.readLine();
			if( metricName == null || ( metricName != null && metricName.equals( "" ) ) ) metricName = "CPUUtilization";
		}

		catch( Exception ex ){

			ex.printStackTrace();

			System.exit( -1 );
		}*/
	}

	public String initializeMonitor( String IPofKB, String IPofDA, String IPofMM, String portOfKB, String portOfDA, String portOfMM, String privatePortOfMM, String SLAServiceURIRulesReady, String SLAServiceURIReplanning, String DashboardURIRulesReady, String DashboardURIReplanning, String PlannerURIRulesReady, String PlannerURIReplanning ){

		storeInitializationData( IPofKB, IPofDA, IPofMM, portOfKB, portOfDA, portOfMM, privatePortOfMM, SLAServiceURIRulesReady, SLAServiceURIReplanning, DashboardURIRulesReady, DashboardURIReplanning, PlannerURIRulesReady, PlannerURIReplanning );


		return "[INFO] Monitor controller: MODACLouds monitoring platform has been initialized.";


		/*try{

			storeInitializationData( initializationData );
		}

		catch( IOException ex ){

			ex.printStackTrace();
		}*/

		/*try{

			System.out.println( "\nGive the IP of the knowledge base:" );
			System.out.print( "IP = " );

			BufferedReader input = new BufferedReader( new InputStreamReader( System.in ) );
			IPofKB = input.readLine();
			if( IPofKB == null || ( IPofKB != null && IPofKB.equals( "" ) ) ) IPofKB = EndpointIP.getIP(); 

			System.out.println( "\nGive the IP of the data analyzer:" );
			System.out.print( "IP = " );

			input = new BufferedReader( new InputStreamReader( System.in ) );
			IPofDA = input.readLine();
			if( IPofDA == null || ( IPofDA != null && IPofDA.equals( "" ) ) ) IPofDA = EndpointIP.getIP();


			System.out.println( "\nGive the IP of the monitoring manager:" );
			System.out.print( "IP = " );

			input = new BufferedReader( new InputStreamReader( System.in ) );
			IPofMM = input.readLine();
			if( IPofMM == null || ( IPofMM != null && IPofMM.equals( "" ) ) ) IPofMM = EndpointIP.getIP();


			System.out.println( "\nGive the metric name:" );
			System.out.print( "Metric name = " );

			input = new BufferedReader( new InputStreamReader( System.in ) );
			metricName = input.readLine();
			if( metricName == null || ( metricName != null && metricName.equals( "" ) ) ) metricName = "CPUUtilization";
		}

		catch( Exception ex ){

			ex.printStackTrace();

			System.exit( -1 );
		}*/
	}

	private void storeInitializationData( String IPofKB, String IPofDA, String IPofMM, String portOfKB, String portOfDA, String portOfMM, String privatePortOfMM, String SLAServiceURIRulesReady, String SLAServiceURIReplanning, String DashboardURIRulesReady, String DashboardURIReplanning, String PlannerURIRulesReady, String PlannerURIReplanning ){

		String fileContent = "";

		if( IPofKB != null ) fileContent += "IPofKB=" + IPofKB;
		if( IPofDA != null ) fileContent += "\nIPofDA=" + IPofDA;
		if( IPofMM != null ) fileContent += "\nIPofMM=" + IPofMM;
		if( portOfKB != null ) fileContent += "\nportOfKB=" + portOfKB;
		if( portOfDA != null ) fileContent += "\nportOfDA=" + portOfDA;
		if( portOfMM != null ) fileContent += "\nportOfMM=" + portOfMM;
		if( privatePortOfMM != null ) fileContent += "\nprivatePortOfMM=" + privatePortOfMM;
		if( SLAServiceURIRulesReady != null ) fileContent += "\nSLAServiceURIRulesReady=" + SLAServiceURIRulesReady;
		if( SLAServiceURIReplanning != null ) fileContent += "\nSLAServiceURIReplanning=" + SLAServiceURIReplanning;
		if( DashboardURIRulesReady != null ) fileContent += "\nDashboardURIRulesReady=" + DashboardURIRulesReady;
		if( DashboardURIReplanning != null ) fileContent += "\nDashboardURIReplanning=" + DashboardURIReplanning;
		if( PlannerURIRulesReady != null ) fileContent += "\nPlannerURIRulesReady=" + PlannerURIRulesReady;
		if( PlannerURIReplanning != null ) fileContent += "\nPlannerURIReplanning=" + PlannerURIReplanning;


		new File( SEACLOUDS_FOLDER ).mkdirs();

		TxtFileWriter.write( fileContent, INITIALIZATION_CONFIGURATION_FILE_ON_SERVER );
	}

	/*private void storeInitializationData( InitializationData initializationData ) throws IOException{

		Kryo kryo = new Kryo();

		try{

			RandomAccessFile raf = new RandomAccessFile( INITIALIZATION_DATA, "rw" );

			Output output = new Output( new FileOutputStream( raf.getFD() ), 1024 );

			kryo.writeObject( output, initializationData );

			raf.close();

			output.close();
		}

		catch( FileNotFoundException ex ){

			ex.printStackTrace();
		}
	}*/

	public String initiateMonitor(){

		String msg = "[INFO] Monitor controller: MODACLouds monitoring platform has been initiated.";


		Properties properties = readInitializationData( INITIALIZATION_CONFIGURATION_FILE_ON_SERVER );

		String IPofKB = properties.getProperty( "IPofKB" );

		if( IPofKB == null ){

			msg = "[ERROR] Monitor controller: you should firstly initialize MODACLouds monitoring platform.";

			System.err.println( msg );
		}

		else{

			String IPofDA= properties.getProperty( "IPofDA" );
			String IPofMM= properties.getProperty( "IPofMM" );
			String portOfKB= properties.getProperty( "portOfKB" );
			String portOfDA= properties.getProperty( "portOfDA" );
			String portOfMM= properties.getProperty( "portOfMM" );
			String privatePortOfMM= properties.getProperty( "privatePortOfMM" );


			MODACloudsMonitoringInitiation.initiate( IPofKB, portOfKB, IPofDA, portOfDA, IPofMM, portOfMM, privatePortOfMM, SEACLOUDS_FOLDER );
		}


		return msg;
	}

	private static Properties readInitializationData( String initilizationFile ){

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

	/*private InitializationData retrieveInitializationData() throws IOException{

		InitializationData initializationData = null;

		if( new File( INITIALIZATION_DATA ).exists() ){

			Kryo kryo = new Kryo();

			try{

				RandomAccessFile raf = new RandomAccessFile( INITIALIZATION_DATA, "rw" );

				Input input = new Input( new FileInputStream( raf.getFD() ), 1024 );

				initializationData = kryo.readObject( input, InitializationData.class );

				raf.close();

				input.close();
			}

			catch( FileNotFoundException ex ){

				ex.printStackTrace();
			}
		}


		return initializationData;
	}*/


	public String installMonitoringRules( String monitoringRules ){

		String msg = "[INFO] Monitor controller: Monitoring rules have been installed.";


		Properties properties = readInitializationData( INITIALIZATION_CONFIGURATION_FILE_ON_SERVER );

		String IPofMM = properties.getProperty( "IPofMM" );

		if( IPofMM == null ){

			msg = "[ERROR] Monitor controller: you should firstly initialize MODACLouds monitoring platform.";

			System.err.println( msg );
		}

		else{

			String portOfMM= properties.getProperty( "portOfMM" );


			MODACloudsMonitoringRules.installMonitoringRules( IPofMM, portOfMM, monitoringRules );
		}


		return msg;
	}


	public String installDeploymentModel( String deploymentModel ){

		String msg = "[INFO] Monitor controller: Deployment model has been installed.";


		Properties properties = readInitializationData( INITIALIZATION_CONFIGURATION_FILE_ON_SERVER );

		String IPofKB = properties.getProperty( "IPofKB" );

		if( IPofKB == null ){

			msg = "[ERROR] Monitor controller: you should firstly initialize MODACLouds monitoring platform.";

			System.err.println( msg );
		}

		else{

			String IPofMM= properties.getProperty( "IPofMM" );
			String portOfMM= properties.getProperty( "portOfMM" );


			MODACloudsDeploymentModel.installDeploymentModel( IPofMM, portOfMM, deploymentModel );
		}


		return msg;
	}


	public String[] getRunningMetrics(){

		Properties properties = readInitializationData( INITIALIZATION_CONFIGURATION_FILE_ON_SERVER );

		String IPofMM = properties.getProperty( "IPofMM" );
		String portOfMM= properties.getProperty( "portOfMM" );

		if( IPofMM != null && portOfMM != null ) return MODACloudsMetrics.getRunningMetrics( IPofMM, portOfMM );


		return null;
	}

	public String[] getAllMetrics(){

		return MODACloudsMetrics.getAllMetrics();
	}


	public File getDataCollectors(){

		return MODACloudsDataCollectors.getDataCollectorsJar( SERVER_DATA_COLLECTORS_PATH, DATA_COLLECTORS_FILE_NAME );
	}

	public File getDataCollector( String metricName ){

		return MODACloudsDataCollectors.getDataCollectorJar( metricName, SERVER_DATA_COLLECTORS_PATH, DATA_COLLECTORS_FILE_NAME );
	}

	public String getDataCollectorInstallationFile( String metricName ){//It returns the content of the installation file of a data collector as a String.

		Properties properties = readInitializationData( INITIALIZATION_CONFIGURATION_FILE_ON_SERVER );

		String IPofKB = properties.getProperty( "IPofKB" );
		String portOfKB= properties.getProperty( "portOfKB" );
		String IPofDA= properties.getProperty( "IPofDA" );
		String portOfDA= properties.getProperty( "portOfDA" );

		if( IPofKB != null && portOfKB != null && IPofDA != null && portOfDA != null ) return MODACloudsDataCollectors.formInstallationExecutionCommand( metricName, IPofKB, portOfKB, IPofDA, portOfDA, DATA_COLLECTORS_FILE_NAME );


		return null;
	}


	public String uninstallMonitoringRule( String id ){

		String msg = "[INFO] Monitor controller: The monitoring rule has been deleted.";


		Properties properties = readInitializationData( INITIALIZATION_CONFIGURATION_FILE_ON_SERVER );

		String IPofMM = properties.getProperty( "IPofMM" );
		String portOfMM= properties.getProperty( "portOfMM" );

		if( IPofMM != null && portOfMM != null ) MODACloudsMonitoringRules.uninstallMonitoringRules( IPofMM, portOfMM, id );

		else msg = "[ERROR] Monitor controller: you should firstly initialize MODACLouds monitoring platform.";


		return msg;
	}

	public String addObserver( String metricName, String portOfObserver, String callbackURL ){

		String msg = "[INFO] Monitor controller: The observer has been added.";


		Properties properties = readInitializationData( INITIALIZATION_CONFIGURATION_FILE_ON_SERVER );

		String IPofMM = properties.getProperty( "IPofMM" );
		String portOfMM= properties.getProperty( "portOfMM" );

		if( IPofMM != null && portOfMM != null ){

			MODACloudsObservers.addObserver( IPofMM, portOfMM, metricName, callbackURL );

			MODACloudsObservers.startObserver( portOfObserver );
		}

		else msg = "[ERROR] Monitor controller: you should firstly initialize MODACLouds monitoring platform.";


		return msg;
	}


	//Main.
	public static void main( String[] args ){

		ControllerImpl controller = new ControllerImpl();

		controller.initiateMonitor();


		try{

			System.out.println( "\nSeaClouds Monitoring Platform Menu:" );

			while( true ){

				System.out.println( "1: Install a deployment model (in json format)" );
				System.out.println( "2: Install monitoring rules (in .xml format)" );
				System.out.println( "3: Retrieve installation files of the data collector and instanll it" );
				System.out.println( "4: Start observer" );
				System.out.println( "5: Add observer" );
				System.out.print( "\nChoice = " );
				//System.out.println( "5: Get metrics" );


				/*Integer answer = 0;

				BufferedReader input = new BufferedReader( new InputStreamReader( System.in ) );

				answer = Integer.parseInt( input.readLine() );
	
	
				if( answer == 3 ){

					File[] files = controller.getDataCollectorJars( metricName, IPofKB, IPofDA );


					TxtFileWriter.copyFile( files[ 0 ], new File( files[ 0 ].getName() ) );

					TxtFileWriter.copyFile( files[ 1 ], new File( files[ 1 ].getName() ) );

					BatchFile.execute( files[ 0 ].getName() );
				}

				else if( answer == 1 ){
	
					File deploymentModel = new File( DEPLOYMENT_MODEL_FILE );
	
					String content = TxtFileReader.read( deploymentModel );
	
	
					controller.installDeploymentModel( content );
				}

				else if( answer == 2 ){
	
					File monitoringRules = new File( MONITORING_RULES_FILE );
	
					String content = TxtFileReader.read( monitoringRules );
	
	
					controller.installMonitoringRules( content );
				}
	
				//else if( answer == 5 ){
	
					//String metrics = getMetrics();
	
					//System.out.println( metrics );
				//}
	
				else if( answer == 4 ) controller.startObserver();*/
	
				//else if( answer == 5 ) controller.addObserver( "FrontendCPUUtilization", "http://" + controller.IPofObserver + ":" + controller.portOfObserver /*+ "/v1/results"*/ );
			}
		}

		catch( Exception ex ){

			ex.printStackTrace();


			System.exit( -1 );
		}
	}
}
