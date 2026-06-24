package com.bankrolldiscipline.domain.money;

import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

/**
 * An ISO 4217 currency code supported by the current Java runtime.
 *
 * @param value normalized three-letter currency code
 */
public record CurrencyCode(String value) {

  /**
   * Creates and normalizes a supported currency code.
   *
   * @throws NullPointerException if the code is {@code null}
   * @throws IllegalArgumentException if the code is unsupported
   */
  public CurrencyCode {
    Objects.requireNonNull(value, "Currency code must not be null");

    String normalizedValue = value.trim().toUpperCase(Locale.ROOT);
    Currency currency;
    try {
      currency = Currency.getInstance(normalizedValue);
    } catch (IllegalArgumentException exception) {
      throw new IllegalArgumentException("Unsupported currency code: " + value, exception);
    }

    if (currency.getDefaultFractionDigits() < 0) {
      throw new IllegalArgumentException("Currency must define fraction digits: " + value);
    }

    value = normalizedValue;
  }

  public static CurrencyCode of(String value) {
    return new CurrencyCode(value);
  }

  public int fractionDigits() {
    return Currency.getInstance(value).getDefaultFractionDigits();
  }
}
