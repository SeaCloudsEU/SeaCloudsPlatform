/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package eu.seaclouds.apache;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.apache.brooklyn.api.entity.Application;
import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.api.sensor.AttributeSensor;
import org.apache.brooklyn.core.entity.Attributes;
import org.apache.brooklyn.core.entity.trait.Startable;
import org.apache.brooklyn.entity.php.PhpWebAppService;
import org.apache.brooklyn.entity.php.httpd.PhpHttpdServer;
import org.apache.brooklyn.launcher.camp.SimpleYamlLauncher;
import org.apache.brooklyn.test.Asserts;
import org.testng.annotations.Test;

@Test( groups={"Live"} )
public class PhpYamlTest {

    public static String APP_NAME = "test-app";
    
    @Test
    public void deployWebappWithServicesFromYaml(){
        SimpleYamlLauncher launcher = new SimpleYamlLauncher();
        launcher.setShutdownAppsOnExit(true);
        Application app = launcher.launchAppYaml("php-helloworld-tarball.yaml").getApplication();

        final PhpHttpdServer server = (PhpHttpdServer)
                findEntityChildByDisplayName(app, "PHP-HTTPD Server");

        Asserts.succeedsEventually(new Runnable() {
            public void run() {
                assertNotNull(server);

                assertTrue(server.getAttribute(Startable.SERVICE_UP));

                assertNotNull(server.getAttribute(Attributes.MAIN_URI));
                assertNotNull(server.getAttribute(PhpWebAppService.ROOT_URL));
                assertNotNull(server.getAttribute(PhpWebAppService.HTTP_PORT));

                assertNotNull(server.getConfig(PhpWebAppService.APP_NAME));
                assertEquals(server.getConfig(PhpWebAppService.APP_NAME), APP_NAME);
            }
        });
    }

    private String escapeString(String s) {
        return s.replace("/", "\\/");
    }

    private Entity findEntityChildByDisplayName(Application app, String displayName){
        for(Object entity: app.getChildren().toArray())
            if(((Entity)entity).getDisplayName().equals(displayName)){
                return (Entity)entity;
            }
        return null;
    }

    private Object findSensorValueByName(Entity entity, String sensorName){
        AttributeSensor<Object> sensor = findSensorByName(entity, sensorName);
        return entity.getAttribute(sensor);
    }

    @SuppressWarnings("unchecked")
    private AttributeSensor<Object> findSensorByName(Entity entity, String sensorName){
        return (AttributeSensor<Object>) entity.getEntityType().getSensor(sensorName);
    }
}
