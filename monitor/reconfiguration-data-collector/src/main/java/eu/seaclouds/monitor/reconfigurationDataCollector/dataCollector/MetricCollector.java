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
package eu.seaclouds.monitor.reconfigurationDataCollector.dataCollector;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.rest.client.BrooklynApi;
import brooklyn.rest.domain.ApplicationSummary;
import eu.seaclouds.monitor.reconfigurationDataCollector.exception.ConfigurationException;
import eu.seaclouds.monitor.reconfigurationDataCollector.exception.MetricNotAvailableException;
import eu.seaclouds.monitor.reconfigurationDataCollector.config.EnvironmentReader;

public class MetricCollector implements Runnable {

    private Logger logger = LoggerFactory.getLogger(MetricCollector.class);

    private int samplingTime = 10;
    private String monitoredMetric;

    private BrooklynApi deployer;

    @Override
    public void run() {

        MetricManager manager = new MetricManager();
        EnvironmentReader config = null;
        try {
            config = EnvironmentReader.getInstance();
        } catch (ConfigurationException e1) {
            logger.warn(e1.getMessage());
            throw new IllegalStateException(e1.getMessage(), e1.getCause());
        }

        deployer = new BrooklynApi("http://" + config.getDeployerIP() + ":"
                + config.getDeployerPort() + "/", config.getDeployerUsername(),
                config.getDeployerPassword());

        while (true) {
            List<ApplicationSummary> apps = deployer.getApplicationApi().list(
                    null);
            for (ApplicationSummary app : apps) {

                if (DeployerDC.shouldMonitor(monitoredMetric, app)) {

                    try {

                        Object toSend = manager.getMetricValue(monitoredMetric,
                                app);
                        DeployerDC.send(monitoredMetric, app.getId(), toSend);

                        Thread.sleep(samplingTime * 1000);
                    } catch (InterruptedException e) {
                        logger.warn(e.getMessage());
                        throw new IllegalStateException(e.getMessage(), e.getCause());
                    } catch (MetricNotAvailableException e) {
                        logger.warn(e.getMessage());
                        throw new IllegalStateException(e.getMessage(), e.getCause());
                    }
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