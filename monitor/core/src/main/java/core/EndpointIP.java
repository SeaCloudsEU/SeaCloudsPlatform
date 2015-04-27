package core;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 *
 */
public class EndpointIP {

	public static String getIP(){

		String address = null;

		try{

			InetAddress inetAddress = InetAddress.getLocalHost();


			return address = inetAddress.getHostAddress();
		}

		catch( UnknownHostException ex ){}


		return address;
	}
}
