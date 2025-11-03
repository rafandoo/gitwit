package dev.rafandoo.gitwit.mock;

import dev.rafandoo.gitwit.service.GitService;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;

public abstract class AbstractGitMock {

    protected GitService spyGitService;
    protected MockedStatic<GitService> gitMock;

    protected void setupGitServiceMock() {
        this.spyGitService = spy(GitService.getInstance());
        this.gitMock = mockStatic(GitService.class);
        this.gitMock.when(GitService::getInstance).thenReturn(this.spyGitService);
    }

    protected void closeGitServiceMock() {
        if (this.gitMock != null) {
            this.gitMock.close();
        }
    }
}
