package se.montesmites.ekonomi.ui.model;

import java.util.Optional;
import se.montesmites.ekonomi.db.model.Amount;

public record DebitCreditAmount(Side side, Optional<Amount> amount) {

  public enum Side {
    PENDING,
    DEBIT,
    CREDIT;

    public Side opposite() {
      return switch (this) {
        case DEBIT -> CREDIT;
        case CREDIT -> DEBIT;
        case PENDING -> PENDING;
      };
    }
  }

  public DebitCreditAmount with(Side side, Optional<Amount> amount) {
    return new DebitCreditAmount(side, amount);
  }

  public DebitCreditAmount merge(DebitCreditAmount other) {
    return other.amount().map(__ -> other).orElse(this);
  }

  public Optional<Amount> asAmount() {
    return amount.map(
        _amount ->
            switch (side) {
              case DEBIT, PENDING -> _amount;
              case CREDIT -> _amount.negate();
            });
  }

  public Optional<String> format(Side side) {
    return this.side == side ? amount.map(Amount::format) : Optional.empty();
  }

  public static DebitCreditAmount parse(Side side, String text) {
    return Amount.parse(text)
        .map(
            amount ->
                switch (amount.sign()) {
                  case POSITIVE, ZERO -> new DebitCreditAmount(side, Optional.of(amount));
                  case NEGATIVE -> new DebitCreditAmount(
                      side.opposite(), Optional.of(amount.negate()));
                })
        .orElse(DebitCreditAmount.empty());
  }

  public static DebitCreditAmount empty() {
    return new DebitCreditAmount(Side.PENDING, Optional.empty());
  }

  public static DebitCreditAmount from(Amount amount) {
    return switch (amount.sign()) {
      case POSITIVE, ZERO -> new DebitCreditAmount(Side.DEBIT, Optional.of(amount.absolute()));
      case NEGATIVE -> new DebitCreditAmount(Side.CREDIT, Optional.of(amount.absolute()));
    };
  }
}
