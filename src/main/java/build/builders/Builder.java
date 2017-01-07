package build.builders;

import java.nio.file.Path;

/**
 * Created by Grisha Weintraub on 07/01/2017.
 */

public interface Builder {

    /**
     * Builds a project located in the directory <code>input</code> and stores the result in the directory <code>output</code>
     * @param input location of the project to build
     * @param output location of the build result
     * @return true if the build succeeded, false otherwise
     */
    boolean build(Path input, Path output);
}
