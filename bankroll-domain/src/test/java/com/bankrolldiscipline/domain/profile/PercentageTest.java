package com.bankrolldiscipline.domain.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bankrolldiscipline.domain.money.CurrencyCode;
import com.bankrolldiscipline.domain.money.Money;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class PercentageTest {

  private static final CurrencyCode PLN = CurrencyCode.of("PLN");

  @ParameterizedTest
  @CsvSource({"0.00, 0", "5.000, 5", "100.0, 100", "2.50, 2.5"})
  void shouldNormalizeValidPercentage(String input, String expected) {
    assertThat(Percentage.of(input).value()).isEqualTo(new BigDecimal(expected));
  }

  @ParameterizedTest
  @ValueSource(strings = {"-0.01", "100.01", "999"})
  void shouldRejectPercentageOutsideInclusiveRange(String input) {
    assertThatThrownBy(() -> Percentage.of(input))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Percentage must be between 0 and 100");
  }

  @ParameterizedTest
  @ValueSource(strings = {"abc", "5,5", ""})
  void shouldRejectMalformedPercentageWithStableMessage(String input) {
    assertThatThrownBy(() -> Percentage.of(input))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "Invalid percentage: expected a decimal number using '.' as separator")
        .hasCauseInstanceOf(NumberFormatException.class);
  }

  @Test
  void shouldApplyPercentageExactly() {
    Money result = Percentage.of("5").applyTo(Money.of("1750.00", PLN), RoundingMode.DOWN);

    assertThat(result).isEqualTo(Money.of("87.50", PLN));
  }

  @Test
  void shouldRoundDownToCurrencyScale() {
    Money result =
        Percentage.of("33.33").applyTo(Money.of("10.01", PLN), RoundingMode.DOWN);

    assertThat(result).isEqualTo(Money.of("3.33", PLN));
  }

  @ParameterizedTest
  @CsvSource({
    "JPY, 10, 3",
    "PLN, 10.00, 3.33",
    "KWD, 10.000, 3.333"
  })
  void shouldRoundDownUsingSelectedCurrencyScale(
      String currency, String amount, String expected) {
    CurrencyCode currencyCode = CurrencyCode.of(currency);

    Money result =
        Percentage.of("33.33")
            .applyTo(Money.of(amount, currencyCode), RoundingMode.DOWN);

    assertThat(result).isEqualTo(Money.of(expected, currencyCode));
  }

  @Test
  void shouldRejectNullPercentage() {
    assertThatNullPointerException()
        .isThrownBy(() -> new Percentage(null))
        .withMessage("Percentage value must not be null");
  }
}
