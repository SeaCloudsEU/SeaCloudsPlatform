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
package eu.atos.sla.parser;
/**
 * 
 */
public interface IParser <T> {
    /*
     * getWsagObject receives in serializedData the object information in an xml, json or any other format and 
     * must return the T object (eu.atos.sla.parser.data.wsag.Agreement or an eu.atos.sla.parser.data.wsag.Template)
     */
    public T getWsagObject(String serializedData) throws ParserException;

    /*
     * getWsagAsSerializedData receives in serializedData the object information in an xml, json or any other format and 
     * must return information following and xml in wsag standard.
     */
    public String getWsagAsSerializedData(String serializedData) throws ParserException;
    
    /*
     * getSerializedData receives in wsagSerialized the information in wsag standard as it was generated with the
     * getWsagAsSerializedData method and returns it in a xml, json or any other format
     */
    public String getSerializedData(String wsagSerialized) throws ParserException;
}
