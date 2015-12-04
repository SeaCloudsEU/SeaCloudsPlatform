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

package eu.seaclouds.platform.dashboard.rest;

import eu.atos.sla.parser.data.GuaranteeTermsStatus;
import eu.atos.sla.parser.data.Violation;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.GuaranteeTerm;
import eu.seaclouds.platform.dashboard.model.SeaCloudsApplicationData;
import eu.seaclouds.platform.dashboard.model.SeaCloudsApplicationDataStorage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class SlaResourceTest extends AbstractResourceTest<SlaResource>{
    SlaResource resource = new SlaResource(getSlaProxy());
    SeaCloudsApplicationData applicationData;

    @Override
    @BeforeMethod
    public void setUpMethod() throws Exception {
        super.setUpMethod();
        applicationData = new SeaCloudsApplicationData(getDam());
        applicationData.setAgreementId(getAgreement());
        SeaCloudsApplicationDataStorage.getInstance().addSeaCloudsApplicationData(applicationData);
    }


    @Test
    public void testGetAgreement() throws Exception {
        Agreement agreement = (Agreement) resource.getAgreement(applicationData.getSeaCloudsApplicationId()).getEntity();
        assertNotNull(agreement);
    }

    @Test
    public void testGetAgreementStatus() throws Exception {
        GuaranteeTermsStatus entity = (GuaranteeTermsStatus) resource.getAgreementStatus(applicationData.getSeaCloudsApplicationId()).getEntity();
        assertNotNull(entity);
    }

    @Test
    public void testGetViolations() throws Exception {
        Agreement agreement = (Agreement) resource.getAgreement(applicationData.getSeaCloudsApplicationId()).getEntity();

        for(GuaranteeTerm term :  agreement.getTerms().getAllTerms().getGuaranteeTerms()){
            List<Violation> entity = (List<Violation>) resource.getViolations(applicationData.getSeaCloudsApplicationId(), term.getName()).getEntity();
            assertNotNull(entity);
        }
    }


}