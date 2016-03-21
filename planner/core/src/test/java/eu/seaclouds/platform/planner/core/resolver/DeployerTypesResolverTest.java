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
package eu.seaclouds.platform.planner.core.resolver;

import com.google.common.io.Resources;
import eu.seaclouds.platform.planner.core.resolver.DeployerTypesResolver;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Test
public class DeployerTypesResolverTest {

    final private static String TYPES_TEST_MAPPING = "mapping/test-types-mapping.yaml";
    final private static String NODETYPE_TEST_MAPPING = "mapping/test-NodeTypes-mapping.yaml";
    final private static String RELATIONSHIPTYPE_TEST_MAPPING =
            "mapping/test-RelationshipTypes-mapping.yaml";

    @SuppressWarnings("unchecked")
    public void testResolvingTargetDeployerTypes() throws URISyntaxException, IOException {
        DeployerTypesResolver typesResolver = new DeployerTypesResolver(Resources
                .getResource(TYPES_TEST_MAPPING).toURI().toString());

        assertEquals(typesResolver.resolveNodeType("source.NodeType1"), "target.NodeType1");
        assertEquals(typesResolver.resolveNodeType("source.NodeType2"), "target.NodeType2");
        assertNull(typesResolver.resolveNodeType("source.NodeType_X"));

        assertEquals(typesResolver.resolveRelationshipType("source.RelationshipType1"),
                "target.RelationshipType1");
        assertEquals(typesResolver.resolveRelationshipType("source.RelationshipType2"),
                "target.RelationshipType2");
        assertNull(typesResolver.resolveRelationshipType("source.RelationshipType_X"));

        assertNotNull(typesResolver.getNodeTypeDefinition("target.NodeType1"));
        assertNull(typesResolver.getNodeTypeDefinition("target.NodeType2"));
        assertNotNull(typesResolver.getNodeTypeDefinition("target.NodeType3"));

        assertTrue(typesResolver.getNodeTypeDefinition("target.NodeType1") instanceof Map);
        Map<String, Object> typeDefinition = (Map<String, Object>) typesResolver
                .getNodeTypeDefinition("target.NodeType1");
        assertEquals(typeDefinition.get("derived_from"), "tosca.nodes.Root");
    }

    public void testResolvingWithoutRelationshipTypes() throws URISyntaxException, IOException {
        DeployerTypesResolver typesResolver = new DeployerTypesResolver(Resources
                .getResource(NODETYPE_TEST_MAPPING).toURI().toString());

        assertEquals(typesResolver.resolveNodeType("source.NodeType1"), "target.NodeType1");
        assertEquals(typesResolver.resolveNodeType("source.NodeType2"), "target.NodeType2");
        assertNull(typesResolver.resolveNodeType("source.NodeType_X"));

        assertNull(typesResolver.resolveRelationshipType("source.RelationshipType1"));
        assertNull(typesResolver.resolveRelationshipType("source.RelationshipType2"));
        assertNull(typesResolver.resolveRelationshipType("source.RelationshipType_X"));
    }

    public void testResolvingWithNodeTypes() throws URISyntaxException, IOException {
        DeployerTypesResolver typesResolver = new DeployerTypesResolver(Resources
                .getResource(RELATIONSHIPTYPE_TEST_MAPPING).toURI().toString());

        assertNull(typesResolver.resolveNodeType("source.NodeType1"));
        assertNull(typesResolver.resolveNodeType("source.NodeType2"));
        assertNull(typesResolver.resolveNodeType("source.NodeType_X"));

        assertEquals(typesResolver.resolveRelationshipType("source.RelationshipType1"),
                "target.RelationshipType1");
        assertEquals(typesResolver.resolveRelationshipType("source.RelationshipType2"),
                "target.RelationshipType2");
        assertNull(typesResolver.resolveRelationshipType("source.RelationshipType_X"));
    }

    public void testResolvingPolicyType() throws URISyntaxException, IOException {
        DeployerTypesResolver typesResolver = new DeployerTypesResolver(Resources
                .getResource(TYPES_TEST_MAPPING).toURI().toString());

        assertEquals(typesResolver.resolvePolicyType("source.policy1"), "target.policy1");
        assertEquals(typesResolver.resolvePolicyType("source.policy2"), "target.policy2");
        assertNull(typesResolver.resolvePolicyType("source.policyX"));
    }

}

