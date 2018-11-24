package se.montesmites.ekonomi.report;

import static se.montesmites.ekonomi.report.Column.DESCRIPTION;

import java.time.Month;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;

public class AccumulatingNegatedRow implements RowWithAccounts {

  private final CashflowDataFetcher fetcher;
  private final Supplier<Stream<AccountId>> accountIds;
  private final java.time.Year year;
  private final RowWithAccounts monthlyNetAmounts;
  private final Map<Column, Currency> amounts;

  public AccumulatingNegatedRow(
      CashflowDataFetcher fetcher, Supplier<Stream<AccountId>> accountIds, java.time.Year year) {
    this.fetcher = fetcher;
    this.accountIds = accountIds;
    this.year = year;
    this.monthlyNetAmounts =
        new DefaultRowWithAccountsWithNegatedAmounts(
            new DefaultRowWithAccounts(fetcher, accountIds, year, ""));
    this.amounts = getAmounts();
  }

  @Override
  public String formatDescription() {
    return getBalance().format();
  }

  @Override
  public Supplier<Stream<AccountId>> getAccountIds() {
    return accountIds;
  }

  @Override
  public Currency getMonthlyAmount(Column column) {
    return amounts.getOrDefault(column, new Currency(0));
  }

  @Override
  public Currency getYearlyTotal() {
    return new Currency(0);
  }

  @Override
  public Supplier<Stream<Month>> months() {
    return () -> fetcher.touchedMonths(year).stream();
  }

  public Currency getBalance() {
    return amounts.get(DESCRIPTION);
  }

  private Map<Column, Currency> getAmounts() {
    final Set<Month> months = fetcher.touchedMonths(year);
    Map<Column, Currency> map = new EnumMap<>(Column.class);
    Currency accumulator = new Currency(0);
    for (Column column : Column.values()) {
      if (!column.getMonth().isPresent() || months.contains(column.getMonth().get())) {
        Currency net = columnNetAmount(column);
        Currency columnBalance = accumulator.add(net);
        map.put(column, columnBalance);
        accumulator = columnBalance;
      }
    }
    return map;
  }

  private Currency columnNetAmount(Column column) {
    switch (column) {
      case DESCRIPTION:
        return balance();
      case TOTAL:
        return new Currency(0);
      case AVERAGE:
        return new Currency(0);
      default:
        return monthlyNetAmounts.getMonthlyAmount(column);
    }
  }

  private Currency balance() {
    return accountIds.get().map(this::balance).reduce(new Currency(0), Currency::add);
  }

  private Currency balance(AccountId accountId) {
    return fetcher.fetchBalance(accountId).map(Balance::getBalance).orElse(new Currency(0));
  }
}
