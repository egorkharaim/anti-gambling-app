package com.bankrolldiscipline.domain.money;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class MoneyTest {

  private static final CurrencyCode PLN = CurrencyCode.of("PLN");
  private static final CurrencyCode USD = CurrencyCode.of("USD");

  @ParameterizedTest
  @CsvSource({"JPY, 10, 10", "PLN, 10, 10.00", "KWD, 10, 10.000"})
  void shouldNormalizeAmountToCurrencyScale(String code, String input, String expected) {
    Money money = Money.of(input, CurrencyCode.of(code));

    assertThat(money.amount()).isEqualByComparingTo(expected);
    assertThat(money.amount().scale()).isEqualTo(CurrencyCode.of(code).fractionDigits());
  }

  @Test
  void shouldAcceptRedundantTrailingZeros() {
    Money money = Money.of("10.000", PLN);

    assertThat(money.amount()).isEqualTo(new BigDecimal("10.00"));
  }

  @Test
  void shouldRejectAmountWithUnsupportedFraction() {
    assertThatThrownBy(() -> Money.of("10.001", PLN))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("invalid scale")
        .hasMessageContaining("PLN");
  }

  @ParameterizedTest
  @ValueSource(strings = {"abc", "12,50", ""})
  void shouldRejectMalformedAmountWithStableMessage(String amount) {
    assertThatThrownBy(() -> Money.of(amount, PLN))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "Invalid monetary amount: expected a decimal number using '.' as separator")
        .hasCauseInstanceOf(NumberFormatException.class);
  }

  @Test
  void shouldAddDecimalAmountsExactly() {
    Money result = Money.of("0.10", PLN).add(Money.of("0.20", PLN));

    assertThat(result).isEqualTo(Money.of("0.30", PLN));
  }

  @Test
  void shouldPreserveLargeAmountPrecision() {
    Money result =
        Money.of("12345678901234567.89", PLN).add(Money.of("0.01", PLN));

    assertThat(result).isEqualTo(Money.of("12345678901234567.90", PLN));
  }

  @Test
  void shouldSubtractIntoNegativeAmount() {
    Money result = Money.of("10.00", PLN).subtract(Money.of("12.50", PLN));

    assertThat(result).isEqualTo(Money.of("-2.50", PLN));
    assertThat(result.isNegative()).isTrue();
  }

  @Test
  void shouldNegateAndReturnAbsoluteAmount() {
    Money negative = Money.of("-12.50", PLN);

    assertThat(negative.negate()).isEqualTo(Money.of("12.50", PLN));
    assertThat(negative.abs()).isEqualTo(Money.of("12.50", PLN));
  }

  @Test
  void shouldCreateCurrencyScaledZero() {
    Money zero = Money.zero(PLN);

    assertThat(zero.amount()).isEqualTo(new BigDecimal("0.00"));
    assertThat(zero.isZero()).isTrue();
  }

  @Test
  void shouldCompareAmountsInTheSameCurrency() {
    assertThat(Money.of("10.00", PLN)).isLessThan(Money.of("10.01", PLN));
  }

  @ParameterizedTest
  @CsvSource({"add", "subtract", "compare"})
  void shouldRejectOperationsBetweenCurrencies(String operation) {
    Money pln = Money.of("10.00", PLN);
    Money usd = Money.of("10.00", USD);

    assertThatThrownBy(
            () -> {
              switch (operation) {
                case "add" -> pln.add(usd);
                case "subtract" -> pln.subtract(usd);
                case "compare" -> pln.compareTo(usd);
                default -> throw new IllegalStateException("Unexpected operation: " + operation);
              }
            })
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Currency mismatch: PLN and USD");
  }

  @Test
  void shouldRejectNullAmount() {
    assertThatNullPointerException()
        .isThrownBy(() -> new Money(null, PLN))
        .withMessage("Money amount must not be null");
  }

  @Test
  void shouldRejectNullCurrency() {
    assertThatNullPointerException()
        .isThrownBy(() -> new Money(BigDecimal.ONE, null))
        .withMessage("Money currency must not be null");
  }
}
