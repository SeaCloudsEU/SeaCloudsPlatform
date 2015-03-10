/**
 * Copyright 2015 SeaClouds
 * Contact: SeaClouds
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
package eu.seaclouds.sla.retriever;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.IfProfileValue;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.bean.GuaranteeTerm;
import eu.atos.sla.monitoring.IMetricsRetrieverV2.RetrievalItem;
import eu.atos.sla.monitoring.IMonitoringMetric;
import eu.seaclouds.sla.retriever.BrooklynMetricsRetrieverV1.SensorClient;

/**
 * Test to show BrooklynMetricsRetriever.
 * 
 * Assume there are three system properties: 
 * <li>BROOKLYN_URL : Brooklyn root url (e.g.: http://localhost:8081)
 * <li>BROOKLYN_APP : Brooklyn application id
 * <li>BROOKLYN_ENTITY: Brooklyn entity id
 * <li>BROOKLYN_SENSOR: Brooklyn sensor id
 * 
 * To run:
 *  mvn test -Dtest-profile=IntegrationTest -Dorg.slf4j.simpleLogger.defaultLogLevel=debug
 * @author rsosa
 *
 */
@IfProfileValue(name="test-profile", value="IntegrationTest")
public class BrooklynMetricsRetrieverV1IntegrationTest {
	private Logger logger = LoggerFactory.getLogger(BrooklynMetricsRetrieverV1IntegrationTest.class);
	
	private static final String BROOKLYN_SENSOR = "BROOKLYN_SENSOR";
	private static final String BROOKLYN_ENTITY = "BROOKLYN_ENTITY";
	private static final String BROOKLYN_APP = "BROOKLYN_APP";
	private static final String BROOKLYN_URL = "BROOKLYN_URL";
	
	private SensorClient sensorClient;
	private String brooklynUrl;
	private String brooklynApp;
	private String brooklynEntity;
	private String brooklynSensor;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		brooklynUrl = System.getenv(BROOKLYN_URL);
		brooklynApp = System.getenv(BROOKLYN_APP);
		brooklynEntity = System.getenv(BROOKLYN_ENTITY);
		brooklynSensor = System.getenv(BROOKLYN_SENSOR);

		ClientConfig config = new DefaultClientConfig();
		Client jerseyClient = Client.create(config);
		sensorClient = new SensorClient(jerseyClient, brooklynUrl);
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testGetMetricsShouldPass() {
		
		BrooklynMetricsRetrieverV1 retriever = new BrooklynMetricsRetrieverV1(sensorClient);
		
		final String agreementId = "agreement";
		final String variable = brooklynSensor;
		final IGuaranteeTerm term = new GuaranteeTerm();
		term.setServiceScope(brooklynApp + "/" + brooklynEntity);
		
		RetrievalItem item = buildRetrievalItem(variable, term);
		List<RetrievalItem> retrievalItems = Collections.singletonList(item);
		Map<IGuaranteeTerm, List<IMonitoringMetric>> metricsMap = 
				retriever.getMetrics(agreementId, retrievalItems, 1);
		
		List<IMonitoringMetric> metrics = metricsMap.get(term);
		assertEquals(1, metrics.size());
		
		IMonitoringMetric metric = metrics.get(0);
		logger.debug("metricvalue = " + metric.getMetricValue());

		assertEquals(variable, metric.getMetricKey());
		assertNotEquals("", metric.getMetricValue());
		
	}

	private RetrievalItem buildRetrievalItem(final String variable,
			final IGuaranteeTerm term) {
		return new RetrievalItem() {
			
			@Override
			public String getVariable() {
				return variable;
			}
			
			@Override
			public IGuaranteeTerm getGuaranteeTerm() {
				return term;
			}
			
			@Override
			public Date getEnd() {
				return new Date(0);
			}
			
			@Override
			public Date getBegin() {
				return new Date(0);
			}
		};
	}

}
