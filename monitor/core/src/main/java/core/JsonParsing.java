package core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */

public class JsonParsing {

	private static void getArray( Object object ) throws ParseException {

		JSONArray jsonArr = (JSONArray) object;

		for( int k = 0; k < jsonArr.size(); k++ ){

			if( jsonArr.get( k ) instanceof JSONObject ) parseJson( (JSONObject) jsonArr.get( k ) );

			//else System.out.println( jsonArr.get( k ) );
		}
	}

	private static void getArray( Object object, List<String> allArrayElements ) throws ParseException {

		JSONArray jsonArr = (JSONArray) object;

		for( int k = 0; k < jsonArr.size(); k++ ){

			if( jsonArr.get( k ) instanceof JSONObject ) parseJson( (JSONObject) jsonArr.get( k ), allArrayElements );

			else{

				//System.out.println( jsonArr.get( k ) );

				allArrayElements.add( jsonArr.get( k ).toString() );
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void parseJson( JSONObject jsonObject ) throws ParseException {

		Set<Object> set = jsonObject.keySet();

		Iterator iterator = set.iterator();

		while( iterator.hasNext() ){

			Object obj = iterator.next();

			if( jsonObject.get( obj ) instanceof JSONArray ){

				//System.out.println( obj.toString() );

				getArray( jsonObject.get( obj ) );
			}

			else{

				if( jsonObject.get( obj ) instanceof JSONObject ) parseJson( (JSONObject) jsonObject.get( obj ) );

				//else System.out.println( obj.toString() + "\t" + jsonObject.get( obj ) );
			}
        }
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void parseJson( JSONObject jsonObject, List<String> allArrayElements ) throws ParseException {

		Set<Object> set = jsonObject.keySet();

		Iterator iterator = set.iterator();

		while( iterator.hasNext() ){

			Object obj = iterator.next();

			if( jsonObject.get( obj ) instanceof JSONArray ){

				//System.out.println( obj.toString() );

				getArray( jsonObject.get( obj ), allArrayElements );
			}

			else{

				if( jsonObject.get( obj ) instanceof JSONObject ) parseJson( (JSONObject) jsonObject.get( obj ), allArrayElements );

				//else System.out.println( obj.toString() + "\t" + jsonObject.get( obj ) );
			}
        }
    }


	public static void main(String[] args) {

		try {

			JSONParser jsonParser = new JSONParser();

			//File file = new File("./resources/deploymentModel.json");

			//Object object = jsonParser.parse( new FileReader( file ) );

			Object object = jsonParser.parse( "{\"metrics\":[\"averageappavailability\",\"averageresponsetime\"]}" );

			JSONObject jsonObject = (JSONObject) object;

			List<String> allArrayElements = new ArrayList<String>();

			parseJson( jsonObject, allArrayElements );

			System.out.println( allArrayElements );
		}

		catch( Exception ex ){

			ex.printStackTrace();
		}
	}
}
