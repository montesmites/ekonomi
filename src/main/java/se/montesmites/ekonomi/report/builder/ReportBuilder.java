package se.montesmites.ekonomi.report.builder;

import static java.util.stream.Collectors.toList;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.report.AccountFilterByRegex;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountFetcher;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.CashflowReport;
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

  public ReportBuilder accountGroups(String title, List<AccountGroup> accountGroups) {
    var sectionBuilder = new SectionBuilder(year, amountFetcher);
    this.sections.add(sectionBuilder);
    sectionBuilder
        .header(header -> header.title(title).months())
        .body(body -> body.accountGroups(accountGroups))
        .footer(FooterBuilder::aggregateBody);
    return this;
  }

  public ReportBuilder accumulateAccountGroups(String title, List<AccountGroup> accountGroups) {
    var sectionBuilder = new SectionBuilder(year, amountFetcher);
    this.sections.add(sectionBuilder);
    sectionBuilder
        .header(header -> header.title(title).months())
        .body(body -> body.accountGroups(accountGroups).dematerialize())
        .footer(
            footer ->
                footer.accumulateBody(
                    accountGroups
                        .stream()
                        .map(
                            accountGroup ->
                                amountFetcher
                                    .streamAccountIds(year, AccountFilterByRegex.of(accountGroup))
                                    .map(
                                        accountId ->
                                            amountFetcher
                                                .fetchBalance(accountId)
                                                .map(Balance::getBalance)
                                                .orElse(Currency.zero()))
                                    .reduce(Currency.zero(), Currency::add))
                        .reduce(Currency::add)
                        .orElse(Currency.zero())))
        .section();
    return this;
  }

  public ReportBuilder section(UnaryOperator<SectionBuilder> section) {
    var sectionBuilder = new SectionBuilder(year, amountFetcher);
    this.sections.add(sectionBuilder);
    section.apply(sectionBuilder);
    return this;
  }

  public ReportBuilder subtotal(String description) {
    var sectionBuilder = new SectionBuilder(year, amountFetcher);
    this.sections.add(sectionBuilder);
    var aggregates =
        List.copyOf(this.sections)
            .stream()
            .map(SectionBuilder::getBodyBuilder)
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
                            .map(amountsProvider -> amountsProvider.getMonthlyAmount(month))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .reduce(Currency::add))
                    .asRow()));
    return this;
  }

  List<Section> getSections() {
    return sections.stream().map(SectionBuilder::section).collect(toList());
  }

  public CashflowReport report() {
    return new CashflowReport(() -> getSections().stream());
  }
}
