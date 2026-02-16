package dev.rafandoo.gitwit.di;

import com.google.inject.AbstractModule;
import dev.rafandoo.gitwit.service.changelog.render.ChangelogMarkdownRenderer;
import dev.rafandoo.gitwit.service.changelog.render.Renderer;

/**
 * Guice module for configuring application dependencies.
 * <p>
 * This module can be extended to bind interfaces to their implementations
 * and manage the dependency graph for the application.
 */
public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Renderer.class).to(ChangelogMarkdownRenderer.class);
    }
}
