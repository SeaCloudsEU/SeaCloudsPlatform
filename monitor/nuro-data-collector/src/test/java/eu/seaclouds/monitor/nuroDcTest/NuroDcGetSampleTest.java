package eu.seaclouds.monitor.nuroDcTest;

import it.polimi.tower4clouds.model.ontology.InternalComponent;
import it.polimi.tower4clouds.model.ontology.Resource;

import org.testng.Assert;
import org.testng.annotations.Test;

import eu.seaclouds.monitor.nuroDc.metrics.NuroMetric;

public class NuroDcGetSampleTest {


    private static final double EXPECTED_LAST_MINUTE_AVERAGE_RUNTIME = 0.0084509995062027;
    private static final int EXPECTED_LAST_MINUTE_PLAYER_COUNT = 10;
    private static final int EXPECTED_LAST_MINUTE_REQUEST_COUNT = 213;
    private static final double EXPECTED_LAST_MINUTE_AVERAGE_THROUGHPUT = EXPECTED_LAST_MINUTE_REQUEST_COUNT / 60;

    private static final double EXPECTED_LAST_TEN_SECONDS_AVERAGE_RUNTIME = 0.0086382230122884;
    private static final int EXPECTED_LAST_TEN_SECONDS_PLAYER_COUNT = 3;
    private static final int EXPECTED_LAST_TEN_SECONDS_REQUEST_COUNT = 39;
    private static final double EXPECTED_LAST_TEN_SECONDS_AVERAGE_THROUGHPUT = EXPECTED_LAST_TEN_SECONDS_REQUEST_COUNT / 10;

    
    @Test
    public void testNuroDataCollector() throws Exception {

        NuroMetric metric = new NuroMetric();
        double sample;
        Resource testResource = new InternalComponent("NuroApplication",
                "NuroApplication_id");
        
        metric.setResponse(InputExample.EXAMPLE_INPUT);
        
        metric.setMonitoredMetric("NUROServerLastMinuteAverageRunTime");     
        sample = metric.getSample(testResource).doubleValue();
        Assert.assertTrue(sample == EXPECTED_LAST_MINUTE_AVERAGE_RUNTIME);
        
        metric.setMonitoredMetric("NUROServerLastMinuteAverageThroughput");
        sample = metric.getSample(testResource).doubleValue();
        Assert.assertTrue(sample == EXPECTED_LAST_MINUTE_AVERAGE_THROUGHPUT);
        
        metric.setMonitoredMetric("NUROServerLastMinutePlayerCount");
        sample = metric.getSample(testResource).doubleValue();
        Assert.assertTrue(sample == EXPECTED_LAST_MINUTE_PLAYER_COUNT);
        
        metric.setMonitoredMetric("NUROServerLastMinuteRequestCount");
        sample = metric.getSample(testResource).doubleValue();
        Assert.assertTrue(sample == EXPECTED_LAST_MINUTE_REQUEST_COUNT);
        
        metric.setMonitoredMetric("NUROServerLastTenSecondsAverageRunTime");
        sample = metric.getSample(testResource).doubleValue();
        Assert.assertTrue(sample == EXPECTED_LAST_TEN_SECONDS_AVERAGE_RUNTIME);
        
        metric.setMonitoredMetric("NUROServerLastTenSecondsAverageThroughput");
        sample = metric.getSample(testResource).doubleValue();
        Assert.assertTrue(sample == EXPECTED_LAST_TEN_SECONDS_AVERAGE_THROUGHPUT);
        
        metric.setMonitoredMetric("NUROServerLastTenSecondsPlayerCount");
        sample = metric.getSample(testResource).doubleValue();
        Assert.assertTrue(sample == EXPECTED_LAST_TEN_SECONDS_PLAYER_COUNT);
        
        metric.setMonitoredMetric("NUROServerLastTenSecondsRequestCount");
        sample = metric.getSample(testResource).doubleValue();
        Assert.assertTrue(sample == EXPECTED_LAST_TEN_SECONDS_REQUEST_COUNT);
 
    }
}
