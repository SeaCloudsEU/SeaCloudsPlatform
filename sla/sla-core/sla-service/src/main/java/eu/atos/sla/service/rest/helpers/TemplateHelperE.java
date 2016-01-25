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

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IAgreement.Context.ServiceProvider;
import eu.atos.sla.datamodel.IProvider;
import eu.atos.sla.datamodel.ITemplate;
import eu.atos.sla.parser.data.wsag.Template;
import eu.atos.sla.service.rest.helpers.exception.DBExistsHelperException;
import eu.atos.sla.service.rest.helpers.exception.DBMissingHelperException;
import eu.atos.sla.service.rest.helpers.exception.InternalHelperException;
import eu.atos.sla.service.rest.helpers.exception.ParserHelperException;
import eu.atos.sla.util.IModelConverter;
import eu.atos.sla.util.ModelConversionException;

/**
 * 
 */

@Service
@Transactional
public class TemplateHelperE  {
    private static Logger logger = LoggerFactory.getLogger(TemplateHelperE.class);

    @Autowired
    private ITemplateDAO templateDAO;

    @Autowired
    private IModelConverter modelConverter;

    @Autowired
    public IProviderDAO providerDAO;
    
    @Autowired
    private IAgreementDAO agreementDAO;
    
    public TemplateHelperE() {
    }

    public TemplateHelperE(ITemplateDAO templateDAO) {
        this.templateDAO = templateDAO;

    }

    private boolean doesTemplateIdExistInDatabase(String templateId) {
    if (this.templateDAO.getByUuid(templateId) != null)
            return true;
        else
            return false;
    }

    private IProvider getProviderPerUUIDFromDatabase(String providerUUID) {
        return providerDAO.getByUUID(providerUUID);
    }
    

    public List<ITemplate> getTemplates(String providerId, String []serviceIds){
        logger.debug("StartOf getTemplates providerId {}, serviceIds{}",providerId, serviceIds);
        List<ITemplate> templates = null;
        if ((serviceIds == null) && (providerId==null))
            templates = templateDAO.getAll();
        else
            templates = templateDAO.search(providerId, serviceIds);
        logger.debug("EndOf getTemplates");
        return templates;
    }

    
    public ITemplate getTemplateByUUID(String uuid) {
        logger.debug("StartOf getTemplateByUUID uuid:{}", uuid);
        ITemplate template = templateDAO.getByUuid(uuid);
        logger.debug("EndOf getTemplateByUUID" );
        return template;
    }


    public boolean deleteTemplateByUuid(String uuid) throws DBExistsHelperException  {
        logger.debug("StartOf deleteTemplateByUuid uuid:{}", uuid);

        boolean deleted = false;
        List<IAgreement> list = agreementDAO.getByTemplate(uuid);
        if (list.size() > 0) {
            throw new DBExistsHelperException(
                    "There are still agreements associated to this template, it cannot be removed");
        }
        
        ITemplate template = templateDAO.getByUuid(uuid);
    
        if (template != null) {
            deleted = templateDAO.delete(template);
        }
        logger.debug("EndOf deleteTemplateByUuid");
        return deleted;
    }


    
    public String createTemplate(Template templateXML, String originalSerializedTemplate)throws DBMissingHelperException, DBExistsHelperException, InternalHelperException, ParserHelperException {
        logger.debug("StartOf createTemplate payload:{}", originalSerializedTemplate);
        try {
            ITemplate templateStored = null;

            String serializedTemplate = Utils.removeXmlHeader(originalSerializedTemplate);
            if (templateXML != null) {
                // add field TemplateId if it doesn't exist
                if (templateXML.getTemplateId() == null) {
                    String templateId = UUID.randomUUID().toString();
                    logger.debug("createTemplate template has no uuid, {} will be assigned", templateId ); 
                    serializedTemplate = setTemplateIdInSerializedTemplate(serializedTemplate, templateId);    
                    templateXML.setTemplateId(templateId);
                }
    
                if (!doesTemplateIdExistInDatabase(templateXML.getTemplateId())) {
                    String providerUUID = null;
                    try {
                        ServiceProvider ctxProvider = ServiceProvider.fromString(templateXML.getContext().getServiceProvider());

                        switch (ctxProvider) {
                        case AGREEMENT_RESPONDER: providerUUID = templateXML.getContext().getAgreementResponder(); break;
                        case AGREEMENT_INITIATOR: providerUUID = templateXML.getContext().getAgreementInitiator(); break;
                        }
                    } catch (IllegalArgumentException e) {
                        throw new ModelConversionException("The Context/ServiceProvider field must match with the word "+ServiceProvider.AGREEMENT_RESPONDER+ " or "+ServiceProvider.AGREEMENT_INITIATOR);
                    }
                    IProvider provider = getProviderPerUUIDFromDatabase(providerUUID);
                    
                    if (provider==null) throw new DBMissingHelperException("Provider with UUID "+providerUUID+" doesn't exist in the database");

                    ITemplate template = modelConverter.getTemplateFromTemplateXML(templateXML, serializedTemplate);
                    logger.info("Template uuid is {} - Template id is {} - Template text is {}", 
                            template.getUuid(), template.getId(), template.getText());
                    templateStored = templateDAO.save(template);
                    provider.addTemplate(templateStored);
                    providerDAO.update(provider);
                } else {
                    throw new DBExistsHelperException("Element with id:"+ templateXML.getTemplateId()
                            + " already exists in the SLA Repository Database");
                }
            }
            if (templateStored != null) {
                logger.debug("EndOf createTemplate");
                return templateStored.getUuid();
            } else{
                logger.debug("EndOf createTemplate");
                throw new InternalHelperException("Error when creating the template in the SLA Repository Database");
            }
        } catch (ModelConversionException e) {
            logger.error("Error in createTemplate " , e);
            throw new ParserHelperException("Error when creating template, parsing file:" + e.getMessage() );
        }
            
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)    
    public ITemplate updateTemplate(String uuid, Template templateXML, String originalSerializedTemplate) 
            throws ParserHelperException, InternalHelperException, DBMissingHelperException, DBExistsHelperException {
        
        // TODO  correct method, check that everything is properly updated
        // provider can be incorrect and software doesn't return any error
        
        logger.debug("StartOf updateTemplate with uuid:{} payload:{}", uuid, originalSerializedTemplate);
        try{
            if (uuid==null) throw new InternalHelperException("TemplateUuid has not been informed, cannot do and update");

            if (templateXML.getTemplateId()==null) templateXML.setTemplateId(uuid); 
            ITemplate template = modelConverter.getTemplateFromTemplateXML(templateXML, originalSerializedTemplate);
            logger.debug("uuid: {} templateXML.getTemplateId():{} template.getUuid:{}", 
                    uuid, templateXML.getTemplateId(), template.getUuid());
            if (!template.getUuid().equals(uuid)) {
                throw new InternalHelperException(
                        "TemplateUuid in file has been informed and doesn't match with the parameter. "
                        + "Remove the one from the file or send the same.");
            }
            
            List<IAgreement> agreementList = agreementDAO.getByTemplate(uuid);
            if (agreementList != null){
                logger.debug("agreements list not null");
                if (agreementList.size() > 0) { 
                    throw new DBExistsHelperException("Template with "+uuid+" has agreements associated. It cannot be changed");
                }
            }
            
            String providerUUID = null;
            try {
                ServiceProvider ctxProvider = ServiceProvider.fromString(templateXML.getContext().getServiceProvider());

                switch (ctxProvider) {
                case AGREEMENT_RESPONDER: providerUUID = templateXML.getContext().getAgreementResponder(); break;
                case AGREEMENT_INITIATOR: providerUUID = templateXML.getContext().getAgreementInitiator(); break;
                }
            } catch (IllegalArgumentException e) {
                throw new ModelConversionException("The Context/ServiceProvider field must match with the word "+ServiceProvider.AGREEMENT_RESPONDER+ " or "+ServiceProvider.AGREEMENT_INITIATOR);
            }
            IProvider provider = getProviderPerUUIDFromDatabase(providerUUID);
            if (provider==null) throw new DBMissingHelperException("Provider with UUID "+providerUUID+" doesn't exist in the database");
            
        
            template.setUuid(uuid);
            boolean templateStored = templateDAO.update(uuid, template);
            ITemplate templateFromDatabase = null;
    
            if (templateStored) {
                templateFromDatabase = this.templateDAO.getByUuid(uuid);
            }
            provider.addTemplate(templateFromDatabase);            
            providerDAO.update(provider);
            
            logger.debug("EndOf updateTemplate");
            return templateFromDatabase;
            
        }catch (ModelConversionException e) {
            logger.error("Error in updateTemplate " , e);
            throw new ParserHelperException("Error when updating, parsing template:" + e.getMessage() );
        }
        
    }

    private String setTemplateIdInSerializedTemplate(String serializedTemplate, String templateId){
        return serializedTemplate.replaceAll(
                "<wsag:Template xmlns:wsag=\"http://www.ggf.org/namespaces/ws-agreement\" xmlns:sla=\"http://sla.atos.eu\">",
                "<wsag:Template xmlns:wsag=\"http://www.ggf.org/namespaces/ws-agreement\" xmlns:sla=\"http://sla.atos.eu\" wsag:TemplateId=\""+ templateId + "\">");
    }

}
