package build;

import build.builders.Builder;
import build.builders.BuildersFactory;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static build.BuildStatus.*;

/**
 * Created by Grisha Weintraub on 06/01/2017.
 */
public class BuilderService {

    private static final AtomicLong buildIdSequence;
    private static final ExecutorService threadPool;
    private static final Map<Long, Path> projectsLocations;
    private static final Map<Long, BuildStatus> buildsStatuses;
    private static final String OUTPUT_LOCATION;

    private static final Logger logger = LogManager.getLogger(BuilderService.class, new StringFormatterMessageFactory());

    static{
        Map<Long, Path> tempProjectsMap = getProjectsMap();
        Configuration builderConfig = getBuilderConfig();

        buildsStatuses = new ConcurrentHashMap<>();
        projectsLocations = Collections.unmodifiableMap(tempProjectsMap);
        buildIdSequence = new AtomicLong(builderConfig.getInt("build.id.start"));
        threadPool = Executors.newFixedThreadPool(builderConfig.getInt("build.workers.num"));
        OUTPUT_LOCATION = builderConfig.getString("build.output.dir");
    }

    private static Map <Long, Path> getProjectsMap(){
        Map<Long, Path> tempMap = new ConcurrentHashMap<>();
        Configurations configurations = new Configurations();
        try {
            Configuration projectsConfig = configurations.properties(new File("projects.properties"));
            Iterator<String> iterator = projectsConfig.getKeys();
            while (iterator.hasNext()){
                String curKey = iterator.next();
                tempMap.put(Long.valueOf(curKey), Paths.get(projectsConfig.getString(curKey)));
            }
        } catch (Exception e) {
            logger.error("Init failed !", e);
            System.exit(-1);
        }
        return tempMap;
    }

    private static Configuration getBuilderConfig(){
        Configurations configurations = new Configurations();
        Configuration builderConfig = null;
        try {
            builderConfig = configurations.properties(new File("builder.properties"));
        } catch (Exception e) {
            logger.error("Init failed !", e);
            System.exit(-1);
        }
        return builderConfig;
    }

    /**
     * Builds a project with the specified id.
     *
     * @param projectId project id to build
     * @return unique build id or null if there was an error to accept the build request
     */
    public static Long buildProject(long projectId){
        Long buildId = buildIdSequence.getAndIncrement();
        buildsStatuses.put(buildId, ACCEPTED);
        if (submitBuildTask(projectId, buildId)){
            buildsStatuses.compute(buildId, (k, v) -> (v == ACCEPTED) ? QUEUED : v);
        }else{
            buildsStatuses.put(buildId, FAILED);
            buildId = null;
        }
        return buildId;
    }

    private static boolean submitBuildTask(long projectId, long buildId){
        try {
            threadPool.execute(new BuildTask(projectId, buildId));
            return true;
        }catch(RejectedExecutionException e){
            logger.error("Submitting of the task failed", e);
            return false;
        }
    }

    public static int getBuildStatusId(long buildId){
        return buildsStatuses.get(buildId).getId();
    }

    public static boolean projectExists(long projectId){
        return projectsLocations.containsKey(projectId);
    }

    public static boolean buildExists(long buildId){
        return buildsStatuses.containsKey(buildId);
    }

    private static class BuildTask implements Runnable{
        long projectId;
        long buildId;

        BuildTask(long projectId, long buildId){
            this.projectId = projectId;
            this.buildId = buildId;
        }

        @Override
        public void run() {
            logger.debug("Going to build project : %d, build id : %d", projectId, buildId);
            Builder builder = BuildersFactory.getDefaultBuilder();
            boolean result;
            synchronized(projectsLocations.get(projectId)) {
                buildsStatuses.put(buildId, RUNNING);
                result = builder.build(projectsLocations.get(projectId), Paths.get(OUTPUT_LOCATION + buildId));
            }
            BuildStatus status = result ? DONE : FAILED;
            buildsStatuses.put(buildId, status);
            logger.debug("Finished building project : %d, build id : %d, the status is : %s", projectId, buildId, status);
        }
    }

}
