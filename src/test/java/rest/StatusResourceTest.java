package rest;

import org.junit.Test;
import build.BuildStatus;
import org.hamcrest.Matchers;
import org.junit.Assert;
import javax.ws.rs.core.Response;
import java.util.Arrays;

import static build.BuildStatus.*;
import static rest.BuildResourceTest.*;

/**
 * Created by Grisha Weintraub on 06/01/2017.
 */
public class StatusResourceTest {

    @Test
    public void getStatusForInvalidBuildIdTest(){
        Response re = new StatusResource().getStatus(INVALID_PROJECT_ID);
        Assert.assertEquals("We expect 404 in case of invalid build id", Response.Status.NOT_FOUND.getStatusCode(), re.getStatus());
    }

    @Test
    public void getStatusForValidBuildIdTest() throws InterruptedException {
        BuildResourceTest.BuildResponse  buildResponse = BuildResourceTest.buildProject(VALID_PROJECT_ID);
        Assert.assertEquals("We expect 200 in case of valid project id", Response.Status.OK.getStatusCode(), buildResponse.httpStatusCode);
        Thread.sleep(5000);
        Response statusResponse = new StatusResource().getStatus(buildResponse.buildId);
        Assert.assertEquals("We expect 200 in case of valid build id", Response.Status.OK.getStatusCode(), statusResponse.getStatus());
        BuildStatus buildStatus = BuildStatus.getById((int)statusResponse.getEntity());
        Assert.assertThat("Status of the valid build must be one of the {accepted, queued, running, done}", buildStatus,
                Matchers.isIn(Arrays.asList(ACCEPTED, QUEUED, RUNNING, DONE)));
    }

    @Test
    public void getStatusForFailingBuildTest() throws InterruptedException {
        BuildResourceTest.BuildResponse  buildResponse = BuildResourceTest.buildProject(FAILING_PROJECT_ID);
        Assert.assertEquals("We expect 200 in case of valid project id", Response.Status.OK.getStatusCode(), buildResponse.httpStatusCode);
        Thread.sleep(5000);
        Response statusResponse = new StatusResource().getStatus(buildResponse.buildId);
        Assert.assertEquals("We expect 200 in case of valid build id", Response.Status.OK.getStatusCode(), statusResponse.getStatus());
        BuildStatus buildStatus = BuildStatus.getById((int)statusResponse.getEntity());
        Assert.assertEquals("We expect failed status here", buildStatus, FAILED);
    }




}
