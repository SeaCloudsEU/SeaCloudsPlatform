package eu.seaclouds.monitor.datacollector;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class DCConfiguration extends Configuration {
    @NotEmpty
    private String manager_ip;

    @NotEmpty
    private String manager_port;
    
    @NotEmpty
    private String dc_sync_period;    
        
    @NotEmpty
    private String resources_keep_alive_period;

    @JsonProperty
    public String getManager_ip() {
        return manager_ip;
    }
    
    @JsonProperty
    public void setManager_ip(String manager_ip) {
        this.manager_ip = manager_ip;
    }

    @JsonProperty
    public String getManager_port() {
        return manager_port;
    }

    @JsonProperty
    public void setManager_port(String manager_port) {
        this.manager_port = manager_port;
    }

    @JsonProperty
    public String getDc_sync_period() {
        return dc_sync_period;
    }

    @JsonProperty
    public void setDc_sync_period(String dc_sync_period) {
        this.dc_sync_period = dc_sync_period;
    }

    @JsonProperty
    public String getResources_keep_alive_period() {
        return resources_keep_alive_period;
    }

    @JsonProperty
    public void setResources_keep_alive_period(String resources_keep_alive_period) {
        this.resources_keep_alive_period = resources_keep_alive_period;
    }
}
