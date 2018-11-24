package se.montesmites.ekonomi.report;

import java.time.Month;
import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.Currency;

class RowAggregator {

  static RowAggregator of(Supplier<Stream<Row>> rows) {
    return new RowAggregator(rows);
  }

  private final Supplier<Stream<Row>> rows;

  private RowAggregator(Supplier<Stream<Row>> rows) {
    this.rows = rows;
  }

  Row aggregate() {
    return new RowWithAmounts() {
      @Override
      public Supplier<Stream<Month>> months() {
        return rows.get()
            .findAny()
            .flatMap(Row::asRowWithAmounts)
            .map(RowWithAmounts::months)
            .orElse(Stream::empty);
      }

      @Override
      public Currency getMonthlyAmount(Column column) {
        return rows.get()
            .map(row -> row.asRowWithAmounts().orElseThrow())
            .map(row -> row.getMonthlyAmount(column))
            .reduce(new Currency(0), Currency::add);
      }
    };
  }
}
