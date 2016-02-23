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
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import eu.seaclouds.platform.dashboard.proxy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Api("/")
public class CoreResource implements Resource{
    private static final Logger LOG = LoggerFactory.getLogger(CoreResource.class);

    private final DeployerProxy deployerProxy;
    private final GrafanaProxy grafanaProxy;
    private final PlannerProxy plannerProxy;
    private final SlaProxy slaProxy;

    public CoreResource(DeployerProxy deployerProxy, GrafanaProxy grafanaProxy, PlannerProxy plannerProxy, SlaProxy slaProxy) {
        this.deployerProxy = deployerProxy;
        this.grafanaProxy = grafanaProxy;
        this.plannerProxy = plannerProxy;
        this.slaProxy = slaProxy;
    }

    //TODO: Move this answer to objects

    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("about")
    @ApiOperation("Get SeaClouds Components endpoints")
    public Response getSeaCloudsInformation(){
        JsonObject jsonResponse = new JsonObject();

        JsonObject deployerObject = new JsonObject();
        JsonObject monitorObject = new JsonObject();
        JsonObject plannerObject = new JsonObject();
        JsonObject slaObject = new JsonObject();

        deployerObject.addProperty("url", deployerProxy.getEndpoint());
        jsonResponse.add("deployer", deployerObject);

        plannerObject.addProperty("url", plannerProxy.getEndpoint());
        jsonResponse.add("planner", plannerObject);

        slaObject.addProperty("url", slaProxy.getEndpoint());
        jsonResponse.add("sla", slaObject);

        JsonObject grafanaObject = new JsonObject();
        grafanaObject.addProperty("url", grafanaProxy.getEndpoint());

        monitorObject.add("grafana", grafanaObject);
        jsonResponse.add("monitor", monitorObject);

        return Response.ok(jsonResponse.toString()).build();
    }
}
