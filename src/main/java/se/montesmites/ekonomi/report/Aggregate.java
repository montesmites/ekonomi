package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toList;

import java.time.Month;
import java.util.List;
import java.util.Optional;
import se.montesmites.ekonomi.model.Currency;

public interface Aggregate {

  static AmountsProvider of(Body body) {
    return Aggregate.of(body.rows());
  }

  static AmountsProvider of(String description, Body body) {
    return Aggregate.of(description, body.rows());
  }

  static AmountsProvider of(List<? extends AmountsProvider> amountsProviders) {
    return Aggregate.of("", amountsProviders);
  }

  static AmountsProvider of(
      String description, List<? extends AmountsProvider> amountsProviders) {
    return new AmountsProvider() {
      @Override
      public Optional<Currency> getMonthlyAmount(Month month) {
        var amounts =
            amountsProviders
                .stream()
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
}
