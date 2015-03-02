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
package eu.atos.sla.util;

import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Node;

import eu.atos.sla.datamodel.IBusinessValueList;
import eu.atos.sla.datamodel.ICompensationDefinition.IPenaltyDefinition;
import eu.atos.sla.datamodel.bean.PenaltyDefinition;
import eu.atos.sla.parser.data.wsag.BusinessValueList;
import eu.atos.sla.parser.data.wsag.GuaranteeTerm;
import eu.atos.sla.parser.data.wsag.custom.CustomBusinessValue;
import eu.atos.sla.parser.data.wsag.custom.Penalty;

/**
 * An implementation of a XML parser of BusinessValueList element used in the model conversion step.
 * 
 * This class is responsible of translating the business elements of a template/agreement into model classes.
 * 
 * According to wsag spec, Guarantee/BusinessValueList/CustomBusinessValue is of type xs:anyType. This implementation
 * relies on CustomBusinessValue to generate the business rules.
 * 
 * The recognized xml structure is:
 * 
 * <pre><code>
 * &lt;wsag:BusinessValueList>
 *   &lt;wsag:Importance>xs:integer&lt;/wsag:Importance>?
 *   &lt;wsag:CustomBusinessValue count="xs:integer" duration="xs:duration">
 *     &lt;sla:Penalty type="xs:string" 
 *       expression="xs:string"
 *       unit="xs:string"
 *       validity="xs:string"
 *     />*
 *   &lt;/wsag:CustomBusinessValue>*
 * </code></pre>
 * 
 * Count and duration attributes are optional. If not specified, this CustomBusinessValue applies at every
 * violation. Otherwise, it applies if <code>count</code> violations occur in a <code>duration</code> interval
 * of time.
 * 
 * The interpretation of every Penalty attribute is up to an external accounting module, but the intended meaning is:
 * <li>type: kind of penalty (f.e: discount, service, terminate)
 * <li>expression, unit: value of the penalty (f.e. discount of (50, euro), discount(100, %), service(sms))
 * <li>validity: interval of time where the penalty is applied

 * @author rsosa
 *
 * @see IModelConverter
 * @see CustomBusinessValue
 * @see Penalty
 */
public class BusinessValueListParser {
	
	private JAXBContext context;
	private Unmarshaller um;

	public BusinessValueListParser() throws ModelConversionException {
		try {
			context = JAXBContext.newInstance(CustomBusinessValue.class);
			um = context.createUnmarshaller();
		} catch (JAXBException e) {
			throw new ModelConversionException(e.getMessage());
		}
	}

	protected IBusinessValueList parse(GuaranteeTerm guaranteeTermXML) 
			throws ModelConversionException {

		BusinessValueList bvlXml = guaranteeTermXML.getBusinessValueList();
		if (bvlXml == null) {
			return newBusinessValueList(0);
		}
		
		int importance = (bvlXml.getImportance() != null)? bvlXml.getImportance() : 0;
		IBusinessValueList businessValueList = newBusinessValueList(importance);
		
		/*
		 * most standard elements not handled; only importance and CustomBusinessValue
		 */

		parseCustomBusinessValues(bvlXml, businessValueList);
		return businessValueList;
			
	}

	/*
	 * this method parses CustomBusinessValue if jaxb deserialized it as a Node.
	 */
	@SuppressWarnings("unused")
	private void parseCustomBusinessValuesOld(BusinessValueList bvlXml,
			IBusinessValueList businessValueList) throws JAXBException {
		if (bvlXml.getCustomBusinessValue() != null) {
			for (Object bvlItem : bvlXml.getCustomBusinessValue()) {
				CustomBusinessValue item = (CustomBusinessValue) um.unmarshal((Node) bvlItem);
				
				for (Penalty action: item.getPenalties()) {
					
					IPenaltyDefinition penaltyDef = newPenaltyDefinition(item, action);
					businessValueList.addPenalty(penaltyDef);
				}
			}
		}
	}

	private void parseCustomBusinessValues(BusinessValueList bvlXml,
			IBusinessValueList businessValueList) {
		if (bvlXml.getCustomBusinessValue() != null) {
			for (CustomBusinessValue item : bvlXml.getCustomBusinessValue()) {
				
				for (Penalty action: item.getPenalties()) {
					
					IPenaltyDefinition penaltyDef = newPenaltyDefinition(item, action);
					businessValueList.addPenalty(penaltyDef);
				}
			}
		}
	}

	protected IBusinessValueList newBusinessValueList(int importance) {
		
		Collection<IPenaltyDefinition> emptyList = Collections.<IPenaltyDefinition>emptyList();
		eu.atos.sla.datamodel.bean.BusinessValueList businessValueList = 
				new eu.atos.sla.datamodel.bean.BusinessValueList(
						importance, 
						emptyList
				);
		return businessValueList;
	}
	
	protected IPenaltyDefinition newPenaltyDefinition(CustomBusinessValue item, Penalty action) {
		
		IPenaltyDefinition penalty = new PenaltyDefinition(
				item.getCount(), 
				item.getDuration(), 
				action.getType(), 
				action.getUnit(), 
				action.getExpression(), 
				action.getValidity());
		
		return penalty;
	}
}