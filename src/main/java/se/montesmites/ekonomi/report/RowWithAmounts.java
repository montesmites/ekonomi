package se.montesmites.ekonomi.report;

import java.time.Month;
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
}
