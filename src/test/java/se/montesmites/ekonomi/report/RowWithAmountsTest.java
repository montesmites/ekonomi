package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
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
    var exp = Column.streamMonths().map(column -> column.getMonth().orElseThrow())
        .collect(toList());
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
}
