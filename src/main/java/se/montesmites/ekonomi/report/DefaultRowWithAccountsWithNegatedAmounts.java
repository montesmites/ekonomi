package se.montesmites.ekonomi.report;

import java.time.Month;
import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.Currency;

public class DefaultRowWithAccountsWithNegatedAmounts implements RowWithAmounts {

  private final RowWithAmounts source;

  public DefaultRowWithAccountsWithNegatedAmounts(RowWithAmounts source) {
    this.source = source;
  }

  @Override
  public String formatDescription() {
    return source.formatDescription();
  }

  @Override
  public Currency getMonthlyAmount(Column column) {
    final Currency sourceAmount = source.getMonthlyAmount(column);
    return Signedness.NEGATED_SIGN.apply(sourceAmount);
  }

  @Override
  public Supplier<Stream<Month>> months() {
    return source.months();
  }
}
