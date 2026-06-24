package com.bankrolldiscipline.domain.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * An immutable monetary amount with currency-safe arithmetic.
 *
 * @param amount exact decimal amount normalized to the currency scale
 * @param currency amount currency
 */
public record Money(BigDecimal amount, CurrencyCode currency) implements Comparable<Money> {

  /**
   * Creates a monetary amount and normalizes it without rounding.
   *
   * @throws NullPointerException if the amount or currency is {@code null}
   * @throws IllegalArgumentException if non-zero fraction digits would be discarded
   */
  public Money {
    Objects.requireNonNull(amount, "Money amount must not be null");
    Objects.requireNonNull(currency, "Money currency must not be null");

    try {
      amount = amount.setScale(currency.fractionDigits(), RoundingMode.UNNECESSARY);
    } catch (ArithmeticException exception) {
      throw new IllegalArgumentException(
          "Amount " + amount.toPlainString() + " has an invalid scale for " + currency.value(),
          exception);
    }
  }

  /**
   * Parses an exact decimal amount using a dot as the decimal separator.
   *
   * @throws NullPointerException if the amount is {@code null}
   * @throws IllegalArgumentException if the amount is not a valid decimal number
   */
  public static Money of(String amount, CurrencyCode currency) {
    Objects.requireNonNull(amount, "Money amount must not be null");
    try {
      return new Money(new BigDecimal(amount), currency);
    } catch (NumberFormatException exception) {
      throw new IllegalArgumentException(
          "Invalid monetary amount: expected a decimal number using '.' as separator",
          exception);
    }
  }

  public static Money zero(CurrencyCode currency) {
    return new Money(BigDecimal.ZERO, currency);
  }

  public Money add(Money augend) {
    requireSameCurrency(augend);
    return new Money(amount.add(augend.amount), currency);
  }

  public Money subtract(Money subtrahend) {
    requireSameCurrency(subtrahend);
    return new Money(amount.subtract(subtrahend.amount), currency);
  }

  public Money negate() {
    return new Money(amount.negate(), currency);
  }

  public Money abs() {
    return amount.signum() < 0 ? negate() : this;
  }

  public boolean isNegative() {
    return amount.signum() < 0;
  }

  public boolean isZero() {
    return amount.signum() == 0;
  }

  @Override
  public int compareTo(Money other) {
    requireSameCurrency(other);
    return amount.compareTo(other.amount);
  }

  private void requireSameCurrency(Money other) {
    Objects.requireNonNull(other, "Money operand must not be null");
    if (!currency.equals(other.currency)) {
      throw new IllegalArgumentException(
          "Currency mismatch: " + currency.value() + " and " + other.currency.value());
    }
  }
}
