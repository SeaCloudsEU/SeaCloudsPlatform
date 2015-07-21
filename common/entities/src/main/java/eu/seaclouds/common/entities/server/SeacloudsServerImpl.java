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
package eu.seaclouds.common.entities.server;

import brooklyn.entity.basic.SoftwareProcessImpl;

public class SeacloudsServerImpl extends SoftwareProcessImpl implements SeacloudsServer {

    @Override
    public Class getDriverInterface() {
        return SeacloudsServerDriver.class;
    }

    @Override
    public String getShortName() {
        return "SeaClouds Server";
    }

    @Override
    protected void connectSensors() {
        super.connectSensors();
        connectServiceUpIsRunning();
    }

    @Override
    protected void disconnectSensors() {
        disconnectServiceUpIsRunning();
        super.disconnectSensors();
    }
    
    public String getConfigUrl() {
        return getAttribute(CONFIG_URL);
    }
    
    public Integer getPort() {
        return getAttribute(DASHBOARD_PORT);
    }
    
    public Integer getAdminPort() {
        return getAttribute(DASHBOARD_ADMIN_PORT);
    }

    public String getDeployerHost() {
        return getConfig(DEPLOYER_HOST);
    }

    public Integer getDeployerPort() {
        return getConfig(DEPLOYER_PORT);
    }

    public String getDeployerUser() {
        return getConfig(DEPLOYER_USERNAME);
    }
    
    public String getDeployerPassword() {
        return getConfig(DEPLOYER_PASSWORD);
    }
    
    public String getMonitorHost() {
        return getConfig(MONITOR_HOST);
    }
    
    public Integer getMonitorPort() {
        return getConfig(MONITOR_PORT);
    }
    
    public String getPlannerHost() {
        return getConfig(PLANNER_HOST);
    }
    
    public Integer getPlannerPort() {
        return getConfig(PLANNER_PORT);
    }
    
    public String getSlaHost() {
        return getConfig(SLA_HOST);
    }

    public Integer getSlaPort() {
        return getConfig(SLA_PORT);
    }
}
