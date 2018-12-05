package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toList;
import static se.montesmites.ekonomi.report.HeaderRow.SHORT_MONTHS_HEADER;

import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;

public class ReportBuilder {

  private final CashflowDataFetcher fetcher;
  private final java.time.Year year;

  public ReportBuilder(CashflowDataFetcher fetcher, Year year) {
    this.fetcher = fetcher;
    this.year = year;
  }

  RowWithAmounts buildRowWithAmounts(AccountGroup accountGroup) {
    var accountIds =
        fetcher
            .streamAccountIds(year, AccountFilterByRegex.of(accountGroup.regex()))
            .collect(toList());
    var row =
        new RowWithAmounts() {
          @Override
          public String formatDescription() {
            return accountGroup.description();
          }

          @Override
          public Optional<Currency> getMonthlyAmount(Column column) {
            var yearMonth = YearMonth.of(year.getValue(), column.getMonth().orElseThrow());
            var year = Year.of(yearMonth.getYear());
            var month = yearMonth.getMonth();
            var sum =
                (Supplier<Currency>)
                    () ->
                        accountIds
                            .stream()
                            .map(
                                accountId ->
                                    fetcher
                                        .fetchAmount(accountId, yearMonth)
                                        .map(Currency::getAmount)
                                        .map(Currency::of)
                                        .map(Currency::negate)
                                        .orElse(Currency.zero()))
                            .reduce(Currency.zero(), Currency::add);
            return fetcher.touchedMonths(year).contains(month)
                ? Optional.of(sum.get())
                : Optional.empty();
          }
        };
    return accountGroup.postProcessor().apply(row);
  }

  public Section buildSection(String title, List<AccountGroup> accountGroups) {
    var header = Header.of(() -> title).add(SHORT_MONTHS_HEADER);
    var body = Body.of(() -> accountGroups.stream().map(this::buildRowWithAmounts));
    var footer = Footer.of(body.aggregate());
    return Section.of(header, body, footer);
  }

  public Section buildSectionWithAcculumatingFooter(String title, AccountGroup accountGroup) {
    var header = Header.of(() -> title).add(SHORT_MONTHS_HEADER);
    var footer =
        Footer.of(
            this.buildRowWithAmounts(accountGroup)
                .accumulate(balance(year, AccountFilterByRegex.of(accountGroup))));
    return Section.of(header, Body.empty(), footer);
  }

  public Section buildSection(Row footer) {
    return Section.of(Header.empty(), Body.empty(), Footer.of(footer));
  }

  private Currency balance(Year year, Predicate<AccountId> filter) {
    return fetcher
        .streamAccountIds(year, filter)
        .map(this::balance)
        .reduce(Currency.zero(), Currency::add);
  }

  private Currency balance(AccountId accountId) {
    return fetcher.fetchBalance(accountId).map(Balance::getBalance).orElse(Currency.zero());
  }
}
