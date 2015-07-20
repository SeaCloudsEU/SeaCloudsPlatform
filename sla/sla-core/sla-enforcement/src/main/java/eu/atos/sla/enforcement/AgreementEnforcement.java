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
package eu.atos.sla.enforcement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.evaluation.AgreementEvaluator;
import eu.atos.sla.evaluation.constraint.IConstraintEvaluator;
import eu.atos.sla.evaluation.guarantee.GuaranteeTermEvaluator.GuaranteeTermEvaluationResult;
import eu.atos.sla.monitoring.IMetricsRetriever;
import eu.atos.sla.monitoring.IMetricsRetrieverV2;
import eu.atos.sla.monitoring.IMonitoringMetric;
import eu.atos.sla.notification.INotifierManager;



/**
 * 
 * Enforces an agreement and store the results in repository.
 * 
 * This class retrieves all the metrics prior to the evaluation start, at once if the metricsRetriever implements
 * IMetricsRetrieverV2 interface. If not, falls back to IMetricsRetriever, calling the monitoring once per metric type.
 * 
 * The needed properties to set are:
 * <li>agreementEvaluator: in memory evaluation of the agreement
 * <li>metricsRetriever: IMetricsRetriever implementer that retrieves from monitoring the new metrics to evaluate.
 * <li>constraintEvaluator: parse service levels and evaluates if new metrics fulfill them.
 * <li>maxRetrievedResults: maximum number of values for each metric to retrieve. It has a default value of 
 * <code>MAX_RETRIEVED_RESULTS</code>. 
 * 
 * 
 * <br/>Usage:
 * <pre>
 * <code>
 * AgreementEnforcement ae = new AgreementEnforcement();
 * ae.setAgreementEvaluator(...);
 * ae.setMetricsRetriever(...);
 * ae.setConstraintEvaluator(...);
 * ae.setMaxRetrievedResults(...);
 * 
 * ae.enforce(agreement, metrics);
 * </code>
 * </pre>
 * 
 * @see IAgreementEnforcement
 * @see IMetricsRetriever
 * @see IMetricsRetrieverV2
 * @see IAgreementEvaluator
 * @see IConstraintEvaluator
 * 
 */
public class AgreementEnforcement {
    private static Logger logger = LoggerFactory.getLogger(AgreementEnforcement.class);
    private static final String POLL_INTERVAL = "eu.atos.sla.enforcement.poll.interval.mseconds";

    /**
     * Default value for max metrics to retrieve.
     */
    private static final int MAX_RETRIEVED_RESULTS = 1000;

    @Autowired
    private IEnforcementService service;

    @Autowired
    INotifierManager notifierManager;

    private int maxRetrievedResults = MAX_RETRIEVED_RESULTS;
    private AgreementEvaluator agreementEvaluator;
    private IMetricsRetriever retriever;
    private IConstraintEvaluator constraintEval;

    @Value("ENF{" + POLL_INTERVAL + "}")
    private String pollIntervalString;
    private long pollInterval;
    
    
    public AgreementEnforcement() {
    }

    /**
     * Enforce an agreement that was last enforced at <code>since</code>. The enforcement process must retrieve
     * the metrics since that date and validate them, raising violations if applicable.
     * 
     * This method is intended to be used when the metric data has to be pulled from the Monitoring.
     * 
     * @param agreement Agreement to enforce.
     * @param since Last time the agreement was enforced.
     * @param isLastExecution the enforcement job has been stopped and it will be the last execution
     */
    public void enforce(IAgreement agreement, Date since, boolean isLastExecution) {
        logger.debug("enforce(agreement={},since={})", agreement.getAgreementId(), since);
        
        final Date now = new Date();
        
        checkInitialized(true);

        Map<IGuaranteeTerm, IMetricsRetrieverV2.RetrievalItem> retrievalItems = buildRetrievalItems(agreement, since, now, isLastExecution);

        Map<IGuaranteeTerm, List<IMonitoringMetric>> metricsMap;
        if (retriever instanceof IMetricsRetrieverV2) {
            metricsMap = getMetrics((IMetricsRetrieverV2)retriever, agreement, retrievalItems);
        }
        else {
            metricsMap = getMetrics(retriever, agreement, retrievalItems);
        }
        enforce(agreement, metricsMap);
    }

    /**
     * Enforce an agreement given the new metrics that has occurred since the last enforcement.
     * 
     * This method is intended to be called when the Monitoring pushes the metrics to be checked.
     * 
     * @param agreement Agreement to enforce.
     * @param metricsMap new metrics to evaluate.
     */
    public void enforce(IAgreement agreement, Map<IGuaranteeTerm, List<IMonitoringMetric>> metricsMap) {
        logger.debug("enforce(agreement={}) ", agreement.getAgreementId());

        checkInitialized(false);
        
        Map<IGuaranteeTerm, GuaranteeTermEvaluationResult> evaluationResult = 
                agreementEvaluator.evaluate(agreement, metricsMap);
        
        service.saveEnforcementResult(agreement, evaluationResult);
        
        notifierManager.addToBeNotified(agreement, evaluationResult);
    }
    
    /**
     * Enforce an agreement when violations are provided by an external smart monitoring.
     * 
     * This method is intended to be used when Monitoring pushes the raised violations.
     */
    public void enforceBusiness(IAgreement agreement,
            Map<IGuaranteeTerm, List<IViolation>> violationsMap) {
        
        logger.debug("enforceBusiness(agreement={})", agreement.getAgreementId());
        checkInitialized(false);
        
        Map<IGuaranteeTerm, GuaranteeTermEvaluationResult> evaluationResult = 
                agreementEvaluator.evaluateBusiness(agreement, violationsMap);
        
        service.saveEnforcementResult(agreement, evaluationResult);
        
        notifierManager.addToBeNotified(agreement, evaluationResult);
    }
    
    private void checkInitialized(boolean checkRetriever) {
        if (agreementEvaluator == null) {
            throw new NullPointerException("agreementEvaluator has not been set");
        }
        if (checkRetriever && constraintEval == null) {
            throw new NullPointerException("constraintEval has not been set");
        }
        if (checkRetriever && retriever == null) {
            throw new NullPointerException("retriever has not been set");
        }
    }
    
    //it is possible to specify the period a guarantee term has to be evaluated.
    //this period is a multiple from the pollInterval.
    //with this method it is obtained if the guarantee term as to be evaluated or not. 
    private boolean guaranteeTermHasToBeEvaluated(IGuaranteeTerm term, boolean isLastExecution){
        boolean hasToBeRetrieved = false;
        if (isLastExecution){
            if (term.getSamplingPeriodFactor()!=null){ 
                hasToBeRetrieved = (term.getSamplingPeriodFactor()== IGuaranteeTerm.ENFORCED_AT_END);
            }
        }else{
            if (term.getSamplingPeriodFactor()!=null){
                if ((term.getLastSampledDate()==null) && (term.getSamplingPeriodFactor()!= IGuaranteeTerm.ENFORCED_AT_END)){
                    hasToBeRetrieved = true;
                }else{
                    if (term.getSamplingPeriodFactor()!= IGuaranteeTerm.ENFORCED_AT_END){
                        long timeToWait = term.getSamplingPeriodFactor() * pollInterval;
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.MILLISECOND, -((int)(timeToWait)));
                        hasToBeRetrieved = (calendar.getTimeInMillis() > term.getLastSampledDate().getTime());
                    }
                }
            }else{
                hasToBeRetrieved = true;
            }
        }
        logger.debug("term.getKpiName() "+term.getKpiName()+" - term.getSamplingPeriodFactor:"+term.getSamplingPeriodFactor()+ " - term.getLastSampledDate():"+term.getLastSampledDate()+ " - hasToBeRetrieved:"+hasToBeRetrieved);
        return hasToBeRetrieved;
    }

    
    private Map<IGuaranteeTerm, IMetricsRetrieverV2.RetrievalItem> buildRetrievalItems(
            IAgreement agreement,
            final Date since,
            final Date now, boolean isLastExecution) {
        try {
            pollInterval = Long.parseLong(pollIntervalString);
        }catch(NumberFormatException npe){
            String str = String.format("AgreementEnforcement Can not parse ENF{%s} value{%s}. Is it a number?",    POLL_INTERVAL, pollIntervalString); 
            throw new IllegalArgumentException(str);
        }

        logger.debug("buildRetrievalItems(agreement:"+agreement.getAgreementId()+" - since:"+iso8601(since)+"+ now "+iso8601(now)+")");
        
        Map<IGuaranteeTerm, IMetricsRetrieverV2.RetrievalItem> retrievalItems = 
                    new HashMap<IGuaranteeTerm, IMetricsRetrieverV2.RetrievalItem>();
        
        for (final IGuaranteeTerm term : agreement.getGuaranteeTerms()) {
            if (guaranteeTermHasToBeEvaluated(term, isLastExecution)){
                service.saveCheckedGuaranteeTerm(term);

                retrievalItems.put(term, new IMetricsRetrieverV2.RetrievalItem() {
                    
                    @Override
                    public IGuaranteeTerm getGuaranteeTerm() {
                        return term;
                    }
                    
                    @Override
                    public String getVariable() {
                        return constraintEval.getConstraintVariable(term.getServiceLevel());
                    }
                    
                    @Override
                    public Date getEnd() {
                        return now;
                    }
                    
                    @Override
                    public Date getBegin() {
                        return since;
                    }
    
                });
            }

        }
        return retrievalItems;
    }

    private Map<IGuaranteeTerm, List<IMonitoringMetric>> getMetrics(
            IMetricsRetriever retriever, 
            IAgreement agreement, 
            Map<IGuaranteeTerm, IMetricsRetrieverV2.RetrievalItem> retrievalItems) {
        logger.debug("getMetrics");
        
        Map<IGuaranteeTerm, List<IMonitoringMetric>> result = new HashMap<IGuaranteeTerm, List<IMonitoringMetric>>();
        for (IGuaranteeTerm term : agreement.getGuaranteeTerms()) {
            IMetricsRetrieverV2.RetrievalItem item = retrievalItems.get(term);
            if (item!=null){
                List<IMonitoringMetric> metrics = retriever.getMetrics(
                        agreement.getAgreementId(), 
                        term.getServiceScope(), 
                        item.getVariable(),
                        item.getBegin(), 
                        item.getEnd(),
                        maxRetrievedResults);
                
                result.put(term, metrics);
            }
        }
        return result;
    }

    private Map<IGuaranteeTerm, List<IMonitoringMetric>> getMetrics(
            IMetricsRetrieverV2 retriever, 
            IAgreement agreement, 
            Map<IGuaranteeTerm, IMetricsRetrieverV2.RetrievalItem> retrievalItems) {
        logger.debug("getMetrics(IMetricsRetrieverV2)");
        
        return retriever.getMetrics(agreement.getAgreementId(), asList(retrievalItems.values()), maxRetrievedResults);
    }
    
    private <E> List<E> asList(Collection<E> coll) {
        
        if (coll instanceof List) {
            return (List<E>) coll;
        }
        return new ArrayList<E>(coll);
    }

    // variable set by configuration file
    public void setAgreementEvaluator(AgreementEvaluator agreementEval) {
        this.agreementEvaluator = agreementEval;
    }

    // variable set by configuration file
    public void setMetricsRetriever(IMetricsRetriever retriever) {
        this.retriever = retriever;
    }

    // variable set by configuration file
    public void setMaxRetrievedResults(int maxRetrievedResults) {
        this.maxRetrievedResults = maxRetrievedResults;
    }

    // variable set by configuration file
    public void setConstraintEvaluator(IConstraintEvaluator constraintEval) {
        this.constraintEval = constraintEval;
    }

    private String iso8601(Date date) {
        
        return (date!=null) ? String.format("%tFT%<tTZ", date):"null";
    }
}