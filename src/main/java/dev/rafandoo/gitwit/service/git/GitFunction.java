package dev.rafandoo.gitwit.service.git;

import org.eclipse.jgit.api.Git;

/**
 * A functional interface representing a function that takes a Git instance and returns a result of type T.
 *
 * @param <T> the type of the result produced by the function.
 */
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
