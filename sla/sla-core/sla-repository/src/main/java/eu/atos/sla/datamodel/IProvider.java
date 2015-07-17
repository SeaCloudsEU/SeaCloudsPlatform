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

import java.util.List;

public interface IProvider {

    /*
     * Internal generated id
     */
    Long getId();

    /**
     * The provider is recognized by external parties by this UUID
     */
    String getUuid();

    /**
     * Provider's name
     */
    String getName();

    void setId(Long id);

    /**
     * The provider is recognized by external parties by this UUID
     */
    void setUuid(String uuid);

    /**
     * Provider's name
     */
    void setName(String name);
    
    
    /**
     * Template list 
     */
    public List<ITemplate> getTemplates();
    public void setTemplates(List<ITemplate> templates);
    public void addTemplate(ITemplate template);
}
