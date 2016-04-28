/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.seaclouds.platform.planner.core;


import java.net.MalformedURLException;
import java.net.URL;

import org.apache.brooklyn.util.exceptions.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DamGeneratorConfigBag {

    static Logger log = LoggerFactory.getLogger(DamGeneratorConfigBag.class);

    private static final String SEACLOUDS_DC_PORT = "8176";

    private String monitorUrl;
    private String monitorPort;
    private String slaEndpoint;
    private String influxdbUrl;
    private String influxdbPort;
    private String influxdbDatabase;
    private String influxdbUsername;
    private String influxdbPassword;
    private String grafanaUsername;
    private String grafanaPassword;
    private String grafanaEndpoint;
    private URL monitorEndPoint;
    private URL influxDbEndpoint;
    private String seacloudsDcEndPoint;

    public DamGeneratorConfigBag(Builder builder) {
        this.monitorUrl = builder.monitorUrl;
        this.monitorPort = builder.monitorPort;
        this.slaEndpoint = builder.slaUrl;
        this.influxdbUrl = builder.influxdbUrl;
        this.influxdbPort = builder.influxdbPort;
        this.influxdbDatabase = builder.influxdbDatabase;
        this.influxdbUsername = builder.influxdbUsername;
        this.influxdbPassword = builder.influxdbPassword;
        this.grafanaUsername = builder.grafanaUsername;
        this.grafanaPassword = builder.grafanaPassword;
        this.grafanaEndpoint = builder.grafanaEndpoint;
        init();
    }

    private void init() {
        monitorEndPoint = createMonitoringEndpoint();
        influxDbEndpoint = createInfluxDbEndpoint();
        seacloudsDcEndPoint = createSeaCloudsDcEndpoint();
    }

    private String createSeaCloudsDcEndpoint() {
        return "http://" + monitorUrl + ":" + SEACLOUDS_DC_PORT;

    }

    private URL createMonitoringEndpoint() {
        try {
            return new URL("http://" + monitorUrl + ":" + monitorPort + "");
        } catch (MalformedURLException e) {
            Exceptions.propagateIfFatal(e);
            log.warn("Error creating MonitoringEndpoint: http://" + monitorUrl + ":" + monitorPort);
        }
        return null;
    }

    private URL createInfluxDbEndpoint() {
        try {
            return new URL("http://" + influxdbUrl + ":" + influxdbPort + "");

        } catch (MalformedURLException e) {
            Exceptions.propagateIfFatal(e);
            log.warn("Error creating InfluxDbEndpoint: http://" + influxdbUrl + ":" + influxdbPort);
        }
        return null;
    }

    public String getSlaEndpoint() {
        return slaEndpoint;
    }

    public String getMonitorUrl() {
        return monitorUrl;
    }

    public String getMonitorPort() {
        return monitorPort;
    }

    public String getInfluxdbUrl() {
        return influxdbUrl;
    }

    public String getInfluxdbPort() {
        return influxdbPort;
    }

    public String getInfluxdbDatabase() {
        return influxdbDatabase;
    }

    public String getInfluxdbUsername() {
        return influxdbUsername;
    }

    public String getInfluxdbPassword() {
        return influxdbPassword;
    }

    public String getGrafanaUsername() {
        return grafanaUsername;
    }

    public String getGrafanaPassword() {
        return grafanaPassword;
    }

    public String getGrafanaEndpoint() {
        return grafanaEndpoint;
    }

    public String getSeacloudsDcEndPoint(){
        return seacloudsDcEndPoint;
    }

    public URL getMonitorEndpoint() {
        return monitorEndPoint;
    }

    public URL getInfluxDbEndpoint() {
        return influxDbEndpoint;
    }


    public static class Builder {

        private String monitorUrl;
        private String monitorPort;
        private String seacloudsDCEndpoint;
        private String slaUrl;
        private String influxdbUrl;
        private String influxdbPort;
        private String grafanaEndpoint;
        private String influxdbDatabase;
        private String influxdbUsername;
        private String influxdbPassword;
        private String grafanaUsername;
        private String grafanaPassword;

        public Builder() {
        }

        public Builder monitorUrl(String monitorUrl) {
            this.monitorUrl = monitorUrl;
            return this;
        }

        public Builder monitorPort(String monitorPort) {
            this.monitorPort = monitorPort;
            return this;
        }

        public Builder slaUrl(String slaUrl) {
            this.slaUrl = slaUrl;
            return this;
        }

        public Builder influxdbUrl(String influxdbUrl) {
            this.influxdbUrl = influxdbUrl;
            return this;
        }

        public Builder influxdbPort(String influxdbPort) {
            this.influxdbPort = influxdbPort;
            return this;
        }

        public Builder influxdbDatabase(String influxdbDatabase) {
            this.influxdbDatabase = influxdbDatabase;
            return this;
        }

        public Builder influxdbUsername(String influxdbUsername) {
            this.influxdbUsername = influxdbUsername;
            return this;
        }

        public Builder influxdbPassword(String influxdbPassword) {
            this.influxdbPassword = influxdbPassword;
            return this;
        }

        public Builder grafanaUsername(String grafanaUsername) {
            this.grafanaUsername = grafanaUsername;
            return this;
        }


        public Builder grafanaPassword(String grafanaPassword) {
            this.grafanaPassword = grafanaPassword;
            return this;
        }

        public Builder grafanaEndpoint(String grafanaEndpoint) {
            this.grafanaEndpoint = grafanaEndpoint;
            return this;
        }

        public DamGeneratorConfigBag build() {
            return new DamGeneratorConfigBag(this);
        }
    }

}
