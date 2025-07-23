package br.dev.rplus.config;

import br.dev.rplus.cup.config.Config;
import br.dev.rplus.cup.config.ConfigLoader;
import br.dev.rplus.cup.config.source.YamlConfigSource;
import br.dev.rplus.enums.ConfigPaths;
import br.dev.rplus.enums.ExceptionMessage;
import br.dev.rplus.exception.GitWitException;
import br.dev.rplus.service.GitService;
import br.dev.rplus.service.MessageService;
import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
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

    @Data
    public static class TypesConfig {
        private String description;
        private Map<String, String> values;
    }

    @Data
    public static class ScopeConfig {
        private String description;
        private boolean required;

        /**
         * Either <code>list</code> or <code>text</code>.
         */
        private String type = "text";
        private List<String> values;
    }

    @Data
    public static class ShortDescriptionConfig {
        private String description;
        private int minLength = 1;
        private int maxLength = 72;
    }

    @Data
    public static class LongDescriptionConfig {
        private boolean enabled;
        private String description;
        private boolean required;
        private int minLength = 0;
        private int maxLength = 100;
    }

    @Data
    public static class ChangelogConfig {
        private String title = "Changelog";
        private Map<String, String> types;
        private boolean showOtherTypes = true;
        private List<String> ignored;
        private ChangelogFormat format = new ChangelogFormat();

        @Data
        public static class ChangelogFormat {
            private boolean showBreakingChanges = false;
            private boolean showScope = true;
            private boolean showShortHash = true;
        }
    }

    @Data
    public static class BreakingChangesConfig {
        private boolean enabled;
        private String description;
    }

    /**
     * Loads YAML file defined by {@link ConfigPaths#CONFIG_FILE}.
     *
     * @return {@link GitWitConfig} instance.
     */
    public static GitWitConfig load() {
        Path repo = GitService.getInstance().getRepo();
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
        Path repo = GitService.getInstance().getRepo();
        Path configPath = repo.resolve(ConfigPaths.CONFIG_FILE.get().asString());

        if (Files.exists(configPath)) {
            MessageService.getInstance().warn("error.config_already_exists");
            return;
        }

        try (
            InputStream is = GitWitConfig.class.getClassLoader().getResourceAsStream(
                ConfigPaths.EXAMPLE_CONFIG_FILE.get().asString()
            )
        ) {
            assert is != null;
            Files.copy(is, configPath, StandardCopyOption.REPLACE_EXISTING);
            MessageService.getInstance().success("success.config_example_generated", configPath);
        } catch (IOException e) {
            throw new GitWitException(ExceptionMessage.CONFIG_EXAMPLE_COPY_FAILED, e);
        }
    }
}
