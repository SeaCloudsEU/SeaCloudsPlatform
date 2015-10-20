package eu.seaclouds.monitor.monitoringdamgeneratorservice;

import it.polimi.tower4clouds.rules.MonitoringRules;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringDamGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Module;

@Path("/")
public class Service{

    private static String monitoringManagerIp;
    private static String monitoringManagerPort;

    private static Map<String, List<Module>> monitoringInfoByApplication=new HashMap<String,List<Module>>();
    
    public Service(String monitorUrl,
            String monitorPort)
        {
        monitoringManagerIp = monitorUrl;
        monitoringManagerPort = monitorPort;
      
        }
    
    
    @POST
    @Path("/generateDamMonitoringInfo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public ApplicationId generateMonitoringInfo(String adp) {

        MonitoringDamGenerator monDamGen= new MonitoringDamGenerator(monitoringManagerIp, monitoringManagerPort);
        
        String generatedApplicationId = UUID.randomUUID().toString();
        List<Module> generated = monDamGen.generateMonitoringInfo(adp);
        
        monitoringInfoByApplication.put(generatedApplicationId, generated);   
        
        return new ApplicationId(generatedApplicationId);

    }
    
    
    /**
     * @param applicationId the id of an application previously obtained calling the /generateDamMonitoringInfo endpoint
     * @return              all the monitoring rules that need to be installed into Tower 4Cloud
     */
    @GET
    @Path("/{id}/monitoringRules/")
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationRules getMonitoringRulesByApplication(@PathParam("id") String applicationId){
        
        MonitoringRules toReturn=new MonitoringRules();
        StringWriter sw = new StringWriter();
        
        for(Module i: monitoringInfoByApplication.get(applicationId)){
            
            if(i.getRules()!=null){
                toReturn.getMonitoringRules().addAll(i.getRules().getMonitoringRules());
            }
            
            if(i.getHost().getRules() != null){
                toReturn.getMonitoringRules().addAll(i.getHost().getRules().getMonitoringRules());
            }
            
        }
        
        JAXBContext context;
        try {
            context = JAXBContext
                    .newInstance("it.polimi.tower4clouds.rules");
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
            marshaller.marshal(toReturn, sw);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new ApplicationRules(sw.toString());
    }
    
    /**
     * @param applicationId the id of an application previously obtained calling the /generateDamMonitoringInfo endpoint
     * @return              the list of all the node templates that need to be added to the DAM 
     *                      in order to deploy the data collectors associated which applicationId
     */
    @GET
    @Path("/{id}/dataCollectorNodeTemplates")
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationToscaDcScripts getDataCollectorNodeTemplatesByApplication(@PathParam("id") String applicationId){
        
        List<String> output = new ArrayList<String>();
        
        for(Module i: monitoringInfoByApplication.get(applicationId)){
            
            for(String dc: i.getDataCollectorToscaDeploymentScripts().keySet()){
                output.add(i.getDataCollectorToscaDeploymentScripts().get(dc));
            }
            
            for(String dc: i.getHost().getDataCollectorToscaDeploymentScripts().keySet()){
                output.add(i.getHost().getDataCollectorToscaDeploymentScripts().get(dc));
            }
           
        }
        
        return new ApplicationToscaDcScripts(output);
    }

}