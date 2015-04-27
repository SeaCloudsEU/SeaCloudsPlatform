package core.modaCloudsMonitoring.modaCloudsMonitoringRules;

import core.RESTCalls.RESTDelete;
import core.RESTCalls.RESTPost;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public class MODACloudsMonitoringRules {

	public static void installMonitoringRules( String IPofMM, String portOfMM, String monitoringRules ){

		try {

			RESTPost.httpPost( "http://" + IPofMM + ":" + portOfMM + "/v1/monitoring-rules", monitoringRules, "xml" );
		}

		catch( Exception ex ){

			ex.printStackTrace();
		}
	}

	public static void uninstallMonitoringRules( String IPofMM, String portOfMM, String id ){

		try {

			RESTDelete.httpDelete( "http://" + IPofMM + ":" + portOfMM + "/v1/monitoring-rules/" + id );
		}

		catch( Exception ex ){

			ex.printStackTrace();
		}
	}
}
