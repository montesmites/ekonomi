package se.montesmites.ekonomi.report;

import java.time.Month;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.Currency;

public interface Body {

  static Body empty() {
    return Stream::empty;
  }

  static Body of(Supplier<Stream<? extends RowWithAmounts>> rowWithAmounts) {
    return rowWithAmounts::get;
  }

  static Body of(RowWithAmounts rowWithAmounts) {
    return () -> Stream.of(rowWithAmounts);
  }

  default Body add(RowWithAmounts rowWithAmounts) {
    return () -> Stream.concat(this.stream(), Stream.of(rowWithAmounts));
  }

  Stream<? extends RowWithAmounts> stream();

  default RowWithAmounts aggregate() {
    return new RowWithAmounts() {
      @Override
      public Supplier<Stream<Month>> months() {
        return stream().findAny().map(RowWithAmounts::months).orElse(Stream::empty);
      }

      @Override
      public Optional<Currency> getMonthlyAmount(Column column) {
        return Optional.of(
            stream()
                .map(row -> row.getMonthlyAmount(column))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(Currency.zero(), Currency::add));
      }
    };
  }

  default Body concat(Body body) {
    return Body.of(() -> Stream.concat(this.stream(), body.stream()));
  }

  default Body negate() {
    var base = this;
    return () -> base.stream().map(RowWithAmounts::negate);
  }
}
