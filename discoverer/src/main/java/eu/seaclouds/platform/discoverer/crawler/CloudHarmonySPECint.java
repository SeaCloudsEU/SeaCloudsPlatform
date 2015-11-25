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


package eu.seaclouds.platform.discoverer.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class CloudHarmonySPECint {

    private static HashMap<String, Integer> SPECint = new HashMap<>();

    public static void initializeMap(InputStream resource) {
        try {
            InputStreamReader isr = new InputStreamReader(resource);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                String parts[] = line.split("=");
                try {
                    Integer value = Integer.parseInt(parts[1]);
                    SPECint.put(parts[0], value);
                } catch (NumberFormatException e) {
                    System.err.println("Cannot parse integer value " + parts[1]);
                }
            }

            br.close();
            isr.close();
            resource.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.print("Cannot open location mapping file");
        }
    }

    /**
     * Gets the SPECint of the specified instance of the specified offerings provider
     *
     * @param providerName the name of the offerings provider
     * @param instanceType istance type as specified in the CloudHarmony API
     * @return SPECint of the specified instance
     */
    public static Integer getSPECint(String providerName, String instanceType) {
        String key = providerName + "." + instanceType;
        return SPECint.get(key);
    }

}
