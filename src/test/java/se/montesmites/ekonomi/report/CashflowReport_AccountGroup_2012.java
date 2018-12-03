package se.montesmites.ekonomi.report;

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
import se.montesmites.ekonomi.model.Currency;

public enum CashflowReport_AccountGroup_2012 {
  BOKFORT_RESULTAT(
      "Bokfört resultat",
      "([3-7]\\d|8[1-8])\\d\\d",
      Currency.of(3923589),
      new EnumMap<>(Column.class) {
        {
          put(JANUARY, Currency.of(-2866947));
          put(FEBRUARY, Currency.of(6758901));
          put(MARCH, Currency.of(14611584));
          put(APRIL, Currency.of(2150417));
          put(MAY, Currency.of(-6833003));
          put(JUNE, Currency.of(30431649));
          put(JULY, Currency.of(-4877975));
          put(AUGUST, Currency.of(3810720));
          put(SEPTEMBER, Currency.of(4316052));
          put(OCTOBER, Currency.of(-123642));
          put(NOVEMBER, Currency.of(21238571));
          put(DECEMBER, Currency.of(-21533255));
        }
      }),
  KORTFRISTIGA_SKULDER(
      "Kortfristiga skulder",
      "2[4-9]\\d\\d",
      Currency.of(950219),
      new EnumMap<>(Column.class) {
        {
          put(JANUARY, Currency.of(-1387853));
          put(FEBRUARY, Currency.of(50442848));
          put(MARCH, Currency.of(63669735));
          put(APRIL, Currency.of(-137123271));
          put(MAY, Currency.of(9537958));
          put(JUNE, Currency.of(-288127));
          put(JULY, Currency.of(-10662616));
          put(AUGUST, Currency.of(14155331));
          put(SEPTEMBER, Currency.of(-12777139));
          put(OCTOBER, Currency.of(7434716));
          put(NOVEMBER, Currency.of(-6669553));
          put(DECEMBER, Currency.of(35070604));
        }
      });

  private static final java.time.Year YEAR = java.time.Year.of(2012);

  public static Stream<RowWithAmounts> bodyRowsOf(
      CashflowDataFetcher fetcher, List<CashflowReport_AccountGroup_2012> groups) {
    return groups.stream().map(group -> group.bodyRow(fetcher));
  }

  public static void assertBodyRowDescriptions(
      Section section, List<CashflowReport_AccountGroup_2012> groups) {
    var exp = groups.stream().map(g -> g.description).collect(toList());
    var act = section.body().stream().map(row -> row.format(DESCRIPTION)).collect(toList());
    assertEquals(exp.size(), act.size());
    for (var i = 0; i < exp.size(); i++) {
      var fmt = "%s at %d";
      var description = section.header().stream().findFirst().orElseThrow().format(DESCRIPTION);
      var msg = String.format(fmt, description, i);
      assertEquals(exp.get(i), act.get(i), msg);
    }
  }

  public static void assertMonthlyAmounts(
      Section section, List<CashflowReport_AccountGroup_2012> groups) {
    var expList = groups.stream().map(group -> group.expectedAmounts).collect(toList());
    var actList =
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
    for (var i = 0; i < expList.size(); i++) {
      var ix = i;
      var exp = expList.get(i);
      var act = actList.get(i);
      var description = section.header().stream().findFirst().orElseThrow().format(DESCRIPTION);
      var fmt = "%s at %s at %s: ";
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
    var exp = groups.stream().map(g -> g.expectedAverage).collect(toList());
    var act =
        section
            .body()
            .stream()
            .map(row -> row.asRowWithAmounts().orElseThrow().getAverage())
            .collect(toList());
    assertEquals(exp.size(), act.size());
    for (var i = 0; i < exp.size(); i++) {
      var fmt = "%s at %d";
      var description = section.header().stream().findFirst().orElseThrow().format(DESCRIPTION);
      var msg = String.format(fmt, description, i);
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
    return fetcher.reportBuilderOf(YEAR).buildRowWithAmounts(AccountGroup.of(description, regex));
  }
}
