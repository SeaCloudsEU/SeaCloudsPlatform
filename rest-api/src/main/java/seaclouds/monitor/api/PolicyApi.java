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
