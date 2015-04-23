package core.modaCloudsMonitoring.modaCloudsMetrics;

import core.RESTCalls.RESTPost;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public class MODACloudsObservers {

	//Constants.
	//private static final Logger LOGGER = LoggerFactory.getLogger( CVSObServer.class );


	//Methods.
	public static void addObserver( String IPofMM, String portOfMM, String metricName, String callbackURL ){

		try {

			String monitoringManagerURL = "http://" + IPofMM + ":" + portOfMM + "/v1/metrics/" + metricName + "/observers";

			callbackURL = callbackURL + "/v1/results";

 
			//System.out.println( "Monitoring manager URL = " + monitoringManagerURL );

			//System.out.println( "\nObserver callback URL = " + callbackURL + "\n" );


			RESTPost.httpPost( monitoringManagerURL, callbackURL );
		}

		catch( Exception ex ){

			ex.printStackTrace();
		}
	}

	public static void startObserver( String portOfObserver ){

		//LOGGER.debug( "Using port {}", portOfObserver );

		CVSObServer observer = new CVSObServer( Integer.parseInt( portOfObserver ) );

		try{

			observer.start();
		}

		catch( Exception ex ){

			ex.printStackTrace();
		}
	}
}
