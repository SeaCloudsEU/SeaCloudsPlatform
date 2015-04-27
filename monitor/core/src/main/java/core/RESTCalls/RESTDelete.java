package core.RESTCalls;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public class RESTDelete {

	public static String httpDelete( String urlStr ) throws Exception{

		//System.out.println( "\nURL = " + urlStr );

		//System.out.println( "\ndata = " + data );


		HttpClient client = new DefaultHttpClient();

		HttpDelete delete = new HttpDelete( urlStr );

		HttpResponse response = client.execute( delete );


		String result = null;

		if( response != null && response.getEntity() != null ){

			BufferedReader r = new BufferedReader( new InputStreamReader( response.getEntity().getContent() ) );

			StringBuilder total = new StringBuilder();

			String line = null;

			while ( (line = r.readLine() ) != null ) total.append( line + "\n" );

			result = total.toString();
		}


		return result;
	}
}
