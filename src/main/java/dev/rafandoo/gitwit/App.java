package dev.rafandoo.gitwit;

import dev.rafandoo.gitwit.cli.*;
import dev.rafandoo.gitwit.cli.*;
import dev.rafandoo.gitwit.cli.*;
import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.cup.os.OperatingSystem;
import dev.rafandoo.gitwit.enums.ExceptionMessage;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.service.TerminalService;
import dev.rafandoo.gitwit.util.EncodingUtil;
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
@SuppressWarnings("CanBeFinal")
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
    private App() {
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
            throw new GitWitException(ExceptionMessage.APP_DIRECTORY_ERROR, e.getMessage());
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
     * Initializes the application, executes the command line interface,
     * closes the terminal service, and exits with the command's exit code.
     *
     * @param args command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        App app = new App();
        if (App.isDebug()) {
            configureDebugLogging();
        }

        if (OperatingSystem.isWindows()) {
            EncodingUtil.setSystemEncoding(EncodingUtil.getWindowsEncoding());
        } else {
            EncodingUtil.setSystemEncoding(Charset.defaultCharset().displayName());
        }

        CommandLine cmd = new CommandLine(app);
        int ec = cmd.execute(args);
        TerminalService.getInstance().close();
        System.exit(ec);
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
