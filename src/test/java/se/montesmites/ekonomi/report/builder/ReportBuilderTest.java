package se.montesmites.ekonomi.report.builder;

import static java.util.Map.entry;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.AccountStatus;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.Report;
import se.montesmites.ekonomi.report.Row;
import se.montesmites.ekonomi.report.Section;
import se.montesmites.ekonomi.report.TagFilter;

class ReportBuilderTest {

  private final Year year = Year.now();
  private final YearId yearId = new YearId(year.toString());

  @Test
  void accountGroups() {
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var amountsFetcher =
        AmountsFetcherBuilder.of(Map.ofEntries(entry(new AccountId(yearId, "1111"), row1)))
            .amountsFetcher();
    var accountGroups = List.of(AccountGroup.of("", "\\d\\d\\d\\d"));
    var title = "title";
    var header = Header.of(Row.title(title)).add(Row.descriptionWithMonths("", Row.SHORT_MONTHS));
    var body = Body.of(row1);
    var footer = Footer.of(body.aggregate("").asRow());
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
    var description1 = "1111 " + "1".repeat(Report.DESCRIPTION_WIDTH - 5);
    var description2 = "2222 " + "2".repeat(Report.DESCRIPTION_WIDTH - 5);
    var row1 =
        AmountsProvider.of(description1, month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 =
        AmountsProvider.of(description2, month -> Optional.of(Currency.of(month.ordinal() * 200)));
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
    var footer = Footer.of(body.aggregate("").asRow());
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
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.getValue() * 100)));
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
                    entry(DESCRIPTION, Currency.of(0).format()),
                    entry(JANUARY, Currency.of(100).format()),
                    entry(FEBRUARY, Currency.of(300).format()),
                    entry(MARCH, Currency.of(600).format()),
                    entry(APRIL, Currency.of(1000).format()),
                    entry(MAY, Currency.of(1500).format()),
                    entry(JUNE, Currency.of(2100).format()),
                    entry(JULY, Currency.of(2800).format()),
                    entry(AUGUST, Currency.of(3600).format()),
                    entry(SEPTEMBER, Currency.of(4500).format()),
                    entry(OCTOBER, Currency.of(5500).format()),
                    entry(NOVEMBER, Currency.of(6600).format()),
                    entry(DECEMBER, Currency.of(7800).format()),
                    entry(TOTAL, Currency.of(0).format()),
                    entry(AVERAGE, Currency.of(3033).format()))
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
  void section() {
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
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
  void report() {
    var description = "description";
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
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
            .subtotal(description, TagFilter.any());
    var exp = new Report(() -> reportBuilder.getSections().stream()).render();
    var act = reportBuilder.report().render();
    assertEquals(exp, act);
  }
}
