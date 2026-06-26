package com.bankrolldiscipline.domain.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bankrolldiscipline.domain.money.CurrencyCode;
import com.bankrolldiscipline.domain.money.Money;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FinancialProfileTest {

  private static final CurrencyCode PLN = CurrencyCode.of("PLN");
  private static final CurrencyCode USD = CurrencyCode.of("USD");
  private static final ZoneId WARSAW = ZoneId.of("Europe/Warsaw");

  @Test
  void shouldCreateProfileWithContractFields() {
    FinancialProfile profile = validProfile();

    assertThat(profile.currency()).isEqualTo(PLN);
    assertThat(profile.monthlyIncome()).isEqualTo(Money.of("5000.00", PLN));
    assertThat(profile.mandatoryExpenses()).isEqualTo(Money.of("2500.00", PLN));
    assertThat(profile.savingsGoal()).isEqualTo(Money.of("500.00", PLN));
    assertThat(profile.emergencyContribution()).isEqualTo(Money.of("250.00", PLN));
    assertThat(profile.allocationPercentage()).isEqualTo(Percentage.of("5"));
    assertThat(profile.timeZone()).isEqualTo(WARSAW);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"monthlyIncome", "mandatoryExpenses", "savingsGoal", "emergencyContribution"})
  void shouldRejectNegativeFinancialValue(String field) {
    assertThatThrownBy(() -> profileWithMoney(field, Money.of("-0.01", PLN)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(field + " must not be negative");
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"monthlyIncome", "mandatoryExpenses", "savingsGoal", "emergencyContribution"})
  void shouldRejectMoneyInAnotherCurrency(String field) {
    assertThatThrownBy(() -> profileWithMoney(field, Money.of("1.00", USD)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(field + " currency must be PLN");
  }

  @Test
  void shouldAllowProtectedMoneyToExceedIncome() {
    FinancialProfile profile =
        new FinancialProfile(
            PLN,
            Money.of("1000.00", PLN),
            Money.of("1200.00", PLN),
            Money.zero(PLN),
            Money.zero(PLN),
            Percentage.of("5"),
            WARSAW);

    assertThat(profile.mandatoryExpenses()).isGreaterThan(profile.monthlyIncome());
  }

  @Test
  void shouldRejectNullTimeZone() {
    assertThatNullPointerException()
        .isThrownBy(
            () ->
                new FinancialProfile(
                    PLN,
                    Money.zero(PLN),
                    Money.zero(PLN),
                    Money.zero(PLN),
                    Money.zero(PLN),
                    Percentage.of("5"),
                    null))
        .withMessage("Time zone must not be null");
  }

  private static FinancialProfile validProfile() {
    return new FinancialProfile(
        PLN,
        Money.of("5000.00", PLN),
        Money.of("2500.00", PLN),
        Money.of("500.00", PLN),
        Money.of("250.00", PLN),
        Percentage.of("5"),
        WARSAW);
  }

  private static FinancialProfile profileWithMoney(String field, Money replacement) {
    FinancialProfile profile = validProfile();
    return new FinancialProfile(
        profile.currency(),
        field.equals("monthlyIncome") ? replacement : profile.monthlyIncome(),
        field.equals("mandatoryExpenses") ? replacement : profile.mandatoryExpenses(),
        field.equals("savingsGoal") ? replacement : profile.savingsGoal(),
        field.equals("emergencyContribution") ? replacement : profile.emergencyContribution(),
        profile.allocationPercentage(),
        profile.timeZone());
  }
}
