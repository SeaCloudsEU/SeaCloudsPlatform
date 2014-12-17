/**
 * Copyright 2014 SeaClouds
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
package seaclouds.monitor.api;

import seaclouds.monitor.api.model.PolicySummary;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * @author MBarrientos
 */
@Path("/policy")
@Api(value = "/policy", description = "Operations about policies")
@Produces({"application/json"})
public interface PolicyApi {

    @GET
    @Path("/application/{applicationId}/module/{moduleId}/policy/{policyId}}")
    @ApiOperation(value = "Fetch details from a policy")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Policy not found")
    })
    public PolicySummary getPolicyById(
            @ApiParam(value = "ID of application", required = true)
            @PathParam("applicationId") String applicationId,

            @ApiParam(value = "ID of module", required = true)
            @PathParam("moduleId") String moduleId,

            @ApiParam(value = "ID of policy", required = true)
            @PathParam("policyId") String policyId
    );
}
