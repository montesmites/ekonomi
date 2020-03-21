package se.montesmites.ekonomi.report;

import static java.time.Month.APRIL;
import static java.time.Month.AUGUST;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.JULY;
import static java.time.Month.JUNE;
import static java.time.Month.MARCH;
import static java.time.Month.MAY;
import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import static java.time.Month.SEPTEMBER;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;

import java.time.Month;
import java.util.AbstractMap;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import se.montesmites.ekonomi.model.Currency;

public enum Report_AccountGroup_2012 {
  BOKFORT_RESULTAT(
      "Bokfört resultat",
      "([3-7]\\d|8[1-8])\\d\\d",
      new Currency(3923589),
      new Currency(47083072),
      new EnumMap<>(Month.class) {
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
      new Currency(11402633),
      new EnumMap<>(Month.class) {
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

  public static Stream<AmountsProvider> bodyRowsOf(
      DataFetcher fetcher, List<Report_AccountGroup_2012> groups) {
    return groups
        .stream()
        .map(
            group ->
                AmountsProvider.of(fetcher, YEAR, AccountGroup.of(group.description, group.regex)));
  }

  public static void assertBodyRowDescriptions(
      Section section, List<Report_AccountGroup_2012> groups) {
    var exp = groups.stream().map(g -> g.description).collect(toList());
    var act =
        section
            .body()
            .stream()
            .map(AmountsProvider::asRow)
            .map(row -> row.format(DESCRIPTION))
            .collect(toList());
    assertEquals(exp.size(), act.size());
    for (var i = 0; i < exp.size(); i++) {
      var fmt = "%s at %d";
      var description = section.header().stream().findFirst().orElseThrow().format(DESCRIPTION);
      var msg = String.format(fmt, description, i);
      assertEquals(exp.get(i), act.get(i), msg);
    }
  }

  public static void assertMonthlyAmounts(Section section, List<Report_AccountGroup_2012> groups) {
    var expList = groups.stream().map(group -> group.expectedAmounts).collect(toList());
    var actList =
        section
            .body()
            .stream()
            .map(
                amountsProvider ->
                    stream(Month.values())
                        .map(
                            col ->
                                new AbstractMap.SimpleEntry<>(
                                    col, amountsProvider.getMonthlyAmount(col)))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)))
            .collect(toList());
    assertEquals(expList.size(), actList.size());
    for (var i = 0; i < expList.size(); i++) {
      var ix = i;
      var exp = expList.get(i);
      var act = actList.get(i);
      var description = section.header().stream().findFirst().orElseThrow().format(DESCRIPTION);
      var fmt = "%s at %s at %s: ";
      stream(Month.values())
          .forEach(
              month ->
                  assertEquals(
                      Optional.of(exp.get(month)),
                      act.get(month),
                      String.format(fmt, description, month.name(), ix)));
    }
  }

  public static void assertExpectedAverages(
      Section section, List<Report_AccountGroup_2012> groups) {
    var exp = groups.stream().map(g -> g.expectedAverage).collect(toList());
    var act = section.body().stream().map(AmountsProvider::getAverage).collect(toList());
    assertEquals(exp.size(), act.size());
    for (var i = 0; i < exp.size(); i++) {
      var fmt = "%s at %d";
      var description = section.header().stream().findFirst().orElseThrow().format(DESCRIPTION);
      var msg = String.format(fmt, description, i);
      assertEquals(exp.get(i), act.get(i).orElseThrow(), msg);
    }
  }

  public static void assertExpectedTotals(Section section, List<Report_AccountGroup_2012> groups) {
    var exp = groups.stream().map(g -> g.expectedTotal).collect(toList());
    var act = section.body().stream().map(AmountsProvider::getYearlyTotal).collect(toList());
    assertEquals(exp.size(), act.size());
    for (var i = 0; i < exp.size(); i++) {
      var fmt = "%s at %d";
      var description = section.header().stream().findFirst().orElseThrow().format(DESCRIPTION);
      var msg = String.format(fmt, description, i);
      assertEquals(exp.get(i), act.get(i).orElseThrow(), msg);
    }
  }

  private final String description;
  private final String regex;
  private final Map<Month, Currency> expectedAmounts;
  private final Currency expectedAverage;
  private final Currency expectedTotal;

  Report_AccountGroup_2012(
      String description,
      String regex,
      Currency expectedAverage,
      Currency expectedTotal,
      Map<Month, Currency> expectedAmounts) {
    this.description = description;
    this.regex = regex;
    this.expectedAmounts = expectedAmounts;
    this.expectedAverage = expectedAverage;
    this.expectedTotal = expectedTotal;
  }
}
