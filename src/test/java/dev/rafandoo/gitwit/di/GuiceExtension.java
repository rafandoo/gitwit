package dev.rafandoo.gitwit.di;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.util.Arrays;
import java.util.List;

public class GuiceExtension implements TestInstancePostProcessor, ParameterResolver {

    private Injector injector;

    @Override
    public void postProcessTestInstance(
        Object testInstance,
        ExtensionContext context
    ) {
        Module baseModule = new TestAppModule();

        OverrideModules override =
            context.getRequiredTestClass()
                .getAnnotation(OverrideModules.class);

        if (override != null) {
            List<Module> overrideModules = Arrays.stream(override.value())
                .map(this::newModule)
                .toList();

            this.injector = Guice.createInjector(
                Modules.override(baseModule).with(overrideModules)
            );
        } else {
            this.injector = Guice.createInjector(baseModule);
        }

        InjectorFactory.setInjector(this.injector);
        this.injector.injectMembers(testInstance);
    }

    private Module newModule(Class<? extends Module> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create module: " + type, e);
        }
    }

    @Override
    public boolean supportsParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    ) {
        return parameterContext.getParameter().isAnnotationPresent(Inject.class);
    }

    @Override
    public Object resolveParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    ) {
        return this.injector.getInstance(parameterContext.getParameter().getType());
    }
}
