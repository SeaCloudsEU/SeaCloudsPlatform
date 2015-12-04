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
package eu.atos.sla.parser.json;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import eu.atos.sla.parser.IParser;
import eu.atos.sla.parser.ParserException;
import eu.atos.sla.parser.ValidationHandler;
import eu.atos.sla.parser.data.wsag.Template;
/**
 * 
 */
public class TemplateParser implements IParser<Template> {
    private static Logger logger = LoggerFactory.getLogger(TemplateParser.class);

    private ObjectMapper mapper;
    
    public TemplateParser() {
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);
    }

    /*
     * getWsagObject receives in serializedData the object information in json 
     * must returns a eu.atos.sla.parser.data.wsag.Template 
     */    
    @Override
    public Template getWsagObject(String serializedData) throws ParserException{
        Template template = null;
        try{
            logger.info("Will parse {}", serializedData);
            template = mapper.readValue(serializedData, Template.class);
            logger.debug("Template parsed {}", template);
        } catch (JsonProcessingException e) {
            throw new ParserException(e);
        } catch (Throwable e) {
            throw new ParserException(e);
        }
        return template;

    }

    /*
     * getWsagAsSerializedData receives in serializedData json  
     * must return information following and xml in wsag standard.
     */
    @Override
    public String getWsagAsSerializedData(String serializedData) throws ParserException {
        StringWriter stringWriter = new StringWriter();
        try {
            Template template = getWsagObject(serializedData);
            JAXBContext jaxbContext = JAXBContext.newInstance(Template.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setEventHandler(new ValidationHandler());
            jaxbMarshaller.marshal(template, stringWriter);
        } catch (JAXBException e) {
            throw new ParserException(e);
        }
        return stringWriter.toString();
    }

    
    /*
     * getSerializedData receives in wsagSerialized the information in wsag standard as it was generated with the
     * getWsagAsSerializedData method and returns it in json 
     */
    @Override
    public String getSerializedData(String wsagSerialized) throws ParserException{
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(Template.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                Template template = (Template)jaxbUnmarshaller.unmarshal(new StringReader(wsagSerialized));
                String result = mapper.writeValueAsString(template);
                return result;
            } catch (JsonProcessingException e) {
                throw new ParserException(e);
            } catch (JAXBException e) {
                throw new ParserException(e);
            }
    }

    
    
}
