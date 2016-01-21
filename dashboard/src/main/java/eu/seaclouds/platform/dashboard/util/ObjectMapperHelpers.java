/*
 *  Copyright 2014 SeaClouds
 *  Contact: SeaClouds
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package eu.seaclouds.platform.dashboard.util;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ObjectMapperHelpers {

    private static com.fasterxml.jackson.databind.ObjectMapper jackson2Mapper;
    
    static {
        jackson2Mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    }
    
    /**
     * Transforms a JSON string to an Object using org.codehaus.jackson.map.ObjectMapper
     * If you want to parse a collection please @see {#link JsonToObjectCollection}
     *
     * @param json
     * @param type Class object representing the target class T
     * @param <T>  target class
     * @return an instance of T
     * @throws IOException if is not possible to parse the object
     */
    public static <T> T JsonToObject(String json, Class<T> type) throws IOException {
        return new ObjectMapper().readValue(json, type);

    }

    public static <T> T JsonToObjectJackson2(String json, Class<T> type) throws IOException {
        return jackson2Mapper.readValue(json, type);

    }

    /**
     * Transforms a JSON string to an List<Object> using org.codehaus.jackson.map.ObjectMapper
     * If you want to parse a single Object please @see {#link JsonToObject}
     *
     * @param json  string representing a collection of objects
     * @param type Class of the contained objects within the list
     * @param <T>  target class
     * @return a List of T
     * @throws IOException if is not possible to parse the object
     **/
    public static <T> List<T> JsonToObjectCollection(String json, Class<T> type) throws IOException {
        return new ObjectMapper().readValue(json, TypeFactory.collectionType(List.class, type));
    }

    public static <T> List<T> JsonToObjectCollectionJackson2(String json, Class<T> type) throws IOException {
        com.fasterxml.jackson.databind.type.TypeFactory typeFactory = com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();
        return jackson2Mapper.readValue(
                json, typeFactory.constructCollectionType(List.class, type));
    }

    /**
     * Transforms an annotated Object to a JSON string using org.codehaus.jackson.map.ObjectMapper
     *
     * @param object to transform to a Json String
     * @return a JSON string representing the object
     * @throws IOException if is not possible to parse the object
     */
    public static String ObjectToJson(Object object) throws IOException {
        return new ObjectMapper().writeValueAsString(object);
    }

    /**
     * Transforms a XML string to an Object using javax.xml.bind.Unmarshaller.
     * If you want to parse a collection please @see {#link XmlToObjectCollection}
     *
     * @param xml
     * @param type Class object representing the target class T
     * @param <T>  target class
     * @return an instance of T
     * @throws IOException if is not possible to parse the object
     */
    public static <T> T XmlToObject(String xml, Class<T> type) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(type);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        T obj = (T) jaxbUnmarshaller.unmarshal(new StringReader(xml));
        return obj;
    }

    /**
     * Transforms a XML string to an List<Object> using javax.xml.bind.Unmarshaller.
     * If you want to parse a single Object please @see {#link XmlToObject}
     *
     * @param xml  string representing a collection of objects
     * @param type Class of the contained objects within the list
     * @param <T>  target class
     * @return a List of T
     * @throws IOException if is not possible to parse the object
     **/
    public static <T> List<T> XmlToObjectCollection(String xml, Class<T> type) throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(ObjectMapperHelpers.JAXBCollection.class, type);
        Unmarshaller u = ctx.createUnmarshaller();

        Source src = new StreamSource(IOUtils.toInputStream(xml));
        ObjectMapperHelpers.JAXBCollection<T> collection = u.unmarshal(src, ObjectMapperHelpers.JAXBCollection.class).getValue();
        return collection.getItems();
    }

    /**
     * Transforms an annotated Object to a XML string using javax.xml.bind.Marshaller
     *
     * @param object to transform to a XML String
     * @return a XML string representing the object
     * @throws IOException if is not possible to parse the object
     */
    public static String ObjectToXml(Object object) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = jaxbContext.createMarshaller();
        StringWriter sw = new StringWriter();
        marshaller.marshal(object, sw);
        return sw.toString();
    }


    /**
     * Required Wrapper to unmarshal List<T> XML elements. @see {#link https://jaxb.java.net/guide/Mapping_interfaces.html}
     *
     * @param <T> class of the list's objects
     */
    private static class JAXBCollection<T> {
        @XmlAnyElement(lax = true)
        private final List<T> items;

        public JAXBCollection(Collection<T> contents) {
            if (contents instanceof List) {
                items = (List<T>) contents;
            } else {
                items = new ArrayList<T>(contents);
            }

        }

        public JAXBCollection() {
            this(new ArrayList<T>());
        }

        public List<T> getItems() {
            return items;
        }

    }
}
