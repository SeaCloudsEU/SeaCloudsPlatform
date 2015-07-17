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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.atos.sla.parser.DateTimeDeserializerJSON;
import eu.atos.sla.parser.DateTimeSerializerJSON;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "metric")
public class MonitoringMetric {

    @XmlElement(name = "key")
    private String metricKey;
    
    @XmlElement(name = "value")
    private String metricValue;

    @JsonSerialize(using=DateTimeSerializerJSON.class)
    @JsonDeserialize(using=DateTimeDeserializerJSON.class)
    @XmlElement(name = "datetime")    
    private Date date;

    
    public String getMetricKey() {
        return metricKey;
    }

    public String getMetricValue() {
        return metricValue;
    }

    public Date getDate() {
        return date;
    }
    
    @Override
    public String toString() {
        return String.format(
                "MonitoringMetric[metricKey='%s', metricValue='%s', date='%s'(%d)]",
                metricKey, metricValue, date, date.getTime());
    }
}
