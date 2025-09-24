package dev.rafandoo.gitwit.config;

import br.dev.rplus.cup.config.Config;
import br.dev.rplus.cup.config.ConfigLoader;
import br.dev.rplus.cup.config.source.YamlConfigSource;
import dev.rafandoo.gitwit.enums.ConfigPaths;
import dev.rafandoo.gitwit.enums.ExceptionMessage;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.service.GitService;
import dev.rafandoo.gitwit.service.MessageService;
import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
 import java.util.Locale;
import java.util.Map;

/**
 * POJO that mirrors the YAML configuration used by the commit tool.
 * It is deserialised via {@link Config#as(Class)}.
 */
@Data
public class GitWitConfig {

    private TypesConfig types = new TypesConfig();
    private ScopeConfig scope = new ScopeConfig();
    private ShortDescriptionConfig shortDescription = new ShortDescriptionConfig();
    private LongDescriptionConfig longDescription = new LongDescriptionConfig();
    private BreakingChangesConfig breakingChanges = new BreakingChangesConfig();
    private ChangelogConfig changelog = new ChangelogConfig();

    /**
     * Configuration for commit types.
     */
    @Data
    public static class TypesConfig {

        /**
         * General description of what the commit type selection is for.
         */
        private String description;

        /**
         * Mapping of commit type identifiers to their human-readable descriptions.
         */
        private Map<String, String> values;
    }

    /**
     * Configuration for the scope field.
     */
    @Data
    public static class ScopeConfig {

        /**
         * Optional description to explain what the scope field represents.
         */
        private String description;

        /**
         * Indicates whether the scope is mandatory or optional.
         */
        private boolean required;

        /**
         * Defines the input type: "text" (free-form) or "list" (predefined values).
         * Defaults to "text".
         */
        private String type = "text";

        /**
         * List of allowed scope values, only used when type is "list".
         */
        private List<String> values;
    }

    /**
     * Configuration for the short description field.
     */
    @Data
    public static class ShortDescriptionConfig {

        /**
         * Optional description to guide the user when filling the short description.
         */
        private String description;

        /**
         * Minimum number of characters required.
         */
        private int minLength = 1;

        /**
         * Maximum number of characters allowed (conventionally 72).
         */
        private int maxLength = 72;
    }

    /**
     * Configuration for the long description field.
     */
    @Data
    public static class LongDescriptionConfig {

        /**
         * Enables or disables the long description field.
         */
        private boolean enabled;

        /**
         * Optional help message to guide the user when writing the long description.
         */
        private String description;

        /**
         * Indicates whether the long description is required.
         */
        private boolean required;

        /**
         * Minimum number of characters allowed (default is 0).
         */
        private int minLength = 0;

        /**
         * Maximum number of characters allowed.
         */
        private int maxLength = 100;
    }

    /**
     * Configuration the changelog.
     */
    @Data
    public static class ChangelogConfig {

        /**
         * The title displayed at the top of the changelog document.
         * Defaults to "Changelog".
         */
        private String title = "Changelog";

        /**
         * A map of commit types and their section titles in the changelog.
         * Key = commit type (e.g., "feat"), Value = section title (e.g., "New features").
         */
        private Map<String, String> types;

        /**
         * Whether to show types not listed in the `types` section under a generic "Other Changes" section.
         */
        private boolean showOtherTypes = true;

        /**
         * Whether to include a separate section for breaking changes.
         */
        private boolean showBreakingChanges = false;

        /**
         * List of commit types to ignore when generating the changelog.
         */
        private List<String> ignored;

        /**
         * Formatting preferences for rendering each commit entry.
         */
        private ChangelogFormat format = new ChangelogFormat();

        /**
         * Configuration for formatting commit entries in the changelog.
         */
        @Data
        public static class ChangelogFormat {

            /**
             * Template for the main section of the changelog.
             */
            private String sectionTemplate;

            /**
             * Template for the breaking changes section.
             */
            private String breakingChangesTemplate;

            /**
             * Template for other types of changes not covered by the main sections.
             */
            private String otherTypesTemplate;

            /**
             * Default template for formatting commit messages in the changelog.
             */
            private String defaultTemplate = "{scope}: {description} ({shortHash})";
        }
    }

    /**
     * Configuration for breaking changes.
     */
    @Data
    public static class BreakingChangesConfig {

        /**
         * Enables the breaking change prompt in the wizard.
         */
        private boolean enabled;

        /**
         * Optional description to explain what counts as a breaking change.
         */
        private String description;
    }

    /**
     * Loads YAML file defined by {@link ConfigPaths#CONFIG_FILE}.
     *
     * @return {@link GitWitConfig} instance.
     */
    public static GitWitConfig load() {
        Path repo = GitService.getInstance().getRepo();
        MessageService.getInstance().debug("config.loading", repo);
        Path configPath = repo.resolve(ConfigPaths.CONFIG_FILE.get().asString());

        if (!Files.exists(configPath)) {
            throw new GitWitException(ExceptionMessage.CONFIG_FILE_NOT_FOUND);
        }

        Config config = ConfigLoader.from(configPath, new YamlConfigSource());
        return config.as(GitWitConfig.class);
    }

    /**
     * Generates an example configuration file in the repository if it does not already exist.
     * Copies the default example configuration from resources to the repository's config path.
     * Warns if a configuration file already exists and skips generation.
     */
    public static void generateExample() {
        String lang = Locale.getDefault().toString();
        String fallback = "en_US";

        Path repo = GitService.getInstance().getRepo();
        Path configPath = repo.resolve(ConfigPaths.CONFIG_FILE.get().asString());

        if (Files.exists(configPath)) {
            MessageService.getInstance().warn("error.config_already_exists");
            return;
        }

        String resourcePattern = ConfigPaths.EXAMPLE_CONFIG_FILE.get().asString();
        String langResource = String.format(resourcePattern, lang);
        String fallbackResource = String.format(resourcePattern, fallback);

        InputStream is = GitWitConfig.class
            .getClassLoader()
            .getResourceAsStream(langResource);
        if (is == null) {
            is = GitWitConfig.class
                .getClassLoader()
                .getResourceAsStream(fallbackResource);
            if (is == null) {
                throw new GitWitException(ExceptionMessage.CONFIG_EXAMPLE_NOT_FOUND);
            }
        }

        try (InputStream stream = is) {
            Files.copy(stream, configPath, StandardCopyOption.REPLACE_EXISTING);
            MessageService.getInstance().success("success.config_example_generated", configPath);
        } catch (IOException e) {
            throw new GitWitException(ExceptionMessage.CONFIG_EXAMPLE_COPY_FAILED, e);
        }
    }
}
