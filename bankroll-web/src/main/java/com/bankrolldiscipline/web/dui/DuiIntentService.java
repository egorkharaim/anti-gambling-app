package com.bankrolldiscipline.web.dui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

/** Builds validated local DUI responses for the mock dashboard flow. */
@Service
public class DuiIntentService {

  private static final String VERSION = "1";
  private static final Set<String> WHITELIST =
      Set.of(
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

  private final DuiResponseValidator validator;

  public DuiIntentService(DuiResponseValidator validator) {
    this.validator = validator;
  }

  /** Builds initial page state for a server-rendered route. */
  public DuiIntentResponse initialScreen(String screen) {
    String normalizedScreen = normalize(screen);
    if ("dashboard".equals(normalizedScreen)) {
      return safe(dashboardResponse(defaultRequest(), List.of()), "landing");
    }
    if ("profile".equals(normalizedScreen)) {
      return safe(screenResponse("profile", profileWorkspace()), "landing");
    }
    if ("plans".equals(normalizedScreen)) {
      return safe(screenResponse("plans", planWorkspace()), "landing");
    }
    if ("sessions".equals(normalizedScreen)) {
      return safe(screenResponse("sessions", sessionsWorkspace()), "landing");
    }
    if ("calendar".equals(normalizedScreen)) {
      return safe(screenResponse("calendar", calendarWorkspace()), "landing");
    }
    if ("statistics".equals(normalizedScreen)) {
      return safe(screenResponse("statistics", statisticsWorkspace()), "landing");
    }
    return safe(screenResponse("landing", landingComponents()), "landing");
  }

  /** Evaluates the local mock flow and returns validated components. */
  public DuiIntentResponse evaluate(DuiIntentRequest request) {
    List<String> messages = new ArrayList<>();
    DuiIntentRequest safeRequest = request == null ? defaultRequest() : request;

    try {
      LocalAmounts amounts = parseAmounts(safeRequest);
      boolean limitIncreaseRejected =
          amounts.currentLimit != null && amounts.limit.compareTo(amounts.currentLimit) > 0;
      if (limitIncreaseRejected) {
        messages.add("Limit increase was rejected.");
      }

      DuiIntentRequest effectiveRequest =
          limitIncreaseRejected
              ? replaceLimit(safeRequest, amounts.currentLimit.toPlainString())
              : safeRequest;

      return safe(dashboardResponse(effectiveRequest, messages), "dashboard");
    } catch (RuntimeException exception) {
      messages.add("Invalid input was rejected.");
      return fallback("dashboard", messages);
    }
  }

  private DuiIntentResponse dashboardResponse(DuiIntentRequest request, List<String> messages) {
    LocalAmounts amounts = parseAmounts(request);
    BigDecimal remaining = amounts.limit.subtract(amounts.spent).max(BigDecimal.ZERO);
    BigDecimal usage =
        amounts.limit.signum() == 0
            ? BigDecimal.ZERO
            : amounts.spent
                .multiply(BigDecimal.valueOf(100))
                .divide(amounts.limit, 0, RoundingMode.DOWN)
                .min(BigDecimal.valueOf(100));
    String level = warningLevel(usage);

    List<DuiComponent> components = new ArrayList<>();
    components.add(
        component(
            "dashboardSummary",
            Map.of(
                "title", "Current bankroll plan",
                "period", request.period(),
                "status", statusText(level),
                "limit", usd(amounts.limit),
                "spent", usd(amounts.spent),
                "remaining", usd(remaining))));
    components.add(
        component(
            "progressBar",
            Map.of("label", "Limit usage", "value", usage + "%", "level", level)));
    components.add(
        component(
            "warningBlock",
            Map.of(
                "level", level,
                "title", warningTitle(level),
                "body", warningBody(level))));
    components.addAll(profileComponents());
    components.addAll(planComponents());
    components.add(component("activityForm", Map.of()));
    components.add(
        component(
            "timeline",
            Map.of(
                "title", "Today’s discipline flow",
                "items",
                List.of(
                    "Profile checked",
                    "Weekly limit active",
                    "Warning thresholds ready",
                    "No limit increase allowed"))));

    return new DuiIntentResponse(VERSION, "dashboard", WHITELIST, components, false, messages);
  }

  private List<DuiComponent> landingComponents() {
    return List.of(
        component(
            "section",
            Map.of(
                "title", "Strict bankroll control",
                "body", "Plan the next week or month, then track usage against that boundary."),
            List.of(
                component(
                    "metricCard",
                    Map.of("title", "MVP", "body", "Accounting and warnings only.")),
                component(
                    "metricCard",
                    Map.of("title", "Currency", "body", "USD for the first working slice.")))));
  }

  private List<DuiComponent> profileComponents() {
    return List.of(component("profileForm", Map.of()));
  }

  private List<DuiComponent> profileWorkspace() {
    return List.of(
        component(
            "section",
            Map.of(
                "title", "Profile inputs",
                "body", "Edit the financial profile and preview how it changes the dashboard."),
            profileComponents()),
        component(
            "statStrip",
            Map.of(
                "title", "Profile contract",
                "items",
                List.of("USD only", "Money as strings", "BigDecimal in Java", "No payments"))));
  }

  private List<DuiComponent> planComponents() {
    return List.of(component("planForm", Map.of()));
  }

  private List<DuiComponent> planWorkspace() {
    return List.of(
        component(
            "section",
            Map.of(
                "title", "Plan controls",
                "body",
                "Choose week or month, set the active limit, and test the decrease-only rule."),
            planComponents()),
        component(
            "warningBlock",
            Map.of(
                "level", "warning",
                "title", "Increasing is blocked",
                "body", "A higher active limit is rejected until the next planning period.")),
        component(
            "timeline",
            Map.of(
                "title", "Limit lifecycle",
                "items",
                List.of("Create period", "Set strict limit", "Track spending", "Decrease only"))));
  }

  private List<DuiComponent> sessionsWorkspace() {
    return List.of(
        component(
            "section",
            Map.of(
                "title", "Session logger",
                "body", "Record current spending and evaluate the warning state locally."),
            List.of(component("activityForm", Map.of()))),
        component(
            "dashboardSummary",
            Map.of(
                "title", "Latest mock session",
                "period", "WEEK",
                "status", "not saved",
                "limit", "$80.00",
                "spent", "$20.00",
                "remaining", "$60.00")),
        component(
            "warningBlock",
            Map.of(
                "level", "safe",
                "title", "No encouragement loop",
                "body", "This screen records spending only. It does not rate play quality.")));
  }

  private List<DuiComponent> calendarWorkspace() {
    return List.of(
        component(
            "statStrip",
            Map.of(
                "title", "Mock weekly map",
                "items",
                List.of("Mon safe", "Tue safe", "Wed warning", "Thu paused", "Fri planned"))),
        component(
            "timeline",
            Map.of(
                "title", "Pattern notes",
                "items",
                List.of(
                    "Warnings stay visible",
                    "Paused days are valid progress",
                    "Calendar does not gamify streaks"))),
        component(
            "warningBlock",
            Map.of(
                "level", "warning",
                "title", "Risk day preview",
                "body", "A warning day should suggest pausing, not chasing results.")));
  }

  private List<DuiComponent> statisticsWorkspace() {
    return List.of(
        component(
            "statStrip",
            Map.of(
                "title", "Discipline signals",
                "items",
                List.of("3 warnings", "1 reduction", "$60 remaining", "0 increases allowed"))),
        component(
            "progressBar",
            Map.of("label", "Mock limit usage", "value", "25%", "level", "safe")),
        component(
            "section",
            Map.of(
                "title", "Signal over noise",
                "body",
                "Statistics focus on limits, warnings, and reductions - not performance.")));
  }

  private DuiIntentResponse plannedScreen(String screen) {
    return screenResponse(
        screen,
        List.of(
            component(
                "section",
                Map.of(
                    "title", "Planned MVP screen",
                    "body",
                    "This route is stable while domain and application contracts mature."))));
  }

  private DuiIntentResponse screenResponse(String screen, List<DuiComponent> components) {
    return new DuiIntentResponse(VERSION, screen, WHITELIST, components, false, List.of());
  }

  private DuiIntentResponse fallback(String screen, List<String> messages) {
    return new DuiIntentResponse(
        VERSION,
        screen,
        WHITELIST,
        List.of(
            component(
                "fallback",
                Map.of(
                    "title", "Safe fallback",
                    "body", "The requested JSON did not pass validation and was not rendered."))),
        true,
        messages);
  }

  private DuiIntentResponse safe(DuiIntentResponse response, String fallbackScreen) {
    if (validator.isValid(response)) {
      return response;
    }
    return fallback(fallbackScreen, List.of("Unsafe DUI response was replaced."));
  }

  private DuiComponent component(String type, Map<String, Object> props) {
    return new DuiComponent(type, props);
  }

  private DuiComponent component(
      String type, Map<String, Object> props, List<DuiComponent> children) {
    return new DuiComponent(type, props, children);
  }

  private DuiIntentRequest defaultRequest() {
    return new DuiIntentRequest(
        "dashboard",
        new DuiIntentRequest.FinancialProfileInput(
            "USD",
            "3200.00",
            "1800.00",
            "350.00",
            "150.00",
            "10",
            "UTC"),
        "WEEK",
        "80.00",
        "20.00",
        "");
  }

  private DuiIntentRequest replaceLimit(DuiIntentRequest request, String limit) {
    return new DuiIntentRequest(
        request.screen(),
        request.profile(),
        request.period(),
        limit,
        request.spent(),
        request.currentLimit());
  }

  private LocalAmounts parseAmounts(DuiIntentRequest request) {
    BigDecimal limit = money(request.limit(), "limit");
    BigDecimal spent = money(request.spent(), "spent");
    BigDecimal currentLimit =
        request.currentLimit() == null || request.currentLimit().isBlank()
            ? null
            : money(request.currentLimit(), "currentLimit");
    if (spent.compareTo(BigDecimal.ZERO) < 0 || limit.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Amounts must not be negative");
    }
    return new LocalAmounts(limit, spent, currentLimit);
  }

  private BigDecimal money(String value, String field) {
    try {
      return new BigDecimal(value).setScale(2, RoundingMode.UNNECESSARY);
    } catch (RuntimeException exception) {
      throw new IllegalArgumentException("Invalid " + field + " amount", exception);
    }
  }

  private String normalize(String value) {
    return value == null ? "" : value.trim().toLowerCase();
  }

  private String usd(BigDecimal value) {
    return "$" + value.setScale(2, RoundingMode.UNNECESSARY).toPlainString();
  }

  private String warningLevel(BigDecimal usage) {
    if (usage.compareTo(BigDecimal.valueOf(100)) >= 0) {
      return "danger";
    }
    if (usage.compareTo(BigDecimal.valueOf(75)) >= 0) {
      return "warning";
    }
    return "safe";
  }

  private String statusText(String level) {
    return switch (level) {
      case "danger" -> "limit reached";
      case "warning" -> "attention";
      default -> "within limit";
    };
  }

  private String warningTitle(String level) {
    return switch (level) {
      case "danger" -> "Active limit reached";
      case "warning" -> "Warning threshold reached";
      default -> "Within active limit";
    };
  }

  private String warningBody(String level) {
    return switch (level) {
      case "danger" -> "Stop now. The planned bankroll boundary has been reached.";
      case "warning" -> "You are close to the active limit. Pause before any new decision.";
      default -> "The current mock plan is still inside the user-defined boundary.";
    };
  }

  private record LocalAmounts(BigDecimal limit, BigDecimal spent, BigDecimal currentLimit) {
  }
}
