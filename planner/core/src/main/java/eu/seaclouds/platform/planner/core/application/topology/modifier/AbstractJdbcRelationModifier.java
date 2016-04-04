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
package eu.seaclouds.platform.planner.core.application.topology.modifier;

import com.google.common.collect.ImmutableList;
import eu.seaclouds.platform.planner.core.DamGenerator;

import java.util.List;
import java.util.Map;

public abstract class AbstractJdbcRelationModifier extends AbstractRelationModifier {

    public static final String SUPPORTED_RELATIONS = "seaclouds.relations.databaseconnections.jdbc";
    public static final List<String> VALID_TARGET_NODES_TYPES =
            ImmutableList.of("org.apache.brooklyn.entity.database.mysql.MySqlNode");

    private static final String DB_NAME_PROPERTY_NAME = "db_name";
    private static final String DB_USER_PROPERTY_NAME = "db_user";
    private static final String DB_PASSWORD_PROPERTY_NAME = "db_password";

    @Override
    protected List<String> getSupportedRelationTypes() {
        return ImmutableList.of(SUPPORTED_RELATIONS);
    }

    @Override
    protected boolean isValidTargetNode(Map<String, Object> requirement) {
        return VALID_TARGET_NODES_TYPES.contains(getNodeTargetType(requirement));
    }

    @Override
    protected String getPropValue(Map<String, Object> requirement) {
        return createPropertyValue(requirement);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected String getPropName(Map<String, Object> requirement) {
        Map<String, Object> properties =
                (Map<String, Object>) getRequirementValues(requirement)
                        .get(DamGenerator.PROPERTIES);
        return (String) properties.get(PROP_NAME);
    }

    @Override
    protected String getRelationName() {
        return "dbConnection";
    }

    @SuppressWarnings("unchecked")
    private String createPropertyValue(Map<String, Object> requirement) {
        String targetId = getTargetNodeId(requirement);
        String dbName = (String) topologyTemplate.getPropertyValue(targetId, DB_NAME_PROPERTY_NAME);
        String dbUSer = (String) topologyTemplate.getPropertyValue(targetId, DB_USER_PROPERTY_NAME);
        String dbPass = (String) topologyTemplate
                .getPropertyValue(targetId, DB_PASSWORD_PROPERTY_NAME);

        return JdbcStringBuilder
                .buildConnectionString(targetId, nodeTemplate.getType(), dbName, dbUSer, dbPass);
    }

}
