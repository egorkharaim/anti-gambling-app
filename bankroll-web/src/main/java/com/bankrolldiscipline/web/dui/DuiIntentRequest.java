package com.bankrolldiscipline.web.dui;

/** Request body for the local DUI mock flow. */
public record DuiIntentRequest(
    String screen,
    FinancialProfileInput profile,
    String period,
    String limit,
    String spent,
    String currentLimit) {

  /** Financial profile input represented as API-safe strings. */
  public record FinancialProfileInput(
      String currency,
      String monthlyIncome,
      String mandatoryExpenses,
      String savingsGoal,
      String emergencyContribution,
      String allocationPercentage,
      String timeZone) {
  }
}
