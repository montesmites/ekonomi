package se.montesmites.ekonomi.report.builder;

import static java.util.stream.Collectors.toList;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.report.AccountFilterByRegex;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountFetcher;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Section;

public class ReportBuilder {

  private final AmountFetcher amountFetcher;
  private final java.time.Year year;
  private final List<SectionBuilder> sections;

  public ReportBuilder(AmountFetcher amountFetcher, Year year) {
    this.amountFetcher = amountFetcher;
    this.year = year;
    this.sections = new ArrayList<>();
  }

  @Deprecated(forRemoval = true)
  public Section buildSection(String title, List<AccountGroup> accountGroups) {
    return section()
        .header(header -> header.title(title).months())
        .body(body -> body.accountGroups(accountGroups))
        .footer(FooterBuilder::aggregateBody)
        .section();
  }

  @Deprecated(forRemoval = true)
  public Section buildSectionWithAcculumatingFooter(String title, AccountGroup accountGroup) {
    return section()
        .header(header -> header.title(title).months())
        .body(body -> body.accountGroups(List.of(accountGroup)).dematerialize())
        .footer(footer -> footer.accumulateBody(initialBalance(accountGroup)))
        .section();
  }

  @Deprecated(forRemoval = true)
  private Currency initialBalance(AccountGroup accountGroup) {
    return balance(year, AccountFilterByRegex.of(accountGroup));
  }

  @Deprecated(forRemoval = true)
  private Currency balance(Year year, Predicate<AccountId> filter) {
    return amountFetcher
        .streamAccountIds(year, filter)
        .map(this::balance)
        .reduce(Currency.zero(), Currency::add);
  }

  @Deprecated(forRemoval = true)
  private Currency balance(AccountId accountId) {
    return amountFetcher.fetchBalance(accountId).map(Balance::getBalance).orElse(Currency.zero());
  }

  @Deprecated(forRemoval = true)
  public SectionBuilder section() {
    return new SectionBuilder(year, amountFetcher);
  }

  public ReportBuilder section(UnaryOperator<SectionBuilder> section) {
    var sectionBuilder = new SectionBuilder(year, amountFetcher);
    this.sections.add(sectionBuilder);
    section.apply(sectionBuilder);
    return this;
  }

  ReportBuilder subtotal(String description) {
    var sectionBuilder = new SectionBuilder(year, amountFetcher);
    this.sections.add(sectionBuilder);
    var aggregates =
        List.copyOf(this.sections)
            .stream()
            .map(SectionBuilder::getBodyBuilder)
            .filter(BodyBuilder::isMaterialized)
            .map(BodyBuilder::body)
            .map(body -> body.aggregate(""))
            .collect(toList());
    sectionBuilder.footer(
        footer ->
            footer.add(
                AmountsProvider.of(
                    description,
                    month ->
                        aggregates
                            .stream()
                            .map(
                                amountsProvider ->
                                    amountsProvider
                                        .getMonthlyAmount(month)
                                        .orElse(Currency.zero()))
                            .reduce(Currency::add))
                    .asRow()));
    return this;
  }

  List<Section> getSections() {
    return sections.stream().map(SectionBuilder::section).collect(toList());
  }
}
