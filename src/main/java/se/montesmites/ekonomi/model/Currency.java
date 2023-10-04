package se.montesmites.ekonomi.model;

import java.math.BigDecimal;
import se.montesmites.ekonomi.db.model.Amount;
import se.montesmites.ekonomi.i18n.Messages;

public record Currency(long amount) {

  private static final int decimals = 2;
  private static final double divisor = Math.pow(10, decimals);

  public static Currency zero() {
    return new Currency(0);
  }

  public static Currency from(BigDecimal amount) {
    return new Currency(amount.multiply(BigDecimal.valueOf(100)).longValue());
  }

  public Currency add(Currency that) {
    return new Currency(this.amount + that.amount);
  }

  public int decimalPlaces() {
    return decimals;
  }

  public double toDouble() {
    return (double) amount / divisor;
  }

  public String format() {
    return Messages.formatNumber(this);
  }

  public Currency negate() {
    return new Currency(-this.amount);
  }

  private BigDecimal toBigDecimal() {
    return BigDecimal.valueOf(this.amount(), decimals);
  }

  public Amount toAmount() {
    return new Amount(this.toBigDecimal());
  }
}
