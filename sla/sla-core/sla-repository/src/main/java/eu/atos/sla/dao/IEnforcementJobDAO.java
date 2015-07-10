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

import eu.atos.sla.datamodel.IEnforcementJob;

public interface IEnforcementJobDAO {
    /**
     * Retrieves enabled jobs not executed since <code>since</code>
     */
    List<IEnforcementJob> getNotExecuted(Date since);

    /**
     * Returns the GuaranteeTerm from the database by its Id
     * 
     * @param id
     *            of the GuaranteeTerm
     * @return the corresponding GuaranteeTerm from the database
     */
    IEnforcementJob getById(Long id);

    /**
     * Retrieves the job associated with <code>agreementId</code>.
     * 
     * @return EnforcementJob if exists; else <code>null</code>
     */
    IEnforcementJob getByAgreementId(String agreementId);

    /**
     * Returns all the EnforcementJob stored in the database
     * 
     * @return all the EnforcementJob stored in the database
     */
    public List<IEnforcementJob> getAll();

    /**
     * Stores an EnforcementJob into the database
     * 
     * @param EnforcementJob
     *            EnforcementJob to be saved.
     * @return <code>true</code> if the EnforcementJob was saved correctly
     * @throws Exception 
     */
    public IEnforcementJob save(IEnforcementJob expected);

    /**
     * Deletes an EnforcementJob from the database
     * 
     * @param EnforcementJob
     *            to be deleted
     * @return <code>true</code> if the EnforcementJob was deleted correctly
     */
    public boolean delete(IEnforcementJob enforcementJob);



}
