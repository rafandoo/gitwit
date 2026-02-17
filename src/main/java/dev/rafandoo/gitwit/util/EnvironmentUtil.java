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
     *
     * @return {@code true} if in testing environment, {@code false} otherwise.
     */
    public static boolean isTesting() {
        return Optional.ofNullable(System.getenv("GITWIT_ENV"))
            .map(env -> env.equalsIgnoreCase("TEST"))
            .orElse(false);
    }

    /**
     * Checks if the current environment is set to development.
     *
     * @return {@code true} if in development environment, {@code false} otherwise.
     */
    public static boolean isDevelopment() {
        return Optional.ofNullable(System.getenv("GITWIT_ENV"))
            .map(env -> env.equalsIgnoreCase("DEV"))
            .orElse(false);
    }

    /**
     * Checks if the current environment is set to continuous integration (CI).
     *
     * @return {@code true} if in CI environment, {@code false} otherwise.
     */
    public static boolean isCI() {
        return Optional.ofNullable(System.getenv("GITWIT_ENV"))
            .map(env -> env.equalsIgnoreCase("CI"))
            .orElse(false);
    }

    /**
     * Checks if the current environment is set to production.
     *
     * @return {@code true} if in production environment, {@code false} otherwise.
     */
    public static boolean isProd() {
        return !isTesting() && !isDevelopment();
    }
}
