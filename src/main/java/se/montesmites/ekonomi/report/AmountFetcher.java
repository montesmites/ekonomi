package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.YearMonth;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;

public interface AmountFetcher {

  Optional<Currency> fetchAmount(AccountId accountId, YearMonth yearMonth);

  Optional<Balance> fetchBalance(AccountId accountId);

  Stream<AccountId> streamAccountIds(java.time.Year year, Predicate<AccountId> filter);

  Set<Month> touchedMonths(java.time.Year year);
}
