package com.bankrolldiscipline.web.dui;

import java.util.Map;
import java.util.Set;

/** Whitelist of components that the browser renderer is allowed to render. */
public final class DuiComponentRegistry {

  private static final Set<String> ALLOWED_TYPES = Set.of(
      "landingHero",
      "section",
      "metricCard",
      "warningBlock",
      "profileForm",
      "planForm",
      "activityForm",
      "dashboardSummary",
      "progressBar",
      "timeline",
      "statStrip",
      "textBlock",
      "fallback");

  private static final Map<String, Set<String>> ALLOWED_PROPS =
      Map.ofEntries(
          Map.entry("landingHero", Set.of("title", "body")),
          Map.entry("section", Set.of("title", "body")),
          Map.entry("metricCard", Set.of("title", "body")),
          Map.entry("warningBlock", Set.of("title", "body", "level")),
          Map.entry("profileForm", Set.of()),
          Map.entry("planForm", Set.of()),
          Map.entry("activityForm", Set.of()),
          Map.entry(
              "dashboardSummary",
              Set.of("title", "period", "status", "limit", "spent", "remaining")),
          Map.entry("progressBar", Set.of("label", "value", "level")),
          Map.entry("timeline", Set.of("title", "items")),
          Map.entry("statStrip", Set.of("title", "items")),
          Map.entry("textBlock", Set.of("body")),
          Map.entry("fallback", Set.of("title", "body")));

  private static final Set<String> FORBIDDEN_PROPS =
      Set.of("chainOfThought", "reasoning", "trace", "toolTrace", "internalPrompt");

  private static final Set<String> ALLOWED_LEVELS = Set.of("safe", "warning", "danger",
      "neutral");

  private DuiComponentRegistry() {
  }

  public static boolean isAllowed(String type) {
    return ALLOWED_TYPES.contains(type);
  }

  public static boolean isAllowedProperty(String type, String property) {
    return ALLOWED_PROPS.getOrDefault(type, Set.of()).contains(property)
        && !FORBIDDEN_PROPS.contains(property);
  }

  public static boolean isAllowedLevel(String level) {
    return ALLOWED_LEVELS.contains(level);
  }

  public static Set<String> allowedTypes() {
    return ALLOWED_TYPES;
  }
}
