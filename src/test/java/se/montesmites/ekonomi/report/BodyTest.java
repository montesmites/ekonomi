package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Currency;

class BodyTest {

  @Test
  void emptyBody() {
    var body = Body.empty();
    var exp = List.<Row>of();
    var act = body.stream().collect(toList());
    assertBodys(exp, act);
  }

  @Test
  void oneRowWithAmounts() {
    var row = RowWithAmounts.of(column -> Currency.of(column.ordinal()));
    var body = Body.of(row);
    var exp = List.of(row);
    var act = body.stream().collect(toList());
    assertBodys(exp, act);
  }

  @Test
  void twoRowsWithAmounts() {
    var row1 = RowWithAmounts.of(column -> Currency.of(column.ordinal()));
    var row2 = RowWithAmounts.of(column -> Currency.of(column.ordinal() * 100));
    var body = Body.of(row1).add(row2);
    var exp = List.of(row1, row2);
    var act = body.stream().collect(toList());
    assertBodys(exp, act);
  }

  @Test
  void streamOfRowsWithAmounts() {
    var row1 = RowWithAmounts.of(column -> Currency.of(column.ordinal()));
    var row2 = RowWithAmounts.of(column -> Currency.of(column.ordinal() * 100));
    var body = Body.of(() -> Stream.of(row1, row2));
    var exp = List.of(row1, row2);
    var act = body.stream().collect(toList());
    assertBodys(exp, act);
  }

  @Test
  void aggregate() {
    var row1 = RowWithAmounts.of(column -> Currency.of(column.ordinal()));
    var row2 = RowWithAmounts.of(column -> Currency.of(column.ordinal() * 100));
    var body = Body.of(() -> Stream.of(row1, row2));
    var exp =
        RowWithAmounts.of(column -> Currency.of(column.ordinal() * 100 + column.ordinal()))
            .merge(DESCRIPTION, Row.empty());
    var act = body.aggregate();
    assertTrue(act.isEquivalentTo(exp));
  }

  private void assertBodys(List<? extends Row> exp, List<? extends Row> act) {
    assertAll(
        () -> assertEquals(exp.size(), act.size()),
        () ->
            assertAll(
                () -> range(0, exp.size()).forEach(i -> exp.get(i).isEquivalentTo(act.get(i)))));
  }
}
