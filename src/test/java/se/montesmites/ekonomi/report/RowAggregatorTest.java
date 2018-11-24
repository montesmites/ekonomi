package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Currency;

class RowAggregatorTest {

  @Test
  void empty() {
    var aggregator = RowAggregator.of(Stream::empty);
    var exp = RowWithAmounts.empty();
    var act = aggregator.aggregate().asRowWithAmounts().orElseThrow();
    assertRowWithAmounts(exp, act);
  }

  @Test
  void oneRow() {
    var row = RowWithAmounts.of(column -> Currency.of(column.ordinal()));
    var aggregator = RowAggregator.of(() -> Stream.of(row));
    var exp = RowWithAmounts.of(column -> Currency.of(column.ordinal()));
    var act = aggregator.aggregate().asRowWithAmounts().orElseThrow();
    assertRowWithAmounts(exp, act);
  }

  @Test
  void twoRows() {
    var row1 = RowWithAmounts.of(column -> Currency.of(column.ordinal()));
    var row2 = RowWithAmounts.of(column -> Currency.of(column.ordinal()));
    var aggregator = RowAggregator.of(() -> Stream.of(row1, row2));
    var exp = RowWithAmounts.of(column -> Currency.of(column.ordinal() * 2));
    var act = aggregator.aggregate().asRowWithAmounts().orElseThrow();
    assertRowWithAmounts(exp, act);
  }

  private void assertRowWithAmounts(RowWithAmounts exp, RowWithAmounts act) {
    var expMonths = exp.months().get().collect(toList());
    var actMonths = act.months().get().collect(toList());
    assertAll(
        () -> expMonths.equals(actMonths),
        () ->
            expMonths
                .stream()
                .map(Column::valueOf)
                .forEach(
                    column ->
                        assertEquals(exp.getMonthlyAmount(column), act.getMonthlyAmount(column))));
  }
}
