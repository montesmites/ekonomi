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
import se.montesmites.ekonomi.report.Section;

public class ReportBuilder {

  private final AmountFetcher amountFetcher;
  private final java.time.Year year;

  public ReportBuilder(AmountFetcher amountFetcher, Year year) {
    this.amountFetcher = amountFetcher;
    this.year = year;
  }

  public Section buildSection(String title, List<AccountGroup> accountGroups) {
    return section()
        .header(header -> header.title(title).months())
        .body(body -> body.accountGroups(accountGroups))
        .footer(FooterBuilder::aggregateBody)
        .section();
  }

  public Section buildSectionWithAcculumatingFooter(String title, AccountGroup accountGroup) {
    return section()
        .header(header -> header.title(title).months())
        .body(body -> body.accountGroups(List.of(accountGroup)).dematerialize())
        .footer(footer -> footer.accumulateBody(initialBalance(accountGroup)))
        .section();
  }

  private Currency initialBalance(AccountGroup accountGroup) {
    return balance(year, AccountFilterByRegex.of(accountGroup));
  }

  private Currency balance(Year year, Predicate<AccountId> filter) {
    return amountFetcher
        .streamAccountIds(year, filter)
        .map(this::balance)
        .reduce(Currency.zero(), Currency::add);
  }

  private Currency balance(AccountId accountId) {
    return amountFetcher.fetchBalance(accountId).map(Balance::getBalance).orElse(Currency.zero());
  }

  public SectionBuilder section() {
    return new SectionBuilder(year, amountFetcher);
  }
}
