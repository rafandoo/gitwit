package dev.rafandoo.gitwit.provider;

import picocli.CommandLine.IVersionProvider;

/**
 * Provides version information for GitWit.
 * <p>
 * This implementation retrieves version details from the package metadata,
 * including the implementation title, version, and vendor. If no version
 * is available, it defaults to "dev-snapshot".
 */
public final class ManifestVersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getPackage().getImplementationTitle())
            .append(" - ");

        sb.append(
            getClass().getPackage().getImplementationVersion() != null
                ? getClass().getPackage().getImplementationVersion() : "dev‑snapshot"
        ).append("\n");

        sb.append("Copyright (c) 2025-present, ")
            .append(getClass().getPackage().getImplementationVendor())
            .append("\n");
        return new String[]{
            sb.toString()
        };
    }
}
