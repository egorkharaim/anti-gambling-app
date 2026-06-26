package com.bankrolldiscipline.domain.profile;

import com.bankrolldiscipline.domain.money.CurrencyCode;
import com.bankrolldiscipline.domain.money.Money;
import java.time.ZoneId;
import java.util.Objects;

/**
 * Financial inputs used to calculate an affordable gambling allocation.
 *
 * @param currency profile currency
 * @param monthlyIncome monthly net income
 * @param mandatoryExpenses mandatory monthly expenses
 * @param savingsGoal planned monthly savings
 * @param emergencyContribution planned monthly emergency fund contribution
 * @param allocationPercentage percentage of disposable income allocated to gambling
 * @param timeZone user's display time zone
 */
public record FinancialProfile(
    CurrencyCode currency,
    Money monthlyIncome,
    Money mandatoryExpenses,
    Money savingsGoal,
    Money emergencyContribution,
    Percentage allocationPercentage,
    ZoneId timeZone) {

  /**
   * Creates a financial profile and validates its monetary invariants.
   *
   * @throws NullPointerException if any field is {@code null}
   * @throws IllegalArgumentException if a monetary value is negative or uses another currency
   */
  public FinancialProfile {
    Objects.requireNonNull(currency, "Profile currency must not be null");
    Objects.requireNonNull(allocationPercentage, "Allocation percentage must not be null");
    Objects.requireNonNull(timeZone, "Time zone must not be null");

    validateMoney("monthlyIncome", monthlyIncome, currency);
    validateMoney("mandatoryExpenses", mandatoryExpenses, currency);
    validateMoney("savingsGoal", savingsGoal, currency);
    validateMoney("emergencyContribution", emergencyContribution, currency);
  }

  private static void validateMoney(String field, Money money, CurrencyCode expectedCurrency) {
    Objects.requireNonNull(money, field + " must not be null");
    if (!money.currency().equals(expectedCurrency)) {
      throw new IllegalArgumentException(
          field + " currency must be " + expectedCurrency.value());
    }
    if (money.isNegative()) {
      throw new IllegalArgumentException(field + " must not be negative");
    }
  }
}
