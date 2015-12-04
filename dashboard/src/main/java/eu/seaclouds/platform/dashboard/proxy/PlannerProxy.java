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

import it.polimi.tower4clouds.rules.MonitoringRules;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;


public class PlannerProxy extends AbstractProxy {

    /**
     * Creates proxied HTTP GET request to SeaClouds Planner which returns the monitoring rules according to the template id
     * @param monitoringRuleTemplateId Monitoring Rules template id
     * @return MonitoringRules ready to be installed in Tower4Clouds
     */
    public MonitoringRules getMonitoringRulesByTemplateId(String monitoringRuleTemplateId) {
        return getJerseyClient().target(getEndpoint() + "/planner/monitoringrules/" + monitoringRuleTemplateId).request()
                .buildGet().invoke().readEntity(MonitoringRules.class);
    }


    /**
     * Creates proxied HTTP POST request to SeaClouds Planner which returns a list TOSCA compliant SeaClouds ADP in JSON format
     * @param aam file compliant with SeaClouds AAM definition
     * @return String representing a List of ADP files compliant with SeaClouds ADP definition
     */
    public String getAdps(String aam) {
        Entity content = Entity.entity(aam, MediaType.TEXT_PLAIN);
        Invocation invocation = getJerseyClient().target(getEndpoint() + "/planner/plan").request().buildPost(content);
        return invocation.invoke().readEntity(String.class);
    }


    /**
     * Creates proxied HTTP POST request to SeaClouds Planner which returns a SeaClouds TOSCA DAM file
     * @param adp file compliant with SeaClouds ADP definition
     * @return TOSCA DAM compliant with SeaClouds TOSCA DAM definition
     */
    public String getDam(String adp) {
        Entity content = Entity.entity(adp, MediaType.TEXT_PLAIN);
        Invocation invocation = getJerseyClient().target(getEndpoint() + "/planner/damgen").request().buildPost(content);
        return invocation.invoke().readEntity(String.class);
    }
}
