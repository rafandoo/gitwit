package dev.rafandoo.gitwit.cli.help;

import picocli.CommandLine;

import java.util.ResourceBundle;

/**
 * Custom {@link CommandLine.IHelpFactory} that injects hierarchical
 * resource bundle support into picocli help generation.
 * <p>
 * This factory combines a command-specific resource bundle with a shared
 * base bundle, enabling fallback resolution for common help messages
 * such as usage headings, footers, and standard options.
 */
public class GlobalHelpFactory implements CommandLine.IHelpFactory {

    @Override
    public CommandLine.Help create(CommandLine.Model.CommandSpec spec, CommandLine.Help.ColorScheme scheme) {
        String bundleName = spec.resourceBundleBaseName();

        if (bundleName != null) {
            ResourceBundle base = ResourceBundle.getBundle("i18n.commands.base");
            ResourceBundle local = ResourceBundle.getBundle(bundleName);

            spec.resourceBundle(new HierarchicalResourceBundle(local, base));
        }
        return new CommandLine.Help(spec, scheme);
    }
}
