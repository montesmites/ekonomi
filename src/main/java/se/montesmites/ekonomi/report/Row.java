package se.montesmites.ekonomi.report;

import static java.util.Map.entry;
import static java.util.stream.Collectors.joining;
import static se.montesmites.ekonomi.i18n.Messages.Message.HEADER_ROW_AVERAGE;
import static se.montesmites.ekonomi.i18n.Messages.Message.HEADER_ROW_TOTAL;
import static se.montesmites.ekonomi.report.Column.AVERAGE;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;
import static se.montesmites.ekonomi.report.Column.TOTAL;

import java.time.Month;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import se.montesmites.ekonomi.i18n.Messages;

@FunctionalInterface
public interface Row {

  Function<Month, String> SHORT_MONTHS = Messages::getShortMonth;

  static Row empty() {
    return column -> "";
  }

  static Row title(String title) {
    return Row.of(Map.of(DESCRIPTION, title.toUpperCase()));
  }

  static Row descriptionWithMonths(String description, Function<Month, String> formattedMonths) {
    var map =
        Map.ofEntries(
            entry(DESCRIPTION, description),
            entry(AVERAGE, Messages.get(HEADER_ROW_AVERAGE)),
            entry(TOTAL, Messages.get(HEADER_ROW_TOTAL)));
    return Row.of(
        column ->
            column.getMonth().map(formattedMonths).orElse(map.getOrDefault(column, "")));
  }

  static Row of(Map<Column, String> values) {
    return Row.of(column -> values.getOrDefault(column, ""));
  }

  static Row of(Function<Column, String> formattedValues) {
    return formattedValues::apply;
  }

  String format(Column column);

  default boolean isEquivalentTo(Row that) {
    return Column.stream().allMatch(columnIsEquivalentPredicate(that));
  }

  private Predicate<Column> columnIsEquivalentPredicate(Row that) {
    return column -> this.format(column).equals(that.format(column));
  }

  default Row merge(Column column, Row row) {
    return col -> col == column ? row.format(col) : this.format(col);
  }

  default String asString() {
    return Column.stream()
        .map(column -> column.name() + " = " + this.format(column))
        .collect(joining(", ", "{", "}"));
  }
}
