/**
 * Copyright 2015 SeaClouds
 * Contact: SeaClouds
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

import java.util.List;

import eu.atos.sla.datamodel.ITemplate;

/**
 * DAO interface to access to the Template information
 * 
 * @author Pedro Rey - Atos
 * 
 */
public interface ITemplateDAO  {

	/**
	 * Returns the Template from the database by its Id
	 * 
	 * @param id
	 *            of the Template
	 * @return the corresponding Template from the database
	 */
	public ITemplate getById(Long id);

	/**
	 * Returns the Template from the database by its UUID
	 * 
	 * @param id
	 *            of the Template
	 * @return the corresponding Template from the database
	 */
	public ITemplate getByUuid(String uuid);

	/**
	 * Returns the Template from the database by service Id
	 *
	 * @param providerId of the Template
	 * @param serviceIds list of serviceId's of the Template
	 * @return the corresponding Template from the database
	 */
	public List<ITemplate> search(String providerUuid, String []serviceIds);
	
	
	
	/**
	 * Returns the Template from the database by service Id
	 * 
	 * @param agreement of the Template
	 * @return the corresponding Template from the database
	 */
	public List<ITemplate> getByAgreement (String agreement);
	
	/**
	 * Returns all the Template stored in the database
	 * 
	 * @return all the Template stored in the database
	 */
	public List<ITemplate> getAll();

	/**
	 * Stores a Template into the database
	 * 
	 * @param AgreementXML
	 *            Template to be saved.
	 * @return <code>true</code> if the TemplateType was saved correctly
	 * @throws Exception 
	 */
	public ITemplate save(ITemplate template);

	/**
	 * Updates a Template in the database
	 * 
	 * @param AgreementXML
	 *            Template to be updated
	 * @return <code>true</code> if the Template was saved correctly
	 */
	public boolean update(String uuid, ITemplate template);

	/**
	 * Deletes a Template from the database
	 * 
	 * @param AgreementXML
	 *            to be deleted
	 * @return <code>true</code> if the Template was deleted correctly
	 */
	public boolean delete(ITemplate template);

}
