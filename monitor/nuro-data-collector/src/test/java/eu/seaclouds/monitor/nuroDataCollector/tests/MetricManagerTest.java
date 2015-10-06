/**
 * Copyright 2015 SeaCloudsEU
 * Contact: Michele Guerriero <michele.guerriero@mail.polimi.it>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.seaclouds.monitor.nuroDataCollector.tests;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import eu.seaclouds.monitor.nuroDataCollector.dataCollector.MetricManager;
import eu.seaclouds.monitor.nuroDataCollector.dataCollector.Metrics;
import eu.seaclouds.monitor.nuroDataCollector.dataCollector.NuroApplicationDC;
import eu.seaclouds.monitor.nuroDataCollector.exception.MetricNotAvailableException;

public class MetricManagerTest 
{
    private Logger logger = LoggerFactory.getLogger(NuroApplicationDC.class);
	
    private static final int TEN_SECONDS = 10;
    private static final int MINUTE = 60;
    
	private static final Double lastTenSecondsAverageRunTimeTestValue=new Double(0.0086382230122884);
	private static final Double lastMinuteAverageRunTimeTestValue=new Double(0.0084509995062027);
	private static final Integer lastTenSecondsPlayerCountTestValue=new Integer(3);
	private static final Integer lastMinutePlayerCountTestValue=new Integer(10);
	private static final Integer lastTenSecondsRequestCountTestValue=new Integer(39);
	private static final Integer lastMinuteRequestCountTestValue=new Integer(213);
	private static final Double lastTenSecondsAverageThroughputTestValue=new Double(lastTenSecondsRequestCountTestValue.doubleValue()/TEN_SECONDS);
	private static final Double lastMinuteAverageThroughputTestValue=new Double(lastMinuteRequestCountTestValue.doubleValue()/MINUTE);

	
    @Test
    public void testSignificantInstance() {
        NuroApplicationDC dc = new NuroApplicationDC();
        Assert.assertNotNull(dc);
    }
    
    @Test
    public void testMetricRetrieving() {
        try {
			MetricManager manager=new MetricManager();
			
			for(String metric: manager.getApplicationMetrics()){
				ObjectMapper mapper = new ObjectMapper();
				JsonNode actualObj = mapper.readTree(InputExample.EXAMPLE_INPUT);
				if(metric.equals(Metrics.NURO_LAST_TEN_SECONDS_AVERAGE_RESPONSE_TIME)){
					logger.info("testing "+metric);
					Assert.assertEquals(manager.getMetricValue(metric, actualObj),lastTenSecondsAverageRunTimeTestValue);
				}else if(metric.equals(Metrics.NURO_LAST_MINUTE_AVERAGE_RESPONSE_TIME)){
					logger.info("testing "+metric);
					Assert.assertEquals(manager.getMetricValue(metric, actualObj),lastMinuteAverageRunTimeTestValue);
				}else if(metric.equals(Metrics.NURO_LAST_TEN_SECONDS_PLAYER_COUNT)){
					logger.info("testing "+metric);
					Assert.assertEquals(manager.getMetricValue(metric, actualObj),lastTenSecondsPlayerCountTestValue);
				}else if(metric.equals(Metrics.NURO_LAST_MINUTE_PLAYER_COUNT)){
					logger.info("testing "+metric);
					Assert.assertEquals(manager.getMetricValue(metric, actualObj),lastMinutePlayerCountTestValue);
				}else if(metric.equals(Metrics.NURO_LAST_TEN_SECONDS_REQUEST_COUNT)){
					logger.info("testing "+metric);
					Assert.assertEquals(manager.getMetricValue(metric, actualObj),lastTenSecondsRequestCountTestValue);
				}else if(metric.equals(Metrics.NURO_LAST_MINUTE_REQUEST_COUNT)){
					logger.info("testing "+metric);
					Assert.assertEquals(manager.getMetricValue(metric, actualObj),lastMinuteRequestCountTestValue);
				}else if(metric.equals(Metrics.NURO_LAST_TEN_SECONDS_THROUGHPUT)){
					logger.info("testing "+metric);
					Assert.assertEquals(manager.getMetricValue(metric, actualObj),lastTenSecondsAverageThroughputTestValue);
				}else if(metric.equals(Metrics.NURO_LAST_MINUTE_THROUGHPUT)){
					logger.info("testing "+metric);
					Assert.assertEquals(manager.getMetricValue(metric, actualObj),lastMinuteAverageThroughputTestValue);
				}
			}
		} catch (MetricNotAvailableException e) {
            logger.warn(e.getMessage());
            throw new IllegalStateException(e.getMessage(), e.getCause());
		}  catch (IOException e) {
            logger.warn("There were some problems getting the test input example.");
            throw new IllegalStateException("There were some problems getting the test input example.", e.getCause());
		}
    }
  
    
}
