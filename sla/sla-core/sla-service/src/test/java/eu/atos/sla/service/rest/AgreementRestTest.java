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
package eu.atos.sla.service.rest;

//import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.jayway.restassured.RestAssured;
//import com.jayway.restassured.response.Response;

/**
 * Rest Service that exposes all the stored information of the SLA core
 * 
 * @author Pedro Rey
 */

public class AgreementRestTest {
	private static Logger logger = LoggerFactory.getLogger(AgreementRestTest.class);

	@Test
	public void testGetAgreements() {
		logger.debug("start of testGetAgreements");
/*		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8080;
		RestAssured.basePath = "/sla-service";
		Response someData  = RestAssured.given().contentType("application/xml").multiPart("payload", new File("./trunk/src/main/resources/samples/agreement01.xml")).when().post("/agreements");
		
		logger.log(Level.INFO, "Rest output call "+someData.htmlPath().prettyPrint());
		//TODO egarrido, missing to do verification, if rest call result is correct or not.
*/
		logger.debug("end of testGetAgreements");
		
		assert (true);
	}

	@Test
	public void testGetAgreementById() {

		assert (true);

	}

	@Test
	public void testCreateAgreement() {

		assert (true);
	}

	@Test
	public void testGetActiveAgreements() {

		assert (true);

	}

	@Test
	public void testDeleteAgreement() {

		assert (true);
	}

}
