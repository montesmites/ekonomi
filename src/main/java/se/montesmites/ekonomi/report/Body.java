package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.Currency;

public interface Body {

  static Body empty() {
    return List::of;
  }

  static Body of(Supplier<Stream<? extends AmountsProvider>> rowWithAmounts) {
    return () -> rowWithAmounts.get().collect(toList());
  }

  static Body of(AmountsProvider rowWithAmounts) {
    return () -> List.of(rowWithAmounts);
  }

  static Body of(List<AmountsProvider> amountsProviders) {
    return () -> List.copyOf(amountsProviders);
  }

  default Body add(AmountsProvider rowWithAmounts) {
    return () -> Stream.concat(this.stream(), Stream.of(rowWithAmounts)).collect(toList());
  }

  default Stream<? extends AmountsProvider> stream() {
    return rows().stream();
  }

  List<? extends AmountsProvider> rows();

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

  default Body negate() {
    var base = this;
    return () -> base.stream().map(AmountsProvider::negate).collect(toList());
  }

  default String asString(String delimiter) {
    return stream()
        .map(AmountsProvider::asRow)
        .map(Row::asExtendedString)
        .collect(joining(delimiter));
  }
}
