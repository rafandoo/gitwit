package br.dev.rplus.gitwit.service;

import br.dev.rplus.gitwit.enums.ExceptionMessage;
import br.dev.rplus.gitwit.exception.GitWitException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Class responsible for managing the terminal, providing access to the terminal instance.
 */
public final class TerminalService {

    private static TerminalService instance;
    private static Terminal terminal;

    /**
     * Private constructor to prevent instantiation.
     */
    private TerminalService() {
    }

    /**
     * Returns the singleton instance, instantiating it on first use.
     *
     * @return {@link TerminalService} instance.
     */
    public static synchronized TerminalService getInstance() {
        if (instance == null) {
            instance = new TerminalService();
        }
        return instance;
    }

    /**
     * Retrieves or creates a terminal instance with color and system support.
     * <p>
     * Creates a new terminal using JLine's TerminalBuilder with color and system
     * settings enabled. If a terminal has already been created, returns the existing
     * instance. Throws a {@link GitWitException} if terminal creation fails.
     *
     * @return a configured {@link Terminal} instance.
     * @throws GitWitException if terminal cannot be built.
     */
    public synchronized Terminal getTerminal() {
        if (terminal == null) {
            try {
                terminal = TerminalBuilder.builder()
                    .name("GitWit Terminal")
                    .color(true)
                    .system(true)
                    .dumb(false)
                    .encoding(StandardCharsets.UTF_8)
                    .nativeSignals(true)
                    .build();

                MessageService.getInstance().debug("Terminal type: %s", terminal.getType());
                MessageService.getInstance().debug("Ansi supported: %s", terminal.getType().contains("ansi"));
                MessageService.getInstance().debug("Width: %s", terminal.getWidth());
                MessageService.getInstance().debug("Height: %s", terminal.getHeight());
                MessageService.getInstance().debug("Encoding: %s", terminal.encoding().displayName());
            } catch (IOException e) {
                throw new GitWitException(ExceptionMessage.TERMINAL_CREATION_ERROR, e);
            }
        }
        return terminal;
    }

    /**
     * Closes the {@link #terminal} if it is not null, handling any potential IOException.
     *
     * @throws GitWitException if an error occurs while closing the terminal.
     */
    public void close() {
        if (terminal != null) {
            try {
                terminal.close();
            } catch (IOException e) {
                throw new GitWitException(ExceptionMessage.TERMINAL_FINISH_ERROR, e);
            }
        }
    }
}
