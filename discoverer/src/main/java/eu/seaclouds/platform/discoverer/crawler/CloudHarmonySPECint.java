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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class CloudHarmonySPECint {

    private static HashMap<String, Integer> SPECint = new HashMap<>();
    static Logger log = LoggerFactory.getLogger(CloudHarmonySPECint.class);

    public static void initializeMap(InputStream resource) {
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            isr = new InputStreamReader(resource);
            br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                String parts[] = line.split("=");
                try {
                    Integer value = Integer.parseInt(parts[1]);
                    SPECint.put(parts[0], value);
                } catch (NumberFormatException e) {
                    log.error("Cannot parse integer value " + parts[1]);
                }
            }
        } catch (IOException e) {
            log.error("Cannot open location mapping file");
            log.error(e.getMessage());
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
                if (br != null) {
                    br.close();
                }

                resource.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
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
