package se.montesmites.ekonomi.report;

import java.time.Month;
import java.util.List;
import java.util.Optional;
import se.montesmites.ekonomi.db.model.Amount;

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

  static AmountsProvider of(String description, List<? extends AmountsProvider> amountsProviders) {
    return new AmountsProvider() {
      @Override
      public Optional<Amount> getMonthlyAmount(Month month) {
        var amounts =
            amountsProviders.stream()
                .map(row -> row.getMonthlyAmount(month))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        var sum = amounts.stream().reduce(Amount.ZERO, Amount::add);
        return amounts.isEmpty() ? Optional.empty() : Optional.of(sum);
      }

      @Override
      public String formatDescription() {
        return description;
      }
    };
  }
}
