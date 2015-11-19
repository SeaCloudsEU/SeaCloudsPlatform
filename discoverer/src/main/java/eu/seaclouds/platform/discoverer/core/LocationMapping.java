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


package eu.seaclouds.platform.discoverer.core;

import java.util.HashMap;

public class LocationMapping {

    private static HashMap<String, String> map;

    static {
        HashMap<String, String> initializedMap = new HashMap<>();

        initializedMap.put("Amazon_EC2", "aws-ec2");
        initializedMap.put("SoftLayer_Cloud_Servers", "softlayer");
        initializedMap.put("Microsoft_Azure_Virtual_Machines", "azurecompute");
        initializedMap.put("Google_Compute_Engine", "google-compute-engine");
        initializedMap.put("HP_Cloud_Compute", "hpcloud-compute");
        initializedMap.put("Cloud_Foundry", "CloudFoundry");

        //initializedMap.put("Rackspace_Cloud_Servers", "");
        // Rackspace provides more location so it is not yes possible to make an unique mapping

        map = initializedMap;
    }

    /**
     * Gets the sanitized location of an offering
     *
     * @param providerName the name of the provider
     * @return the sanitized location string if sanitizable, null otherwise
     */
    public static String getLocation(String providerName) {
        for (String key : map.keySet()) {
            if (providerName.startsWith(key)) {
                return map.get(key);
            }
        }

        return null;
    }

}
