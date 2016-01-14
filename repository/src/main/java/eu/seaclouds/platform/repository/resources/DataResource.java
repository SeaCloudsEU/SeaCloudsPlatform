package eu.seaclouds.platform.repository.resources;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.platform.repository.store.Item;
import eu.seaclouds.platform.repository.store.MemoryItem;
import eu.seaclouds.platform.repository.store.ObjectStore;

@Path("/data")
public class DataResource {
    private static Logger log = LoggerFactory.getLogger(DataResource.class);
    
    private static ObjectStore map = new ObjectStore();
    
    public DataResource() {
    }
    
    @GET
    @Path("/{id}")
    public Response getData(@PathParam("id") String id) {
        Response response;
        
        log.debug("getData({})", id);
        if (map.containsKey(id)) {
            Item item = map.get(id);
            response = Response.ok(item.getData(), item.getContentType()).build();
        }
        else {
            response = Response.status(Response.Status.NOT_FOUND).build();
        }
        return response;
    }
    
    @POST
    public Response createData(@Context Request request, @Context HttpHeaders headers, byte[] input) {
        Response response;
        
        MediaType type = headers.getMediaType();
        String id = getId(headers);
        log.debug("createData({}, {}", id, type);
        MemoryItem item = new MemoryItem(id, type, input);

        response = saveValue(id, item);
        return response;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createDataMultipart(
            @Context Request request, @Context HttpHeaders headers, FormDataMultiPart form) {
        Response response;
        
        FormDataBodyPart dataPart = form.getField("data");
        String data = dataPart.getEntityAs(String.class);
        MediaType type = dataPart.getMediaType();
        String id = getId(headers);
        
        log.debug("createDataMulti({}, {})", data, type);
        MemoryItem item = new MemoryItem(id, type, data);

        response = saveValue(id, item);
        return response;
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteData(@PathParam("id") String id) {
        Response response;
        
        log.debug("deleteData({})", id);

        boolean success = map.remove(id);

        if (success) {
            response = Response.ok().build();
        }
        else {
            response = Response.status(Response.Status.NOT_FOUND).build();
        }
        return response;
    }
    
    private String getId(HttpHeaders headers) {
        
        String id = headers.getHeaderString("id");
        
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    private Response saveValue(String id, Item item) {
        Response response;
        
        boolean success = map.put(id, item);
        
        if (success) {
            response = Response.status(Status.CREATED).
                    entity(id).
                    type(MediaType.TEXT_PLAIN).
                    build();
        }
        else {
            response = Response.status(Status.CONFLICT).build();
        }
        return response;
    }
    
}
