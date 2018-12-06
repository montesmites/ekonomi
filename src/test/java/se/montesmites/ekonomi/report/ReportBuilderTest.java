package se.montesmites.ekonomi.report;

import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.MARCH;
import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.montesmites.ekonomi.report.HeaderRow.SHORT_MONTHS_HEADER;

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
  private static final String DESCRIPTION = "description";
  private static final String REGEX = "regex";
  private static final AccountGroup ACCOUNT_GROUP = AccountGroup.of(DESCRIPTION, REGEX);
  private static final Year YEAR = Year.of(2018);
  private static final YearId YEAR_ID = new YearId("YearId");
  private static final AccountId ACCOUNT_ID = new AccountId(YEAR_ID, REGEX);
  private static final RowWithAmounts TEMPLATE_ROW =
      ((RowWithAmounts) column -> Optional.of(Currency.of(column.ordinal() * 100)))
          .description(DESCRIPTION);
  private static final Header TEMPLATE_HEADER = Header.of(() -> TITLE).add(SHORT_MONTHS_HEADER);
  private static final Body TEMPLATE_BODY = Body.of(TEMPLATE_ROW);
  private static final Footer TEMPLATE_FOOTER = Footer.of(TEMPLATE_BODY.aggregate());
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
              var column = Column.valueOf(yearMonth.getMonth());
              return Optional.of(
                  TEMPLATE_ROW.getMonthlyAmount(column).orElse(Currency.zero()).negate());
            });
  }

  @Test
  void buildRowWithAmounts() {
    var builder = new ReportBuilder(fetcher, YEAR);
    var act = builder.buildRowWithAmounts(ACCOUNT_GROUP);
    assertEquals(TEMPLATE_ROW.asString(), act.asString());
  }

  @Test
  void buildRowWithAmounts_average_threMonths() {
    var fetcher = mock(CashflowDataFetcher.class);
    when(fetcher.streamAccountIds(any(), any())).then(answer -> Stream.of(ACCOUNT_ID));
    when(fetcher.touchedMonths(YEAR)).then(answer -> Set.of(JANUARY, FEBRUARY, MARCH));
    when(fetcher.fetchAmount(any(), any()))
        .thenAnswer(
            answer -> {
              var yearMonth = (YearMonth) answer.getArgument(1);
              var year = Year.of(yearMonth.getYear());
              var month = yearMonth.getMonth();
              return fetcher.touchedMonths(year).contains(month) ? Optional.of(Currency.of(-100))
                  : Optional.empty();
            });
    var builder = new ReportBuilder(fetcher, YEAR);
    var row = builder.buildRowWithAmounts(ACCOUNT_GROUP);
    var exp = Currency.of(100);
    var act = row.getAverage();
    assertEquals(exp, act);
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
    var header = Header.of(() -> TITLE).add(SHORT_MONTHS_HEADER);
    var footer =
        (Row)
            column ->
                Map.ofEntries(
                    entry(Column.DESCRIPTION, Currency.of(0).format()),
                    entry(Column.JANUARY, Currency.of(100).format()),
                    entry(Column.FEBRUARY, Currency.of(300).format()),
                    entry(Column.MARCH, Currency.of(600).format()),
                    entry(Column.APRIL, Currency.of(1000).format()),
                    entry(Column.MAY, Currency.of(1500).format()),
                    entry(Column.JUNE, Currency.of(2100).format()),
                    entry(Column.JULY, Currency.of(2800).format()),
                    entry(Column.AUGUST, Currency.of(3600).format()),
                    entry(Column.SEPTEMBER, Currency.of(4500).format()),
                    entry(Column.OCTOBER, Currency.of(5500).format()),
                    entry(Column.NOVEMBER, Currency.of(6600).format()),
                    entry(Column.DECEMBER, Currency.of(7800).format()),
                    entry(Column.TOTAL, Currency.of(0).format()),
                    entry(Column.AVERAGE, Currency.of(3033).format()))
                    .get(column);
    var exp = Section.of(header, Body.empty(), Footer.of(footer));
    var act = builder.buildSectionWithAcculumatingFooter(TITLE, ACCOUNT_GROUP);
    assertEquals(
        new CashflowReport(() -> Stream.of(exp)).render(),
        new CashflowReport(() -> Stream.of(act)).render());
  }

  @Test
  void buildSection_footerRow() {
    var builder = new ReportBuilder(fetcher, YEAR);
    var footer = (RowWithAmounts) column -> Optional.of(Currency.of(column.ordinal() * 100));
    var exp = Section.of(Header.empty(), Body.empty(), Footer.of(footer));
    var act = builder.buildSection(footer);
    assertTrue(act.isEquivalentTo(exp));
  }
}