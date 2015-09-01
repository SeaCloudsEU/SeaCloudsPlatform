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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IProvider;
import eu.atos.sla.datamodel.ITemplate;
import eu.atos.sla.datamodel.IAgreement.Context.ServiceProvider;
import eu.atos.sla.datamodel.bean.Template;
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
public class TemplateHelper  {
    private static Logger logger = LoggerFactory.getLogger(TemplateHelper.class);

    @Autowired
    private ITemplateDAO templateDAO;

    @Autowired
    private IModelConverter modelConverter;

    @Autowired
    public IProviderDAO providerDAO;
    
    @Autowired
    private IAgreementDAO agreementDAO;
    
    public TemplateHelper() {
    }

    public TemplateHelper(ITemplateDAO templateDAO) {
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
    
    private Template getTemplateForDB(
            eu.atos.sla.parser.data.wsag.Template templateXML,
            String payload) {
        Template templateDB = new Template();

        if (templateXML.getTemplateId() != null) {
            templateDB.setUuid(templateXML.getTemplateId());
            logger.debug("templateId:"+ templateDB.getUuid());
        }

        if (payload != null) {
            templateDB.setText(payload);
        }

        return templateDB;
    }

    private String printTemplateToXML(ITemplate template) {

        StringBuilder xmlResponse = new StringBuilder();
        xmlResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

        xmlResponse.append(template.getText());

        return xmlResponse.toString();
    }

    private String printTemplatesToXML(List<ITemplate> templates){
        StringBuilder xmlResponse = new StringBuilder();
        xmlResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlResponse.append("<collection href=\"/templates\">\n");
        xmlResponse.append("<items offset=\"0\" total=\"" + templates.size()
                + "\">\n");

        for (ITemplate template : templates) {
            xmlResponse.append(template.getText());

        }

        xmlResponse.append("</items>\n");
        xmlResponse.append("</collection>\n");

        return xmlResponse.toString();
    }

    public String getTemplates()  throws HelperException{
        logger.debug("StartOf getTemplates");
        List<ITemplate> templates = new ArrayList<ITemplate>();
    
        templates = this.templateDAO.getAll();
    
        if (templates.size() != 0){
            String str = printTemplatesToXML(templates); 
            logger.debug("EndOf getTemplates");
            return str;
        }else{
            logger.debug("EndOf getTemplates");
            throw new HelperException(Code.DB_DELETED, "There are no templates in the SLA Repository Database");
        }

    }

    public String getTemplateByUUID(String uuid) throws HelperException {
        try{
            logger.debug("StartOf getTemplateByUUID uuid:"+uuid);
            ITemplate template = null;
            template = this.templateDAO.getByUuid(uuid);
            String str = null;
            if (template != null) str = printTemplateToXML(template);
            logger.debug("EndOf getProviderByUUID");
            return str;
        } catch (Throwable e) {
            logger.error("Error in getTemplateByUUID " , e);
            throw new HelperException(Code.INTERNAL, "Error when getting template:" + e.getMessage() );
        }

    }



    public boolean deleteTemplateByUuid(String uuid) throws HelperException  {
        logger.debug("StartOf deleteTemplateByUuid uuid:"+uuid);

        boolean deleted = false;
        try{
            List<IAgreement> list = agreementDAO.getByTemplate(uuid);
            if (list.size()>0) throw new HelperException(Code.DB_EXIST, "There are still agreements associated to this template, it cannot be removed");
            ITemplate template = this.templateDAO.getByUuid(uuid);
    
            if (template != null) {
                deleted = this.templateDAO.delete(template);
            }
    
            logger.debug("EndOf deleteTemplateByUuid");
            return deleted;
        } catch (Throwable e) {
            logger.error("Error in deleteTemplateByUuid " , e);
            throw new HelperException(Code.INTERNAL, "Error when deleting template:" + e.getMessage() );
        }

    }

    public String createTemplate(HttpHeaders hh, String uriInfo, String payload)throws HelperException {
        logger.debug("StartOf createTemplate payload:"+payload);
        try {
            JAXBContext jaxbContext = JAXBContext
                    .newInstance(eu.atos.sla.parser.data.wsag.Template.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            eu.atos.sla.parser.data.wsag.Template templateXML = (eu.atos.sla.parser.data.wsag.Template) jaxbUnmarshaller
                    .unmarshal(new StringReader(payload));
    
            ITemplate template = new Template();
            String location = null;
            ITemplate templateStored = null;
    
            if (templateXML != null) {
    
                // remove xml header
                payload = payload.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
    
                // add field TemplateId if it doesn't exist
                if (templateXML.getTemplateId() == null) {
                    String templateId = UUID.randomUUID().toString();
                    payload = payload
                            .replaceAll(
                                    "\\<wsag\\:Template xmlns\\:wsag=\"http\\:\\/\\/www.ggf.org\\/namespaces\\/ws-agreement\"\\>",
                                    "\\<wsag\\:Template xmlns\\:wsag=\"http\\:\\/\\/www.ggf.org\\/namespaces\\/ws-agreement\" TemplateId=\""
                                            + templateId + "\"\\>").trim();
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
                    
                    if (provider==null) throw new HelperException(Code.DB_MISSING, "Provider with UUID "+providerUUID+" doesn't exist in the database");

                    template = modelConverter.getTemplateFromTemplateXML(
                            templateXML, payload);
                    logger.info("Template uuid is"+template.getUuid());
                    logger.info("Template id is"+template.getId());
                    logger.info("Template text is"+template.getText());
                    templateStored = templateDAO.save(template);
                    provider.addTemplate(templateStored);
                    providerDAO.update(provider);
                } else {
                    throw new HelperException(Code.DB_EXIST, "Element with id:"
                            + templateXML.getTemplateId()
                            + " already exists in the SLA Repository Database");
                }
            }
    
            if (templateStored != null) {
                location = uriInfo + "/" +  templateStored.getUuid();
                logger.debug("EndOf createTemplate");
                return location;
                
            } else
                throw new HelperException(Code.INTERNAL, "Error when creating the template in the SLA Repository Database");
        } catch (JAXBException e) {
            logger.error("Error in createTemplate " , e);
            throw new HelperException(Code.PARSER, "Error when creating template parsing file:" + e.getMessage() );
        } catch (Throwable e) {
            logger.error("Error in createTemplate " , e);
            throw new HelperException(Code.INTERNAL, "Error when creating template:" + e.getMessage() );
        }
            
    }

    public String updateTemplate(HttpHeaders hh, String payload) throws HelperException {
        logger.debug("StartOf updateTemplate payload:"+payload);
        try{
            JAXBContext context = JAXBContext.newInstance(Template.class);
            Unmarshaller unMarshaller = context.createUnmarshaller();
    
            eu.atos.sla.parser.data.wsag.Template templateXML = (eu.atos.sla.parser.data.wsag.Template) unMarshaller
                    .unmarshal(new StringReader(payload));
    
            Template template = getTemplateForDB(templateXML, payload);
    
            boolean templateStored = this.templateDAO.update(template.getUuid(), template);
            ITemplate templateFromDatabase = new Template();
    
            String str = null;
            if (templateStored) {
    
                templateFromDatabase = this.templateDAO.getByUuid(template
                        .getUuid());
                str = printTemplateToXML(templateFromDatabase); 
                 
            } 
            logger.debug("EndOf updateTemplate");
            return str;
            
        } catch (JAXBException e) {
            logger.error("Error in updateTemplate " , e);
            throw new HelperException(Code.PARSER, "Error when updating template parsing file:" + e.getMessage() );
        }catch (Throwable e) {
            logger.error("Error in updateTemplate " , e);
            throw new HelperException(Code.INTERNAL, "Error when updating template:" + e.getMessage() );
        }
        
    }

    
}
