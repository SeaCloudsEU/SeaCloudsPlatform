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
package eu.atos.sla.dao;

import java.util.Date;
import java.util.List;

import eu.atos.sla.datamodel.ICompensation.IPenalty;

/**
 * DAO interface to access to the Penalty information
 * 
 */
public interface IPenaltyDAO {

    /**
     * Returns penalty from database by its id
     * @return existent penalty or null
     */
    public IPenalty getById(Long id);

    /**
     * Stores a Penalty into the database
     * 
     * @param penalty to save
     * 
     * @return the penalty stored in the database (id filled if was null in parameter)
     */
    public IPenalty save(IPenalty penalty);
    
    /**
     * Returns penalty from database by its uuid
     * @return existent penalty or null
     */
    public IPenalty getByUuid(String uuid);
    
    /**
     * Returns penalties applied to an agreement
     */
    public List<IPenalty> getByAgreement(String agreementId);
    
    public List<IPenalty> search(SearchParameters params);
    
    public static class SearchParameters {

        private String agreementId;
        private String guaranteeTermName;
        private Date begin;
        private Date end;
        
        public SearchParameters() {
        }
        
        public String getAgreementId() {
            return agreementId;
        }

        public String getGuaranteeTermName() {
            return guaranteeTermName;
        }

        public Date getBegin() {
            return begin;
        }

        public Date getEnd() {
            return end;
        }

        public void setAgreementId(String agreementId) {
            this.agreementId = agreementId;
        }

        public void setGuaranteeTermName(String guaranteeTermName) {
            this.guaranteeTermName = guaranteeTermName;
        }

        public void setBegin(Date begin) {
            this.begin = begin;
        }

        public void setEnd(Date end) {
            this.end = end;
        }
        
    }
    
}
