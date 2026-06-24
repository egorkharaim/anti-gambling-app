package com.bankrolldiscipline.domain.money;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class CurrencyCodeTest {

  @ParameterizedTest
  @CsvSource({"pln, PLN", "' usd ', USD", "Kwd, KWD"})
  void shouldNormalizeSupportedCurrencyCode(String input, String expected) {
    assertThat(CurrencyCode.of(input).value()).isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource({"JPY, 0", "PLN, 2", "KWD, 3"})
  void shouldExposeCurrencyFractionDigits(String code, int expectedFractionDigits) {
    assertThat(CurrencyCode.of(code).fractionDigits()).isEqualTo(expectedFractionDigits);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "ABC", "XXX"})
  void shouldRejectUnsupportedCurrencyCode(String code) {
    assertThatThrownBy(() -> CurrencyCode.of(code))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldRejectNullCurrencyCode() {
    assertThatNullPointerException()
        .isThrownBy(() -> CurrencyCode.of(null))
        .withMessage("Currency code must not be null");
  }
}
