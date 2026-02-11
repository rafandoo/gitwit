package dev.rafandoo.gitwit.di;

import com.google.inject.Injector;
import picocli.CommandLine.IFactory;

/**
 * Guice-based implementation of Picocli's IFactory for dependency injection.
 */
public class GuiceFactory implements IFactory {

    private final Injector injector;

    /**
     * Constructs a GuiceFactory with the given Injector.
     *
     * @param injector the Guice Injector to use for creating instances
     */
    public GuiceFactory(Injector injector) {
        this.injector = injector;
    }

    @Override
    public <K> K create(Class<K> cls) {
        return this.injector.getInstance(cls);
    }
}
