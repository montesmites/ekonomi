package se.montesmites.ekonomi.report.builder;

import static java.util.stream.Collectors.toList;
import static se.montesmites.ekonomi.report.builder.SectionBuilder.headerBuilder;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.report.AccountFilterByRegex;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.CashflowDataFetcher;
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Section;

public class ReportBuilder {

  private final CashflowDataFetcher fetcher;
  private final java.time.Year year;

  public ReportBuilder(CashflowDataFetcher fetcher, Year year) {
    this.fetcher = fetcher;
    this.year = year;
  }

  public AmountsProvider buildAmountsProvider(AccountGroup accountGroup) {
    var accountIds =
        fetcher
            .streamAccountIds(year, AccountFilterByRegex.of(accountGroup.regex()))
            .collect(toList());
    var row =
        new AmountsProvider() {
          @Override
          public Optional<Currency> getMonthlyAmount(Month month) {
            var yearMonth = YearMonth.of(year.getValue(), month);
            var year = Year.of(yearMonth.getYear());
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

          @Override
          public String formatDescription() {
            return accountGroup.description();
          }
        };
    return accountGroup.postProcessor().apply(row);
  }

  public Section buildSection(String title, List<AccountGroup> accountGroups) {
    return section()
        .header(headerBuilder().title(title).months())
        .body(Body.of(() -> accountGroups.stream().map(this::buildAmountsProvider)))
        .footer(
            Footer.of(
                Body.of(() -> accountGroups.stream().map(this::buildAmountsProvider))
                    .aggregate("")
                    .asRow()))
        .section();
  }

  public Section buildSectionWithAcculumatingFooter(String title, AccountGroup accountGroup) {
    return section()
        .header(headerBuilder().title(title).months())
        .footer(
            Footer.of(
                this.buildAmountsProvider(accountGroup)
                    .accumulate(initialBalance(accountGroup))
                    .asRow()))
        .section();
  }

  private Currency initialBalance(AccountGroup accountGroup) {
    return balance(year, AccountFilterByRegex.of(accountGroup));
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

  public SectionBuilder section() {
    return new SectionBuilder();
  }
}
