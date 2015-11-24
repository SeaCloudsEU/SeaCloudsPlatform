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
package eu.seaclouds.platform.planner.core;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class DeployerTypesResolver {

    static Logger log = LoggerFactory.getLogger(DeployerTypesResolver.class);

    final private static String NODE_TYPES_SECTION = "mapping.nodeTypes";
    final private static String RELATIONSHIP_TYPES_SECTION = "mapping.relationshipTypes";


    Map<String, Object> mapping;
    Map<String, String> nodeTypesMapping;
    Map<String, String> relationshipTypesMapping;

    public DeployerTypesResolver(String mappingFile) throws IOException {
        this(new URL(mappingFile));
    }

    public DeployerTypesResolver(URL mappingFileUrl) throws IOException {
        Yaml yml = new Yaml();
        mapping = (Map<String, Object>) yml.load(
                Resources.toString(mappingFileUrl, Charsets.UTF_8));
        initTypesMapping();

    }

    /**
     * Initialize the different types mapping.
     */
    private void initTypesMapping() {
        if (mapping == null) {
            throw new IllegalStateException("Mapping does contain any information in " +
                    "DeployerTypesResolver " + this);
        }

        if (mapping.containsKey(NODE_TYPES_SECTION)) {
            log.debug("Mapping contains NodeTypes description");
            nodeTypesMapping = (Map<String, String>) mapping.get(NODE_TYPES_SECTION);
        }

        if (mapping.containsKey(RELATIONSHIP_TYPES_SECTION)) {
            log.debug("Mapping contains NodeTypes description");
            relationshipTypesMapping = (Map<String, String>) mapping
                    .get(RELATIONSHIP_TYPES_SECTION);
        }
    }

    public String resolveNodeType(String sourceNodeType) {
        if (nodeTypesMapping == null || nodeTypesMapping.isEmpty()) {
            log.debug("NodeType mapping was not initialized for " + this);
            return null;
        }
        return nodeTypesMapping.get(sourceNodeType);
    }

    public String resolveRelationshipType(String sourceRelationshipType) {
        if (relationshipTypesMapping == null || relationshipTypesMapping.isEmpty()) {
            log.debug("RelationshipType mapping was not initialized for " + this);
            return null;
        }
        return relationshipTypesMapping.get(sourceRelationshipType);
    }

}
