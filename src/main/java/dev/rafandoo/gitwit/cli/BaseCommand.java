package dev.rafandoo.gitwit.cli;

import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.gitwit.provider.ManifestVersionProvider;
import dev.rafandoo.gitwit.service.MessageService;
import picocli.CommandLine;

/**
 * Base class for all commands.
 * <p>
 * This class provides a default implementation of the {@link Runnable} interface
 * that prints the command's usage information.
 */
@CommandLine.Command(
    mixinStandardHelpOptions = true,
    versionProvider = ManifestVersionProvider.class
)
public abstract class BaseCommand implements Runnable {

    /**
     * Loads the GitWit configuration from the default configuration source.
     *
     * @return the loaded {@link GitWitConfig} configuration instance.
     */
    protected GitWitConfig loadConfig() {
        GitWitConfig config = GitWitConfig.load();
        MessageService.getInstance().debug("config.loaded", config);
        return config;
    }

    @Override
    public void run() {
        CommandLine cmd = new CommandLine(this);
        cmd.usage(System.out);
    }
}
