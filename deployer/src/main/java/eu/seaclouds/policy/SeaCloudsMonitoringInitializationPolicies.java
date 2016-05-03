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
package eu.seaclouds.policy;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.brooklyn.api.entity.Application;
import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.api.entity.EntityLocal;
import org.apache.brooklyn.api.sensor.AttributeSensor;
import org.apache.brooklyn.api.sensor.SensorEvent;
import org.apache.brooklyn.api.sensor.SensorEventListener;
import org.apache.brooklyn.camp.brooklyn.BrooklynCampConstants;
import org.apache.brooklyn.config.ConfigKey;
import org.apache.brooklyn.core.config.BasicConfigKey;
import org.apache.brooklyn.core.config.ConfigKeys;
import org.apache.brooklyn.core.entity.Attributes;
import org.apache.brooklyn.core.entity.lifecycle.Lifecycle;
import org.apache.brooklyn.core.policy.AbstractPolicy;
import org.apache.brooklyn.core.sensor.Sensors;
import org.apache.brooklyn.util.collections.MutableList;
import org.apache.brooklyn.util.collections.MutableMap;
import org.apache.brooklyn.util.core.flags.SetFromFlag;
import org.apache.brooklyn.util.http.HttpTool;
import org.apache.brooklyn.util.http.HttpToolResponse;
import org.apache.brooklyn.util.text.Strings;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.net.MediaType;

import eu.seaclouds.policy.utils.SeaCloudsDcRequestDto;


public class SeaCloudsMonitoringInitializationPolicies extends AbstractPolicy {

    private static final Logger LOG = LoggerFactory.getLogger(SeaCloudsMonitoringInitializationPolicies.class);

    private static final String RESOURCES = "/resource";

    private static final ConfigKey<String> TOSCA_ID = ConfigKeys.newStringConfigKey("tosca.template.id");

    @SuppressWarnings("unchecked")
    @SetFromFlag("targetEntities")
    public static ConfigKey<List<String>> TARGET_ENTITIES = new BasicConfigKey(List.class,
            "cloudFoundry.webapp.boundServices",
            "List of names of the services that should be bound to this application, " +
                    "providing credentials for its usage", MutableList.of());

    public static ConfigKey<String> SEACLOUDS_DC_ENDPOINT = ConfigKeys
            .newStringConfigKey("seacloudsdc.endpoint");

    @SuppressWarnings("unchecked")
    public static final AttributeSensor<Boolean> MONITORING_CONFIGURED =
            Sensors.newBooleanSensor("seaclouds.application.monitoring.configured", "Shows if the" +
                    "configuration of the seaclouds monitor system was completed");

    @Override
    public void setEntity(EntityLocal entity) {
        super.setEntity(entity);

        if (!entity.getApplication().equals(entity)) {
            throw new RuntimeException("SeaCloudsMonitoringInitializationPolicies must be attached " +
                    "to an application");
        }
        LifecycleListener listener = new LifecycleListener();
        entity.subscriptions().subscribe(entity, Attributes.SERVICE_STATE_ACTUAL, listener);
    }

    private Optional<Entity> findChildEntityByPlanId(Application app, String planId) {
        for (Entity child : app.getChildren()) {
            if (isCampPlanIdOfEntity(child, planId)
                    || isToscaIdPlanOfEntity(child, planId)) {
                return Optional.of(child);
            }
        }
        return Optional.absent();
    }

    private boolean isCampPlanIdOfEntity(Entity entity, String planId) {
        return planId.equalsIgnoreCase(getCampPlanId(entity));
    }

    private boolean isToscaIdPlanOfEntity(Entity entity, String planId) {
        return planId.equalsIgnoreCase(getToscaPlanId(entity));
    }

    private String getCampPlanId(Entity child) {
        return child.getConfig(BrooklynCampConstants.PLAN_ID);
    }

    private String getToscaPlanId(Entity child) {
        return child.getConfig(TOSCA_ID);
    }

    private class LifecycleListener implements SensorEventListener<Lifecycle> {

        private boolean configured;

        public LifecycleListener(){
            configured = false;
        }

        @Override
        public void onEvent(SensorEvent<Lifecycle> event) {
            if (event.getValue().equals(Lifecycle.RUNNING) && !configured) {
                configured = true;
                configureMontiroringForTargetEntities();
            }
        }

        private void configureMontiroringForTargetEntities() {

            for (String targetEntityId : getConfig(TARGET_ENTITIES)) {

                Optional<Entity> optionalChild = findChildEntityByPlanId((Application) entity, targetEntityId);
                if (optionalChild.isPresent()) {
                    Entity child = optionalChild.get();
                    configureSeaCloudDcForAEntity(child, targetEntityId);
                }
            }
            entity.sensors().set(MONITORING_CONFIGURED, true);
        }

        private void configureSeaCloudDcForAEntity(Entity child, String entityId) {
            String mainUri = getMainUri(child);
            if (!Strings.isBlank(mainUri)) {
                SeaCloudsDcRequestDto requestBody = new SeaCloudsDcRequestDto.Builder()
                        .type(entityId)
                        .url(mainUri)
                        .build();
                postSeaCloudsDcConfiguration(requestBody);
            }
        }

        private void postSeaCloudsDcConfiguration(SeaCloudsDcRequestDto requestBody) {
            try {
                String jsonBody = new ObjectMapper().writeValueAsString(requestBody);
                postSeaCloudsDcConfiguration(jsonBody);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Something went wrong creating Request body to " +
                        "configure the SeaCloudsDc for type" + requestBody.getType() + " and " +
                        "url " + requestBody.getUrl() + ". Message: " + e.getMessage());
            }
        }

        private void postSeaCloudsDcConfiguration(String requestBody) {

            URI apiEndpoint = URI.create(getConfig(SEACLOUDS_DC_ENDPOINT) + RESOURCES);

            Map<String, String> headers = MutableMap.of(
                    HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString(),
                    HttpHeaders.ACCEPT, MediaType.JSON_UTF_8.toString());

            HttpToolResponse response = HttpTool
                    .httpPost(HttpTool.httpClientBuilder().build(), apiEndpoint, headers, requestBody.getBytes());

            if (!HttpTool.isStatusCodeHealthy(response.getResponseCode())) {
                throw new RuntimeException("Something went wrong during SeaCloudsDc configuration, "
                        + response.getResponseCode() + ":" + response.getContentAsString());
            }
        }

        private String getMainUri(Entity child) {
            URI mainUri = child.getAttribute(Attributes.MAIN_URI);
            if (mainUri != null) {
                return mainUri.toString();
            }
            return Strings.EMPTY;
        }
        
    }

}
