package se.montesmites.ekonomi.report;

import java.time.format.TextStyle;
import java.util.Locale;

public interface HeaderRow extends Row {

    default String getDescription() {
        return "";
    }

    default String getTotal() {
        return "";
    }

    default String getMonth(Column column) {
        return column.getMonth().get()
                .getDisplayName(TextStyle.SHORT, Locale.getDefault());
    }

    @Override
    default String getText(Column column) {
        switch (column.getColumnType()) {
            case DESCRIPTION:
                return getDescription();
            case TOTAL:
                return getTotal();
            default:
                return getMonth(column);
        }
    }
}
