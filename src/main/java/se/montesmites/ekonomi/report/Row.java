package se.montesmites.ekonomi.report;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@FunctionalInterface
public interface Row {

  static Row empty() {
    return column -> "";
  }

  static Row of(Function<Column, String> formattedValues) {
    return formattedValues::apply;
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
}
