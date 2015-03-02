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
package eu.atos.sla.service.messagebodyserializers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Resource;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.parser.IParser;
import eu.atos.sla.parser.NullParser;
import eu.atos.sla.parser.ParserException;
import eu.atos.sla.parser.data.wsag.Agreement;


/**
 * 
 * @author Elena Garrido
 */

@Component
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class AgreementJsonMessageBodyWriter implements MessageBodyWriter<IAgreement> {
	private static Logger logger = LoggerFactory.getLogger(AgreementJsonMessageBodyWriter.class);
	Throwable catchedException;
	
	@Resource(name="agreementJsonParser")
	IParser<Agreement> jsonParser;
	
	private void initParser() {
		if (jsonParser instanceof NullParser) jsonParser=null;		
	}
	
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		initParser();
		boolean isUsed =  (genericType == IAgreement.class) && (mediaType.toString().contains(MediaType.APPLICATION_JSON)) && jsonParser!=null ;
		if (isUsed)
			logger.debug("isWritable: {} -->type:{} genericType:{} mediaType:{} with parser:{}",
					isUsed, type, genericType, mediaType, jsonParser);
		return isUsed;
	}
	
	byte[] serializedData = null;
	@Override
	public long getSize(IAgreement agreement, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		if (agreement.getText()!=null){
			try {
				String jsonData = jsonParser.getSerializedData(agreement.getText());
				logger.info("jsonData {}", jsonData);
				serializedData = jsonData.getBytes();
				return serializedData.length;
			} catch (ParserException e) {
				logger.error(e.getMessage());
				catchedException = e;
			} catch (Throwable e) {
				logger.error(e.getMessage());
				catchedException = e;
			}
		}else {
			logger.error("Error marshalling data agreement text is null");
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		return 0;
	}

	@Override
	public void writeTo(IAgreement agreement, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> multivaluedMap, OutputStream entityStream)
			throws IOException, WebApplicationException {
		logger.info("writeTo " );
		if (catchedException!=null) {
			throw new WebApplicationException(catchedException, Response.Status.INTERNAL_SERVER_ERROR);
		}else
			entityStream.write(serializedData);
		
	}
}
