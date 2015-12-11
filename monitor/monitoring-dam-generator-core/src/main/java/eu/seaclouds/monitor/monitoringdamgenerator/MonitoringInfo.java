package eu.seaclouds.monitor.monitoringdamgenerator;

import it.polimi.tower4clouds.rules.MonitoringRules;

public final class MonitoringInfo {

    private String returnedAdp;
    private MonitoringRules applicationRules;

    public MonitoringInfo(MonitoringRules applicationRules, String returnedAdp) {
        this.applicationRules = applicationRules;
        this.returnedAdp = returnedAdp;
    }

    public String getReturnedAdp() {
        return returnedAdp;
    }

    public MonitoringRules getApplicationMonitoringRules() {
        return applicationRules;
    }

}
