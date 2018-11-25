package se.montesmites.ekonomi.report;

import java.time.Month;
import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.Currency;

class RowAggregator {

  static RowAggregator of(Supplier<Stream<? extends RowWithAmounts>> rows) {
    return new RowAggregator(rows);
  }

  private final Supplier<Stream<? extends RowWithAmounts>> rows;

  private RowAggregator(Supplier<Stream<? extends RowWithAmounts>> rows) {
    this.rows = rows;
  }

  RowWithAmounts aggregate() {
    return new RowWithAmounts() {
      @Override
      public Supplier<Stream<Month>> months() {
        return rows.get()
            .findAny()
            .map(RowWithAmounts::months)
            .orElse(Stream::empty);
      }

      @Override
      public Currency getMonthlyAmount(Column column) {
        return rows.get()
            .map(row -> row.getMonthlyAmount(column))
            .reduce(new Currency(0), Currency::add);
      }
    };
  }
}
