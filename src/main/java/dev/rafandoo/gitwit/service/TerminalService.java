package dev.rafandoo.gitwit.service;

import com.google.inject.Singleton;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.util.EnvironmentUtil;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Class responsible for managing the terminal, providing access to the terminal instance.
 */
@Singleton
public final class TerminalService {

    private Terminal terminal;

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
                if (EnvironmentUtil.isTesting()) {
                    terminal = TerminalBuilder.builder()
                        .name("GitWit Test Terminal")
                        .dumb(true)
                        .build();
                } else if (EnvironmentUtil.isDevelopment()) {
                    terminal = TerminalBuilder.builder()
                        .name("GitWit Dev Terminal")
                        .dumb(true)
                        .build();
                } else {
                    terminal = TerminalBuilder.builder()
                        .name("GitWit Terminal")
                        .color(true)
                        .system(true)
                        .dumb(false)
                        .encoding(StandardCharsets.UTF_8)
                        .nativeSignals(false)
                        .ffm(false)
                        .jansi(false)
                        .jna(false)
                        .build();
                }
            } catch (IOException e) {
                throw new GitWitException("terminal.error.create", e);
            }
        }
        return terminal;
    }

    /**
     * Retrieves detailed information about the terminal.
     *
     * @return a string containing terminal type, dimensions, encoding, and OS information.
     */
    public String getTerminalInfo() {
        Terminal terminal = this.getTerminal();

        StringBuilder sb = new StringBuilder();
        sb.append("Terminal Information:\n");
        sb.append("Type: ").append(terminal.getType()).append("\n");
        sb.append("Ansi Supported: ").append(terminal.getType().contains("ansi")).append("\n");
        sb.append("Width: ").append(terminal.getWidth()).append("\n");
        sb.append("Height: ").append(terminal.getHeight()).append("\n");
        sb.append("Encoding: ").append(terminal.encoding().displayName()).append("\n");
        sb.append("OS: ").append(System.getProperty("os.name")).append("\n");

        return sb.toString();
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
                throw new GitWitException("terminal.error.finish", e);
            }
        }
    }
}
