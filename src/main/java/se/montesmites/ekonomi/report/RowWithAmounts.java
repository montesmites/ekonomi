package se.montesmites.ekonomi.report;

import java.time.Month;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.Currency;

public interface RowWithAmounts extends RowWithGranularFormatters {

  static RowWithAmounts empty() {
    return column -> Currency.zero();
  }

  static RowWithAmounts of(Function<Column, Currency> amounts) {
    return amounts::apply;
  }

  default RowWithAmounts withMonths(Supplier<Stream<Month>> months) {
    var base = this;
    return new RowWithAmounts() {
      @Override
      public Currency getMonthlyAmount(Column column) {
        return base.getMonthlyAmount(column);
      }

      @Override
      public Supplier<Stream<Month>> months() {
        return months;
      }
    };
  }

  Currency getMonthlyAmount(Column column);

  default Currency getYearlyTotal() {
    return Column.streamMonths().map(this::getMonthlyAmount).reduce(Currency.zero(), Currency::add);
  }

  default Currency getAverage() {
    var average =
        months()
            .get()
            .map(Column::valueOf)
            .map(this::getMonthlyAmount)
            .mapToLong(Currency::getAmount)
            .average()
            .orElse(0);
    return Currency.of(Math.round(average));
  }

  default Supplier<Stream<Month>> months() {
    return Stream::empty;
  }

  @Override
  default String formatMonth(Column column) {
    return getMonthlyAmount(column).format();
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
      public Currency getMonthlyAmount(Column column) {
        return base.getMonthlyAmount(column).negate();
      }

      @Override
      public Supplier<Stream<Month>> months() {
        return base.months();
      }
    };
  }

  default RowWithAmounts description(String description) {
    var base = this;
    return new RowWithAmounts() {
      @Override
      public Currency getMonthlyAmount(Column column) {
        return base.getMonthlyAmount(column);
      }

      @Override
      public Supplier<Stream<Month>> months() {
        return base.months();
      }

      @Override
      public String formatDescription() {
        return description;
      }
    };
  }

  default RowWithAmounts accumulate(Currency initial) {
    var base = this;
    var amounts = doAccumulate(initial);
    return new RowWithAmounts() {
      @Override
      public Currency getMonthlyAmount(Column column) {
        return amounts.getOrDefault(column, Currency.zero());
      }

      @Override
      public Supplier<Stream<Month>> months() {
        return base.months();
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
    months()
        .get()
        .map(Column::valueOf)
        .reduce(
            initial,
            (accumulator, column) ->
                amounts.merge(
                    column, accumulator.add(base.getMonthlyAmount(column)), Currency::add),
            Currency::add);
    return Map.copyOf(amounts);
  }
}
