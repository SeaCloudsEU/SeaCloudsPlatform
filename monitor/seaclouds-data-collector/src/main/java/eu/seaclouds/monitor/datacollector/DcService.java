package eu.seaclouds.monitor.datacollector;

import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DcService {
    static Logger log = LoggerFactory.getLogger(DcService.class);
    
    public DcService(String dc_sync_period, String  resources_keep_alive_period, String manager_ip, String manager_port) {
        
        Registry.initialize(dc_sync_period, resources_keep_alive_period, manager_ip, manager_port);

    }
    
    @POST
    @Path("/resource")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addMonitoredResource(MonitoredResource resource) {  
        
        log.info("Adding resource:");
        log.info(resource.getType());
        log.info(resource.getId());
        log.info(resource.getUrl());

        try{
            
            Registry.addResource(resource.getType(), resource.getId(), resource.getUrl());
            
            return Response.ok().build();
        }catch(Exception e){
            log.error("There was an error while adding a new resource to the SeaClouds Data Collector");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        

    }
}
