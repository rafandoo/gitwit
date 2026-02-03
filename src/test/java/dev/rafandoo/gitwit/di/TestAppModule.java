package dev.rafandoo.gitwit.di;

import dev.rafandoo.gitwit.service.GitService;
import org.mockito.Mockito;

public class TestAppModule extends AppModule {

    @Override
    protected void configure() {
        super.configure();

        GitService gitService = Mockito.mock(GitService.class);
        bind(GitService.class).toInstance(gitService);
    }
}
