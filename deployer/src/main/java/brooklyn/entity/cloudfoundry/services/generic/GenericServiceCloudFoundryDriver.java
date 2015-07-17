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
package brooklyn.entity.cloudfoundry.services.generic;


import brooklyn.entity.cloudfoundry.services.CloudFoundryServiceImpl;
import brooklyn.entity.cloudfoundry.services.PaasServiceCloudFoundryDriver;
import brooklyn.entity.cloudfoundry.webapp.CloudFoundryWebAppImpl;
import brooklyn.location.cloudfoundry.CloudFoundryPaasLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericServiceCloudFoundryDriver extends PaasServiceCloudFoundryDriver  implements GenericServiceDriver {

    public static final Logger log = LoggerFactory.getLogger(GenericServiceCloudFoundryDriver.class);

    public GenericServiceCloudFoundryDriver(CloudFoundryServiceImpl entity, CloudFoundryPaasLocation location) {
        super(entity, location);
    }

    @Override
    public void operation(CloudFoundryWebAppImpl app) {
        /*Empty*/
    }

    @Override
    public GenericServiceImpl getEntity() {
        return (GenericServiceImpl) super.getEntity();
    }


}
