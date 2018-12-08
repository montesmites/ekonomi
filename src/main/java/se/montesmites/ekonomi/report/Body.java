package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toList;

import java.time.Month;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.Currency;

public interface Body {

  static Body empty() {
    return Stream::empty;
  }

  static Body of(Supplier<Stream<? extends AmountsProvider>> rowWithAmounts) {
    return rowWithAmounts::get;
  }

  static Body of(AmountsProvider rowWithAmounts) {
    return () -> Stream.of(rowWithAmounts);
  }

  default Body add(AmountsProvider rowWithAmounts) {
    return () -> Stream.concat(this.stream(), Stream.of(rowWithAmounts));
  }

  Stream<? extends AmountsProvider> stream();

  default AmountsProvider aggregate(String description) {
    return new AmountsProvider() {
      @Override
      public Optional<Currency> getMonthlyAmount(Month month) {
        var amounts =
            Body.this.stream()
                .map(row -> row.getMonthlyAmount(month))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
        var sum = amounts.stream().reduce(Currency.zero(), Currency::add);
        return amounts.isEmpty() ? Optional.empty() : Optional.of(sum);
      }

      @Override
      public String formatDescription() {
        return description;
      }
    };
  }

  default Body concat(Body body) {
    return Body.of(() -> Stream.concat(this.stream(), body.stream()));
  }

  default Body negate() {
    var base = this;
    return () -> base.stream().map(AmountsProvider::negate);
  }
}
