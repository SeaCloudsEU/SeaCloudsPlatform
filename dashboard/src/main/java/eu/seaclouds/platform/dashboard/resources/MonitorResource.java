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


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.seaclouds.platform.dashboard.config.DeployerFactory;
import eu.seaclouds.platform.dashboard.config.MonitorFactory;
import eu.seaclouds.platform.dashboard.http.HttpDeleteRequestBuilder;
import eu.seaclouds.platform.dashboard.http.HttpGetRequestBuilder;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/monitor")
@Produces(MediaType.APPLICATION_JSON)
public class MonitorResource {
    static Logger log = LoggerFactory.getLogger(MonitorResource.class);

    private final MonitorFactory monitor;
    private final DeployerFactory deployer;
    
    public MonitorResource(){
        this(new MonitorFactory(), new DeployerFactory());
        log.warn("Using default configuration for MonitorResource");
    }
    
    public MonitorResource(MonitorFactory monitorFactory, DeployerFactory deployerFactory){
        this.monitor = monitorFactory;
        this.deployer = deployerFactory;
    }

    @GET
    @Path("metrics/value")
    public Response getMetric(@QueryParam("applicationId") String applicationId,
                              @QueryParam("entityId") String entityId,
                              @QueryParam("metricId") String metricId) {

        if (applicationId != null && entityId != null && metricId != null) {

            try {
                String monitorResponse = new HttpGetRequestBuilder()
                        .host(deployer.getEndpoint())
                        .setCredentials(deployer.getUser(), deployer.getPassword())
                        .path("/v1/applications/" + applicationId + "/entities/" + entityId + "/sensors/" + metricId)
                        .addParam("raw", "true")
                        .build();

                return Response.ok(monitorResponse).build();
            } catch (IOException | URISyntaxException e) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    private boolean isNumberType(String sensorType){
        return sensorType.equals("java.lang.Integer")
                || sensorType.equals("java.lang.Double")
                || sensorType.equals("java.lang.Float")
                || sensorType.equals("java.lang.Long")
                || sensorType.equals("java.lang.Short")
                || sensorType.equals("java.lang.BigDecimal")
                || sensorType.equals("java.lang.BigInteger")
                || sensorType.equals("java.lang.Byte");
    }

    private JsonArray retrieveMetrics(String applicationId) throws IOException, URISyntaxException {
        String rawEntityList = new HttpGetRequestBuilder()
                .host(deployer.getEndpoint())
                .setCredentials(deployer.getUser(), deployer.getPassword())
                .path("/v1/applications/" + applicationId + "/entities")
                .build();
        
        JsonArray entityList = new JsonParser().parse(rawEntityList).getAsJsonArray();
        JsonArray allMetricsList = new JsonArray();
        for (JsonElement entity : entityList) {
            String entityId = entity.getAsJsonObject().getAsJsonPrimitive("id").getAsString();
            String entityName = entity.getAsJsonObject().getAsJsonPrimitive("name").getAsString();

            // Creating entity object
            JsonArray entityMetrics = retrieveMetrics(applicationId, entityId);
            JsonObject entityJson = new JsonObject();
            entityJson.addProperty("id", entityId);
            entityJson.addProperty("name", entityName);
            entityJson.add("metrics", entityMetrics);
            
            allMetricsList.add(entityJson);
        }
        
        return allMetricsList;
    }
    
    private JsonArray retrieveMetrics(String applicationId, String entityId) throws IOException, URISyntaxException {
        String monitorResponse = new HttpGetRequestBuilder()
                .host(deployer.getEndpoint())
                .setCredentials(deployer.getUser(), deployer.getPassword())
                .path("/v1/applications/" + applicationId + "/entities/" + entityId + "/sensors")
                .build();

        JsonArray metricList = new JsonParser().parse(monitorResponse).getAsJsonArray();

        Iterator<JsonElement> metricIterator = metricList.iterator();

        while(metricIterator.hasNext()){
            JsonObject metric = metricIterator.next().getAsJsonObject();
            metric.remove("links");
            if(!isNumberType(metric.getAsJsonPrimitive("type").getAsString())){
                metricIterator.remove();
            }
        }

        return metricList.getAsJsonArray();

    }
    
    @GET
    @Path("metrics")
    public Response availableMetrics(@QueryParam("applicationId") String applicationId){
        if (applicationId != null) {
            try {
                JsonArray metricList = retrieveMetrics(applicationId);
                return Response.ok(metricList.toString()).build();
            } catch (IOException | URISyntaxException e) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
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


    @DELETE
    @Path("rules")
    public Response removeMonitoringRules(@QueryParam("id") String id) {

        if (id != null) {
            try {
                String monitorResponse = new HttpDeleteRequestBuilder()
                        .host(monitor.getEndpoint())
                        .path("/v1/monitoring-rules/"+id)
                        .build();
                return Response.ok().build();
            } catch (IOException | URISyntaxException e) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }


        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }

    @DELETE
    @Path("model")
    public Response removeDeploymentModel(@QueryParam("id") String id) {

        if (id != null) {
            try {
                String monitorResponse = new HttpDeleteRequestBuilder()
                        .host(monitor.getEndpoint())
                        .path("/v1/model/resources/"+id)
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
