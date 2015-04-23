package core.RESTCalls;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public class RESTGet {

	public static String httpGet( String urlStr ) throws Exception{

		//System.out.println( "\nURL = " + urlStr );

		HttpClient client = new DefaultHttpClient();

		HttpGet get = new HttpGet( urlStr );

		HttpResponse response = client.execute( get );

		BufferedReader rd = new BufferedReader(new InputStreamReader( response.getEntity().getContent() ) );

		String content = "", line = null;

		while ( (line = rd.readLine()) != null ) content += line + "\n";


		return content;
	}

	public static InputStream httpGetResponse( String urlStr ){

		//System.out.println( "\nURL = " + urlStr );

		InputStream inputStream = null;

		try{

			HttpClient client = new DefaultHttpClient();

			HttpGet get = new HttpGet( urlStr );

			HttpResponse response = client.execute( get );

			inputStream = response.getEntity().getContent();
		}

		catch( Exception ex ){

			ex.printStackTrace();
		}


		return inputStream;
	}
}
