package se.montesmites.ekonomi.report;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.montesmites.ekonomi.report.Column.APRIL;
import static se.montesmites.ekonomi.report.Column.AUGUST;
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

import java.util.AbstractMap;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.AccountId;
import se.montesmites.ekonomi.model.Currency;

public enum CashflowReport_AccountGroup_2012 {
  BOKFORT_RESULTAT(
      "Bokfört resultat",
      "([3-7]\\d|8[1-8])\\d\\d",
      new Currency(3923589),
      new EnumMap<>(Column.class) {
        {
          put(JANUARY, new Currency(-2866947));
          put(FEBRUARY, new Currency(6758901));
          put(MARCH, new Currency(14611584));
          put(APRIL, new Currency(2150417));
          put(MAY, new Currency(-6833003));
          put(JUNE, new Currency(30431649));
          put(JULY, new Currency(-4877975));
          put(AUGUST, new Currency(3810720));
          put(SEPTEMBER, new Currency(4316052));
          put(OCTOBER, new Currency(-123642));
          put(NOVEMBER, new Currency(21238571));
          put(DECEMBER, new Currency(-21533255));
        }
      }),
  KORTFRISTIGA_SKULDER(
      "Kortfristiga skulder",
      "2[4-9]\\d\\d",
      new Currency(950219),
      new EnumMap<>(Column.class) {
        {
          put(JANUARY, new Currency(-1387853));
          put(FEBRUARY, new Currency(50442848));
          put(MARCH, new Currency(63669735));
          put(APRIL, new Currency(-137123271));
          put(MAY, new Currency(9537958));
          put(JUNE, new Currency(-288127));
          put(JULY, new Currency(-10662616));
          put(AUGUST, new Currency(14155331));
          put(SEPTEMBER, new Currency(-12777139));
          put(OCTOBER, new Currency(7434716));
          put(NOVEMBER, new Currency(-6669553));
          put(DECEMBER, new Currency(35070604));
        }
      });

  private static final java.time.Year YEAR = java.time.Year.of(2012);

  public static Stream<RowWithAmounts> bodyRowsOf(
      CashflowDataFetcher fetcher, List<CashflowReport_AccountGroup_2012> groups) {
    return groups.stream().map(group -> group.bodyRow(fetcher));
  }

  public static void assertBodyRowDescriptions(
      Section section, List<CashflowReport_AccountGroup_2012> groups) {
    final List<String> exp = groups.stream().map(g -> g.description).collect(toList());
    final List<String> act =
        section.body().stream().map(row -> row.format(DESCRIPTION)).collect(toList());
    assertEquals(exp.size(), act.size());
    for (int i = 0; i < exp.size(); i++) {
      String fmt = "%s at %d";
      String description = section.header().stream().findFirst().orElseThrow().format(DESCRIPTION);
      final String msg = String.format(fmt, description, i);
      assertEquals(exp.get(i), act.get(i), msg);
    }
  }

  public static void assertMonthlyAmounts(
      Section section, List<CashflowReport_AccountGroup_2012> groups) {
    final List<Map<Column, Currency>> expList =
        groups.stream().map(group -> group.expectedAmounts).collect(toList());
    final List<Map<Column, Currency>> actList =
        section
            .body()
            .stream()
            .map(row -> row.asRowWithAmounts().orElseThrow())
            .map(
                row ->
                    Column.streamMonths()
                        .map(col -> new AbstractMap.SimpleEntry<>(col, row.getMonthlyAmount(col)))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)))
            .collect(toList());
    assertEquals(expList.size(), actList.size());
    for (int i = 0; i < expList.size(); i++) {
      final int ix = i;
      final Map<Column, Currency> exp = expList.get(i);
      final Map<Column, Currency> act = actList.get(i);
      String description = section.header().stream().findFirst().orElseThrow().format(DESCRIPTION);
      final String fmt = "%s at %s at %s: ";
      Column.streamMonths()
          .forEach(
              column ->
                  assertEquals(
                      exp.get(column),
                      act.get(column),
                      String.format(fmt, description, column.name(), ix)));
    }
  }

  public static void assertExpectedAverages(
      Section section, List<CashflowReport_AccountGroup_2012> groups) {
    final List<Currency> exp = groups.stream().map(g -> g.expectedAverage).collect(toList());
    final List<Currency> act =
        section
            .body()
            .stream()
            .map(row -> row.asRowWithAmounts().orElseThrow().getAverage())
            .collect(toList());
    assertEquals(exp.size(), act.size());
    for (int i = 0; i < exp.size(); i++) {
      String fmt = "%s at %d";
      String description = section.header().stream().findFirst().orElseThrow().format(DESCRIPTION);
      String msg = String.format(fmt, description, i);
      assertEquals(exp.get(i), act.get(i), msg);
    }
  }

  private final String description;
  private final String regex;
  private final Map<Column, Currency> expectedAmounts;
  private final Currency expectedAverage;

  CashflowReport_AccountGroup_2012(
      String description,
      String regex,
      Currency expectedAverage,
      Map<Column, Currency> expectedAmounts) {
    this.description = description;
    this.regex = regex;
    this.expectedAmounts = expectedAmounts;
    this.expectedAverage = expectedAverage;
  }

  private RowWithAmounts bodyRow(CashflowDataFetcher fetcher) {
    final AccountFilter filter = new AccountFilterByRegex(regex);
    var accountIds =
        filter
            .filter(fetcher.streamAccountIds(YEAR))
            .distinct()
            .sorted(comparing(AccountId::getId))
            .collect(toList());
    return fetcher.buildRowWithAmounts(accountIds, YEAR, description);
  }
}
