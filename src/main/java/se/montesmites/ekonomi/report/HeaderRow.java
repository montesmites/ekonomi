package se.montesmites.ekonomi.report;

import java.time.format.TextStyle;
import java.util.Locale;

public interface HeaderRow extends Row {

    @Override
    default String formatMonth(Column column) {
        return column.getMonth().get()
                .getDisplayName(TextStyle.SHORT, Locale.getDefault());
    }

    @Override
    default String formatTotal() {
        return "totalt";
    }

    @Override
    default String formatAverage() {
        return "medel";
    }
}
