package eu.seaclouds.monitor.monitoringdamgeneratorservice;

import java.util.List;

public class ApplicationToscaDcScripts {
    
    List<String> applicationToscaDcScripts;
    
    public ApplicationToscaDcScripts(List<String> scripts){
        this.applicationToscaDcScripts = scripts;
    }
    
    public List<String> getApplicationToscaDcScripts() {
        return applicationToscaDcScripts;
    }

    public void setApplicationToscaDcScripts(List<String> applicationToscaDcScripts) {
        this.applicationToscaDcScripts = applicationToscaDcScripts;
    }

}
