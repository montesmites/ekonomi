package se.montesmites.ekonomi.report;

import static java.util.stream.Collectors.joining;
import static se.montesmites.ekonomi.report.Column.DESCRIPTION;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@FunctionalInterface
public interface Row {

  static Row empty() {
    return column -> "";
  }

  static Row title(String title) {
    return Row.of(Map.of(DESCRIPTION, title.toUpperCase()));
  }

  static Row of(Map<Column, String> values) {
    return Row.of(column -> values.getOrDefault(column, ""));
  }

  static Row of(Function<Column, String> values) {
    return values::apply;
  }

  String format(Column column);

  default Optional<RowWithAmounts> asRowWithAmounts() {
    return Optional.empty();
  }

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

  default String formatDescription() {
    return "";
  }
}
