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

    @Override
    public String toString() {
        return String.format("Graph [name=%s, nodes=%s, links=%s, requirements=%s]",
                this.name,
                Arrays.toString(nodes.toArray()), 
                Arrays.toString(links.toArray()),
                requirements.toString());
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
}
