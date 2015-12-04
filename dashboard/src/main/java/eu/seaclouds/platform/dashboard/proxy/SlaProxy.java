/*
 * Copyright 2014 SeaClouds
 * Contact: dev@seaclouds-project.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.seaclouds.platform.dashboard.proxy;


import eu.atos.sla.parser.data.GuaranteeTermsStatus;
import eu.atos.sla.parser.data.Violation;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.GuaranteeTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.List;

public class SlaProxy extends AbstractProxy {
    private static final Logger LOG = LoggerFactory.getLogger(SlaProxy.class);
    private ObjectMapper mapper;
    
    public SlaProxy() {
        mapper = new ObjectMapper();
    }
    /**
     * Creates proxied HTTP POST request to SeaClouds SLA core which installs a set of SLA Agreements
     * paired with the corresponding Monitoring Rules Monitoring Rules
     * @param slaAgreement SLA Agreement to install
     * @return String representing that the Agreement was installed properly
     */
    public String addAgreement(Agreement slaAgreement) {
        Entity content = Entity.entity(slaAgreement, MediaType.APPLICATION_JSON);

        Invocation invocation = getJerseyClient().target(getEndpoint() + "/seaclouds/agreements").request()
                .header("Accept", MediaType.APPLICATION_JSON)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .buildPost(content);

        //SLA Core returns a text message if the response was succesfully not the object, this is not the best behaviour
        return invocation.invoke().readEntity(String.class);
    }


    /**
     * Creates proxied HTTP DELETE request to SeaClouds SLA core which removes the SLA from the SLA Core
     * @param agreementId of the SLA Agreement to be removed. This ID may differ from SeaClouds Application ID
     * @return String representing that the Agreement was removed  properly
     */
    public String removeAgreement(String agreementId) {
        Invocation invocation = getJerseyClient().target(getEndpoint() + "/agreements/" + agreementId).request()
                .header("Accept", MediaType.APPLICATION_JSON)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .buildDelete();

        //SLA Core returns a text message if the response was succesfully not the object, this is not the best behaviour
        return invocation.invoke().readEntity(String.class);
    }

    /**
     * Creates proxied HTTP POST request to SeaClouds SLA core which notifies that the Monitoring Rules were installed
     * in Tower4Clouds. @see Issue #56
     * @return String representing that the SLA was notified properly
     */
    public String notifyRulesReady(Agreement slaAgreement) {
        Entity content = Entity.entity("", MediaType.TEXT_PLAIN);
        Invocation invocation = getJerseyClient().target(getEndpoint() + "/seaclouds/commands/rulesready?agreementId=" + slaAgreement.getAgreementId()).request()
                .header("Accept", MediaType.APPLICATION_JSON)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .buildPost(content);

        //SLA Core returns a text message if the response was succesfully not the object, this is not the best behaviour
        return invocation.invoke().readEntity(String.class);
    }

    /**
     * Creates proxied HTTP GET request to SeaClouds SLA core which retrieves the Agreement details
     * @param agreementId of the desired agreement. This ID may differ from SeaClouds Application ID
     * @return the Agreement
     */
    public Agreement getAgreement(String agreementId) {
        return getJerseyClient().target(getEndpoint() + "/agreements/" + agreementId).request()
                .header("Accept", MediaType.APPLICATION_JSON)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .buildGet().invoke().readEntity(Agreement.class);
    }


    /**
     * Creates proxied HTTP GET request to SeaClouds SLA which returns the Agreement according to the template id
     * @param slaAgreementTemplateId SLA Agreement template ID
     * @return the Agreement
     */
    public Agreement getAgreementByTemplateId(String slaAgreementTemplateId) {
        return getJerseyClient().target(getEndpoint() + "/seaclouds/commands/fromtemplate?templateId=" + slaAgreementTemplateId).request()
                .header("Accept", MediaType.APPLICATION_JSON)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .buildGet().invoke().readEntity(Agreement.class);
    }


    /**
     * Creates proxied HTTP GET request to SeaClouds SLA core which retrieves the Agreement Status
     * @param agreement to fetch the status
     * @return the GuaranteeTermsStatus
     */
    public GuaranteeTermsStatus getAgreementStatus(Agreement agreement) {
        return getAgreementStatus(agreement.getAgreementId());
    }

    /**
     * Creates proxied HTTP GET request to SeaClouds SLA core which retrieves the Agreement Status
     * @param agreementId to fetch the status
     * @return the GuaranteeTermsStatus
     */
    public GuaranteeTermsStatus getAgreementStatus(String agreementId) {
        return getJerseyClient().target(getEndpoint() + "/agreements/" + agreementId + "/guaranteestatus").request()
                .header("Accept", MediaType.APPLICATION_JSON)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .buildGet().invoke().readEntity(GuaranteeTermsStatus.class);
    }

    /**
     * Creates proxied HTTP GET request to SeaClouds SLA core which retrieves the Agreement Term Violations
     * @param agreement which contains the guaranteeTerm to fetch
     * @param guaranteeTerm to check violations
     * @return the list of Violations for this <Agreement, GuaranteeTerm> pair
     */
    public List<Violation> getGuaranteeTermViolations(Agreement agreement, GuaranteeTerm guaranteeTerm) {
        String json = getJerseyClient().target(getEndpoint() + "/violations?agreementId=" + agreement.getAgreementId() + "&guaranteeTerm=" + guaranteeTerm.getName()).request()
                .header("Accept", MediaType.APPLICATION_JSON)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .buildGet().invoke().readEntity(String.class);
        try {
            return mapper.readValue(json, new TypeReference<List<Violation>>(){});
        } catch (IOException e) {
            /*
             * TODO: Change Runtime for a DashboardException
             */
            throw new RuntimeException(e);
        }
    }
}
