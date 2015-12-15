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

import com.google.common.io.Resources;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

@Test
public class DeployerTypesResolverTest {

    final private static String TYPES_TEST_MAPPING = "mapping/test-types-mapping.yaml";
    final private static String NODETYPE__TEST_MAPPING = "mapping/test-NodeTypes-mapping.yaml";
    final private static String RELATIONSHIPTYPE__TEST_MAPPING = "mapping/test-RelationshipTypes-mapping.yaml";


    DeployerTypesResolver typesResolver;

    public void testResolvingTargetDeployerTypes() throws URISyntaxException, IOException {
        typesResolver = new DeployerTypesResolver(Resources
                .getResource(TYPES_TEST_MAPPING).toURI().toString());

        assertEquals(typesResolver.resolveNodeType("source.NodeType1"), "target.NodeType1");
        assertEquals(typesResolver.resolveNodeType("source.NodeType2"), "target.NodeType2");
        assertNull(typesResolver.resolveNodeType("source.NodeType_X"));

        assertEquals(typesResolver.resolveRelationshipType("source.RelationshipType1"),
                "target.RelationshipType1");
        assertEquals(typesResolver.resolveRelationshipType("source.RelationshipType2"),
                "target.RelationshipType2");
        assertNull(typesResolver.resolveRelationshipType("source.RelationshipType_X"));

    }

    public void testResolvingWithRelationshipTypes() throws URISyntaxException, IOException {
        typesResolver = new DeployerTypesResolver(Resources
                .getResource(NODETYPE__TEST_MAPPING).toURI().toString());

        assertEquals(typesResolver.resolveNodeType("source.NodeType1"), "target.NodeType1");
        assertEquals(typesResolver.resolveNodeType("source.NodeType2"), "target.NodeType2");
        assertNull(typesResolver.resolveNodeType("source.NodeType_X"));

        assertNull(typesResolver.resolveRelationshipType("source.RelationshipType1"));
        assertNull(typesResolver.resolveRelationshipType("source.RelationshipType2"));
        assertNull(typesResolver.resolveRelationshipType("source.RelationshipType_X"));
    }

    public void testResolvingWithNodeTypes() throws URISyntaxException, IOException {
        typesResolver = new DeployerTypesResolver(Resources
                .getResource(RELATIONSHIPTYPE__TEST_MAPPING).toURI().toString());

        assertNull(typesResolver.resolveNodeType("source.NodeType1"));
        assertNull(typesResolver.resolveNodeType("source.NodeType2"));
        assertNull(typesResolver.resolveNodeType("source.NodeType_X"));

        assertEquals(typesResolver.resolveRelationshipType("source.RelationshipType1"),
                "target.RelationshipType1");
        assertEquals(typesResolver.resolveRelationshipType("source.RelationshipType2"),
                "target.RelationshipType2");
        assertNull(typesResolver.resolveRelationshipType("source.RelationshipType_X"));
    }


}