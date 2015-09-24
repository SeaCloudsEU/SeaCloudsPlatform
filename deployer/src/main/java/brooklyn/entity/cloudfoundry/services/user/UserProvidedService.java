/*
 * Copyright 2015 SeaClouds
 * Contact: dev@seaclouds-project.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package brooklyn.entity.cloudfoundry.services.user;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.cloudfoundry.services.CloudFoundryService;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.basic.MapConfigKey;
import brooklyn.util.flags.SetFromFlag;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

/**
 * This Interface represent a generic and empty service.
 * It could be bound to any application but the operation it will be empty.
 * The ServiceType is not predefined and it should be defined for the service creation.
 */

@ImplementedBy(UserProvidedServiceImpl.class)
public interface UserProvidedService extends CloudFoundryService {

    @SetFromFlag("serviceType")
    public static final ConfigKey<String> SERVICE_TYPE = ConfigKeys
            .newStringConfigKey("service.type", "Type Of Service", "user-provided");

    @SetFromFlag("credentials")
    public static final MapConfigKey<Object> CREDENTIALS =
            new MapConfigKey<>(Object.class, "cloudfoundry.service.user.credentials",
                    "Credentials to access the user-provided service",
                    ImmutableMap.<String, Object>of());

    public Map<String,Object> getCredentials();

    public String getServiceName();
}
