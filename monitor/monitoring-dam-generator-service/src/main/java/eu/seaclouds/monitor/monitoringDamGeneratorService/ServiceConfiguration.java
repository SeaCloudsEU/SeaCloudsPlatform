package eu.seaclouds.monitor.monitoringDamGeneratorService;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class ServiceConfiguration extends Configuration {

    
    @NotEmpty
    private String monitorIp;
    
    @NotEmpty
    private String monitorPort;
    
    @JsonProperty("monitorIp")
    public String getHost() {
        return monitorIp;
    }

    @JsonProperty("monitorIp")
    public void setHost(String monitorIp) {
        this.monitorIp = monitorIp;
    }

    @JsonProperty("monitorPort")
    public String getPort() {
        return monitorPort;
    }

    @JsonProperty("monitorPort")
    public void setPort(String monitorPort) {
        this.monitorPort = monitorPort;
    }
}
