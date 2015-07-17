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

import java.util.ArrayList;
import java.util.List;

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
import eu.atos.sla.parser.data.Provider;
import eu.atos.sla.service.rest.helpers.exception.DBExistsHelperException;
import eu.atos.sla.service.rest.helpers.exception.HelperException;
import eu.atos.sla.service.rest.helpers.exception.InternalHelperException;
import eu.atos.sla.util.IModelConverter;

/**
 * 
 */

@Service
@Transactional
public class ProviderHelperE {
    private static Logger logger = LoggerFactory.getLogger(ProviderHelperE.class);

    @Autowired
    public IProviderDAO providerDAO;

    @Autowired
    private IModelConverter modelConverter;
    
    @Autowired
    private IAgreementDAO agreementDAO;

    @Autowired
    private ITemplateDAO templateDAO;

    public ProviderHelperE() {

    }

    private boolean doesProviderExistsInRepository(String uuid, String name) {
        boolean exitstsuuid = providerDAO.getByUUID(uuid) != null; 
        boolean exitstsname = providerDAO.getByName(name) != null; 
        return (exitstsuuid || exitstsname);
    }

    public List<Provider> getProviders() throws HelperException {
        logger.debug("StartOf getProviders");
        List<IProvider> storedProviders = providerDAO.getAll();
        List<Provider> providers = new ArrayList<Provider>(); 
        for (IProvider storedProvider:storedProviders) providers.add(modelConverter.getProviderXML(storedProvider));
        logger.debug("EndOf getProviders");
        return providers;
    }
    
    public Provider getProviderByUUID(String uuid)  {
        logger.debug("StartOf getProviderByUUID uuid:{}", uuid);
        Provider provider = null;
        IProvider storedProvider = providerDAO.getByUUID(uuid);
        if (storedProvider!=null)
            provider = modelConverter.getProviderXML(storedProvider);
        logger.debug("EndOf getProviderByUUID");
        return provider;    
    }
    
    
    public String createProvider(Provider providerXML) throws DBExistsHelperException, InternalHelperException {
        logger.debug("StartOf createProvider");
        IProvider stored = null;
        if (providerXML != null) {
            if (!doesProviderExistsInRepository(providerXML.getUuid(), providerXML.getName())) {
                IProvider provider = modelConverter.getProviderFromProviderXML(providerXML);
                stored = providerDAO.save(provider);
            } else {
                throw new DBExistsHelperException("Provider with id:"
                    + providerXML.getUuid() +" or name:"+providerXML.getName() 
                    + " already exists in the SLA Repository Database");
            }
        }
    
        if (stored != null) {
            logger.debug("EndOf createProvider");
            return stored.getUuid();
        } else
            logger.debug("EndOf createProvider");
        throw new InternalHelperException("Error when creating provider the SLA Repository Database");
    }
    
    
    public boolean deleteByProviderUUID(String providerUUID) throws DBExistsHelperException{
        logger.debug("StartOf deleteByProviderUUID providerUUID:{}", providerUUID);
        
        boolean deleted = false;
        List<ITemplate> listTemplates = templateDAO.search(providerUUID, null);
        if ((listTemplates==null) || listTemplates.size()>0){ 
            logger.debug("Templates exists associated to providerUUID {}, provider will not be removed", providerUUID);
            throw new DBExistsHelperException("Template with provider uuid:"
                    + providerUUID +" exists in the SLA Repository Database. Provider cannot be removed.");
        }else{
            List<IAgreement> listAgreements = agreementDAO.getByProvider(providerUUID);
            if ((listAgreements==null) || listAgreements.size()>0){ 
                logger.debug("Agreements exists associated to providerUUID {}, provider will not be removed", 
                        providerUUID);
                throw new DBExistsHelperException("Agreements with provider uuid:"
                        + providerUUID +" exists in the SLA Repository Database. Provider cannot be removed.");
            }else{
                IProvider provider = providerDAO.getByUUID(providerUUID);
                if (provider!=null)
                    deleted = this.providerDAO.delete(provider);
            }
        }

        logger.debug("EndOf deleteByProviderUUID");
        return deleted;
    }

}
