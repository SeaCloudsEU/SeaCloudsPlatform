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
package dataCollector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import config.EnvironmentReader;
import exception.ConfigurationException;
import exception.MetricNotAvailableException;
import it.polimi.tower4clouds.data_collector_library.DCAgent;
import it.polimi.tower4clouds.manager.api.ManagerAPI;
import it.polimi.tower4clouds.model.data_collectors.DCDescriptor;
import it.polimi.tower4clouds.model.ontology.CloudProvider;
import it.polimi.tower4clouds.model.ontology.ExternalComponent;
import it.polimi.tower4clouds.model.ontology.InternalComponent;
import it.polimi.tower4clouds.model.ontology.Location;
import it.polimi.tower4clouds.model.ontology.PaaSService;
import it.polimi.tower4clouds.model.ontology.Resource;
import it.polimi.tower4clouds.model.ontology.VM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NuroApplicationDC implements Observer {

      private Logger logger = LoggerFactory.getLogger(NuroApplicationDC.class);

      public void startMonitor(EnvironmentReader config) throws ConfigurationException {

            MetricManager manager=new MetricManager();
            
            String username;
            String password;
            String sensorUrl;

            
            logger.info("Start collecting NURO sensors data...");

            
            DCAgent dcAgent = new DCAgent(new ManagerAPI(config.getMmIP(),
                        config.getMmPort()));
            
            DCDescriptor dcDescriptor = new DCDescriptor();
            
            if (config.getInternalComponentId() != null) {
                  dcDescriptor.addResource(buildInternalComponent(config));
                  dcDescriptor.addMonitoredResource(manager.getApplicationMetrics(),
                              buildInternalComponent(config));
            }
            if (config.getVmId() != null) {
                  dcDescriptor.addResource(buildExternalComponent(config));
            }
            dcDescriptor.addResources(buildRelatedResources(config));
            dcDescriptor.setConfigSyncPeriod(config.getDcSyncPeriod());
            dcDescriptor.setKeepAlive(config.getResourcesKeepAlivePeriod());
            dcAgent.setDCDescriptor(dcDescriptor);
            dcAgent.addObserver(this);
            dcAgent.start();
            
            String monitoredTargetId=EnvironmentReader
            .getInstance().getInternalComponentId();
            String monitoredTargetType=EnvironmentReader.getInstance()
                    .getInternalComponentType();

            HttpClient httpClient = HttpClientBuilder.create().build();

            while (true) {
                  try {

                        for (String metric : manager.getApplicationMetrics()) {
                              if (dcAgent.shouldMonitor(new InternalComponent(
                                          monitoredTargetType, monitoredTargetId),
                                          metric)) {
                            	                                  
                                    username = dcAgent.getParameters(metric)
                                                .get("userName");
                                    password = dcAgent.getParameters(metric)
                                                .get("password");
                                    sensorUrl =dcAgent.getParameters(metric).get("sensorUrl");
                                    String auth = username + ":" + password;
                                    String encodedAuth = Base64.encodeBase64String(auth
                                                .getBytes());
                                    HttpGet httpget = new HttpGet(sensorUrl);
                                    httpget.addHeader("Authorization", "Basic "
                                                + encodedAuth);
                                    HttpResponse response = httpClient.execute(httpget);
                                    HttpEntity responseEntity = response.getEntity();
                                    InputStream stream = responseEntity.getContent();
                                    BufferedReader reader = new BufferedReader(
                                                new InputStreamReader(stream));
                                    StringBuilder out = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                          out.append(line);
                                    }
                                    reader.close();

                                    ObjectMapper mapper = new ObjectMapper();
                                    JsonNode actualObj = mapper.readTree(out.toString());

                                    Object toSend=manager.getMetricValue(metric, actualObj);
                                    

                                    logger.info("Sending datum: {} {} {}",toSend, metric, monitoredTargetId);
                                    dcAgent.send(new InternalComponent(monitoredTargetType,
                                                monitoredTargetId), metric,
                                                            toSend);
                                    Thread.sleep(Integer.parseInt(dcAgent.getParameters(
                                                metric).get("samplingTime")) * 1000);
                              }

                        }

                  } catch (IOException e) {
                   logger.debug("There were some problem executing NURO sensor HTTP request or processing the HTTP response!");;
                   e.printStackTrace();
                  } catch (InterruptedException e) {
                   logger.debug("Data collector interrupeted while waiting to sample a new datum!");;
                   e.printStackTrace();
                  } catch (MetricNotAvailableException e) {
                   logger.debug("Currently used version of NURO sensor does not provide the requested metric!");;
                      e.printStackTrace();
                  }
            }

      }

      private static Set<Resource> buildRelatedResources(EnvironmentReader config) {
            Set<Resource> relatedResources = new HashSet<Resource>();
            if (config.getCloudProviderId() != null) {
                  relatedResources.add(new CloudProvider(config
                              .getCloudProviderType(), config.getCloudProviderId()));
            }
            if (config.getLocationId() != null) {
                  relatedResources.add(new Location(config.getLocationtype(), config
                              .getLocationId()));
            }
            return relatedResources;
      }

      private static Resource buildExternalComponent(EnvironmentReader config)
                  throws ConfigurationException {
            ExternalComponent externalComponent;
            if (config.getVmId() != null) {
                  externalComponent = new VM();
                  externalComponent.setId(config.getVmId());
                  externalComponent.setType(config.getVmType());
            } else if (config.getPaasServiceId() != null) {
                  externalComponent = new PaaSService();
                  externalComponent.setId(config.getPaasServiceId());
                  externalComponent.setType(config.getPaasServiceType());
            } else {
                  throw new ConfigurationException(
                              "Neither VM nor PaaS service were specified");
            }
            if (config.getCloudProviderId() != null)
                  externalComponent.setCloudProvider(config.getCloudProviderId());
            if (config.getLocationId() != null)
                  externalComponent.setLocation(config.getLocationId());
            return externalComponent;
      }

      private static Resource buildInternalComponent(EnvironmentReader config) {
            InternalComponent internalComponent = new InternalComponent(
                        config.getInternalComponentType(),
                        config.getInternalComponentId());
            if (config.getVmId() != null)
                  internalComponent.addRequiredComponent(config.getVmId());
            return internalComponent;
      }

      public void update(Observable arg0, Object arg1) {
            // not used
      }

}
