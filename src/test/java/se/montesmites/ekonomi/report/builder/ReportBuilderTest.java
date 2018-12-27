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

import java.time.Month;
import java.time.Year;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountsProvider;
import se.montesmites.ekonomi.report.Body;
import se.montesmites.ekonomi.report.CashflowReport;
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.Row;
import se.montesmites.ekonomi.report.Section;

class ReportBuilderTest {

  private static final String TITLE = "title";
  private static final String DESCRIPTION_TEXT = "description";
  private static final String REGEX = "regex";
  private static final AccountGroup ACCOUNT_GROUP = AccountGroup.of(DESCRIPTION_TEXT, REGEX);
  private static final Year YEAR = Year.of(2018);
  private static final YearId YEAR_ID = new YearId("YearId");
  private static final AccountId ACCOUNT_ID = new AccountId(YEAR_ID, REGEX);
  private static final AmountsProvider TEMPLATE_AMOUNTS_PROVIDER =
      new AmountsProvider() {
        @Override
        public Optional<Currency> getMonthlyAmount(Month month) {
          return Optional.of(Currency.of((month.ordinal() + 1) * 100));
        }

        @Override
        public String formatDescription() {
          return DESCRIPTION_TEXT;
        }
      };
  private static final Header TEMPLATE_HEADER =
      Header.of(Row.title(TITLE)).add(Row.descriptionWithMonths("", Row.SHORT_MONTHS));
  private static final Body TEMPLATE_BODY = Body.of(TEMPLATE_AMOUNTS_PROVIDER);
  private static final Footer TEMPLATE_FOOTER = Footer.of(TEMPLATE_BODY.aggregate("").asRow());
  private static final Section TEMPLATE_SECTION =
      Section.of(TEMPLATE_HEADER, TEMPLATE_BODY, TEMPLATE_FOOTER);

  private final Year year = Year.now();
  private final YearId yearId = new YearId(year.toString());

  @Test
  void accountGroups() {
    var amountFetcher =
        AmountFetcherBuilder.of(
            Map.of(
                ACCOUNT_ID,
                AmountsProvider.of(
                    month ->
                        Optional.of(
                            TEMPLATE_AMOUNTS_PROVIDER
                                .getMonthlyAmount(month)
                                .orElse(Currency.zero())))))
            .amountFetcher();
    var exp = List.of(TEMPLATE_SECTION).stream().map(Section::asString).collect(toList());
    var act =
        new ReportBuilder(amountFetcher, YEAR)
            .accountGroups(TITLE, List.of(ACCOUNT_GROUP))
            .getSections()
            .stream()
            .map(Section::asString)
            .collect(toList());
    Assertions.assertEquals(exp, act);
  }

  @Test
  void buildSection_title_accountGroup() {
    var amountFetcher =
        AmountFetcherBuilder.of(
            Map.of(
                ACCOUNT_ID,
                AmountsProvider.of(
                    month ->
                        Optional.of(
                            TEMPLATE_AMOUNTS_PROVIDER
                                .getMonthlyAmount(month)
                                .orElse(Currency.zero())))))
            .amountFetcher();
    var header = Header.of(Row.title(TITLE)).add(Row.descriptionWithMonths("", Row.SHORT_MONTHS));
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
    var exp =
        List.of(Section.of(header, Body.empty(), Footer.of(footer)))
            .stream()
            .map(Section::asString)
            .collect(toList());
    var act =
        new ReportBuilder(amountFetcher, YEAR)
            .accumulateAccountGroups(TITLE, List.of(ACCOUNT_GROUP))
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
    var amountFetcher =
        AmountFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountFetcher();
    var reportBuilder =
        new ReportBuilder(amountFetcher, YEAR)
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
  void subtotal_materializedBody() {
    var description = "description";
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var subtotal =
        AmountsProvider.of(description, month -> Optional.of(Currency.of(month.ordinal() * 300)));
    var amountFetcher =
        AmountFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountFetcher();
    var reportBuilder =
        new ReportBuilder(amountFetcher, Year.now())
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "1111")))))
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "2222")))))
            .subtotal(description);
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
  void subtotal_dematerializedBody() {
    var description = "description";
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var subtotal =
        AmountsProvider.of(description, month -> Optional.of(Currency.of(month.ordinal() * 300)));
    var amountFetcher =
        AmountFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountFetcher();
    var reportBuilder =
        new ReportBuilder(amountFetcher, Year.now())
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "1111")))))
            .section(
                section ->
                    section.body(
                        body ->
                            body.accountGroups(List.of(AccountGroup.of("", "2222")))
                                .dematerialize()))
            .subtotal(description);
    var exp =
        List.of(
            Section.of(Header.empty(), Body.of(row1), Footer.empty()),
            Section.of(Header.empty(), Body.empty(), Footer.empty()),
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
  void subtotal_twoSubtotals() {
    var description1 = "description1";
    var description2 = "description2";
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var row3 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 300)));
    var subtotal1 =
        AmountsProvider.of(description1, month -> Optional.of(Currency.of(month.ordinal() * 300)));
    var subtotal2 =
        AmountsProvider.of(description2, month -> Optional.of(Currency.of(month.ordinal() * 600)));
    var amountFetcher =
        AmountFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2),
                entry(new AccountId(yearId, "3333"), row3)))
            .amountFetcher();
    var reportBuilder =
        new ReportBuilder(amountFetcher, Year.now())
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "1111")))))
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "2222")))))
            .subtotal(description1)
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "3333")))))
            .subtotal(description2);
    var exp =
        List.of(
            Section.of(Header.empty(), Body.of(row1), Footer.empty()),
            Section.of(Header.empty(), Body.of(row2), Footer.empty()),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal1.asRow())),
            Section.of(Header.empty(), Body.of(row3), Footer.empty()),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal2.asRow())))
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
  void subtotal_withTwoUntouchedMonths() {
    var touchedMonths =
        EnumSet.of(
            Month.JANUARY,
            Month.FEBRUARY,
            Month.MARCH,
            Month.APRIL,
            Month.MAY,
            Month.JUNE,
            Month.JULY,
            Month.AUGUST,
            Month.SEPTEMBER,
            Month.OCTOBER);
    var description1 = "description1";
    var description2 = "description2";
    var row1 =
        AmountsProvider.of(
            month ->
                touchedMonths.contains(month)
                    ? Optional.of(Currency.of(month.ordinal() * 100))
                    : Optional.empty());
    var row2 =
        AmountsProvider.of(
            month ->
                touchedMonths.contains(month)
                    ? Optional.of(Currency.of(month.ordinal() * 200))
                    : Optional.empty());
    var row3 =
        AmountsProvider.of(
            month ->
                touchedMonths.contains(month)
                    ? Optional.of(Currency.of(month.ordinal() * 300))
                    : Optional.empty());
    var subtotal1 =
        AmountsProvider.of(
            description1,
            month ->
                touchedMonths.contains(month)
                    ? Optional.of(Currency.of(month.ordinal() * 300))
                    : Optional.empty());
    var subtotal2 =
        AmountsProvider.of(
            description2,
            month ->
                touchedMonths.contains(month)
                    ? Optional.of(Currency.of(month.ordinal() * 600))
                    : Optional.empty());
    var amountFetcher =
        AmountFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2),
                entry(new AccountId(yearId, "3333"), row3)))
            .touchedMonths(Map.of(YEAR, touchedMonths))
            .amountFetcher();
    var reportBuilder =
        new ReportBuilder(amountFetcher, Year.now())
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "1111")))))
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "2222")))))
            .subtotal(description1)
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "3333")))))
            .subtotal(description2);
    var exp =
        List.of(
            Section.of(Header.empty(), Body.of(row1), Footer.empty()),
            Section.of(Header.empty(), Body.of(row2), Footer.empty()),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal1.asRow())),
            Section.of(Header.empty(), Body.of(row3), Footer.empty()),
            Section.of(Header.empty(), Body.empty(), Footer.of(subtotal2.asRow())))
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
    var row1 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 100)));
    var row2 = AmountsProvider.of(month -> Optional.of(Currency.of(month.ordinal() * 200)));
    var amountFetcher =
        AmountFetcherBuilder.of(
            Map.ofEntries(
                entry(new AccountId(yearId, "1111"), row1),
                entry(new AccountId(yearId, "2222"), row2)))
            .amountFetcher();
    var reportBuilder =
        new ReportBuilder(amountFetcher, Year.now())
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "1111")))))
            .section(
                section ->
                    section.body(body -> body.accountGroups(List.of(AccountGroup.of("", "2222")))))
            .subtotal(description);
    var exp = new CashflowReport(() -> reportBuilder.getSections().stream()).render();
    var act = reportBuilder.report().render();
    assertEquals(exp, act);
  }
}
