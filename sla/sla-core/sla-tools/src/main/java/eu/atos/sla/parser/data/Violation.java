/**
 * Copyright 2014 Atos
 * Contact: Atos <roman.sosa@atos.net>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.atos.sla.parser.data;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.atos.sla.parser.DateTimeDeserializerJSON;
import eu.atos.sla.parser.DateTimeSerializerJSON;

/**
 * A POJO Object that stores all the information from a Violation
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "violation")
@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class Violation  {

    @XmlElement(name = "id")
    private Long id;
    
    @JsonProperty("uuid")
    @XmlElement(name = "uuid")
    private String uuid;
    
    @JsonProperty("contract_uuid")
    @XmlElement(name = "contract_uuid")
    private String contractUUID;
    
    @JsonProperty("service_name")
    @XmlElement(name = "service_name")
    private String serviceName;
    
    @JsonProperty("service_scope")
    @XmlElement(name = "service_scope")
    private String serviceScope;
    
    @JsonProperty("metric_name")
    @XmlElement(name = "metric_name")
    private String metricName;
    
    @JsonSerialize(using=DateTimeSerializerJSON.class)
    @JsonDeserialize(using=DateTimeDeserializerJSON.class)
    @JsonProperty("datetime")
    @XmlElement(name = "datetime")
    private Date datetime;

    @JsonProperty("expected_value")
    @XmlElement(name = "expected_value")
    private String expectedValue;
    
    @JsonProperty("actual_value")
    @XmlElement(name = "actual_value")
    private String actualValue;

    public Violation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContractUuid() {
        return contractUUID;
    }

    public void setContractUuid(String contractUUID) {
        this.contractUUID = contractUUID;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceScope() {
        return serviceScope;
    }

    public void setServiceScope(String serviceScope) {
        this.serviceScope = serviceScope;
    }

    public String getKpiName() {
        return metricName;
    }

    public void setKpiName(String metricName) {
        this.metricName = metricName;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }

    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }

}