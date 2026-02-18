package dev.rafandoo.gitwit.cli.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import picocli.CommandLine;

/**
 * DTO for changelog command options.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangelogOptions {

    @CommandLine.Option(
        names = {"-f", "--from"},
        hidden = true
    )
    @Deprecated(forRemoval = true, since = "1.1.0")
    private String from;

    @CommandLine.Option(
        names = {"-t", "--to"},
        hidden = true
    )
    @Deprecated(forRemoval = true, since = "1.1.0")
    private String to;

    @CommandLine.Option(
        names = {"-a", "--append"},
        descriptionKey = "changelog.option.append"
    )
    private boolean append = false;

    @CommandLine.ArgGroup
    private SubtitleOptions subtitleOptions = new SubtitleOptions();

    @CommandLine.ArgGroup
    private OutputOptions outputOptions = new OutputOptions();

    @CommandLine.ArgGroup
    private TagOptions tagOptions = new TagOptions();

    @CommandLine.ArgGroup
    private VersionOptions versionOptions = new VersionOptions();

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class SubtitleOptions {

        @CommandLine.Option(
            names = {"-s", "--subtitle"},
            descriptionKey = "changelog.option.subtitle"
        )
        private String subtitle;

        @CommandLine.Option(
            names = {"-n", "--no-subtitle"},
            descriptionKey = "changelog.option.no-subtitle"
        )
        private boolean noSubtitle = false;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class OutputOptions {

        @CommandLine.Option(
            names = {"-c", "--copy"},
            descriptionKey = "changelog.option.copy"
        )
        private boolean copyToClipboard;

        @CommandLine.Option(
            names = {"-S", "--stdout"},
            descriptionKey = "changelog.option.stdout"
        )
        private boolean stdout = false;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class TagOptions {

        @CommandLine.Option(
            names = {"-l", "--last-tag"},
            descriptionKey = "changelog.option.last-tag"
        )
        private boolean lastTag;

        @CommandLine.Option(
            names = {"--for-tag"},
            descriptionKey = "changelog.option.for-tag"
        )
        private String forTag;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class VersionOptions {

        @CommandLine.Option(
            names = {"-M", "--major"},
            descriptionKey = "changelog.option.major"
        )
        private boolean major;

        @CommandLine.Option(
            names = {"-m", "--minor"},
            descriptionKey = "changelog.option.minor"
        )
        private boolean minor;

        @CommandLine.Option(
            names = {"-p", "--patch"},
            descriptionKey = "changelog.option.patch"
        )
        private boolean patch;
    }
}
