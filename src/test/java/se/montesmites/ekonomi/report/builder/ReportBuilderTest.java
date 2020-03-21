package se.montesmites.ekonomi.report.builder;

import static java.util.Map.entry;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.montesmites.ekonomi.report.Column.APRIL;
import static se.montesmites.ekonomi.report.Column.AUGUST;
import static se.montesmites.ekonomi.report.Column.AVERAGE;
import static se.montesmites.ekonomi.report.Column.DECEMBER;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;
import static se.montesmites.ekonomi.report.Column.FEBRUARY;
import static se.montesmites.ekonomi.report.Column.JANUARY;
import static se.montesmites.ekonomi.report.Column.JULY;
import static se.montesmites.ekonomi.report.Column.JUNE;
import static se.montesmites.ekonomi.report.Column.MARCH;
import static se.montesmites.ekonomi.report.Column.MAY;
import static se.montesmites.ekonomi.report.Column.NOVEMBER;
import static se.montesmites.ekonomi.report.Column.OCTOBER;
import static se.montesmites.ekonomi.report.Column.SEPTEMBER;
import static se.montesmites.ekonomi.report.Column.TOTAL;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.AccountStatus;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.Aggregate;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.Report;
import se.montesmites.ekonomi.report.Row;
import se.montesmites.ekonomi.report.Section;
import se.montesmites.ekonomi.report.Tag;
import se.montesmites.ekonomi.report.TagFilter;

class ReportBuilderTest {

  private final Year year = Year.now();
  private final YearId yearId = new YearId(year.toString());

  @Test
  void accountGroups() {
    var row1 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 100)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(Map.ofEntries(entry(new AccountId(yearId, "1111"), row1)))
            .amountsFetcher();
    var accountGroups = List.of(AccountGroup.of("", "\\d\\d\\d\\d"));
    var title = "title";
    var header = Header.of(Row.title(title)).add(Row.descriptionWithMonths("", Row.SHORT_MONTHS));
    var body = Body.of(row1);
    var footer = Footer.of(Aggregate.of(body).asRow());
    var section = Section.of(header, body, footer);
    var exp = List.of(section).stream().map(Section::asString).collect(toList());
    var act =
        new ReportBuilder(amountsFetcher, year)
            .accountGroups(title, accountGroups)
            .getSections()
            .stream()
            .map(Section::asString)
            .collect(toList());
    Assertions.assertEquals(exp, act);
  }

  @Test
  void accounts() {
    var year = Year.now();
    var yearId = new YearId(year.toString());
    var description1 = "1".repeat(Report.DESCRIPTION_WIDTH - 5);
    var description2 = "2".repeat(Report.DESCRIPTION_WIDTH - 5);
    var row1 =
        AmountsProvider.of(
            "1111 " + description1, month -> Optional.of(new Currency(month.ordinal() * 100)));
    var row2 =
        AmountsProvider.of(
            "2222 " + description2, month -> Optional.of(new Currency(month.ordinal() * 200)));
    var accountId1 = new AccountId(yearId, "1111");
    var accountId2 = new AccountId(yearId, "2222");
    var account1 =
        new Account(
            accountId1,
            description1 + " " + description1.repeat(Report.DESCRIPTION_WIDTH * 2),
            AccountStatus.OPEN);
    var account2 =
        new Account(
            accountId2,
            description2 + " " + description2.repeat(Report.DESCRIPTION_WIDTH * 2),
            AccountStatus.OPEN);
    var accounts =
        Map.ofEntries(
            entry(accountId1, Optional.of(account1)), entry(accountId2, Optional.of(account2)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(Map.ofEntries(entry(accountId1, row1), entry(accountId2, row2)))
            .amountsFetcher();
    var title = "title";
    var reportBuilder =
        new ReportBuilder(accounts::get, amountsFetcher, year)
            .accounts(title, "\\d\\d\\d\\d", AmountsProvider::self);
    var header = Header.of(Row.title(title)).add(Row.descriptionWithMonths("", Row.SHORT_MONTHS));
    var body = Body.of(List.of(row1, row2));
    var footer = Footer.of(Aggregate.of(body).asRow());
    var exp =
        List.of(Section.of(header, body, footer)).stream().map(Section::asString).collect(toList());
    var act =
        reportBuilder
            .getSections()
            .stream()
            .map(section -> section.asString("\n"))
            .collect(toList());
    assertEquals(exp, act);
  }

  @Test
  void accumulateAccountGroups() {
    var row1 = AmountsProvider.of(month -> Optional.of(new Currency(month.getValue() * 100)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(Map.ofEntries(entry(new AccountId(yearId, "1111"), row1)))
            .amountsFetcher();
    var accountGroups = List.of(AccountGroup.of("", "\\d\\d\\d\\d"));
    var title = "title";
    var header = Header.of(Row.title(title)).add(Row.descriptionWithMonths("", Row.SHORT_MONTHS));
    var footer =
        (Row)
            column ->
                Map.ofEntries(
                    entry(DESCRIPTION, new Currency(0).format()),
                    entry(JANUARY, new Currency(100).format()),
                    entry(FEBRUARY, new Currency(300).format()),
                    entry(MARCH, new Currency(600).format()),
                    entry(APRIL, new Currency(1000).format()),
                    entry(MAY, new Currency(1500).format()),
                    entry(JUNE, new Currency(2100).format()),
                    entry(JULY, new Currency(2800).format()),
                    entry(AUGUST, new Currency(3600).format()),
                    entry(SEPTEMBER, new Currency(4500).format()),
                    entry(OCTOBER, new Currency(5500).format()),
                    entry(NOVEMBER, new Currency(6600).format()),
                    entry(DECEMBER, new Currency(7800).format()),
                    entry(TOTAL, new Currency(0).format()),
                    entry(AVERAGE, new Currency(3033).format()))
                    .get(column);
    var section = Section.of(header, Body.empty(), Footer.of(footer));
    var exp = List.of(section).stream().map(Section::asString).collect(toList());
    var act =
        new ReportBuilder(amountsFetcher, year)
            .accumulateAccountGroups(title, accountGroups)
            .getSections()
            .stream()
            .map(Section::asString)
            .collect(toList());
    assertEquals(exp, act);
  }

  @Test
  void tags() {
    var row1 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 200)));
    var row3 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 300)));
    var row4 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 400)));
    var subtotal1 =
        AmountsProvider.of("subtotal1", month -> Optional.of(new Currency(month.ordinal() * 300)));
    var subtotal2 =
        AmountsProvider.of("subtotal2", month -> Optional.of(new Currency(month.ordinal() * 700)));
    var subtotal3 =
        AmountsProvider.of("subtotal3", month -> Optional.of(new Currency(month.ordinal() * 1000)));
    var tag1 = Tag.of("tag1");
    var tag2 = Tag.of("tag2");
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2),
                entry(new AccountId(yearId, "3333"), row3),
                entry(new AccountId(yearId, "4444"), row4)))
            .amountsFetcher();
    var reportBuilder =
        new ReportBuilder(amountsFetcher, year)
            .tags(Set.of(tag1))
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "1111")))))
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "2222")))))
            .tags(Set.of(tag2))
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "3333")))))
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "4444")))))
            .subtotal(sbttl -> sbttl.description("subtotal1").tagFilter(TagFilter.isEqualTo(tag1)))
            .subtotal(sbttl -> sbttl.description("subtotal2").tagFilter(TagFilter.isEqualTo(tag2)))
            .subtotal(sbttl -> sbttl.description("subtotal3"));
    var exp =
        List.of(
            Section.of(Header.empty(), Body.of(row1), Footer.empty()).asString("\n"),
            Section.of(Header.empty(), Body.of(row2), Footer.empty()).asString("\n"),
            Section.of(Header.empty(), Body.of(row3), Footer.empty()).asString("\n"),
            Section.of(Header.empty(), Body.of(row4), Footer.empty()).asString("\n"),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal1.asRow())).asString("\n"),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal2.asRow())).asString("\n"),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal3.asRow())).asString("\n"));
    var act =
        reportBuilder
            .getSections()
            .stream()
            .map(section -> section.asString("\n"))
            .collect(toList());
    assertEquals(exp, act);
  }

  @Test
  void section() {
    var row1 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 200)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountsFetcher();
    var reportBuilder =
        new ReportBuilder(amountsFetcher, year)
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "1111")))))
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "2222")))));
    var exp =
        List.of(
            Section.of(Header.empty(), Body.of(List.of(row1)), Footer.empty()).asString("\n"),
            Section.of(Header.empty(), Body.of(List.of(row2)), Footer.empty()).asString("\n"));
    var act =
        reportBuilder
            .getSections()
            .stream()
            .map(section -> section.asString("\n"))
            .collect(toList());
    assertEquals(exp, act);
  }

  @Test
  void subtotal() {
    var description = "description";
    var row1 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 200)));
    var subtotal =
        AmountsProvider.of(description, month -> Optional.of(new Currency(month.ordinal() * 300)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountsFetcher();
    var reportBuilder =
        new ReportBuilder(amountsFetcher, Year.now())
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "1111")))))
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "2222")))))
            .subtotal(sbttl -> sbttl.description(description));
    var exp =
        List.of(
            Section.of(Header.empty(), Body.of(row1), Footer.empty()),
            Section.of(Header.empty(), Body.of(row2), Footer.empty()),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal.asRow())))
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    var act =
        reportBuilder
            .getSections()
            .stream()
            .map(section -> section.asString("\n"))
            .collect(joining("\n"));
    assertEquals(exp, act);
  }

  @Test
  void report() {
    var description = "description";
    var row1 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(new Currency(month.ordinal() * 200)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountsFetcher();
    var reportBuilder =
        new ReportBuilder(amountsFetcher, Year.now())
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "1111")))))
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "2222")))))
            .subtotal(sbttl -> sbttl.description(description));
    var exp = new Report(() -> reportBuilder.getSections().stream()).render();
    var act = reportBuilder.report().render();
    assertEquals(exp, act);
  }
}
