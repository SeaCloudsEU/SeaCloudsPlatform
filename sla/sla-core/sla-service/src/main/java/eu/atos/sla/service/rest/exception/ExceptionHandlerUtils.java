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
package eu.atos.sla.service.rest.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.atos.sla.parser.data.ErrorResponse;

public class ExceptionHandlerUtils {
    private static Logger logger = LoggerFactory.getLogger(ExceptionHandlerUtils.class);

    
    static protected Response buildResponse(Status status, Exception e) {
        int code = status.getStatusCode();
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(code);
        errorResponse.setMessage(e.getMessage());
        logger.info("Sending error {} - {} - {}", code, e.getMessage(), status.getReasonPhrase());
        return  Response
                .status(status)
                .entity(errorResponse)
                .build();
    }
    
}
