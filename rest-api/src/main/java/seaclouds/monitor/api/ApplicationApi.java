package seaclouds.monitor.api;

import seaclouds.monitor.api.model.ApplicationSummary;
import seaclouds.monitor.api.model.MetricSummary;
import seaclouds.monitor.api.model.ModuleSummary;

import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.annotations.ApiParam;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * @author MBarrientos
 */

@Path("/application")
@Api(value = "/application", description = "Details about applications being monitored")
@Produces({"application/json"})
public interface ApplicationApi {

    @GET
    @Path("/application/{applicationId}")
    @ApiOperation(value = "Retrieves all metrics from an application")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Application not found")
    })
    public ApplicationSummary getMetricsByApplication(
            @ApiParam(value = "Application ID", required = true)
            @PathParam("applicationId")
            String applicationId
    );

    @GET
    @Path("/application/{applicationId}/module/{moduleId}")
    @ApiOperation(value = "Retrieves all metrics from a module")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Module not found")
    })
    public ModuleSummary getMetricsByModule(
            @ApiParam(value = "Application ID", required = true)
            @PathParam("applicationId")
            String applicationId,

            @ApiParam(value = "Module ID", required = true)
            @PathParam("moduleId")
            String moduleId
    );

    @GET
    @Path("/application/{applicationId}/module/{moduleId}/metric/{metricId}")
    @ApiOperation(value = "Get value from metric")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Metric not found")
    })
    public Object getMetric(
            @ApiParam(value = "Application ID", required = true)
            @PathParam("applicationId")
            String applicationId,

            @ApiParam(value = "Module ID", required = true)
            @PathParam("moduleId")
            String moduleId,

            @ApiParam(value = "Metric ID", required = true)
            @PathParam("metricId")
            String metricId
    );

    @POST
    @Path("/application/{applicationId}")
    @ApiOperation(value = "Register an application into the monitor")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Application not found")
    })
    public String registerApplication(
            @ApiParam(value = "Application ID", required = true)
            @PathParam("applicationId")
            String applicationId
    );

}
