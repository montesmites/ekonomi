package se.montesmites.ekonomi.report;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;

import org.junit.jupiter.api.Test;

class RowTest {

  @Test
  void isEquivalentTo() {
    var row1 = (Row) column -> column.name() + "_1";
    var row2 = (Row) column -> column.name() + "_2";
    assertAll(
        () -> assertTrue(row1.isEquivalentTo(row1)),
        () -> assertTrue(row2.isEquivalentTo(row2)),
        () -> assertFalse(row1.isEquivalentTo(row2)),
        () -> assertFalse(row2.isEquivalentTo(row1)));
  }

  @Test
  void merge() {
    var row1 = (Row) column -> column.name() + "_row1";
    var row2 = (Row) column -> column.name() + "_row2";
    var exp = (Row) column -> (column != DESCRIPTION ? row1 : row2).format(column);
    var act = row1.merge(DESCRIPTION, row2);
    assertTrue(act.isEquivalentTo(exp));
  }
}
