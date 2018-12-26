package se.montesmites.ekonomi.report.builder;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Balance;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;
import se.montesmites.ekonomi.report.AccountGroup;
import se.montesmites.ekonomi.report.AmountFetcher;
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

  private AmountFetcher fetcher;

  @BeforeEach
  void beforeEach() {
    this.fetcher =
        new AmountFetcher() {
          @Override
          public Optional<Currency> fetchAmount(AccountId accountId, YearMonth yearMonth) {
            return Optional.of(
                TEMPLATE_AMOUNTS_PROVIDER
                    .getMonthlyAmount(yearMonth.getMonth())
                    .orElse(Currency.zero())
                    .negate());
          }

          @Override
          public Optional<Balance> fetchBalance(AccountId accountId) {
            return Optional.empty();
          }

          @Override
          public Stream<AccountId> streamAccountIds(Year year, Predicate<AccountId> filter) {
            return Stream.of(ACCOUNT_ID);
          }

          @Override
          public Set<Month> touchedMonths(Year year) {
            return Set.of(Month.values());
          }
        };
  }

  @Test
  void buildSection_title_accountGroups() {
    var builder = new ReportBuilder(fetcher, YEAR);
    var act = builder.buildSection(TITLE, List.of(ACCOUNT_GROUP));
    Assertions.assertEquals(
        new CashflowReport(() -> Stream.of(TEMPLATE_SECTION)).render(),
        new CashflowReport(() -> Stream.of(act)).render());
  }

  @Test
  void buildSection_title_accountGroup() {
    var builder = new ReportBuilder(fetcher, YEAR);
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
    var exp = Section.of(header, Body.empty(), Footer.of(footer));
    var act = builder.buildSectionWithAcculumatingFooter(TITLE, ACCOUNT_GROUP);
    assertEquals(
        new CashflowReport(() -> Stream.of(exp)).render(),
        new CashflowReport(() -> Stream.of(act)).render());
  }

  @Test
  void section() {
    var reportBuilder = new ReportBuilder(fetcher, YEAR);
    assertNotNull(reportBuilder.section());
  }
}
