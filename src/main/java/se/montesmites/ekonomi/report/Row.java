package se.montesmites.ekonomi.report;

import java.util.Optional;
import java.util.function.Predicate;

public interface Row {

    default String formatText(Column column) {
        switch (column.getColumnType()) {
            case DESCRIPTION:
                return formatDescription();
            case TOTAL:
                return formatTotal();
            case AVERAGE:
                return formatAverage();
            default:
                return formatMonth(column);
        }
    }

    default String formatDescription() {
        return "";
    }

    default String formatTotal() {
        return "";
    }

    default String formatMonth(Column column) {
        return "";
    }

    default String formatAverage() {
        return "";
    }

    default Optional<RowWithAmounts> asRowWithAmounts() {
        return Optional.empty();
    }

    default Optional<RowWithAccounts> asRowWithAccounts() {
        return Optional.empty();
    }

    default boolean isEquivalentTo(Row that) {
        return Column.stream().allMatch(columnIsEquivalentPredicate(that));
    }

    private Predicate<Column> columnIsEquivalentPredicate(Row that) {
        return column -> this.formatText(column).equals(that.formatText(column));
    }
}
