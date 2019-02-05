package se.montesmites.ekonomi.report.builder;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.report.AccountDescriptor;
import se.montesmites.ekonomi.report.AccountFilterByRegex;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AccountsFetcher;
import se.montesmites.ekonomi.report.Aggregate;
import se.montesmites.ekonomi.report.AmountsFetcher;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Report;
import se.montesmites.ekonomi.report.Section;
import se.montesmites.ekonomi.report.Tag;
import se.montesmites.ekonomi.report.TagFilter;

public class ReportBuilder {

  private final AccountsFetcher accountsFetcher;
  private final AmountsFetcher amountsFetcher;
  private final java.time.Year year;
  private final List<SectionBuilder> sections;
  private Set<Tag> tags = Set.of();

  ReportBuilder(AmountsFetcher amountsFetcher, Year year) {
    this(AccountsFetcher.empty(), amountsFetcher, year);
  }

  public ReportBuilder(AccountsFetcher accountsFetcher, AmountsFetcher amountsFetcher, Year year) {
    this.accountsFetcher = accountsFetcher;
    this.amountsFetcher = amountsFetcher;
    this.year = year;
    this.sections = new ArrayList<>();
  }

  public ReportBuilder accountGroups(String title, List<AccountGroup> accountGroups) {
    var sectionBuilder = sectionBuilder();
    this.sections.add(sectionBuilder);
    sectionBuilder
        .header(header -> header.title(title).months())
        .body(body -> body.accountGroups(accountGroups))
        .footer(FooterBuilder::aggregateBody);
    return this;
  }

  public ReportBuilder accounts(
      String title, String regex, UnaryOperator<AmountsProvider> amountsProviderProcessor) {
    var sectionBuilder = sectionBuilder();
    this.sections.add(sectionBuilder);
    var accounts =
        amountsFetcher
            .streamAccountIds(year, AccountFilterByRegex.of(regex))
            .sorted(comparing(AccountId::getId))
            .map(accountsFetcher::getAccount)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(toList());
    sectionBuilder
        .header(header -> header.title(title).months())
        .body(
            body ->
                body.accounts(
                    bodyFromAccounts ->
                        bodyFromAccounts
                            .accounts(accounts)
                            .accountDescriptor(
                                AccountDescriptor.accountIdConcatAccountDescriptionWithMaxLength(
                                    Report.DESCRIPTION_WIDTH))
                            .amountsProviderProcessor(amountsProviderProcessor)))
        .footer(FooterBuilder::aggregateBody);
    return this;
  }

  public ReportBuilder accumulateAccountGroups(String title, List<AccountGroup> accountGroups) {
    var sectionBuilder = sectionBuilder();
    this.sections.add(sectionBuilder);
    var amountsProviders =
        accountGroups
            .stream()
            .map(accountGroup -> AmountsProvider.of(amountsFetcher, year, accountGroup))
            .collect(toList());
    var aggregate = Aggregate.of(amountsProviders);
    var initialBalance =
        accountGroups
            .stream()
            .map(
                accountGroup ->
                    amountsFetcher
                        .streamAccountIds(year, AccountFilterByRegex.of(accountGroup))
                        .map(
                            accountId ->
                                amountsFetcher
                                    .fetchBalance(accountId)
                                    .map(Balance::getBalance)
                                    .orElse(Currency.zero()))
                        .reduce(Currency.zero(), Currency::add))
            .reduce(Currency::add)
            .orElse(Currency.zero());
    sectionBuilder
        .header(header -> header.title(title).months())
        .footer(footer -> footer.add(aggregate.accumulate(initialBalance).asRow()))
        .section();
    return this;
  }

  public ReportBuilder tags(Set<Tag> tags) {
    this.tags = Set.copyOf(tags);
    return this;
  }

  public ReportBuilder section(UnaryOperator<SectionBuilder> section) {
    var sectionBuilder = sectionBuilder();
    this.sections.add(sectionBuilder);
    section.apply(sectionBuilder);
    return this;
  }

  public ReportBuilder subtotal(String description, TagFilter tagFilter) {
    var sectionBuilder = sectionBuilder();
    this.sections.add(sectionBuilder);
    var aggregates =
        List.copyOf(this.sections)
            .stream()
            .filter(section -> tagFilter.test(section.getTags()))
            .map(SectionBuilder::getBodyBuilder)
            .map(BodyBuilder::body)
            .map(Aggregate::of)
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

  public Report report() {
    return new Report(() -> getSections().stream());
  }

  private SectionBuilder sectionBuilder() {
    return new SectionBuilder(year, amountsFetcher).tags(this.tags);
  }
}
