package dev.rafandoo.gitwit.di;

import dev.rafandoo.gitwit.service.git.*;

import static org.mockito.Mockito.mock;

public class TestAppModule extends AppModule {

    @Override
    protected void configure() {
        super.configure();

        GitService gitService = mock(GitService.class);
        bind(GitService.class).toInstance(gitService);

        GitRepositoryService gitRepositoryService = mock(GitRepositoryService.class);
        bind(GitRepositoryService.class).toInstance(gitRepositoryService);

        GitConfigService gitConfigService = mock(GitConfigService.class);
        bind(GitConfigService.class).toInstance(gitConfigService);

        GitHookService gitHookService = mock(GitHookService.class);
        bind(GitHookService.class).toInstance(gitHookService);

        GitCommitService gitCommitService = mock(GitCommitService.class);
        bind(GitCommitService.class).toInstance(gitCommitService);
    }
}
