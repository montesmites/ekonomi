package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toCollection;

import java.time.Month;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.Currency;

public interface RowWithAmounts extends RowWithGranularFormatters {

  static RowWithAmounts empty() {
    return column -> Currency.of(0);
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
    return Column.streamMonths().map(this::getMonthlyAmount).reduce(new Currency(0), Currency::add);
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
    return new Currency(Math.round(average));
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
    var months = months().get().collect(toCollection(() -> EnumSet.noneOf(Month.class)));
    var amounts = new EnumMap<Column, Currency>(Column.class);
    Column.streamMonths()
        .reduce(
            initial,
            (accumulator, column) -> {
              if (months.contains(column.getMonth().orElseThrow())) {
                var amount = base.getMonthlyAmount(column);
                var columnBalance = accumulator.add(amount);
                amounts.put(column, columnBalance);
                return columnBalance;
              } else {
                return accumulator;
              }
            },
            Currency::add);
    return new RowWithAmounts() {
      @Override
      public Currency getMonthlyAmount(Column column) {
        return amounts.getOrDefault(column, new Currency(0));
      }

      @Override
      public Supplier<Stream<Month>> months() {
        return base.months();
      }

      @Override
      public String formatTotal() {
        return Currency.of(0).format();
      }

      @Override
      public String formatDescription() {
        return initial.format();
      }
    };
  }
}
