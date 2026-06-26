package com.bankrolldiscipline.web.dui;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DuiIntentController.class)
@Import({DuiIntentService.class, DuiResponseValidator.class})
class DuiIntentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldReturnValidatedDashboardComponents() throws Exception {
    mockMvc.perform(get("/api/dui/intent").param("screen", "dashboard"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.version").value("1"))
        .andExpect(jsonPath("$.screen").value("dashboard"))
        .andExpect(jsonPath("$.fallback").value(false))
        .andExpect(jsonPath("$.whitelist").isArray())
        .andExpect(jsonPath("$.components[0].type").value("dashboardSummary"));
  }

  @Test
  void shouldReturnLandingForUnsupportedInitialScreen() throws Exception {
    mockMvc.perform(get("/api/dui/intent").param("screen", "unknown"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.screen").value("landing"))
        .andExpect(jsonPath("$.components", hasSize(1)))
        .andExpect(jsonPath("$.components[0].type").value("section"))
        .andExpect(jsonPath("$.components[0].props.title", not(containsString("hidden"))));
  }

  @Test
  void shouldRejectActiveLimitIncreaseInMockFlow() throws Exception {
    String body = """
        {
          "screen": "dashboard",
          "profile": {
            "currency": "USD",
            "monthlyIncome": "3200.00",
            "mandatoryExpenses": "1800.00",
            "savingsGoal": "350.00",
            "emergencyContribution": "150.00",
            "allocationPercentage": "10",
            "timeZone": "UTC"
          },
          "period": "WEEK",
          "limit": "120.00",
          "spent": "10.00",
          "currentLimit": "80.00"
        }
        """;

    mockMvc.perform(
            post("/api/dui/intent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.screen").value("dashboard"))
        .andExpect(jsonPath("$.messages[0]").value("Limit increase was rejected."))
        .andExpect(jsonPath("$.components[0].props.limit").value("$80.00"));
  }
}
