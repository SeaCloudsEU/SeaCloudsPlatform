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

import it.polimi.tower4clouds.rules.MonitoringRule;
import it.polimi.tower4clouds.rules.MonitoringRules;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;


public class MonitorProxy extends AbstractProxy {

    /**
     * Creates proxied HTTP GET request to Tower4Clouds which returns the list of installed Monitoring Rules
     * @return installed MonitoringRules in Tower4Clouds
     */
    public MonitoringRules listMonitoringRules(){
        return getJerseyClient().target(getEndpoint() + "/v1/monitoring-rules").request()
                .buildGet().invoke().readEntity(MonitoringRules.class);
    }

    /**
     * Creates proxied HTTP POST request to Tower4Clouds which installs the corresponding Monitoring Rules
     * @param monitoringRules MonitoringRules to install in Tower4Clouds
     * @return String representing that the rules were installed properly
     */
     public String addMonitoringRules(MonitoringRules monitoringRules){
        Entity content = Entity.entity(monitoringRules, MediaType.APPLICATION_XML);
        Invocation invocation = getJerseyClient().target(getEndpoint() + "/v1/monitoring-rules").request().buildPost(content);
        return  invocation.invoke().readEntity(String.class);
    }

    /**
     * Creates proxied HTTP DELETE request which removes an installed Monitoring Rule in Tower4Clouds
     * @param monitoringRuleId of the desired Monitoring Rule. This ID may differ from SeaClouds Application ID
     * @return String representing that the rules were removed properly
     */
    public String removeMonitoringRule(String monitoringRuleId){
        return getJerseyClient().target(getEndpoint() + "/v1/monitoring-rules/" + monitoringRuleId).request()
                .buildDelete().invoke().readEntity(String.class);
    }

}
