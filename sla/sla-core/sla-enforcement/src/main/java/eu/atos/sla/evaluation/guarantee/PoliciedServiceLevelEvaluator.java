/**
 * Copyright 2014 Atos
 * Contact: Atos <roman.sosa@atos.net>
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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IBreach;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.IPolicy;
import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.datamodel.bean.Breach;
import eu.atos.sla.datamodel.bean.Policy;
import eu.atos.sla.datamodel.bean.Violation;
import eu.atos.sla.evaluation.constraint.IConstraintEvaluator;
import eu.atos.sla.monitoring.IMonitoringMetric;

/**
 * Implements a ServiceLevelEvaluator that takes into account Policies. 
 * 
 * <p>In a policy, a non-fulfilled service level by a metric is considered a breach. A policy specifies how many
 * breaches in a interval of time must occur to raise a violation.</p>
 * 
 * <p>If no policies are defined for the guarantee term, each breach is a violation. Otherwise, only a violation
 * will be raised (if applicable) in each execution. Therefore, to avoid having breaches not considered as 
 * violations, the policy interval should be greater than the evaluation interval.</p>
 * 
 * <p>The breaches management (load, store) is totally performed in this class, and therefore, can be considered 
 * as a side effect. The advantage is that this way, the interface for upper levels is cleaner 
 * (GuaranteeTermEvaluator and AgreementEvaluator do not know about breaches).</p>
 * 
 * Usage:
 * <pre>
 * PoliciedServiceLevelEvaluator pe = new PoliciedServiceLevelEvaluator();
 * pe.setConstraintEvaluator(...);
 * pe.setBreachRepository(...);
 * 
 * pe.evaluate(...):
 * </pre>
 * 
 * @see IBreachRepository
 * @see IConstraintEvaluator
 *
 */
public class PoliciedServiceLevelEvaluator implements IServiceLevelEvaluator {

    private static Logger logger = LoggerFactory.getLogger(PoliciedServiceLevelEvaluator.class);

    private static IPolicy defaultPolicy = new Policy(1, new Date(0));
    private IConstraintEvaluator constraintEval;
    private IBreachRepository breachRepository;
    private PoliciedServiceLevelEvaluator.ActualValueBuilder actualValueBuilder = new ActualValueBuilder();
    private PoliciedServiceLevelEvaluator.BreachesFromMetricsBuilder breachesFromMetricsBuilder = new BreachesFromMetricsBuilder();

    @Override
    public List<IViolation> evaluate(
            IAgreement agreement, IGuaranteeTerm term, List<IMonitoringMetric> metrics, Date now) {
        logger.debug("evaluate(agreement={}, term={}, servicelevel={})", 
                agreement.getAgreementId(), term.getKpiName(), term.getServiceLevel());

        /*
         * throws NullPointerException if not property initialized 
         */
        checkInitialized();
        
        List<IViolation> newViolations = new ArrayList<IViolation>();
        String kpiName = term.getKpiName();
        String constraint = term.getServiceLevel();

        /*
         * Calculate with new metrics are breaches
         */
        List<IMonitoringMetric> newBreachMetrics = constraintEval.evaluate(kpiName, constraint, metrics);
        logger.debug("Found {} breaches in new metrics", newBreachMetrics.size());
        
        List<IPolicy> policies = getPoliciesOrDefault(term);
        boolean withPolicies = !isDefaultPolicy(policies);
        
        List<IBreach> newBreaches = null;         /* only to use with policies */
        if (withPolicies) {
            newBreaches = breachesFromMetricsBuilder.build(newBreachMetrics, agreement, kpiName);
            saveBreaches(newBreaches);
        }
        
        /*
         * Evaluate each policy
         */
        for (IPolicy policy : policies) {
            Date breachesBegin = new Date(now.getTime() - policy.getTimeInterval().getTime());

            logger.debug("Evaluating policy({},{}s) in interval({}, {})", 
                    policy.getCount(), policy.getTimeInterval().getTime() / 1000, breachesBegin, now);
            
            List<IBreach> oldBreaches;
            
            if (withPolicies) {
                /*
                 * TODO rsosa: oldBreaches should start from last violation, if any; otherwise, as is.
                 */
                oldBreaches = breachRepository.getBreachesByTimeRange(agreement, kpiName, breachesBegin, now);
                logger.debug("Found {} breaches", oldBreaches.size());
                
                List<IBreach> breaches = new PoliciedServiceLevelEvaluator.CompositeList<IBreach>(
                        oldBreaches, newBreaches);
                if (evaluatePolicy(policy, oldBreaches, newBreaches)) {
                    IViolation violation = newViolation(agreement, term, policy, kpiName, breaches, now);

                    newViolations.add(violation);
                    violation.setBreaches(breaches);
                    logger.debug("Violation raised");
                }
            } 
            else {
                oldBreaches = Collections.emptyList();
                for (IMonitoringMetric breach : newBreachMetrics) {
                    IViolation violation = newViolation(agreement, term, kpiName, breach);
                    newViolations.add(violation);
                    logger.debug("Violation raised");
                }
            }
        }
        return newViolations;
    }

    private void checkInitialized() {
        
        if (breachRepository == null) {
            throw new NullPointerException("breachRepository is not set");
        }
        if (constraintEval == null) {
            throw new NullPointerException("constraintEval is not set");
        }
    }
    
    private void saveBreaches(List<IBreach> breaches) {
        
        breachRepository.saveBreaches(breaches);
    }

    /**
     * Builds a Violation from a list of breaches (for the case when the term has policies)
     */
    private IViolation newViolation(final IAgreement agreement, final IGuaranteeTerm term,
            final IPolicy policy, final String kpiName, final List<IBreach> breaches, final Date timestamp) {
        
        String actualValue = actualValueBuilder.fromBreaches(breaches);
        String expectedValue = null;
        
        IViolation v = newViolation(
                agreement, term, policy, kpiName, actualValue, expectedValue, timestamp);
        return v;
    }
    
    /**
     * Builds a Violation from metric breach (for the case when the term does not have policies)
     */
    private IViolation newViolation(final IAgreement agreement, final IGuaranteeTerm term,
            final String kpiName, IMonitoringMetric breach) {
        
        String actualValue = breach.getMetricValue();
        String expectedValue = null;
        
        /*
         * The policy is null, as the term has the default policy.
         */
        IViolation v = newViolation(
                agreement, term, null, kpiName, actualValue, expectedValue, breach.getDate());
        return v;
    }
    
    private Violation newViolation(final IAgreement contract, final IGuaranteeTerm term, 
            final IPolicy policy, final String kpiName, final String actualValue, 
            final String expectedValue, final Date timestamp) {

        Violation result = new Violation();
        result.setUuid(UUID.randomUUID().toString());
        result.setContractUuid(contract.getAgreementId());
        result.setKpiName(kpiName);
        result.setDatetime(timestamp);
        result.setExpectedValue(expectedValue);
        result.setActualValue(actualValue);
        result.setServiceName(term.getServiceName());
        result.setServiceScope(term.getServiceScope());
        result.setContractUuid(contract.getAgreementId());
        result.setPolicy(policy);
        
        return result;
    }
    
    private boolean evaluatePolicy(
            IPolicy policy, 
            List<IBreach> oldBreaches, 
            List<IBreach> newBreaches) {
        
        return oldBreaches.size() + newBreaches.size() >= policy.getCount();
    }
    
    /**
     * Return policies of the term if any, or the default policy.
     */
    private List<IPolicy> getPoliciesOrDefault(final IGuaranteeTerm term) {
        
        if (term.getPolicies() != null && term.getPolicies().size() > 0) {
            return term.getPolicies();
        }
        
        return Collections.singletonList(defaultPolicy);
    }
    
    /**
     * Checks if a list of breaches is only the default policy.
     */
    private boolean isDefaultPolicy(List<IPolicy> policies) {
        
        return policies.size() == 1 && isDefaultPolicy(policies.get(0));
    }
    
    private boolean isDefaultPolicy(IPolicy policy) {
        return policy.getCount() == 1;
    }
    
    public IConstraintEvaluator getConstraintEvaluator() {
        return constraintEval;
    }

    public void setConstraintEvaluator(IConstraintEvaluator constraintEval) {
        this.constraintEval = constraintEval;
    }

    public IBreachRepository getBreachRepository() {
        return breachRepository;
    }

    public void setBreachRepository(IBreachRepository breachRepository) {
        this.breachRepository = breachRepository;
    }

    /**
     * Unmodifiable List wrapper over 2 lists. 
     * 
     * There is no such wrapper in jdk. 
     * @see http://stackoverflow.com/a/13868352
     */
    public static class CompositeList<E> extends AbstractList<E> {

        private final List<E> list1;
        private final List<E> list2;

        public CompositeList(List<E> list1, List<E> list2) {
            this.list1 = list1;
            this.list2 = list2;
        }

        @Override
        public E get(int index) {
            if (index < list1.size()) {
                return list1.get(index);
            }
            return list2.get(index-list1.size());
        }

        @Override
        public int size() {
            return list1.size() + list2.size();
        }
    }

    /**
     * Constructs a list of Breach entities from a list of metrics that are considered breaches.
     */
    public static class BreachesFromMetricsBuilder {
        
        public List<IBreach> build(
                final List<IMonitoringMetric> metrics, 
                final IAgreement agreement, 
                final String kpiName) {
            
            List<IBreach> result = new ArrayList<IBreach>();
            for (IMonitoringMetric metric : metrics) {
                
                result.add(newBreach(agreement, metric, kpiName));
            }
            return result;
        }
        
        private IBreach newBreach(
                final IAgreement contract, 
                final IMonitoringMetric metric, 
                final String kpiName) {
            
            Breach breach = new Breach();
            breach.setDatetime(metric.getDate());
            breach.setKpiName(kpiName);
            breach.setValue(metric.getMetricValue());
            breach.setAgreementUuid(contract.getAgreementId());
            
            return breach;
        }
    }
    
    /**
     * Calculates the actual value of a breach. This value is not expected to be read by a computer, but by a human,
     * as it can be truncated.
     * 
     * This builder builds a comma separated list of the first three breach values.
     */
    public static class ActualValueBuilder {
        private static final int MAX_ACTUAL_VALUES = 3;
        private PoliciedServiceLevelEvaluator.BreachesFromMetricsBuilder breachesFromMetricsBuilder = new BreachesFromMetricsBuilder();
        
        public String fromBreaches(final List<IBreach> breaches) {
            
            StringBuilder str = new StringBuilder();
            String sep = "";
            for (int i = 0; i < breaches.size() && i < MAX_ACTUAL_VALUES; i++) {
                IBreach breach = breaches.get(i);
                str.append(sep);
                str.append(breach.getValue());
                
                sep = ",";
            }
            if (breaches.size() > MAX_ACTUAL_VALUES) {
                str.append("...");
            }
            return str.toString();
        }
        
        public String fromMetrics(
                final List<IMonitoringMetric> breachMetrics, IAgreement agreement, String kpiName) {

            List<IBreach> breaches = breachesFromMetricsBuilder.build(breachMetrics, agreement, kpiName);
            
            String result = fromBreaches(breaches);
            
            return result;
        }
    }
}