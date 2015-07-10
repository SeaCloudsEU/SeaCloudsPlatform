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

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.datamodel.IProvider;
import eu.atos.sla.datamodel.bean.Provider;
import eu.atos.sla.service.rest.helpers.exception.HelperException;
import eu.atos.sla.service.rest.helpers.exception.HelperException.Code;
import eu.atos.sla.util.IModelConverter;

/**
 * 
 */
@Deprecated
@Service
@Transactional
public class ProviderHelper {
    private static Logger logger = LoggerFactory.getLogger(ProviderHelper.class);

    @Autowired
    public IProviderDAO providerDAO;

    @Autowired
    private IModelConverter modelConverter;
    
    public ProviderHelper() {

    }



    private boolean doesProviderExistsInRepository(String uuid) {

        if (this.providerDAO.getByUUID(uuid) != null)
            return true;
        else
            return false;
    }

    private String printProviderToXML(IProvider provider) throws JAXBException {

        eu.atos.sla.parser.data.Provider providerXML = modelConverter.getProviderXML(provider);

        JAXBContext jaxbContext = JAXBContext
                .newInstance(eu.atos.sla.parser.data.Provider.class);
        Marshaller marshaller = jaxbContext.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(providerXML, out);

        return out.toString();
    }

    private String printProvidersToXML(List<IProvider> providers)
            throws JAXBException {
        StringBuilder xmlResponse = new StringBuilder();
        xmlResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlResponse.append("<collection href=\"/providers\">\n");
        xmlResponse.append("<items offset=\"0\" total=\"" + providers.size()
                + "\">\n");

        for (IProvider provider : providers) {
            xmlResponse.append(printProviderToXML(provider));

        }

        xmlResponse.append("</items>\n");
        xmlResponse.append("</collection>\n");

        return xmlResponse.toString();
    }

    public String getProviders() throws HelperException {
        logger.debug("StartOf getProviders");
        try{
            List<IProvider> providers = new ArrayList<IProvider>();
    
            providers = this.providerDAO.getAll();
    
            if (providers.size() != 0){
                String str = printProvidersToXML(providers);
                logger.debug("EndOf getEnforcements");
                return str;
            }else{
                logger.debug("EndOf getEnforcements");
                throw new HelperException(Code.DB_DELETED, "There are no providers in the SLA Repository Database");
            }
        } catch (JAXBException e) {
            logger.error("Error in getProviders " , e);
            throw new HelperException(Code.PARSER, "Error when parsing :" + e.getMessage() );
        }
            
    }

    
    public String getProviderByUUID(String uuid) throws HelperException {
        logger.debug("StartOf getProviderByUUID uuid:"+uuid);
        try{
            IProvider provider = new Provider();
    
            provider = this.providerDAO.getByUUID(uuid);
            String str = null;
            if (provider != null)
                str = printProviderToXML(provider);
            logger.debug("EndOf getProviderByUUID");
            return str;
        } catch (JAXBException e) {
            logger.error("Error in getEnforcementJobByUUID " , e);
            throw new HelperException(Code.PARSER, "Error when creating enforcementJob parsing file:" + e.getMessage() );
        }
    }


    
    public String createProvider(HttpHeaders hh, String uriInfo,
            String payload) throws HelperException {
        logger.debug("StartOf createProvider payload:"+payload);
        try {
            eu.atos.sla.parser.data.Provider providerXML = null;
    
            JAXBContext jaxbContext = JAXBContext.newInstance(eu.atos.sla.parser.data.Provider.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            providerXML = (eu.atos.sla.parser.data.Provider) jaxbUnmarshaller.unmarshal(new StringReader(payload));
    
            IProvider provider = new Provider();
            
            IProvider stored = new Provider();
    
            if (providerXML != null) {
    
                if (!doesProviderExistsInRepository(providerXML.getUuid())) {
                    provider = modelConverter.getProviderFromProviderXML(providerXML);
                    stored = providerDAO.save(provider);
                } else {
                    throw new HelperException(Code.DB_EXIST, "Provider with id:"
                            + providerXML.getUuid()
                            + " already exists in the SLA Repository Database");
                }
            }
    
            if (stored != null) {
                logger.debug("EndOf createProvider");
                String location = uriInfo + "/" + stored.getUuid();
                return location;
            } else
                logger.debug("EndOf createProvider");
                throw new HelperException(Code.INTERNAL, "Error when creating provider the SLA Repository Database");
        } catch (JAXBException e) {
            logger.error("Error in createProvider " , e);
            throw new HelperException(Code.PARSER, "Error when creating provider parsing file:" + e.getMessage() );
        }
    }
    
    
}
