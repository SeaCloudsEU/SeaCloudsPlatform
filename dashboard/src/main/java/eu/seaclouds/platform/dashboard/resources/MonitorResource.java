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

package eu.seaclouds.platform.dashboard.resources;


import eu.seaclouds.platform.dashboard.config.MonitorFactory;
import eu.seaclouds.platform.dashboard.http.HttpDeleteRequestBuilder;
import eu.seaclouds.platform.dashboard.http.HttpGetRequestBuilder;
import eu.seaclouds.platform.dashboard.http.HttpPostRequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;

@Path("/monitor")
@Produces(MediaType.APPLICATION_JSON)
public class MonitorResource {
    static Logger log = LoggerFactory.getLogger(MonitorResource.class);

    private final MonitorFactory monitor;

    public MonitorResource(){
        this(new MonitorFactory());
        log.warn("Using default configuration for MonitorResource");
    }

    public MonitorResource(MonitorFactory monitorFactory){
        this.monitor = monitorFactory;
    }


    @GET
    @Path("rules")
    public Response getMonitoringRules() {
        try {
            String monitorResponse = new HttpGetRequestBuilder()
                    .host(monitor.getEndpoint())
                    .path("/v1/monitoring-rules")
                    .build();
            //TODO: Dirty hack in order to output JSON
            return Response.ok(org.json.XML.toJSONObject(monitorResponse).toString()).build();
        } catch (IOException | URISyntaxException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("rules")
    public Response addMonitoringRules(String monitoringRules) {

        try {
            String monitorResponse = new HttpPostRequestBuilder()
                    .entity(new StringEntity(monitoringRules, ContentType.APPLICATION_XML))
                    .host(monitor.getEndpoint())
                    .path("/v1/monitoring-rules")
                    .build();

            return Response.ok().build();
        } catch (IOException | URISyntaxException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }


    @DELETE
    @Path("rules/{id}")
    public Response removeMonitoringRules(@PathParam("id") String id) {

        if (id != null) {
            try {
                String monitorResponse = new HttpDeleteRequestBuilder()
                        .host(monitor.getEndpoint())
                        .path("/v1/monitoring-rules/" + id)
                        .build();
                return Response.ok().build();
            } catch (IOException | URISyntaxException e) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }


        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }



    @POST
    @Path("model")
    public Response addDeploymentModel(String monitoringModel) {

        try {
            String monitorResponse = new HttpPostRequestBuilder()
                    .entity(new StringEntity(monitoringModel, ContentType.APPLICATION_JSON))
                    .host(monitor.getEndpoint())
                    .path("/v1/model/resources")
                    .build();

            return Response.ok().build();
        } catch (IOException | URISyntaxException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }

    @DELETE
    @Path("model/{id}")
    public Response removeDeploymentModel(@PathParam("id") String id) {

        if (id != null) {
            try {
                String monitorResponse = new HttpDeleteRequestBuilder()
                        .host(monitor.getEndpoint())
                        .path("/v1/model/resources/" + id)
                        .build();
                return Response.ok().build();
            } catch (IOException | URISyntaxException e) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }


        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }
}
