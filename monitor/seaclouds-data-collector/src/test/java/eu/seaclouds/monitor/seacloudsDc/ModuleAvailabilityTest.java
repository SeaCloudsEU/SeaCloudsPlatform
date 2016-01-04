package eu.seaclouds.monitor.seacloudsDc;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import eu.seaclouds.monitor.datacollector.metrics.ModuleAvailability;

public class ModuleAvailabilityTest {
    
    @Test
    public void moduleAvailabilityTest() throws Exception {
        MockWebServer mockWebServer = new MockWebServer();
        
        mockWebServer.enqueue(new MockResponse());
        HttpUrl serverUrl = mockWebServer.url("/");
        
        
        ModuleAvailability metric = new ModuleAvailability();
        
        Number response = metric.getSample("http://"+serverUrl.host()+":"+serverUrl.port(), "", "");
        Assert.assertEquals(response, 1);
        
        mockWebServer.shutdown();
        
        response = metric.getSample("http://"+serverUrl.host()+":"+serverUrl.port(), "", "");
        Assert.assertEquals(response, 0);
        

    }

}
