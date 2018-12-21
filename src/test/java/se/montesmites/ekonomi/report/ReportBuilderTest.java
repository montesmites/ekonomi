package se.montesmites.ekonomi.report;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;
import se.montesmites.ekonomi.model.YearId;

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
  private static final Footer TEMPLATE_FOOTER =
      Footer.of(TEMPLATE_BODY.aggregate("").asRow());
  private static final Section TEMPLATE_SECTION =
      Section.of(TEMPLATE_HEADER, TEMPLATE_BODY, TEMPLATE_FOOTER);

  private CashflowDataFetcher fetcher;

  @BeforeEach
  void beforeEach() {
    this.fetcher = mock(CashflowDataFetcher.class);
    when(fetcher.streamAccountIds(any(), any())).then(answer -> Stream.of(ACCOUNT_ID));
    when(fetcher.touchedMonths(YEAR)).then(answer -> Set.of(Month.values()));
    when(fetcher.fetchAmount(any(), any()))
        .thenAnswer(
            answer -> {
              var yearMonth = (YearMonth) answer.getArgument(1);
              return Optional.of(
                  TEMPLATE_AMOUNTS_PROVIDER
                      .getMonthlyAmount(yearMonth.getMonth())
                      .orElse(Currency.zero())
                      .negate());
            });
  }

  @Test
  void buildRowWithAmounts() {
    var builder = new ReportBuilder(fetcher, YEAR);
    var act = builder.buildAmountsProvider(ACCOUNT_GROUP);
    assertEquals(TEMPLATE_AMOUNTS_PROVIDER.asRow().asString(), act.asRow().asString());
  }

  @Test
  void buildRowWithAmounts_average_threeMonths() {
    var fetcher = mock(CashflowDataFetcher.class);
    when(fetcher.streamAccountIds(any(), any())).then(answer -> Stream.of(ACCOUNT_ID));
    when(fetcher.touchedMonths(YEAR))
        .then(answer -> Set.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH));
    when(fetcher.fetchAmount(any(), any()))
        .thenAnswer(
            answer -> {
              var yearMonth = (YearMonth) answer.getArgument(1);
              var year = Year.of(yearMonth.getYear());
              var month = yearMonth.getMonth();
              return fetcher.touchedMonths(year).contains(month)
                  ? Optional.of(Currency.of(-100))
                  : Optional.empty();
            });
    var builder = new ReportBuilder(fetcher, YEAR);
    var row = builder.buildAmountsProvider(ACCOUNT_GROUP);
    var exp = Currency.of(100);
    var act = row.getAverage();
    assertEquals(exp, act.orElseThrow());
  }

  @Test
  void buildSection_title_accountGroups() {
    var builder = new ReportBuilder(fetcher, YEAR);
    var act = builder.buildSection(TITLE, List.of(ACCOUNT_GROUP));
    assertEquals(
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
