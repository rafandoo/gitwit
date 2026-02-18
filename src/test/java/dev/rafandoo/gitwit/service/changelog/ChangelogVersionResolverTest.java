package dev.rafandoo.gitwit.service.changelog;

import dev.rafandoo.gitwit.cli.dto.ChangelogOptions;
import dev.rafandoo.gitwit.service.MessageService;
import dev.rafandoo.gitwit.service.git.GitRepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChangelogVersionResolver test")
class ChangelogVersionResolverTest {

    @Mock
    MessageService messageService;

    @Mock
    GitRepositoryService gitRepositoryService;

    ChangelogVersionResolver resolver;

    @BeforeEach
    void setup() {
        this.resolver = new ChangelogVersionResolver(
            this.gitRepositoryService,
            this.messageService
        );
    }

    @BeforeEach
    void resetMocks() {
        reset(this.gitRepositoryService);
        clearInvocations(this.gitRepositoryService);
    }

    @Test
    void shouldReturnSubtitleIfProvided() {
        ChangelogOptions options = new ChangelogOptions(
            null,
            null,
            false,
            "Custom Subtitle",
            false,
            false,
            false,
            null,
            null
        );

        String result = this.resolver.resolveSubtitle(options);
        assertThat(result).isEqualTo("Custom Subtitle");
    }

    @Test
    void shouldReturnNullIfNoSubtitleOrVersion() {
        ChangelogOptions options = new ChangelogOptions(
            null,
            null,
            false,
            null,
            false,
            false,
            false,
            new ChangelogOptions.TagOptions(),
            new ChangelogOptions.VersionOptions()
        );

        String result = this.resolver.resolveSubtitle(options);
        assertThat(result).isNull();
    }

    @Test
    void shouldReturnDefaultVersionForMajorBump() {
        ChangelogOptions options = new ChangelogOptions(
            null,
            null,
            false,
            null,
            false,
            false,
            false,
            new ChangelogOptions.TagOptions(),
            new ChangelogOptions.VersionOptions(true, false, false)
        );

        String result = this.resolver.resolveSubtitle(options);
        assertThat(result).isEqualTo("v1.0.0");
    }

    @Test
    void shouldReturnDefaultVersionForMinorBump() {
        ChangelogOptions options = new ChangelogOptions(
            null,
            null,
            false,
            null,
            false,
            false,
            false,
            new ChangelogOptions.TagOptions(),
            new ChangelogOptions.VersionOptions(false, true, false)
        );

        String result = this.resolver.resolveSubtitle(options);
        assertThat(result).isEqualTo("v0.1.0");
    }

    @Test
    void shouldReturnDefaultVersionForPatchBump() {
        ChangelogOptions options = new ChangelogOptions(
            null,
            null,
            false,
            null,
            false,
            false,
            false,
            new ChangelogOptions.TagOptions(),
            new ChangelogOptions.VersionOptions(false, false, true)
        );

        String result = this.resolver.resolveSubtitle(options);
        assertThat(result).isEqualTo("v0.0.1");
    }

    @Test
    void shouldReturnLatestTagWhenLastTagOptionIsSet() {
        when(this.gitRepositoryService.getLatestTag()).thenReturn("v2.3.4");

        ChangelogOptions options = new ChangelogOptions(
            null,
            null,
            false,
            null,
            false,
            false,
            false,
            new ChangelogOptions.TagOptions(true, null),
            new ChangelogOptions.VersionOptions()
        );

        String result = this.resolver.resolveSubtitle(options);
        assertThat(result).isEqualTo("v2.3.4");
        verify(this.gitRepositoryService).getLatestTag();
    }

    @Test
    void shouldReturnForTagValueWhenForTagOptionIsSet() {
        ChangelogOptions options = new ChangelogOptions(
            null,
            null,
            false,
            null,
            false,
            false,
            false,
            new ChangelogOptions.TagOptions(false, "v1.2.3"),
            new ChangelogOptions.VersionOptions()
        );

        String result = this.resolver.resolveSubtitle(options);
        assertThat(result).isEqualTo("v1.2.3");
        verify(this.gitRepositoryService, never()).getLatestTag();
    }

    @Test
    void shouldApplyBumpMajorToResolvedVersion() {
        when(this.gitRepositoryService.getLatestTag()).thenReturn("v1.2.3");

        ChangelogOptions options = new ChangelogOptions(
            null,
            null,
            false,
            null,
            false,
            false,
            false,
            new ChangelogOptions.TagOptions(),
            new ChangelogOptions.VersionOptions(true, false, false)
        );

        String result = this.resolver.resolveSubtitle(options);
        assertThat(result).isEqualTo("v2.0.0");
        verify(this.gitRepositoryService).getLatestTag();
    }

    @Test
    void shouldApplyBumpMinorToResolvedVersion() {
        when(this.gitRepositoryService.getLatestTag()).thenReturn("v1.2.3");

        ChangelogOptions options = new ChangelogOptions(
            null,
            null,
            false,
            null,
            false,
            false,
            false,
            new ChangelogOptions.TagOptions(),
            new ChangelogOptions.VersionOptions(false, true, false)
        );

        String result = this.resolver.resolveSubtitle(options);
        assertThat(result).isEqualTo("v1.3.0");
        verify(this.gitRepositoryService).getLatestTag();
    }

    @Test
    void shouldApplyBumpPatchToResolvedVersion() {
        when(this.gitRepositoryService.getLatestTag()).thenReturn("v1.2.3");

        ChangelogOptions options = new ChangelogOptions(
            null,
            null,
            false,
            null,
            false,
            false,
            false,
            new ChangelogOptions.TagOptions(),
            new ChangelogOptions.VersionOptions(false, false, true)
        );

        String result = this.resolver.resolveSubtitle(options);
        assertThat(result).isEqualTo("v1.2.4");
        verify(this.gitRepositoryService).getLatestTag();
    }

    @Test
    void shouldReturnResolvedVersionWithoutBump() {
        when(this.gitRepositoryService.getLatestTag()).thenReturn("v1.2.3");

        ChangelogOptions options = new ChangelogOptions(
            null,
            null,
            false,
            null,
            false,
            false,
            false,
            new ChangelogOptions.TagOptions(),
            new ChangelogOptions.VersionOptions()
        );

        String result = this.resolver.resolveSubtitle(options);
        assertThat(result).isEqualTo("v1.2.3");
        verify(this.gitRepositoryService).getLatestTag();
    }

    @Test
    void shouldReturnVersionWhenParseFails() {
        when(this.gitRepositoryService.getLatestTag()).thenReturn("invalid-version");

        ChangelogOptions options = new ChangelogOptions(
            null,
            null,
            false,
            null,
            false,
            false,
            false,
            new ChangelogOptions.TagOptions(),
            new ChangelogOptions.VersionOptions(true, false, false)
        );

        String result = this.resolver.resolveSubtitle(options);
        assertThat(result).isEqualTo("invalid-version");
        verify(this.gitRepositoryService).getLatestTag();
        verify(this.messageService).warn("changelog.warn.invalid-semver", "invalid-version");
    }

}
