package dev.rafandoo.gitwit.service.changelog;

import dev.rafandoo.gitwit.TestUtils;
import dev.rafandoo.gitwit.cli.dto.ChangelogOptions;
import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.gitwit.entity.Changelog;
import dev.rafandoo.gitwit.entity.CommitMessage;
import dev.rafandoo.gitwit.enums.ChangelogScope;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.mock.CommitMockFactory;
import dev.rafandoo.gitwit.service.git.GitRepositoryService;
import dev.rafandoo.gitwit.service.I18nService;
import dev.rafandoo.gitwit.service.MessageService;
import dev.rafandoo.gitwit.service.changelog.render.Renderer;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChangelogService test")
class ChangelogServiceTest {

    @Mock
    MessageService messageService;

    @Mock
    GitRepositoryService gitRepositoryService;

    @Mock
    Renderer renderer;

    @Mock
    ChangelogOutputService outputService;

    @Mock
    ChangelogVersionResolver versionResolver;

    ChangelogService service;

    I18nService i18nService = new I18nService();

    @BeforeEach
    void setup() {
        this.service = new ChangelogService(
            this.messageService,
            this.gitRepositoryService,
            this.renderer,
            this.outputService,
            this.versionResolver
        );
    }

    @Nested
    @DisplayName("handle method")
    class Handle {

        @Test
        void shouldGenerateRenderAndOutputChangelog() {
            TestUtils.setupConfig(".changelog.gitwit");
            GitWitConfig config = GitWitConfig.load();
            ChangelogOptions options = new ChangelogOptions(
                null,
                null,
                false,
                new ChangelogOptions.SubtitleOptions(
                    "subtitle",
                    false
                ),
                new ChangelogOptions.OutputOptions(),
                new ChangelogOptions.TagOptions(),
                new ChangelogOptions.VersionOptions()
            );


            List<RevCommit> commits = List.of(
                CommitMockFactory.mockCommit("a1", "feat: add feature"),
                CommitMockFactory.mockCommit("b2", "fix: bug fix")
            );

            when(gitRepositoryService.resolveCommits(eq("HEAD"), any(), any(), anyList()))
                .thenReturn(new ArrayList<>(commits));

            when(renderer.render(any(Changelog.class), eq(false)))
                .thenReturn("rendered");

            service.handle(
                "HEAD",
                options,
                config
            );

            verify(renderer).render(any(Changelog.class), eq(false));
            verify(outputService).output("rendered", false, false, config, false);
            verify(messageService).success("changelog.generated");
        }

        @Test
        void shouldDoNothingWhenNoCommits() {
            TestUtils.setupConfig(".changelog.gitwit");
            GitWitConfig config = GitWitConfig.load();
            ChangelogOptions options = new ChangelogOptions(
                null,
                null,
                false,
                new ChangelogOptions.SubtitleOptions(
                    "subtitle",
                    false
                ),
                new ChangelogOptions.OutputOptions(),
                new ChangelogOptions.TagOptions(),
                new ChangelogOptions.VersionOptions()
            );

            when(gitRepositoryService.resolveCommits(eq("HEAD"), any(), any(), anyList()))
                .thenReturn(new ArrayList<>());

            service.handle(
                "HEAD",
                options,
                config
            );

            verify(messageService).warn("changelog.warn.no_commits");
            verifyNoInteractions(renderer, outputService);
        }

        @Test
        void shouldThrowWhenNoTypesConfigured() {
            TestUtils.setupConfig(".changelog.gitwit");
            GitWitConfig config = GitWitConfig.load();
            config.getChangelog().getTypes().clear();

            ChangelogOptions options = new ChangelogOptions(
                null,
                null,
                false,
                new ChangelogOptions.SubtitleOptions(
                    "subtitle",
                    false
                ),
                new ChangelogOptions.OutputOptions(),
                new ChangelogOptions.TagOptions(),
                new ChangelogOptions.VersionOptions()
            );

            assertThatThrownBy(() ->
                service.handle(
                    "",
                    options,
                    config
                )
            )
                .isInstanceOf(GitWitException.class)
                .hasMessage(i18nService.getMessage("changelog.error.types_required"));
            verifyNoInteractions(renderer, outputService);
        }

        @Test
        void shouldUseNullSubtitleWhenNoSubtitleOptionIsEnabled() {
            TestUtils.setupConfig(".changelog.gitwit");
            GitWitConfig config = GitWitConfig.load();

            ChangelogOptions options = new ChangelogOptions(
                null,
                null,
                false,
                new ChangelogOptions.SubtitleOptions(
                    null,
                    true
                ),
                new ChangelogOptions.OutputOptions(),
                new ChangelogOptions.TagOptions(),
                new ChangelogOptions.VersionOptions()
            );

            List<RevCommit> commits = List.of(
                CommitMockFactory.mockCommit("a1", "feat: test")
            );

            when(gitRepositoryService.resolveCommits(anyString(), any(), any(), anyList()))
                .thenReturn(commits);

            when(renderer.render(any(Changelog.class), anyBoolean()))
                .thenReturn("rendered");

            service.handle("HEAD", options, config);

            verify(versionResolver, never()).resolveSubtitle(any(ChangelogOptions.class));
        }

        @Test
        void shouldNotPrintSuccessMessageWhenStdoutIsEnabled() {
            TestUtils.setupConfig(".changelog.gitwit");
            GitWitConfig config = GitWitConfig.load();

            ChangelogOptions.OutputOptions outputOptions = new ChangelogOptions.OutputOptions(false, true);

            ChangelogOptions options = new ChangelogOptions(
                null,
                null,
                false,
                new ChangelogOptions.SubtitleOptions("subtitle", false),
                outputOptions,
                new ChangelogOptions.TagOptions(),
                new ChangelogOptions.VersionOptions()
            );

            List<RevCommit> commits = List.of(
                CommitMockFactory.mockCommit("a1", "feat: test")
            );

            when(gitRepositoryService.resolveCommits(anyString(), any(), any(), anyList()))
                .thenReturn(commits);

            when(renderer.render(any(Changelog.class), anyBoolean()))
                .thenReturn("rendered");

            service.handle("HEAD", options, config);

            verify(messageService, never()).success(anyString());
        }
    }

    @Nested
    @DisplayName("generate method")
    class Generate {

        @Test
        void shouldReturnNullWhenGroupedIsEmpty() {
            TestUtils.setupConfig(".changelog.gitwit");
            GitWitConfig config = GitWitConfig.load();

            Changelog result = service.generate(
                config,
                Map.of(),
                Map.of("feat", "Features"),
                null
            );

            assertThat(result).isNull();
            verify(messageService).warn("changelog.warn.no_commits");
        }

        @Test
        void shouldExtractBreakingChanges() {
            TestUtils.setupConfig(".changelog.gitwit");
            GitWitConfig config = GitWitConfig.load();

            CommitMessage breaking = CommitMessage.of(
                CommitMockFactory.mockCommit("a1", "feat!: breaking change")
            );

            Map<String, List<CommitMessage>> grouped = new HashMap<>();
            grouped.put("feat", new ArrayList<>(List.of(breaking)));

            Changelog changelog = service.generate(
                config,
                grouped,
                Map.of("feat", "Features"),
                null
            );

            assertThat(changelog).isNotNull();
            assertThat(changelog.breakingChanges().isEmpty()).isFalse();
        }

        @Test
        void shouldNotExtractBreakingChangesWhenDisabled() {
            TestUtils.setupConfig(".changelog.gitwit");
            GitWitConfig config = GitWitConfig.load();
            config.getChangelog().setShowBreakingChanges(false);

            CommitMessage breaking = CommitMessage.of(
                CommitMockFactory.mockCommit("a1", "feat!: breaking")
            );

            Map<String, List<CommitMessage>> grouped = new HashMap<>();
            grouped.put("feat", new ArrayList<>(List.of(breaking)));

            Changelog result = service.generate(
                config,
                grouped,
                Map.of("feat", "Features"),
                null
            );

            assertThat(result.breakingChanges()).isEmpty();
        }

        @Test
        void shouldNotIncludeOtherTypesWhenDisabled() {
            TestUtils.setupConfig(".changelog.gitwit");
            GitWitConfig config = GitWitConfig.load();
            config.getChangelog().setShowOtherTypes(false);

            CommitMessage msg = CommitMessage.of(
                CommitMockFactory.mockCommit("a1", "chore: test")
            );

            Map<String, List<CommitMessage>> grouped = new HashMap<>();
            grouped.put("chore", new ArrayList<>(List.of(msg)));

            List<String> result = service.generate(
                config,
                grouped,
                Map.of("feat", "Features"),
                null
            ).otherChanges();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getTemplate method")
    class GetTemplate {

        @Test
        void getTemplate_shouldFallbackToDefault() {
            GitWitConfig.ChangelogConfig.ChangelogFormat format =
                new GitWitConfig.ChangelogConfig.ChangelogFormat();

            format.setDefaultTemplate("- %s");

            String template = service.getChangelogCommitTemplateByScope(
                format,
                ChangelogScope.SECTION
            );

            assertThat(template).isEqualTo("- %s");
        }

        @Test
        void getTemplate_shouldThrowWhenNoTemplateDefined() {
            GitWitConfig.ChangelogConfig.ChangelogFormat format =
                new GitWitConfig.ChangelogConfig.ChangelogFormat();
            format.setDefaultTemplate(null);

            assertThatThrownBy(() ->
                service.getChangelogCommitTemplateByScope(format, ChangelogScope.SECTION)
            )
                .isInstanceOf(GitWitException.class)
                .hasMessage(i18nService.getMessage("changelog.error.no_template"));
        }
    }

    @Nested
    @DisplayName("Resolve commits")
    class ResolveCommits {

        @Test
        void shouldResolveCommitsFromLatestTagToHead() {
            TestUtils.setupConfig(".changelog.gitwit");
            GitWitConfig config = GitWitConfig.load();

            ChangelogOptions.TagOptions tagOptions = new ChangelogOptions.TagOptions(true, null);

            ChangelogOptions options = new ChangelogOptions(
                null,
                null,
                false,
                new ChangelogOptions.SubtitleOptions("subtitle", false),
                new ChangelogOptions.OutputOptions(),
                tagOptions,
                new ChangelogOptions.VersionOptions()
            );

            when(gitRepositoryService.getLatestTag())
                .thenReturn("v1.0.0");

            List<RevCommit> commits = List.of(
                CommitMockFactory.mockCommit("a1", "feat: test")
            );

            when(gitRepositoryService.resolveCommits(anyString(), any(), any(), anyList()))
                .thenReturn(commits);

            when(renderer.render(any(Changelog.class), anyBoolean()))
                .thenReturn("rendered");

            service.handle("HEAD", options, config);

            verify(gitRepositoryService)
                .resolveCommits(eq("v1.0.0..HEAD"), any(), any(), anyList());
        }

        @Test
        void shouldFallbackToCaretWhenNoPreviousTagExists() {
            TestUtils.setupConfig(".changelog.gitwit");
            GitWitConfig config = GitWitConfig.load();

            ChangelogOptions.TagOptions tagOptions = new ChangelogOptions.TagOptions(false, "v2.0.0");

            ChangelogOptions options = new ChangelogOptions(
                null,
                null,
                false,
                new ChangelogOptions.SubtitleOptions("subtitle", false),
                new ChangelogOptions.OutputOptions(),
                tagOptions,
                new ChangelogOptions.VersionOptions()
            );

            when(gitRepositoryService.getPreviousTag("v2.0.0"))
                .thenReturn(null);

            List<RevCommit> commits = List.of(
                CommitMockFactory.mockCommit("a1", "feat: test")
            );

            when(gitRepositoryService.resolveCommits(anyString(), any(), any(), anyList()))
                .thenReturn(commits);

            when(renderer.render(any(Changelog.class), anyBoolean()))
                .thenReturn("rendered");

            service.handle("HEAD", options, config);

            verify(messageService)
                .warn("changelog.warn.no_previous_tag", "v2.0.0");

            verify(gitRepositoryService)
                .resolveCommits(eq("v2.0.0^..v2.0.0"), any(), any(), anyList());
        }

        @Test
        void shouldIgnoreCommitWithoutTypeAndWarn() {
            TestUtils.setupConfig(".changelog.gitwit");
            GitWitConfig config = GitWitConfig.load();

            List<RevCommit> commit = List.of(
                CommitMockFactory.mockCommit("a1", ".")
            );

            when(gitRepositoryService.resolveCommits(anyString(), any(), any(), anyList()))
                .thenReturn(commit);

            service.handle("HEAD", new ChangelogOptions(), config);

            verify(messageService)
                .warn(eq("changelog.warn.commit_no_type"), anyString());
        }
    }
}
