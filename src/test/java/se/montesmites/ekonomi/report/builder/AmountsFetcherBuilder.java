package se.montesmites.ekonomi.report.builder;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.report.AmountsFetcher;
import se.montesmites.ekonomi.report.AmountsProvider;

public class AmountsFetcherBuilder {

  private Map<Year, List<AccountId>> accountIds = Map.of();
  private Map<AccountId, Optional<Balance>> balances = Map.of();
  private BiFunction<AccountId, YearMonth, Optional<Currency>> amounts =
      (__, ___) -> Optional.empty();
  private Map<Year, Set<Month>> touchedMonths = Map.of();

  public static AmountsFetcherBuilder empty() {
    return new AmountsFetcherBuilder();
  }

  public static AmountsFetcherBuilder of(Map<AccountId, AmountsProvider> amountsProviders) {
    var year = Year.now();
    return new AmountsFetcherBuilder()
        .accountIds(Map.of(year, new ArrayList<>(amountsProviders.keySet())))
        .amounts(
            (accountId, yearMonth) ->
                amountsProviders
                    .getOrDefault(accountId, AmountsProvider.empty())
                    .getMonthlyAmount(yearMonth.getMonth())
                    .map(Currency::negate))
        .touchedMonths(Map.of(year, EnumSet.allOf(Month.class)));
  }

  private AmountsFetcherBuilder accountIds(Map<Year, List<AccountId>> accountIds) {
    this.accountIds = accountIds;
    return this;
  }

  AmountsFetcherBuilder balances(Map<AccountId, Optional<Balance>> balances) {
    this.balances = balances;
    return this;
  }

  private AmountsFetcherBuilder amounts(
      BiFunction<AccountId, YearMonth, Optional<Currency>> amounts) {
    this.amounts = amounts;
    return this;
  }

  AmountsFetcherBuilder touchedMonths(Map<Year, Set<Month>> touchedMonths) {
    this.touchedMonths = touchedMonths;
    return this;
  }

  public AmountsFetcher amountsFetcher() {
    return new AmountsFetcher() {
      @Override
      public Optional<Currency> fetchAmount(AccountId accountId, YearMonth yearMonth) {
        return amounts.apply(accountId, yearMonth);
      }

      @Override
      public Optional<Balance> fetchBalance(AccountId accountId) {
        return balances.getOrDefault(accountId, Optional.empty());
      }

      @Override
      public Stream<AccountId> streamAccountIds(Year year, Predicate<AccountId> filter) {
        return accountIds.getOrDefault(year, List.of()).stream().filter(filter);
      }

      @Override
      public Set<Month> touchedMonths(Year year) {
        return touchedMonths.getOrDefault(year, Set.of());
      }
    };
  }
}
