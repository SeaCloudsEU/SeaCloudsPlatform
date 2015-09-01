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
package eu.atos.sla.datamodel;



public interface ITemplate {

    /*
     * Internal generated ID
     */
    Long getId();
    void setId(Long id);
    
    /**
     * This template is recognized by external parties by this internally generated UUID. 
     */
    
    String getUuid();
    void setUuid(String uuid);
    
    /**
     * Template body. This is an ws-agreement-compliant xml.
     * NOTE: String? Maybe there is a better type.
     */
    String getText();
    void setText(String text);
    

    /** 
     * Service from the context
     */
    public String getServiceId();
    public void setServiceId(String serviceId);
    
    /** 
     * Name from the template
     */
    public String getName();
    public void setName(String name);
    
    
    /** 
     * Provider from the template
     */
    public IProvider getProvider();
    public void setProvider(IProvider provider); 
    
}
