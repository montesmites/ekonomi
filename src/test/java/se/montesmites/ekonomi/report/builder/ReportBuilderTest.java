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

import java.time.Month;
import java.time.Year;
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
import se.montesmites.ekonomi.report.Footer;
import se.montesmites.ekonomi.report.Header;
import se.montesmites.ekonomi.report.Report;
import se.montesmites.ekonomi.report.Row;
import se.montesmites.ekonomi.report.Section;

class ReportBuilderTest {

  private static final String TITLE = "title";
  private static final String DESCRIPTION_TEXT = "description";
  private static final String REGEX = "regex";
  private static final AccountGroup ACCOUNT_GROUP = AccountGroup.of(DESCRIPTION_TEXT, REGEX);
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
  private final AccountId accountId = new AccountId(yearId, REGEX);

  @Test
  void accountGroups() {
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.of(
                accountId,
                AmountsProvider.of(
                    month ->
                        Optional.of(
                            TEMPLATE_AMOUNTS_PROVIDER
                                .getMonthlyAmount(month)
                                .orElse(Currency.zero())))))
            .amountsFetcher();
    var exp = List.of(TEMPLATE_SECTION).stream().map(Section::asString).collect(toList());
    var act =
        new ReportBuilder(amountsFetcher, year)
            .accountGroups(TITLE, List.of(ACCOUNT_GROUP))
            .getSections()
            .stream()
            .map(Section::asString)
            .collect(toList());
    Assertions.assertEquals(exp, act);
  }

  @Test
  void buildSection_title_accountGroup() {
    var amountsFetcher =
        AmountsFetcherBuilder.of(
            Map.of(
                accountId,
                AmountsProvider.of(
                    month ->
                        Optional.of(
                            TEMPLATE_AMOUNTS_PROVIDER
                                .getMonthlyAmount(month)
                                .orElse(Currency.zero())))))
            .amountsFetcher();
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
        new ReportBuilder(amountsFetcher, year)
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
            .subtotal(description);
    var exp = new Report(() -> reportBuilder.getSections().stream()).render();
    var act = reportBuilder.report().render();
    assertEquals(exp, act);
  }
}
