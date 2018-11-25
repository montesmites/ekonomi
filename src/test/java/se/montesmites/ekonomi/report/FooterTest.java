package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.model.Currency;

class FooterTest {

  @Test
  void emptyFooter() {
    var footer = Footer.empty();
    var exp = List.<Row>of();
    var act = footer.stream().collect(toList());
    assertFooters(exp, act);
  }

  @Test
  void aggregator() {
    var row = RowWithAmounts.of(column -> Currency.of(column.ordinal()));
    var aggregator = RowAggregator.of(() -> Stream.of(row));
    var footer = Footer.of(aggregator);
    var exp = List.of(Row.of(row::format));
    var act = footer.stream().collect(toList());
    assertFooters(exp, act);
  }

  @Test
  void aggregatorAndEmptyRow() {
    var row = RowWithAmounts.of(column -> Currency.of(column.ordinal()));
    var aggregator = RowAggregator.of(() -> Stream.of(row));
    var footer = Footer.of(aggregator).add(Row.empty());
    var exp = List.of(Row.of(row::format), Row.empty());
    var act = footer.stream().collect(toList());
    assertFooters(exp, act);
  }

  @Test
  void body() {
    var row = RowWithAmounts.of(column -> Currency.of(column.ordinal()));
    var body = Body.of(() -> Stream.of(row));
    var footer = Footer.of(body);
    var exp = List.of(Row.of(row::format));
    var act = footer.stream().collect(toList());
    assertFooters(exp, act);
  }

  private void assertFooters(List<? extends Row> exp, List<? extends Row> act) {
    assertAll(
        () -> assertEquals(exp.size(), act.size()),
        () ->
            assertAll(
                () -> range(0, exp.size()).forEach(i -> exp.get(i).isEquivalentTo(act.get(i)))));
  }
}
