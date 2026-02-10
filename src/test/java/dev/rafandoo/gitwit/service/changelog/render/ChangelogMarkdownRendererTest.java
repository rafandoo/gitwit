package dev.rafandoo.gitwit.service.changelog.render;

import dev.rafandoo.gitwit.entity.Changelog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ChangelogMarkdownRenderer Tests")
class ChangelogMarkdownRendererTest {

    ChangelogMarkdownRenderer renderer;

    @BeforeEach
    void setup() {
        this.renderer = new ChangelogMarkdownRenderer();
    }

    @Test
    void shouldRenderFullChangelogWhenAppendIsFalse() {
        Changelog changelog = new Changelog(
            "🚀 Release v1.0.0",
            "Highlights",
            List.of("API breaking change"),
            Map.of(
                "Features", List.of("New login flow", "Dark mode"),
                "Fixes", List.of("Crash on startup")
            ),
            List.of("Minor refactoring")
        );

        String result = this.renderer.render(changelog, false);

        assertThat(result).contains("# 🚀 Release v1.0.0");
        assertThat(result).contains("## Highlights");

        assertThat(result).contains("### Breaking Changes");
        assertThat(result).contains("- API breaking change");

        assertThat(result).contains("### Features");
        assertThat(result).contains("- New login flow");
        assertThat(result).contains("- Dark mode");

        assertThat(result).contains("### Fixes");
        assertThat(result).contains("- Crash on startup");

        assertThat(result).contains("### Other");
        assertThat(result).contains("- Minor refactoring");
    }

    @Test
    void shouldNotRenderTitleWhenAppendIsTrue() {
        Changelog changelog = new Changelog(
            "Release v1.0.0",
            null,
            List.of(),
            Map.of(),
            List.of()
        );

        String result = this.renderer.render(changelog, true);

        assertThat(result).doesNotContain("# Release v1.0.0");
    }

    @Test
    void shouldRenderOnlySubtitleWhenTitleIsBlank() {
        Changelog changelog = new Changelog(
            " ",
            "Only subtitle",
            List.of(),
            Map.of(),
            List.of()
        );

        String result = this.renderer.render(changelog, false);

        assertThat(result).contains("## Only subtitle");
    }

    @Test
    void shouldSkipEmptySectionsGracefully() {
        Changelog changelog = new Changelog(
            "Release",
            null,
            List.of(),
            Map.of(),
            List.of()
        );

        String result = this.renderer.render(changelog, false);

        assertThat(result).contains("# Release");
        assertThat(result).doesNotContain("Breaking Changes");
        assertThat(result).doesNotContain("Other");
    }
}
