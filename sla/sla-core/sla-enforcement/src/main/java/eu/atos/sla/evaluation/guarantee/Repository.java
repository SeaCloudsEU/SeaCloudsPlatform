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
package eu.atos.sla.evaluation.guarantee;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import eu.atos.sla.dao.IBreachDAO;
import eu.atos.sla.dao.IViolationDAO;
import eu.atos.sla.dao.IViolationDAO.SearchParameters;
import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IBreach;
import eu.atos.sla.datamodel.IViolation;

/**
 * Implements the access to a repository that stores breaches and compensations.
 *
 */
public class Repository implements IBreachRepository, IViolationRepository {

    @Autowired
    IBreachDAO breachDao;

    @Autowired
    IViolationDAO violationDao;
    
    public Repository() {
    }

    @Override
    public List<IBreach> getBreachesByTimeRange(IAgreement agreement, String kpiName, Date begin, Date end) {

        List<IBreach> result = breachDao.getByTimeRange(agreement, kpiName, begin, end);
        return result;
    }

    @Override
    public void saveBreaches(List<IBreach> breaches) {

        for (IBreach breach : breaches) {
        
            breachDao.save(breach);
        }
    }

    @Override
    public List<IViolation> getViolationsByTimeRange(IAgreement agreement,
            String guaranteeTermName, Date begin, Date end) {
        SearchParameters params = newSearchParameters(agreement, guaranteeTermName, begin, end);
        List<IViolation> result = violationDao.search(params);
        
        return result;
    }

    private SearchParameters newSearchParameters(IAgreement agreement,
            String guaranteeTermName, Date begin, Date end) {
        SearchParameters params = new SearchParameters();
        params.setAgreementId(agreement.getAgreementId());
        params.setGuaranteeTermName(guaranteeTermName);
        params.setBegin(begin);
        params.setEnd(end);
        return params;
    }
}
