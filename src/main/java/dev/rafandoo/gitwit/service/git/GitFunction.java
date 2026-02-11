package dev.rafandoo.gitwit.service.git;

import org.eclipse.jgit.api.Git;

@FunctionalInterface
public interface GitFunction<T> {

    /**
     * Applies this function to the given Git instance.
     *
     * @param git the Git instance to which the function is applied.
     * @return the function result.
     */
    T apply(Git git);

}
