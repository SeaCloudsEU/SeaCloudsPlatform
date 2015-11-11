package eu.seaclouds.monitor.monitoringDamGeneratorService;

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

import eu.seaclouds.monitor.monitoringDamGenerator.MonitoringDamGenerator;
import eu.seaclouds.monitor.monitoringDamGenerator.adpParsing.Module;

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
    @Path("/damgen")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public ApplicationId generateMonitoringInfo(String adp) {

        MonitoringDamGenerator monDamGen= new MonitoringDamGenerator(monitoringManagerIp, monitoringManagerPort);
        
        String generatedApplicationId = UUID.randomUUID().toString();
        
        List<Module> generated = monDamGen.generateMonitoringInfo(adp);
        
        monitoringInfoByApplication.put(generatedApplicationId, generated);   
        
        return new ApplicationId(generatedApplicationId);

    }
    
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
    
    @GET
    @Path("/{id}/generatedDam")
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationToscaDcScripts getGeneratedPiecesOfDamByApplication(@PathParam("id") String applicationId){
        
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