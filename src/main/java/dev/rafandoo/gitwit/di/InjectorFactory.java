package dev.rafandoo.gitwit.di;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Setter;

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

}
