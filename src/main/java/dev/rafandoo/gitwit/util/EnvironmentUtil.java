package dev.rafandoo.gitwit.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EnvironmentUtil {

    public static boolean isTesting() {
        return System.getProperty("GITWIT_ENV", "").equalsIgnoreCase("TEST");
    }

    public static boolean isProd() {
        return !isTesting();
    }
}
