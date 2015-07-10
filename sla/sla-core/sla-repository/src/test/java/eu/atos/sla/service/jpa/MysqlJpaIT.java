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
package eu.atos.sla.service.jpa;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IBreachDAO;
import eu.atos.sla.dao.IGuaranteeTermDAO;
import eu.atos.sla.dao.IPolicyDAO;
import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.dao.IViolationDAO;
import eu.atos.sla.datamodel.bean.Agreement;

public class MysqlJpaIT {

    @SuppressWarnings({ "resource", "unused" })
    public static void main(String args[]) throws InterruptedException {
        // Load Spring configuration
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "/sla-repository-db-JPA-test-context.xml");
        IAgreementDAO agreementDAO = (IAgreementDAO) context
                .getBean("AgreementService");
        IBreachDAO breachDAO = (IBreachDAO) context.getBean("BreachService");
        IGuaranteeTermDAO guaranteeTerm = (IGuaranteeTermDAO) context
                .getBean("GuaranteeTermService");

        IPolicyDAO slaPolicyDAO = (IPolicyDAO) context
                .getBean("SLAPolicyService");
        ITemplateDAO templateDAO = (ITemplateDAO) context
                .getBean("TemplateService");
        IViolationDAO violationDAO = (IViolationDAO) context
                .getBean("ViolationService");

        Agreement agreement = new Agreement();
        agreement.setId(1L);

        Agreement agreementSaved = null;

        try {
            agreementDAO.save(agreement);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        if (agreementSaved != null) {
            System.out.println("IMPOSIBLE TO SAVE AGREEMENT!!!!");
            Thread.sleep(600000l);
        }
    }
}
