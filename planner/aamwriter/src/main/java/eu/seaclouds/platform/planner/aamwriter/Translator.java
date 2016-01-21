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
package eu.seaclouds.platform.planner.aamwriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.seaclouds.platform.planner.aamwriter.modelaam.Aam;
import eu.seaclouds.platform.planner.aamwriter.modelaam.Constraint;
import eu.seaclouds.platform.planner.aamwriter.modelaam.NodeTemplate;
import eu.seaclouds.platform.planner.aamwriter.modelaam.NodeType;
import eu.seaclouds.platform.planner.aamwriter.modelaam.Operation;
import eu.seaclouds.platform.planner.aamwriter.modelaam.Policy;
import eu.seaclouds.platform.planner.aamwriter.modelaam.TopologyTemplate;
import eu.seaclouds.platform.planner.aamwriter.modeldesigner.DGraph;
import eu.seaclouds.platform.planner.aamwriter.modeldesigner.DLink;
import eu.seaclouds.platform.planner.aamwriter.modeldesigner.DNode;
import eu.seaclouds.platform.planner.aamwriter.modeldesigner.DRequirements;

/**
 * Translates from designer model to AAM.
 * 
 * TODO: QoB
 *
 */
public class Translator {

    private static final String NODE_TYPE_PREFIX = "sc_req.";
    private static final String OPERATION_PREFIX = "operation_";

    public Aam translate(DGraph graph) {

        Aam aam = new Aam(graph.getName());
        for (DNode n : graph.getNodes()) {
            
            translateDNode(n, aam);
        }
        
        for (DLink l : graph.getLinks()) {
            
            translateDLink(l, aam);
        }
        buildGroups(graph, aam);
        return aam;
    }

    private void translateDNode(DNode dnode, Aam aam) {
        /*
         * The names module, container and host used in this code are generic and not following TOSCA spec
         * where each of them are "modules". So:
         * - a module is a component of our software
         * - a container is where a module is hosted (maybe specified or not). E.g: apache, jboss
         * - a host is a VM/PaaS where a module/container is hosted.
         * 
         * If the module is to be hosted on a container (e.g. a war using a Tomcat), the node template for the module 
         * is a Tomcat, using the war as an artifact.
         * 
         * If the module is a standalone module (e.g. a jar that listens on port when run), the node template for the
         * module is a WebApplication, with the jar as an artifact.
         */
        TopologyTemplate topology = aam.getTopologyTemplate();

        NodeTemplate module = new NodeTemplate(dnode.getName());
        topology.addNodeTemplate(module);
        
        boolean needsContainer = !dnode.getContainer().isEmpty();
        
        if (!dnode.getLanguage().isEmpty()) {
            module.addProperty("language", dnode.getLanguage());
        }
        
        module.addProperties(dnode.getOtherProperties());
        
        /*
         * Handle artifacts
         */
        String artifactValue = dnode.getArtifact();
        if (!artifactValue.isEmpty()) {
            String artifactName;
            String artifactType;
            
            artifactName = buildArtifactName(dnode, needsContainer);

            artifactType = "tosca.artifacts.File";
            module.addArtifact(artifactName, artifactValue, artifactType);
        }
        
        /*
         * Handle module_type
         */
        NodeType moduleType = new NodeType(NODE_TYPE_PREFIX + module.getName());

        String derivedFrom = buildDerivedFrom(dnode);
        moduleType.setDerivedFrom(derivedFrom);
        
        
        /*
         * If PaaS, set *_support for software requirements: language, container, category
         */
        if (isPlatformEligible(dnode)) {
            /*
             * support item handling
             */
            List<String> supportItems = new ArrayList<String>();
            if (!"".equals(dnode.getLanguage())) {
                String prefix = getPrefixItem(dnode.getLanguage(), dnode.getMinVersion(), dnode.getMaxVersion());
                supportItems.add(prefix);
            }
            if (!"".equals(dnode.getContainer())) {
                supportItems.add(getPrefixItem(dnode.getContainer(), "", ""));
            }
            if (!"".equals(dnode.getCategory())) {
                String prefix = getPrefixItem(dnode.getCategory(), dnode.getMinVersion(), dnode.getMaxVersion());
                supportItems.add(prefix);
            }
            for (String supportItem : supportItems) {
                if (!"".equals(supportItem)) {
                    moduleType.addSupportItemConstraintProperty(supportItem + "_support");
                }
            }
        }
        /*
         * If IaaS, 
         *   - set location, disk size, mem size in node_type
         *   - set language, category, versions in node_template
         */
        if (isComputeEligible(dnode)) {
            if (!"".equals(dnode.getNumCpus())) {
                Constraint constraint = buildGreaterEqualsConstraint(dnode.getNumCpus());
                moduleType.addConstrainedProperty("num_cpus", constraint);
            }
            if (!"".equals(dnode.getDiskSize())) {
                Constraint constraint = buildGreaterEqualsConstraint(dnode.getDiskSize());
                moduleType.addConstrainedProperty("disk_size", constraint);
            }
        }
        /*
         * version handling
         */
        {
            if (!"".equals(dnode.getLanguage())) {
                String prefix = getPrefixItem(dnode.getLanguage(), dnode.getMinVersion(), dnode.getMaxVersion());
                
                Constraint[] constraints1 = buildVersionConstraints(dnode);
                Constraint[] constraints2 = buildVersionConstraints(dnode);
                
                if (isPlatformEligible(dnode)) {
                    moduleType.addConstrainedProperty(prefix + "_version", constraints1);
                }
                if (isComputeEligible(dnode)) {
                    module.addConstrainedProperty(prefix + "_version", constraints2);
                }
            }
            if (!"".equals(dnode.getCategory())) {
                String prefix = getPrefixItem(dnode.getCategory(), dnode.getMinVersion(), dnode.getMaxVersion());
                
                Constraint[] constraints1 = buildVersionConstraints(dnode);
                Constraint[] constraints2 = buildVersionConstraints(dnode);
                if (isPlatformEligible(dnode)) {
                    moduleType.addConstrainedProperty(prefix + "_version", constraints1);
                }
                if (isComputeEligible(dnode)) {
                    module.addConstrainedProperty(prefix + "_version", constraints2);
                }
            }
        }

        aam.addNodeType(moduleType);
     
        moduleType.addResourceTypeConstraintProperty(dnode.getInfrastructure());
        
        /*
         * Assign node types to node templates
         */
        module.setType(moduleType);
    }

    private void translateDLink(DLink l, Aam aam) {
        TopologyTemplate topologyTemplate = aam.getTopologyTemplate();
        
        DNode dSource = l.getSource();
        DNode dTarget = l.getTarget();
        
        NodeTemplate source = topologyTemplate.getNodeTemplate(dSource.getName());
        NodeTemplate target = topologyTemplate.getNodeTemplate(dTarget.getName());
        
        source.addConnectionRequirement(target, l.getOperationType());
        if (!l.getCredentialsFile().isEmpty()) {
            source.addProperty(DLink.Attributes.CREDENTIALS_FILE, l.getCredentialsFile());
        }
    }

    private void buildGroups(DGraph dgraph, Aam aam) {
        
        for (DNode dnode : dgraph.getNodes()) {
            Operation operation = new Operation(buildOperationName(dnode), dnode.getName());
            aam.addOperation(operation);
        }
        
        for (DNode dnode : dgraph.getNodes()) {
            Operation sourceOperation = aam.getOperation(buildOperationName(dnode));
            /*
             * QoSInfo
             */
            if (!"".equals(dnode.getBenchmarkResponseTime()) && !"".equals(dnode.getBenchmarkPlatform())) {
                Policy.QoSInfo info = new Policy.QoSInfo(
                        dnode.getBenchmarkResponseTime(), 
                        dnode.getBenchmarkPlatform());
                sourceOperation.addQoSInfo(info);
            }
            /*
             * Dependencies
             */
            List<Policy.Dependency> dependencies = new ArrayList<Policy.Dependency>();
            for (DLink link : dgraph.getLinksFrom(dnode)) {
                Operation targetOperation = aam.getOperation(buildOperationName(link.getTarget()));
                Policy.Dependency dep = new Policy.Dependency(targetOperation, link.getCalls());
                dependencies.add(dep);
            }
            sourceOperation.addDependencies(new Policy.Dependencies(dependencies));
            
            /*
             * Application QosRequirements if this node is the source of the flow graph
             */
            if (isSource(dgraph, dnode)) {
                DRequirements dreq = dgraph.getRequirements();
                Policy.AppQoSRequirements requirements = new Policy.AppQoSRequirements(
                        dreq.getResponseTime(), dreq.getAvailability(), dreq.getCost(), dreq.getWorkload());
                sourceOperation.addAppQoSRequirement(requirements);
            }
            /*
             * Module QoSRequirements
             */
            if (!dnode.getQos().isEmpty()) {
                Policy.ModuleQoSRequirements requirements = buildModuleQoSRequirements(dnode.getQos());
                sourceOperation.addModuleQoSRequirement(requirements);
            }
        }
    }
    
    private String buildDerivedFrom(DNode dnode) {
        String derivedFrom;
        String derivedFromSuffix;
        if ("WebApplication".equals(dnode.getType())) {
            derivedFromSuffix = dnode.getContainer(); 
        }
        else {
            derivedFromSuffix = dnode.getCategory();
        }
        if ("".equals(derivedFromSuffix)) {
            derivedFrom = "seaclouds.nodes.SoftwareComponent";
        }
        else {
            derivedFrom = "seaclouds.nodes." + derivedFromSuffix;
        }
        return derivedFrom;
    }

    private String buildArtifactName(DNode dnode, boolean needsContainer) {
        String artifactName;
        switch (dnode.getType()) {
        case "WebApplication":
            String language = dnode.getLanguage();
            if ("JAVA".equals(language)) {
                artifactName = (needsContainer)? "war" : "jar";
            }
            else {
                artifactName = "file";
            }
            break;
        case "Database":
            artifactName = "db_create";
            break;
        default:
            artifactName = "artifact";
            break;
        }
        return artifactName;
    }
    
    private boolean isSource(DGraph dgraph, DNode dnode) {
        List<DLink> links = dgraph.getLinksTo(dnode);
        return links.size() == 0;
    }

    private String buildOperationName(DNode dnode) {
        return OPERATION_PREFIX + dnode.getName();
    }

    private boolean isComputeEligible(DNode dnode) {
        return "".equals(dnode.getInfrastructure()) || "compute".equals(dnode.getInfrastructure());
    }

    private boolean isPlatformEligible(DNode dnode) {
        return "".equals(dnode.getInfrastructure()) || "platform".equals(dnode.getInfrastructure());
    }

    private Constraint[] buildVersionConstraints(DNode dnode) {
        Constraint[] constraints = new Constraint[2];
        if (!"".equals(dnode.getMinVersion())) {
            constraints[0] = 
                    new Constraint(Constraint.Names.GE, dnode.getMinVersion());
        }
        if (!"".equals(dnode.getMaxVersion())) {
            constraints[1] = 
                    new Constraint(Constraint.Names.LE, dnode.getMaxVersion());
        }
        return constraints;
    }
    
    private Constraint buildGreaterEqualsConstraint(String threshold) {
        Constraint result = new Constraint(Constraint.Names.GE, threshold);
        
        return result;
    }
    
    /*
     * Quick & dirty getPrefixItem
     */
    private String getPrefixItem(String requirement, String minVersion, String maxVersion) {
        
        switch (requirement) {
        case "JAVA": return "java";
        case "PYTHON": return "python";
        case "RUBY": return "ruby";
        case ".NET": return "dotnet";
        case "PHP": return "php";
        
        case "webapp.jboss.JBoss6Server": return "jboss";
        case "webapp.jboss.JBoss7Server": return "jboss";
        case "webapp.jetty.Jetty6Server": return "jetty";
        case "webapp.tomcat.TomcatServer": return "tomcat";
        case "webapp.tomcat.Tomcat8Server": return "tomcat";

        case "database.mysql.MySqlNode": return "mysql";
        case "database.mariadb.MariaDbNode": return "maria";
        case "database.postgresql.PostgreSqlNode": return "postgresql";
        case "nosql.mongodb.MongoDBServer": return "mongoDB";
        case "nosql.redis.RedisStore": return "redis";

        default:
            return "";
        }
    }
    
    private Policy.ModuleQoSRequirements buildModuleQoSRequirements(List<Map<String, String>> qos) {
        
        Policy.ModuleQoSRequirements result = new Policy.ModuleQoSRequirements();
        for (Map<String, String> item : qos) {
            
            MonitoringMetrics metric = MonitoringMetrics.from(item.get("metric"));
            String metricName = metric.getMetricName();
            String operator = item.get("operator");
            String threshold = item.get("threshold");
            String unit = metric.getUnit();
            result.addConstraint(metricName, operator, Double.parseDouble(threshold), unit);
        }
        
        return result;
    }
    
}
