package se.montesmites.ekonomi.report;

import java.util.Optional;
import java.util.function.Function;
import se.montesmites.ekonomi.model.Currency;

public class DefaultRowWithAmounts implements RowWithAmounts {

  private final String description;
  private final Function<Column, Currency> monthlyAmounts;

  public DefaultRowWithAmounts(String description, Function<Column, Currency> monthlyAmounts) {
    this.description = description;
    this.monthlyAmounts = monthlyAmounts;
  }

  @Override
  public String formatDescription() {
    return description;
  }

  @Override
  public Currency getMonthlyAmount(Column column) {
    return monthlyAmounts.apply(column);
  }

  @Override
  public Optional<RowWithAmounts> asRowWithAmounts() {
    return Optional.of(this);
  }
}
