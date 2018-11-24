package se.montesmites.ekonomi.report;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
