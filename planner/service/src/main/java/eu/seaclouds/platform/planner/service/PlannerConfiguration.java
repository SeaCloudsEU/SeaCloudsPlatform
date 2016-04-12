package eu.seaclouds.platform.planner.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class PlannerConfiguration extends Configuration{
    @NotEmpty
    private String discovererURL;

    @NotEmpty
    private String[] deployableProviders;

    @NotEmpty
    private String monitorGeneratorURL;

    @NotEmpty
    private  String monitorGeneratorPort;

    @NotEmpty
    private String influxdbURL;

    @NotEmpty
    private  String influxdbPort;

    @NotEmpty
    private String slaGeneratorURL;

    @NotEmpty
    private String filterOfferings;

    @NotEmpty
    private String influxdbDatabase;

    @NotEmpty
    private String influxdbUsername;

    @NotEmpty
    private String influxdbPassword;

    @NotEmpty
    private String grafanaUsername;

    @NotEmpty
    private String grafanaPassword;

    @NotEmpty
    private String grafanaEndpoint;

    @JsonProperty("monitorGeneratorPort")
    public void setMonitorGeneratorPort(String monitorGeneratorPort) { this.monitorGeneratorPort = monitorGeneratorPort; }

    @JsonProperty("monitorGeneratorPort")
    public String getMonitorGeneratorPort() { return this.monitorGeneratorPort; }

    @JsonProperty("influxdbPort")
    public void setInfluxdbPort(String influxdbPort) { this.influxdbPort = influxdbPort; }

    @JsonProperty("influxdbPort")
    public String getInfluxdbPort() { return this.influxdbPort; }

    @JsonProperty("deployableProviders")
    public void setDeployableProviders(String[] deployableProviders) { this.deployableProviders = deployableProviders; }

    @JsonProperty("deployableProviders")
    public String[] getDeployableProviders() { return this.deployableProviders; }

    @JsonProperty("monitorGeneratorURL")
    public void setMonitorGeneratorURL(String monitorGeneratorURL) { this.monitorGeneratorURL = monitorGeneratorURL; }

    @JsonProperty("monitorGeneratorURL")
    public String getMonitorGeneratorURL() { return this.monitorGeneratorURL; }

    @JsonProperty("influxdbURL")
    public void setInfluxdbURL(String influxdbURL) { this.influxdbURL = influxdbURL; }

    @JsonProperty("influxdbURL")
    public String getInfluxdbURL() { return this.influxdbURL; }

    @JsonProperty("slaGeneratorURL")
    public void setSlaGeneratorURL(String slaGeneratorURL) { this.slaGeneratorURL = slaGeneratorURL; }

    @JsonProperty("slaGeneratorURL")
    public String getSlaGeneratorURL() { return this.slaGeneratorURL;}

    @JsonProperty("discovererURL")
    public void setDiscovererURL(String discovererURL) { this.discovererURL = discovererURL; }

    @JsonProperty("discovererURL")
    public String getDiscovererURL() { return this.discovererURL; }

    @JsonProperty("filterOfferings")
    public void setFilterOfferings(String filterOfferings) { this.filterOfferings = filterOfferings; }

    @JsonProperty("filterOfferings")
    public String getFilterOfferings() { return this.filterOfferings; }

    @JsonProperty("influxdbDatabase")
    public void setInfluxdbDatabase(String influxdbDatabase) { this.influxdbDatabase = influxdbDatabase; }

    @JsonProperty("influxdbDatabase")
    public String getInfluxdbDatabase() { return this.influxdbDatabase; }

    @JsonProperty("influxdbUsername")
    public void setInfluxdbUsername(String influxdbUsername) { this.influxdbUsername = influxdbUsername; }

    @JsonProperty("influxdbUsername")
    public String getInfluxdbUsername() { return this.influxdbUsername; }

    @JsonProperty("influxdbPassword")
    public void setInfluxdbPassword(String influxdbPassword) { this.influxdbPassword = influxdbPassword; }

    @JsonProperty("influxdbPassword")
    public String getInfluxdbPassword() { return this.influxdbPassword; }

    @JsonProperty("grafanaUsername")
    public void setGrafanaUsername(String grafanaUsername) { this.grafanaUsername = grafanaUsername; }

    @JsonProperty("grafanaUsername")
    public String getGrafanaUsername() { return this.grafanaUsername; }

    @JsonProperty("grafanaPassword")
    public void setGrafanaPassword(String grafanaPassword) { this.grafanaPassword = grafanaPassword; }

    @JsonProperty("grafanaPassword")
    public String getGrafanaPassword() { return this.grafanaPassword; }

    @JsonProperty("grafanaEndpoint")
    public void setGrafanaEndpoint(String grafanaEndpoint) { this.grafanaEndpoint = grafanaEndpoint; }

    @JsonProperty("grafanaEndpoint")
    public String getGrafanaEndpoint() { return this.grafanaEndpoint; }
}
