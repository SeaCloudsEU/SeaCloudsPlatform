package seaclouds.monitor.api;

import com.wordnik.swagger.annotations.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * @author MBarrientos
 */

@Path("/pet")
@Api(value = "/pet", description = "Operations about pets")
@Produces({"application/json", "application/xml"})
public interface CatalogApi {

    @GET
    @Path("/{petId}")
    @ApiOperation(value = "Find pet by ID", notes = "More notes about this method")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Pet not found")
    })
    public Response getPetById(
            @ApiParam(value = "ID of pet to fetch", required = true) @PathParam("petId") String petId)
            throws WebApplicationException;

}
