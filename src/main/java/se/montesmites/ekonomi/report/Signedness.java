package se.montesmites.ekonomi.report;

import se.montesmites.ekonomi.model.Currency;

public enum Signedness {
  UNCHANGED_SIGN(1),
  NEGATED_SIGN(-1);

  private final int signedness;

  Signedness(int signedness) {
    this.signedness = signedness;
  }

  public long apply(long number) {
    return number * signedness;
  }

  public Currency apply(Currency amount) {
    return new Currency(amount.getAmount() * signedness);
  }
}
