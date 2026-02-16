package dev.rafandoo.gitwit.service.changelog;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rafandoo.cup.utils.StringUtils;
import dev.rafandoo.gitwit.cli.dto.ChangelogOptions;
import dev.rafandoo.gitwit.service.MessageService;
import dev.rafandoo.gitwit.service.git.GitRepositoryService;
import lombok.RequiredArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves the changelog subtitle version based on CLI options and repository state.
 * <p>
 * This service determines the effective version to be used as the changelog
 * subtitle, optionally applying semantic version bumps (major, minor, patch)
 * when requested.
 * </p>
 *
 * <p>
 * Version resolution strategy:
 * <ul>
 *     <li>Uses an explicitly provided subtitle if present.</li>
 *     <li>Resolves the version from the specified tag or latest repository tag.</li>
 *     <li>Applies semantic version bump if requested.</li>
 *     <li>Falls back to default initial versions when no tag is available.</li>
 * </ul>
 * </p>
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @__({@Inject}))
public final class ChangelogVersionResolver {

    private final GitRepositoryService gitRepositoryService;
    private final MessageService messageService;

    private static final Pattern SEMVER_PATTERN = Pattern.compile(
        "^(v?)" +                        // prefix
            "(\\d+)\\.(\\d+)\\.(\\d+)" +     // major.minor.patch
            "(?:-([0-9A-Za-z.-]+))?" +       // pre-release
            "(?:\\+([0-9A-Za-z.-]+))?$"      // build
    );

    /**
     * Resolves the subtitle to be displayed in the generated changelog.
     *
     * @param options the changelog CLI options.
     * @return the resolved subtitle version, or {@code null} if none applies.
     */
    public String resolveSubtitle(ChangelogOptions options) {
        if (!StringUtils.isNullOrBlank(options.getSubtitle())) {
            return options.getSubtitle();
        }

        String version = this.resolveVersion(options);
        if (version == null) {
            if (options.getVersionOptions().isMajor()) return "v1.0.0";
            if (options.getVersionOptions().isMinor()) return "v0.1.0";
            if (options.getVersionOptions().isPatch()) return "v0.0.1";

            return null;
        }

        return this.applyBumpIfNecessary(version, options);
    }

    /**
     * Resolves the base version from tag-related options.
     *
     * @param options the changelog CLI options.
     * @return the resolved version, or {@code null} if not found.
     */
    private String resolveVersion(ChangelogOptions options) {
        if (options.getTagOptions().isLastTag()) {
            return this.gitRepositoryService.getLatestTag();
        }

        if (!StringUtils.isNullOrBlank(options.getTagOptions().getForTag())) {
            return options.getTagOptions().getForTag();
        }

        return this.gitRepositoryService.getLatestTag();
    }

    /**
     * Applies semantic version bump if requested.
     *
     * @param version the base version.
     * @param options the changelog CLI options.
     * @return the updated version after bump, or the original version if no bump is required.
     */
    private String applyBumpIfNecessary(String version, ChangelogOptions options) {
        boolean hasBump = options.getVersionOptions().isMajor() ||
            options.getVersionOptions().isMinor() ||
            options.getVersionOptions().isPatch();

        if (!hasBump) {
            return version;
        }

        SemVer semVer = this.parse(version);
        if (semVer == null) {
            this.messageService.warn("changelog.warn.invalid-semver", version);
            return version;
        }

        if (options.getVersionOptions().isMajor()) {
            semVer = semVer.bumpMajor();
        } else if (options.getVersionOptions().isMinor()) {
            semVer = semVer.bumpMinor();
        } else if (options.getVersionOptions().isPatch()) {
            semVer = semVer.bumpPatch();
        }

        return semVer.toString();
    }

    /**
     * Parses a semantic version string.
     *
     * @param version the version string.
     * @return a {@link SemVer} instance if valid, otherwise {@code null}.
     */
    private SemVer parse(String version) {
        Matcher matcher = SEMVER_PATTERN.matcher(version);

        if (!matcher.matches()) {
            return null;
        }

        return new SemVer(
            matcher.group(1),
            Integer.parseInt(matcher.group(2)),
            Integer.parseInt(matcher.group(3)),
            Integer.parseInt(matcher.group(4)),
            matcher.group(5),
            matcher.group(6)
        );
    }

    /**
     * Immutable representation of a Semantic Version (SemVer).
     * Supports major, minor and patch increments.
     */
    private record SemVer(
        String prefix,
        int major,
        int minor,
        int patch,
        String preRelease,
        String build
    ) {

        /**
         * Creates a new SemVer instance with the major version incremented by 1,
         * and minor and patch reset to 0.
         *
         * @return a new SemVer instance with the major version bumped.
         */
        SemVer bumpMajor() {
            return new SemVer(prefix, major + 1, 0, 0, null, null);
        }

        /**
         * Creates a new SemVer instance with the minor version incremented by 1,
         * and patch reset to 0.
         *
         * @return a new SemVer instance with the minor version bumped.
         */
        SemVer bumpMinor() {
            return new SemVer(prefix, major, minor + 1, 0, null, null);
        }

        /**
         * Creates a new SemVer instance with the patch version incremented by 1.
         *
         * @return a new SemVer instance with the patch version bumped.
         */
        SemVer bumpPatch() {
            return new SemVer(prefix, major, minor, patch + 1, null, null);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(prefix)
                .append(major).append(".")
                .append(minor).append(".")
                .append(patch);

            if (preRelease != null) {
                sb.append("-").append(preRelease);
            }

            if (build != null) {
                sb.append("+").append(build);
            }

            return sb.toString();
        }
    }
}
