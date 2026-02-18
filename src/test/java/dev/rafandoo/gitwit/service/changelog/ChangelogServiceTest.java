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

    @Test
    void handle_shouldGenerateRenderAndOutputChangelog() {
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

        when(this.gitRepositoryService.resolveCommits(eq("HEAD"), any(), any(), anyList()))
            .thenReturn(new ArrayList<>(commits));

        when(this.renderer.render(any(Changelog.class), eq(false)))
            .thenReturn("rendered");

        this.service.handle(
            "HEAD",
            options,
            config
        );

        verify(this.renderer).render(any(Changelog.class), eq(false));
        verify(this.outputService).output("rendered", false, false, config, false);
        verify(this.messageService).success("changelog.generated");
    }

    @Test
    void handle_shouldDoNothingWhenNoCommits() {
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

        when(this.gitRepositoryService.resolveCommits(eq("HEAD"), any(), any(), anyList()))
            .thenReturn(new ArrayList<>());

        this.service.handle(
            "HEAD",
            options,
            config
        );

        verify(messageService).warn("changelog.warn.no_commits");
        verifyNoInteractions(this.renderer, this.outputService);
    }

    @Test
    void handle_shouldThrowWhenNoTypesConfigured() {
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
            this.service.handle(
                "",
                options,
                config
            )
        )
            .isInstanceOf(GitWitException.class)
            .hasMessage(this.i18nService.getMessage("changelog.error.types_required"));
        verifyNoInteractions(this.renderer, this.outputService);
    }

    @Test
    void generate_shouldReturnNullWhenGroupedIsEmpty() {
        TestUtils.setupConfig(".changelog.gitwit");
        GitWitConfig config = GitWitConfig.load();

        Changelog result = this.service.generate(
            config,
            Map.of(),
            Map.of("feat", "Features"),
            null
        );

        assertThat(result).isNull();
        verify(this.messageService).warn("changelog.warn.no_commits");
    }

    @Test
    void generate_shouldExtractBreakingChanges() {
        TestUtils.setupConfig(".changelog.gitwit");
        GitWitConfig config = GitWitConfig.load();

        CommitMessage breaking = CommitMessage.of(
            CommitMockFactory.mockCommit("a1", "feat!: breaking change")
        );

        Map<String, List<CommitMessage>> grouped = new HashMap<>();
        grouped.put("feat", new ArrayList<>(List.of(breaking)));

        Changelog changelog = this.service.generate(
            config,
            grouped,
            Map.of("feat", "Features"),
            null
        );

        assertThat(changelog).isNotNull();
        assertThat(changelog.breakingChanges().isEmpty()).isFalse();
    }

    @Test
    void getTemplate_shouldFallbackToDefault() {
        GitWitConfig.ChangelogConfig.ChangelogFormat format =
            new GitWitConfig.ChangelogConfig.ChangelogFormat();

        format.setDefaultTemplate("- %s");

        String template = this.service.getChangelogCommitTemplateByScope(
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
            this.service.getChangelogCommitTemplateByScope(format, ChangelogScope.SECTION)
        )
            .isInstanceOf(GitWitException.class)
            .hasMessage(this.i18nService.getMessage("changelog.error.no_template"));
    }
}
