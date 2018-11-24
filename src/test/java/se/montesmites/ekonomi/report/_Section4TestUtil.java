package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import se.montesmites.ekonomi.model.Currency;

class _Section4TestUtil {

  private final List<Row> rows = new ArrayList<>();

  _Section4TestUtil add(Row row) {
    this.rows.add(row);
    return this;
  }

  _Section4TestUtil addBodyRowWithAmounts(
      String description, Function<Column, Currency> monthlyAmounts) {
    this.rows.add(new DefaultRowWithAmounts(description, monthlyAmounts));
    return this;
  }

  void assertIsEqualTo(Section that) {
    var expected = that.stream().collect(toList());
    var actual = rows;
    assertEquals(expected.size(), actual.size(), "row count");
    for (var row = 0; row < actual.size(); row++) {
      for (var column : Column.values()) {
        var msg = String.format("value at row %d and column %s", row + 1, column);
        var act = actual.get(row).format(column);
        var exp = expected.get(row).format(column);
        assertEquals(exp, act, msg);
      }
    }
  }
}
