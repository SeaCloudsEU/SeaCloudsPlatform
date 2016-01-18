/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.seaclouds.platform.discoverer.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;


public class DiscovererConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty("activeCrawlers")
    private ArrayList<String> activeCrawlers = new ArrayList<String>();

    @Valid
    @JsonProperty("databaseURL")
    private String databaseURL;

    @Valid
    @JsonProperty("databasePort")
    private Integer databasePort;

    @JsonProperty("activeCrawlers")
    public ArrayList<String> getActiveCrawlers() { return this.activeCrawlers; }

    @JsonProperty("activeCrawlers")
    public void setActiveCrawlers(ArrayList<String> activeCrawlers) {
        for (String crawlerName : activeCrawlers) {
            this.activeCrawlers.add(crawlerName);
        }
    }

    @JsonProperty("databaseURL")
    public String getDatabaseURL() {
        return this.databaseURL;
    }

    @JsonProperty("databaseURL")
    public void setDatabaseURL(String databaseURL) {
        this.databaseURL = databaseURL;
    }

    @JsonProperty("databasePort")
    public Integer getDatabasePort() {
        return 27017;
    }

    @JsonProperty("databasePort")
    public Integer setDatabasePort(Integer port) {
        return this.databasePort = port;
    }
}

