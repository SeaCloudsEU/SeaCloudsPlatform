package eu.seaclouds.monitor.monitoringdamgenerator;

import it.polimi.tower4clouds.rules.MonitoringRules;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Host;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Module;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.AdpParsingException;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.YAMLMonitorParser;
import eu.seaclouds.monitor.monitoringdamgenerator.dcgenerators.JavaAppDcGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.dcgenerators.MODACloudsDcGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.rulesgenerators.ApplicationRulesGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.rulesgenerators.InfrastructuralRulesGenerator;

public class MonitoringDamGenerator {
    private static final Logger logger = LoggerFactory
            .getLogger(MonitoringDamGenerator.class);

    private URL monitoringManagerUrl;

    public MonitoringDamGenerator(URL monitoringManagerUrl) {

            this.monitoringManagerUrl = monitoringManagerUrl;

    }

    public MonitoringInfo generateMonitoringInfo(String adp) {

        logger.info("Request received.");

        try {

            YAMLMonitorParser adpParser = new YAMLMonitorParser();

            InfrastructuralRulesGenerator irg = new InfrastructuralRulesGenerator();
            ApplicationRulesGenerator arg = new ApplicationRulesGenerator();
            MODACloudsDcGenerator modacloudsDcScriptGen = new MODACloudsDcGenerator();
            JavaAppDcGenerator javaDcScriptGen = new JavaAppDcGenerator();
                
            List<Module> modules = adpParser.getModuleRelevantInfoFromAdp(adp);
            List<Host> hosts = getDistinctHostsFromModules(modules);

            for (Host host : hosts) {
                logger.info("Generating monitoring information for host "
                        + host.getHostName());
                Module mainModule = getMainHostedModule(modules, host);
                                    
                irg.generateMonitoringRules(mainModule);
                
                modacloudsDcScriptGen.addDataCollector(mainModule,
                        this.monitoringManagerUrl.getHost(),
                        this.monitoringManagerUrl.getPort());
            }

            for (Module module : modules) {

                logger.info("Generating monitoring information for module "
                        + module.getModuleName());

                arg.generateMonitoringRules(module);             

                javaDcScriptGen.addDataCollector(module,
                        this.monitoringManagerUrl.getHost(),
                        this.monitoringManagerUrl.getPort());
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
        
        for(Module module : modules){
            applicationRules.getMonitoringRules().addAll(module.getApplicationMonitoringRules().getMonitoringRules());
        }
        
        Yaml yamlApp = new Yaml();
        Map<String, Object> appMap = (Map<String, Object>) yamlApp.load(originalAdp);
        
        Map<String, Object> topology = (Map<String, Object>) appMap
                .get("topology_template");
        Map<String, Object> nodeTemplates = (Map<String, Object>) topology.get("node_templates");
        

        for(Module module: modules){
            for(Map<String, Object> dataCollector: module.getDataCollector()){
                for(String id: dataCollector.keySet()){
                    nodeTemplates.put(id, dataCollector.get(id));
                }
            }
        }
             
        topology.put("node_templates", nodeTemplates);
        
        appMap.put("topology_template", topology);
        
        enrichedAdp = yamlApp.dump(appMap);
        
        return new MonitoringInfo(applicationRules, enrichedAdp);
        
    }

}