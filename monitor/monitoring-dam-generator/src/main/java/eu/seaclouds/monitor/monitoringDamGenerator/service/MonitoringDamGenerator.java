package eu.seaclouds.monitor.monitoringDamGenerator.service;

import it.polimi.tower4clouds.rules.MonitoringRules;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.IOUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import eu.seaclouds.monitor.monitoringDamGenerator.core.adpParsing.Module;
import eu.seaclouds.monitor.monitoringDamGenerator.core.adpParsing.ParsingException;
import eu.seaclouds.monitor.monitoringDamGenerator.core.adpParsing.YAMLMonitorParser;
import eu.seaclouds.monitor.monitoringDamGenerator.core.impl.dcScriptGenerators.JavaAppLevelDcDeploymentScriptGenerator;
import eu.seaclouds.monitor.monitoringDamGenerator.core.impl.dcScriptGenerators.MODACloudsDcDeploymentScriptGenerator;
import eu.seaclouds.monitor.monitoringDamGenerator.core.impl.dcScriptGenerators.NuroDcDeploymentScriptGenerator;
import eu.seaclouds.monitor.monitoringDamGenerator.core.impl.rulesGenerators.NuroRulesGenerator;
import eu.seaclouds.monitor.monitoringDamGenerator.core.impl.rulesGenerators.ReconfigurationRulesGenerator;
import eu.seaclouds.monitor.monitoringDamGenerator.core.impl.rulesGenerators.SLARulesGenerator;
import eu.seaclouds.monitor.monitoringDamGenerator.core.impl.rulesGenerators.VisualizationRulesGenerator;

@Path("/")
public class MonitoringDamGenerator {
    private static final Logger logger = LoggerFactory
            .getLogger(MonitoringDamGenerator.class);

    private static String port = "8176";
    private static String monitoringManagerIp = "127.0.0.1";
    private static String monitoringManagerPort = "8170";

    @POST
    @Path("/data")
    public String receiveData(InputStream incomingData) {

        logger.info("Request received.");
        try {
            StringWriter writer = new StringWriter();
            JSONArray output = new JSONArray();

            IOUtils.copy(incomingData, writer);
            String adp = writer.toString();

            YAMLMonitorParser adpParser= new YAMLMonitorParser();
            
            List<Module> requirements = adpParser.getModuleRelevantInfoFromAdp(adp);

            for (Module module : requirements) {
                
                logger.info("Generating monitoring information for module "+module.getModuleName());
                
                VisualizationRulesGenerator vrg = new VisualizationRulesGenerator();
                SLARulesGenerator srg = new SLARulesGenerator();
                ReconfigurationRulesGenerator rrg = new ReconfigurationRulesGenerator();
                NuroRulesGenerator nrg=new NuroRulesGenerator();
                
                Map<String, Object> moduleElement = new HashMap<String, Object>();
                Map<String, Object> membersElement = new HashMap<String, Object>();
                List<Map<String, Object>> policies = new ArrayList<Map<String, Object>>();
                Map<String, Object> rulesElement = new HashMap<String, Object>();
                Map<String, Object> deploymentScriptsElement = new HashMap<String, Object>();

                MonitoringRules result = vrg.generateMonitoringRules(module);
                result.getMonitoringRules().addAll(
                        srg.generateMonitoringRules(module)
                                .getMonitoringRules());
                result.getMonitoringRules().addAll(
                        rrg.generateMonitoringRules(module)
                                .getMonitoringRules());
                result.getMonitoringRules().addAll(nrg.generateMonitoringRules(module).getMonitoringRules());


                JAXBContext context;
                context = JAXBContext
                        .newInstance("it.polimi.tower4clouds.rules");
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
                StringWriter sw = new StringWriter();
                marshaller.marshal(result, sw);

                if(module.getDeploymentType().equals("compute")){
                    
                    MODACloudsDcDeploymentScriptGenerator modacloudsDcScriptGen = new MODACloudsDcDeploymentScriptGenerator();
                    String modacloudsDeploymentScript = modacloudsDcScriptGen
                            .generateDataCollectorDeploymentScript(module,
                                    monitoringManagerIp, monitoringManagerPort);
                    deploymentScriptsElement.put("modacloudsDeploymentScript",
                            modacloudsDeploymentScript);
                }
                
                if(module.isJavaApp()){
                    
                    JavaAppLevelDcDeploymentScriptGenerator javaDcScriptGen= new JavaAppLevelDcDeploymentScriptGenerator();
                    String javaDcDeploymentScript = javaDcScriptGen.generateDataCollectorDeploymentScript(module, monitoringManagerIp, monitoringManagerPort);
                    deploymentScriptsElement.put("javaAppLevelDcDeploymentScript", javaDcDeploymentScript);
                }
                
                if(module.getModuleName().equals("NuroApp")){
                    
                    NuroDcDeploymentScriptGenerator nuroDcScriptGen= new NuroDcDeploymentScriptGenerator();
                    String nuroDcDeploymentScript = nuroDcScriptGen.generateDataCollectorDeploymentScript(module, monitoringManagerIp, monitoringManagerPort);
                    deploymentScriptsElement.put("nuroDcDeploymentScript", nuroDcDeploymentScript);
                }

                rulesElement.put("monitoringRules", sw.toString());

                policies.add(rulesElement);
                policies.add(deploymentScriptsElement);

                membersElement.put("policies", policies);

                List<String> members = new ArrayList<String>();
                members.add(module.getModuleName());
                membersElement.put("members", members);

                moduleElement.put("monitoringInformation_"+module.getModuleName(), membersElement);

                Yaml yaml = new Yaml();
                yaml.dump(moduleElement, writer);
                output.put(yaml.dump(moduleElement));

            }

            return output.toString();

        } catch (IOException e) {
            logger.error("Error reading the incoming data.", e);
        } catch (JAXBException e) {
            logger.error("Error unmarshalling the produced monitoring rules.", e);
        } catch (ParsingException e) {
            logger.error("Error parsing the Abstract Deployment Model", e);
        }

        return null;

    }

    public static void startServer() {

        final ResourceConfig rc = new ResourceConfig()
                .packages(MonitoringDamGenerator.class.getPackage().getName());
        HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(
                URI.create("http://0.0.0.0:" + port), rc, false);
        NetworkListener listener = httpServer.getListeners().iterator().next();
        ThreadPoolConfig thx = listener.getTransport()
                .getWorkerThreadPoolConfig();
        thx.setQueueLimit(500);
        thx.setMaxPoolSize(500);

        try {
            httpServer.start();
        } catch (IOException e) {
            logger.error("Error while starting the HTTP server.", e);
        }
    }

    public static void main(String[] args) throws IOException {

        // monitorIp should be retrieved from the dashboard specific endpoint

        if (args.length == 1) {
            monitoringManagerIp = args[0];
        } else if (args.length == 2) {
            monitoringManagerIp = args[0];
            monitoringManagerPort = args[1];
        } else if (args.length == 3) {
            monitoringManagerIp = args[0];
            monitoringManagerPort = args[1];
            port = args[2];
        }

        startServer();

    }

}