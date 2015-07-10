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
package eu.atos.sla.service.types;

import javax.ws.rs.WebApplicationException;

public class BooleanParam {

    private final Boolean value;
    
    public static BooleanParam valueOf(String dateStr) {
        
        return new BooleanParam(dateStr);
    }

    public static Boolean getValue(BooleanParam instance) {

        return instance == null? null : instance.getValue();
    }
    
    public BooleanParam(String str) throws WebApplicationException {
        if ("".equals(str)) {
            this.value = null;
            return;
        }

        if ("0".equals(str) || "false".equalsIgnoreCase(str)) {
            
            this.value = Boolean.FALSE;
        }
        else {
            this.value = Boolean.TRUE;
        }
    }

    public Boolean getValue() {
        return value;
    }
}
