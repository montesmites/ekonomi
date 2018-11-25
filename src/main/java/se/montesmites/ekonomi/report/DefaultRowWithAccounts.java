package se.montesmites.ekonomi.report;

import java.time.Month;
import java.time.YearMonth;
import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;

public class DefaultRowWithAccounts implements RowWithAmounts {

  private final CashflowDataFetcher fetcher;
  private final Supplier<Stream<AccountId>> accountIds;
  private final java.time.Year year;
  private final String description;

  public DefaultRowWithAccounts(
      CashflowDataFetcher fetcher,
      Supplier<Stream<AccountId>> accountIds,
      java.time.Year year,
      String description) {
    this.fetcher = fetcher;
    this.accountIds = accountIds;
    this.year = year;
    this.description = description;
  }

  private String getDescription() {
    return description;
  }

  @Override
  public Supplier<Stream<Month>> months() {
    return () -> fetcher.touchedMonths(year).stream();
  }

  @Override
  public String formatDescription() {
    return getDescription();
  }

  @Override
  public Currency getMonthlyAmount(Column column) {
    return accountIds
        .get()
        .map(acc -> getMonthlyAmount(acc, column.getMonth().get()))
        .reduce(new Currency(0), Currency::add);
  }

  private Currency getMonthlyAmount(AccountId accountId, Month month) {
    return getMonthlyAmount(accountId, YearMonth.of(year.getValue(), month));
  }

  private Currency getMonthlyAmount(AccountId accountId, YearMonth yearMonth) {
    return fetcher
        .fetchAmount(accountId, yearMonth)
        .map(Currency::getAmount)
        .map(Currency::new)
        .map(Signedness.NEGATED_SIGN::apply)
        .orElse(new Currency(0));
  }
}
