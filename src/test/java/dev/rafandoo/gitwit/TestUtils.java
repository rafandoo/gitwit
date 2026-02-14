package dev.rafandoo.gitwit;

import dev.rafandoo.gitwit.config.GitWitConfig;
import lombok.experimental.UtilityClass;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@UtilityClass
public final class TestUtils {

    public static void setupConfig(String resourceName) {
        var resource = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        assertNotNull(resource, "Config not found: " + resourceName);
        System.setProperty("gitwit.config", resource.getFile());
    }

    public static GitWitConfig loadDefaultConfig() {
        setupConfig(".general.gitwit");
        return GitWitConfig.load();
    }

    public static void initTempGitRepo(Path tempDir) throws GitAPIException {
        Git git = Git.init().setDirectory(tempDir.toFile()).call();
        git.close();
    }

    public static int executeCommand(String[] args) {
        return App.execute(args);
    }
}
