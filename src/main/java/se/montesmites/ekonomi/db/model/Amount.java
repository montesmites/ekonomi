package se.montesmites.ekonomi.db.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import se.montesmites.ekonomi.i18n.Messages;

public record Amount(BigDecimal amount) {

  public static final int NUMBER_OF_DECIMALS = 2;
  public static final Amount ZERO = new Amount(BigDecimal.valueOf(0, 2));

  public enum Sign {
    NEGATIVE,
    ZERO,
    POSITIVE
  }

  public Amount add(Amount that) {
    return new Amount(this.amount.add(that.amount));
  }

  public Amount divide(Amount that) {
    return new Amount(this.amount.divide(that.amount, RoundingMode.HALF_UP));
  }

  public Amount negate() {
    return new Amount(this.amount.negate());
  }

  public String format() {
    return Messages.formatNumber(this);
  }

  public Sign sign() {
    var comparison = this.amount.compareTo(BigDecimal.ZERO);
    return comparison < 0 ? Sign.NEGATIVE : comparison > 0 ? Sign.POSITIVE : Sign.ZERO;
  }

  public static Amount of(long amount) {
    return new Amount(new BigDecimal(amount));
  }
}
