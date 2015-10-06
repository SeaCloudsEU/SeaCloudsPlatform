/**
 * Copyright 2015 SeaCloudsEU
 * Contact: Michele Guerriero <michele.guerriero@mail.polimi.it>
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
package eu.seaclouds.monitor.nuroDataCollector.dataCollector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.monitor.nuroDataCollector.config.EnvironmentReader;
import eu.seaclouds.monitor.nuroDataCollector.exception.ConfigurationException;
import eu.seaclouds.monitor.nuroDataCollector.exception.MetricNotAvailableException;

public class MetricCollector implements Runnable {

    private Logger logger = LoggerFactory.getLogger(MetricCollector.class);

    private int samplingTime = 10;
    private String monitoredMetric;

    @Override
    public void run() {

        MetricManager manager = new MetricManager();
        EnvironmentReader config = null;
        try {
            config = EnvironmentReader.getInstance();
        } catch (ConfigurationException e1) {
            logger.warn(e1.getMessage());
            throw new IllegalStateException("", e1.getCause());
        }

        HttpClient httpClient = HttpClientBuilder.create().build();
        String auth = config.getNuroInstanceUsername() + ":"
                + config.getNuroInstancePassword();
        String encodedAuth = Base64.encodeBase64String(auth.getBytes());

        HttpGet httpget = new HttpGet("http://" + config.getNuroInstanceIP()
                + ":" + config.getNuroInstancePort() + "/sensor.php");
        httpget.addHeader("Authorization", "Basic " + encodedAuth);

        HttpResponse response;
        HttpEntity responseEntity;
        InputStream stream;
        BufferedReader reader;
        StringBuilder out;

        while (true) {
            if (NuroApplicationDC.shouldMonitor(monitoredMetric)) {
                try {
                    response = httpClient.execute(httpget);

                    responseEntity = response.getEntity();
                    stream = responseEntity.getContent();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    out = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        out.append(line);
                    }
                    reader.close();

                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode actualObj = mapper.readTree(out.toString());

                    Object toSend = manager.getMetricValue(monitoredMetric,
                            actualObj);

                    logger.info("Sending datum: {} {}", toSend, monitoredMetric);

                    NuroApplicationDC.send(monitoredMetric, toSend);

                    Thread.sleep(samplingTime * 1000);
                } catch (ClientProtocolException e) {
                    logger.warn(e.getMessage());
                    throw new IllegalStateException(e.getMessage(), e.getCause());
                } catch (IOException e) {
                    logger.warn(e.getMessage());
                    throw new IllegalStateException(e.getMessage(), e.getCause());
                } catch (MetricNotAvailableException e) {
                    logger.warn(e.getMessage());
                    throw new IllegalStateException(e.getMessage(), e.getCause());
                } catch (InterruptedException e) {
                    logger.warn(e.getMessage());
                    throw new IllegalStateException(e.getMessage(), e.getCause());
                }
            }
        }

    }

    public void setSamplingTime(int toSet) {
        samplingTime = toSet;
    }

    public int getSamplingTime() {
        return samplingTime;
    }

    public void setMonitoredMetric(String toSet) {
        monitoredMetric = toSet;
    }

    public String getMonitoredMetric() {
        return monitoredMetric;
    }

}
