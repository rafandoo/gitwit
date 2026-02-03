package dev.rafandoo.gitwit.di;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Setter;
import dev.rafandoo.gitwit.util.EnvironmentUtil;

/**
 * Provides a shared Guice {@link Injector} instance for the application.
 */
public final class InjectorFactory {

    @Setter
    private static volatile Injector injector;

    /**
     * Private constructor to prevent instantiation.
     */
    private InjectorFactory() {
    }

    /**
     * Returns the shared Guice {@link Injector} instance, creating it
     * on first access.
     *
     * @return the application-wide {@link Injector}.
     */
    public static Injector get() {
        if (injector == null) {
            synchronized (InjectorFactory.class) {
                if (injector == null) {
                    injector = Guice.createInjector(new AppModule());
                }
            }
        }
        return injector;
    }

    /**
     * Sets the Guice {@link Injector} instance.
     * <p>
     * This method is intended for use in non-production environments
     * (e.g., testing, development) to allow for custom injector setups.
     *
     * @param injector the {@link Injector} to set.
     * @throws IllegalStateException if called in a production environment.
     */
    public static void setInjector(Injector injector) {
        if (EnvironmentUtil.isProd()) {
            throw new IllegalStateException("Injector can only be set in non-production environments.");
        }
        InjectorFactory.injector = injector;
    }
}
