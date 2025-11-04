package dev.rafandoo.gitwit.util;

import lombok.experimental.UtilityClass;

import java.util.Optional;

/**
 * Utility class for environment-related operations.
 */
@UtilityClass
public class EnvironmentUtil {

    /**
     * Checks if the current environment is set to testing.
     * <p>
     * This method checks the "GITWIT_ENV" environment variable to determine
     * if the application is running in a testing environment.
     *
     * @return {@code true} if in testing environment, {@code false} otherwise.
     */
    public static boolean isTesting() {
        return Optional.ofNullable(System.getenv("GITWIT_ENV"))
            .map(env -> env.equalsIgnoreCase("TEST"))
            .orElse(false);

    }

    /**
     * Checks if the current environment is set to production.
     * <p>
     * This method returns the inverse of {@link #isTesting()}.
     *
     * @return {@code true} if in production environment, {@code false} otherwise.
     */
    public static boolean isProd() {
        return !isTesting();
    }
}
