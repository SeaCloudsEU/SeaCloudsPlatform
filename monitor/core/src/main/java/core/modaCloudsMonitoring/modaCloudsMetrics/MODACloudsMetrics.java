package core.modaCloudsMonitoring.modaCloudsMetrics;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import core.JsonParsing;
import core.RESTCalls.RESTGet;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public class MODACloudsMetrics {

	public static String[] getRunningMetrics( String IPofMM, String portOfMM ){
		//Metrics:
		//	1: {"metrics":["averageappavailability","averageresponsetime"]}

		try {

			String metrics = RESTGet.httpGet( "http://" + IPofMM + ":" + portOfMM + "/v1/metrics" );//It returns a json object.


			JSONParser jsonParser = new JSONParser();

			Object object = jsonParser.parse( metrics );

			JSONObject jsonObject = (JSONObject) object;

			List<String> allArrayElements = new ArrayList<String>();

			JsonParsing.parseJson( jsonObject, allArrayElements );

			String[] result = allArrayElements != null ? allArrayElements.toArray( new String[ allArrayElements.size() ] ) : null;


			return result;
		}

		catch( Exception ex ){

			ex.printStackTrace();


			return null;
		}
	}

	public static String[] getAllMetrics(){

		String[] metrics = { "ResponseTime", "CPUUtilization", "MemoryUtilization", "Queries", "Availability" };


		return metrics;
	}
}
