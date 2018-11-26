package se.montesmites.ekonomi.report;

import static java.util.Map.entry;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import static se.montesmites.ekonomi.report.Column.TOTAL;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Currency;

class RowWithAmountsTest {

  @Test
  void of() {
    var row = RowWithAmounts.of(column -> Currency.of(column.ordinal()));
    var exp =
        Column.streamMonths()
            .map(column -> Currency.of(column.ordinal()).format())
            .collect(toList());
    var act = Column.streamMonths().map(row::formatMonth).collect(toList());
    assertEquals(exp, act);
  }

  @Test
  void empty() {
    var exp = RowWithAmounts.of(__ -> Currency.of(0));
    var act = RowWithAmounts.empty();
    assertTrue(act.isEquivalentTo(exp));
  }

  @Test
  void formatMonth() {
    var row = RowWithAmounts.of(column -> Currency.of(column.ordinal()));
    var exp =
        Column.streamMonths()
            .map(column -> Currency.of(column.ordinal()).format())
            .collect(toList());
    var act = Column.streamMonths().map(row::formatMonth).collect(toList());
    assertEquals(exp, act);
  }

  @Test
  void getYearlyTotal_formatTotal() {
    var row = RowWithAmounts.of(column -> Currency.of(column.ordinal()));
    var sum = Column.streamMonths().mapToInt(Column::ordinal).sum();
    var exp = Currency.of(sum).format();
    var act = row.formatTotal();
    assertEquals(exp, act);
  }

  @Test
  void getAverage_formatAverage() {
    var row =
        RowWithAmounts.of(column -> Currency.of(column.ordinal() * 100))
            .withMonths(() -> Column.streamMonths().map(column -> column.getMonth().orElseThrow()));
    var avg =
        Column.streamMonths().mapToInt(column -> column.ordinal() * 100).average().orElseThrow();
    var exp = Currency.of((int) avg).format();
    var act = row.formatAverage();
    assertEquals(exp, act);
  }

  @Test
  void withMonths() {
    var row =
        RowWithAmounts.of(column -> Currency.of(column.ordinal() * 100))
            .withMonths(() -> Column.streamMonths().map(column -> column.getMonth().orElseThrow()));
    var exp =
        Column.streamMonths().map(column -> column.getMonth().orElseThrow()).collect(toList());
    var act = row.months().get().collect(toList());
    assertEquals(exp, act);
  }

  @Test
  void months() {
    var row = RowWithAmounts.empty();
    var exp = List.of();
    var act = row.months().get().collect(toList());
    assertEquals(exp, act);
  }

  @Test
  void asRowWithAmounts() {
    var exp = RowWithAmounts.empty();
    var act = RowWithAmounts.empty().asRowWithAmounts().orElseThrow();
    assertTrue(act.isEquivalentTo(exp));
  }

  @Test
  void negate() {
    var neutral = RowWithAmounts.of(column -> Currency.of(column.ordinal() * 100));
    var negated = RowWithAmounts.of(column -> Currency.of(-column.ordinal() * 100));
    var act = neutral.negate();
    assertTrue(act.isEquivalentTo(negated));
  }

  @Test
  void description() {
    var description = "DESCRIPTION";
    var row = RowWithAmounts.of(column -> Currency.of(column.ordinal()));
    var body = Body.of(() -> Stream.of(row));
    var exp = Row.of(column -> column == DESCRIPTION ? description : row.format(column));
    var act = row.description(description);
    assertTrue(act.isEquivalentTo(exp));
  }

  @Test
  void accumulate() {
    var initial = Currency.of(100);
    var row =
        RowWithAmounts.of(column -> Currency.of(column.ordinal() * 100))
            .withMonths(() -> Column.streamMonths().map(column -> column.getMonth().orElseThrow()));
    var exp =
        RowWithAmounts.of(
            column ->
                Map.ofEntries(
                    entry(JANUARY, Currency.of(200)),
                    entry(FEBRUARY, Currency.of(400)),
                    entry(MARCH, Currency.of(700)),
                    entry(APRIL, Currency.of(1100)),
                    entry(MAY, Currency.of(1600)),
                    entry(JUNE, Currency.of(2200)),
                    entry(JULY, Currency.of(2900)),
                    entry(AUGUST, Currency.of(3700)),
                    entry(SEPTEMBER, Currency.of(4600)),
                    entry(OCTOBER, Currency.of(5600)),
                    entry(NOVEMBER, Currency.of(6700)),
                    entry(DECEMBER, Currency.of(7900)))
                    .getOrDefault(column, Currency.of(0)))
            .withMonths(() -> Column.streamMonths().map(column -> column.getMonth().orElseThrow()))
            .description(initial.format())
            .merge(TOTAL, RowWithAmounts.empty());
    var act = row.accumulate(initial);
    assertEquals(exp.asString(), act.asString());
  }
}
