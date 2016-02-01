package eu.seaclouds.monitor.datacollector.metrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.seaclouds.monitor.datacollector.Metric;
import it.polimi.tower4clouds.model.ontology.Resource;

public class NuroMetric extends Metric {

    private static final Logger logger = LoggerFactory.getLogger(NuroMetric.class);
    private static final String LAST_MINUTE = "minute";
    private static final String LAST_TEN_SECONDS = "10seconds";
    private static final String NURO_REQUEST_ANALYTICS_KEY = "request_analytics";
    private static final String AVG_RUNTIME_METRIC ="avg_run_time";
    private static final String REQUEST_COUNT_METRIC ="request_count";
    private static final String REQUEST_PLAYER_METRIC ="player_count";
    private static final String NURO_SENSOR_PATH ="/sensor.php";


    @Override
    public Number getSample(String applicationUrl, String user, String password) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(this.getNuroSensorResponse(applicationUrl, user, password));
        String value;
        Number toReturn;
        
        switch (this.getMonitoredMetric()) {
        case "NUROServerLastMinuteAverageRunTime":
             value = actualObj.get(NURO_REQUEST_ANALYTICS_KEY).get(LAST_MINUTE)
                    .get(AVG_RUNTIME_METRIC).toString();
             toReturn = Double.parseDouble(value.substring(1,
                    value.length() - 1));
            break;
        case "NUROServerLastMinuteAverageThroughput":
             value = actualObj.get(NURO_REQUEST_ANALYTICS_KEY).get(LAST_MINUTE)
                    .get(REQUEST_COUNT_METRIC).toString();
             toReturn = Integer.parseInt(value.substring(1,
                    value.length() - 1)) / 60;

            break;
        case "NUROServerLastMinutePlayerCount":
             value = actualObj.get(NURO_REQUEST_ANALYTICS_KEY).get(LAST_MINUTE)
                    .get(REQUEST_PLAYER_METRIC).toString();
             toReturn = Integer.parseInt(value.substring(1,
                    value.length() - 1));

            break;
        case "NUROServerLastMinuteRequestCount":
             value = actualObj.get(NURO_REQUEST_ANALYTICS_KEY).get(LAST_MINUTE)
                    .get(REQUEST_COUNT_METRIC).toString();
             toReturn = Integer.parseInt(value.substring(1,
                    value.length() - 1));

            break;
        case "NUROServerLastTenSecondsAverageRunTime":
             value = actualObj.get(NURO_REQUEST_ANALYTICS_KEY).get(LAST_TEN_SECONDS)
                    .get(AVG_RUNTIME_METRIC).toString();
             toReturn = Double.parseDouble(value.substring(1,
                    value.length() - 1));

            break;
        case "NUROServerLastTenSecondsAverageThroughput":
             value = actualObj.get(NURO_REQUEST_ANALYTICS_KEY).get(LAST_TEN_SECONDS)
                    .get(REQUEST_COUNT_METRIC).toString();
             toReturn = Integer.parseInt(value.substring(1,
                    value.length() - 1)) / 10;

            break;
        case "NUROServerLastTenSecondsPlayerCount":
             value = actualObj.get(NURO_REQUEST_ANALYTICS_KEY).get(LAST_TEN_SECONDS)
                    .get(REQUEST_PLAYER_METRIC).toString();
             toReturn = Integer.parseInt(value.substring(1,
                    value.length() - 1));

            break;
        case "NUROServerLastTenSecondsRequestCount":
             value = actualObj.get(NURO_REQUEST_ANALYTICS_KEY).get(LAST_TEN_SECONDS)
                    .get(REQUEST_COUNT_METRIC).toString();
             toReturn = Integer.parseInt(value.substring(1,
                    value.length() - 1));

            break;
        default:
            toReturn=null;
            break;
        }
        
        return toReturn;
    }
    
    private String getNuroSensorResponse(String applicationUrl, String user, String password) {

            HttpResponse response;
            HttpEntity responseEntity;
            InputStream stream;
            BufferedReader reader;
            StringBuilder out;

            HttpClient httpClient = HttpClientBuilder.create().build();
            String auth = user + ":" + password;
            String encodedAuth = Base64.encodeBase64String(auth.getBytes());

            HttpGet httpget = new HttpGet(applicationUrl + NURO_SENSOR_PATH);
                       
            httpget.addHeader("Authorization", "Basic " + encodedAuth);

            try {

                response = httpClient.execute(httpget);

                responseEntity = response.getEntity();
                stream = responseEntity.getContent();
                reader = new BufferedReader(new InputStreamReader(stream));
                out = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                reader.close();

                return out.toString();

            } catch (ClientProtocolException e) {
                logger.warn(e.getMessage());
                throw new IllegalStateException(e.getMessage(), e.getCause());
            } catch (IOException e) {
                logger.warn(e.getMessage());
                throw new IllegalStateException(e.getMessage(), e.getCause());
            }

    }

}
