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

import brooklyn.rest.client.BrooklynApi;
import brooklyn.rest.domain.ApplicationSummary;
import eu.seaclouds.monitor.reconfigurationDataCollector.exception.ConfigurationException;
import eu.seaclouds.monitor.reconfigurationDataCollector.exception.MetricNotAvailableException;

public class Main {
    public static void main(String[] args) {

        BrooklynApi deployer=new BrooklynApi("http://127.0.0.1:8081/", "",
                "");
        
        List<ApplicationSummary> apps = deployer.getApplicationApi().list(
                null);
        for (ApplicationSummary app : apps) {

           System.out.println(app.getId());
           System.out.println(app.getStatus());
        }
        
        /*
        DeployerDC dc = new DeployerDC();
        

        try {

            dc.startMonitor();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        */

    }

}
