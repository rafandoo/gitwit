package dev.rafandoo.gitwit.cli;

import com.google.inject.Inject;
import dev.rafandoo.gitwit.cli.help.GlobalHelpFactory;
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

    @Inject
    protected MessageService messageService;

    /**
     * Loads the GitWit configuration from the default configuration source.
     *
     * @return the loaded {@link GitWitConfig} configuration instance.
     */
    protected GitWitConfig loadConfig() {
        GitWitConfig config = GitWitConfig.load();
        this.messageService.debug("config.loaded", config);
        return config;
    }

    @Override
    public void run() {
        CommandLine cmd = new CommandLine(this);
        cmd.setHelpFactory(new GlobalHelpFactory());
        cmd.usage(System.out);
    }
}
