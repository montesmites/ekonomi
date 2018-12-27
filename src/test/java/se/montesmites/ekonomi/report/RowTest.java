package se.montesmites.ekonomi.report;

import static java.util.Map.entry;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.montesmites.ekonomi.i18n.Messages.Message.HEADER_ROW_AVERAGE;
import static se.montesmites.ekonomi.i18n.Messages.Message.HEADER_ROW_TOTAL;
import static se.montesmites.ekonomi.report.Column.AVERAGE;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;
import static se.montesmites.ekonomi.report.Column.TOTAL;
import static se.montesmites.ekonomi.report.Row.SHORT_MONTHS;

import java.util.Map;
import org.junit.jupiter.api.Test;
import se.montesmites.ekonomi.i18n.Messages;
import se.montesmites.ekonomi.model.Currency;

class RowTest {

  @Test
  void empty() {
    var row = Row.empty();
    var exp = ((Row) column -> "").asExtendedString();
    var act = row.asExtendedString();
    assertEquals(exp, act);
  }

  @Test
  void of_function() {
    var row = Row.of(column -> Currency.of(column.ordinal() * 100).format());
    var exp = ((Row) column -> Currency.of(column.ordinal() * 100).format()).asExtendedString();
    var act = row.asExtendedString();
    assertEquals(exp, act);
  }

  @Test
  void of_mapWithAllColumns() {
    var map = Column.stream().collect(toMap(column -> column, Column::name));
    var row = Row.of(map);
    var exp = Row.of(map::get).asExtendedString();
    var act = row.asExtendedString();
    assertEquals(exp, act);
  }

  @Test
  void of_mapWithSomeEmptyColumns() {
    var map =
        Map.ofEntries(
            entry(DESCRIPTION, "description"), entry(AVERAGE, "average"), entry(TOTAL, "total"));
    var row = Row.of(map);
    var exp = Row.of(column -> map.getOrDefault(column, "")).asExtendedString();
    var act = row.asExtendedString();
    assertEquals(exp, act);
  }

  @Test
  void title() {
    var title = "title";
    var exp = Row.of(Map.of(DESCRIPTION, title.toUpperCase())).asExtendedString();
    var act = Row.title(title).asExtendedString();
    assertEquals(exp, act);
  }

  @Test
  void of_descriptionWithMonths() {
    var description = "description";
    var average = Messages.get(HEADER_ROW_AVERAGE);
    var total = Messages.get(HEADER_ROW_TOTAL);
    var map =
        Map.ofEntries(
            entry(DESCRIPTION, description), entry(AVERAGE, average), entry(TOTAL, total));
    var row = Row.descriptionWithMonths(description, SHORT_MONTHS);
    var exp =
        Row.of(
            column ->
                column
                    .getMonth()
                    .map(Messages::getShortMonth)
                    .orElse(map.getOrDefault(column, "")))
            .asExtendedString();
    var act = row.asExtendedString();
    assertEquals(exp, act);
  }

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
