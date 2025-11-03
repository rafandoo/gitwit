package dev.rafandoo.gitwit.mock;

import dev.rafandoo.gitwit.service.GitService;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;

/**
 * This class provides methods to mock the static {@code GitService.getInstance()} method
 * and replace it with a spy instance, allowing controlled behavior in unit tests.
 */
public abstract class AbstractGitMock {

    /**
     * Spy of the {@link GitService} singleton instance.
     */
    protected GitService spyGitService;

    /**
     * Static mock of the {@link GitService} class.
     */
    protected MockedStatic<GitService> gitMock;

    /**
     * Sets up the mock for {@code GitService}.
     * <p>
     * Creates a spy for the real {@code GitService} instance and mocks the
     * {@code GitService.getInstance()} method to return the spy.
     */
    protected void setupGitServiceMock() {
        this.spyGitService = spy(GitService.getInstance());
        this.gitMock = mockStatic(GitService.class);
        this.gitMock.when(GitService::getInstance).thenReturn(this.spyGitService);
    }

    /**
     * Closes the mock for {@code GitService}.
     */
    protected void closeGitServiceMock() {
        if (this.gitMock != null) {
            this.gitMock.close();
        }
    }
}
