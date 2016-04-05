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
package eu.seaclouds.platform.planner.core.application.topology.modifier.relation;


import eu.seaclouds.platform.planner.core.application.topology.nodetemplate.NodeTemplate;

import java.util.Map;

public class PhpDatabasePaaSRelationModifier extends AbstractPhpDatabaseRelationModifier {

    private static final String PROP_COLLECTION_VALUE = "env";

    @Override
    protected boolean nodeTemplateCanBeModified(NodeTemplate nodeTemplate) {
        return !topologyTemplate.isDeployedOnIaaS(nodeTemplate.getNodeTemplateId());
    }

    @Override
    protected String getPropCollection(Map<String, Object> requirementValues) {
        return PROP_COLLECTION_VALUE;
    }
}
