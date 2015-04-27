package core.modaCloudsMonitoring.modaCloudsDeploymentModel;

import core.RESTCalls.RESTPost;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public class MODACloudsDeploymentModel {

	//Methods.
	public static void installDeploymentModel( String monitoringManagerEndpointIP, String monitoringManagerEndpointPort, String deploymentModel ){

		try {

			RESTPost.httpPost( "http://" + monitoringManagerEndpointIP + ":" + monitoringManagerEndpointPort + "/v1/model/resources", deploymentModel, "json" );
		}

		catch( Exception e ){

			e.printStackTrace();
		}
	}
}
