package build.builders;

/**
 * Created by Grisha Weintraub on 07/01/2017.
 */
public class BuildersFactory {

    public static Builder getDefaultBuilder(){
        return MavenBuilder.getInstance();
    }
}
