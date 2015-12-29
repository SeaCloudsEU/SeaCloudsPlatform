package eu.seaclouds.monitor.monitoringdamgenerator.adpparsing;

import eu.seaclouds.monitor.monitoringdamgenerator.DeploymentType;

public class Host {

    private String hostName;
    private DeploymentType deploymentType;
    
    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public DeploymentType getDeploymentType() {
        return deploymentType;
    }

    public void setDeploymentType(DeploymentType deploymentType) {
        this.deploymentType = deploymentType;
    }
}
