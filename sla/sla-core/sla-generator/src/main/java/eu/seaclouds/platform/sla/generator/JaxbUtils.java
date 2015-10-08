/**
 * Copyright 2015 Atos
 * Contact: Seaclouds
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
package eu.seaclouds.platform.sla.generator;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JaxbUtils {

    public static <E> E load(Class<E> clazz, InputStream is) throws JAXBException {
        
        JAXBContext jaxbContext;
        Unmarshaller jaxbUnmarshaller;
        
        jaxbContext = JAXBContext.newInstance(clazz);
        
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        
        @SuppressWarnings("unchecked")
        E result = (E) jaxbUnmarshaller.unmarshal(is);
        return result;
    }
    
    public static <E> E load(Class<E> clazz, Reader r) throws JAXBException {
        
        JAXBContext jaxbContext;
        Unmarshaller jaxbUnmarshaller;
        
        jaxbContext = JAXBContext.newInstance(clazz);
        
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        
        @SuppressWarnings("unchecked")
        E result = (E) jaxbUnmarshaller.unmarshal(r);
        return result;
    }
    
    public static <E> String toString(E e) throws JAXBException {
        String charsetName = "utf-8";
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        
        print(e, os, e.getClass());
        try {
            return os.toString(charsetName);
        } catch (UnsupportedEncodingException e1) {
            throw new IllegalArgumentException(charsetName + " is not supported");
        }
    }
    
    public static <E> void print(E e) throws JAXBException {
        print(e, e.getClass());
    }
    
    public static <E> void print(E e, Class<?>... classesToBeBound) throws JAXBException {
        print(e, System.out, classesToBeBound);
    }

    public static <E> void print(E e, OutputStream os, Class<?>... classesToBeBound) throws JAXBException {
        JAXBContext jaxbContext;
        Marshaller jaxbMarshaller;
        jaxbContext = JAXBContext.newInstance(classesToBeBound);
        jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        /*
         * http://stackoverflow.com/a/22756191
         */
        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
        jaxbMarshaller.marshal(e, os);
    }
    
}
