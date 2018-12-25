package se.montesmites.ekonomi.report.builder;

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
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.Row;
import se.montesmites.ekonomi.report.Section;

public class ReportBuilder {

  private final AmountFetcher fetcher;
  private final java.time.Year year;

  public ReportBuilder(AmountFetcher fetcher, Year year) {
    this.fetcher = fetcher;
    this.year = year;
  }

  private HeaderBuilder headerBuilder() {
    return new HeaderBuilder();
  }

  private BodyBuilder bodyBuilder() {
    return new BodyBuilder(year, fetcher);
  }

  @Deprecated(forRemoval = true)
  public AmountsProvider buildAmountsProvider(AccountGroup accountGroup) {
    var bodyBuilder = new BodyBuilder(year, fetcher);
    return bodyBuilder.buildAmountsProvider(accountGroup);
  }

  @Deprecated(forRemoval = true)
  public Section footerOnly(Row footer) {
    return Section.of(Header.empty(), Body.empty(), Footer.of(footer));
  }

  public Section buildSection(String title, List<AccountGroup> accountGroups) {
    var bodyBuilder = bodyBuilder().accountGroups(accountGroups);
    return section()
        .header(headerBuilder().title(title).months())
        .body(bodyBuilder)
        .footer(new FooterBuilder(bodyBuilder::body).aggregateBody())
        .section();
  }

  public Section buildSectionWithAcculumatingFooter(String title, AccountGroup accountGroup) {
    var bodyBuilder = bodyBuilder().accountGroups(List.of(accountGroup)).dematerialize();
    return section()
        .header(headerBuilder().title(title).months())
        .body(bodyBuilder)
        .footer(new FooterBuilder(bodyBuilder::body).accumulateBody(initialBalance(accountGroup)))
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
