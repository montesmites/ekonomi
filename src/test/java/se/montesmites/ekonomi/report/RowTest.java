package se.montesmites.ekonomi.report;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RowTest {
    @Test
    void isEquivalentTo() {
        var row1 = (Row) column -> column.name() + "_1";
        var row2 = (Row) column -> column.name() + "_2";
        assertAll(
                () -> assertTrue(row1.isEquivalentTo(row1)),
                () -> assertTrue(row2.isEquivalentTo(row2)),
                () -> assertFalse(row1.isEquivalentTo(row2)),
                () -> assertFalse(row2.isEquivalentTo(row1))
        );
    }
}
