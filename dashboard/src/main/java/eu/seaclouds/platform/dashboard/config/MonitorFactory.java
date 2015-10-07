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

package eu.seaclouds.platform.dashboard.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class MonitorFactory extends BasicEndpointFactory {

    @NotEmpty
    private String grafanaHost;

    @NotEmpty
    private String graphiteHost;

    @Min(1)
    @Max(65535)
    private int grafanaPort = 3000;

    @Min(1)
    @Max(65535)
    private int graphitePort = 80;

    @JsonProperty
    public String getGrafanaHost() {
        return grafanaHost;
    }

    @JsonProperty
    public void setGrafanaHost(String grafanaHost) {
        this.grafanaHost = grafanaHost;
    }

    @JsonProperty
    public String getGraphiteHost() {
        return graphiteHost;
    }

    @JsonProperty
    public void setGraphiteHost(String graphiteHost) {
        this.graphiteHost = graphiteHost;
    }

    @JsonProperty
    public int getGrafanaPort() {
        return grafanaPort;
    }

    @JsonProperty
    public void setGrafanaPort(int grafanaPort) {
        this.grafanaPort = grafanaPort;
    }

    @JsonProperty
    public int getGraphitePort() {
        return graphitePort;
    }

    @JsonProperty
    public void setGraphitePort(int graphitePort) {
        this.graphitePort = graphitePort;
    }

    public String getGraphiteEndpoint() {
        return graphiteHost + ":" + graphitePort;
    }

    public String getGrafanaEndpoint() {
        return grafanaHost + ":" + grafanaPort;
    }
}
