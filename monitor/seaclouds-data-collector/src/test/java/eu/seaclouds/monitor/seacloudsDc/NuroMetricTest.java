package eu.seaclouds.monitor.seacloudsDc;

import eu.seaclouds.monitor.datacollector.metrics.NuroMetric;
import eu.seaclouds.monitor.seacloudsDc.NuroInputExample;

import javax.ws.rs.core.MediaType;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

public class NuroMetricTest {


    private static final double EXPECTED_LAST_MINUTE_AVERAGE_RUNTIME = 0.0084509995062027;
    private static final int EXPECTED_LAST_MINUTE_PLAYER_COUNT = 10;
    private static final int EXPECTED_LAST_MINUTE_REQUEST_COUNT = 213;
    private static final double EXPECTED_LAST_MINUTE_AVERAGE_THROUGHPUT = EXPECTED_LAST_MINUTE_REQUEST_COUNT / 60;

    private static final double EXPECTED_LAST_TEN_SECONDS_AVERAGE_RUNTIME = 0.0086382230122884;
    private static final int EXPECTED_LAST_TEN_SECONDS_PLAYER_COUNT = 3;
    private static final int EXPECTED_LAST_TEN_SECONDS_REQUEST_COUNT = 39;
    private static final double EXPECTED_LAST_TEN_SECONDS_AVERAGE_THROUGHPUT = EXPECTED_LAST_TEN_SECONDS_REQUEST_COUNT / 10;
    private static final String TEST_APPLICATION_USER = "seaclouds";
    private static final String TEST_APPLICATION_PASSWORD = "preview";
    private static final int NUMBER_OF_METRICS = 8;

    
    @Test
    public void nuroMetricTest() throws Exception{

        MockWebServer mockWebServer = new MockWebServer();   
        NuroMetric metric = new NuroMetric();
        double sample;
        
        for(int i=0; i<=NUMBER_OF_METRICS; i++){
            mockWebServer.enqueue(new MockResponse()
                    .setBody(NuroInputExample.EXAMPLE_INPUT)
                    .setHeader("Content-Type", MediaType.APPLICATION_JSON));            
        }

        
        mockWebServer.start();
        
        HttpUrl serverUrl = mockWebServer.url("/sensor.php");
                        
        metric.setMonitoredMetric("NUROServerLastMinuteAverageRunTime");     
        sample = metric.getSample("http://"+serverUrl.host()+":"+serverUrl.port(), TEST_APPLICATION_USER, TEST_APPLICATION_PASSWORD).doubleValue();
        Assert.assertTrue(sample == EXPECTED_LAST_MINUTE_AVERAGE_RUNTIME);
                
        metric.setMonitoredMetric("NUROServerLastMinuteAverageThroughput");
        sample = metric.getSample("http://"+serverUrl.host()+":"+serverUrl.port(), TEST_APPLICATION_USER, TEST_APPLICATION_PASSWORD).doubleValue();
        Assert.assertTrue(sample == EXPECTED_LAST_MINUTE_AVERAGE_THROUGHPUT);
        
        metric.setMonitoredMetric("NUROServerLastMinutePlayerCount");
        sample = metric.getSample("http://"+serverUrl.host()+":"+serverUrl.port(), TEST_APPLICATION_USER, TEST_APPLICATION_PASSWORD).doubleValue();
        Assert.assertTrue(sample == EXPECTED_LAST_MINUTE_PLAYER_COUNT);
        
        metric.setMonitoredMetric("NUROServerLastMinuteRequestCount");
        sample = metric.getSample("http://"+serverUrl.host()+":"+serverUrl.port(), TEST_APPLICATION_USER, TEST_APPLICATION_PASSWORD).doubleValue();
        Assert.assertTrue(sample == EXPECTED_LAST_MINUTE_REQUEST_COUNT);
        
        metric.setMonitoredMetric("NUROServerLastTenSecondsAverageRunTime");
        sample = metric.getSample("http://"+serverUrl.host()+":"+serverUrl.port(), TEST_APPLICATION_USER, TEST_APPLICATION_PASSWORD).doubleValue();
        Assert.assertTrue(sample == EXPECTED_LAST_TEN_SECONDS_AVERAGE_RUNTIME);
        
        metric.setMonitoredMetric("NUROServerLastTenSecondsAverageThroughput");
        sample = metric.getSample("http://"+serverUrl.host()+":"+serverUrl.port(), TEST_APPLICATION_USER, TEST_APPLICATION_PASSWORD).doubleValue();
        Assert.assertTrue(sample == EXPECTED_LAST_TEN_SECONDS_AVERAGE_THROUGHPUT);
        
        metric.setMonitoredMetric("NUROServerLastTenSecondsPlayerCount");
        sample = metric.getSample("http://"+serverUrl.host()+":"+serverUrl.port(), TEST_APPLICATION_USER, TEST_APPLICATION_PASSWORD).doubleValue();
        Assert.assertTrue(sample == EXPECTED_LAST_TEN_SECONDS_PLAYER_COUNT);
        
        metric.setMonitoredMetric("NUROServerLastTenSecondsRequestCount");
        sample = metric.getSample("http://"+serverUrl.host()+":"+serverUrl.port(), TEST_APPLICATION_USER, TEST_APPLICATION_PASSWORD).doubleValue();
        Assert.assertTrue(sample == EXPECTED_LAST_TEN_SECONDS_REQUEST_COUNT);
        
        mockWebServer.shutdown();

    }
}
