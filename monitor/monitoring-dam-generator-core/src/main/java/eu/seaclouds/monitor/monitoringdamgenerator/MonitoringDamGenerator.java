package eu.seaclouds.monitor.monitoringdamgenerator;

import it.polimi.tower4clouds.rules.MonitoringRules;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Host;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Module;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.ParsingException;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.YAMLMonitorParser;
import eu.seaclouds.monitor.monitoringdamgenerator.dcscriptgenerators.JavaAppLevelDcDeploymentScriptGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.dcscriptgenerators.MODACloudsDcDeploymentScriptGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.dcscriptgenerators.NuroDcDeploymentScriptGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.rulesgenerators.ApplicationRulesGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.rulesgenerators.InfrastructuralRulesGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.rulesgenerators.NuroRulesGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.util.CustomizedApplications;

public class MonitoringDamGenerator {
    private static final Logger logger = LoggerFactory
            .getLogger(MonitoringDamGenerator.class);
    
    private URL monitoringManagerUrl;
    
    
    private static final String NURO_DC_ID="nuroDc";
    private static final String JAVA_APP_DC_ID="javaAppDc";
    private static final String MODACLOUDS_DC_ID="modacloudsDc";

    public MonitoringDamGenerator(String monitorIp, String monitorPort) {
        
        try {
            this .monitoringManagerUrl= new URL("http://"+monitorIp+":"+monitorPort);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public List<Module> generateMonitoringInfo(String adp) {

        logger.info("Request received.");
        
        try {

            List<Module> toReturn = new ArrayList<Module>();
            YAMLMonitorParser adpParser = new YAMLMonitorParser();

            InfrastructuralRulesGenerator irg = new InfrastructuralRulesGenerator();
            ApplicationRulesGenerator arg = new ApplicationRulesGenerator();
            NuroRulesGenerator nrg = new NuroRulesGenerator();

            List<Module> modules = adpParser.getModuleRelevantInfoFromAdp(adp);
            List<Host> hosts = getDistinctHostsFromModules(modules);
            MonitoringRules tempRules;
            Map<String, String> tempDataCollectorDeploymentScripts;

            for (Host host : hosts) {

                logger.info("Generating monitoring information for host "
                        + host.getHostName());

                tempDataCollectorDeploymentScripts = new HashMap<String, String>();
                MODACloudsDcDeploymentScriptGenerator modacloudsDcScriptGen = new MODACloudsDcDeploymentScriptGenerator();
                Module mainModule = null;

                if (host.getDeploymentType().equals("IaaS")) {
                    tempRules = irg.generateMonitoringRules(host
                            .getHostName());

                    host.setRules(tempRules);
                    
                    mainModule = getMainHostedModule(modules, host);
                    String modacloudsDeploymentScript = modacloudsDcScriptGen
                            .generateDataCollectorDeploymentScript(mainModule,
                                    this.monitoringManagerUrl.getHost(), this.monitoringManagerUrl.getPort());
                    tempDataCollectorDeploymentScripts.put(MODACLOUDS_DC_ID,
                            modacloudsDeploymentScript);
                }

                host.setDataCollectorBashDeploymentScripts(tempDataCollectorDeploymentScripts);

            }

            for (Module module : modules) {

                logger.info("Generating monitoring information for module "
                        + module.getModuleName());

                tempRules = arg.generateMonitoringRules(module);
                tempRules.getMonitoringRules().addAll(
                        nrg.generateMonitoringRules(module)
                                .getMonitoringRules());

                module.setRules(tempRules);

                tempDataCollectorDeploymentScripts = new HashMap<String, String>();

                if (module.isJavaApp()) {

                    JavaAppLevelDcDeploymentScriptGenerator javaDcScriptGen = new JavaAppLevelDcDeploymentScriptGenerator();
                    String javaDcDeploymentScript = javaDcScriptGen
                            .generateDataCollectorDeploymentScript(module,
                                    this.monitoringManagerUrl.getHost(), this.monitoringManagerUrl.getPort());
                    tempDataCollectorDeploymentScripts.put(JAVA_APP_DC_ID,
                            javaDcDeploymentScript);

                }

                if (CustomizedApplications.isCustomized(module.getModuleName())) {

                    NuroDcDeploymentScriptGenerator nuroDcScriptGen = new NuroDcDeploymentScriptGenerator();
                    String nuroDcDeploymentScript = nuroDcScriptGen
                            .generateDataCollectorDeploymentScript(module,
                                    this.monitoringManagerUrl.getHost(), this.monitoringManagerUrl.getPort());
                    tempDataCollectorDeploymentScripts.put(NURO_DC_ID,
                            nuroDcDeploymentScript);

                }

                module.setDataCollectorBashDeploymentScripts(tempDataCollectorDeploymentScripts);
                toReturn.add(module);

            }

            this.generateDataCollectorsNodeTemplates(toReturn);

            return toReturn;

        }catch (ParsingException e) {
            logger.error("Error parsing the Abstract Deployment Model", e);
            throw new RuntimeException("Error parsing the Abstract Deployment Model");
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

    private void generateDataCollectorsNodeTemplates(List<Module> modules) {

        Map<String, String> tempDcToscaScripts;
        Map<String, String> tempDcBashScripts;
        Map<String, Object> toSet;
        Map<String, Object> dataCollector;
        Map<String, Object> properties;
        Map<String, Object> requirements;
        Map<String, Object> interfaces;
        Map<String, Object> startCommand;
        
        Yaml yaml = new Yaml();

        for (Module module : modules) {
            tempDcBashScripts = module.getDataCollectorBashDeploymentScripts();
            tempDcToscaScripts = new HashMap<String, String>();

            for (String dc : tempDcBashScripts.keySet()) {
                toSet = new HashMap<String, Object>();
                dataCollector = new HashMap<String, Object>();
                properties = new HashMap<String, Object>();
                requirements = new HashMap<String, Object>();
                interfaces = new HashMap<String, Object>();
                startCommand = new HashMap<String, Object>();

                startCommand.put("start", tempDcBashScripts.get(dc));
                interfaces.put("Standard", startCommand);
                requirements.put("host", module.getModuleName());
                properties.put("install.latch", "$brooklyn:component(\""+module.getModuleName()+"\").attributeWhenReady(\"service.isUp\")");

                dataCollector.put("type", "seaclouds.nodes.Datacollector");
                dataCollector.put("properties", properties);
                dataCollector.put("requirements", requirements);
                dataCollector.put("interfaces", interfaces);

                toSet.put(dc, dataCollector);

                tempDcToscaScripts.put(dc, yaml.dump(toSet));

            }

            module.setDataCollectorToscaDeploymentScripts(tempDcToscaScripts);
        }

        for (Host host : getDistinctHostsFromModules(modules)) {

            tempDcBashScripts = host.getDataCollectorBashDeploymentScripts();
            tempDcToscaScripts = new HashMap<String, String>();
            

            for (String dc : tempDcBashScripts.keySet()) {
                toSet = new HashMap<String, Object>();
                dataCollector = new HashMap<String, Object>();
                properties = new HashMap<String, Object>();
                requirements = new HashMap<String, Object>();
                interfaces = new HashMap<String, Object>();
                startCommand = new HashMap<String, Object>();

                startCommand.put("start", tempDcBashScripts.get(dc));
                interfaces.put("Standard", startCommand);
                requirements.put("host", host.getHostName());

                dataCollector.put("type", "seaclouds.nodes.Datacollector");
                dataCollector.put("properties", properties);
                dataCollector.put("requirements", requirements);
                dataCollector.put("interfaces", interfaces);

                toSet.put(dc, dataCollector);

                tempDcToscaScripts.put(dc, yaml.dump(toSet));

            }

            host.setDataCollectorToscaDeploymentScripts(tempDcToscaScripts);
        }
    }
    
    public static void main(String agrs[]){
        try {
            URL testUrl = new URL("http://"+"127.0.0.1"+":"+"8170");
            System.out.println(testUrl.getHost());
            System.out.println(testUrl.getPort());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}