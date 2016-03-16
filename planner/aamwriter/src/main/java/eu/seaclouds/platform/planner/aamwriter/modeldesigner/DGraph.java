/**
 * Copyright 2015 Atos
 * Contact: Seaclouds
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
package eu.seaclouds.platform.planner.aamwriter.modeldesigner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import eu.seaclouds.platform.planner.aamwriter.AamWriterException;

/**
 * Classes in aamwriter.modeldesigner package model the topology described in UI. 
 * 
 * DGraph class is the root of the topology model. It is in charge of reading a description
 * of the topology in json and build the hierarchy of classes.
 * 
 * NOTE: The preferred way to set the frontend node of the application is specifying it in the 
 * application_requirements element of the json topology. If not specified, it falls back
 * to the first node found with "frontend" property set to true. If not specified, the frontend
 * node is calculated as the first node found without incoming links. This may lead to 
 * unexpected behaviour. Due to optimizer constraint, only one frontend node is allowed.
 */
public class DGraph {
    public final static class Properties {
        public static final String NAME = "name";
        public static final String NODES = "nodes";
        public static final String LINKS = "links";
        public static final String REQUIREMENTS = "application_requirements";
    }

    private String name;
    private List<DNode> nodes; 
    private List<DLink> links;
    private DRequirements requirements;
    private DNode frontendNode;

    public DGraph(JSONObject json) {
        this.name = (String)json.get(Properties.NAME);
        
        this.nodes = new ArrayList<DNode>();

        JSONArray onodes = (JSONArray)json.get(Properties.NODES);
        this.nodes.addAll(parseArray(onodes, DNode.class));

        this.links = new ArrayList<DLink>();

        JSONArray olinks = (JSONArray)json.get(Properties.LINKS);
        this.links.addAll(parseArray(olinks, DLink.class));
        
        JSONObject orequirements = (JSONObject) json.get(Properties.REQUIREMENTS);
        this.requirements = new DRequirements(orequirements, this);
        
        this.frontendNode = findFrontendNode();
    }

    public DNode getNode(String nodeName) {
        if (nodeName == null) {
            throw new NullPointerException("nodeName cannot be null");
        }
        for (DNode node : nodes) {
            if (nodeName.equals(node.getName())) {
                return node;
            }
        }
        return DNode.NOT_FOUND;
    }
    
    public List<DLink> getLinksFrom(DNode node) {
        if (node == null) {
            throw new NullPointerException("node cannot be null");
        }
        List<DLink> result = new ArrayList<DLink>();
        
        for (DLink link : links) {
            if (node.equals(link.getSource())) {
                result.add(link);
            }
        }
        return result;
    }

    public List<DLink> getLinksTo(DNode node) {
        if (node == null) {
            throw new NullPointerException("node cannot be null");
        }
        List<DLink> result = new ArrayList<DLink>();
        
        for (DLink link : links) {
            if (node.equals(link.getTarget())) {
                result.add(link);
            }
        }
        return result;
    }

    /*
     * this method can be extracted to a utils class if needed 
     */
    private <T> List<T> parseArray(JSONArray items, Class<T> clazz) {

        List<T> result = new ArrayList<T>();
        for (Object onode : items) {
            JSONObject jnode = (JSONObject) onode;

            T item;
            try {
                item = clazz.getDeclaredConstructor(JSONObject.class, DGraph.class).newInstance(jnode, this);
            } catch (InstantiationException | 
                    IllegalAccessException | 
                    IllegalArgumentException | 
                    InvocationTargetException | 
                    NoSuchMethodException | 
                    SecurityException e) {
                throw new AamWriterException(e);
            }

            result.add(item);
        }
        return result;
    }

    /**
     * Assigns a frontend node to the graph according to the rules in the class header
     */
    private DNode findFrontendNode() {
        DNode frontend = getNode(requirements.getFrontend());
        
        if (frontend == DNode.NOT_FOUND) {
            
            frontend = searchFrontendNode();

            if (frontend == DNode.NOT_FOUND) {
                frontend = calculateSourceNode();
            }
        }
        return frontend;
    }

    /**
     * Searches the first node with "frontend" property set to true
     */
    private DNode searchFrontendNode() {
        DNode result = DNode.NOT_FOUND;
        
        for (DNode node : nodes) {
            if (node.getFrontend()) {
                result = node;
                break;
            }
        }
        return result;
    }
    
    /**
     * Finds the first node with no incoming links.
     */
    private DNode calculateSourceNode() {
        DNode result = DNode.NOT_FOUND;
        
        for (DNode node : nodes) {
            List<DLink> links = getLinksTo(node);
            if (links.size() == 0) {
                result = node;
                break;
            }
        }
        return result;
    }
    
    @Override
    public String toString() {
        return String.format("Graph [name=%s, nodes=%s, links=%s, requirements=%s, source=%s]",
                this.name,
                Arrays.toString(nodes.toArray()), 
                Arrays.toString(links.toArray()),
                requirements.toString(),
                frontendNode.getName());
    }

    public String getName() {
        return name;
    }
    
    public List<DNode> getNodes() {
        return nodes;
    }

    public List<DLink> getLinks() {
        return links;
    }
    
    public DRequirements getRequirements() {
        return requirements;
    }

    /**
     * The frontend node is specified in application_requirements/frontend. If not specified,
     * the frontend is considered the source of the flow graph. 
     * @return
     */
    public DNode getFrontendNode() {
        return frontendNode;
    }
}
