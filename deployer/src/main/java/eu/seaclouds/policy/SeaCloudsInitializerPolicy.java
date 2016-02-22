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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.BaseEncoding;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.google.common.reflect.TypeToken;
import eu.atos.sla.parser.data.wsag.Agreement;
import it.polimi.tower4clouds.rules.Action;
import it.polimi.tower4clouds.rules.MonitoringRule;
import it.polimi.tower4clouds.rules.MonitoringRules;
import it.polimi.tower4clouds.rules.Parameter;
import org.apache.brooklyn.api.catalog.Catalog;
import org.apache.brooklyn.api.effector.Effector;
import org.apache.brooklyn.api.entity.EntityLocal;
import org.apache.brooklyn.api.location.Location;
import org.apache.brooklyn.api.sensor.AttributeSensor;
import org.apache.brooklyn.config.ConfigKey;
import org.apache.brooklyn.core.config.ConfigKeys;
import org.apache.brooklyn.core.effector.EffectorBody;
import org.apache.brooklyn.core.effector.Effectors;
import org.apache.brooklyn.core.entity.EntityInternal;
import org.apache.brooklyn.core.entity.StartableApplication;
import org.apache.brooklyn.core.entity.trait.Startable;
import org.apache.brooklyn.core.location.Locations;
import org.apache.brooklyn.core.policy.AbstractPolicy;
import org.apache.brooklyn.core.sensor.BasicAttributeSensor;
import org.apache.brooklyn.core.sensor.Sensors;
import org.apache.brooklyn.entity.software.base.SoftwareProcess;
import org.apache.brooklyn.entity.stock.EffectorStartableImpl;
import org.apache.brooklyn.util.collections.MutableMap;
import org.apache.brooklyn.util.core.config.ConfigBag;
import org.apache.brooklyn.util.core.flags.SetFromFlag;
import org.apache.brooklyn.util.exceptions.Exceptions;
import org.apache.brooklyn.util.http.HttpTool;
import org.apache.brooklyn.util.http.HttpToolResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Catalog(name = "SeaClouds Initializer", description = "Policy that configures Tower4Clouds, InfluxDB Observers and SLA for a SeaClouds Application")
public class SeaCloudsInitializerPolicy extends AbstractPolicy {
    private static final Logger LOG = LoggerFactory.getLogger(SeaCloudsInitializerPolicy.class);

    @SetFromFlag("slaAgreement")
    public static final ConfigKey<String> SLA_AGREEMENT = ConfigKeys.newStringConfigKey(
            "sla.agreement", "SLA Agreement (Base64-encoded)");

    @SetFromFlag("slaEndpoint")
    public static final ConfigKey<String> SLA_ENDPOINT = ConfigKeys.newStringConfigKey(
            "sla.endpoint", "SLA Core Endpoint (http(s)://url:port)");

    @SetFromFlag("slaUsername")
    public static final ConfigKey<String> SLA_USERNAME = ConfigKeys.newStringConfigKey(
            "sla.username", "SLA Core Username");

    @SetFromFlag("slaPasword")
    public static final ConfigKey<String> SLA_PASSWORD = ConfigKeys.newStringConfigKey(
            "sla.password", "SLA Core Password");

    @SetFromFlag("t4cRules")
    public static final ConfigKey<String> T4C_RULES = ConfigKeys.newStringConfigKey(
            "t4c.rules", "SLA Agreement (Base64-encoded)");

    @SetFromFlag("t4cEndpoint")
    public static final ConfigKey<String> T4C_ENDPOINT = ConfigKeys.newStringConfigKey(
            "t4c.endpoint", "Tower4Clouds Endpoint (http(s)://url:port)");

    @SetFromFlag("t4cUsername")
    public static final ConfigKey<String> T4C_USERNAME = ConfigKeys.newStringConfigKey(
            "t4c.username", "Tower4Clouds Username");

    @SetFromFlag("t4cPassword")
    public static final ConfigKey<String> T4C_PASSWORD = ConfigKeys.newStringConfigKey(
            "t4c.password", "Tower4Clouds Password");

    @SetFromFlag("influxdbEndpoint")
    public static final ConfigKey<String> INFLUXDB_ENDPOINT = ConfigKeys.newStringConfigKey(
            "influxdb.endpoint", "InfluxDB Endpoint (http(s)://url:port)");

    @SetFromFlag("influxdbUsername")
    public static final ConfigKey<String> INFLUXDB_USERNAME = ConfigKeys.newStringConfigKey(
            "influxdb.username", "InfluxDB Username", "root");

    @SetFromFlag("influxdbPassword")
    public static final ConfigKey<String> INFLUXDB_PASSWORD = ConfigKeys.newStringConfigKey(
            "influxdb.password", "InfluxDB Password", "root");

    @SetFromFlag("influxdbDatabase")
    public static final ConfigKey<String> INFLUXDB_DATABASE = ConfigKeys.newStringConfigKey(
            "influxdb.database", "InfluxDB Database Name", "tower4clouds");

    @SetFromFlag("grafanaEndpoint")
    public static final ConfigKey<String> GRAFANA_ENDPOINT = ConfigKeys.newStringConfigKey(
            "grafana.endpoint", "InfluxDB Endpoint (http(s)://url:port)");

    @SetFromFlag("grafanaUsername")
    public static final ConfigKey<String> GRAFANA_USERNAME = ConfigKeys.newStringConfigKey(
            "grafana.username", "Grafana Username", "admin");

    @SetFromFlag("grafanaPassword")
    public static final ConfigKey<String> GRAFANA_PASSWORD = ConfigKeys.newStringConfigKey(
            "grafana.password", "Grafana Password", "admin");


    static final AttributeSensor<String> SLA_ID = Sensors.newStringSensor("sla.id", "Service Level Agreement ID");
    static final AttributeSensor<List<String>> T4C_IDS = new BasicAttributeSensor<List<String>>(new TypeToken<List<String>>() {
    }, "t4c.ids", "Tower4Clouds MonitoringRules IDs");

    private MonitoringRules monitoringRules;
    private Agreement agreement;
    private String grafanaDashboardSlug;

    private String observerInitializerPayload;
    private String grafanaInitializerPayload;

    //TODO: This method is boilerplate code also contained in eu.seaclouds.platform.dashboard.util.ObjectMapperHelpers
    private <T> T xmlToObject(String xml, Class<T> type) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(type);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        T obj = (T) jaxbUnmarshaller.unmarshal(new StringReader(xml));
        return obj;
    }

    // Helper method to extract metric names from Monitoring Rules
    private List<String> extractMetricNames() {

        List<String> result = new ArrayList<>();
        for (MonitoringRule rule : monitoringRules.getMonitoringRules()) {

            /*
             * Workaround loop to retrieve the metric name. This is required due a poorly modeled POJO
             * <actions>
             *     ...
             *     <action name="OutputMetric">
             *         <parameter name="metric">appid_mysql_server_cpu_utilization_metric</parameter>
             *          ...
             *      </action>
             * </actions>
             */

            for (Action action : rule.getActions().getActions()) {
                if (action.getName().equalsIgnoreCase("OutputMetric")) {
                    for (Parameter parameter : action.getParameters()) {
                        if (parameter.getName().equalsIgnoreCase("metric")) {
                            result.add(parameter.getValue());
                        }
                    }
                }
            }
        }
        return result;
    }

    private void parseMonitoringRules() {
        String decodedRules = new String(BaseEncoding.base64().decode(getConfig(T4C_RULES)));
        try {
            monitoringRules = xmlToObject(decodedRules, MonitoringRules.class);
        } catch (JAXBException e) {
            Exceptions.propagateIfFatal(e);
            LOG.warn("Cannot unmarshal the content of t4c.rule into MonitoringRules");
        }
    }

    private void parseAgreements() {
        String decodedRules = new String(BaseEncoding.base64().decode(getConfig(SLA_AGREEMENT)));
        try {
            agreement = xmlToObject(decodedRules, Agreement.class);
        } catch (JAXBException e) {
            Exceptions.propagateIfFatal(e);
            LOG.warn("Cannot unmarshal the content of sla.agreement into an Agreement");
        }
    }

    private void setupObserverInitializerPayload() {
        // T4C JSON payload to attach a INFLUXDB Observer to T4C
        observerInitializerPayload = "{ \"format\":\"INFLUXDB\", \"protocol\":\"HTTP\",\"callbackUrl\":" +
                "\"" + getConfig(INFLUXDB_ENDPOINT) + "/db/" + getConfig(INFLUXDB_DATABASE) +
                "/series?u=" + getConfig(INFLUXDB_USERNAME) + "&p=" + getConfig(INFLUXDB_PASSWORD) + "\" }";
    }

    private void setupGrafanaInitializerPayload() {
        final String globalGrafanaOptionsJSON =
                "{  " +
                        "\"overwrite\": false," +
                        "\"dashboard\": {" +
                        "\"id\": null," +
                        "\"title\": \"" + entity.getDisplayName() + " Dashboard\"," +
                        "\"tags\": [\"SeaClouds\", \"AUTOGENERATED\"]," +
                        "\"style\": \"dark\"," +
                        "\"timezone\": \"browser\"," +
                        "\"editable\": false," +
                        "\"hideControls\": false," +
                        "\"sharedCrosshair\": true," +
                        "\"time\": { \"from\": \"now-6h\", \"to\": \"now\" }," +
                        "\"timepicker\": { " +
                        "                   \"refresh_intervals\": [\"1m\", \"1h\", \"1d\"]," +
                        "                   \"time_options\": [\"1h\", \"12h\", \"24h\", \"2d\", \"7d\", \"30d\"] " +
                        "}," +
                        "\"templating\": { \"list\": []  }," +
                        "\"annotations\": { \"list\": []  }," +
                        "\"schemaVersion\": 7," +
                        "\"version\": 1," +
                        "\"links\": []," +
                        "\"rows\": [";


        String rowOptions = "";

        for (String metricName : extractMetricNames()) {
            int index = 0;
            String currentRow =
                    "{" +
                            "\"collapse\": false," +
                            "\"editable\": false," +
                            "\"height\": \"250px\"," +
                            "\"panels\": [{" +
                            "                  \"id\":" + index + "," +
                            "                  \"aliasColors\": {}," +
                            "                  \"bars\": false," +
                            "                  \"datasource\": null," +
                            "                  \"editable\": true," +
                            "                  \"error\": false," +
                            "                  \"fill\": 1," +
                            "                  \"grid\": { \"leftLogBase\": 1, \"leftMax\": null, \"leftMin\": null, \"rightLogBase\": 1, \"rightMax\": null, \"rightMin\": null, \"threshold1\": null,  \"threshold1Color\": \"rgba(216, 200, 27, 0.27)\", \"threshold2\": null, \"threshold2Color\": \"rgba(234, 112, 112, 0.22)\" }," +
                            "                  \"legend\": { \"avg\": true, \"current\": false, \"max\": true, \"min\": true, \"show\": true, \"total\": false, \"values\": false}," +
                            "                  \"lines\": true," +
                            "                  \"linewidth\": 2," +
                            "                  \"links\": []," +
                            "                  \"nullPointMode\": \"connected\"," +
                            "                  \"percentage\": false," +
                            "                  \"pointradius\": 5," +
                            "                  \"points\": false," +
                            "                  \"renderer\": \"flot\"," +
                            "                  \"seriesOverrides\": []," +
                            "                  \"span\": 12," +
                            "                  \"stack\": false," +
                            "                  \"steppedLine\": false," +
                            "                  \"targets\": [{ " +
                            "                                    \"column\": \"value\"," +
                            "                                    \"function\": \"mean\"," +
                            "                                    \"query\": \"select mean(value) from \\\"" + metricName + "\\\" where $timeFilter group by time($interval) order asc\"," +
                            "                                    \"refId\": \"A\"," +
                            "                                    \"series\": \"" + metricName + "\"" +
                            "                               }]," +
                            "                  \"timeFrom\": null," +
                            "                  \"timeShift\": null," +
                            "                  \"title\": \"" + metricName + "\"," +
                            "                  \"tooltip\": { \"shared\": true, \"value_type\": \"cumulative\"}," +
                            "                  \"type\": \"graph\"," +
                            "                  \"x-axis\": true," +
                            "                  \"y-axis\": true," +
                            "                  \"y_formats\": [\"short\",\"short\"]" +
                            "              }]," +
                            "      \"title\": \"" + entity.getDisplayName() + "\"" +
                            "},";
            rowOptions += currentRow;
        }

        // Remove last ,
        rowOptions = rowOptions.substring(0, rowOptions.length() - 1);
        grafanaInitializerPayload = globalGrafanaOptionsJSON + rowOptions + "] } }";

    }

    @Override
    public void setEntity(EntityLocal entity) {
        super.setEntity(entity);

        // SeaCloudsInitializerPolicy should only be attached to Application
        if (!entity.getApplication().equals(entity)) {
            //TODO: Check if this exception stops the deployment process
            throw new RuntimeException("SeaCloudsInitializerPolicy must be attached to an application");
        }

        parseMonitoringRules();
        parseAgreements();
        setupObserverInitializerPayload();
        setupGrafanaInitializerPayload();
        // Overriding the default START/STOP behaivour to include the aditional tasks required by
        // SeaClouds while keeping the original behaviour. It allows to initialize and clean-up SeaClouds
        // components when an application is added/removed.
        ((EntityInternal) entity).getMutableEntityType().addSensor(SLA_ID);
        ((EntityInternal) entity).getMutableEntityType().addSensor(T4C_IDS);
        ((EntityInternal) entity).getMutableEntityType().addEffector(newStartEffector());
        ((EntityInternal) entity).getMutableEntityType().addEffector(newStopEffector());

    }

    private Effector<Void> newStartEffector() {
        return Effectors.effector(Startable.START)
                .impl(new EffectorBody<Void>() {
                    @Override
                    public Void call(ConfigBag parameters) {
                        LOG.info("Starting SeaCloudsInitializerPolicy " + "for " + entity.getId());
                        installSlaAgreement();
                        installMonitoringRules();
                        notifyRulesReady();
                        installInfluxDbObservers();
                        installGrafanaDashboards();

                        // Rewire the original behaviour
                        Collection<? extends Location> locations = null;
                        Object locationRaw = parameters.getStringKey(EffectorStartableImpl.StartParameters.LOCATIONS.getName());
                        locations = Locations.coerceToCollection(getManagementContext(), locationRaw);
                        ((StartableApplication) entity).start(locations);

                        return null;
                    }
                })
                .build();
    }

    private Effector<Void> newStopEffector() {
        return Effectors.effector(Startable.STOP)
                .parameter(SoftwareProcess.StopSoftwareParameters.STOP_PROCESS_MODE)
                .parameter(SoftwareProcess.StopSoftwareParameters.STOP_MACHINE_MODE)
                .impl(new EffectorBody<Void>() {
                    @Override
                    public Void call(ConfigBag parameters) {
                        LOG.info("Stopping SeaCloudsInitializerPolicy " + "for " + entity.getId());
                        removeSlaAgreement();
                        removeMonitoringRules();
                        removeInfluxDbObservers();
                        removeGrafanaDashboard();

                        // Rewire the original behaviour
                        ((StartableApplication) entity).stop();

                        return null;
                    }
                })
                .build();
    }

    private void installSlaAgreement() {
        LOG.info("SeaCloudsInitializerPolicy is installing SLA Agreements for " + entity.getId());

        HttpToolResponse response = post(getConfig(SLA_ENDPOINT) + "/seaclouds/agreements", MediaType.APPLICATION_XML_UTF_8.toString(),
                getConfig(SLA_USERNAME), getConfig(SLA_PASSWORD), BaseEncoding.base64().decode(getConfig(SLA_AGREEMENT)));

        if (!HttpTool.isStatusCodeHealthy(response.getResponseCode())) {
            throw new RuntimeException("Something went wrong during the SLA Agreements installation. " +
                    "Invalid response code, " + response.getResponseCode() + ":" + response.getContentAsString());
        } else {
            entity.sensors().set(SLA_ID, agreement.getAgreementId());
        }

    }

    private void notifyRulesReady() {
        LOG.info("SeaCloudsInitializerPolicy is starting to enforce SLA Agreements for " + entity.getId());

        HttpToolResponse response = post(getConfig(SLA_ENDPOINT) + "/seaclouds/commands/rulesready?agreementId=" +
                        agreement.getAgreementId(), MediaType.APPLICATION_XML_UTF_8.toString(),
                getConfig(SLA_USERNAME), getConfig(SLA_PASSWORD), "".getBytes());

        if (!HttpTool.isStatusCodeHealthy(response.getResponseCode())) {
            throw new RuntimeException("Something went wrong during the SLA Agreements installation. " +
                    "Invalid response code, " + response.getResponseCode() + ":" + response.getContentAsString());
        }
    }

    private void installMonitoringRules() {
        LOG.info("SeaCloudsInitializerPolicy is installing T4C Monitoring Rules for " + entity.getId());

        HttpToolResponse response = post(getConfig(T4C_ENDPOINT) + "/v1/monitoring-rules", MediaType.APPLICATION_XML_UTF_8.toString(),
                getConfig(T4C_USERNAME), getConfig(T4C_PASSWORD), BaseEncoding.base64().decode(getConfig(T4C_RULES)));

        if (!HttpTool.isStatusCodeHealthy(response.getResponseCode())) {
            throw new RuntimeException("Something went wrong while the monitoring rules installation. " +
                    "Invalid response code, " + response.getResponseCode() + ":" + response.getContentAsString());
        } else {
            List<String> ruleIds = new ArrayList<>();

            for (MonitoringRule rule : monitoringRules.getMonitoringRules()) {
                ruleIds.add(rule.getId());
            }

            entity.sensors().set(T4C_IDS, ruleIds);
        }


    }


    private void installInfluxDbObservers() {
        LOG.info("SeaCloudsInitializerPolicy is installing InfluxDB Observers for " + entity.getId());

        for (String metricName : extractMetricNames()) {
            HttpToolResponse response = post(getConfig(T4C_ENDPOINT) + "/v1/metrics/" + metricName + "/observers",
                    MediaType.JSON_UTF_8.toString(), getConfig(T4C_USERNAME),
                    getConfig(T4C_PASSWORD), observerInitializerPayload.getBytes());

            if (!HttpTool.isStatusCodeHealthy(response.getResponseCode())) {
                throw new RuntimeException("Something went wrong while attaching the observers to the Rules. " +
                        "Invalid response code, " + response.getResponseCode() + ":" + response.getContentAsString());
            }
        }
    }

    private void installGrafanaDashboards() {


        // Parse response of Grafana to be able to remove them later
        // HTTP/1.1 200 OK
        // Content-Type: application/json; charset=UTF-8
        // Content-Length: 78

        // {
        //  "slug": "production-overview",
        //  "status": "success",
        //  "version": 1
        // }

        try {
            HttpToolResponse response = post(getConfig(GRAFANA_ENDPOINT) + "/api/dashboards/db",
                    MediaType.JSON_UTF_8.toString(), getConfig(GRAFANA_USERNAME),
                    getConfig(GRAFANA_PASSWORD), grafanaInitializerPayload.getBytes());

            if (!HttpTool.isStatusCodeHealthy(response.getResponseCode())) {
                throw new RuntimeException("Something went wrong while installing Grafana Dashboards. " +
                        "Invalid response code, " + response.getResponseCode() + ":" + response.getContentAsString());
            }

            // Retrieving unique slug to be able to remove the Dashboard later
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.getContent());
            grafanaDashboardSlug = json.get("slug").asText();
        } catch (Exception e) {
            Exceptions.propagate(e);
        }


    }

    private void removeGrafanaDashboard() {
        LOG.info("SeaCloudsInitializerPolicy is removing Grafana Dashboard for " + entity.getId());

        HttpToolResponse response = delete(getConfig(GRAFANA_ENDPOINT) + "/api/dashboards/db/" + grafanaDashboardSlug,
                MediaType.JSON_UTF_8.toString(), getConfig(GRAFANA_USERNAME), getConfig(GRAFANA_PASSWORD));

        if (!HttpTool.isStatusCodeHealthy(response.getResponseCode())) {
            throw new RuntimeException("Something went wrong while removing Grafana Dashboards. " +
                    "Invalid response code, " + response.getResponseCode() + ":" + response.getContentAsString());
        }
    }

    private void removeSlaAgreement() {
        LOG.info("SeaCloudsInitializerPolicy is removing SLA Agreeement for " + entity.getId());

        HttpToolResponse response = delete(getConfig(SLA_ENDPOINT) + "/agreements/" + agreement.getAgreementId(),
                MediaType.APPLICATION_XML_UTF_8.toString(), getConfig(SLA_USERNAME), getConfig(SLA_PASSWORD));

        if (!HttpTool.isStatusCodeHealthy(response.getResponseCode())) {
            throw new RuntimeException("Something went wrong while removing the SLA Agreements. " +
                    "Invalid response code, " + response.getResponseCode() + ":" + response.getContentAsString());
        }
    }

    private void removeMonitoringRules() {
        LOG.info("SeaCloudsInitializerPolicy is removing T4C Monitoring Rules for " + entity.getId());
        for (MonitoringRule rule : monitoringRules.getMonitoringRules()) {
            HttpToolResponse response = delete(getConfig(T4C_ENDPOINT) + "/v1/monitoring-rules/" + rule.getId(),
                    MediaType.JSON_UTF_8.toString(), getConfig(T4C_USERNAME), getConfig(T4C_PASSWORD));

            if (!HttpTool.isStatusCodeHealthy(response.getResponseCode())) {
                throw new RuntimeException("Something went wrong while removing the Monitoring Rules. " +
                        "Invalid response code, " + response.getResponseCode() + ":" + response.getContentAsString());
            }
        }

    }

    private void removeInfluxDbObservers() {
        LOG.info("SeaCloudsInitializerPolicy is removing InfluxDB Observers for " + entity.getId());
        // Removing the rules also removes the observers, this is just a info msg.
    }


    private HttpToolResponse delete(String url, String mediaType, String username, String password) {
        URI apiEndpoint = URI.create(url);
        // the uri is required by the HttpClientBuilder in order to set the AuthScope of the credentials

        if (username == null || password == null) {
            return HttpTool.httpDelete(HttpTool.httpClientBuilder().build(), apiEndpoint,
                    MutableMap.of(HttpHeaders.CONTENT_TYPE, mediaType, HttpHeaders.ACCEPT, mediaType));
        } else {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);

            // the uri is required by the HttpClientBuilder in order to set the AuthScope of the credentials
            return HttpTool.httpDelete(HttpTool.httpClientBuilder().uri(apiEndpoint).credentials(credentials).build(),
                    apiEndpoint,
                    MutableMap.of(HttpHeaders.CONTENT_TYPE, mediaType, HttpHeaders.ACCEPT, mediaType,
                            HttpHeaders.AUTHORIZATION, HttpTool.toBasicAuthorizationValue(credentials)));
        }
    }

    private HttpToolResponse post(String url, String mediaType, String username, String password, byte[] payload) {
        URI apiEndpoint = URI.create(url);
        // the uri is required by the HttpClientBuilder in order to set the AuthScope of the credentials

        if (username == null || password == null) {
            return HttpTool.httpPost(HttpTool.httpClientBuilder().build(), apiEndpoint,
                    MutableMap.of(HttpHeaders.CONTENT_TYPE, mediaType, HttpHeaders.ACCEPT, mediaType), payload);
        } else {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);

            // the uri is required by the HttpClientBuilder in order to set the AuthScope of the credentials
            return HttpTool.httpPost(HttpTool.httpClientBuilder().uri(apiEndpoint).credentials(credentials).build(),
                    apiEndpoint, MutableMap.of(HttpHeaders.CONTENT_TYPE, mediaType, HttpHeaders.ACCEPT, mediaType,
                            HttpHeaders.AUTHORIZATION, HttpTool.toBasicAuthorizationValue(credentials)), payload);
        }
    }

}
