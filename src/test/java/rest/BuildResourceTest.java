package rest;

import org.junit.Test;
import org.junit.Assert;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.*;

/**
 * Created by Grisha Weintraub on 06/01/2017.
 */
public class BuildResourceTest {

    static final long INVALID_PROJECT_ID = -999;
    static final long VALID_PROJECT_ID = 1000;
    static final long FAILING_PROJECT_ID = 2000;

    @Test
    public void buildInvalidProjectIdTest(){
        BuildResponse re = buildProject(INVALID_PROJECT_ID);
        Assert.assertEquals("We expect 404 in case of invalid project id", NOT_FOUND.getStatusCode(), re.httpStatusCode);
    }

    @Test
    public void buildValidProjectTest(){
        BuildResponse re = buildProject(VALID_PROJECT_ID);
        Assert.assertEquals("We expect 200 in case of valid project id", OK.getStatusCode(), re.httpStatusCode);
        Assert.assertTrue("We expect positive build id", re.buildId > 0);
    }

    @Test
    public void buildSeveralValidProjectsTest(){
        BuildResponse re1 = buildProject(VALID_PROJECT_ID);
        BuildResponse re2 = buildProject(VALID_PROJECT_ID);
        Assert.assertEquals("We expect 200 in case of valid project id", OK.getStatusCode(), re1.httpStatusCode);
        Assert.assertEquals("We expect 200 in case of valid project id", OK.getStatusCode(), re2.httpStatusCode);
        Assert.assertTrue("We expect positive build id", re1.buildId > 0);
        Assert.assertTrue("We expect positive build id", re2.buildId > 0);
        Assert.assertFalse("We expect uniqueness of build ids", re1.buildId.equals(re2.buildId));
    }

    static BuildResponse buildProject(long projectId){
        Response r = new BuildResource().buildProject(projectId);
        return new BuildResponse(r.getStatus(), (Long)r.getEntity());
    }

    static class BuildResponse{
        int httpStatusCode;
        Long buildId;

        public BuildResponse(int httpStatusCode, Long buildId) {
            this.httpStatusCode = httpStatusCode;
            this.buildId = buildId;
        }
    }

}
