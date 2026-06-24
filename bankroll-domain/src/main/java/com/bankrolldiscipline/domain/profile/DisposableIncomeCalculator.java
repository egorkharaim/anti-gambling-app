package com.bankrolldiscipline.domain.profile;

import com.bankrolldiscipline.domain.money.Money;
import java.math.RoundingMode;
import java.util.Objects;

/** Calculates disposable income and the recommended gambling allocation. */
public final class DisposableIncomeCalculator {

  /**
   * Calculates protected money, disposable income, recommended limit, and deficit.
   */
  public FinancialProfileCalculation calculate(FinancialProfile profile) {
    Objects.requireNonNull(profile, "Financial profile must not be null");

    Money protectedMoney =
        profile.mandatoryExpenses()
            .add(profile.savingsGoal())
            .add(profile.emergencyContribution());
    Money disposableBeforeFloor = profile.monthlyIncome().subtract(protectedMoney);
    Money zero = Money.zero(profile.currency());
    boolean hasDeficit = disposableBeforeFloor.isNegative();
    Money disposableIncome = hasDeficit ? zero : disposableBeforeFloor;
    Money deficitAmount = hasDeficit ? disposableBeforeFloor.negate() : zero;
    Money recommendedLimit =
        profile.allocationPercentage().applyTo(disposableIncome, RoundingMode.DOWN);

    return new FinancialProfileCalculation(
        protectedMoney, disposableIncome, recommendedLimit, deficitAmount);
  }
}
