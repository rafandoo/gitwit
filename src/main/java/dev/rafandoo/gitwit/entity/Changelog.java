package dev.rafandoo.gitwit.entity;

import java.util.List;
import java.util.Map;

/**
 * Immutable domain model representing a generated changelog.
 * <p>
 * This record holds structured, format-agnostic changelog data produced
 * from Git commit history and later rendered into an output format
 * (e.g. Markdown).
 *
 * @param title           optional main title of the changelog.
 * @param subtitle        optional secondary title, such as a version or release name.
 * @param breakingChanges list of formatted breaking change entries.
 * @param sections        map of section titles to their corresponding changelog entries.
 *                        the insertion order of the map should be preserved.
 * @param otherChanges    list of formatted entries that do not fit into any configured section.
 */
public record Changelog(
    String title,
    String subtitle,
    List<String> breakingChanges,
    Map<String, List<String>> sections,
    List<String> otherChanges
) {
}
