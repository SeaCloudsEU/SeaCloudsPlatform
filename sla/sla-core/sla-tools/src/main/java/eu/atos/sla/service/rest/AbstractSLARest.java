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

import java.io.ByteArrayOutputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.http.HttpStatus;

import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;

import eu.atos.sla.parser.data.ErrorResponse;
import eu.atos.sla.parser.data.MessageResponse;
import eu.atos.sla.service.rest.helpers.exception.HelperException;
import eu.atos.sla.service.rest.helpers.exception.HelperException.Code;

public abstract class AbstractSLARest {

	protected Response buildResponse(int code, String payload) {

		ResponseBuilder builder = Response.status(code);
		builder.entity(payload);
		return builder.build();
	}

	protected Response buildResponse(HttpStatus status, String payload) {
		return buildResponse(status.value(), payload);
	}

	protected Response buildResponse(HelperException serviceException) {

		ResponseBuilder builder = Response.status(translateServiceExceptionCode(serviceException.getCode()));
		builder.entity(printError(translateServiceExceptionCode(serviceException.getCode()),serviceException.getMessage()));
		return builder.build();
	}


	private Response buildResponsePOST(int code, String message, String location) {

		ResponseBuilderImpl builder = new ResponseBuilderImpl();
		builder.header("location", location);
		builder.status(code);
		builder.entity(message);

		return builder.build();

	}
	
	protected Response buildResponsePOST(HttpStatus status, MessageResponse message, String location) {
		ResponseBuilderImpl builder = new ResponseBuilderImpl();
		builder.header("location", location);
		builder.status(status.value());
		builder.entity(message);
		return builder.build();		
	}

	protected Response buildResponsePOST(HttpStatus status, String message, String location) {
		return  buildResponsePOST(status.value(), message, location);
	}
	
	private int translateServiceExceptionCode(Code code){
		int httpCode = 0;
		switch (code)
		{
			case INTERNAL: httpCode = 500; break;
			case PARSER: httpCode = 500; break;
			case DB_EXIST: httpCode = 500; break;
			case DB_DELETED: httpCode = 404; break;
			default: httpCode = 500; break;
		}
		return httpCode;
	}

	protected String printError(HttpStatus status, String text) {
		return printError(status.value(), text); 
	}
	
	protected String printError(int code, String text) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ErrorResponse errorResponse = new ErrorResponse();
	
			errorResponse.setCode(code);
			errorResponse.setMessage(text);
	
			JAXBContext jaxbContext;
				jaxbContext = JAXBContext
						.newInstance(eu.atos.sla.parser.data.ErrorResponse.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
	
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	
			marshaller.marshal(errorResponse, out);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return out.toString();

	}

	protected String printMessage(HttpStatus status, String text) {
		return printMessage(status.value(), text);
	}

	protected String printMessage(int code, String text) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {

			MessageResponse messageResponse = new MessageResponse();
	
			messageResponse.setCode(code);
			messageResponse.setMessage(text);
	
			JAXBContext jaxbContext = JAXBContext
					.newInstance(eu.atos.sla.parser.data.MessageResponse.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
	
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	
			marshaller.marshal(messageResponse, out);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return out.toString();

	}
	
	protected MessageResponse createMessage(HttpStatus status, String elementId, String text) {
		MessageResponse message = new MessageResponse();
		message.setCode(status.value());
		message.setMessage(text);
		message.setElementId(elementId);
		return message;
	}
	
	private static final String PATH_SEP = "/";

	protected String buildResourceLocation(String collectionUri, String resourceId) {
		String result;
		if (collectionUri.endsWith(PATH_SEP)) {
			result = collectionUri + resourceId;
		}
		else {
			result = collectionUri + PATH_SEP + resourceId;
		}
		return result;
	}


}
