package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;

public interface AmountsFetcher {

  static AmountsFetcher empty() {
    return new AmountsFetcher() {
      @Override
      public Optional<Currency> fetchAmount(AccountId accountId, YearMonth yearMonth) {
        return Optional.empty();
      }

      @Override
      public Optional<Balance> fetchBalance(AccountId accountId) {
        return Optional.empty();
      }

      @Override
      public Stream<AccountId> streamAccountIds(Year year, Predicate<AccountId> filter) {
        return Stream.empty();
      }

      @Override
      public Set<Month> touchedMonths(Year year) {
        return Set.of();
      }
    };
  }

  Optional<Currency> fetchAmount(AccountId accountId, YearMonth yearMonth);

  Optional<Balance> fetchBalance(AccountId accountId);

  Stream<AccountId> streamAccountIds(java.time.Year year, Predicate<AccountId> filter);

  Set<Month> touchedMonths(java.time.Year year);
}
