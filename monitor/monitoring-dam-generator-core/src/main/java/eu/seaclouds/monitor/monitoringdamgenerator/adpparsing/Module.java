package eu.seaclouds.monitor.monitoringdamgenerator.adpparsing;

import it.polimi.tower4clouds.rules.MonitoringRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Module {

    private double respTime = 0;
    private double availability = 0;
    private boolean isJavaApp;
    private String moduleName;
    private Host host;
    private String port;

    private List<Map<String, Object>> dataCollectors;
    private MonitoringRules monitoringRules;

    public Module() {
        dataCollectors = new ArrayList<Map<String, Object>>();
        monitoringRules = new MonitoringRules();
    }

    public void addApplicationMonitoringRules(MonitoringRules toAdd) {
        this.monitoringRules.getMonitoringRules().addAll(
                toAdd.getMonitoringRules());
    }

    public void addDataCollector(Map<String, Object> toAdd) {
        this.dataCollectors.add(toAdd);
    }

    public List<Map<String, Object>> getDataCollector() {
        return dataCollectors;
    }

    public MonitoringRules getApplicationMonitoringRules() {
        return monitoringRules;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleNane) {
        this.moduleName = moduleNane;
    }

    public double getResponseTime() {
        return respTime;
    }

    public void setResponseTimeMillis(Double resp) {
        this.respTime = resp;
    }

    public boolean existResponseTimeRequirement() {
        return respTime != 0.0;
    }

    public double getAvailability() {
        return availability;
    }

    public void setAvailability(double availability) {
        this.availability = availability;
    }

    public boolean existAvailabilityRequirement() {
        return availability != 0.0;
    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder(300);
        return sb.append("ModuleName= ").append(this.moduleName)
              .append(" RespTime= ").append(this.respTime)
              .append(" Availability= ").append(this.availability)
              .append(" Host= ").append(this.host.getHostName())
              .append(" DeploymentType= ").append(this.host.getDeploymentType())
              .append(" isJavaApp= ").append(this.isJavaApp)
              .toString();
    }

    public boolean isJavaApp() {
        return isJavaApp;
    }

    public void setJavaApp(boolean isJavaApp) {
        this.isJavaApp = isJavaApp;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }


}
