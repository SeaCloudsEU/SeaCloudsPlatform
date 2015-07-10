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
package eu.atos.sla.service.rest.helpers;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.HttpHeaders;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IEnforcementJobDAO;
import eu.atos.sla.dao.IGuaranteeTermDAO;
import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IEnforcementJob;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.IGuaranteeTerm.GuaranteeTermStatusEnum;
import eu.atos.sla.datamodel.IProvider;
import eu.atos.sla.datamodel.ITemplate;
import eu.atos.sla.datamodel.bean.Agreement;
import eu.atos.sla.parser.data.GuaranteeTermStatus;
import eu.atos.sla.parser.data.GuaranteeTermsStatus;
import eu.atos.sla.service.rest.helpers.exception.HelperException;
import eu.atos.sla.service.rest.helpers.exception.HelperException.Code;
import eu.atos.sla.util.IModelConverter;
import eu.atos.sla.util.ModelConversionException;


/**
 * 
 */
@Deprecated
@Service
@Transactional
public class AgreementHelper{
    private static Logger logger = LoggerFactory.getLogger(AgreementHelper.class);

    @Autowired
    private IAgreementDAO agreementDAO;

    @Autowired
    private IGuaranteeTermDAO guaranteeTermDAO;

    @Autowired
    private IProviderDAO providerDAO;

    @Autowired
    private ITemplateDAO templateDAO;

    @Autowired
    private IModelConverter modelConverter;
    
    @Autowired
    private IEnforcementJobDAO enforcementJobDAO;


    private Utils<GuaranteeTermsStatus> utils;

    public AgreementHelper() {
        this.utils = new Utils<GuaranteeTermsStatus>();
    }

    private boolean doesAgreementIdExistInRepository(String agreementId) {

        if (this.agreementDAO.getByAgreementId(agreementId) != null)
            return true;
        else
            return false;
    }

    public String createAgreement(HttpHeaders hh, String uriInfo, String payload) throws HelperException {
        logger.debug("StartOf createAgreement payload:"+payload);
        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(eu.atos.sla.parser.data.wsag.Agreement.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            eu.atos.sla.parser.data.wsag.Agreement agreementXML = (eu.atos.sla.parser.data.wsag.Agreement) jaxbUnmarshaller
                    .unmarshal(new StringReader(payload));
    
            String location = null;
            IAgreement agreement = new Agreement();
            IAgreement agreementStored = null;
    
            if (agreementXML != null) {
    
                // remove xml header
                payload = payload.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
                String agreementId = null;
    
                // add field AggrementId if it doesn't exist
                if (agreementXML.getAgreementId() == null) {
    
                    agreementId = UUID.randomUUID().toString();
    
                    payload = payload
                            .replaceAll(
                                    "\\<wsag\\:Agreement xmlns\\:wsag=\"http\\:\\/\\/www.ggf.org\\/namespaces\\/ws-agreement\"\\>",
                                    "\\<wsag\\:Agreement xmlns\\:wsag=\"http\\:\\/\\/www.ggf.org\\/namespaces\\/ws-agreement\" AgreementId=\""
                                            + agreementId + "\"\\>").trim();
                    agreementXML.setAgreementId(agreementId);
    
                }
    
                if (!doesAgreementIdExistInRepository(agreementXML.getAgreementId())) {
                    agreement = modelConverter.getAgreementFromAgreementXML(agreementXML, payload);
    
                    String providerUuid = agreement.getProvider().getUuid();
                    IProvider provider = providerDAO.getByUUID(providerUuid);
    
                    if (provider == null) {
                        throw new HelperException(Code.DB_MISSING, "Proivder with id:"
                                + providerUuid
                                + " doesn't exist SLA Repository Database");
                    }
                    agreement.setProvider(provider);
    
                    String templateUuid = agreement.getTemplate().getUuid();
    
                    if (templateUuid != null) {
                        ITemplate template = templateDAO.getByUuid(templateUuid);
                        if (template == null) {
                            throw new HelperException(Code.DB_MISSING, "Template with id:"
                                    + templateUuid
                                    + " doesn't exist SLA Repository Database");
                        }
                        agreement.setTemplate(template);
                    }
    
                    agreementStored = this.agreementDAO.save(agreement);
    
                } else {
                    throw new HelperException(Code.DB_EXIST, "Agreement with id:"
                            + agreementXML.getAgreementId()
                            + " already exists in the SLA Repository Database");
                }
            }
    
            if (agreementStored != null) {
                location = uriInfo + "/" + agreementStored.getAgreementId();
                logger.debug("EndOf createAgreement");
                return location;
            } else{
                logger.debug("EndOf createAgreement");
                throw new HelperException(Code.INTERNAL, "Error when creating agreement the SLA Repository Database");
            }
        } catch (JAXBException e) {
            logger.error("Error in createAgreement " , e);
            throw new HelperException(Code.PARSER, "Error when creating agreement parsing file:" + e.getMessage() );
        } catch (ModelConversionException e) {
            logger.error("Error in createAgreement " , e);
            throw new HelperException(Code.PARSER, "Error when creating:" + e.getMessage() );
        } catch (Throwable e) {
            logger.error("Error in createAgreement " , e);
            throw new HelperException(Code.PARSER, "Error when creating:" + e.getMessage() );
        }

    }


    private String printAgreementToXML(IAgreement agreement){
        StringBuilder xmlResponse = new StringBuilder();
        xmlResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

        xmlResponse.append(agreement.getText());

        return xmlResponse.toString();
    }

    private String printAgreementsToXML(List<IAgreement> agreements){
        StringBuilder xmlResponse = new StringBuilder();
        xmlResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xmlResponse.append("<collection href=\"/agreements\">");
        xmlResponse.append("<items offset=\"0\" total=\"" + agreements.size()
                + "\">\n");

        for (IAgreement agreement : agreements) {
            xmlResponse.append(agreement.getText());
        }

        xmlResponse.append("</items>");
        xmlResponse.append("</collection>");

        return xmlResponse.toString();
    }


    private GuaranteeTermsStatus getGuaranteeStatus(String agreementId,
            List<IGuaranteeTerm> guaranteeTerms) {

        logger.debug("Getting the list of guaranteeTerms from the database");
        logger.debug("Found " + guaranteeTerms.size() + " in the database");

        // Pojo GuaranteeTermsStatus
        GuaranteeTermsStatus guaranteeTermsStatus = new GuaranteeTermsStatus();

        // Pojo GuaranteeTermStatus
        GuaranteeTermStatus guaranteeTermStatus;

        // Array GuaranteeTermStatus
        List<GuaranteeTermStatus> guaranteeTermStatusList = new ArrayList<GuaranteeTermStatus>();

        // Status GuaranteTerm
        GuaranteeTermStatusEnum agreementStatus;
        agreementStatus = getAgreementStatus(guaranteeTerms);

        guaranteeTermsStatus.setAgreementId(agreementId);
        guaranteeTermsStatus.setValue(agreementStatus.toString());

        // Fill GuaranteeTermsStatus pojo

        for (IGuaranteeTerm guaranteeTerm : guaranteeTerms) {

            guaranteeTermStatus = new GuaranteeTermStatus();

            guaranteeTermStatus.setName(guaranteeTerm.getName());
            guaranteeTermStatus.setValue(guaranteeTerm.getStatus().toString());
            guaranteeTermStatusList.add(guaranteeTermStatus);
        }

        guaranteeTermsStatus.setGuaranteeTermsStatus(guaranteeTermStatusList);

        return guaranteeTermsStatus;
    }

    private GuaranteeTermStatusEnum getAgreementStatus(
            List<IGuaranteeTerm> guaranteeTerms) {

        GuaranteeTermStatusEnum result = GuaranteeTermStatusEnum.FULFILLED;

        if (guaranteeTerms.size() == 0) {
            result = GuaranteeTermStatusEnum.NON_DETERMINED;
        } else {
            result = GuaranteeTermStatusEnum.FULFILLED;

            for (IGuaranteeTerm guaranteeTerm : guaranteeTerms) {

                GuaranteeTermStatusEnum termStatus = guaranteeTerm.getStatus();
                if (termStatus == null
                        || termStatus == GuaranteeTermStatusEnum.NON_DETERMINED) {
                    result = GuaranteeTermStatusEnum.NON_DETERMINED;
                } else if (termStatus == GuaranteeTermStatusEnum.VIOLATED) {
                    result = GuaranteeTermStatusEnum.VIOLATED;
                }
            }
        }
        return result;
    }

    public String getAgreements(String consumerId, String providerId, Boolean active) throws HelperException{
        logger.debug("StartOf getAgreements consumerId:"+consumerId+ " - providerId:"+providerId+ " - active:"+active);
        
        List<IAgreement> agreements = new ArrayList<IAgreement>();
        agreements = this.agreementDAO.search(consumerId, providerId, null, active);
        String str = printAgreementsToXML(agreements);
        logger.debug("EndOf getAgreements");
        return str;
    }


    public String getAgreementByID(String id) throws HelperException {
        logger.debug("StartOf getAgreementByID id:"+id);
        IAgreement agreement = null;

        agreement = this.agreementDAO.getByAgreementId(id);

        if (agreement != null){
            String str = printAgreementToXML(agreement);
            logger.debug("EndOf getAgreementByID");
            return str;
        }else
            throw new HelperException(Code.DB_MISSING, "There is no agreement with id:  "
                    + id);

    }


    
    public String getActiveAgreements(long actualDate) throws HelperException {
        logger.debug("StartOf getActiveAgreements actualDate:"+actualDate);
        List<IAgreement> agreements = null;
        

        agreements = this.agreementDAO.getByActiveAgreements(actualDate);

        if (agreements != null){
            String str = printAgreementsToXML(agreements);
            logger.debug("EndOf getActiveAgreements");
            return str;
        }else
            throw new HelperException(Code.DB_MISSING, "There are no active agreements:"+actualDate);
        
    }

    public boolean deleteByAgreementId(String agreementId) {
        logger.debug("StartOf deleteByAgreementId agreementId:"+agreementId);

        boolean deleted = false;
        IEnforcementJob enforcementJob = enforcementJobDAO.getByAgreementId(agreementId);
        if (enforcementJob!=null){
            logger.debug("EnforcementJob exists associated to agreementId "+agreementId+", it will be stopped and removed");
            enforcementJobDAO.delete(enforcementJob);
        }
        
        IAgreement agreement = this.agreementDAO.getByAgreementId(agreementId);

        if (agreement != null) {
            deleted = this.agreementDAO.delete(agreement);
        }

        logger.debug("EndOf deleteByAgreementId");
        return deleted;
    }

    public String getAgreementStatus(String id, String resultType) throws HelperException{
        logger.debug("StartOf getAgreementStatus id:"+id+ " - resultType:"+resultType);

        List<IGuaranteeTerm> guaranteeTerms = new ArrayList<IGuaranteeTerm>();

        IAgreement agreement = new Agreement();

        agreement = this.agreementDAO.getByAgreementId(id);

        if (agreement == null)
            throw new HelperException(Code.DB_MISSING, "The agreementId " + id + " doesn't exist");

        guaranteeTerms = agreement.getGuaranteeTerms();
        String guaranteeStatusResult = null;
        GuaranteeTermsStatus guaranteeStatus = new GuaranteeTermsStatus();

        try {

            guaranteeStatus = getGuaranteeStatus(id, guaranteeTerms);

            if (resultType.equals(MediaType.APPLICATION_XML.toString())) {
                guaranteeStatusResult = utils.parseToXML(guaranteeStatus, GuaranteeTermsStatus.class);
            } else { /* fallback to json */
                guaranteeStatusResult = utils.parseToJson(guaranteeStatus);
            }

            return guaranteeStatusResult;
        } catch (JAXBException e) {
            throw new HelperException(Code.PARSER, "Error obtaining GuaranteeTermStatus from agreementId: "
                    + id + ".Error: " + e.getMessage());
            
        }
        
        

    }
}
