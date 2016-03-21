/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.seaclouds.platform.planner.core.template;

import java.util.List;
import java.util.Map;

public interface NodeTemplate {

    public static final String TYPE = "type";

    public Map<String, Object> transform();

    boolean isDeployedOnIaaS();

    public Map<String, Object> getNodeTypeDefinition();

    public String getModuleType();

    public String getType();

    public String getHostNodeName();

    List<Map<String, Object>> getArtifacts();
}
