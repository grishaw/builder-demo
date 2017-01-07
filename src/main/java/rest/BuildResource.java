package rest;

/**
 * Created by Grisha Weintraub on 06/01/2017.
 */

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import build.BuilderService;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static javax.ws.rs.core.Response.Status.*;

@Path("/build")
public class BuildResource {

    private static final Logger logger = LogManager.getLogger(BuildResource.class);

    @GET
    @Path("/{projectId}")
    public Response buildProject(@PathParam("projectId") long projectId) {
        if (BuilderService.projectExists(projectId)){
            Long buildId = BuilderService.buildProject(projectId);
            if (buildId != null) {
                  return Response.status(OK).entity(buildId).build();
            }
            return Response.status(INTERNAL_SERVER_ERROR).build();
        }
        logger.warn("Got invalid project id : " + projectId);
        return Response.status(NOT_FOUND).build();
    }
}