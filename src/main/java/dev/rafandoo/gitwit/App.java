package dev.rafandoo.gitwit;

import com.google.inject.Injector;
import dev.rafandoo.gitwit.cli.*;
import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.cup.os.OperatingSystem;
import dev.rafandoo.gitwit.di.InjectorFactory;
import dev.rafandoo.gitwit.di.GuiceFactory;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.service.MessageService;
import dev.rafandoo.gitwit.service.TerminalService;
import dev.rafandoo.gitwit.util.EncodingUtil;
import dev.rafandoo.gitwit.util.EnvironmentUtil;
import lombok.Getter;
import picocli.CommandLine;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main command for the GitWit CLI application, providing subcommands for install, lint, and hooks.
 */
@CommandLine.Command(
    name = "gitwit",
    subcommands = {
        Install.class,
        Uninstall.class,
        Commit.class,
        Hook.class,
        Lint.class,
        Changelog.class,
    },
    resourceBundle = "i18n.commands.app",
    sortOptions = false
)
public class App extends BaseCommand {

    @Getter
    @CommandLine.Option(
        names = {"-d", "--debug"},
        descriptionKey = "app.option.debug"
    )
    private static boolean debug = false;

    @CommandLine.Option(
        names = {"-ce", "--config-example"},
        descriptionKey = "app.option.config-example"
    )
    private boolean configExample = false;

    /**
     * Private constructor to prevent instantiation.
     */
    public App() {
    }

    @Override
    public void run() {
        if (this.configExample) {
            GitWitConfig.generateExample();
            return;
        }
        super.run();
    }

    /**
     * Returns the file system path of the application's location.
     *
     * @return a {@link Path} representing the location of the running JAR or classpath root.
     * @throws GitWitException if there is an error converting the code source location to a URI.
     */
    public static Path getApplicationPath() {
        try {
            return Paths.get(
                App.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
            );
        } catch (URISyntaxException e) {
            throw new GitWitException("error.app_dir", e.getMessage());
        }
    }

    /**
     * Checks if the application is running from a JAR file.
     *
     * @return {@code true} if running from a JAR file, {@code false} otherwise.
     */
    public static boolean isRunningFromJar() {
        String cmd = System.getProperty("sun.java.command", "");
        return cmd.contains(".jar");
    }

    /**
     * Main entry point for the GitWit CLI application.
     *
     * @param args command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        int ec = execute(args);
        if (EnvironmentUtil.isTesting()) {
            return;
        }
        System.exit(ec);
    }

    /**
     * Executes the GitWit application with the provided command-line arguments.
     * Configures debug logging if the debug option is enabled and sets the system encoding
     * based on the operating system.
     *
     * @param args command-line arguments to be processed.
     * @return the exit code of the command execution.
     */
    public static int execute(String[] args) {
        Injector injector = InjectorFactory.get();
        CommandLine.IFactory factory = new GuiceFactory(injector);

        if (OperatingSystem.isWindows()) {
            EncodingUtil.setSystemEncoding(EncodingUtil.getWindowsEncoding());
        } else {
            EncodingUtil.setSystemEncoding(Charset.defaultCharset().displayName());
        }

        int ec;
        try {
            CommandLine cmd = getCommandLine(factory, injector);
            ec = cmd.execute(args);
        } finally {
            injector.getInstance(TerminalService.class).close();
        }
        return ec;
    }

    /**
     * Creates and configures the CommandLine instance for the application.
     * Sets up an execution strategy that initializes the root command and
     * configures debug logging if the debug option is enabled.
     *
     * @param factory  the {@link CommandLine.IFactory} to create command instances.
     * @param injector the Guice {@link Injector} for dependency injection.
     * @return a configured {@link CommandLine} instance.
     */
    private static CommandLine getCommandLine(CommandLine.IFactory factory, Injector injector) {
        CommandLine cmd = new CommandLine(App.class, factory);

        cmd.setExecutionStrategy(parseResult -> {
            // força a criação do comando root para que as options (ex: --debug) sejam aplicadas
            parseResult.commandSpec()
                .root()
                .userObject();

            if (App.isDebug()) {
                configureDebugLogging();

                TerminalService terminalService = injector.getInstance(TerminalService.class);
                MessageService messageService = injector.getInstance(MessageService.class);

                messageService.debug(terminalService.getTerminalInfo());
            }

            return new CommandLine.RunLast().execute(parseResult);
        });
        return cmd;
    }

    /**
     * Configures logging to display fine-grained debug information for the JLine library.
     * Sets up a console handler with FINE log level and applies it to the JLine logger.
     */
    private static void configureDebugLogging() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);

        Logger logger = Logger.getLogger("org.jline");
        logger.setLevel(Level.FINE);
        logger.addHandler(handler);
    }
}
