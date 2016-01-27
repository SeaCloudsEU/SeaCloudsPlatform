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
import eu.seaclouds.platform.dashboard.model.SeaCloudsApplicationData;
import eu.seaclouds.platform.dashboard.model.SeaCloudsApplicationDataStorage;
import eu.seaclouds.platform.dashboard.proxy.DeployerProxy;
import eu.seaclouds.platform.dashboard.proxy.MonitorProxy;
import org.apache.brooklyn.rest.domain.EntitySummary;
import org.apache.brooklyn.rest.domain.SensorSummary;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

//TODO: Not sure if we should map responses as objects

@Path("/monitor")
@Api("/monitor")
public class MonitorResource implements Resource {
    private static final Logger LOG = LoggerFactory.getLogger(MonitorResource.class);

    private final MonitorProxy monitor;
    private final DeployerProxy deployer;
    private final SeaCloudsApplicationDataStorage dataStore;

    public MonitorResource(MonitorProxy monitorProxy, DeployerProxy deployer) {
        this.monitor = monitorProxy;
        this.deployer = deployer;
        this.dataStore = SeaCloudsApplicationDataStorage.getInstance();
    }

    private List<EntitySummary> getFlatEntityTree(JsonNode siblingsNode){

        List result = new ArrayList();
        Iterator<JsonNode> silblingsItr = siblingsNode.getElements();
        while(silblingsItr.hasNext()){
            JsonNode silblingNode = silblingsItr.next();
            EntitySummary silbling = new EntitySummary(silblingNode.get("id").getTextValue(),
                    silblingNode.get("name").getTextValue(), silblingNode.get("type").getTextValue(), null, null);
            result.add(silbling);

            JsonNode childrenNode = silblingNode.get("children");
            if(childrenNode != null){
                result.addAll(getFlatEntityTree(childrenNode));
            }
        }

        return result;
    }

    private List<EntitySummary> getFlatEntityTree(String brooklynId) throws IOException {
        List result = new ArrayList();
        for (JsonNode application : deployer.getApplicationsTree()) {
            if(application.get("id").getTextValue().equals(brooklynId)){
                EntitySummary mainEntity = new EntitySummary(application.get("id").getTextValue(),
                        application.get("name").getTextValue(), application.get("id").getTextValue(), null, null);
                result.add(mainEntity);

                JsonNode children = application.get("children");
                if(children != null){
                    result.addAll(getFlatEntityTree(children));
                }
            }
        }

        return result;
    }

    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("applications/{seacloudsId}/sensors")
    @Deprecated
    @ApiOperation(value = "Get Sensors from a SeaClouds Application")
    public Response getEntitySensorMapList(@PathParam("seacloudsId") String seacloudsId) throws IOException {
        if (seacloudsId == null) {
            LOG.error("Missing input parameters");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            SeaCloudsApplicationData seaCloudsApplicationData = dataStore.getSeaCloudsApplicationDataById(seacloudsId);

            if (seaCloudsApplicationData == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            List<Map<String, Object>> entityList = new ArrayList<>();
            List<EntitySummary> appEntities = getFlatEntityTree(seaCloudsApplicationData.getDeployerApplicationId());

            for (EntitySummary entity : appEntities) {
                List<SensorSummary> entitySensors = deployer.getEntitySensors(seaCloudsApplicationData.getDeployerApplicationId(), entity.getId());
                HashMap<String, Object> entityMap = new HashMap<>();

                entityMap.put("id", entity.getId());
                entityMap.put("name", entity.getName());
                entityMap.put("type", entity.getType());
                entityMap.put("sensors", entitySensors);
                entityList.add(entityMap);
            }

            return Response.ok(entityList).build();
        }
    }

    private boolean isNumberType(String sensorType) {
        return sensorType.equals("java.lang.Integer")
                || sensorType.equals("java.lang.Double")
                || sensorType.equals("java.lang.Float")
                || sensorType.equals("java.lang.Long")
                || sensorType.equals("java.lang.Short")
                || sensorType.equals("java.lang.BigDecimal")
                || sensorType.equals("java.lang.BigInteger")
                || sensorType.equals("java.lang.Byte");
    }

    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("applications/{seacloudsId}/metrics")
    @Deprecated
    @ApiOperation(value = "Get Metrics from a SeaClouds Application")
    public Response getEntityMetricMapList(@PathParam("seacloudsId") String seacloudsId) throws IOException {
        if (seacloudsId == null) {
            LOG.error("Missing input parameters");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            SeaCloudsApplicationData seaCloudsApplicationData = dataStore.getSeaCloudsApplicationDataById(seacloudsId);

            if (seaCloudsApplicationData == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            List<Map<String, Object>> entityList = new ArrayList<>();
            List<EntitySummary> appEntities = getFlatEntityTree(seaCloudsApplicationData.getDeployerApplicationId());

            for (EntitySummary entity : appEntities) {
                List<SensorSummary> entitySensors = deployer.getEntitySensors(seaCloudsApplicationData.getDeployerApplicationId(), entity.getId());
                List<SensorSummary> numberSensors = new ArrayList<>();

                for (SensorSummary entitySensor : entitySensors) {
                    if (isNumberType(entitySensor.getType())) {
                        numberSensors.add(entitySensor);
                    }
                }

                HashMap<String, Object> entityMap = new HashMap<>();
                entityMap.put("id", entity.getId());
                entityMap.put("name", entity.getName());
                entityMap.put("type", entity.getType());
                entityMap.put("metrics", numberSensors);
                entityList.add(entityMap);
            }

            return Response.ok(entityList).build();
        }
    }

    @GET
    @Timed
    @Produces(MediaType.TEXT_PLAIN)
    @Path("applications/{seacloudsId}/entities/{entityId}/metrics/{metricId}")
    @Deprecated
    @ApiOperation(value = "Get Metric Value from a particular <SeaCloudsApplication, EntityId, MetricId> combination")
    public Response getMetricValue(@PathParam("seacloudsId") String seacloudsId,
                                   @PathParam("entityId") String brooklynEntityId,
                                   @PathParam("metricId") String brooklynMetricId) throws IOException {

        if (seacloudsId == null || brooklynEntityId == null || brooklynMetricId == null) {
            LOG.error("Missing input parameters");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            SeaCloudsApplicationData seaCloudsApplicationData = dataStore.getSeaCloudsApplicationDataById(seacloudsId);

            if (seaCloudsApplicationData == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            String monitorResponse = deployer.getEntitySensorsValue(seaCloudsApplicationData.getDeployerApplicationId(),
                    brooklynEntityId, brooklynMetricId);

            if (monitorResponse == null) {
                monitorResponse = "0";
            }
            return Response.ok(monitorResponse).build();

        }
    }

}
