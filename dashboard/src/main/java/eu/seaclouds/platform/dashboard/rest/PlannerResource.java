/*
 *  Copyright 2014 SeaClouds
 *  Contact: SeaClouds
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package eu.seaclouds.platform.dashboard.rest;


import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import eu.seaclouds.platform.dashboard.proxy.PlannerProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/planner")
@Api("/planner")
public class PlannerResource implements Resource{
    private static final Logger LOG = LoggerFactory.getLogger(PlannerResource.class);

    private final PlannerProxy planner;

    public PlannerResource(PlannerProxy planner) {
        this.planner = planner;
    }


    @GET
    @Timed
    @Produces(MediaType.APPLICATION_XML)
    @Path("monitoringrules/{templateId}")
    @ApiOperation(value="Get Monitoring Rules from the Monitoring Rule Template Id")
    public Response getMonitoringRulesById(@PathParam("templateId") String templateId) {
        if (templateId == null) {
            LOG.error("Missing input parameters");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } else {
            return Response.ok(planner.getMonitoringRulesByTemplateId(templateId)).build();
        }
    }


    @POST
    @Timed
    @Produces("application/x-yaml")
    @Path("adps")
    @ApiOperation("Get SeaClouds-compliant ADP list from an AAM document")
    public Response getAdps(@ApiParam() String aam) {
        if (aam == null) {
            LOG.error("Missing input parameters");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } else {
            return Response.ok(planner.getAdps(aam)).build();
        }
    }

    @POST
    @Timed
    @Produces("application/x-yaml")
    @Path("dam")
    @ApiOperation("Get SeaClouds-compliant TOSCA DAM from an ADP document")
    public Response getDam(@ApiParam() String adp) {
        if (adp == null) {
            LOG.error("Missing input parameters");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } else {
            return Response.ok(planner.getDam(adp)).build();
        }
    }
}
