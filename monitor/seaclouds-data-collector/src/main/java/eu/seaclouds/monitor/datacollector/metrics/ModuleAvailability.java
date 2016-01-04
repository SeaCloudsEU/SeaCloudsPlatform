package eu.seaclouds.monitor.datacollector.metrics;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.monitor.datacollector.Metric;

public class ModuleAvailability extends Metric {

    private static final Logger logger = LoggerFactory.getLogger(ModuleAvailability.class);
    
    @Override
    public Number getSample(String applicationUrl, String user, String password) throws Exception {
        
        try {
            URL url = new URL(applicationUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(2000);
            urlConn.connect();
            
            if(HttpURLConnection.HTTP_OK == urlConn.getResponseCode()){
                return 1;
            }else{
                return 0;
            }
            
        } catch (IOException e) {
            logger.error("Error creating HTTP connection.");
            return 0;
        }
    }

}
