package core.RESTGraphite;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;
//import com.sun.jersey.api.container.grizzly2.GrizzlyWebContainerFactory;


public class Main {

    public static URI getBaseURI() {
        return UriBuilder.fromUri("http://" + System.getProperty("localhost") + "/").port(Integer.parseInt(System.getProperty("localport"))).build();
    }

    public static URI BASE_URI;
    
    /*protected static HttpServer startServer() throws IOException {
        final Map<String, String> initParams = new HashMap<String, String>();

        initParams.put("com.sun.jersey.config.property.packages", "org.modaclouds.RESTGraphite");

        System.out.println("Starting grizzly2...");
        System.out.println("Local host and port: " + BASE_URI);
        System.out.println("Remote host and port: " + System.getProperty("graphitehost") + ":" + System.getProperty("graphiteport"));
        
        return GrizzlyWebContainerFactory.create(BASE_URI, initParams);
    }*/
    
    public static void main(String[] args) throws IOException {
    	if (args.length == 0) {
    		System.setProperty("localhost", "localhost");
    		System.setProperty("localport", "9998");
    		System.setProperty("graphitehost", "localhost");
    		System.setProperty("graphiteport", "2003");
    	} else if (args.length == 2) {
    		System.setProperty("localhost", "localhost");
    		System.setProperty("localport", "9998");
    		System.setProperty("graphitehost", args[0]);
    		System.setProperty("graphiteport", args[1]);
    	} else if (args.length == 4) {
    		System.setProperty("localhost", args[2]);
    		System.setProperty("localport", args[3]);
    		System.setProperty("graphitehost", args[0]);
    		System.setProperty("graphiteport", args[1]);
    	} else {
    		System.out.println("Usage: RESTGraphiteServer [graphitehost] [graphiteport] [localhost] [localport]");
    		System.exit(0);
    	}
    	
    	BASE_URI = getBaseURI();
    	
        //HttpServer httpServer = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...",
                BASE_URI));
        System.in.read();
        //while (true) {
        //}
       // httpServer.stop();
    }    
}
