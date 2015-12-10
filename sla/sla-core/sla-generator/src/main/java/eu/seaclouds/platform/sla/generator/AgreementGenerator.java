/**
 * Copyright 2015 Atos
 * Contact: Seaclouds
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
package eu.seaclouds.platform.sla.generator;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.AllTerms;
import eu.atos.sla.parser.data.wsag.Context;
import eu.atos.sla.parser.data.wsag.GuaranteeTerm;
import eu.atos.sla.parser.data.wsag.ServiceDescriptionTerm;
import eu.atos.sla.parser.data.wsag.ServiceProperties;
import eu.atos.sla.parser.data.wsag.Template;
import eu.atos.sla.parser.data.wsag.Terms;

public class AgreementGenerator {

    private Template template;
    private ContextInfo ctx;
    
    public AgreementGenerator(Template template) {
        
        this.template = template;
        Context c = template.getContext();
        this.ctx = new ContextInfo(
                c.getAgreementResponder(), 
                c.getAgreementInitiator(), 
                c.getService(),
                "P1Y");
    }
    public AgreementGenerator(Template template, ContextInfo ctx) {
        this.template = template;
        this.ctx = ctx;
    }
    
    public Agreement generate() {
        String agreementId = UUID.randomUUID().toString();
        
        Agreement result = generate(agreementId);
        return result;
    }
    
    public Agreement generate(String agreementId) {
        Agreement agreement = new Agreement();
        
        agreement.setAgreementId(agreementId);
        
        String name = String.format("%s-%s-%s", ctx.getConsumer(), ctx.getProvider(), ctx.getService());
        agreement.setName(name);
        
        agreement.setContext(generateContext());
        agreement.setTerms(generateTerms());
        
        return agreement;
    }
    
    private Context generateContext() {
        
        Context result = new Context();
        result.setAgreementInitiator(ctx.getConsumer());
        result.setAgreementResponder(ctx.getProvider());
        result.setServiceProvider(Context.ServiceProvider.AGREEMENT_RESPONDER.toString());
        result.setExpirationTime(ctx.getValidity().add(new Date()));
        result.setTemplateId(template.getTemplateId());
        result.setService(ctx.getService());
        
        return result;
    }

    private Terms generateTerms() {
        Terms result = new Terms();
        AllTerms all = new AllTerms();
        result.setAllTerms(all);
        
        all.setServiceDescriptionTerm(generateServiceDescriptionTerms());
        all.setServiceProperties(generateServiceProperties());
        all.setGuaranteeTerms(generateGuaranteeTerms());
        
        return result;
    }
    
    private ServiceDescriptionTerm generateServiceDescriptionTerms() {
        return template.getTerms().getAllTerms().getServiceDescriptionTerm();
    }
    
    private List<ServiceProperties> generateServiceProperties() {
        return template.getTerms().getAllTerms().getServiceProperties();
    }
    
    private List<GuaranteeTerm> generateGuaranteeTerms() {
        return template.getTerms().getAllTerms().getGuaranteeTerms();
    }

}
