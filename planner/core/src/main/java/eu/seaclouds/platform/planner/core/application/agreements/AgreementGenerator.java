/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.seaclouds.platform.planner.core.application.agreements;


import com.fasterxml.jackson.databind.ObjectMapper;
import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringInfo;
import eu.seaclouds.platform.planner.core.application.ApplicationMonitorId;
import eu.seaclouds.platform.planner.core.utils.HttpHelper;
import eu.seaclouds.platform.planner.core.utils.YamlParser;
import it.polimi.tower4clouds.rules.MonitoringRules;
import org.apache.brooklyn.util.collections.MutableList;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class AgreementGenerator {
    static Logger log = LoggerFactory.getLogger(AgreementGenerator.class);

    private static final String SLA_GEN_OP = "/seaclouds/templates";
    private static final String GET_AGREEMENT_OP = "/seaclouds/commands/fromtemplate";

    private String slaUrl;

    public AgreementGenerator(String slaUrl) {
        this.slaUrl = slaUrl;
    }

    public String generateAgreeemntId(String templateDescription, MonitoringInfo monitoringInfo) {
        String result = null;
        String monitoring = serializeToXml(monitoringInfo);
        Map<String, Object> template = YamlParser.load(templateDescription);

        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addTextBody("dam", YamlParser.dump(template))
                .addTextBody("rules", monitoring)
                .build();

        String slaInfoResponse = new HttpHelper(slaUrl)
                .postEntity(SLA_GEN_OP, httpEntity);
        checkNotNull(slaInfoResponse, "Error getting SLA info");
        try {
            ApplicationMonitorId applicationMonitoringId = new ObjectMapper()
                    .readValue(slaInfoResponse, ApplicationMonitorId.class);
            result = applicationMonitoringId.getId();
        } catch (IOException e) {
            log.error("Error AgreementTemplateId during dam generation {}", this);
        }
        return result;
    }

    public String getAgreement(String applicationMonitorId) {
        List<NameValuePair> paremeters = MutableList.of((NameValuePair)
                new BasicNameValuePair("templateId", applicationMonitorId));
        return new HttpHelper(slaUrl).getRequest(GET_AGREEMENT_OP, paremeters);

    }

    private static String serializeToXml(MonitoringInfo monitoringInfo) {
        StringWriter sw = new StringWriter();
        JAXBContext jaxbContext;
        String marshalledMonitoringRules = null;
        try {
            jaxbContext = JAXBContext.newInstance(MonitoringRules.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            jaxbMarshaller.marshal(monitoringInfo.getApplicationMonitoringRules(), sw);
            marshalledMonitoringRules = sw.toString();
        } catch (JAXBException e) {
            log.error("Monitoring rules {} can not be marshalled by addSeaCloudsPolicy in " +
                            "DamGenerator",
                    monitoringInfo.getApplicationMonitoringRules());
        }

        return marshalledMonitoringRules;
    }

}
