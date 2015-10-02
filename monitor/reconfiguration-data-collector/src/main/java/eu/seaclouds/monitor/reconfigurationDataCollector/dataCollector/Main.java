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

import eu.seaclouds.monitor.reconfigurationDataCollector.config.EnvironmentReader;
import eu.seaclouds.monitor.reconfigurationDataCollector.exception.ConfigurationException;

public class Main 
{
    public static void main( String[] args )
    {
          DeployerDC dc=new DeployerDC();
          try {
                EnvironmentReader config = EnvironmentReader.getInstance();

                  dc.startMonitor(config);
            } catch (ConfigurationException e) {
                  e.printStackTrace();
            }

    }

  
            
    
}
