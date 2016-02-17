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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import static eu.seaclouds.platform.planner.aamwriter.modeldesigner.DNode.Types;
import static eu.seaclouds.platform.planner.aamwriter.modeldesigner.DNode.Categories;
import static eu.seaclouds.platform.planner.aamwriter.modeldesigner.DNode.Containers;


/**
 * Translates from designer model to AAM.
 * 
 * TODO: QoB
 *
 */
public class Translator {

    public static Logger log = LoggerFactory.getLogger(Translator.class);
    
    private static final String NODE_TYPE_PREFIX = "sc_req.";
    private static final String OPERATION_PREFIX = "operation_";

    private static final class Languages {
        public static final String PHP = "PHP";
        public static final String NET = ".NET";
        public static final String RUBY = "RUBY";
        public static final String PYTHON = "PYTHON";
        public static final String JAVA = "JAVA";
    }
    
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
             * Application QosRequirements if this node is the frontend
             */
            if (dnode.isFrontend()) {
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
        if (Types.WEB_APPLICATION.equals(dnode.getType())) {
            derivedFromSuffix = dnode.getContainer(); 
        }
        else {
            derivedFromSuffix = dnode.getCategory();
        }
        if (derivedFromSuffix.isEmpty()) {
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
        case Types.WEB_APPLICATION:
            String language = dnode.getLanguage();
            if (Languages.JAVA.equals(language)) {
                artifactName = (needsContainer)? "wars.root" : "jar";
            } else if (Languages.PHP.equals(language)) {
                artifactName = dnode.getArtifact().endsWith(".git")? "git.url" : "tarball.url";
            }
            else {
                artifactName = "file";
            }
            break;
        case Types.DATABASE:
            artifactName = Categories.MYSQL.equals(dnode.getCategory())?"creationScriptUrl" : "db_create";
            break;
        default:
            artifactName = "artifact";
            break;
        }
        return artifactName;
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
        case Languages.JAVA: return "java";
        case Languages.PYTHON: return "python";
        case Languages.RUBY: return "ruby";
        case Languages.NET: return "dotnet";
        case Languages.PHP: return "php";
        
        case Containers.JBOSS6: return "jboss";
        case Containers.JBOSS7: return "jboss";
        case Containers.JETTY6: return "jetty";
        case Containers.TOMCAT: return "tomcat";
        case Containers.TOMCAT8: return "tomcat";

        case Categories.MYSQL: return "mysql";
        case Categories.MARIADB: return "maria";
        case Categories.POSTGRESQL: return "postgresql";
        case Categories.MONGODB: return "mongoDB";
        case Categories.REDIS: return "redis";

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
            
            try {
                
                double thresholdValue = Double.parseDouble(threshold);
                result.addConstraint(metricName, operator, thresholdValue, unit);
                
            } catch (NumberFormatException e) {
                log.warn("Could not parse threshold value ('{}'). This QoS requirement is ignored", threshold);
            }
        }
        
        return result;
    }
    
}
