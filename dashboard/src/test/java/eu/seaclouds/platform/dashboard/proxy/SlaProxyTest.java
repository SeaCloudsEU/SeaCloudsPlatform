/*
 *  Copyright 2014 SeaClouds
 *  Contact: SeaClouds
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package eu.seaclouds.platform.dashboard.proxy;

import com.squareup.okhttp.mockwebserver.MockResponse;
import eu.atos.sla.parser.data.GuaranteeTermsStatus;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.GuaranteeTerm;
import eu.seaclouds.platform.dashboard.util.ObjectMapperHelpers;
import eu.seaclouds.platform.dashboard.utils.TestFixtures;
import eu.seaclouds.platform.dashboard.utils.TestUtils;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.util.UUID;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class SlaProxyTest extends AbstractProxyTest<SlaProxy> {
    private final String RANDOM_STRING = UUID.randomUUID().toString();

     //TODO: Add JSON based tests

    @Override
    public SlaProxy getProxy() {
        return getSupport().getConfiguration().getSlaProxy();
    }

    @Test
    public void testAddAgreement() throws Exception {
        String json = TestUtils.getStringFromPath(TestFixtures.AGREEMENT_PATH_JSON);

        getMockWebServer().enqueue(new MockResponse()
                        .setHeader("Accept", MediaType.APPLICATION_JSON)
                        .setHeader("Content-Type", MediaType.APPLICATION_JSON)
        );

        assertNotNull(getProxy().addAgreement(ObjectMapperHelpers.JsonToObject(json, Agreement.class)));
    }

    @Test
    public void testRemoveAgreement() throws Exception {
        getMockWebServer().enqueue(new MockResponse()
                        .setHeader("Accept", MediaType.APPLICATION_JSON)
                        .setHeader("Content-Type", MediaType.APPLICATION_JSON)
        );

        assertNotNull(getProxy().removeAgreement(RANDOM_STRING));
    }

    @Test
    public void testNotifyRulesReady() throws Exception {
        String json = TestUtils.getStringFromPath(TestFixtures.AGREEMENT_PATH_JSON);

        getMockWebServer().enqueue(new MockResponse()
                        .setHeader("Accept", MediaType.APPLICATION_JSON)
                        .setHeader("Content-Type", MediaType.APPLICATION_JSON)
        );

        assertNotNull(getProxy().notifyRulesReady(ObjectMapperHelpers.JsonToObject(json, Agreement.class)));
    }

    @Test
    public void testGetAgreement() throws Exception {
        String json = TestUtils.getStringFromPath(TestFixtures.AGREEMENT_PATH_JSON);

        getMockWebServer().enqueue(new MockResponse()
                        .setBody(json)
                        .setHeader("Accept", MediaType.APPLICATION_JSON)
                        .setHeader("Content-Type", MediaType.APPLICATION_JSON)
        );

        Agreement response = getProxy().getAgreement(RANDOM_STRING);

        // Agreement doesn't implement equals(), so we are going to check the IDs
        Agreement fixture = ObjectMapperHelpers.JsonToObject(json, Agreement.class);
        assertEquals(response.getAgreementId(), fixture.getAgreementId());
    }

    @Test
    public void testGetAgreementByTemplateId() throws Exception {
        String json = TestUtils.getStringFromPath(TestFixtures.AGREEMENT_PATH_JSON);

        getMockWebServer().enqueue(new MockResponse()
                        .setBody(json)
                        .setHeader("Accept", MediaType.APPLICATION_JSON)
                        .setHeader("Content-Type", MediaType.APPLICATION_JSON)
        );

        Agreement response = getProxy().getAgreementByTemplateId(RANDOM_STRING);

        // Agreement doesn't implement equals(), so we are going to check the IDs
        Agreement fixture = ObjectMapperHelpers.JsonToObject(json, Agreement.class);
        assertEquals(response.getAgreementId(), fixture.getAgreementId());
    }

    @Test
    public void testGetAgreementStatus() throws Exception {
        String termStatusJson = TestUtils.getStringFromPath(TestFixtures.AGREEMENT_STATUS_PATH_JSON);
        String agreementJson = TestUtils.getStringFromPath(TestFixtures.AGREEMENT_PATH_JSON);

        Agreement agreement = ObjectMapperHelpers.JsonToObject(agreementJson, Agreement.class);

        getMockWebServer().enqueue(new MockResponse()
                        .setBody(termStatusJson)
                        .setHeader("Accept", MediaType.APPLICATION_JSON)
                        .setHeader("Content-Type", MediaType.APPLICATION_JSON)
        );

        // GuaranteeTermsStatus doesn't implement equals(), so we are going to check the IDs
        GuaranteeTermsStatus response = getProxy().getAgreementStatus(agreement);
        GuaranteeTermsStatus fixture = ObjectMapperHelpers.JsonToObjectJackson2(termStatusJson, GuaranteeTermsStatus.class);
        assertEquals(response.getAgreementId(), fixture.getAgreementId());
    }

    @Test
    public void testGetAgreementViolations() throws Exception {
        String violationJson =  TestUtils.getStringFromPath(TestFixtures.VIOLATIONS_JSON_PATH);

        String agreementJson = TestUtils.getStringFromPath(TestFixtures.AGREEMENT_PATH_JSON);
        Agreement agreement = ObjectMapperHelpers.JsonToObject(agreementJson, Agreement.class);

        for(int i = 0; i < agreement.getTerms().getAllTerms().getGuaranteeTerms().size(); i++){
            getMockWebServer().enqueue(new MockResponse()
                            .setBody(violationJson)
                            .setHeader("Accept", MediaType.APPLICATION_JSON)
                            .setHeader("Content-Type", MediaType.APPLICATION_JSON)
            );
        }


        // GuaranteeTerm doesn't implement equals(), so we are going to check not null
        for(GuaranteeTerm term : agreement.getTerms().getAllTerms().getGuaranteeTerms()){
            assertNotNull(getProxy().getGuaranteeTermViolations(agreement, term));
        }

    }

}
