package br.dev.rplus.util;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@UtilityClass
public final class EncodingUtil {

    /**
     * Retrieves the current Windows code page encoding.
     *
     * @return a string representing the Windows code page number, defaulting to "1252" if retrieval fails.
     */
    public static String getWindowsEncoding() {
        try {
            Process process = new ProcessBuilder("cmd.exe", "/c", "chcp").start();
            process.waitFor();
            String output = new String(
                process.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
            ).trim();
            if (output.matches(".*\\d+.*")) {
                return output.replaceAll("\\D", "");
            }
        } catch (IOException | InterruptedException ignored) {
        }
        return "1252";
    }

    public void setSystemEncoding(String encoding) {
        Charset charset = Charset.forName(encoding);

        System.setOut(new PrintStream(System.out, true, charset));
        System.setErr(new PrintStream(System.err, true, charset));
    }

}
