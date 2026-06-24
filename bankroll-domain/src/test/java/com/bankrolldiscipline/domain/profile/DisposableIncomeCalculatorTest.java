package com.bankrolldiscipline.domain.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import com.bankrolldiscipline.domain.money.CurrencyCode;
import com.bankrolldiscipline.domain.money.Money;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DisposableIncomeCalculatorTest {

  private static final CurrencyCode PLN = CurrencyCode.of("PLN");
  private static final ZoneId WARSAW = ZoneId.of("Europe/Warsaw");
  private final DisposableIncomeCalculator calculator = new DisposableIncomeCalculator();

  @Test
  void shouldCalculateDisposableIncomeAndRecommendedLimit() {
    FinancialProfileCalculation result =
        calculator.calculate(profile("5000.00", "2500.00", "500.00", "250.00", "5"));

    assertThat(result.protectedMoney()).isEqualTo(Money.of("3250.00", PLN));
    assertThat(result.disposableIncome()).isEqualTo(Money.of("1750.00", PLN));
    assertThat(result.recommendedLimit()).isEqualTo(Money.of("87.50", PLN));
    assertThat(result.deficitAmount()).isEqualTo(Money.zero(PLN));
    assertThat(result.hasDeficit()).isFalse();
  }

  @Test
  void shouldFloorDisposableIncomeAndExposeDeficit() {
    FinancialProfileCalculation result =
        calculator.calculate(profile("1000.00", "1200.00", "100.00", "50.00", "5"));

    assertThat(result.protectedMoney()).isEqualTo(Money.of("1350.00", PLN));
    assertThat(result.disposableIncome()).isEqualTo(Money.zero(PLN));
    assertThat(result.recommendedLimit()).isEqualTo(Money.zero(PLN));
    assertThat(result.deficitAmount()).isEqualTo(Money.of("350.00", PLN));
    assertThat(result.hasDeficit()).isTrue();
  }

  @Test
  void shouldNotReportDeficitWhenProtectedMoneyEqualsIncome() {
    FinancialProfileCalculation result =
        calculator.calculate(profile("1000.00", "800.00", "100.00", "100.00", "5"));

    assertThat(result.disposableIncome()).isEqualTo(Money.zero(PLN));
    assertThat(result.deficitAmount()).isEqualTo(Money.zero(PLN));
    assertThat(result.hasDeficit()).isFalse();
  }

  @ParameterizedTest
  @CsvSource({"0, 0.00", "100, 10.01", "33.33, 3.33"})
  void shouldApplyPercentageAndRoundDown(String percentage, String expectedLimit) {
    FinancialProfileCalculation result =
        calculator.calculate(profile("10.01", "0.00", "0.00", "0.00", percentage));

    assertThat(result.recommendedLimit()).isEqualTo(Money.of(expectedLimit, PLN));
  }

  @Test
  void shouldPreservePrecisionForLargeAmounts() {
    FinancialProfileCalculation result =
        calculator.calculate(
            profile(
                "9999999999999999.99",
                "1111111111111111.11",
                "2222222222222222.22",
                "3333333333333333.33",
                "7.5"));

    assertThat(result.disposableIncome()).isEqualTo(Money.of("3333333333333333.33", PLN));
    assertThat(result.recommendedLimit()).isEqualTo(Money.of("249999999999999.99", PLN));
  }

  @Test
  void shouldRejectNullProfile() {
    assertThatNullPointerException()
        .isThrownBy(() -> calculator.calculate(null))
        .withMessage("Financial profile must not be null");
  }

  private static FinancialProfile profile(
      String income,
      String expenses,
      String savings,
      String emergencyContribution,
      String percentage) {
    return new FinancialProfile(
        PLN,
        Money.of(income, PLN),
        Money.of(expenses, PLN),
        Money.of(savings, PLN),
        Money.of(emergencyContribution, PLN),
        Percentage.of(percentage),
        WARSAW);
  }
}
