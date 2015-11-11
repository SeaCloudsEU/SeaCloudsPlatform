package eu.seaclouds.monitor.monitoringDamGenerator.adpParsing;

import it.polimi.tower4clouds.rules.MonitoringRules;

import java.util.Map;

public class Host {
    
    private String hostName;
    private String deploymentType;
    private MonitoringRules rules;
    private Map<String,String> dataCollectorBashDeploymentScripts;
    private Map<String,String> dataCollectorToscaDeploymentScripts;
    
    public String getHostName() {
        return hostName;
    }
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
    public MonitoringRules getRules() {
        return rules;
    }
    public void setRules(MonitoringRules rules) {
        this.rules = rules;
    }
    public Map<String, String> getDataCollectorBashDeploymentScripts() {
        return dataCollectorBashDeploymentScripts;
    }
    public void setDataCollectorBashDeploymentScripts(
            Map<String, String> dataCollectorBashDeploymentScripts) {
        this.dataCollectorBashDeploymentScripts = dataCollectorBashDeploymentScripts;
    }
    public Map<String, String> getDataCollectorToscaDeploymentScripts() {
        return dataCollectorToscaDeploymentScripts;
    }
    public void setDataCollectorToscaDeploymentScripts(
            Map<String, String> dataCollectorToscaDeploymentScripts) {
        this.dataCollectorToscaDeploymentScripts = dataCollectorToscaDeploymentScripts;
    }
    public String getDeploymentType() {
        return deploymentType;
    }
    public void setDeploymentType(String deploymentType) {
        this.deploymentType = deploymentType;
    }
}
