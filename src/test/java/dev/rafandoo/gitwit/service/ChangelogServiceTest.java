package dev.rafandoo.gitwit.service;

import com.google.inject.Inject;
import dev.rafandoo.gitwit.TestUtils;
import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.gitwit.di.GuiceExtension;
import dev.rafandoo.gitwit.enums.ChangelogScope;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.mock.CommitMockFactory;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(GuiceExtension.class)
@DisplayName("ChangelogService Tests")
class ChangelogServiceTest {

    @Inject
    GitService gitService;

    @Inject
    ChangelogService changelogService;

    @Inject
    I18nService i18nService;

    @Test
    void shouldReturnNullWhenNoCommitsFound() {
        TestUtils.setupConfig(".changelog.gitwit");
        GitWitConfig config = GitWitConfig.load();

        doReturn(List.of())
            .when(this.gitService)
            .getCommits(any(), any());

        StringBuilder changelog = this.changelogService.generateChangelog(
            "from",
            "to",
            config,
            "subtitle"
        );

        assertNull(changelog);
    }

    @Test
    void shouldThrowExceptionWhenNoTypesConfigured() {
        TestUtils.setupConfig(".changelog.gitwit");
        GitWitConfig config = GitWitConfig.load();
        config.getChangelog().getTypes().clear();

        GitWitException ex = assertThrows(
            GitWitException.class,
            () -> this.changelogService.generateChangelog("from", "to", config, "subtitle")
        );

        String expected = this.i18nService.getMessage("changelog.error.types_required");
        assertAll(
            () -> assertTrue(ex.getMessage().contains(expected))
        );
    }

    @Test
    void shouldGenerateMarkdownChangelogSuccessfully() {
        TestUtils.setupConfig(".changelog.gitwit");
        GitWitConfig config = GitWitConfig.load();

        List<RevCommit> commits = List.of(
            CommitMockFactory.mockCommit("abcd1234", "feat: new feature"),
            CommitMockFactory.mockCommit("efgh5678", "fix: bug fix"),
            CommitMockFactory.mockCommit("ijkl9012", "perf: performance improvement"),
            CommitMockFactory.mockCommit("mnop3456", "fix!: breaking change fix")
        );

        doReturn(commits)
            .when(this.gitService)
            .getCommits(any(), any());

        StringBuilder changelog = this.changelogService.generateChangelog(
            "from",
            "to",
            config,
            "subtitle"
        );

        assertAll(
            () -> assertNotNull(changelog),
            () -> assertTrue(changelog.toString().contains("New features")),
            () -> assertTrue(changelog.toString().contains("new feature"))
        );
    }

    @Test
    void shouldWriteChangelogFileSuccessfully(@TempDir Path tempDir) throws IOException {
        Path repoPath = tempDir.resolve("repo");
        Files.createDirectories(repoPath);

        doReturn(repoPath)
            .when(this.gitService)
            .getRepo();

        Path result = this.changelogService.writeChangeLog("content", false);

        assertAll(
            () -> assertTrue(Files.exists(result)),
            () -> assertEquals("content", Files.readString(result))
        );
    }

    @Test
    void shouldReturnDefaultTemplateWhenScopeIsMissing() {
        GitWitConfig.ChangelogConfig.ChangelogFormat format = new GitWitConfig.ChangelogConfig.ChangelogFormat();
        format.setDefaultTemplate("- %s");

        String result = this.changelogService.getChangelogCommitTemplateByScope(format, ChangelogScope.SECTION);
        assertEquals("- %s", result);
    }

    @Test
    void shouldThrowExceptionWhenAllTemplatesEmpty() {
        GitWitConfig.ChangelogConfig.ChangelogFormat format = new GitWitConfig.ChangelogConfig.ChangelogFormat();
        format.setDefaultTemplate(null);

        assertThrows(
            GitWitException.class,
            () -> this.changelogService.getChangelogCommitTemplateByScope(format, ChangelogScope.SECTION)
        );
    }

}
