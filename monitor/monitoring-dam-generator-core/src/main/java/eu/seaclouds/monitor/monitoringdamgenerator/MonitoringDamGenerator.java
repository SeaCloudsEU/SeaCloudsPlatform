package eu.seaclouds.monitor.monitoringdamgenerator;

import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.AdpParsingException;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Host;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Module;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.YAMLMonitorParser;
import eu.seaclouds.monitor.monitoringdamgenerator.dcgenerators.JavaAppDcGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.dcgenerators.MODACloudsDcGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.rulesgenerators.ApplicationRulesGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.rulesgenerators.InfrastructuralRulesGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.rulesgenerators.NuroRulesGenerator;
import it.polimi.tower4clouds.rules.MonitoringRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;


import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class MonitoringDamGenerator {
    private static final Logger logger = LoggerFactory
            .getLogger(MonitoringDamGenerator.class);
    
    private static final String DATA_COLLECTOR_NODE_TYPE = ""
            + "derived_from: tosca.nodes.Root\n"
            + "description: >\n"
            + " A simple Tomcat server\n"
            + "properties:\n"
            + " install_latch:\n"
            + "  type: string\n"
            + "  required: false\n"
            + " shell.env:\n"
            + "  type: map\n"
            + "  required: false\n"
            + "  entry_schema:\n"
            + "   type: string\n"
            + "requirements:\n"
            + " - host: tosca.nodes.Compute\n"
            + "   type: tosca.relationships.HostedOn\n";
    private static final String JAVA_LANGUAGE = "JAVA";
    private static final String PHP_LANGUAGE = "PHP";

    private URL monitoringManagerUrl;
    private URL influxdbUrl;
    
    public static void main(String args[]){
        MonitoringDamGenerator gen;
        try {
            gen = new MonitoringDamGenerator(new URL("http://128.0.0.1:8080/"), new URL("http://128.0.0.1:8083/"));
            MonitoringInfo info = gen.generateMonitoringInfo(readFile("resources/adp_example.yml", Charset.defaultCharset()));
            System.out.println(info.getReturnedAdp());

            StringWriter writer = new StringWriter();
            JAXBContext context = JAXBContext.newInstance(MonitoringRules.class);            
            Marshaller m = context.createMarshaller();
            m.marshal(info.getApplicationMonitoringRules(), writer);
            //System.out.println(writer.toString());
            
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public MonitoringDamGenerator(URL monitoringManagerUrl, URL influxdbUrl) {

            this.monitoringManagerUrl = monitoringManagerUrl;
            this.influxdbUrl = influxdbUrl;

    }

    public MonitoringInfo generateMonitoringInfo(String adp) {

        logger.info("Request received.");

        try {

            YAMLMonitorParser adpParser = new YAMLMonitorParser();

            InfrastructuralRulesGenerator irg = new InfrastructuralRulesGenerator();
            ApplicationRulesGenerator arg = new ApplicationRulesGenerator();
            NuroRulesGenerator nrg = new NuroRulesGenerator();
            MODACloudsDcGenerator modacloudsDcScriptGen = new MODACloudsDcGenerator();
            JavaAppDcGenerator javaDcScriptGen = new JavaAppDcGenerator();
                
            List<Module> modules = adpParser.getModuleRelevantInfoFromAdp(adp);
            List<Host> hosts = getDistinctHostsFromModules(modules);

            for (Host host : hosts) {
                if(host.getDeploymentType().equals(DeploymentType.IaaS)){
                    
                    logger.info("Generating monitoring information for host "
                            + host.getHostName());
                    Module mainModule = getMainHostedModule(modules, host);

                    mainModule.addApplicationMonitoringRules(irg.generateMonitoringRules(mainModule));
    
                    modacloudsDcScriptGen.addDataCollector(mainModule,
                            this.monitoringManagerUrl.getHost(),
                            this.monitoringManagerUrl.getPort(),
                            this.influxdbUrl.getHost(),
                            this.influxdbUrl.getPort());
                }
            }

            for (Module module : modules) {
                logger.info("Generating monitoring information for module "
                        + module.getModuleName());

                if(!module.getType().toLowerCase().contains("database")){
                 
                    module.addApplicationMonitoringRules(arg.generateMonitoringRules(module));
                    
                    if(module.getLanguage().equals(PHP_LANGUAGE)){
                        module.addApplicationMonitoringRules(nrg.generateMonitoringRules(module));
                    }
                    
                    if (module.getLanguage().equals(JAVA_LANGUAGE)) {
                        javaDcScriptGen.addDataCollector(module,
                                this.monitoringManagerUrl.getHost(),
                                this.monitoringManagerUrl.getPort(),
                                this.influxdbUrl.getHost(),
                                this.influxdbUrl.getPort());
                    }
                    
                }
                
            }

            return this.buildMonitoringInfo(modules, adp);

        } catch (AdpParsingException e) {
            logger.error(e.getMessage());
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    private Module getMainHostedModule(List<Module> modules, Host host) {
        Module toReturn = null;

        for (Module m : modules) {
            if (m.getHost().equals(host)) {
                if (toReturn == null) {
                    toReturn = m;
                }

                if (m.getAvailability() > 0) {
                    toReturn = m;
                }
            }
        }

        return toReturn;
    }

    private List<Host> getDistinctHostsFromModules(List<Module> modules) {

        List<Host> toReturn = new ArrayList<Host>();

        for (Module m : modules) {
            if (!toReturn.contains(m.getHost())) {
                toReturn.add(m.getHost());
            }
        }

        return toReturn;

    }
    
    @SuppressWarnings("unchecked")
    private MonitoringInfo buildMonitoringInfo(List<Module> modules, String originalAdp){
        MonitoringRules applicationRules = new MonitoringRules();
        String enrichedAdp;
        Yaml yamlApp = new Yaml();
        
        Map<String, Object> appMap = (Map<String, Object>) yamlApp.load(originalAdp);
        Map<String, Object> topology = (Map<String, Object>) appMap
                .get("topology_template");
        Map<String, Object> nodeTemplates = (Map<String, Object>) topology.get("node_templates");
        
        Map<String, String> variablesToSet;
        Map<String, Object> currentNodeTemplate;
        Map<String, Object> properties;
        Map<String, Object> currentVars;
        
        for(Module module : modules){
            applicationRules.getMonitoringRules().addAll(module.getApplicationMonitoringRules().getMonitoringRules());
        }

        for(Module module: modules){
            for(Map<String, Object> dataCollector: module.getDataCollector()){
                for(String id: dataCollector.keySet()){
                    nodeTemplates.put(id, dataCollector.get(id));
                }
            }

            variablesToSet = module.getMonitoringEnvVars();
            currentNodeTemplate = (Map<String, Object>) nodeTemplates.get(module.getModuleName());
            properties = (Map<String, Object>) currentNodeTemplate.get("properties");

            if(currentNodeTemplate.get("properties") != null){
                currentVars = (Map<String, Object>) properties.get("env");
                if(currentVars != null){
                    for(String variable : variablesToSet.keySet()){
                        currentVars.put(variable, variablesToSet.get(variable));
                    }     
                } else if((variablesToSet!= null ) && (!variablesToSet.isEmpty())) {
                    properties.put("env", module.getMonitoringEnvVars());
                }
            } else if (!variablesToSet.isEmpty()){
                properties = new HashMap<String, Object>();
                properties.put("env", module.getMonitoringEnvVars());
                currentNodeTemplate.put("properties", properties);
            }
        }
        
             
        topology.put("node_templates", nodeTemplates);
        
        Map<String, Object> nodeTypes = (Map<String, Object>) appMap.get("node_types");
        
        nodeTypes.put("seaclouds.nodes.Datacollector", DATA_COLLECTOR_NODE_TYPE);
        
        appMap.put("node_types", nodeTypes);
                
        enrichedAdp = yamlApp.dump(appMap);
        
        return new MonitoringInfo(applicationRules, enrichedAdp);
        
    }
    
    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}