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

package eu.seaclouds.platform.dashboard.proxy;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.client.Client;

/**
 * AbstractProxy holds the necessary information to configure a Client for each SeaClouds MicroServices
 * each client is configured from Dropwizard YAML file. Please read README.md to further information.
 */
public abstract class AbstractProxy {

    @NotEmpty
    private String host;

    @Min(1)
    @Max(65535)
    private int port = 80;

    private String user;
    private String password;
    private Client jerseyClient;

    @JsonProperty
    public String getHost() {
        return host;
    }

    @JsonProperty
    public void setHost(String host) {
        this.host = host;
    }

    @JsonProperty
    public int getPort() {
        return port;
    }

    @JsonProperty
    public void setPort(int port) {
        this.port = port;
    }

    @JsonProperty
    public String getUser() {
        return user;
    }

    @JsonProperty
    public void setUser(String user) {
        this.user = user;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public Client getJerseyClient() {
        return jerseyClient;
    }

    public void setJerseyClient(Client jerseyClient) {
        this.jerseyClient = jerseyClient;
    }

    public String getEndpoint() {
        return host.startsWith("http://") ? host + ":" + port : "http://" + host + ":" + port;
    }
}
