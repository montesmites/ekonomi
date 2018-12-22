package se.montesmites.ekonomi.report.builder;

import static se.montesmites.ekonomi.report.builder.SectionBuilder.headerBuilder;

import java.time.Year;
import java.util.List;
import java.util.function.Predicate;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.report.AccountFilterByRegex;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountFetcher;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Section;

public class ReportBuilder {

  private final AmountFetcher fetcher;
  private final java.time.Year year;

  public ReportBuilder(AmountFetcher fetcher, Year year) {
    this.fetcher = fetcher;
    this.year = year;
  }

  @Deprecated(forRemoval = true)
  public AmountsProvider buildAmountsProvider(AccountGroup accountGroup) {
    var bodyBuilder = new BodyBuilder(year, fetcher);
    return bodyBuilder.buildAmountsProvider(accountGroup);
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
