package dev.rafandoo.gitwit.util;

import dev.rafandoo.cup.os.OperatingSystem;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.service.MessageService;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Utility class providing clipboard operations across different operating systems.
 * <p>
 * Supports copying text to the system clipboard using native clipboard utilities
 * for Windows, macOS, and Linux platforms.
 */
@UtilityClass
public final class ClipboardUtil {

    /**
     * Copies the given text to the system clipboard.
     * <p>
     * Supports copying on Windows, macOS, and Linux using various clipboard utilities.
     *
     * @param text the text to be copied to the clipboard.
     * @return {@code true} if the text was successfully copied, {@code false} otherwise.
     * @throws GitWitException if clipboard copy operation fails.
     */
    public static boolean copyToClipboard(String text) {
        try {
            if (OperatingSystem.isWindows()) {
                return copyUsingProcess("clip", text);
            } else if (OperatingSystem.isMac()) {
                return copyUsingProcess("pbcopy", text);
            } else if (OperatingSystem.isLinux()) {
                if (isCommandAvailable("xclip")) {
                    return copyUsingProcess("xclip", "-selection", "clipboard", text);
                } else if (isCommandAvailable("xsel")) {
                    return copyUsingProcess("xsel", "--clipboard", "--input", text);
                } else if (isCommandAvailable("wl-copy")) {
                    return copyUsingProcess("wl-copy", text);
                } else {
                    MessageService.getInstance().warn("clipboard.warn.no_utility");
                    return false;
                }
            } else {
                MessageService.getInstance().warn("clipboard.warn.unsupported_os");
                return false;
            }
        } catch (Exception e) {
            throw new GitWitException("clipboard.error.copy", e);
        }
    }

    /**
     * Copies text to the clipboard using a system command.
     *
     * @param commandWithText variable arguments containing the command and text to copy.
     * @return {@code true} if the copy operation was successful, {@code false} otherwise.
     * @throws IOException          if an I/O error occurs while executing the command.
     * @throws InterruptedException if the command execution is interrupted.
     */
    private static boolean copyUsingProcess(String... commandWithText) throws IOException, InterruptedException {
        String[] command = Arrays.copyOf(commandWithText, commandWithText.length - 1);
        String text = commandWithText[commandWithText.length - 1];

        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();

        try (OutputStream os = process.getOutputStream()) {
            if (OperatingSystem.isWindows()) {
                Charset charset = Charset.forName(EncodingUtil.getWindowsEncoding());
                os.write(text.getBytes(charset));
                os.flush();
            } else {
                os.write(text.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }
        }

        return process.waitFor() == 0;
    }

    /**
     * Checks if a specific command is available in the system's PATH.
     *
     * @param command the name of the command to check for availability.
     * @return {@code true} if the command is found and executable, {@code false} otherwise.
     */
    private static boolean isCommandAvailable(String command) {
        try {
            Process process = new ProcessBuilder("which", command).start();
            return process.waitFor() == 0;
        } catch (Exception ignored) {
            return false;
        }
    }
}
