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
package eu.atos.sla.evaluation.guarantee;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.IPolicy;
import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.datamodel.bean.Agreement;
import eu.atos.sla.datamodel.bean.GuaranteeTerm;
import eu.atos.sla.datamodel.bean.Policy;
import eu.atos.sla.evaluation.constraint.IConstraintEvaluator;
import eu.atos.sla.evaluation.constraint.simple.SimpleConstraintEvaluator;
import eu.atos.sla.evaluation.guarantee.PoliciedServiceLevelEvaluator.ActualValueBuilder;
import eu.atos.sla.monitoring.IMonitoringMetric;

//@RunWith(SpringJUnit4ClassRunner.class)
public class PoliciedServiceLevelEvaluatorTest {

	private static final int THRESHOLD = 100;
	Date now;
	Date[] dates;
	String[] values;
	String constraint;
	IAgreement contract;
	IGuaranteeTerm term;
	PoliciedServiceLevelEvaluator evaluator;
	IBreachRepository breachRepository;
	List<IMonitoringMetric> allMetrics;
	ActualValueBuilder actualValueBuilder = new ActualValueBuilder();
	String kpiName;
	
	@Before
	public void setUp() throws Exception {
		now = new Date();
		kpiName = "LATENCY";
		constraint = kpiName + " LT " + THRESHOLD;			/* Must agree with isBreach() */
		
		IConstraintEvaluator metricsValidator = new SimpleConstraintEvaluator();
		evaluator = new PoliciedServiceLevelEvaluator();
		evaluator.setConstraintEvaluator(metricsValidator);
		
		contract = new Agreement();
		contract.setAgreementId("agreement-test");
		term = newGuaranteeTerm(kpiName, constraint);
	}
	
	/**
	 * Common setup for tests with policies (after setUp has run)
	 */
	private List<IMonitoringMetric> setUpWithPolicies() {
		/*
		 * values is the temporal series of metrics (one per second)
		 * t is the last measure
		 */
		values = new String[] { "102", "99", "99", "99", "101", "100", "99", "99" };
		TestMetricsGenerator metricsGenerator = new TestMetricsGenerator(now, values);
		allMetrics = metricsGenerator.getMetrics(kpiName);
		
		int splitIndex = allMetrics.size() - 3;
		List<IMonitoringMetric> newMetrics = allMetrics.subList(splitIndex, allMetrics.size());
		List<IMonitoringMetric> oldMetrics = allMetrics.subList(0, splitIndex);
		
		breachRepository = new DummyBreachRepository(constraint, oldMetrics);
		evaluator.setBreachRepository(breachRepository);
		
		return newMetrics;
	}

	@Test
	public void testEvaluateWithoutPolicy() {

		System.out.println("---testEvaluateWithoutPolicy()---");
		String kpiName = "UPTIME";
		values = new String[] { "99", "100", "98", "101" };
		TestMetricsGenerator metricsGenerator = new TestMetricsGenerator(now, values);
		List<IMonitoringMetric> metrics = metricsGenerator.getMetrics(kpiName);
		
		breachRepository = new DummyBreachRepository(constraint);
		evaluator.setBreachRepository(breachRepository);
		evaluator.evaluate(contract, term, metrics, now);
		
		
//		Date lastExecution = new Date(now.getTime() - (values.length + 1) * 1000);
//		IAgreementChecker.ResultItem result = policyChecker.calculateViolations(
//				contract, term, policy.getVariable(), policy, constraint, lastExecution, now);
		
		List<IViolation> violations = evaluator.evaluate(contract, term, metrics, now);
		
		assertEquals(2, violations.size());
		assertViolations(violations, metrics.get(1), metrics.get(3));
		//assertEquals(dates[1], result.getViolations().get(0).getDatetime());
		//assertEquals(dates[3], result.getViolations().get(1).getDatetime());
	}

	@Test
	public void testGetViolationsWithPolicy1() {
		IPolicy policy;
		System.out.println("---testEvaluateWithPolicy1()---");

		/*
		 * Only t - 2 is breach 
		 */
		List<IMonitoringMetric> metrics = setUpWithPolicies();

		policy = newPolicy("LATENCY", 2, new Date(3000));
		term.setPolicies(Arrays.asList(new IPolicy[] { policy }));

		List<IViolation> violations = evaluator.evaluate(contract, term, metrics, now);

		assertEquals(0, violations.size());
	}
	
	@Test
	public void testGetViolationsWithPolicy2() {
		IPolicy policy;

		System.out.println("---testEvaluateWithPolicy2()---");
		/*
		 * t-2, t-3 are breaches -> 1 violation
		 */
		List<IMonitoringMetric> metrics = setUpWithPolicies();

		policy = newPolicy("LATENCY", 2, new Date(5000));
		term.setPolicies(Arrays.asList(new IPolicy[] { policy }));

		List<IViolation> violations = evaluator.evaluate(contract, term, metrics, now);

		assertEquals(1, violations.size());
		assertViolations(violations, allMetrics.get(4), allMetrics.get(5));
		
	}
	
	@Test
	public void testGetViolationsWithPolicy3() {
		IPolicy policy;

		System.out.println("---testEvaluateWithPolicy3()---");
		/*
		 * t-2, t-3 are breaches -> 0 violation
		 */
		List<IMonitoringMetric> metrics = setUpWithPolicies();

		policy = newPolicy("LATENCY", 3, new Date(7000));
		term.setPolicies(Arrays.asList(new IPolicy[] { policy }));

		List<IViolation> violations = evaluator.evaluate(contract, term, metrics, now);

		assertEquals(0, violations.size());

	}
	
	@Test
	public void testGetViolationsWithPolicy4() {
		IPolicy policy;

		System.out.println("---testEvaluateWithPolicy4()---");
		/*
		 * t-2, t-3, t-7 are breaches -> 1 violation
		 */
		List<IMonitoringMetric> metrics = setUpWithPolicies();

		policy = newPolicy("LATENCY", 3, new Date(10000));
		term.setPolicies(Arrays.asList(new IPolicy[] { policy }));

		List<IViolation> violations = evaluator.evaluate(contract, term, metrics, now);

		assertEquals(1, violations.size());
		assertViolations(violations, allMetrics.get(0), allMetrics.get(4), allMetrics.get(5));
	}

	@Test
	public void testGetViolationsWithPolicy5() {

		System.out.println("---testEvaluateWithPolicy5()---");
		/*
		 * t-2, t-3, t-7 are breaches -> 1 violation (1st policy)
		 */
		List<IMonitoringMetric> metrics = setUpWithPolicies();

		term.setPolicies(Arrays.asList(new IPolicy[] { 
				newPolicy("LATENCY", 3, new Date(10000)),
				newPolicy("LATENCY", 2, new Date(3000))
		}));

		List<IViolation> violations = evaluator.evaluate(contract, term, metrics, now);

		assertEquals(1, violations.size());
		assertViolations(violations, allMetrics.get(0), allMetrics.get(4), allMetrics.get(5));
		assertEquals(10000, violations.get(0).getPolicy().getTimeInterval().getTime());
	}

	@Test
	public void testGetViolationsWithPolicy6() {

		System.out.println("---testEvaluateWithPolicy6()---");
		/*
		 * t-2, t-3, t-7 are breaches -> 2 violations
		 */
		List<IMonitoringMetric> metrics = setUpWithPolicies();

		term.setPolicies(Arrays.asList(new IPolicy[] { 
				newPolicy("LATENCY", 3, new Date(10000)),
				newPolicy("LATENCY", 2, new Date(5000))
		}));

		List<IViolation> violations = evaluator.evaluate(contract, term, metrics, now);

		assertEquals(2, violations.size());
		assertEquals(10000, violations.get(0).getPolicy().getTimeInterval().getTime());
		assertEquals(5000, violations.get(1).getPolicy().getTimeInterval().getTime());
		assertViolations(
				Collections.singletonList(violations.get(0)), 
				allMetrics.get(0), 
				allMetrics.get(4), 
				allMetrics.get(5)
		);
		assertViolations(
				Collections.singletonList(violations.get(1)), 
				allMetrics.get(4), 
				allMetrics.get(5)
		);
	}

	/**
	 * Assert that the raised violations match with the expected violations.
	 * 
	 * Assumes that both lists have the same order.
	 */
	private void assertViolations(List<IViolation> violations, IMonitoringMetric... expectedViolatedMetrics) {
		
		/*
		 * Assume actual value has to be built.
		 */
		if (violations.size() == 1) {
			String value = actualValueBuilder.fromMetrics(Arrays.asList(expectedViolatedMetrics), contract, kpiName);
			IMonitoringMetric expectedMetric = new MonitoringMetric(kpiName, value, now);
			expectedViolatedMetrics = new IMonitoringMetric[] { expectedMetric };
		}
		for (int i = 0; i < violations.size(); i++) {
			IViolation v = violations.get(i);
			IMonitoringMetric m = expectedViolatedMetrics[i];
			
			assertEquals(m.getDate(), v.getDatetime());
			assertEquals(m.getMetricValue(), v.getActualValue());
		}
	}
	
	private IGuaranteeTerm newGuaranteeTerm(String kpiName, String constraint) {
		
		GuaranteeTerm t = new GuaranteeTerm();
		t.setKpiName(kpiName);
		t.setServiceLevel(constraint);
		
		return t;
	}
	
	private IPolicy newPolicy(String kpiName, int count, Date timeInterval) {
		
		Policy p = new Policy();
		p.setVariable(kpiName);
		p.setCount(count);
		p.setTimeInterval(timeInterval);
		
		return p;
	}
	
	static class MonitoringMetric implements IMonitoringMetric {

		private String metricKey;
		private String value;
		private Date date;
		
		
		public MonitoringMetric(String metricKey, String value, Date date) {
			super();
			this.metricKey = metricKey;
			this.value = value;
			this.date = date;
		}

		@Override
		public String getMetricKey() {
			return metricKey;
		}

		@Override
		public String getMetricValue() {
			return value;
		}

		@Override
		public Date getDate() {
			return date;
		}
		
	}
}
