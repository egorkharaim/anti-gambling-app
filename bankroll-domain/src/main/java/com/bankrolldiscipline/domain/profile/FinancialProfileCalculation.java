package com.bankrolldiscipline.domain.profile;

import com.bankrolldiscipline.domain.money.Money;
import java.util.Objects;

/**
 * Exact calculated values derived from a financial profile.
 *
 * @param protectedMoney expenses and protected contributions
 * @param disposableIncome income remaining after protected money
 * @param recommendedLimit selected percentage of disposable income
 * @param deficitAmount protected money exceeding income
 */
public record FinancialProfileCalculation(
    Money protectedMoney,
    Money disposableIncome,
    Money recommendedLimit,
    Money deficitAmount) {

  /**
   * Creates a calculation result and verifies its internal consistency.
   */
  public FinancialProfileCalculation {
    Objects.requireNonNull(protectedMoney, "Protected money must not be null");
    Objects.requireNonNull(disposableIncome, "Disposable income must not be null");
    Objects.requireNonNull(recommendedLimit, "Recommended limit must not be null");
    Objects.requireNonNull(deficitAmount, "Deficit amount must not be null");

    requireSameCurrency(protectedMoney, disposableIncome);
    requireSameCurrency(protectedMoney, recommendedLimit);
    requireSameCurrency(protectedMoney, deficitAmount);
    requireNonNegative(protectedMoney, "Protected money");
    requireNonNegative(disposableIncome, "Disposable income");
    requireNonNegative(recommendedLimit, "Recommended limit");
    requireNonNegative(deficitAmount, "Deficit amount");
  }

  public boolean hasDeficit() {
    return !deficitAmount.isZero();
  }

  private static void requireSameCurrency(Money expected, Money actual) {
    if (!expected.currency().equals(actual.currency())) {
      throw new IllegalArgumentException("Calculation values must use the same currency");
    }
  }

  private static void requireNonNegative(Money value, String field) {
    if (value.isNegative()) {
      throw new IllegalArgumentException(field + " must not be negative");
    }
  }
}
