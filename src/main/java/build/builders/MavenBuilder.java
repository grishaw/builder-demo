package build.builders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.shared.invoker.*;

/**
 * Created by Grisha Weintraub on 07/01/2017.
 */
public class MavenBuilder implements Builder{

    private static final Builder INSTANCE = new MavenBuilder();
    private static final List <String> DEFAULT_GOALS = Arrays.asList("clean", "install");

    private static final Logger logger = LogManager.getLogger(MavenBuilder.class);

    public static Builder getInstance(){
        return INSTANCE;
    }

    @Override
    public boolean build(Path input, Path output) {
        InvocationRequest request = createRequest(input, output);
        if (request != null) {
            Invoker invoker = new DefaultInvoker();
            try {
                InvocationResult result = invoker.execute(request);
                return result.getExitCode() == 0;
            } catch (MavenInvocationException me) {
                logger.error("Failed to build project located in - " + input.getFileName(), me);
            }
        }
        return false;
    }

    private InvocationRequest createRequest(Path input, Path output){
        try {
            Files.createDirectories(output);
        }catch(IOException ioe){
            logger.error("Failed to create directory - " + output.getFileName(), ioe);
            return null;
        }
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(input.toFile());
        request.setGoals(DEFAULT_GOALS);
        request.setLocalRepositoryDirectory(output.toFile());
        return request;
    }

}
