/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
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
package core;


import com.google.common.collect.Lists;
import metrics.Metric;
import metrics.MetricCatalog;
import model.Application;
import model.Module;
import model.exceptions.*;

import java.util.*;

/**
 * Created by Adrian on 14/10/2014.
 */
public class Manager {

    private static Manager instance;
    private List<Application> applicationList;
    private Map<Module, List<Connector>> connectorMap;

    private Manager(){
        applicationList = new LinkedList<>();
        connectorMap = new HashMap<>();
    }

    public static Manager getInstance(){
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }

    public void registerApplication(Application app){
        applicationList.add(app);
    }

    public void addMonitoringAgent(Module module, Connector connector){
        List<Connector> connectorList = connectorMap.get(module);
        if (connectorList == null) {
            // First time connecting the module -> connector list has to be created
            connectorList = Lists.newArrayList();
            connectorList.add(connector);
            connectorMap.put(module, connectorList);

        }else if (connectorList.contains(connector)) {
            //TODO: Add connector toString to get pretty error msgs
            throw new DuplicatedConnectorException(connector.toString(), module.getId());

        } else {
            // Adding connector to the list
            connectorList.add(connector);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getMetricValue(Module module, String id) throws MonitorConnectorException {

        List<Connector> connectorList = connectorMap.get(module);
        if ( connectorList == null)
            throw new ModuleNotFoundException(module.getId());

        Metric metric = MetricCatalog.getInstance().getMetric(id);
        if (metric == null)
            throw new MetricNotFoundException(id);

        for (Connector conn : connectorList){
            if (conn.getAvailableMetrics().contains(metric)){
                return (T) conn.getValue(metric);
            }
        }

        // If reached here metric has not been found in any connector
        throw new MetricNotFoundException(id);
    }
}
