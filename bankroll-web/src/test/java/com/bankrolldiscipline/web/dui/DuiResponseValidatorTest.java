package com.bankrolldiscipline.web.dui;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DuiResponseValidatorTest {

  private final DuiResponseValidator validator = new DuiResponseValidator();

  @Test
  void shouldAcceptWhitelistedComponentsWithPublicProps() {
    DuiIntentResponse response =
        new DuiIntentResponse(
            "1",
            "dashboard",
            DuiComponentRegistry.allowedTypes(),
            List.of(
                new DuiComponent(
                    "dashboardSummary",
                    Map.of(
                        "title", "Remaining bankroll",
                        "period", "WEEK",
                        "status", "within limit",
                        "limit", "$80.00",
                        "spent", "$20.00",
                        "remaining", "$60.00"))),
            false,
            List.of());

    assertThat(validator.isValid(response)).isTrue();
  }

  @Test
  void shouldRejectForbiddenComponentTypes() {
    DuiIntentResponse response =
        new DuiIntentResponse(
            "1",
            "dashboard",
            DuiComponentRegistry.allowedTypes(),
            List.of(new DuiComponent("RawHtml", Map.of("title", "Unsafe"))),
            false,
            List.of());

    assertThat(validator.isValid(response)).isFalse();
  }

  @Test
  void shouldRejectInternalReasoningProps() {
    DuiIntentResponse response =
        new DuiIntentResponse(
            "1",
            "dashboard",
            DuiComponentRegistry.allowedTypes(),
            List.of(
                new DuiComponent(
                    "section",
                    Map.of(
                        "title", "Unsafe",
                        "chainOfThought", "hidden reasoning must never be exposed"))),
            false,
            List.of());

    assertThat(validator.isValid(response)).isFalse();
  }

  @Test
  void shouldRejectUnknownServerWhitelistEntries() {
    DuiIntentResponse response =
        new DuiIntentResponse(
            "1",
            "dashboard",
            Set.of("dashboardSummary", "UnsafeHtml"),
            List.of(
                new DuiComponent(
                    "dashboardSummary",
                    Map.of(
                        "title", "Remaining bankroll",
                        "period", "WEEK",
                        "status", "within limit",
                        "limit", "$80.00",
                        "spent", "$20.00",
                        "remaining", "$60.00"))),
            false,
            List.of());

    assertThat(validator.isValid(response)).isFalse();
  }
}
