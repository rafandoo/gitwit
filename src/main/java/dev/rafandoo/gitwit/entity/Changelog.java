package dev.rafandoo.gitwit.entity;

import java.util.List;
import java.util.Map;

public record Changelog(
    String title,
    String subtitle,
    List<String> breakingChanges,
    Map<String, List<String>> sections,
    List<String> otherChanges
) {
}
