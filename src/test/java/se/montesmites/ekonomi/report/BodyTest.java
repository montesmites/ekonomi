package se.montesmites.ekonomi.report;

import static java.util.Map.entry;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;
import static se.montesmites.ekonomi.report.Column.FEBRUARY;
import static se.montesmites.ekonomi.report.Column.JANUARY;
import static se.montesmites.ekonomi.report.Column.MARCH;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Currency;

class BodyTest {

  @Test
  void emptyBody() {
    var exp = Body.of(Stream::empty);
    var act = Body.empty();
    assertBodys(exp, act);
  }

  @Test
  void oneRowWithAmounts() {
    var row = RowWithAmounts.of(column -> Optional.of(Currency.of(column.ordinal())));
    var exp = Body.of(row);
    var act = Body.of(row);
    assertBodys(exp, act);
  }

  @Test
  void twoRowsWithAmounts() {
    var row1 = RowWithAmounts.of(column -> Optional.of(Currency.of(column.ordinal())));
    var row2 = RowWithAmounts.of(column -> Optional.of(Currency.of(column.ordinal() * 100)));
    var exp = Body.of(() -> Stream.of(row1, row2));
    var act = Body.of(row1).add(row2);
    assertBodys(exp, act);
  }

  @Test
  void streamOfRowsWithAmounts() {
    var row1 = RowWithAmounts.of(column -> Optional.of(Currency.of(column.ordinal())));
    var row2 = RowWithAmounts.of(column -> Optional.of(Currency.of(column.ordinal() * 100)));
    var exp = Body.of(() -> Stream.of(row1, row2));
    var act = Body.of(() -> Stream.of(row1, row2));
    assertBodys(exp, act);
  }

  @Test
  void aggregate() {
    var row1 = RowWithAmounts.of(column -> Optional.of(Currency.of(column.ordinal())));
    var row2 = RowWithAmounts.of(column -> Optional.of(Currency.of(column.ordinal() * 100)));
    var body = Body.of(() -> Stream.of(row1, row2));
    var exp =
        RowWithAmounts.of(
            column -> Optional.of(Currency.of(column.ordinal() * 100 + column.ordinal())))
            .merge(DESCRIPTION, Row.empty());
    var act = body.aggregate();
    assertTrue(act.isEquivalentTo(exp));
  }

  @Test
  void aggregate_threeMonths() {
    var row1 =
        RowWithAmounts.of(
            Map.ofEntries(
                entry(JANUARY, Currency.of(1000)),
                entry(FEBRUARY, Currency.of(2000)),
                entry(MARCH, Currency.of(3000))));
    var row2 =
        RowWithAmounts.of(
            Map.ofEntries(
                entry(JANUARY, Currency.of(100)),
                entry(FEBRUARY, Currency.of(200)),
                entry(MARCH, Currency.of(300))));
    var body = Body.of(() -> Stream.of(row1, row2));
    var exp =
        RowWithAmounts.of(
            Map.ofEntries(
                entry(JANUARY, Currency.of(1100)),
                entry(FEBRUARY, Currency.of(2200)),
                entry(MARCH, Currency.of(3300))))
            .merge(DESCRIPTION, Row.empty());
    var act = body.aggregate();
    assertTrue(act.isEquivalentTo(exp));
  }

  @Test
  void concat() {
    var row1 = RowWithAmounts.of(column -> Optional.of(Currency.of(column.ordinal() * 100)));
    var row2 = RowWithAmounts.of(column -> Optional.of(Currency.of(column.ordinal() * 200)));
    var row3 = RowWithAmounts.of(column -> Optional.of(Currency.of(column.ordinal() * 300)));
    var row4 = RowWithAmounts.of(column -> Optional.of(Currency.of(column.ordinal() * 400)));
    var exp = Body.of(() -> Stream.of(row1, row2, row3, row4));
    var act = Body.of(() -> Stream.of(row1, row2)).concat(Body.of(() -> Stream.of(row3, row4)));
    assertBodys(exp, act);
  }

  @Test
  void negate() {
    var row1 = RowWithAmounts.of(column -> Optional.of(Currency.of(column.ordinal() * 100)));
    var row2 = RowWithAmounts.of(column -> Optional.of(Currency.of(column.ordinal() * 200)));
    var body = Body.of(() -> Stream.of(row1, row2));
    var exp = Body.of(() -> Stream.of(row1.negate(), row2.negate()));
    var act = body.negate();
    assertBodys(exp, act);
  }

  private void assertBodys(Body expected, Body actutal) {
    var exp = expected.stream().collect(toList());
    var act = actutal.stream().collect(toList());
    assertAll(
        () -> assertEquals(exp.size(), act.size()),
        () ->
            assertAll(
                () -> range(0, exp.size()).forEach(i -> exp.get(i).isEquivalentTo(act.get(i)))));
  }
}
