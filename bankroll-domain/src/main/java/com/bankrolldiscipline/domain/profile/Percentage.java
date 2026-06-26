package com.bankrolldiscipline.domain.profile;

import com.bankrolldiscipline.domain.money.Money;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * An immutable percentage in the inclusive range from 0 to 100.
 *
 * @param value normalized decimal percentage value
 */
public record Percentage(BigDecimal value) {

  private static final BigDecimal MAX_VALUE = new BigDecimal("100");

  /**
   * Creates and normalizes a percentage.
   *
   * @throws NullPointerException if the value is {@code null}
   * @throws IllegalArgumentException if the value is outside the 0-100 range
   */
  public Percentage {
    Objects.requireNonNull(value, "Percentage value must not be null");
    if (value.signum() < 0 || value.compareTo(MAX_VALUE) > 0) {
      throw new IllegalArgumentException("Percentage must be between 0 and 100");
    }

    value = normalize(value);
  }

  /**
   * Parses an exact decimal percentage.
   *
   * @throws NullPointerException if the value is {@code null}
   * @throws IllegalArgumentException if the value is not a valid decimal percentage
   */
  public static Percentage of(String value) {
    Objects.requireNonNull(value, "Percentage value must not be null");
    try {
      return new Percentage(new BigDecimal(value));
    } catch (NumberFormatException exception) {
      throw new IllegalArgumentException(
          "Invalid percentage: expected a decimal number using '.' as separator",
          exception);
    }
  }

  /**
   * Applies this percentage to a monetary amount using the requested rounding mode.
   */
  public Money applyTo(Money amount, RoundingMode roundingMode) {
    Objects.requireNonNull(amount, "Percentage amount must not be null");
    Objects.requireNonNull(roundingMode, "Rounding mode must not be null");

    BigDecimal calculatedAmount =
        amount.amount()
            .multiply(value)
            .movePointLeft(2)
            .setScale(amount.currency().fractionDigits(), roundingMode);
    return new Money(calculatedAmount, amount.currency());
  }

  private static BigDecimal normalize(BigDecimal value) {
    BigDecimal normalized = value.stripTrailingZeros();
    return normalized.scale() < 0 ? normalized.setScale(0) : normalized;
  }
}
