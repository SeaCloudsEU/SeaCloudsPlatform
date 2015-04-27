package core.RESTCalls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public class RESTPost {

	public static String httpPost( String urlStr, String[] paramName, String[] paramVal ) throws Exception{

		URL url = new URL( urlStr );
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod( "POST" );

		conn.setDoOutput( true );
		conn.setDoInput( true );
		conn.setUseCaches( false );
		conn.setAllowUserInteraction( false );
		conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );


		//Create the form content.
		OutputStream out = conn.getOutputStream();

		Writer writer = new OutputStreamWriter( out, "UTF-8" );

		for( int i = 0; i < paramName.length; i++ ){

			writer.write( paramName[ i ] );
			writer.write( "=" );
			writer.write( URLEncoder.encode(paramVal[ i ], "UTF-8") );
			writer.write( "&" );
		}

		writer.close();
		out.close();

		if( conn.getResponseCode() != 200 ) throw new IOException( conn.getResponseMessage() );


		//Buffer the result into a string.
		BufferedReader rd = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );

		StringBuilder sb = new StringBuilder();

		String line;

		while ((line = rd.readLine()) != null) sb.append( line + "\n" );

		rd.close();

		conn.disconnect();


		return sb.toString();
	}

	public static String httpPost( String urlStr, String data, String type ){

		String result = null;


		if( type == null || ( type != null && ! type.equals( "text/plain" ) && ! type.equals( "xml" ) && ! type.equals( "json" ) ) ){

			System.err.println( "\n[ERROR] RESTPost: Unknown input type (" + type + ") in the Http RESTful post request." );


			return null;
		}

		else{

			//System.out.println( "\nURL = " + urlStr ); 

			//System.out.println( "\ndata = " + data );


			try{

				HttpClient client = new DefaultHttpClient(); //System.out.println( "\nclient" );

				HttpPost post = new HttpPost( urlStr ); //System.out.println( "\npost" );

				StringEntity input = new StringEntity( data ); //System.out.println( "\ninput" );


				if( type.equals( "text/plain" ) ) input.setContentType( "text/plain" );

				else input.setContentType( "application/" + type );


				post.setEntity( input ); //System.out.println( "\npost" );

				HttpResponse response = client.execute( post ); //System.out.println( "\nresponse" );


				if( response != null && response.getEntity() != null ){

					BufferedReader r = new BufferedReader( new InputStreamReader( response.getEntity().getContent() ) );

					StringBuilder total = new StringBuilder();

					String line = null;

					while ( (line = r.readLine() ) != null ) total.append( line + "\n" );

					result = total.toString();
				}

				//System.out.println( "\nEnd if" );
			}

			catch( Exception ex ){

				ex.printStackTrace();
			}


			return result; 
		}
	}

	public static String httpPost( String urlStr, String data ) throws Exception{

		//System.out.println( "\nURL = " + urlStr );

		//System.out.println( "\ndata = " + data );


		HttpClient client = new DefaultHttpClient();

		HttpPost post = new HttpPost( urlStr );

		StringEntity input = new StringEntity( data );

		post.setEntity( input );

		HttpResponse response = client.execute( post );


		BufferedReader r = new BufferedReader( new InputStreamReader( response.getEntity().getContent() ) );

		StringBuilder total = new StringBuilder();

		String line = null;

		while ( (line = r.readLine() ) != null ) total.append( line + "\n" );


		return total.toString();
	}

	public static String httpPost( String urlStr ) throws Exception{

		//System.out.println( "\nURL = " + urlStr );


		HttpClient client = new DefaultHttpClient();

		HttpPost post = new HttpPost( urlStr );

		HttpResponse response = client.execute( post );


		BufferedReader r = new BufferedReader( new InputStreamReader( response.getEntity().getContent() ) );

		StringBuilder total = new StringBuilder();

		String line = null;

		while ( (line = r.readLine() ) != null ) total.append( line + "\n" );


		return total.toString();
	}

	public static InputStream httpPostResponse( String urlStr ) throws Exception{

		//System.out.println( "\nURL = " + urlStr );


		HttpClient client = new DefaultHttpClient();

		HttpPost post = new HttpPost( urlStr );

		HttpResponse response = client.execute( post );


		return response.getEntity().getContent();
	}
}
