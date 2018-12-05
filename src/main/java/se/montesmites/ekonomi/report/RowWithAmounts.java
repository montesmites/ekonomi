package se.montesmites.ekonomi.report;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import se.montesmites.ekonomi.model.Currency;

@FunctionalInterface
public interface RowWithAmounts extends RowWithGranularFormatters {

  static RowWithAmounts empty() {
    return column -> Optional.of(Currency.zero());
  }

  static RowWithAmounts of(Function<Column, Optional<Currency>> amounts) {
    return amounts::apply;
  }

  static RowWithAmounts of(Map<Column, Currency> amounts) {
    return column -> Optional.ofNullable(amounts.get(column));
  }

  Optional<Currency> getMonthlyAmount(Column column);

  default Currency getYearlyTotal() {
    return Column.streamMonths()
        .map(this::getMonthlyAmount)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .reduce(Currency.zero(), Currency::add);
  }

  default Currency getAverage() {
    var average =
        Column.streamMonths()
            .map(this::getMonthlyAmount)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .mapToLong(Currency::getAmount)
            .average()
            .orElse(0);
    return Currency.of(Math.round(average));
  }

  @Override
  default String formatMonth(Column column) {
    return getMonthlyAmount(column).orElse(Currency.zero()).format();
  }

  @Override
  default String formatTotal() {
    return getYearlyTotal().format();
  }

  @Override
  default String formatAverage() {
    return getAverage().format();
  }

  @Override
  default Optional<RowWithAmounts> asRowWithAmounts() {
    return Optional.of(this);
  }

  default RowWithAmounts negate() {
    var base = this;
    return new RowWithAmounts() {
      @Override
      public String formatDescription() {
        return base.formatDescription();
      }

      @Override
      public Optional<Currency> getMonthlyAmount(Column column) {
        return base.getMonthlyAmount(column).map(Currency::negate);
      }
    };
  }

  default RowWithAmounts description(String description) {
    var base = this;
    return new RowWithAmounts() {
      @Override
      public Optional<Currency> getMonthlyAmount(Column column) {
        return base.getMonthlyAmount(column);
      }

      @Override
      public String formatDescription() {
        return description;
      }
    };
  }

  default RowWithAmounts accumulate(Currency initial) {
    var amounts = doAccumulate(initial);
    return new RowWithAmounts() {
      @Override
      public Optional<Currency> getMonthlyAmount(Column column) {
        return Optional.ofNullable(amounts.get(column));
      }

      @Override
      public String formatTotal() {
        return Currency.zero().format();
      }

      @Override
      public String formatDescription() {
        return initial.format();
      }
    };
  }

  private Map<Column, Currency> doAccumulate(Currency initial) {
    var base = this;
    var amounts = new EnumMap<Column, Currency>(Column.class);
    Column.streamMonths()
        .filter(column -> base.getMonthlyAmount(column).isPresent())
        .reduce(
            initial,
            (accumulator, column) ->
                amounts.merge(
                    column,
                    accumulator.add(base.getMonthlyAmount(column).orElse(Currency.zero())),
                    Currency::add),
            Currency::add);
    return Map.copyOf(amounts);
  }
}
