package dev.rafandoo.gitwit.cli.help;

import java.util.*;

/**
 * A {@link ResourceBundle} implementation that supports hierarchical lookup.
 * <p>
 * This bundle delegates key resolution to multiple underlying resource bundles,
 * returning the first match found in declaration order. It enables fallback
 * behavior, allowing command-specific bundles to override values defined in
 * a shared base bundle.
 */
public class HierarchicalResourceBundle extends ResourceBundle {

    private final List<ResourceBundle> bundles;

    /**
     * Creates a hierarchical resource bundle using the given bundles in lookup order.
     *
     * @param bundles the resource bundles to search, in priority order.
     */
    public HierarchicalResourceBundle(ResourceBundle... bundles) {
        this.bundles = Arrays.asList(bundles);
    }

    @Override
    protected Object handleGetObject(String key) {
        for (ResourceBundle bundle : bundles) {
            if (bundle.containsKey(key)) {
                return bundle.getObject(key);
            }
        }
        return null;
    }

    @Override
    public Enumeration<String> getKeys() {
        Set<String> keys = new LinkedHashSet<>();
        bundles.forEach(b -> keys.addAll(b.keySet()));
        return Collections.enumeration(keys);
    }
}
