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

import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

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
import brooklyn.rest.client.BrooklynApi;
import brooklyn.rest.domain.ApplicationSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.monitor.reconfigurationDataCollector.config.EnvironmentReader;
import eu.seaclouds.monitor.reconfigurationDataCollector.exception.ConfigurationException;

public class DeployerDC implements Observer {

      private Logger logger = LoggerFactory.getLogger(DeployerDC.class);

      public void startMonitor(EnvironmentReader config) throws ConfigurationException {


            logger.info("Start collecting app status from the Deployer sensors data...");
            
            String username;
            String password;
            String deployerIp;
            String deployerPort;
            

            DCAgent dcAgent = new DCAgent(new ManagerAPI(config.getMmIP(),
                        config.getMmPort()));
            
            DCDescriptor dcDescriptor = new DCDescriptor();
            
            if (config.getInternalComponentId() != null) {
                  dcDescriptor.addResource(buildInternalComponent(config));
                  dcDescriptor.addMonitoredResource(getApplicationMetrics(),
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


            while (true) {
                  try {

                        for (String metric : getApplicationMetrics()) {
                              if (dcAgent.shouldMonitor(new InternalComponent(
                                          monitoredTargetType, monitoredTargetId),
                                          metric)) {
                                
                                    username = dcAgent.getParameters(metric)
                                                .get("userName");
                                    password = dcAgent.getParameters(metric)
                                                .get("password");
                                    deployerIp = dcAgent.getParameters(metric)
                                                .get("deployerIp");
                                    deployerPort = dcAgent.getParameters(metric)
                                                .get("deployerPort");
                                    
                                    
                                    BrooklynApi deployer=new BrooklynApi("http://"+deployerIp+":" + deployerPort + "/",
                                            username, password);
                                    
                                    List<ApplicationSummary> apps = deployer.getApplicationApi().list(null);
                                    
                                    
                                    
                                    for(ApplicationSummary app: apps){
                                    
                                        Set<String> loc=app.getSpec().getLocations();
                                        String providers="";
                                        
                                        for(String l:loc){
                                           providers=providers+"-"+deployer.getLocationApi().get(l, "").getName();
                                        }
                                        
                                        if(app.getId().equals(monitoredTargetType)){
                                            dcAgent.send(new InternalComponent(monitoredTargetType,monitoredTargetId), metric,app.getId()+","+app.getStatus()+","+providers);         
                                        }
                                    }
                         
                                    Thread.sleep(Integer.parseInt(dcAgent.getParameters(
                                                metric).get("samplingTime")) * 1000);
                              }

                        }

                  } catch (InterruptedException e) {
                   logger.debug("Data collector interrupeted while waiting to sample a new datum!");;
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
      
      public Set<String> getApplicationMetrics() {

          Set<String> metrics = new HashSet<String>();
          metrics.add(Metrics.IS_APP_ON_FIRE);
          

          return metrics;
    }

}
