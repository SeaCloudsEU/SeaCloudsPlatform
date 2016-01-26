package eu.seaclouds.monitor.monitoringdamgeneratorservice;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class ServiceConfiguration extends Configuration {

    @NotEmpty
    private String monitorIp;

    @NotEmpty
    private String monitorPort;

    @NotEmpty
    private String influxdbIp;

    @NotEmpty
    private String influxdbPort;

    @JsonProperty("monitorIp")
    public String getMonitorHost() {
        return monitorIp;
    }

    @JsonProperty("monitorIp")
    public void setMonitorHost(String monitorIp) {
        this.monitorIp = monitorIp;
    }

    @JsonProperty("monitorPort")
    public String getMonitorPort() {
        return monitorPort;
    }

    @JsonProperty("monitorPort")
    public void setMonitorPort(String monitorPort) {
        this.monitorPort = monitorPort;
    }

    @JsonProperty("influxdbIp")
    public String getInfluxdbHost() {
        return influxdbIp;
    }

    @JsonProperty("influxdbIp")
    public void setInfluxdbHost(String influxdbIp) {
        this.influxdbIp = influxdbIp;
    }

    @JsonProperty("influxdbPort")
    public String getInfluxdbPort() {
        return influxdbPort;
    }

    @JsonProperty("influxdbPort")
    public void setInfluxdbPort(String influxdbPort) {
        this.influxdbPort = influxdbPort;
    }
}
