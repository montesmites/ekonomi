package se.montesmites.ekonomi.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.montesmites.ekonomi.report.HeaderRow.SHORT_MONTHS_HEADER;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
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
      ((RowWithAmounts) column -> Currency.of(column.ordinal() * 100))
          .withMonths(() -> Set.of(Month.values()).stream())
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
              return Optional.of(TEMPLATE_ROW.getMonthlyAmount(column).negate());
            });
  }

  @Test
  void buildRowWithAmounts() {
    var builder = new ReportBuilder(fetcher, YEAR);
    var act = builder.buildRowWithAmounts(ACCOUNT_GROUP);
    assertEquals(TEMPLATE_ROW.asString(), act.asString());
  }

  @Test
  void buildSection() {
    var builder = new ReportBuilder(fetcher, YEAR);
    var act = builder.buildSection(TITLE, List.of(ACCOUNT_GROUP));
    assertEquals(
        new CashflowReport(() -> Stream.of(TEMPLATE_SECTION)).render(),
        new CashflowReport(() -> Stream.of(act)).render());
  }
}
