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
package eu.atos.sla.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;




//import org.codehaus.jackson.JsonNode;
//import org.codehaus.jackson.JsonProcessingException;
//import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IAgreement.Context.ServiceProvider;
import eu.atos.sla.datamodel.ICompensation.IPenalty;
import eu.atos.sla.datamodel.IEnforcementJob;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.IProvider;
import eu.atos.sla.datamodel.IServiceProperties;
import eu.atos.sla.datamodel.ITemplate;
import eu.atos.sla.datamodel.IVariable;
import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.datamodel.bean.Agreement;
import eu.atos.sla.datamodel.bean.Template;
import eu.atos.sla.parser.ParserException;
import eu.atos.sla.parser.data.EnforcementJob;
import eu.atos.sla.parser.data.Penalty;
import eu.atos.sla.parser.data.Provider;
import eu.atos.sla.parser.data.Violation;
import eu.atos.sla.parser.data.wsag.Context;
import eu.atos.sla.parser.data.wsag.GuaranteeTerm;
import eu.atos.sla.parser.data.wsag.ServiceLevelObjective;
import eu.atos.sla.parser.data.wsag.ServiceProperties;
import eu.atos.sla.parser.data.wsag.ServiceScope;
import eu.atos.sla.parser.data.wsag.Variable;
import eu.atos.sla.parser.xml.AgreementParser;

@Component
public class ModelConversion implements IModelConverter {
    @Autowired
    private IProviderDAO providerDAO;
    
    private BusinessValueListParser businessValueListParser;
    
    private static Logger logger = LoggerFactory.getLogger(ModelConversion.class);
    
    
    /* (non-Javadoc)
     * @see eu.atos.sla.util.IModelConversion#getAgreementFromAgreementXML(eu.atos.sla.datamodel.parser.xml.agreement.Agreement, java.lang.String)
     */
    @Override
    public IAgreement getAgreementFromAgreementXML(
            eu.atos.sla.parser.data.wsag.Agreement agreementXML,
            String payload) throws ModelConversionException {

        IAgreement agreement = new Agreement();

        // AgreementId
        if (agreementXML.getAgreementId() != null) {
            agreement.setAgreementId(agreementXML.getAgreementId());
        }

        // Context
        Context context = agreementXML.getContext();
        

        try {
            ServiceProvider ctxProvider = ServiceProvider.fromString(context.getServiceProvider());

            switch (ctxProvider) {
            case AGREEMENT_RESPONDER:
                setProviderAndConsumer(agreement,
                        context.getAgreementResponder(),
                        context.getAgreementInitiator());
                break;
            case AGREEMENT_INITIATOR:
                setProviderAndConsumer(agreement,
                        context.getAgreementInitiator(),
                        context.getAgreementResponder());
                break;
            }
        } catch (IllegalArgumentException e) {
            throw new ModelConversionException("The Context/ServiceProvider field must match with the word "+ServiceProvider.AGREEMENT_RESPONDER+ " or "+ServiceProvider.AGREEMENT_INITIATOR);
        }

        if (context.getTemplateId() != null) {
            eu.atos.sla.datamodel.bean.Template template = new eu.atos.sla.datamodel.bean.Template();
            template.setUuid(context.getTemplateId());
            agreement.setTemplate(template);
        }
        
        if (context.getService() != null) {
            
            agreement.setServiceId(context.getService());
        }else{
            throw new ModelConversionException("Service is null, field must be informed");
        }

        if (context.getExpirationTime() != null) {

            agreement.setExpirationDate(context.getExpirationTime());
        }

        // ServiceProperties

        List<IServiceProperties> servicePropertiesList = new ArrayList<IServiceProperties>();

        List<ServiceProperties> servicePropertiesListXML = agreementXML
                .getTerms().getAllTerms().getServiceProperties();
        if (servicePropertiesListXML == null) {
            servicePropertiesListXML = Collections
                    .<ServiceProperties> emptyList();
        }

        for (ServiceProperties servicePropertiesXML : servicePropertiesListXML) {

            IServiceProperties serviceProperties = new eu.atos.sla.datamodel.bean.ServiceProperties();

            if (servicePropertiesXML.getName() != null) {
                serviceProperties.setName(servicePropertiesXML.getName());
            }

            if (servicePropertiesXML.getServiceName() != null) {
                serviceProperties.setServiceName(servicePropertiesXML.getServiceName());
            }

            if (servicePropertiesXML != null) {
                serviceProperties.setServiceName(servicePropertiesXML.getServiceName());
            }

            // VariableSet
            if (servicePropertiesXML.getVariableSet() != null) {

                List<IVariable> variables = new ArrayList<IVariable>();
                List<Variable> variablesXML = servicePropertiesXML.getVariableSet().getVariables();
                if (variablesXML != null) {

                    for (Variable variableXML : variablesXML) {

                        IVariable variable = new eu.atos.sla.datamodel.bean.Variable();
                        logger.debug("Variable with name:{} -  location:{} - metric:{}", 
                                variableXML.getName(), variableXML.getLocation(), variableXML.getMetric());
                        if (variableXML.getLocation() != null) {
                            variable.setLocation(variableXML.getLocation());
                        }
                        if (variableXML.getMetric() != null) {
                            variable.setMetric(variableXML.getMetric());
                        }
                        if (variableXML.getName() != null) {
                            variable.setName(variableXML.getName());
                        }

                        variables.add(variable);

                    }
                    serviceProperties.setVariableSet(variables);
                }
            }
            servicePropertiesList.add(serviceProperties);
        }

        agreement.setServiceProperties(servicePropertiesList);
        agreement.setName(agreementXML.getName());

        // GuaranteeTerms
        List<IGuaranteeTerm> guaranteeTerms = new ArrayList<IGuaranteeTerm>();

        List<GuaranteeTerm> guaranteeTermsXML = agreementXML.getTerms()
                .getAllTerms().getGuaranteeTerms();

        if (guaranteeTermsXML == null) {
            guaranteeTermsXML = Collections.<GuaranteeTerm> emptyList();
        }

        for (GuaranteeTerm guaranteeTermXML : guaranteeTermsXML) {

            IGuaranteeTerm guaranteeTerm = new eu.atos.sla.datamodel.bean.GuaranteeTerm();

            if (guaranteeTermXML.getName() != null) {
                guaranteeTerm.setName(guaranteeTermXML.getName());
            }

            ServiceScope scope = guaranteeTermXML.getServiceScope();
            if (scope != null) {
                logger.debug("guaranteeTerm with name:{} -  servicescopeName:{} - servicescopeValue:{}", 
                        guaranteeTermXML.getName(), scope.getServiceName(), scope.getValue());
                guaranteeTerm.setServiceScope(scope.getValue());
                guaranteeTerm.setServiceName( scope.getServiceName());
            }else
                logger.debug("guaranteeTerm with name:{} - serviceScope is null", guaranteeTermXML.getName() );

            // qualifying condition
            if (guaranteeTermXML.getQualifyingCondition()!= null){
                logger.debug("qualifying condition informed with:{}", guaranteeTermXML.getQualifyingCondition());
                String qc = guaranteeTermXML.getQualifyingCondition();
                if (qc != null) {
                    QualifyingConditionParser.Result parsedQc = QualifyingConditionParser.parse(qc);
                    guaranteeTerm.setSamplingPeriodFactor(parsedQc.getSamplingPeriodFactor());
                    if (parsedQc.getSamplingPeriodFactor() == IGuaranteeTerm.ENFORCED_AT_END) {
                        agreement.setHasGTermToBeEvaluatedAtEndOfEnformcement(true);
                    }
                }
            }
            /*
             * Parse SLO and BusinessValues
             */
            ServiceLevelObjective slo = guaranteeTermXML.getServiceLevelObjetive();
            if (slo.getKpitarget() != null) {
                if (slo.getKpitarget().getKpiName() != null) {
                    guaranteeTerm.setKpiName(slo.getKpitarget().getKpiName());
                    String csl = slo.getKpitarget().getCustomServiceLevel();
                    logger.debug("guaranteeTerm with kpiname:{} --  getCustomServiceLevel: ", 
                            slo.getKpitarget().getKpiName(), csl);
                    if (csl != null) {
                        logger.debug("CustomServiceLevel not null"); 
                        ServiceLevelParser.Result parsedSlo = ServiceLevelParser.parse(csl);
                        guaranteeTerm.setServiceLevel(parsedSlo.getConstraint());
                    }else{
                        logger.debug("CustomServiceLevel is null"); 
                    }
                }
            }
            guaranteeTerm.setBusinessValueList(businessValueListParser.parse(guaranteeTermXML));
            
            guaranteeTerms.add(guaranteeTerm);
        }

        agreement.setGuaranteeTerms(guaranteeTerms);

        // Text
        agreement.setText(payload);

        return agreement;
    }

    // we retrieve the providerUUID from the template and get the provider object from the database 
    private IProvider getProviderFromTemplate(eu.atos.sla.parser.data.wsag.Template templateXML) throws ModelConversionException {
        
        Context context = templateXML.getContext();
        
        String provider = null;
        try {
            ServiceProvider ctxProvider = ServiceProvider.fromString(context.getServiceProvider());
             
            switch (ctxProvider) {
            case AGREEMENT_RESPONDER:
                provider= context.getAgreementResponder();
                break;
            case AGREEMENT_INITIATOR:
                provider= context.getAgreementInitiator();
                break;
            }
        } catch (IllegalArgumentException e) {
            throw new ModelConversionException("The Context/ServiceProvider field must match with the word "+ServiceProvider.AGREEMENT_RESPONDER+ " or "+ServiceProvider.AGREEMENT_INITIATOR);
        }
        IProvider providerObj = providerDAO.getByUUID(provider);
        return providerObj;
    }
    
    /* (non-Javadoc)
     * @see eu.atos.sla.util.IModelConversion#getTemplateFromTemplateXML(eu.atos.sla.datamodel.parser.xml.agreement.Template, java.lang.String)
     */
    @Override
    public ITemplate getTemplateFromTemplateXML(eu.atos.sla.parser.data.wsag.Template templateXML,    String payload) throws ModelConversionException{
        ITemplate template = new Template();
        if (templateXML.getTemplateId() != null) {
            logger.debug("TemplateId at header will be used:{}", templateXML.getTemplateId());
            template.setUuid(templateXML.getTemplateId());
        } else {
            // uuid
            if (templateXML.getContext().getTemplateId() != null) {
                logger.debug("TemplateId in context will be used:{}", templateXML.getTemplateId());
                template.setUuid(templateXML.getContext().getTemplateId());
            }else{
                String templateId = UUID.randomUUID().toString();
                template.setUuid(templateId);
            }
        }
        
        if (templateXML.getContext().getService()!=null){
            template.setServiceId(templateXML.getContext().getService());
        }else{
            logger.error("Service is null, field must be informed");            
            throw new ModelConversionException("Service is null, field must be informed");
        }
        
        // Text
        template.setText(payload);
        // Name
        template.setName(templateXML.getName());
        template.setProvider(getProviderFromTemplate(templateXML));
        return template;
    }

    
    @Override
    public Context getContextFromAgreement(IAgreement agreement) throws ModelConversionException {
        Context context = null;
        try{
            if (agreement.getText()!=null){
                AgreementParser agreementParser = new AgreementParser();
                eu.atos.sla.parser.data.wsag.Agreement agreememtXML = (eu.atos.sla.parser.data.wsag.Agreement)agreementParser.getWsagObject(agreement.getText());
                context = agreememtXML.getContext();
            }else{
                throw new ModelConversionException("Serialized agreement is null, could not be parsed");
            }
        }catch(ParserException e){
            throw new ModelConversionException("Error parsing agreement "+agreement.getAgreementId()+" stored in database");                
        }

        return context;
    }

    
    
    /* (non-Javadoc)
     * @see eu.atos.sla.util.IModelConversion#getEnforcementJobFromEnforcementJobXML(eu.atos.sla.datamodel.parser.xml.EnforcementJob, java.lang.String)
     */
    @Override
    public IEnforcementJob getEnforcementJobFromEnforcementJobXML(
            EnforcementJob enforcementJobXML) throws ModelConversionException{

        IEnforcementJob enforcementJob = new eu.atos.sla.datamodel.bean.EnforcementJob();
        Agreement agreement = null;

        if (enforcementJobXML.getAgreementId() != null) {

            agreement = new Agreement(enforcementJobXML.getAgreementId());
            enforcementJob.setAgreement(agreement);
        } else
            throw new ModelConversionException("AgreementId is null, field must be informed");


        enforcementJob.setEnabled(enforcementJobXML.getEnabled());

        if (enforcementJobXML.getLastExecuted() != null) {
            enforcementJob.setLastExecuted(enforcementJobXML.getLastExecuted());
        }

        return enforcementJob;
    }

    /* (non-Javadoc)
     * @see eu.atos.sla.util.IModelConversion#getProviderFromProviderXML(eu.atos.sla.datamodel.parser.xml.Provider)
     */
    @Override
    public IProvider getProviderFromProviderXML(eu.atos.sla.parser.data.Provider providerXML) {

        IProvider provider = new eu.atos.sla.datamodel.bean.Provider();

        if (providerXML.getUuid() != null) {

            provider.setUuid(providerXML.getUuid());
        } else {

            provider.setUuid(UUID.randomUUID().toString());
        }

        if (providerXML.getName() != null) {
            provider.setName(providerXML.getName());
        }

        return provider;
    }


    @Override
    public Provider getProviderXML(IProvider provider) {

        Provider providerXML = new Provider();

        // providerXML.setId(provider.getId());
        providerXML.setUuid(provider.getUuid());
        providerXML.setName(provider.getName());

        return providerXML;
    }

    @Override
    public Violation getViolationXML(IViolation violation) {

        Violation violationXML = new Violation();
        if (violation == null) return null;
        // if (violation.getId() != null)
        // violationXML.setId(violation.getId());
        if (violation.getUuid() != null)
            violationXML.setUuid(violation.getUuid());
        if (violation.getContractUuid() != null)
            violationXML.setContractUuid(violation.getContractUuid());
        if (violation.getServiceName() != null)
            violationXML.setServiceName(violation.getServiceName());
        if (violation.getServiceScope() != null)
            violationXML.setServiceScope(violation.getServiceScope());
        if (violation.getKpiName() != null)
            violationXML.setKpiName(violation.getKpiName());
        if (violation.getDatetime() != null)
            violationXML.setDatetime(violation.getDatetime());
        if (violation.getExpectedValue() != null)
            violationXML.setExpectedValue(violation.getExpectedValue());
        if (violation.getActualValue() != null)
            violationXML.setActualValue(violation.getActualValue());

        return violationXML;
    }

    /* (non-Javadoc)
     * @see eu.atos.sla.util.IModelConversion#getEnforcementJobXML(eu.atos.sla.api.datamodel.IEnforcementJob)
     */
    @Override
    public EnforcementJob getEnforcementJobXML(
            IEnforcementJob enforcementJob) {

        EnforcementJob enforcementJobXML = new EnforcementJob();

        enforcementJobXML.setAgreementId(enforcementJob.getAgreement().getAgreementId());
        enforcementJobXML.setEnabled(enforcementJob.getEnabled());
        enforcementJobXML.setLastExecuted(enforcementJob.getLastExecuted());

        return enforcementJobXML;
    }
    
    @Override
    public Penalty getPenaltyXML(IPenalty penalty) {
        
        return new Penalty(penalty);
    }

    public static class ServiceLevelParser {

        public static class Result {
            String constraint;
            
            protected String getConstraint() {
                return constraint;
            }
        }
        
        protected static Result parse(String serviceLevel) throws ModelConversionException {
            ObjectMapper mapper = new ObjectMapper();
            
            String constraint = null;
            JsonNode rootNode = null;
            try {
                rootNode = mapper.readTree(serviceLevel);
                JsonNode constraintNode = rootNode.path("constraint");
                
                constraint = textOrJson(constraintNode);

                if (constraint==null) throw new ModelConversionException(serviceLevel+" didn't contain the constraint keyword");
                Result result = new Result();
                result.constraint = constraint;
                
                return result;
            } catch (JsonProcessingException e) {
                logger.error("Error parsing "+serviceLevel, e);
                throw new ModelConversionException("Error parsing "+serviceLevel+ " message:"+ e.getMessage());
            } catch (IOException e) {
                logger.error("Error parsing "+serviceLevel, e);
                throw new ModelConversionException("Error parsing "+serviceLevel+ " message:"+ e.getMessage());
            }
        }

        /**
         * Returns the text value of a node or its inner string representation.
         * 
         * textOrJson( "constraint" : "performance < 10" ) -> "performance < 10"
         * textOrJson( "constraint" : { "hasMaxValue": 10 } ) -> "{\"hasMaxValue\": 10}"
         */
        private static String textOrJson(JsonNode constraintNode) {
            String constraint = null;
            
            if (!constraintNode.isMissingNode()) {
                constraint = constraintNode.textValue();
                if (constraint == null) {
                    constraint = constraintNode.toString();
                }
            }
            return constraint;
        }
        
    }

    public static class QualifyingConditionParser {
        static private final String AT_END = "AT_END";
        static private final String SCHEDULEx = "SCHEDULEx";
        public static class Result {
            int samplingperiodFactor;
            
            protected int getSamplingPeriodFactor() {
                return samplingperiodFactor;
            }
        }
        
        protected static Result parse(String qualifyingCondition) throws ModelConversionException {
            ObjectMapper mapper = new ObjectMapper();
            
            JsonNode rootNode = null;
            try {
                rootNode = mapper.readTree(qualifyingCondition);
                JsonNode samplingperiodNode = rootNode.path("samplingperiodfactor");
                logger.debug("samplingperiodNode: "+samplingperiodNode);
                
                String samplingperiodfactor = textOrJson(samplingperiodNode);

                if (samplingperiodfactor==null) throw new ModelConversionException(qualifyingCondition+" didn't contain the samplingperiodfactor keyword");
                Result result = new Result();
                if ((samplingperiodfactor.startsWith(SCHEDULEx)) || (samplingperiodfactor.startsWith(AT_END))){
                    if (samplingperiodfactor.startsWith(SCHEDULEx)){ 
                        try{
                            result.samplingperiodFactor = Integer.valueOf(samplingperiodfactor.substring(SCHEDULEx.length()).trim());
                        }catch (NumberFormatException e){
                            throw new ModelConversionException(qualifyingCondition+" "+SCHEDULEx+" must be followed by a decimal");
                        }
                    }
                    if (samplingperiodfactor.startsWith(AT_END)){ 
                        result.samplingperiodFactor = IGuaranteeTerm.ENFORCED_AT_END;
                    }
                }else
                    throw new ModelConversionException(qualifyingCondition+" must be a multiple from schedule or be executed at the end. Make sure the value starts with "+SCHEDULEx+" or has the word "+AT_END);
                
                return result;
            } catch (JsonProcessingException e) {
                logger.error("Error parsing "+qualifyingCondition, e);
                throw new ModelConversionException("Error parsing "+qualifyingCondition+ " message:"+ e.getMessage());
            } catch (IOException e) {
                logger.error("Error parsing "+qualifyingCondition, e);
                throw new ModelConversionException("Error parsing "+qualifyingCondition+ " message:"+ e.getMessage());
            }
        }

        
        private static String textOrJson(JsonNode samplingperiodNode) {
            String value = null;
            
            if (!samplingperiodNode.isMissingNode()) {
                value = samplingperiodNode.textValue();
                if (value == null) {
                    value = samplingperiodNode.toString();
                }
            }
            return value;
        }
        
    }
    
    private void setProviderAndConsumer(IAgreement agreement, String provider, String consumer) {
        logger.info("setProviderAndConsumer provider:{} - consumer:{}", provider, consumer);

        if (consumer != null) {
            agreement.setConsumer(consumer);
        }
        if (provider != null) {
            eu.atos.sla.datamodel.bean.Provider providerObj = new eu.atos.sla.datamodel.bean.Provider();
            providerObj.setUuid(provider);
            agreement.setProvider(providerObj);
        }
    }

    public void setBusinessValueListParser(
            BusinessValueListParser customBusinessValueParser) {
        this.businessValueListParser = customBusinessValueParser;
    }
}
