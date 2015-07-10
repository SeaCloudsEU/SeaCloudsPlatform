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
package eu.atos.sla.service.messagebodyserializers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eu.atos.sla.parser.IParser;
import eu.atos.sla.parser.NullParser;
import eu.atos.sla.parser.ParserException;
import eu.atos.sla.parser.data.wsag.Template;
import eu.atos.sla.service.types.TemplateParam;


/**
 * 
 */

@Component
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class TemplateParamJsonMessageBodyReader implements MessageBodyReader<TemplateParam> {
    private static Logger logger = LoggerFactory.getLogger(TemplateParamJsonMessageBodyReader.class);
    @Resource(name="templateJsonParser")
    IParser<Template> jsonParser;
    Throwable catchedException;

    
    private void initParser() {
        if (jsonParser instanceof NullParser) jsonParser=null;        
    }    
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        initParser();
        boolean isUsed = (type == TemplateParam.class) && 
                mediaType.toString().contains(MediaType.APPLICATION_JSON) && 
                jsonParser!=null;
        if (isUsed)
            logger.debug("isReadable: {} -->type:{} genericType:{} mediaType:{} with parser:{}",
                    isUsed, type, genericType, mediaType, jsonParser);
        return isUsed;
    }


    @Override
    public TemplateParam readFrom(Class<TemplateParam> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
            String str = MessageBodyUtils.getStringFromInputStream(entityStream);
            try {
                Template templateWSAG = jsonParser.getWsagObject(str);
                String serializedWsagData = jsonParser.getWsagAsSerializedData(str);
                TemplateParam agreementParam = new TemplateParam();
                agreementParam.setTemplate(templateWSAG);
                agreementParam.setOriginalSerialzedTemplate(removeXMLHeader(serializedWsagData));
                return agreementParam;
            } catch (ParserException e) {
                logger.error("Error parsing"+e.getMessage());
                throw new WebApplicationException(e,Response.Status.NOT_ACCEPTABLE);
            }
    }


    private String removeXMLHeader(String originalXML){
        return originalXML.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();        
    }

}
