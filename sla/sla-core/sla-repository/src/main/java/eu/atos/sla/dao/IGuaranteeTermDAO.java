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

import eu.atos.sla.datamodel.IGuaranteeTerm;

/**
 * DAO interface to access to the GuaranteeTerm information
 * 
 * @author Pedro Rey - Atos
 * 
 */
public interface IGuaranteeTermDAO {

	/**
	 * Returns the GuaranteeTerm from the database by its Id
	 * 
	 * @param id
	 *            of the GuaranteeTerm
	 * @return the corresponding GuaranteeTerm from the database
	 */
	public IGuaranteeTerm getById(Long id);

	/**
	 * Returns all the GuaranteeTerm stored in the database
	 * 
	 * @return all the GuaranteeTerm stored in the database
	 */
	public List<IGuaranteeTerm> getAll();

	/**
	 * Stores a GuaranteeTerm into the database
	 * 
	 * @param GuaranteeTerm
	 *            GuaranteeTerm to be saved.
	 * @return <code>true</code> if the GuaranteeTermType was saved correctly
	 * @throws Exception
	 */
	public IGuaranteeTerm save(IGuaranteeTerm guaranteeTerm);

	/**
	 * Updates a GuaranteeTerm in the database
	 * 
	 * @param GuaranteeTerm
	 *            GuaranteeTerm to be updated
	 * @return <code>true</code> if the GuaranteeTerm was saved correctly
	 */
	public boolean update(IGuaranteeTerm guaranteeTerm);

	/**
	 * Deletes a GuaranteeTerm from the database
	 * 
	 * @param GuaranteeTerm
	 *            to be deleted
	 * @return <code>true</code> if the GuaranteeTerm was deleted correctly
	 */
	public boolean delete(IGuaranteeTerm guaranteeTerm);

}
