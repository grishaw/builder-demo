package rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import build.BuilderService;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static javax.ws.rs.core.Response.Status.*;

/**
 * Created by Grisha Weintraub on 06/01/2017.
 */

@Path("/status")
public class StatusResource {

    private static final Logger logger = LogManager.getLogger(StatusResource.class);

    @GET
    @Path("/{buildId}")
    public Response getStatus(@PathParam("buildId") long buildId) {
        if (BuilderService.buildExists(buildId)) {
            return Response.status(OK).entity(BuilderService.getBuildStatusId(buildId)).build();
        }
        logger.warn("Got invalid build id : " + buildId);
        return Response.status(NOT_FOUND).build();
    }
}
