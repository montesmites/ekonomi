package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toList;
import static se.montesmites.ekonomi.report.HeaderRow.SHORT_MONTHS_HEADER;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;

public class ReportBuilder {

  private final CashflowDataFetcher fetcher;
  private final java.time.Year year;

  ReportBuilder(CashflowDataFetcher fetcher, Year year) {
    this.fetcher = fetcher;
    this.year = year;
  }

  public RowWithAmounts buildRowWithAmounts(AccountGroup accountGroup) {
    var accountIds =
        fetcher
            .streamAccountIds(year, AccountFilterByRegex.of(accountGroup.regex()))
            .collect(toList());
    var row = new RowWithAmounts() {
      @Override
      public Supplier<Stream<Month>> months() {
        return () -> fetcher.touchedMonths(year).stream().sorted();
      }

      @Override
      public String formatDescription() {
        return accountGroup.description();
      }

      @Override
      public Currency getMonthlyAmount(Column column) {
        return accountIds
            .stream()
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
            .map(Currency::negate)
            .orElse(new Currency(0));
      }
    };
    return accountGroup.postProcessor().apply(row);
  }

  public Section buildSection(String title, List<AccountGroup> accountGroups) {
    var header = Header.of(() -> title).add(SHORT_MONTHS_HEADER);
    var body =
        Body.of(
            () ->
                accountGroups
                    .stream()
                    .map(this::buildRowWithAmounts));
    var footer = Footer.of(body.aggregate());
    return Section.of(header, body, footer);
  }
}
