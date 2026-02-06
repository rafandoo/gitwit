package dev.rafandoo.gitwit.di;

import com.google.inject.AbstractModule;

/**
 * Guice module for configuring application dependencies.
 * <p>
 * This module can be extended to bind interfaces to their implementations
 * and manage the dependency graph for the application.
 */
public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        // Default bindings can be configured here
    }
}
