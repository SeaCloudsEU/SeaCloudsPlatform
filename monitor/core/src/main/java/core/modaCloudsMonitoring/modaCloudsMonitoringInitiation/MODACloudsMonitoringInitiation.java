package core.modaCloudsMonitoring.modaCloudsMonitoringInitiation;

import java.io.File;

import javax.swing.JOptionPane;

import core.BatchFile;
import core.OperatingSystem;
import core.TxtFileWriter;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public class MODACloudsMonitoringInitiation {

	//Constants.
	private static final String INIT_BATCH_FILE = "./init.bat";

	private static final String SERVER_FUSEKI = "/jena-fuseki-1.1.1";

	private static final String SERVER_MONITORING_METRICS = "/resources/monitoring_metrics.xml";

	private static final String SERVER_CSPARQL = "/rsp-services-csparql-0.4.6.2-modaclouds";

	private static final String SERVER_MONITORING_MANAGER = "/monitoring-manager-1.4";


	//Methods.
	public static void initiate( String IPofKB, String portOfKB, String IPofDA, String portOfDA, String IPofMM, String portOfMM, String privatePortOfMM, String seaCloudsFolder ){

		if( OperatingSystem.isWindows() ){

			createBatchfile( IPofKB, portOfKB, IPofDA, portOfDA, IPofMM, portOfMM, privatePortOfMM, seaCloudsFolder );

			BatchFile.execute( INIT_BATCH_FILE );
		}

		else if( OperatingSystem.isUnix() ) JOptionPane.showMessageDialog( null, "To initialize for the case of Unix", "Unix", JOptionPane.ERROR_MESSAGE );
	}

	private static void createBatchfile( String IPofKB, String portOfKB, String IPofDA, String portOfDA, String IPofMM, String portOfMM, String privatePortOfMM, String seaCloudsFolder ){

		File file = new File( seaCloudsFolder + SERVER_FUSEKI + "/ds/tdb.lock" );


		String content = "set \"MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_IP=" + IPofKB + "\"" +
								  "\n" + "set \"MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_PORT=" + portOfKB + "\"" +
								  "\n" + "set \"MODACLOUDS_KNOWLEDGEBASE_DATASET_PATH \"/modaclouds/kb\"" +
								  "\n" + "set \"MODACLOUDS_MONITORING_DDA_ENDPOINT_IP=" + IPofDA + "\"" +
								  "\n" + "set \"MODACLOUDS_MONITORING_DDA_ENDPOINT_PORT=" + portOfDA + "\"" +
								  "\n" + "set \"MODACLOUDS_MONITORING_MANAGER_PORT=" + portOfMM + "\"" +
								  "\n" + "set \"MODACLOUDS_MONITORING_MANAGER_PRIVATE_PORT=" + privatePortOfMM + "\"" +
								  "\n" + "set \"MODACLOUDS_MONITORING_MANAGER_PRIVATE_IP=" + IPofMM + "\"" +
								  "\n" + "set \"MODACLOUDS_MONITORING_MONITORING_METRICS_FILE \"" + seaCloudsFolder + SERVER_MONITORING_METRICS + "\"\n" +


								  "del \"" + file.getAbsolutePath() + "\"\n" +

								  "cd " + seaCloudsFolder + SERVER_FUSEKI + "\n" +
								  "START CMD /C CALL fuseki-server.bat --update --port " + portOfKB + " --loc ./ds /modaclouds/kb\n" +

								  "cd .." + SERVER_CSPARQL + "\n" +
								  "START CMD /C CALL java -jar rsp-services-csparql.jar\n" +

								  "cd .." + SERVER_MONITORING_MANAGER + "\n" +
								  "START CMD /C CALL java -jar monitoring-manager.jar\n" +
								  "exit";

		TxtFileWriter.write( content, INIT_BATCH_FILE );
	}
}
