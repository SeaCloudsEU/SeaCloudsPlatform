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

import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.datamodel.bean.Violation;

/**
 * DAO interface to access to the Violation information
 * 
 * 
 */
public interface IViolationDAO {

    /**
     * Returns the Violation from the database by its Id
     * 
     * @param id
     *            of the Violation
     * @return the corresponding Violation from the database
     */
    public Violation getById(Long id);

    /**
     * Returns the Violation from the database by its uuid
     * 
     * @param uuid
     *            of the Violation
     * @return the corresponding Violation from the database
     */
    public IViolation getViolationByUUID(String uuid);

    /**
     * Returns the Violations from the database by its contractUuid in a range
     * of dates. The violations are sorted descendent by datetime.
     * 
     * @param contractUuid
     *            of the Violation
     * @param termName
     *            of the Violation
     * @param begin
     *            of the Violation
     * @param end
     *            of the Violation
     * @return the corresponding Violation from the database
     */
    public List<IViolation> getByAgreementIdInARangeOfDates(
            String contractUuid, String termName, Date begin, Date end);

        
    /**
     * Returns the Violations from the database by its contractUuid. 
     * The violations are sorted descendent by datetime.
     * 
     * @param contractUuid
     *            of the Violation
     * @param termName
     *            of the Violation
     * @return the corresponding Violation from the database
     */

    public List<IViolation> getByAgreement(String contractUuid,
            String termName);
    
    /**
     * Returns all the Violation stored in the database.
     * 
     * @return all the Violation stored in the database
     */
    public List<IViolation> getAll();

    /**
     * Stores a Template into the database
     * 
     * @param violation
     * 
     * @return the violation stored in the database
     * @throws Exception
     */
    public IViolation save(IViolation violation);

    /**
     * Returns Violations from the database by providerId.
     * The violations are sorted descendent by datetime.
     * 
     * @param providerId
     * @return the corresponding Violations by providerId from the database
     */
    public List<IViolation> getByProvider(String providerId);

    /**
     * Returns Violations from the database by providerId in a range of dates
     * The violations are sorted descendent by datetime.
     * 
     * @param providerId
     * @param begin
     * @param end
     * @return the corresponding Violations by providerId in a range of date
     */
    public List<IViolation> getByProviderInaRangeOfDates(String providerId,
            Date begin, Date end);

    /**
     * Returns the violations that match the parameters
     * @param consumerId match consumerId if not null
     * @param providerId match providerId if not null
     * @param active match non-expired if not null
     */
    public List<IViolation> search(SearchParameters params);
    
    
    public static class SearchParameters {

        private String providerUuid;
        private String agreementId;
        private String guaranteeTermName;
        private Date begin;
        private Date end;
        
        public SearchParameters() {
        }
        
        public String getProviderUuid() {
            return providerUuid;
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

        public void setProviderUuid(String providerUuid) {
            this.providerUuid = providerUuid;
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
