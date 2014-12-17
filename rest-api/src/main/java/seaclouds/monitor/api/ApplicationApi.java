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
