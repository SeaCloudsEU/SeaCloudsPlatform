/*
 * Copyright 2014 SeaClouds
 * Contact: dev@seaclouds-project.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.seaclouds.platform.dashboard.resources;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.seaclouds.platform.dashboard.config.DeployerFactory;
import eu.seaclouds.platform.dashboard.config.MonitorFactory;
import eu.seaclouds.platform.dashboard.config.SlaFactory;
import eu.seaclouds.platform.dashboard.http.HttpDeleteRequestBuilder;
import eu.seaclouds.platform.dashboard.http.HttpGetRequestBuilder;
import eu.seaclouds.platform.dashboard.http.HttpPostRequestBuilder;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/deployer")
public class DeployerResource {
    static Logger log = LoggerFactory.getLogger(DeployerResource.class);

    private final DeployerFactory deployer;
    private final MonitorFactory monitor;
    private final SlaFactory sla;
    
    public DeployerResource(){
        this(new DeployerFactory(), new MonitorFactory(), new SlaFactory());
        log.warn("Using default configuration for DeployerResource");
    }
    
    public DeployerResource(DeployerFactory deployerFactory, MonitorFactory monitorFactory, SlaFactory slaFactory) {
        this.deployer = deployerFactory;
        this.monitor = monitorFactory;
        this.sla = slaFactory;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("application")
    public Response removeApplication(@QueryParam("id") String id) {
        //TODO: Remove monitoring rules and so on when the application is removed
        if (id == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        try {
            String deployerResponse = new HttpDeleteRequestBuilder()
                    .host(deployer.getEndpoint())
                    .setCredentials(deployer.getUser(), deployer.getPassword())
                    .path("/v1/applications/" + id)
                    .build();

            return Response.ok(deployerResponse).build();
        } catch (IOException | URISyntaxException e){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("applications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addApplication(String json) {
        if (json == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }else {
            JsonObject input = new JsonParser().parse(json).getAsJsonObject();

            String dam = input.get("dam").getAsJsonPrimitive().getAsString();
            String monitorDam = input.get("monitorDam").getAsJsonPrimitive().getAsString();
            String monitoringRules = input.get("monitoringRules").getAsJsonPrimitive().getAsString();
            String agreements = input.get("agreements").getAsJsonPrimitive().getAsString();

            String deployerResponse = null;
            String monitorResponseDam  = null;
            String monitorResponseRules  = null;
            String slaResponse  = null;

            try {
                if(dam != null && monitorDam != null && monitoringRules != null && agreements != null) {
                    deployerResponse = new HttpPostRequestBuilder()
                            .entity(new StringEntity(dam))
                            .host(deployer.getEndpoint())
                            .setCredentials(deployer.getUser(), deployer.getPassword())
                            .path("/v1/applications")
                            .build();

                    monitorResponseDam = new HttpPostRequestBuilder()
                            .entity(new StringEntity(monitorDam, ContentType.APPLICATION_JSON))
                            .host(monitor.getEndpoint())
                            .path("/v1/model/resources")
                            .build();

                    monitorResponseRules = new HttpPostRequestBuilder()
                            .entity(new StringEntity(monitoringRules, ContentType.APPLICATION_XML))
                            .host(monitor.getEndpoint())
                            .path("/v1/monitoring-rules")
                            .build();

                    slaResponse = new HttpPostRequestBuilder()
                            .multipartPostRequest(true)
                            .addParam("sla", agreements)
                            .addParam("rules", monitoringRules)
                            .host(sla.getEndpoint())
                            .path("/seaclouds/agreements")
                            .build();

                    slaResponse = new HttpPostRequestBuilder()
                            .host(sla.getEndpoint())
                            .path("/seaclouds/commands/rulesready")
                            .build();

                }else{
                    Response.status(Response.Status.NOT_ACCEPTABLE).build();
                }

                return Response.ok(deployerResponse).build();
            } catch (IOException | URISyntaxException e){
                if(deployerResponse == null){
                    //TODO: Rollback application deployment
                }

                if(deployerResponse != null && monitorResponseDam != null){
                    //TODO:  Rollback monitor rules
                }

                if(deployerResponse != null && monitorResponseRules != null){
                    //TODO:  Rollback monitor rules
                }

                if(deployerResponse != null && monitorResponseDam != null && monitoringRules != null && slaResponse != null){
                    //TODO: Rollback SLA
                }
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
    }

    @GET
    @Path("applications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listApplications() {
        try {
            String deployerResponse = new HttpGetRequestBuilder()
                    .host(deployer.getEndpoint())
                    .setCredentials(deployer.getUser(), deployer.getPassword())
                    .path("/v1/applications/tree")
                    .build();

            JsonArray applicationList = new JsonParser().parse(deployerResponse).getAsJsonArray();

            //TODO: This is not recursive, it only retrieves locations in the first level of  the application topology
            for(JsonElement application  : applicationList){
                for(JsonElement entity : application.getAsJsonObject().getAsJsonArray("children")){
                    deployerResponse = new HttpGetRequestBuilder()
                            .host(deployer.getEndpoint())
                            .path("/v1/applications/" + application.getAsJsonObject().get("id").getAsString() +
                                    "/entities/" + entity.getAsJsonObject().get("id").getAsString() + "/locations")
                            .setCredentials(deployer.getUser(), deployer.getPassword())
                            .build();
                    entity.getAsJsonObject().add("locations", new JsonParser().parse(deployerResponse));
                }
            }

            return Response.ok(applicationList.toString()).build();

        } catch (IOException | URISyntaxException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }

}
